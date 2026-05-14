package com.skylock.ai_cartoon.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.skylock.ai_cartoon.model.AiphotoResponse;
import com.skylock.ai_cartoon.model.GetImageRequest;
import com.skylock.ai_cartoon.model.ImageResponse;
import com.skylock.ai_cartoon.util.AiphotoService;
import com.skylock.ai_cartoon.util.SharePrefUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartoonViewModel extends ViewModel {

    private static final String TAG = "CartoonViewModel";
    private static final String MIME_TEXT = "text/plain";
    private static final String MIME_IMAGE = "image/*";
    private static final int MAX_POLL_COUNT = 80;
    private static final long POLL_INTERVAL_MS = 2000L;
    private static final String AUTH_TOKEN = "Bearer NU4IYAS4D0F8CVBSI26R5NU21E0HW737GPJ07WAM";
    private static final String CARTOON_BASE_URL = "https://api.aicartoon.zeezoo.mobi:8081/cartoon/";
    private static final int MAX_PROCESS_RETRY = 2;
    private static final int POLL_WARNING_THRESHOLD = 80;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final MutableLiveData<ImageResponse> mImageResponse = new MutableLiveData<>();
    private final MutableLiveData<Integer> errorCode = new MutableLiveData<>();
    private final MutableLiveData<ErrorEvent> errorEvent = new MutableLiveData<>();
    private Runnable timerRunnable;
    private String uriAiFace;
    private boolean activityFinished = false;
    private int processRetryCount = 0;
    private String lastImageUri;
    private String lastStyle;
    private double lastStrength;
    private String lastGender;

    public void onCartoon(String imageUri, String aiFaceUri, double strength,
                          String gender, String style) {
        this.uriAiFace = aiFaceUri;
        this.lastImageUri = imageUri;
        this.lastStyle = style;
        this.lastStrength = strength;
        this.lastGender = gender;
        this.processRetryCount = 0;

        mImageResponse.setValue(null);
        errorCode.setValue(null);
        errorEvent.setValue(null);

        AiphotoService.CARTOON_URL = CARTOON_BASE_URL;
        String genderFromPrefs = SharePrefUtils.getGenderString("selected_gender");
        String resolvedGender = (genderFromPrefs != null && !genderFromPrefs.isEmpty())
                ? genderFromPrefs : (gender != null ? gender : "female");

        Log.d(TAG, "onCartoon -> style: " + style + " | gender: " + resolvedGender);
        processZeeZoo(AUTH_TOKEN, imageUri, style, strength, resolvedGender);
    }

    // ─── Public API ────────────────────────────────────────────────────────────

    public void retryFromScratch() {
        processRetryCount = 0;
        mImageResponse.setValue(null);
        errorCode.setValue(null);
        errorEvent.setValue(null);
        timerHandler.removeCallbacksAndMessages(null);

        String genderFromPrefs = SharePrefUtils.getGenderString("selected_gender");
        String resolvedGender = (genderFromPrefs != null && !genderFromPrefs.isEmpty())
                ? genderFromPrefs : (lastGender != null ? lastGender : "female");

        AiphotoService.CARTOON_URL = CARTOON_BASE_URL;
        processZeeZoo(AUTH_TOKEN, lastImageUri, lastStyle, lastStrength, resolvedGender);
    }

    public LiveData<ImageResponse> getImageResponse() {
        return mImageResponse;
    }

    public LiveData<Integer> getErrorCode() {
        return errorCode;
    }

    public LiveData<ErrorEvent> getErrorEvent() {
        return errorEvent;
    }

    public void setActivityFinished() {
        this.activityFinished = true;
        timerHandler.removeCallbacksAndMessages(null);
    }

    private void processZeeZoo(String authToken, String imageUri, String styleName,
                               double strength, String gender) {
        Log.d(TAG, "processZeeZoo -> style: " + styleName + " | gender: " + gender);

        MultipartBody.Part filePart = null;
        RequestBody urlBody = null;

        if (imageUri != null && imageUri.startsWith("http")) {
            urlBody = textBody(imageUri);
        } else {
            String cleanPath = imageUri != null ? imageUri.replace("file://", "") : "";
            File file = new File(cleanPath);
            if (file.exists()) {
                filePart = MultipartBody.Part.createFormData(
                        "file", file.getName(),
                        RequestBody.create(MediaType.parse(MIME_IMAGE), file)
                );
            } else {
                Log.e(TAG, "Image file not found: " + cleanPath);
                errorEvent.setValue(ErrorEvent.SERVER_ERROR);
                return;
            }
        }

        MultipartBody.Part maskPart = null;
        if (uriAiFace != null && !uriAiFace.isEmpty()) {
            File maskFile = new File(uriAiFace.replace("file://", ""));
            if (maskFile.exists()) {
                maskPart = MultipartBody.Part.createFormData(
                        "mask", maskFile.getName(),
                        RequestBody.create(MediaType.parse(MIME_IMAGE), maskFile)
                );
            }
        }

        final MultipartBody.Part finalFilePart = filePart;
        final RequestBody finalUrlBody = urlBody;
        final MultipartBody.Part finalMaskPart = maskPart;

        AiphotoService.getService("cartoon").generate(
                authToken,
                finalFilePart,
                finalUrlBody,
                finalMaskPart,
                textBody(styleName),
                textBody(" "),
                textBody(" "),
                textBody(String.valueOf(strength)),
                textBody(gender != null ? gender : "female")
        ).enqueue(new Callback<AiphotoResponse>() {
            @Override
            public void onResponse(Call<AiphotoResponse> call, Response<AiphotoResponse> response) {
                if (activityFinished) return;

                AiphotoResponse body = response.body();
                Log.d(TAG, "Generate response code: " + response.code());

                if (body == null) {
                    Log.e(TAG, "Null body received");
                    handleProcessFailure(false);
                    return;
                }

                if (body.getCode() == null) {
                    Log.e(TAG, "Null code in body");
                    handleProcessFailure(false);
                    return;
                }

                int code = body.getCode();
                if (code == 0 && body.getImages() != null && !body.getImages().isEmpty()) {
                    Log.d(TAG, "Success: image ready immediately");
                    mImageResponse.setValue(body.getImages().get(0));
                } else if (code == 2 && body.getImages() != null && !body.getImages().isEmpty()) {
                    Log.d(TAG, "Status 2: polling for result");
                    GetImageRequest request = new GetImageRequest();
                    request.setImages(body.getImages());
                    getPhoto(request);
                } else {
                    Log.e(TAG, "Error code from server: " + code);
                    handleProcessFailure(false);
                }
            }

            @Override
            public void onFailure(Call<AiphotoResponse> call, Throwable t) {
                if (activityFinished) return;
                Log.e(TAG, "Generate failure: " + t.getMessage());

                // Detect no internet vs other network error
                if (t instanceof IOException) {
                    String msg = t.getMessage() != null ? t.getMessage().toLowerCase() : "";
                    if (msg.contains("unable to resolve") || msg.contains("network is unreachable")
                            || msg.contains("no address associated") || msg.contains("failed to connect")) {
                        errorEvent.setValue(ErrorEvent.NO_INTERNET);
                    } else {
                        handleProcessFailure(true);
                    }
                } else {
                    handleProcessFailure(true);
                }
            }
        });
    }

    // ─── ZeeZoo Processing ─────────────────────────────────────────────────────

    private void getPhoto(final GetImageRequest request) {
        final int[] pollCount = {0};

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (activityFinished) return;
                pollCount[0]++;
                Log.d(TAG, "getPhoto poll #" + pollCount[0]);

                // Show retry dialog when poll count exceeds threshold
                if (pollCount[0] >= POLL_WARNING_THRESHOLD) {
                    Log.w(TAG, "Poll count exceeded threshold: " + pollCount[0]);
                    timerHandler.removeCallbacks(timerRunnable);
                    errorEvent.setValue(ErrorEvent.MAX_POLL_EXCEEDED);
                    return;
                }

                AiphotoService.getService("cartoon").getPhoto(request).enqueue(
                        new Callback<AiphotoResponse>() {
                            @Override
                            public void onResponse(Call<AiphotoResponse> call,
                                                   Response<AiphotoResponse> response) {
                                if (activityFinished) return;

                                AiphotoResponse resp = response.body();

                                if (resp == null) {
                                    Log.e(TAG, "getPhoto null response body");
                                    timerHandler.removeCallbacks(timerRunnable);
                                    errorEvent.setValue(ErrorEvent.NO_RESPONSE);
                                    return;
                                }

                                if (resp.getCode() == null) {
                                    Log.e(TAG, "getPhoto null code");
                                    timerHandler.removeCallbacks(timerRunnable);
                                    errorEvent.setValue(ErrorEvent.SERVER_ERROR);
                                    return;
                                }

                                int code = resp.getCode();
                                if (code == 0 && resp.getImages() != null
                                        && !resp.getImages().isEmpty()) {
                                    Log.d(TAG, "getPhoto: SUCCESS");
                                    timerHandler.removeCallbacks(timerRunnable);
                                    mImageResponse.setValue(resp.getImages().get(0));

                                } else if (code == 2) {
                                    // Still processing — continue polling
                                    timerHandler.postDelayed(timerRunnable, POLL_INTERVAL_MS);

                                } else {
                                    Log.e(TAG, "getPhoto error code: " + code);
                                    timerHandler.removeCallbacks(timerRunnable);
                                    errorEvent.setValue(ErrorEvent.SERVER_ERROR);
                                }
                            }

                            @Override
                            public void onFailure(Call<AiphotoResponse> call, Throwable t) {
                                if (activityFinished) return;
                                Log.e(TAG, "getPhoto failure: " + t.getMessage());
                                timerHandler.removeCallbacks(timerRunnable);

                                if (t instanceof IOException) {
                                    String msg = t.getMessage() != null
                                            ? t.getMessage().toLowerCase() : "";
                                    if (msg.contains("unable to resolve")
                                            || msg.contains("network is unreachable")
                                            || msg.contains("failed to connect")) {
                                        errorEvent.setValue(ErrorEvent.NO_INTERNET);
                                    } else {
                                        errorEvent.setValue(ErrorEvent.SERVER_TIMEOUT);
                                    }
                                } else {
                                    errorEvent.setValue(ErrorEvent.SERVER_ERROR);
                                }
                            }
                        });
            }
        };

        timerHandler.postDelayed(timerRunnable, 0);
    }

    // ─── Polling ───────────────────────────────────────────────────────────────

    private void handleProcessFailure(boolean isConnectionError) {
        if (processRetryCount < MAX_PROCESS_RETRY && !activityFinished) {
            processRetryCount++;
            long delay = processRetryCount * 2000L;
            Log.w(TAG, "Auto-retry " + processRetryCount + "/" + MAX_PROCESS_RETRY
                    + " in " + delay + "ms");
            timerHandler.postDelayed(() -> {
                if (!activityFinished) {
                    String genderFromPrefs = SharePrefUtils.getGenderString("selected_gender");
                    String resolvedGender = (genderFromPrefs != null && !genderFromPrefs.isEmpty())
                            ? genderFromPrefs : (lastGender != null ? lastGender : "female");
                    processZeeZoo(AUTH_TOKEN, lastImageUri, lastStyle, lastStrength, resolvedGender);
                }
            }, delay);
        } else {
            // Auto-retry exhausted — show dialog to user
            errorEvent.setValue(isConnectionError
                    ? ErrorEvent.NO_INTERNET : ErrorEvent.SERVER_ERROR);
        }
    }

    // ─── Retry logic ───────────────────────────────────────────────────────────

    private RequestBody textBody(String value) {
        return RequestBody.create(MediaType.parse(MIME_TEXT), value != null ? value : "");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        activityFinished = true;
        timerHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "ViewModel cleared");
    }

    public enum ErrorEvent {
        NO_INTERNET,
        SERVER_TIMEOUT,
        SERVER_ERROR,
        MAX_POLL_EXCEEDED,
        NO_RESPONSE
    }
}
/*
package com.skylock.ai_cartoon.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.skylock.ai_cartoon.api.RunpodService;
import com.skylock.ai_cartoon.model.AiphotoResponse;
import com.skylock.ai_cartoon.model.GetImageRequest;
import com.skylock.ai_cartoon.model.ImageResponse;
import com.skylock.ai_cartoon.util.AiphotoService;
import com.skylock.ai_cartoon.util.SharePrefUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartoonViewModel extends ViewModel {

    private static final String TAG = "CartoonViewModel";
    private static final String MIME_TEXT = "text/plain";
    private static final String MIME_IMAGE = "image/*";
    private static final int MAX_POLL_COUNT = 60;
    private static final long POLL_INTERVAL_MS = 2000L;
    private static final String AUTH_TOKEN = "Bearer NU4IYAS4D0F8CVBSI26R5NU21E0HW737GPJ07WAM";
    private static final String CARTOON_BASE_URL = "https://api.aicartoon.zeezoo.mobi:8081/cartoon/";

    // ✅ Fix: retry process on connection failure
    private static final int MAX_PROCESS_RETRY = 2;
    private int processRetryCount = 0;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final MutableLiveData<ImageResponse> mImageResponse = new MutableLiveData<>();
    private final MutableLiveData<Integer> errorCode = new MutableLiveData<>();

    private Runnable timerRunnable;
    private String uriAiFace;
    private boolean activityFinished = false;

    private String lastImageUri;
    private String lastStyle;
    private double lastStrength;
    private String lastGender;

    public void onCartoon(String imageUri, String aiFaceUri, double strength,
                          String gender, String style) {
        this.uriAiFace = aiFaceUri;
        this.lastImageUri = imageUri;
        this.lastStyle = style;
        this.lastStrength = strength;
        this.lastGender = gender;
        this.processRetryCount = 0;

        mImageResponse.setValue(null);
        errorCode.setValue(null);

        AiphotoService.CARTOON_URL = CARTOON_BASE_URL;
        String genderFromPrefs = SharePrefUtils.getGenderString("selected_gender");
        String resolvedGender = (genderFromPrefs != null && !genderFromPrefs.isEmpty())
                ? genderFromPrefs : (gender != null ? gender : "female");

        Log.d(TAG, "onCartoon -> style: " + style + " | gender: " + resolvedGender);
        processZeeZoo(AUTH_TOKEN, imageUri, style, strength, resolvedGender);
    }

    public LiveData<ImageResponse> getImageResponse() { return mImageResponse; }
    public LiveData<Integer> getErrorCode() { return errorCode; }
    public void setActivityFinished() { this.activityFinished = true; }

    private void processZeeZoo(String authToken, String imageUri, String styleName,
                               double strength, String gender) {
        Log.d(TAG, "processZeeZoo -> style: " + styleName + " | gender: " + gender);

        MultipartBody.Part filePart = null;
        RequestBody urlBody = null;

        if (imageUri != null && imageUri.startsWith("http")) {
            urlBody = textBody(imageUri);
        } else {
            String cleanPath = imageUri != null ? imageUri.replace("file://", "") : "";
            File file = new File(cleanPath);
            if (file.exists()) {
                filePart = MultipartBody.Part.createFormData(
                        "file", file.getName(),
                        RequestBody.create(MediaType.parse(MIME_IMAGE), file)
                );
            } else {
                Log.e(TAG, "Image file not found: " + cleanPath);
                errorCode.setValue(99);
                return;
            }
        }

        MultipartBody.Part maskPart = null;
        if (uriAiFace != null && !uriAiFace.isEmpty()) {
            File maskFile = new File(uriAiFace.replace("file://", ""));
            if (maskFile.exists()) {
                maskPart = MultipartBody.Part.createFormData(
                        "mask", maskFile.getName(),
                        RequestBody.create(MediaType.parse(MIME_IMAGE), maskFile)
                );
            }
        }

        final MultipartBody.Part finalFilePart = filePart;
        final RequestBody finalUrlBody = urlBody;
        final MultipartBody.Part finalMaskPart = maskPart;
        final String finalGender = gender;
        final String finalStyle = styleName;

        AiphotoService.getService("cartoon").generate(
                authToken,
                finalFilePart,
                finalUrlBody,
                finalMaskPart,
                textBody(finalStyle),
                textBody(" "),
                textBody(" "),
                textBody(String.valueOf(strength)),
                textBody(finalGender)
        ).enqueue(new Callback<AiphotoResponse>() {
            @Override
            public void onResponse(Call<AiphotoResponse> call, Response<AiphotoResponse> response) {
                AiphotoResponse body = response.body();
                Log.d(TAG, "Generate response code: " + response.code());

                if (body == null || body.getCode() == null) {
                    Log.e(TAG, "Null body — retrying if possible");
                    retryProcessIfNeeded();
                    return;
                }

                int code = body.getCode();
                if (code == 0 && body.getImages() != null && !body.getImages().isEmpty()) {
                    Log.d(TAG, "Success: image ready immediately");
                    mImageResponse.setValue(body.getImages().get(0));
                } else if (code == 2 && body.getImages() != null && !body.getImages().isEmpty()) {
                    Log.d(TAG, "Status 2: polling for result");
                    GetImageRequest request = new GetImageRequest();
                    request.setImages(body.getImages());
                    getPhoto(request);
                } else {
                    Log.e(TAG, "Error code from server: " + code);
                    retryProcessIfNeeded();
                }
            }

            @Override
            public void onFailure(Call<AiphotoResponse> call, Throwable t) {
                Log.e(TAG, "Generate failure: " + t.getMessage());
                // ✅ Fix: retry on "Connection closed by peer"
                retryProcessIfNeeded();
            }
        });
    }

    // ✅ Fix: retry on connection failure up to MAX_PROCESS_RETRY times
    private void retryProcessIfNeeded() {
        if (processRetryCount < MAX_PROCESS_RETRY && !activityFinished) {
            processRetryCount++;
            long delay = processRetryCount * 2000L;
            Log.w(TAG, "Retrying process (" + processRetryCount + "/" + MAX_PROCESS_RETRY
                    + ") in " + delay + "ms");
            timerHandler.postDelayed(() -> {
                String genderFromPrefs = SharePrefUtils.getGenderString("selected_gender");
                String resolvedGender = (genderFromPrefs != null && !genderFromPrefs.isEmpty())
                        ? genderFromPrefs : (lastGender != null ? lastGender : "female");
                processZeeZoo(AUTH_TOKEN, lastImageUri, lastStyle, lastStrength, resolvedGender);
            }, delay);
        } else {
            Log.e(TAG, "Max retries reached — sending error");
            errorCode.setValue(99);
        }
    }

    private void getPhoto(final GetImageRequest request) {
        final int[] pollCount = {0};
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (activityFinished) return;
                pollCount[0]++;
                Log.d(TAG, "getPhoto poll #" + pollCount[0]);

                AiphotoService.getService("cartoon").getPhoto(request).enqueue(
                        new Callback<AiphotoResponse>() {
                            @Override
                            public void onResponse(Call<AiphotoResponse> call,
                                                   Response<AiphotoResponse> response) {
                                AiphotoResponse resp = response.body();
                                if (resp != null && resp.getCode() != null) {
                                    if (resp.getCode() == 0
                                            && resp.getImages() != null
                                            && !resp.getImages().isEmpty()) {
                                        Log.d(TAG, "getPhoto: SUCCESS");
                                        mImageResponse.setValue(resp.getImages().get(0));
                                        timerHandler.removeCallbacks(timerRunnable);
                                    } else if (resp.getCode() == 2
                                            && pollCount[0] < MAX_POLL_COUNT) {
                                        timerHandler.postDelayed(timerRunnable, POLL_INTERVAL_MS);
                                    } else {
                                        Log.e(TAG, "getPhoto timeout or error");
                                        errorCode.setValue(99);
                                    }
                                } else {
                                    Log.e(TAG, "getPhoto null response");
                                    errorCode.setValue(99);
                                }
                            }

                            @Override
                            public void onFailure(Call<AiphotoResponse> call, Throwable t) {
                                Log.e(TAG, "getPhoto failure: " + t.getMessage());
                                errorCode.setValue(99);
                            }
                        });
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private RequestBody textBody(String value) {
        return RequestBody.create(MediaType.parse(MIME_TEXT), value != null ? value : "");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        activityFinished = true;
        timerHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "ViewModel cleared");
    }
}

//man_Birthday_Golden*/
