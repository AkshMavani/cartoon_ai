package com.skylock.ai_cartoon.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.skylock.ai_cartoon.model.ImageModel;

import java.util.List;

/* loaded from: classes9.dex */
public class PhotoLibraryViewModel extends ViewModel {
    private final MutableLiveData<List<ImageModel>> imageListLiveData;
    private LiveData<PagedList<ImageModel>> pagedListLiveData;

    public PhotoLibraryViewModel(List<ImageModel> list) {
        MutableLiveData<List<ImageModel>> mutableLiveData = new MutableLiveData<>();
        this.imageListLiveData = mutableLiveData;
        mutableLiveData.setValue(list);
        createPagedList();
    }

    public void updateImages(List<ImageModel> list) {
        System.out.println("getPagedListLiveData_update" + list);
        this.imageListLiveData.setValue(list);
        createPagedList();
    }

    private void createPagedList() {
        this.pagedListLiveData = new LivePagedListBuilder(new ImageDataSourceFactory(this.imageListLiveData.getValue()), new PagedList.Config.Builder().setPageSize(20).setEnablePlaceholders(false).build()).build();
    }

    public LiveData<PagedList<ImageModel>> getPagedListLiveData() {
        return this.pagedListLiveData;
    }
}
