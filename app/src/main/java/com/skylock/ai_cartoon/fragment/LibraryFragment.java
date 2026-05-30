package com.skylock.ai_cartoon.fragment;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.activity.AIAvatarProcessingActivity;
import com.skylock.ai_cartoon.activity.CropImageActivity;
import com.skylock.ai_cartoon.activity.LibraryVer2Activity;
import com.skylock.ai_cartoon.adapter.AddPhotoLibraryNewAdapter;
import com.skylock.ai_cartoon.adapter.PhotoLibraryNewAdapter;
import com.skylock.ai_cartoon.base.BaseFragment;
import com.skylock.ai_cartoon.databinding.FragmentLibraryBinding;
import com.skylock.ai_cartoon.enhance.ProcessTypeConfig;
import com.skylock.ai_cartoon.model.DemoLibraryModel;
import com.skylock.ai_cartoon.model.ImageModel;
import com.skylock.ai_cartoon.util.Constants;
import com.skylock.ai_cartoon.util.EmptyUtils;
import com.skylock.ai_cartoon.util.Feature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class LibraryFragment extends BaseFragment<FragmentLibraryBinding> {

    public static final Companion INSTANCE = new Companion();
    private final List<ImageModel> demoPhotos = new ArrayList<>();
    private AddPhotoLibraryNewAdapter addPhotoLibraryNewAdapter;
    private long lastSelectImage;
    private int pos;
    private int positionImg;
    private Boolean isGender;
    private String style;
    private String feature = "library_fragment";
    private List<ImageModel> photos = new ArrayList<>();
    private PhotoLibraryNewAdapter photoLibraryAdapter;

    public static LibraryFragment newInstance(int pos, List<ImageModel> photos, String style, int positionImg, boolean isGender) {
        LibraryFragment fragment = new LibraryFragment();
        fragment.photos = photos != null ? photos : new ArrayList<>();
        fragment.pos = pos;
        fragment.style = style;
        fragment.positionImg = positionImg;
        fragment.isGender = isGender;
        return fragment;
    }

    private PhotoLibraryNewAdapter getPhotoLibraryAdapter() {
        if (photoLibraryAdapter == null) {
            photoLibraryAdapter = new PhotoLibraryNewAdapter("library");
        }
        return photoLibraryAdapter;
    }

    @Override
    public FragmentLibraryBinding inflateLayout(LayoutInflater inflater, ViewGroup container) {
        return FragmentLibraryBinding.inflate(inflater, container, false);
    }

    @Override
    public void initData() {
        initAdapter();
        FragmentActivity activity = requireActivity();
        String featureFromActivity = null;
        if (activity instanceof LibraryVer2Activity) {
            featureFromActivity = ((LibraryVer2Activity) activity).getFeature();
        }
        this.feature = featureFromActivity != null ? featureFromActivity : "library_fragment";
    }

    @Override
    public void initView() {
        getPhotoLibraryAdapter().setOnRemovePhoto(position -> {
            ImageModel imageModel = getPhotoLibraryAdapter().getPhotos().get(position);
            if (imageModel != null) {
                onSelectMultiPhotos(imageModel);
            }
        });

        ItemClickSupport.addTo(getBinding().rvLibrary)
                .setOnItemClickListener((recyclerView, position, view) -> {
                    onItemClicked(position);
                });
    }

    private void onItemClicked(int position) {
        Log.d("LibraryDebug", "onItemClicked entry -> position: " + position);

        if (position < 0) {
            Log.e("LibraryDebug", "onItemClicked: Position is invalid (less than 0)");
            Constants.showToast(getString(R.string.photo_loading));
            return;
        }

        boolean isMultiple = false;
        if (requireActivity() instanceof LibraryVer2Activity) {
            isMultiple = ((LibraryVer2Activity) requireActivity()).isMultiple;
        }
        Log.d("LibraryDebug", "isMultiple mode: " + isMultiple);

        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastSelectImage < 1000 && !isMultiple) {
            Log.w("LibraryDebug", "Click ignored: Debounce active");
            return;
        }
        this.lastSelectImage = currentTimeMillis;

        ImageModel imageModel = getPhotoLibraryAdapter().getPhotos().get(position);
        Log.d("LibraryDebug", "Selected Image URI: " + imageModel.getPhotoUri());

        // Multiple selection mode
        if (isMultiple) {
            if (imageModel.isCamera()) {
                if (requireActivity() instanceof LibraryVer2Activity) {
                    ((LibraryVer2Activity) requireActivity()).requestPermissionCamera();
                }
            } else {
                onSelectMultiPhotos(imageModel);
            }
            return;
        }

        // Demo/Icon items
        Integer icon = imageModel.getIcon();
        if (icon != null && icon != 0) {
            Constants.showToast("Development...");
            return;
        }

        // Camera
        if (imageModel.isCamera()) {
            if (requireActivity() instanceof LibraryVer2Activity) {
                ((LibraryVer2Activity) requireActivity()).requestPermissionCamera();
            }
            return;
        }

        // Remove Object
        if (feature.equals(Feature.REMOVEOBJ.getValue())) {
            Companion.onSelectOnePhoto(requireContext(), imageModel);
            Constants.startActivityFeature(
                    requireContext(), feature,
                    imageModel.getPhotoUri(),
                    imageModel.getWidth(),
                    imageModel.getHeight(),
                    false
            );
            return;
        }

        // Cartoon / AI Filter / Headshot
        if (feature.equals(Feature.AI_HUGGING.getValue())
                || feature.equals(Feature.AI_FILTER.getValue())
                || feature.equals(Feature.HEADSHOT.getValue())
                || feature.equals(Feature.HAIR_STYLE.getValue())) {
            Companion.onSelectOnePhoto(requireContext(), imageModel);
            startCartoonActivity(imageModel);
            return;
        }

        // NEW: AI tools (brighten, dehaze, colorize, descratch, retouch, enhance, etc.)
        if (ProcessTypeConfig.INSTANCE.getConfig(feature) != null) {
            // Single image processing using AIAvatarProcessingActivity
            Companion.onSelectOnePhoto(requireContext(), imageModel);
            Intent intent = new Intent(requireActivity(), AIAvatarProcessingActivity.class);
            intent.putExtra("feature", feature);
            intent.putExtra("image_before", imageModel.getPhotoUri());
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing);
            return;
        }

        // Default fallback (e.g., legacy multi‑photo enhance)
        Log.d("LibraryDebug", "Default Case: Adding to MULTI_PHOTOS_NEW and processing");
        Constants.MULTI_PHOTOS_NEW.clear();          // Clear previous selections
        Constants.MULTI_PHOTOS_NEW.add(imageModel);
        onProcessMultiPhotos();
    }

    private void initAdapter() {
        getBinding().rvLibrary.setAdapter(getPhotoLibraryAdapter());
        getBinding().rvLibrary.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        this.demoPhotos.clear();

        if (EmptyUtils.isEmpty(this.photos)) {
            this.photos = new ArrayList<>();
        }

        if (EmptyUtils.isEmpty(this.photos) || (this.pos == 0 && !this.photos.get(0).isCamera())) {
            ImageModel cameraItem = new ImageModel();
            cameraItem.setCamera(true);
            this.photos.add(0, cameraItem);
        }

        if (this.pos == 0) {
            this.photos.removeIf(img -> EmptyUtils.isNotEmpty(img.getUriDemoPreview()));

            for (DemoLibraryModel demoLibraryModel : Constants.DEMO_LIBRARY) {
                if (demoLibraryModel.getFeature().equals(this.feature)) {
                    ImageModel demoItem = new ImageModel();
                    demoItem.setUriDemoAfter(demoLibraryModel.getAfter());
                    demoItem.setUriDemoPreview(demoLibraryModel.getPreview());
                    demoItem.setPhotoUri(demoLibraryModel.getBefore());
                    demoItem.setUri(demoLibraryModel.getBefore());
                    demoItem.setWidth(demoLibraryModel.getWidth());
                    demoItem.setHeight(demoLibraryModel.getHeight());
                    this.photos.add(1, demoItem);
                    this.demoPhotos.add(demoItem);
                }
            }

            if (Constants.isFullAccessPhoto(requireContext()) != 0) {
                updateImages(this.photos.subList(0, this.demoPhotos.size() + 1));
                initAdapterAddPhoto();
            } else {
                updateImages(this.photos);
            }
        } else {
            updateImages(this.photos);
        }
    }

    private void initAdapterAddPhoto() {
        this.photos.removeIf(ImageModel::isAddPhoto);
        List<ImageModel> subList = this.photos.subList(this.demoPhotos.size() + 1, this.photos.size());
        ImageModel addPhotoItem = new ImageModel();
        addPhotoItem.setAddPhoto(true);
        subList.add(0, addPhotoItem);
        this.addPhotoLibraryNewAdapter = new AddPhotoLibraryNewAdapter("library");
        this.addPhotoLibraryNewAdapter.setPhotos(subList);
    }

    private void updateImages(List<ImageModel> photos) {
        getPhotoLibraryAdapter().setPhotos(photos);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
    }

    private void startCartoonActivity(ImageModel photo) {
        Intent intent = new Intent(requireActivity(), CropImageActivity.class);
        intent.putExtra("image_uri", photo.getUri());
        intent.putExtra("image_width", photo.getWidth());
        intent.putExtra("image_height", photo.getHeight());
        intent.putExtra("feature", this.feature);
        intent.putExtra("style", this.style);
        intent.putExtra("is_gender", this.isGender);
        intent.putExtra("position_img", this.positionImg);
        startActivity(intent);
    }

    public void onProcessMultiPhotos() {
        for (ImageModel imageModel : Constants.MULTI_PHOTOS_NEW) {
            Companion.onSelectOnePhoto(requireContext(), imageModel);
        }

        Intent intent = new Intent(requireActivity(), AIAvatarProcessingActivity.class);
        intent.putExtra("feature", this.feature);

        if (requireActivity() instanceof LibraryVer2Activity) {
            LibraryVer2Activity activity = (LibraryVer2Activity) requireActivity();
            if (!activity.isMultiple && Constants.MULTI_PHOTOS_NEW.size() == 1) {
                ImageModel single = Constants.MULTI_PHOTOS_NEW.get(0);
                String imageUri = single.getUri();
                if (imageUri == null) imageUri = single.getPhotoUri();
                intent.putExtra("image_before", imageUri);
            } else {
                intent.putExtra("images", (Serializable) Constants.MULTI_PHOTOS_NEW);
            }
        }

        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing);
    }

    private void onSelectMultiPhotos(ImageModel photo) {
        if (photo.isAddPhoto() || photo.isCamera()) return;

        if (!photo.isSelected()) {
            if (Constants.MULTI_PHOTOS_NEW.size() >= Constants.MAX_MULTI_ENHANCE) return;
            photo.setSelectNumber(Constants.MULTI_PHOTOS_NEW.size() + 1);
            photo.setSelected(true);
            Constants.MULTI_PHOTOS_NEW.add(photo);
        } else {
            Constants.MULTI_PHOTOS_NEW.remove(photo);
            photo.setSelectNumber(0);
            photo.setSelected(false);
        }

        if (requireActivity() instanceof LibraryVer2Activity) {
            ((LibraryVer2Activity) requireActivity()).onSelectMultiPhotos();
        }
    }

    public void updateLibsPhotoDemo(boolean isMultiple) {
        if (EmptyUtils.isEmpty(this.demoPhotos)) return;

        this.photos.removeIf(img -> EmptyUtils.isNotEmpty(img.getUriDemoPreview()));
        this.photos.removeIf(ImageModel::isAddPhoto);

        if (isMultiple) {
            if (!this.photos.isEmpty() && this.photos.get(0).isCamera()) {
                this.photos.remove(0);
            }
            for (ImageModel demoPhoto : this.demoPhotos) {
                this.photos.remove(demoPhoto);
            }
        } else {
            ImageModel cameraItem = new ImageModel();
            cameraItem.setCamera(true);
            if (!this.photos.isEmpty() && !this.photos.get(0).isCamera()) {
                this.photos.add(0, cameraItem);
            }
            Iterator<ImageModel> it = this.demoPhotos.iterator();
            while (it.hasNext()) {
                this.photos.add(1, it.next());
            }
        }

        if (Constants.isFullAccessPhoto(requireContext()) != 0) {
            int end = isMultiple ? 1 : this.demoPhotos.size() + 1;
            updateImages(this.photos.subList(0, end));
            List<ImageModel> subList = this.photos.subList(end, this.photos.size());
            ImageModel addPhotoItem = new ImageModel();
            addPhotoItem.setAddPhoto(true);
            subList.add(0, addPhotoItem);
            if (this.addPhotoLibraryNewAdapter != null) {
                this.addPhotoLibraryNewAdapter.setPhotos(subList);
            }
        } else {
            updateImages(this.photos);
        }
    }

    public void updateRemovePhoto(ImageModel photoLibrary) {
        if (photoLibrary == null) return;
        for (ImageModel imageModel : getPhotoLibraryAdapter().getPhotos()) {
            if (imageModel.getPhotoUri() != null
                    && imageModel.getPhotoUri().equals(photoLibrary.getPhotoUri())) {
                imageModel.setSelected(false);
                return;
            }
        }
    }

    public void setListAlbumPhotos(List<ImageModel> albumPhotos) {
        if (albumPhotos == null || !isAdded()) return;

        this.photos = albumPhotos;

        if (this.pos != 0) {
            updateImages(this.photos);
            return;
        }

        this.demoPhotos.clear();

        if (EmptyUtils.isEmpty(this.photos) || !this.photos.get(0).isCamera()) {
            ImageModel cameraItem = new ImageModel();
            cameraItem.setCamera(true);
            this.photos.add(0, cameraItem);
        }

        this.photos.removeIf(img -> EmptyUtils.isNotEmpty(img.getUriDemoPreview()));

        for (DemoLibraryModel demoLibraryModel : Constants.DEMO_LIBRARY) {
            if (demoLibraryModel.getFeature().equals(this.feature)) {
                ImageModel demoItem = new ImageModel();
                demoItem.setUriDemoAfter(demoLibraryModel.getAfter());
                demoItem.setUriDemoPreview(demoLibraryModel.getPreview());
                demoItem.setPhotoUri(demoLibraryModel.getBefore());
                demoItem.setUri(demoLibraryModel.getBefore());
                demoItem.setWidth(demoLibraryModel.getWidth());
                demoItem.setHeight(demoLibraryModel.getHeight());
                this.photos.add(1, demoItem);
                this.demoPhotos.add(demoItem);
            }
        }

        this.photos.removeIf(ImageModel::isAddPhoto);

        if (Constants.isFullAccessPhoto(requireContext()) != 0) {
            updateImages(this.photos.subList(0, this.demoPhotos.size() + 1));
            List<ImageModel> subList = this.photos.subList(this.demoPhotos.size() + 1, this.photos.size());
            ImageModel addPhotoItem = new ImageModel();
            addPhotoItem.setAddPhoto(true);
            subList.add(0, addPhotoItem);
            if (this.addPhotoLibraryNewAdapter != null) {
                this.addPhotoLibraryNewAdapter.setPhotos(subList);
            }
        } else {
            updateImages(this.photos);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Companion (enhanced with temp file recreation)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Companion {

        private Companion() {
        }

        public static void onSelectOnePhoto(Context context, ImageModel photo) {
            if (photo == null) {
                Constants.showToast(context.getString(R.string.server_busy_try_again_10s));
                return;
            }

            String originalPath = photo.getPhotoUri();
            if (originalPath == null) return;

            File originalFile = new File(originalPath);
            long largeSizeThreshold = Build.VERSION.SDK_INT > 27 ? 5_000_000L : 2_000_000L;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            long fileLen = originalFile.length();
            if (fileLen > 10_000_000L) {
                options.inSampleSize = 5;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
            } else if (fileLen > 7_000_000L) {
                options.inSampleSize = 4;
            } else if (fileLen > 5_000_000L) {
                options.inSampleSize = 3;
            }

            if (fileLen >= largeSizeThreshold) {
                String albumName = photo.getAlbumName();
                if (albumName == null) albumName = "temp";
                File tempFile = new File(
                        new ContextWrapper(context).getCacheDir(),
                        albumName + "_Temp_" + originalFile.getName()
                );

                // If temp file exists and is valid, use it; otherwise recreate
                if (tempFile.exists() && tempFile.length() > 0) {
                    if (photo.getWidth() == 0 || photo.getHeight() == 0) {
                        Bitmap bmp = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
                        if (bmp != null) {
                            photo.setWidth(bmp.getWidth());
                            photo.setHeight(bmp.getHeight());
                        }
                    }
                    photo.setPath(tempFile.getAbsolutePath());
                    photo.setUri("file://" + tempFile.getAbsolutePath());
                    return;
                }

                Bitmap bitmap = null;
                try (FileInputStream fis = new FileInputStream(originalFile)) {
                    bitmap = BitmapFactory.decodeStream(fis, null, options);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                if (bitmap == null) return;

                photo.setWidth(bitmap.getWidth());
                photo.setHeight(bitmap.getHeight());

                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    boolean compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    if (compressed) {
                        photo.setPath(tempFile.getAbsolutePath());
                        photo.setUri("file://" + tempFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Bitmap bitmap = BitmapFactory.decodeFile(originalPath, options);
                if (bitmap == null) return;
                photo.setWidth(bitmap.getWidth());
                photo.setHeight(bitmap.getHeight());
                photo.setPath(originalFile.getAbsolutePath());
                photo.setUri("file://" + originalFile.getAbsolutePath());
            }
        }
    }
}