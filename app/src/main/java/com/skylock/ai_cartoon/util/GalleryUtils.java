package com.skylock.ai_cartoon.util;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;


import com.skylock.ai_cartoon.model.AlbumModel;
import com.skylock.ai_cartoon.model.ImageModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



/**
 * Singleton utility for loading photos and videos from the device gallery.
 * Clean Java rewrite — all Kotlin internals (Intrinsics, StringsKt, CollectionsKt,
 * CloseableKt, Metadata) removed and replaced with standard Java equivalents.
 */
public final class GalleryUtils {

    // ------------------------------------------------------------------
    // Singleton
    // ------------------------------------------------------------------

    public static final GalleryUtils INSTANCE = new GalleryUtils();

    private GalleryUtils() {}

    // ------------------------------------------------------------------
    // Constants / fields
    // ------------------------------------------------------------------

    private static final String AUDIO_COLUMN_ALBUM_ARTIST = "album_artist";

    private static final Uri EXTERNAL_COVERS_URI =
            Uri.parse("content://media/external/audio/albumart");

    private static final Lock lock = new ReentrantLock();

    private static boolean isLoadingImage = false;

    private static ArrayList<ImageModel> listHistoryPhotos = new ArrayList<>();
    private static ArrayList<AlbumModel> listAllPhotos     = new ArrayList<>();

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    public ArrayList<ImageModel> getListHistoryPhotos() {
        return listHistoryPhotos;
    }

    public void setListHistoryPhotos(ArrayList<ImageModel> list) {
        if (list == null) throw new NullPointerException("list must not be null");
        listHistoryPhotos = list;
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    /** Returns true if the MIME type starts with "image". */
    private boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image");
    }

    /** Returns true if running on Android 10 (API 29) or higher. */
    private boolean isMinSdk29() {
        return Build.VERSION.SDK_INT >= 29;
    }

    /** Returns the correct MediaStore URI for the current API level. */
    private Uri getImageCollectionUri() {
        if (isMinSdk29()) {
            return MediaStore.Images.Media.getContentUri("external_primary");
        }
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    /** Returns true if the file path ends with ".mp3". */
    private boolean isAudioFile(String path) {
        if (TextUtils.isEmpty(path)) return false;
        return path.endsWith(".mp3");
    }

    // ------------------------------------------------------------------
    // Projection
    // ------------------------------------------------------------------

    /**
     * Full column projection used by audio/media queries.
     */
    public String[] getProjection() {
        return new String[]{
                "_id",
                "date_added",
                "date_modified",
                "_display_name",
                "_size",
                "duration",   // "duration"
                "mime_type",
                "title",
                "year",
                "album",
                "album_id",
                "artist",
                "_data",
                AUDIO_COLUMN_ALBUM_ARTIST
        };
    }

    // ------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------

    /**
     * Returns true when the gallery has NOT been fully loaded yet
     * AND no loading is currently in progress.
     */
    public boolean checkLoadFull(Context ctx) {
        if (ctx == null) throw new NullPointerException("ctx must not be null");
        return Constants.isFullAccessPhoto(ctx) == 0 && !isLoadingImage;
    }

    // ------------------------------------------------------------------
    // getAllPhotosBackground  (uses deprecated managedQuery — preserved as-is)
    // ------------------------------------------------------------------

    /**
     * Loads all photos from the device into {@link #listAllPhotos} and
     * {@link #listHistoryPhotos}. Designed to be called from a background thread.
     *
     * <p>Skips .gif / .ai / .psd / .tif files.
     * Photos whose title starts with "AIHeadshot_AIFace" are added to the
     * history list as well as the main list.
     */
    @SuppressWarnings("deprecation")
    public void getAllPhotosBackground(Activity activity) {
        ArrayList<ImageModel> allImages = new ArrayList<>();

        if (activity == null) return;
        if (activity.isFinishing()) return;
        if (!Constants.checkGalleryPermission(activity)) return;

        String[] projection = {"title", "_data", "_id", "bucket_display_name"};

        listAllPhotos.clear();
        listHistoryPhotos.clear();

        try {
            Cursor cursor = activity.managedQuery(
                    getImageCollectionUri(),
                    projection,
                    null,
                    null,
                    "date_added DESC"
            );

            System.out.println("isFullAccessPhoto" + cursor.getCount());

            int count = cursor.getCount();
            int colAlbum = cursor.getColumnIndex("bucket_display_name");
            int colTitle = cursor.getColumnIndex("title");
            int colData  = cursor.getColumnIndex("_data");

            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);

                String title     = cursor.getString(colTitle);
                String filePath  = cursor.getString(colData);
                String albumName = cursor.getString(colAlbum);

                if (filePath == null) continue;

                // Skip unsupported formats
                if (filePath.endsWith(".gif") || filePath.endsWith(".ai")
                        || filePath.endsWith(".psd") || filePath.endsWith(".tif")) {
                    continue;
                }

                if (title == null) continue;

                ImageModel img = new ImageModel(
                        null, title, null, albumName, filePath,
                        null, false, 0,
                        null, null, false, false,
                        null, null, null, null, null, null,
                        262117, null
                );

                // History bucket: app-generated photos
                if (title.startsWith("AIHeadshot_AIFace")) {
                    System.out.println("getAllPhotos_" + filePath);
                    listHistoryPhotos.add(img);
                }

                allImages.add(img);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (EmptyUtils.isEmpty(allImages)) return;

        // Build album map
        listAllPhotos.clear();
        LinkedHashMap<String, AlbumModel> albumMap = new LinkedHashMap<>();

        for (ImageModel img : allImages) {
            String albumName = img.getAlbumName();
            if (albumName != null && !albumMap.containsKey(albumName)) {
                AlbumModel album = new AlbumModel(null, img.getPhotoUri(), albumName, 1, null);
                albumMap.put(albumName, album);
            }
            AlbumModel album = albumMap.get(albumName);
            if (album != null && album.getAlbumPhotos() != null) {
                album.getAlbumPhotos().add(img);
            }
        }

        // "All Photo" album at top
        if (!allImages.isEmpty()) {
            ImageModel first = allImages.get(0);
            listAllPhotos.add(new AlbumModel(
                    allImages,
                    first != null ? first.getPhotoUri() : null,
                    "All Photo"
            ));
        } else {
            listAllPhotos.add(new AlbumModel(new ArrayList<>(), "", "All Photo"));
        }

        listAllPhotos.addAll(albumMap.values());
    }

    // ------------------------------------------------------------------
    // getAllPhotos  (uses ContentResolver.query — modern approach)
    // ------------------------------------------------------------------

    /**
     * Queries the device gallery and returns all non-GIF/AI/PSD/TIF images.
     * Also populates {@link #listHistoryPhotos} with app-generated photos
     * (those whose title starts with "Aiphoto").
     */
    public ArrayList<ImageModel> getAllPhotos(Activity activity) {
        ArrayList<ImageModel> result = new ArrayList<>();

        if (activity == null || activity.isFinishing()) return result;
        if (!Constants.checkGalleryPermission(activity)) return result;

        String[] projection = {"title", "_data", "bucket_display_name"};

        listHistoryPhotos.clear();
        listAllPhotos.clear();

        Cursor cursor = activity.getContentResolver().query(
                getImageCollectionUri(),
                projection,
                null,
                null,
                "date_added DESC"
        );

        if (cursor == null) return result;

        try {
            if (cursor.getCount() > 0) {
                int colAlbum = cursor.getColumnIndex("bucket_display_name");
                int colTitle = cursor.getColumnIndex("title");
                int colData  = cursor.getColumnIndex("_data");

                while (cursor.moveToNext()) {
                    String title = cursor.isNull(colTitle) ? null : cursor.getString(colTitle);
                    if (title == null) continue;

                    String filePath = cursor.isNull(colData) ? null : cursor.getString(colData);
                    if (filePath == null) continue;

                    String albumName = cursor.isNull(colAlbum)
                            ? "Unknown"
                            : cursor.getString(colAlbum);
                    if (albumName == null) {
                        albumName = "Unknown";
                    }

                    // Skip unsupported formats
                    if (filePath.endsWith(".gif") || filePath.endsWith(".ai")
                            || filePath.endsWith(".psd") || filePath.endsWith(".tif")) {
                        continue;
                    }

                    ImageModel img = new ImageModel(
                            null, title, null, albumName, filePath,
                            null, false, 0,
                            null, null, false, false,
                            null, null, null, null, null, null,
                            262117, null
                    );

                    // History bucket
                    if (title.startsWith("Aiphoto")) {
                        listHistoryPhotos.add(img);
                    }

                    result.add(img);
                }
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    // ------------------------------------------------------------------
    // folderListFromImages
    // ------------------------------------------------------------------

    /**
     * Builds a list of {@link AlbumModel} folders grouped by bucket name,
     * reading from the Images MediaStore. The first entry is always "All Photo".
     */
    @SuppressWarnings("deprecation")
    public List<AlbumModel> folderListFromImages(Activity context) {
        List<AlbumModel> result = new ArrayList<>();
        if (context == null || context.isFinishing()) return result;

        String[] projection = {"title", "_data", "_id", "bucket_display_name"};

        try {
            Cursor cursor = context.managedQuery(
                    getImageCollectionUri(),
                    projection,
                    null,
                    null,
                    "date_added DESC"
            );

            if (cursor == null) return result;

            ArrayList<ImageModel> allImages = new ArrayList<>();
            int colAlbum = cursor.getColumnIndex("bucket_display_name");
            int colTitle = cursor.getColumnIndex("title");
            int colData  = cursor.getColumnIndex("_data");
            int count    = cursor.getCount();

            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);

                String title     = cursor.getString(colTitle);
                String filePath  = cursor.getString(colData);
                String albumName = cursor.getString(colAlbum);

                if (filePath == null || title == null) continue;

                if (filePath.endsWith(".gif") || filePath.endsWith(".ai")
                        || filePath.endsWith(".psd") || filePath.endsWith(".tif")) {
                    continue;
                }

                allImages.add(new ImageModel(
                        null, title, null, albumName, filePath,
                        null, false, 0,
                        null, null, false, false,
                        null, null, null, null, null, null,
                        262117, null
                ));
            }

            // Group into albums
            LinkedHashMap<String, AlbumModel> albumMap = new LinkedHashMap<>();
            for (ImageModel img : allImages) {
                String albumName = img.getAlbumName();
                if (albumName != null && !albumMap.containsKey(albumName)) {
                    albumMap.put(albumName, new AlbumModel(null, img.getPhotoUri(), albumName, 1, null));
                }
                AlbumModel album = albumMap.get(albumName);
                if (album != null && album.getAlbumPhotos() != null) {
                    album.getAlbumPhotos().add(img);
                }
            }

            // "All Photo" first
            if (!allImages.isEmpty()) {
                ImageModel first = allImages.get(0);
                result.add(new AlbumModel(
                        allImages,
                        first != null ? first.getPhotoUri() : null,
                        "All Photo"
                ));
            } else {
                result.add(new AlbumModel(new ArrayList<>(), "", "All Photo"));
            }

            result.addAll(albumMap.values());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // ------------------------------------------------------------------
    // folderListFromVideos
    // ------------------------------------------------------------------

    /**
     * Builds a list of {@link AlbumModel} folders grouped by bucket name,
     * reading from the Video MediaStore. The first entry is always "All Video".
     */
    @SuppressWarnings("deprecation")
    public List<AlbumModel> folderListFromVideos(Activity context) {
        List<AlbumModel> result = new ArrayList<>();
        if (context == null || context.isFinishing()) return result;

        String[] projection = {
                "title", "_data", "_id",
                "duration",
                "bucket_display_name"
        };

        try {
            Cursor cursor = context.managedQuery(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    "date_added DESC"
            );

            if (cursor == null) return result;

            ArrayList<ImageModel> allVideos = new ArrayList<>();
            int colAlbum    = cursor.getColumnIndex("bucket_display_name");
            int colTitle    = cursor.getColumnIndex("title");
            int colData     = cursor.getColumnIndex("_data");
            int colDuration = cursor.getColumnIndex("duration");
            int count       = cursor.getCount();

            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);

                String title     = cursor.getString(colTitle);
                String filePath  = cursor.getString(colData);
                String albumName = cursor.getString(colAlbum);
                long   duration  = cursor.getLong(colDuration);

                if (filePath == null || title == null) continue;

                allVideos.add(new ImageModel(
                        null, title, null, albumName, filePath,
                        duration, false, 0,
                        null, null, false, false,
                        null, null, null, null, null, null,
                        262085, null
                ));
            }

            // Group into albums
            LinkedHashMap<String, AlbumModel> albumMap = new LinkedHashMap<>();
            for (ImageModel img : allVideos) {
                String albumName = img.getAlbumName();
                if (albumName != null && !albumMap.containsKey(albumName)) {
                    albumMap.put(albumName, new AlbumModel(null, img.getPhotoUri(), albumName, 1, null));
                }
                AlbumModel album = albumMap.get(albumName);
                if (album != null && album.getAlbumPhotos() != null) {
                    album.getAlbumPhotos().add(img);
                }
            }

            // "All Video" first
            if (!allVideos.isEmpty()) {
                ImageModel first = allVideos.get(0);
                result.add(new AlbumModel(
                        allVideos,
                        first != null ? first.getPhotoUri() : null,
                        "All Video"
                ));
            } else {
                result.add(new AlbumModel(new ArrayList<>(), "", "All Video"));
            }

            result.addAll(albumMap.values());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // ------------------------------------------------------------------
    // getMediaVideos  (private — used internally)
    // ------------------------------------------------------------------

    /**
     * Returns a flat list of all video {@link ImageModel} objects from the device.
     */
    @SuppressWarnings("deprecation")
    private ArrayList<ImageModel> getMediaVideos(Activity activity) {
        String[] projection = {
                "title", "_data", "_id",
                "duration",
                "bucket_display_name"
        };

        ArrayList<ImageModel> result = new ArrayList<>();

        try {
            Cursor cursor = activity.managedQuery(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    "date_added DESC"
            );

            int count       = cursor.getCount();
            int colTitle    = cursor.getColumnIndex("title");
            int colData     = cursor.getColumnIndex("_data");
            int colDuration = cursor.getColumnIndex("duration");
            int colAlbum    = cursor.getColumnIndex("bucket_display_name");

            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);

                String albumName = cursor.getString(colAlbum);
                String title     = cursor.getString(colTitle);
                String filePath  = cursor.getString(colData);
                long   duration  = cursor.getLong(colDuration);

                if (filePath == null) continue;

                result.add(new ImageModel(
                        null, title, null, albumName, filePath,
                        duration, false, 0,
                        null, null, false, false,
                        null, null, null, null, null, null,
                        262085, null
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return result;
    }
}