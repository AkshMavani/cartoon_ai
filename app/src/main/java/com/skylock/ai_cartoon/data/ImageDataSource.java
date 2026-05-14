package com.skylock.ai_cartoon.data;

import androidx.paging.PageKeyedDataSource;


import com.skylock.ai_cartoon.model.ImageModel;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes5.dex */
public class ImageDataSource extends PageKeyedDataSource<Integer, ImageModel> {
    private final List<ImageModel> allImages;

    @Override // androidx.paging.PageKeyedDataSource
    public void loadBefore(PageKeyedDataSource.LoadParams<Integer> loadParams, PageKeyedDataSource.LoadCallback<Integer, ImageModel> loadCallback) {
    }

    public ImageDataSource(List<ImageModel> list) {
        this.allImages = new ArrayList(list);
    }

    @Override // androidx.paging.PageKeyedDataSource
    public void loadInitial(PageKeyedDataSource.LoadInitialParams<Integer> loadInitialParams, PageKeyedDataSource.LoadInitialCallback<Integer, ImageModel> loadInitialCallback) {
        int min = Math.min(this.allImages.size(), loadInitialParams.requestedLoadSize);
        if (min > 0) {
            loadInitialCallback.onResult(this.allImages.subList(0, min), null, 1);
        }
    }

    @Override // androidx.paging.PageKeyedDataSource
    public void loadAfter(PageKeyedDataSource.LoadParams<Integer> loadParams, PageKeyedDataSource.LoadCallback<Integer, ImageModel> loadCallback) {
        int intValue = loadParams.key.intValue();
        int i = loadParams.requestedLoadSize * intValue;
        int min = Math.min(loadParams.requestedLoadSize + i, this.allImages.size());
        if (i < min) {
            loadCallback.onResult(this.allImages.subList(i, min), Integer.valueOf(intValue + 1));
        }
    }
}
