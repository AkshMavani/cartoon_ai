package com.skylock.ai_cartoon.util;

/* loaded from: classes3.dex */

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.skylock.ai_cartoon.activity.ActivityProcess;
import com.skylock.ai_cartoon.model.DemoLibraryModel;
import com.skylock.ai_cartoon.model.ImageModel;
import com.skylock.ai_cartoon.remove_obj.RemoveObjActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/* loaded from: classes3.dex */
public class Constants {
    public static final String AI_HUGGING = "ai_hugging";
    public static final String DISCOUNT_ACTIVE = "DISCOUNT_ACTIVE";
    public static final String EVENT_NOEL = "event_noel_new_app";
    public static final String HAIR_STYLE = "hairstyle";
    public static final boolean IS_NOEL = false;
    public static final String LIBRARY_RECOMMEND = "LIBRARY_RECOMMEND";
    public static final String TIME_DISCOUNT = "time_bg_discount";
    public static final long TIME_UNIT = 1800000;
    public static final String TOOLTIP_CAMERA = "tooltip_camera";
    public static final String TOOLTIP_EVENT_SHOW_AI_AVATAR = "tooltip_show_ai_avatar";
    public static final String TOOLTIP_HOME = "tooltip_home";
    public static final String TOOLTIP_HOME_BANNER = "tooltip_home_banner";
    public static final String TOOLTIP_RESULT = "TOOLTIP_RESULT";
    public static final String TOOLTIP_RESULT_AI = "TOOLTIP_RESULT_AI";
    public static final String TOOLTIP_RESULT_BEAUTY = "TOOLTIP_RESULT_BEAUTY";
    public static final String[] arrayCamera;
    public static final String[] arrayGallery;
    public static final String[] arrayGalleryAndroid14;
    private static final String CHILD_DIR = "images";
    private static final int COMPRESS_QUALITY = 100;
    private static final String FILE_EXTENSION = ".png";
    private static final String KEY_STORED_DATE = "storedDate";
    private static final String TEMP_FILE_NAME = "img";
    public static String ACCOUNT_ID = "";
    public static String ADMOB = "admob";
    public static boolean ADS_ADMOB = true;
    public static String ADS_INTER_TYPE = "admob";
    public static String ADS_REWARD_TYPE = "admob";
    public static String APPFLYER_KEY = "Qnh77rpQng8Bd6fmtHHaQd";
    public static String APPLOVIN = "applovin";
    public static Boolean IS_NETWORK_CONNECTED = false;
    public static Boolean IS_GO_HOME = false;
    public static Boolean IS_SHOW_NETWORK_DISCONNECTED = false;
    public static Boolean IS_SHOWED_AD = false;
    public static boolean IS_TABLET = false;
    public static int RESULT_CODE_CLOSE_PREMIUM_AND_SHOW_SAVE_MODAL = 300;
    public static String AB_PREMIUM_SHOW_MONTHLY_OR_WEEKLY = "weekly399";
    public static boolean AB_PREMIUM_FREE_TRIAL_WEEK = false;
    public static boolean AB_REQUEST_PERMISSION_SHOW_POPUP = false;
    public static String AB_HOME_BANNER_POSITION = "top";
    public static boolean AB_PREMIUM_NEW_BANNER = true;
    public static boolean AB_FREE_TRIAL_NEW = true;
    public static boolean AB_ACTION_BUTTON_NEW = true;
    public static boolean AB_TEXT_CALL_TO_ACTION = true;
    public static boolean AB_LIBRARY_NEW_ENABLE = false;
    public static long AB_CARTOON_LIMIT_USE = 0;
    public static boolean AB_FREE_TRIAL_SHOW = false;
    public static boolean AB_PREMIUM_SCROLL_ENABLE = true;
    public static boolean AB_PREMIUM_NEW_UI = false;
    public static boolean AB_ONBOARD_NEW = true;
    public static String AB_PREVIEW_ENHANCE_AD_TYPE = "reward";
    public static boolean AB_HOME_TOP_BANNER_ENABLE = true;
    public static boolean ABTEST_ONBOARDING_032025 = true;
    public static String SUBS_WEEKLY = "mobi.zeezoo.photoenhancer.weekly";
    public static String SUBS_WEEKLY_DC = "mobi.zeezoo.photoenhancer.weekly2";
    public static String SUBS_WEEKLY_FT = "mobi.zeezoo.photoenhancer.weeklytrial3d";
    public static String SUBS_YEARLY = "mobi.zeezoo.photoenhancer.yearly";
    public static String SUBS_YEARLY_FT = "mobi.zeezoo.photoenhancer.yearlytrial";
    public static String IAP_LIFETIME = "mobi.zeezoo.photoenhaner.lifetime";
    public static String SUBS_WEEKLY_PRO = "mobi.zeezoo.photoenhancer.weeklypro";
    public static String SUBS_WEEKLY_PRO_DC20 = "photoenhancer.sub.weeklyprosale20";
    public static String SUBS_YEARLY_PRO = "mobi.zeezoo.photoenhancer.yearlypro";
    public static String SUBS_YEARLY_SALE = "mobi.zeezoo.photoenhancer.yearlydiscount";
    public static String IAP_LIFETIME_PRO = "photoenhancer.iap.lifetimepro";
    public static String IAP_GET_1_PACK = "photoenhancer.iap.get1pack";
    public static String IAP_GET_2_PACK = "photoenhancer.iap.get2pack";
    public static String IAP_GET_3_PACK = "photoenhancer.iap.get3pack";
    public static String IAP_TRAIN_1_MODEL = "photoenhancer.iap.train1model";
    public static int MAX_MULTI_ENHANCE = 5;
    public static Integer CARTOON_USED_ALL_STYLE_ON_DAY = null;

    public static List<ImageModel> MULTI_PHOTOS_NEW = new ArrayList();

    public static List<DemoLibraryModel> DEMO_LIBRARY = new ArrayList();
    public static boolean NEW_LIB_IMAGE = true;
    public static boolean IS_CHECK_UPDATE = false;
    public static boolean IS_SHOW_DIALOG_SUB_5 = true;

    public static Boolean IS_LOADING_PHOTOS = true;

    static {
        String[] strArr = Build.VERSION.SDK_INT < 34 ? new String[]{"android.permission.READ_MEDIA_IMAGES"} : new String[]{"android.permission.READ_MEDIA_VISUAL_USER_SELECTED", "android.permission.READ_MEDIA_IMAGES"};
        arrayGalleryAndroid14 = strArr;
        if (Build.VERSION.SDK_INT < 33) {
            strArr = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
        }
        arrayGallery = strArr;
        arrayCamera = new String[]{"android.permission.CAMERA"};
    }

    public static boolean checkIsPremiumPro(String str) {
        return true;
    }

    public static boolean isActiveDiscount() {
        return false;
    }

    public static void safedk_Context_startActivity_97cb3195734cf5c9cc3418feeafa6dd6(Context p0, Intent p1) {
        if (p1 == null) {
            return;
        }
        p0.startActivity(p1);
    }

    public static String getPath(Context context, Uri uri) {
        Cursor query = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
        if (query == null) {
            return null;
        }
        int columnIndexOrThrow = query.getColumnIndexOrThrow("_data");
        query.moveToFirst();
        String string = query.getString(columnIndexOrThrow);
        query.close();
        return string;
    }

    public static String convertMediaUriToPath(Context context, Uri uri) {
        return "file://" + getFilePathForN(uri, context);
    }

    private static String getFilePathForN(Uri uri, Context context) {
        Cursor query = context.getContentResolver().query(uri, null, null, null, null);
        if (query == null) {
            return "";
        }
        int columnIndex = query.getColumnIndex("_display_name");
        int columnIndex2 = query.getColumnIndex("_size");
        query.moveToFirst();
        String string = query.getString(columnIndex);
        Long.toString(query.getLong(columnIndex2));
        File file = new File(context.getFilesDir(), string);
        try {
            InputStream openInputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[Math.min(openInputStream.available(), 1048576)];
            while (true) {
                int read = openInputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            openInputStream.close();
            fileOutputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }

    public static String saveImage(Context context, final String str) {
        str.lastIndexOf("png");
        new Thread(new Runnable() { // from class: mobi.zeezoo.photoenhancer.utils.Constants.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();
                    if (httpURLConnection.getResponseCode() == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                    }
                } catch (Exception e) {
                    Log.e("Exception", e.toString());
                }
            }
        }).start();
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0126  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0179 A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.String saveToStorage(android.content.Context r10, android.graphics.Bitmap r11, java.lang.String r12, java.lang.String r13) {
        /*
            Method dump skipped, instructions count: 378
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: mobi.zeezoo.photoenhancer.utils.Constants.saveToStorage(android.content.Context, android.graphics.Bitmap, java.lang.String, java.lang.String):java.lang.String");
    }

    /*private void downloadImageNew(Context context, String str, String str2, String str3) {
        if (str3 == null) {
            str3 = "";
        }
        String str4 = str3 + "Aiphoto_" + System.currentTimeMillis() + "." + str2;
        try {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(str));
            request.setAllowedNetworkTypes(3).setAllowedOverRoaming(false).setTitle(str4).setMimeType(MimeTypes.IMAGE_JPEG).setNotificationVisibility(1).setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + str4);
            downloadManager.enqueue(request);
            Toast.makeText(context, "Image download started.", 0).show();
        } catch (Exception unused) {
            Toast.makeText(context, "Image download failed.", 0).show();
        }
    }*/

    public static boolean checkGalleryPermission(Context context) {
        for (String str : arrayGallery) {
            int checkSelfPermission = ActivityCompat.checkSelfPermission(context, str);
            Log.i("checkGalleryPermission", "permission: " + str + ":" + checkSelfPermission);
            if (checkSelfPermission == 0) {
                return true;
            }
        }
        return false;
    }

    public static int isFullAccessPhoto(Context context) {
        int i = 0;
        if (Build.VERSION.SDK_INT < 34) {
            return 0;
        }
        boolean z = ContextCompat.checkSelfPermission(context, "android.permission.READ_MEDIA_VISUAL_USER_SELECTED") == 0;
        boolean z2 = ContextCompat.checkSelfPermission(context, "android.permission.READ_MEDIA_IMAGES") == 0;
        Log.i("checkGalleryPermission", "permission: " + z2 + ":" + z);
        if ((!z2 || !z) && (z2 || z)) {
            i = -1;
        }
        System.out.println("checkGalleryPermission____" + i);
        return i;
    }

    public static boolean checkCameraPermission(Context context) {
        for (String str : arrayCamera) {
            if (ContextCompat.checkSelfPermission(context, str) != 0) {
                return false;
            }
        }
        return true;
    }




    /* JADX WARN: Removed duplicated region for block: B:23:0x00e7 A[Catch: Exception -> 0x00ff, TryCatch #0 {Exception -> 0x00ff, blocks: (B:16:0x0097, B:18:0x009d, B:20:0x00a3, B:21:0x00d0, B:23:0x00e7, B:24:0x00fb, B:32:0x00b6, B:34:0x00bc, B:36:0x00c2), top: B:15:0x0097 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */

    /* JADX WARN: Removed duplicated region for block: B:18:0x00c0 A[Catch: Exception -> 0x00f5, TRY_ENTER, TryCatch #0 {Exception -> 0x00f5, blocks: (B:10:0x003b, B:12:0x0041, B:14:0x0047, B:15:0x0074, B:18:0x00c0, B:19:0x00ee, B:22:0x00e9, B:28:0x005a, B:30:0x0060, B:32:0x0066), top: B:9:0x003b }] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x00e9 A[Catch: Exception -> 0x00f5, TryCatch #0 {Exception -> 0x00f5, blocks: (B:10:0x003b, B:12:0x0041, B:14:0x0047, B:15:0x0074, B:18:0x00c0, B:19:0x00ee, B:22:0x00e9, B:28:0x005a, B:30:0x0060, B:32:0x0066), top: B:9:0x003b }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */


    public static void startActivityFeature(Context context, String str, String str2, Integer num, Integer num2, Boolean bool) {
        Log.e("CallfromStatuactivityFeature", "startActivityFeature: ");
        Intent intent = new Intent(context, ActivityProcess.class);
        if (str.equals(Feature.AI_FILTER.getValue())) {
            intent = new Intent(context, ActivityProcess.class);
        } else if (str.equals(Feature.REMOVEOBJ.getValue())) {
            intent = new Intent(context, RemoveObjActivity.class);
        }
        ArrayList arrayList = new ArrayList();
        ImageModel imageModel = new ImageModel();
        imageModel.setUri(str2);
        arrayList.add(imageModel);
        intent.putExtra("image_uri", str2);
        intent.putExtra(CHILD_DIR, arrayList);
        intent.putExtra("image_width", num);
        intent.putExtra("image_height", num2);
        intent.putExtra("feature", str);
        intent.putExtra("process_and_delete", bool);
        context.startActivity(intent);
    }

/*
    public static SizeImage getImageDimension(Context context, Uri uri) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(uri.getPath(), options);
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
            return new SizeImage(Integer.valueOf(options.outWidth), Integer.valueOf(options.outHeight));
        } catch (IOException unused) {
            return null;
        }
    }
*/

/*
    public static Target pushTarget(final ImageView imageView, ImageView imageView2, final Loading loading) {
        return new Target() { // from class: mobi.zeezoo.photoenhancer.utils.Constants.2
            @Override // com.squareup.picasso.Target
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                imageView.setImageBitmap(bitmap);
                loading.dismiss();
            }

            @Override // com.squareup.picasso.Target
            public void onBitmapFailed(Exception exc, Drawable drawable) {
                loading.dismiss();
            }

            @Override // com.squareup.picasso.Target
            public void onPrepareLoad(Drawable drawable) {
                loading.show();
            }
        };
    }
*/

    public static String formatDateMMDDYYYYString(long j) {
        try {
            return new SimpleDateFormat("MMM dd, YYYY").format(new Date(j));
        } catch (Exception unused) {
            return "";
        }
    }

/*
    public static void pushPaidToFireBase(FirebaseAnalytics firebaseAnalytics, String str, AdValue adValue, AdType adType) {
        ITGAppsflyer.getInstance().pushTrackEventAdmob(adValue, str, adType);
        Bundle bundle = new Bundle();
        bundle.putString("valuemicros", String.valueOf(adValue.getValueMicros()));
        bundle.putString("currency", adValue.getCurrencyCode());
        bundle.putString("precision", String.valueOf(adValue.getPrecisionType()));
        bundle.putString("adunitid", str);
        bundle.putString(a.d, "Admob");
        firebaseAnalytics.logEvent("paid_ad_impression", bundle);
        float tRoasRevenue = SharePreferenceRepositoryImpl.getSharedPreferences().getTRoasRevenue() + (((float) adValue.getValueMicros()) / 1000000.0f);
        double d = tRoasRevenue;
        if (d >= 0.01d) {
            Bundle bundle2 = new Bundle();
            bundle2.putDouble("value", d);
            bundle2.putString("currency", adValue.getCurrencyCode());
            firebaseAnalytics.logEvent("Daily_Ads_Revenue", bundle2);
            firebaseAnalytics.logEvent("Daily_Ads_Revenue_Copy", bundle2);
            SharePreferenceRepositoryImpl.getSharedPreferences().setTRoasRevenue(0.0f);
            return;
        }
        SharePreferenceRepositoryImpl.getSharedPreferences().setTRoasRevenue(tRoasRevenue);
    }
*/

/*
    public static void setAdSize(Context context, FrameLayout frameLayout, AdView adView) {
        float width = frameLayout.getWidth();
        if (width == 0.0f) {
            width = Resources.getSystem().getDisplayMetrics().widthPixels;
        }
        int i = (int) (width / context.getResources().getDisplayMetrics().density);
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, i).getHeight();
        adView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, i));
    }
*/

/*
    public static void setAdSize(Context context, LinearLayout linearLayout, AdView adView) {
        float width = linearLayout.getWidth();
        if (width == 0.0f) {
            width = Resources.getSystem().getDisplayMetrics().widthPixels;
        }
        int i = (int) (width / context.getResources().getDisplayMetrics().density);
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, i).getHeight();
        adView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, i));
    }
*/

    public static int dpToPixel(float f) {
        return (int) TypedValue.applyDimension(1, f, Resources.getSystem().getDisplayMetrics());
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static void showToast(final String str) {
      /*  ThreadUtils.runOnMainThread(new Action() { // from class: mobi.zeezoo.photoenhancer.utils.Constants.3
            @Override // io.reactivex.functions.Action
            public void run() throws Exception {
                View inflate = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.layout_custom_toast, (ViewGroup) null);
                ((TextView) inflate.findViewById(R.id.tvToast)).setText(str);
                Toast toast = new Toast(context);
                toast.setDuration(0);
                toast.setGravity(17, 0, 0);
                toast.setView(inflate);
                toast.show();
            }
        });*/
    }

  /*  public static Boolean isShowRateStore() {
        long lastShowRateStore = SharePreferenceRepositoryImpl.getSharedPreferences().getLastShowRateStore();
        if (lastShowRateStore <= 0) {
            return true;
        }
        Calendar.getInstance().setTimeInMillis(System.currentTimeMillis());
        if (r2.getActualMaximum(6) != lastShowRateStore) {
            return true;
        }
        return false;
    }*/

    /* JADX WARN: Not initialized variable reg: 1, insn: 0x0033: MOVE (r0 I:??[OBJECT, ARRAY]) = (r1 I:??[OBJECT, ARRAY]), block:B:25:0x0033 */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0036 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.String convertImageToBase64(java.io.File r4) {
        /*
            r0 = 0
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L20 java.io.IOException -> L22
            r1.<init>(r4)     // Catch: java.lang.Throwable -> L20 java.io.IOException -> L22
            long r2 = r4.length()     // Catch: java.io.IOException -> L1e java.lang.Throwable -> L32
            int r4 = (int) r2     // Catch: java.io.IOException -> L1e java.lang.Throwable -> L32
            byte[] r4 = new byte[r4]     // Catch: java.io.IOException -> L1e java.lang.Throwable -> L32
            r1.read(r4)     // Catch: java.io.IOException -> L1e java.lang.Throwable -> L32
            r2 = 0
            java.lang.String r4 = android.util.Base64.encodeToString(r4, r2)     // Catch: java.io.IOException -> L1e java.lang.Throwable -> L32
            r1.close()     // Catch: java.io.IOException -> L19
            goto L1d
        L19:
            r0 = move-exception
            r0.printStackTrace()
        L1d:
            return r4
        L1e:
            r4 = move-exception
            goto L24
        L20:
            r4 = move-exception
            goto L34
        L22:
            r4 = move-exception
            r1 = r0
        L24:
            r4.printStackTrace()     // Catch: java.lang.Throwable -> L32
            if (r1 == 0) goto L31
            r1.close()     // Catch: java.io.IOException -> L2d
            goto L31
        L2d:
            r4 = move-exception
            r4.printStackTrace()
        L31:
            return r0
        L32:
            r4 = move-exception
            r0 = r1
        L34:
            if (r0 == 0) goto L3e
            r0.close()     // Catch: java.io.IOException -> L3a
            goto L3e
        L3a:
            r0 = move-exception
            r0.printStackTrace()
        L3e:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: mobi.zeezoo.photoenhancer.utils.Constants.convertImageToBase64(java.io.File):java.lang.String");
    }


    public static void openTerms(Context context) {
        try {
            safedk_Context_startActivity_97cb3195734cf5c9cc3418feeafa6dd6(context, new Intent("android.intent.action.VIEW", Uri.parse("https://sites.google.com/view/zeezooterms")));
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Cannot open browser");
        }
    }

    public static void openPolicy(Context context) {
        try {
            safedk_Context_startActivity_97cb3195734cf5c9cc3418feeafa6dd6(context, new Intent("android.intent.action.VIEW", Uri.parse("https://sites.google.com/view/photoenhancerzeezoo")));
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Cannot open browser");
        }
    }

  /*  public static void setActiveDiscount() {
        Calendar.getInstance().setTimeInMillis(System.currentTimeMillis());
        SharePreferenceRepositoryImpl.getSharedPreferences().setLong(DISCOUNT_ACTIVE, ((r0.get(1) % 100) * 10000) + ((r0.get(2) + 1) * 100) + r0.get(5));
    }*/

   /* private static boolean isDiscountRunAtTimeDay() {
        long j = SharePreferenceRepositoryImpl.getSharedPreferences().getLong("countdown_time_count");
        long j2 = SharePreferenceRepositoryImpl.getSharedPreferences().getLong(TIME_DISCOUNT);
        return j == 0 || (TIME_UNIT - (1000 * j)) - ((j2 > 0L ? 1 : (j2 == 0L ? 0 : -1)) != 0 ? System.currentTimeMillis() - j2 : 0L) > 0;
    }*/

    public static boolean isPastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar calendar2 = Calendar.getInstance();
        System.out.println("Constants_jacky" + date + calendar2.get(6) + "---" + calendar.get(6));
        if (calendar2.get(1) <= calendar.get(1)) {
            return calendar2.get(1) == calendar.get(1) && calendar2.get(6) > calendar.get(6);
        }
        return true;
    }

    /*public static void saveStoreDate(Date date) {
        SharePreferenceRepositoryImpl.getSharedPreferences().setLong(KEY_STORED_DATE, date.getTime());
    }

    public static Date getStoredDate() {
        long j = SharePreferenceRepositoryImpl.getSharedPreferences().getLong(KEY_STORED_DATE);
        if (j == 0) {
            saveStoreDate(Calendar.getInstance().getTime());
        }
        System.out.println("Constants_jacky=======" + j);
        return j == 0 ? Calendar.getInstance().getTime() : new Date(j);
    }*/

    public static void vibrate(Context context, long duration) {
        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(duration);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File convertContentToFile(Context context, String str) throws IOException {
        InputStream openInputStream = null;
        Uri parse = Uri.parse(str);
        File file = null;
        try {
            openInputStream = context.getContentResolver().openInputStream(parse);

        } catch (IOException e) {
            e.printStackTrace();
        }

        file = File.createTempFile("temp_file", null, context.getCacheDir());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bArr = new byte[4096];
        while (true) {
            int read = openInputStream.read(bArr);
            if (read == -1) {
                break;
            }
            fileOutputStream.write(bArr, 0, read);
        }
        fileOutputStream.close();
        if (openInputStream != null) {
            openInputStream.close();
        }
        return file;
    }

}
