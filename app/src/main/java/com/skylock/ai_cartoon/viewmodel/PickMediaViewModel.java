package com.skylock.ai_cartoon.viewmodel;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.skylock.ai_cartoon.model.AlbumModel;
import com.skylock.ai_cartoon.model.ImageModel;
import com.skylock.ai_cartoon.util.GalleryUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;

public final class PickMediaViewModel extends BaseViewModel {

    public static final int $stable = 8;

    private final List<ImageModel> images = new ArrayList<>();
    private final MutableLiveData<List<ImageModel>> imagesSelected = new MutableLiveData<>();
    private final MutableLiveData<List<AlbumModel>> albumModels = new MutableLiveData<>();
    private List<String> listNameAlbums = new ArrayList<>();

    public final List<ImageModel> getImages() {
        return this.images;
    }

    public final MutableLiveData<List<ImageModel>> getImagesSelected() {
        return this.imagesSelected;
    }

    public final MutableLiveData<List<AlbumModel>> getAlbumModels() {
        return this.albumModels;
    }

    public final List<String> getListNameAlbums() {
        return this.listNameAlbums;
    }

    public final void setListNameAlbums(List<String> list) {
        Intrinsics.checkNotNullParameter(list, "<set-?>");
        this.listNameAlbums = list;
    }

    // ✅ Replaces the coroutine-based getAlbumModels()
    public void fetchAlbumModels() {
        new Thread(() -> {
            List<AlbumModel> result = loadAlbumModelsFromSource(); // your data logic here
            albumModels.postValue(result);
        }).start();
    }

    // Implement this based on your original coroutine body logic
    private List<AlbumModel> loadAlbumModelsFromSource() {
        // TODO: Add your album loading logic here
        // (MediaStore queries, repository calls, etc.)
        return new ArrayList<>();
    }

    private final void updateImage(ImageModel imageModel) {
        if (imageModel.isSelected()) {
            boolean exists = false;
            for (ImageModel item : this.images) {
                if (Intrinsics.areEqual(item.getPath(), imageModel.getPath())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                this.images.add(imageModel);
            }
        } else {
            Iterator<ImageModel> iterator = this.images.iterator();
            while (iterator.hasNext()) {
                if (Intrinsics.areEqual(iterator.next().getPath(), imageModel.getPath())) {
                    iterator.remove();
                }
            }
        }

        int i = 0;
        for (ImageModel obj : this.images) {
            int i2 = i + 1;
            obj.setPosition(i2);
            i = i2;
        }
        this.imagesSelected.postValue(this.images);
    }
    // In PickMediaViewModel.java
    public List<AlbumModel> fetchAlbumModelsSync(Activity activity) {
        // Call whatever your existing repository/GalleryUtils method is
        // For example:
        return GalleryUtils.INSTANCE.folderListFromImages(activity);
        // OR whatever method your repo exposes that returns List<AlbumModel>
    }
}














