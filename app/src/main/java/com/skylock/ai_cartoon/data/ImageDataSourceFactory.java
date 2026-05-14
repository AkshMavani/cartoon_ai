package com.skylock.ai_cartoon.data;


import androidx.paging.DataSource;

import com.skylock.ai_cartoon.model.ImageModel;

import java.util.List;

/* loaded from: classes8.dex */
public class ImageDataSourceFactory extends DataSource.Factory<Integer, ImageModel> {
    private final List<ImageModel> allImages;

    public ImageDataSourceFactory(List<ImageModel> list) {
        this.allImages = list;
    }

    @Override // androidx.paging.DataSource.Factory
    public DataSource<Integer, ImageModel> create() {
        return new ImageDataSource(this.allImages);
    }
}
