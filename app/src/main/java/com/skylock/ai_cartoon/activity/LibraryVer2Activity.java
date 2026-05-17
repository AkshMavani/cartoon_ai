package com.skylock.ai_cartoon.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayoutMediator;
import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.adapter.ChooseMultiplePhotosAdapter;
import com.skylock.ai_cartoon.adapter.LibraryPagerAdapter;
import com.skylock.ai_cartoon.base.BaseActivity;
import com.skylock.ai_cartoon.databinding.ActivityLibraryVer2Binding;
import com.skylock.ai_cartoon.fragment.LibraryFragment;
import com.skylock.ai_cartoon.model.AlbumModel;
import com.skylock.ai_cartoon.model.ImageModel;
import com.skylock.ai_cartoon.model.PhotoLibrary;
import com.skylock.ai_cartoon.util.BottomSheetRecommend;
import com.skylock.ai_cartoon.util.Constants;
import com.skylock.ai_cartoon.util.CustomSmoothScroller;
import com.skylock.ai_cartoon.util.Feature;
import com.skylock.ai_cartoon.util.GalleryUtils;
import com.skylock.ai_cartoon.util.ToolEnhanceUtils;
import com.skylock.ai_cartoon.viewmodel.PickMediaViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public final class LibraryVer2Activity extends BaseActivity<ActivityLibraryVer2Binding> {

    private static final String TAG = "LibraryVer2Activity";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    public boolean isMultiple;
    // ---- Fields (matching original) ----
    private Logger logger = Logger.getLogger(LibraryVer2Activity.class.getName());
    private BottomSheetRecommend bottomSheetRecommend;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ChooseMultiplePhotosAdapter chooseMultiplePhotosAdapter;
    private int firstVisiblePosition;
    private int heightItemMultiplePhoto;
    private boolean isChangeMultiple;
    private boolean isCreated;
    private boolean isRequestPermissionCamera;
    private LibraryPagerAdapter libraryPagerViewPager;
    private Uri photoUri;
    private PickMediaViewModel pickMediaViewModel;
    private int sizeAlbum;

    // Lazy-read intent extras (read once, cached)
    private String featureCache;
    private String styleCache;

    // Permission & dialog
    private ActivityResultLauncher<String> requestPermissionCameraLauncher;
    private BottomSheetDialog requestPermissionDialog;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    // -----------------------------------------------------------------------
    // BaseActivity<T> required override
    // -----------------------------------------------------------------------

    @Override
    public ActivityLibraryVer2Binding inflateBinding(LayoutInflater inflater) {
        return ActivityLibraryVer2Binding.inflate(inflater);
    }

    // -----------------------------------------------------------------------
    // Lazy getters — exactly matching original Kotlin lazy delegates
    // -----------------------------------------------------------------------

    public String getFeature() {
        if (featureCache == null) {
            String s = getIntent().getStringExtra("feature");
            featureCache = (s == null) ? "library_screen_error" : s;
        }
        return featureCache;
    }

    private String getStyle() {
        if (styleCache == null) {
            styleCache = getIntent().getStringExtra("style");
        }
        return styleCache;
    }

    private int getPositionImg() {
        return getIntent().getIntExtra("position_img", -1);
    }

    public boolean isPremiumItem() {
        return getIntent().getBooleanExtra("is_premium_item", false);
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }


    private BottomSheetDialog getRequestPermissionDialog() {
        return requestPermissionDialog;
    }

    private PickMediaViewModel getPickMediaViewModel() {
        return pickMediaViewModel;
    }

    // -----------------------------------------------------------------------
    // initView — replaces onCreate logic
    // -----------------------------------------------------------------------

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        // Lazy inits
        pickMediaViewModel = new ViewModelProvider(this).get(PickMediaViewModel.class);
        requestPermissionDialog = new BottomSheetDialog(this, R.style.SheetDialog);

        // Must register BEFORE activity starts
        registerActivityResultLaunchers();

        // EdgeToEdge window insets
        ViewCompat.setOnApplyWindowInsetsListener(getBinding().getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        clearMultiPhotos();
        getDataIntent();

        System.out.println("onClickFeature:x" + System.currentTimeMillis());

        // ivQuestion visibility — VISIBLE for ENHANCE/AI_FILTER/HEADSHOT, else INVISIBLE
        String feature = getFeature();
        boolean showQ = feature.equals(Feature.ENHANCE.getValue())
                || feature.equals(Feature.AI_FILTER.getValue())
                || feature.equals(Feature.HEADSHOT.getValue());
        getBinding().ivQuestion.setVisibility(showQ ? View.VISIBLE : View.INVISIBLE);

        // Launch gallery permissions immediately
        requestPermissionLauncher.launch(Constants.arrayGallery);

        actionLayoutDenyPermission();

        getBinding().llLoadingPhoto.setVisibility(View.VISIBLE);
        getBinding().tvLoadingAd.getLayoutParams().height = Constants.dpToPixel(Constants.IS_TABLET ? 90 : 60);
        getBinding().tvLoadingAd.requestLayout();

        initListener();
        initData();

        // flMultiple — only visible for ENHANCE
        getBinding().flMultiple.setVisibility(feature.equals(Feature.ENHANCE.getValue()) ? View.VISIBLE : View.GONE);

        // flMultiple click — exact logic from original (premium null = show/hide, not go premium)
        getBinding().flMultiple.setOnClickListener(v -> {
            // Original: if (premium == null || !isMultiple) showItem else if (!empty) process else toast
            // Since we removed premium, treat as always no-premium = showItemMultiplePhotos only
            if (!isMultiple) {
                showItemMultiplePhotos();
            } else if (!Constants.MULTI_PHOTOS_NEW.isEmpty()) {
                onProcessMultiPhotos();
            } else {
                Constants.showToast(LibraryVer2Activity.this, getString(R.string.select_least_one_photo));
            }
        });
    }

    // -----------------------------------------------------------------------
    // Register ActivityResultLaunchers (must happen before onCreate completes)
    // -----------------------------------------------------------------------

    private void registerActivityResultLaunchers() {

        // Gallery multi-permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    // Original logic: if ALL granted → handlePermissionGranted + hide views
                    //                 else check rationale per-permission → gotoSetting if never-ask
                    Collection<Boolean> values = permissions.values();
                    boolean allGranted = true;
                    if (!(values instanceof Collection) || !values.isEmpty()) {
                        for (Boolean granted : values) {
                            if (!granted) {
                                allGranted = false;
                                break;
                            }
                        }
                    }
                    if (allGranted) {
                        handlePermissionGranted();
                        getBinding().group.setVisibility(View.GONE);
                        getBinding().denyPermission.main.setVisibility(View.GONE);
                    } else {
                        Set<Map.Entry<String, Boolean>> entrySet = permissions.entrySet();
                        if (entrySet.isEmpty()) return;
                        for (Map.Entry<String, Boolean> entry : entrySet) {
                            if (!entry.getValue() && !shouldShowRequestPermissionRationale(entry.getKey())) {
                                gotoSetting();
                                return;
                            }
                        }
                    }
                }
        );

        // Camera single-permission launcher
        requestPermissionCameraLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) openCamera();
                    else gotoSetting();
                }
        );

        // Camera intent launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri uri = photoUri;
                        if (uri != null) {
                            Log.e(TAG, "2");
                            String path = Constants.convertMediaUriToPath(this, uri);
                            if (path != null) {
                                goToFeatureAction(path);
                            } else {
                                Log.e(TAG, "3");
                                Constants.showToast(this, getString(R.string.error));
                            }
                        } else {
                            Log.e(TAG, "3");
                            Constants.showToast(this, getString(R.string.error));
                        }
                    }
                }
        );
    }

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // AdsManager.INSTANCE.loadRewardProcessing(this); — REMOVED
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getBinding().adViewContainer != null)
            getBinding().adViewContainer.setVisibility(View.GONE);
        if (getBinding().imgMultiplePro != null)
            getBinding().imgMultiplePro.setVisibility(View.GONE);
        if (getBinding().applyAnimation != null)
            getBinding().applyAnimation.setVisibility(View.GONE);

        if (isCreated) {
            new Thread(() -> {
                List<AlbumModel> albumModels = pickMediaViewModel.getAlbumModels().getValue();
                runOnUiThread(() -> initViewPager(albumModels));
            }).start();
        } else {
            isCreated = true;
        }
    }

    // -----------------------------------------------------------------------
    // initData / getDataIntent
    // -----------------------------------------------------------------------

    private void initData() {
        getRequestPermissionDialog().setContentView(R.layout.bottom_sheet_allow_gallery);
        getRequestPermissionDialog().setCanceledOnTouchOutside(false);
        getRequestPermissionDialog().setCancelable(false);
        // Original had: if RESTORE_OLD_PHOTO → goToPremium — REMOVED
    }

    private void getDataIntent() {
        String feature = getFeature();
        getBinding().tvLibraryHeader.setText(ToolEnhanceUtils.getFeature(this, feature));
        if (feature.equals(Feature.REMOVEOBJ.getValue())) {
            getBinding().tvLibraryHeader.setText(getString(R.string.label_remove_object));
        }
    }

    // -----------------------------------------------------------------------
    // initListener — exactly matches original
    // -----------------------------------------------------------------------

    @Override
    public void initListener() {
        super.initListener();

        getBinding().imgBack.setOnClickListener(v -> finish());

        getBinding().ivQuestion.setOnClickListener(v -> showQuestion(getFeature()));

        getBinding().tvMultiplePro.setOnClickListener(v -> {
            // goPremium("library") — REMOVED (no-op)
        });

        getBinding().tvCancelMultiple.setOnClickListener(v -> showItemMultiplePhotos());

        getBinding().llMultiple.setOnClickListener(v -> {
            if (Constants.MULTI_PHOTOS_NEW.isEmpty()) return;
            // Original had: if (premium == null) goPremium else find fragment and process
            // Premium removed — go straight to process
            RecyclerView.Adapter<?> adapter = getBinding().viewPager.getAdapter();
            LibraryFragment libraryFragment = null;
            LibraryPagerAdapter pagerAdapter = adapter instanceof LibraryPagerAdapter
                    ? (LibraryPagerAdapter) adapter : null;
            if (pagerAdapter != null) {
                FragmentManager fm = getSupportFragmentManager();
                libraryFragment = pagerAdapter.getFragmentAtPosition(fm, getBinding().viewPager.getCurrentItem());
            }
            if (libraryFragment != null) {
                libraryFragment.onProcessMultiPhotos();
            }
        });
    }

    // -----------------------------------------------------------------------
    // initViewPager — exactly matches original split into two lambdas
    // -----------------------------------------------------------------------

    private void initViewPager(List<AlbumModel> albumModels) {
        if (albumModels == null || albumModels.isEmpty()) return;

        final List<AlbumModel> list = new ArrayList<>(albumModels);

        if (list.size() != sizeAlbum) {
            sizeAlbum = list.size();
            System.out.println("onClickFeature__-> Updated at " + System.currentTimeMillis());
            // Matches: initViewPager$lambda$12
            runOnUiThread(() -> initViewPagerLambda12(list));
        } else {
            System.out.println("onClickFeature__-> No change, size: " + list.size());
            // Matches: initViewPager$lambda$13
            runOnUiThread(() -> initViewPagerLambda13(list));
        }
    }

    // Matches: initViewPager$lambda$12
    private void initViewPagerLambda12(final List<AlbumModel> safeAlbumModels) {
        FragmentManager fm = getSupportFragmentManager();
        String style = getStyle();
        if (style == null) style = "";

        libraryPagerViewPager = new LibraryPagerAdapter(fm, getLifecycle(), safeAlbumModels, style, getPositionImg());

        getBinding().viewPager.setOffscreenPageLimit(1);
        getBinding().viewPager.setAdapter(libraryPagerViewPager);

        // Matches: initViewPager$lambda$12$lambda$11
        getBinding().viewPager.post(() -> {
            new TabLayoutMediator(getBinding().tabLayout, getBinding().viewPager,
                    // Matches: initViewPager$lambda$12$lambda$11$lambda$10
                    (tab, i) -> {
                        AlbumModel albumModel = (i < safeAlbumModels.size()) ? safeAlbumModels.get(i) : null;
                        String name = (albumModel != null && albumModel.getName() != null)
                                ? albumModel.getName() : "Unknown";
                        tab.setText(name);
                    }).attach();
        });

        getBinding().llLoadingPhoto.setVisibility(View.GONE);
        getBinding().itemContent.setVisibility(View.VISIBLE);
    }

    // Matches: initViewPager$lambda$13
    private void initViewPagerLambda13(List<AlbumModel> safeAlbumModels) {
        getBinding().llLoadingPhoto.setVisibility(View.GONE);
        getBinding().itemContent.setVisibility(View.VISIBLE);

        RecyclerView.Adapter<?> adapter = getBinding().viewPager.getAdapter();
        LibraryFragment libraryFragment = null;
        LibraryPagerAdapter pagerAdapter = adapter instanceof LibraryPagerAdapter
                ? (LibraryPagerAdapter) adapter : null;
        if (pagerAdapter != null) {
            FragmentManager fm = getSupportFragmentManager();
            libraryFragment = pagerAdapter.getFragmentAtPosition(fm, getBinding().viewPager.getCurrentItem());
        }
        if (libraryFragment != null) {
            int currentItem = getBinding().viewPager.getCurrentItem();
            Collection<?> albumPhotos = safeAlbumModels.get(currentItem).getAlbumPhotos();
            if (albumPhotos == null) albumPhotos = new ArrayList<>();
            libraryFragment.setListAlbumPhotos(new ArrayList<>((Collection<? extends ImageModel>) albumPhotos));
        }
    }

    // -----------------------------------------------------------------------
    // Permission helpers — exactly matches original
    // -----------------------------------------------------------------------

    private void actionLayoutDenyPermission() {
        // Matches: actionLayoutDenyPermission$lambda$20
        getBinding().denyPermission.camera.setOnClickListener(v -> {
            //  getFirebaseAnalytics().logEvent("a04_Library_Request_Permission_Photo", new Bundle());
            requestPermissionLauncher.launch(Constants.arrayGallery);
            requestPermissionCamera();
        });

        // Matches: actionLayoutDenyPermission$lambda$21
        getBinding().denyPermission.btnAllow.setOnClickListener(v -> {
            //getFirebaseAnalytics().logEvent("a04_Library_Request_Permission_Photo", new Bundle());
            Log.d(TAG, "actionLayoutDenyPermission: ");
            requestPermissionLauncher.launch(Constants.arrayGallery);
        });
    }

    private void handlePermissionGranted() {
        getBinding().group.setVisibility(View.GONE);
        getAllPhotos();
    }

    // Matches: getAllPhotos (original "allPhotos" property)
    private void getAllPhotos() {
        if (!Constants.checkGalleryPermission(this)) return;
        System.out.println("onClickFeature_load_photo");

        // allPhotos$3 — main IO load that actually fetches and shows data
        new Thread(() -> {
            List<AlbumModel> albumModels = pickMediaViewModel.fetchAlbumModelsSync(this); // see Problem 2
            runOnUiThread(() -> initViewPager(albumModels));
        }).start();
    }

    public void requestPermissionCamera() {
        isRequestPermissionCamera = true;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionCameraLauncher.launch(android.Manifest.permission.CAMERA);
        } else {
            openCamera();
        }
    }

    // -----------------------------------------------------------------------
    // Camera — exactly matches original
    // -----------------------------------------------------------------------

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = createImageUri();
        this.photoUri = imageUri;
        if (imageUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION); // flags 2
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);  // flags 1
            List<ResolveInfo> resolved = getPackageManager().queryIntentActivities(intent, 65536);
            for (ResolveInfo info : resolved) {
                grantUriPermission(info.activityInfo.packageName, this.photoUri, 3);
            }
            cameraLauncher.launch(intent);
        } else {
            Log.e(TAG, "openCamera: Failed to create image URI");
        }
    }

    private Uri createImageUri() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_display_name", "IMG_" + UUID.randomUUID() + ".jpg");
        contentValues.put("mime_type", "image/jpeg");
        contentValues.put("date_added", System.currentTimeMillis() / 1000);
        contentValues.put("relative_path", "Pictures/MyApp");
        try {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } catch (Exception e) {
            Log.e(TAG, "Error creating content URI: " + e.getMessage());
            try {
                File file = new File(getCacheDir(), "camera");
                if (!file.exists()) file.mkdirs();
                return FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".provider",
                        new File(file, "IMG_" + UUID.randomUUID() + ".jpg"));
            } catch (Exception e2) {
                Log.e(TAG, "Error creating file URI: " + e2.getMessage());
                return null;
            }
        }
    }

    // -----------------------------------------------------------------------
    // Navigation — exactly matches original
    // -----------------------------------------------------------------------

    private void goToFeatureAction(String uri) {
        Log.e("CallfromStatuactivityFeature", "goToFeatureAction: ");
        Log.e(TAG, "4");
        String feature = getFeature();
        boolean isCartoon = feature.equals(Feature.AI_HUGGING.getValue())
                || feature.equals(Feature.AI_FILTER.getValue())
                || feature.equals(Feature.HEADSHOT.getValue())
                || feature.equals(Feature.HAIR_STYLE.getValue());

        if (isCartoon) {
            startCartoonActivity(new PhotoLibrary(uri));
        } else {
            Constants.startActivityFeature(this, getFeature(), uri, 0, 0, false);
        }
        // Matches: goToFeatureAction$lambda$22 (IO thread)
        new Thread(() -> GalleryUtils.INSTANCE.getAllPhotosBackground(this)).start();
    }

    private void startCartoonActivity(PhotoLibrary photo) {
        Intent intent = new Intent(this, CropImageActivity.class);
        intent.putExtra("image_uri", photo.getUri());
        intent.putExtra("image_width", photo.getWidth());
        intent.putExtra("image_height", photo.getHeight());
        intent.putExtra("feature", getFeature());
        intent.putExtra("style", getStyle());
        intent.putExtra("position_img", getPositionImg());
        startActivity(intent);
    }

    private void gotoSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }

    // -----------------------------------------------------------------------
    // Multiple photo selection — exactly matches original
    // -----------------------------------------------------------------------

    public void onSelectMultiPhotos() {
        getBinding().tvEditInBulk.setVisibility(View.GONE);
        getBinding().tvMultipleImport.setText(String.valueOf(Constants.MULTI_PHOTOS_NEW.size()));

        ChooseMultiplePhotosAdapter existingAdapter = this.chooseMultiplePhotosAdapter;
        if (existingAdapter == null) {
            this.chooseMultiplePhotosAdapter = new ChooseMultiplePhotosAdapter(Constants.MULTI_PHOTOS_NEW);
            getBinding().rvChoosePhotos.setAdapter(this.chooseMultiplePhotosAdapter);
            getBinding().rvChoosePhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        } else {
            existingAdapter.setPhotos(Constants.MULTI_PHOTOS_NEW);
        }

        ChooseMultiplePhotosAdapter finalAdapter = this.chooseMultiplePhotosAdapter;
        // Matches: onSelectMultiPhotos$lambda$23
        finalAdapter.setDeletePhotoListener(imageModel -> {
            Constants.MULTI_PHOTOS_NEW.remove(imageModel);
            finalAdapter.setPhotos(Constants.MULTI_PHOTOS_NEW);

            RecyclerView.Adapter<?> rvAdapter = getBinding().viewPager.getAdapter();
            LibraryFragment libraryFragment = null;
            LibraryPagerAdapter pagerAdapter = rvAdapter instanceof LibraryPagerAdapter
                    ? (LibraryPagerAdapter) rvAdapter : null;
            if (pagerAdapter != null) {
                FragmentManager fm = getSupportFragmentManager();
                libraryFragment = pagerAdapter.getFragmentAtPosition(fm, getBinding().viewPager.getCurrentItem());
            }
            if (libraryFragment != null) {
                libraryFragment.updateRemovePhoto(imageModel);
            }
            getBinding().tvMultipleImport.setText(String.valueOf(Constants.MULTI_PHOTOS_NEW.size()));
        });

        getBinding().rvChoosePhotos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (llm != null) {
                    firstVisiblePosition = llm.findFirstCompletelyVisibleItemPosition();
                    System.out.println("findFirstCompletelyVisibleItemPosition" + firstVisiblePosition);
                }
            }
        });

        System.out.println("findFirstCompletelyVisibleItemPosition" + firstVisiblePosition);
        getBinding().rvChoosePhotos.smoothScrollToPosition(Constants.MULTI_PHOTOS_NEW.size());
        smoothScrollToPositionWithTime(getBinding().rvChoosePhotos, firstVisiblePosition, Constants.MULTI_PHOTOS_NEW.size(), 2500);
    }

    // Matches: onProcessMultiPhotos — premium bg removed, always use normal bg
    private void onProcessMultiPhotos() {
        System.out.println("onProcessMultiPhotos");
        isMultiple = !isMultiple;
        showMultiplePhoto();

        // Original: bg_button_multi_enhancing_pro if (isMultiple && premium != null) — REMOVED
        getBinding().itemMultiple.setBackgroundResource(R.drawable.bg_button_multi_enhancing);

        Iterator<ImageModel> it = Constants.MULTI_PHOTOS_NEW.iterator();
        while (it.hasNext()) {
            LibraryFragment.Companion.onSelectOnePhoto(this, it.next());
        }

        Intent intent = new Intent(this, ActivityEnhance.class);
        intent.putExtra("feature", getFeature());
        intent.putExtra("position", getPositionImg());
        startActivity(intent);
    }

    // Matches: showItemMultiplePhotos
    private void showItemMultiplePhotos() {
        clearMultiPhotos();
        isMultiple = !isMultiple;
        showMultiplePhoto();

        // Premium bg removed — always normal
        getBinding().itemMultiple.setBackgroundResource(R.drawable.bg_button_multi_enhancing);

        LibraryPagerAdapter pager = libraryPagerViewPager;
        if ((pager == null || pager.getItemCount() != 0) && getBinding().viewPager.getCurrentItem() == 0) {
            RecyclerView.Adapter<?> rvAdapter = getBinding().viewPager.getAdapter();
            LibraryFragment libraryFragment = null;
            LibraryPagerAdapter pagerAdapter = rvAdapter instanceof LibraryPagerAdapter
                    ? (LibraryPagerAdapter) rvAdapter : null;
            if (pagerAdapter != null) {
                FragmentManager fm = getSupportFragmentManager();
                libraryFragment = pagerAdapter.getFragmentAtPosition(fm, getBinding().viewPager.getCurrentItem());
            }
            if (libraryFragment == null || !libraryFragment.isAdded()) return;
            libraryFragment.updateLibsPhotoDemo(isMultiple);
        }
    }

    // Matches: showMultiplePhoto — premium checks removed
    private void showMultiplePhoto() {
        // Original: if (premium != null) show/hide tvEditInBulk — REMOVED (always gone)
        Animation loadAnimation = AnimationUtils.loadAnimation(this,
                isMultiple ? R.anim.bottom_to_top : R.anim.top_to_bottom);

        if (isMultiple) {
            getBinding().itemMultiplePhoto.setVisibility(View.VISIBLE);
            getBinding().itemMultiple.setVisibility(View.VISIBLE); // always visible, no premium gate
        }
        if (!isMultiple) {
            getBinding().fakeItemMultiplePhoto.setVisibility(View.GONE);
        }

        // Matches: LibraryVer2Activity$showMultiplePhoto$1
        loadAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!isMultiple) {
                    getBinding().itemMultiplePhoto.setVisibility(View.GONE);
                    getBinding().itemMultiple.setVisibility(View.GONE);
                } else {
                    // Matches: onResume$lambda$19 logic
                    getBinding().itemMultiplePhoto.post(() -> {
                        heightItemMultiplePhoto = getBinding().itemMultiplePhoto.getHeight();
                        getBinding().fakeItemMultiplePhoto.getLayoutParams().height = heightItemMultiplePhoto;
                        getBinding().fakeItemMultiplePhoto.setVisibility(View.VISIBLE);
                        getBinding().itemContent.invalidate();
                        getBinding().itemContent.requestLayout();
                    });
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        getBinding().bottomMultiplePhoto.startAnimation(loadAnimation);
    }

    // Matches: showQuestion
    private void showQuestion(String feature) {
        if (bottomSheetRecommend != null && bottomSheetRecommend.isShowing()) return;
        bottomSheetRecommend = new BottomSheetRecommend(this, feature);
        bottomSheetRecommend.show();
    }

    // Matches: showRecommend — SharePreferenceRepositoryImpl removed
    private void showRecommend(String feature) {
        showQuestion(feature);
    }

    // Matches: clearMultiPhotos — exactly as original
    private void clearMultiPhotos() {
        if (Constants.MULTI_PHOTOS_NEW.isEmpty()) return;
        Iterator<ImageModel> it = Constants.MULTI_PHOTOS_NEW.iterator();
        while (it.hasNext()) {
            it.next().setSelectNumber(0);
        }
        Constants.MULTI_PHOTOS_NEW.clear();
    }

    // Matches: smoothScrollToPositionWithTime — exactly as original
    private void smoothScrollToPositionWithTime(RecyclerView recyclerView, int fromPosition, int toPosition, int duration) {
        recyclerView.scrollToPosition(fromPosition);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) return;
        CustomSmoothScroller scroller = new CustomSmoothScroller(this,
                (float) (duration / Math.abs(toPosition - fromPosition)));
        scroller.setTargetPosition(toPosition);
        layoutManager.startSmoothScroll(scroller);
    }
}