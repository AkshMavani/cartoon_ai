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

import com.skylock.ai_cartoon.activity.ActivityEnhance;
import com.skylock.ai_cartoon.activity.CropImageActivity;
import com.skylock.ai_cartoon.activity.LibraryVer2Activity;
import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.adapter.AddPhotoLibraryNewAdapter;
import com.skylock.ai_cartoon.adapter.PhotoLibraryNewAdapter;
import com.skylock.ai_cartoon.base.BaseFragment;
import com.skylock.ai_cartoon.databinding.FragmentLibraryBinding;
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

    private AddPhotoLibraryNewAdapter addPhotoLibraryNewAdapter;

    private long lastSelectImage;
    private int pos;
    private int positionImg;
    private String style;
    private String feature = "library_fragment";

    private List<ImageModel> photos = new ArrayList<>();
    private final List<ImageModel> demoPhotos = new ArrayList<>();

    private PhotoLibraryNewAdapter photoLibraryAdapter;

    private PhotoLibraryNewAdapter getPhotoLibraryAdapter() {
        if (photoLibraryAdapter == null) {
            photoLibraryAdapter = new PhotoLibraryNewAdapter("library");
        }
        return photoLibraryAdapter;
    }

    public static LibraryFragment newInstance(int pos, List<ImageModel> photos, String style, int positionImg) {
        LibraryFragment fragment = new LibraryFragment();
        fragment.photos = photos != null ? photos : new ArrayList<>();
        fragment.pos = pos;
        fragment.style = style;
        fragment.positionImg = positionImg;
        return fragment;
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
        // Log the initial entry and position
        Log.d("LibraryDebug", "onItemClicked entry -> position: " + position);

        if (position < 0) {
            Log.e("LibraryDebug", "onItemClicked: Position is invalid (less than 0)");
            Constants.showToast(requireContext(), getString(R.string.photo_loading));
            return;
        }

        // Check if the activity is in Multiple Selection mode
        boolean isMultiple = false;
        if (requireActivity() instanceof LibraryVer2Activity) {
            isMultiple = ((LibraryVer2Activity) requireActivity()).isMultiple;
        }
        Log.d("LibraryDebug", "isMultiple mode: " + isMultiple);

        // Debounce check to prevent double clicks
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastSelectImage < 1000 && !isMultiple) {
            Log.w("LibraryDebug", "Click ignored: Debounce active (less than 1000ms since last click)");
            return;
        }
        this.lastSelectImage = currentTimeMillis;

        // Get the image model from the adapter
        ImageModel imageModel = getPhotoLibraryAdapter().getPhotos().get(position);
        Log.d("LibraryDebug", "Selected Image URI: " + imageModel.getPhotoUri());

        // Case 1: Multiple Selection Mode
        if (isMultiple) {
            if (imageModel.isCamera()) {
                Log.d("LibraryDebug", "Multiple mode: Camera item clicked, requesting permission");
                if (requireActivity() instanceof LibraryVer2Activity) {
                    ((LibraryVer2Activity) requireActivity()).requestPermissionCamera();
                }
            } else {
                Log.d("LibraryDebug", "Multiple mode: Adding photo to collection");
                onSelectMultiPhotos(imageModel);
            }
            return;
        }

        // Case 2: Demo/Icon items (Not actual photos)
        Integer icon = imageModel.getIcon();
        if (icon != null && icon != 0) {
            Log.d("LibraryDebug", "Icon item clicked (Demo)");
            Constants.showToast(requireContext(), "Development...");
            return;
        }

        // Case 3: Single Selection - Camera
        if (imageModel.isCamera()) {
            Log.d("LibraryDebug", "Single mode: Camera item clicked");
            if (requireActivity() instanceof LibraryVer2Activity) {
                ((LibraryVer2Activity) requireActivity()).requestPermissionCamera();
            }
            return;
        }

        // Case 4: Feature - Remove Object
        if (feature.equals(Feature.REMOVEOBJ.getValue())) {
            Log.d("LibraryDebug", "Navigating to REMOVE_OBJECT feature");
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

        // Case 5: Feature - AI Filters / Cartoon / Headshot
        if (feature.equals(Feature.AI_HUGGING.getValue())
                || feature.equals(Feature.AI_FILTER.getValue())
                || feature.equals(Feature.HEADSHOT.getValue())
                || feature.equals(Feature.HAIR_STYLE.getValue())) {

            Log.d("LibraryDebug", "Navigating to Cartoon/AI activity with feature: " + feature);
            Companion.onSelectOnePhoto(requireContext(), imageModel);
            startCartoonActivity(imageModel);
            return;
        }

        // Case 6: Default fallback (e.g., Enhance)
        Log.d("LibraryDebug", "Default Case: Adding to MULTI_PHOTOS_NEW and processing");
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

        List<ImageModel> subList = this.photos.subList(
                this.demoPhotos.size() + 1, this.photos.size()
        );

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
        intent.putExtra("position_img", this.positionImg);
        startActivity(intent);
      //  ActivityExtKt.applyTransition(requireActivity(), R.anim.slide_in_right, R.anim.slide_nothing);
    }

    public void onProcessMultiPhotos() {
        for (ImageModel imageModel : Constants.MULTI_PHOTOS_NEW) {
            Companion.onSelectOnePhoto(requireContext(), imageModel);
        }

        Intent intent = new Intent(requireActivity(), ActivityEnhance.class);
        intent.putExtra("feature", this.feature);

        if (requireActivity() instanceof LibraryVer2Activity) {
            if (!((LibraryVer2Activity) requireActivity()).isMultiple) {
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

            List<ImageModel> subList = this.photos.subList(
                    this.demoPhotos.size() + 1, this.photos.size()
            );
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

    // ─── BaseFragment also needed ──────────────────────────────────────────────

    public static final class Companion {

        private Companion() {}

        public static void onSelectOnePhoto(Context context, ImageModel photo) {
            if (photo == null) {
                Constants.showToast(context, context.getString(R.string.server_busy_try_again_10s));
                return;
            }

            File file = new File(photo.getPhotoUri());
            long largeSizeThreshold = Build.VERSION.SDK_INT > 27 ? 5_000_000L : 2_000_000L;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            if (file.length() > 10_000_000L) {
                options.inSampleSize = 5;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
            } else if (file.length() > 7_000_000L) {
                options.inSampleSize = 4;
            } else if (file.length() > 5_000_000L) {
                options.inSampleSize = 3;
            }

            if (file.length() >= largeSizeThreshold) {
                File tempFile = new File(
                        new ContextWrapper(context).getCacheDir(),
                        photo.getAlbumName() + "_Temp_" + file.getName()
                );

                if (tempFile.exists()) {
                    photo.setPath(tempFile.getAbsolutePath());
                    photo.setUri("file://" + tempFile.getAbsolutePath());
                    return;
                }

                Bitmap bitmap = null;
                try {
                    FileInputStream fis = new FileInputStream(file);
                    bitmap = BitmapFactory.decodeStream(fis, null, options);
                    fis.close();
                } catch (IOException e) {
                    return;
                }

                if (bitmap == null) return;

                photo.setHeight(bitmap.getHeight());
                photo.setWidth(bitmap.getWidth());

                try {
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    boolean compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    if (compressed) {
                        photo.setPath(tempFile.getAbsolutePath());
                        photo.setUri("file://" + tempFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Bitmap bitmap = BitmapFactory.decodeFile(photo.getPhotoUri(), options);
                if (bitmap == null) return;
                photo.setHeight(bitmap.getHeight());
                photo.setWidth(bitmap.getWidth());
                photo.setPath(file.getAbsolutePath());
                photo.setUri("file://" + file.getAbsolutePath());
            }
        }
    }
}