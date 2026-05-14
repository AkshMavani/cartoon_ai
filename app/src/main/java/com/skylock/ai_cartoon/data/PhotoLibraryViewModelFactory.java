package com.skylock.ai_cartoon.data;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.skylock.ai_cartoon.model.ImageModel;

import java.util.List;

/* loaded from: classes6.dex */
public class PhotoLibraryViewModelFactory implements ViewModelProvider.Factory {
    private final List<ImageModel> allImages;

    public PhotoLibraryViewModelFactory(List<ImageModel> list) {
        this.allImages = list;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> cls) {
        if (cls.isAssignableFrom(PhotoLibraryViewModel.class)) {
            return (T) new PhotoLibraryViewModel(this.allImages);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
