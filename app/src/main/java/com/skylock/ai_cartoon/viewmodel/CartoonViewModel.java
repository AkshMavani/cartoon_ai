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
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * CartoonViewModel — Production-ready
 * <p>
 * Error events emitted via errorEvent LiveData:
 * • NO_INTERNET        → internet lost during generate or poll
 * • SERVER_TIMEOUT     → socket/read timeout
 * • SERVER_ERROR       → server returned unexpected code
 * • MAX_POLL_EXCEEDED  → polled 80+ times without result
 * • NO_RESPONSE        → null body from server
 * <p>
 * On retry (user taps "Retry" in dialog) call retryFromScratch().
 * Activity observes errorEvent and shows the appropriate dialog.
 */
public class CartoonViewModel extends ViewModel {

    private static final String TAG = "CartoonViewModel";
    private static final String MIME_TEXT = "text/plain";
    private static final String MIME_IMAGE = "image/*";

    // Max silent auto-retries before showing dialog to user (generate phase only)
    private static final int MAX_PROCESS_RETRY = 2;
    // Max poll attempts (~80 × 2 s = ~160 s) before showing "taking too long" dialog
    private static final int MAX_POLL_COUNT = 85;
    private static final long POLL_INTERVAL_MS = 2000L;

    private static final String AUTH_TOKEN = "Bearer NU4IYAS4D0F8CVBSI26R5NU21E0HW737GPJ07WAM";
    private static final String CARTOON_BASE_URL = "https://api.aicartoon.zeezoo.mobi:8081/cartoon/";

    // ── LiveData ──────────────────────────────────────────────────────────────
    private final MutableLiveData<ImageResponse> mImageResponse = new MutableLiveData<>();
    private final MutableLiveData<Integer> errorCode = new MutableLiveData<>();
    private final MutableLiveData<ErrorEvent> errorEvent = new MutableLiveData<>();

    // ── Internal state ────────────────────────────────────────────────────────
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;
    private String uriAiFace;
    private boolean activityFinished = false;
    private int processRetryCount = 0;

    // Saved for retry
    private String lastImageUri;
    private String lastStyle;
    private double lastStrength;
    private String lastGender;

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    public void onCartoon(String imageUri, String aiFaceUri, double strength,
                          String gender, String style) {
        this.uriAiFace = aiFaceUri;
        this.lastImageUri = imageUri;
        this.lastStyle = style;
        this.lastStrength = strength;
        this.lastGender = gender;
        this.processRetryCount = 0;

        resetLiveData();
        stopPolling();

        AiphotoService.CARTOON_URL = CARTOON_BASE_URL;
        processZeeZoo(AUTH_TOKEN, imageUri, style, strength, resolveGender(gender));
    }

    /**
     * Call this when the user taps "Retry" in the error dialog.
     * Restarts the entire flow from the generate step.
     */
    public void retryFromScratch() {
        processRetryCount = 0;
        resetLiveData();
        stopPolling();

        AiphotoService.CARTOON_URL = CARTOON_BASE_URL;
        processZeeZoo(AUTH_TOKEN, lastImageUri, lastStyle, lastStrength, resolveGender(lastGender));
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
        stopPolling();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Generate phase
    // ─────────────────────────────────────────────────────────────────────────

    private void processZeeZoo(String authToken, String imageUri, String styleName,
                               double strength, String gender) {
        if (activityFinished) return;
        Log.d(TAG, "processZeeZoo → style:" + styleName + " gender:" + gender
                + " retry:" + processRetryCount);

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
                        RequestBody.create(MediaType.parse(MIME_IMAGE), file));
            } else {
                Log.e(TAG, "Image file not found: " + cleanPath);
                postErrorEvent(ErrorEvent.SERVER_ERROR);
                return;
            }
        }

        MultipartBody.Part maskPart = null;
        if (uriAiFace != null && !uriAiFace.isEmpty()) {
            File maskFile = new File(uriAiFace.replace("file://", ""));
            if (maskFile.exists()) {
                maskPart = MultipartBody.Part.createFormData(
                        "mask", maskFile.getName(),
                        RequestBody.create(MediaType.parse(MIME_IMAGE), maskFile));
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
                textBody(gender)
        ).enqueue(new Callback<AiphotoResponse>() {

            @Override
            public void onResponse(Call<AiphotoResponse> call,
                                   Response<AiphotoResponse> response) {
                if (activityFinished) return;

                AiphotoResponse body = response.body();
                Log.d(TAG, "generate onResponse code=" + response.code());

                if (body == null || body.getCode() == null) {
                    // Server returned empty body — treat as a transient server error
                    Log.e(TAG, "generate: null body or null code");
                    handleGenerateFailure(false /*not a connection error*/);
                    return;
                }

                int code = body.getCode();

                if (code == 0 && body.getImages() != null && !body.getImages().isEmpty()) {
                    // Image is ready immediately
                    Log.d(TAG, "generate: image ready immediately");
                    mImageResponse.setValue(body.getImages().get(0));

                } else if (code == 2 && body.getImages() != null && !body.getImages().isEmpty()) {
                    // Server is still processing — start polling
                    Log.d(TAG, "generate: server processing, start polling");
                    GetImageRequest req = new GetImageRequest();
                    req.setImages(body.getImages());
                    startPolling(req);

                } else {
                    // Unexpected server code (e.g. 98 = server busy, anything else)
                    Log.e(TAG, "generate: unexpected server code=" + code);
                    handleGenerateFailure(false);
                }
            }

            @Override
            public void onFailure(Call<AiphotoResponse> call, Throwable t) {
                if (activityFinished) return;
                Log.e(TAG, "generate onFailure: " + t.getMessage());
                handleGenerateFailure(isNoInternetError(t));
            }
        });
    }

    /**
     * Silent auto-retry up to MAX_PROCESS_RETRY times for transient errors.
     * After exhausting retries the appropriate dialog is shown.
     *
     * @param isNoInternet true when the failure is clearly a connectivity loss
     */
    private void handleGenerateFailure(boolean isNoInternet) {
        if (activityFinished) return;

        if (isNoInternet) {
            // No point retrying with no internet — show dialog immediately
            postErrorEvent(ErrorEvent.NO_INTERNET);
            return;
        }

        if (processRetryCount < MAX_PROCESS_RETRY) {
            processRetryCount++;
            long delayMs = processRetryCount * 2000L; // back-off: 2 s, 4 s
            Log.w(TAG, "generate auto-retry " + processRetryCount + "/" + MAX_PROCESS_RETRY
                    + " in " + delayMs + " ms");
            timerHandler.postDelayed(() -> {
                if (!activityFinished) {
                    processZeeZoo(AUTH_TOKEN, lastImageUri, lastStyle,
                            lastStrength, resolveGender(lastGender));
                }
            }, delayMs);
        } else {
            // All silent retries exhausted — show dialog
            Log.e(TAG, "generate: max retries reached");
            postErrorEvent(ErrorEvent.SERVER_ERROR);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Poll phase
    // ─────────────────────────────────────────────────────────────────────────

    private void startPolling(final GetImageRequest request) {
        if (activityFinished) return;
        stopPolling(); // safety: remove any previous runnable

        final int[] pollCount = {0};

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (activityFinished) return;

                pollCount[0]++;
                Log.d(TAG, "poll #" + pollCount[0]);

                // ── Guard: too many polls → show dialog ───────────────────────
                if (pollCount[0] > MAX_POLL_COUNT) {
                    Log.w(TAG, "poll limit reached (" + MAX_POLL_COUNT + ")");
                    postErrorEvent(ErrorEvent.MAX_POLL_EXCEEDED);
                    return;
                }

                AiphotoService.getService("cartoon")
                        .getPhoto(request)
                        .enqueue(new Callback<AiphotoResponse>() {

                            @Override
                            public void onResponse(Call<AiphotoResponse> call,
                                                   Response<AiphotoResponse> response) {
                                if (activityFinished) return;

                                AiphotoResponse resp = response.body();

                                // ── Null body ─────────────────────────────────
                                if (resp == null) {
                                    Log.e(TAG, "poll: null response body");
                                    stopPolling();
                                    postErrorEvent(ErrorEvent.NO_RESPONSE);
                                    return;
                                }

                                // ── Null code ─────────────────────────────────
                                if (resp.getCode() == null) {
                                    Log.e(TAG, "poll: null code in response");
                                    stopPolling();
                                    postErrorEvent(ErrorEvent.SERVER_ERROR);
                                    return;
                                }

                                int code = resp.getCode();

                                if (code == 0
                                        && resp.getImages() != null
                                        && !resp.getImages().isEmpty()) {
                                    // ✅ Success
                                    Log.d(TAG, "poll: SUCCESS on attempt #" + pollCount[0]);
                                    stopPolling();
                                    mImageResponse.setValue(resp.getImages().get(0));

                                } else if (code == 2) {
                                    // Still processing — schedule next poll
                                    timerHandler.postDelayed(timerRunnable, POLL_INTERVAL_MS);

                                } else {
                                    // Any other code is an error
                                    Log.e(TAG, "poll: server error code=" + code);
                                    stopPolling();
                                    postErrorEvent(ErrorEvent.SERVER_ERROR);
                                }
                            }

                            @Override
                            public void onFailure(Call<AiphotoResponse> call, Throwable t) {
                                if (activityFinished) return;
                                Log.e(TAG, "poll onFailure: " + t.getMessage());
                                stopPolling();

                                if (isNoInternetError(t)) {
                                    postErrorEvent(ErrorEvent.NO_INTERNET);
                                } else if (t instanceof SocketTimeoutException) {
                                    postErrorEvent(ErrorEvent.SERVER_TIMEOUT);
                                } else {
                                    postErrorEvent(ErrorEvent.SERVER_ERROR);
                                }
                            }
                        });
            }
        };

        timerHandler.post(timerRunnable);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void stopPolling() {
        if (timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
            timerRunnable = null;
        }
    }

    private void resetLiveData() {
        mImageResponse.setValue(null);
        errorCode.setValue(null);
        errorEvent.setValue(null);
    }

    /**
     * Post an error event only if the activity is still alive.
     * Uses postValue so it is safe to call from any thread.
     */
    private void postErrorEvent(ErrorEvent event) {
        if (!activityFinished) {
            errorEvent.postValue(event);
        }
    }

    private String resolveGender(String gender) {
        String fromPrefs = SharePrefUtils.getGenderString("selected_gender");
        if (fromPrefs != null && !fromPrefs.isEmpty()) return fromPrefs;
        return gender != null ? gender : "female";
    }

    private boolean isNoInternetError(Throwable t) {
        if (t instanceof UnknownHostException) return true;
        if (t instanceof IOException) {
            String msg = t.getMessage() != null ? t.getMessage().toLowerCase() : "";
            return msg.contains("unable to resolve")
                    || msg.contains("network is unreachable")
                    || msg.contains("no address associated")
                    || msg.contains("failed to connect")
                    || msg.contains("enetunreach");
        }
        return false;
    }

    private RequestBody textBody(String value) {
        return RequestBody.create(MediaType.parse(MIME_TEXT), value != null ? value : "");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        activityFinished = true;
        stopPolling();
        timerHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "ViewModel cleared");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Error event enum
    // ─────────────────────────────────────────────────────────────────────────

    public enum ErrorEvent {
        /**
         * Device has no internet connection
         */
        NO_INTERNET,
        /**
         * Request timed out (SocketTimeoutException)
         */
        SERVER_TIMEOUT,
        /**
         * Server returned an unexpected/error response code
         */
        SERVER_ERROR,
        /**
         * Polled 80+ times without receiving a finished image
         */
        MAX_POLL_EXCEEDED,
        /**
         * Server returned a completely empty / null body
         */
        NO_RESPONSE
    }
}