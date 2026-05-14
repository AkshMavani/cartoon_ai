package com.skylock.ai_cartoon.adapter;

import androidx.recyclerview.widget.DiffUtil;

import com.skylock.ai_cartoon.model.ImageModel;

import java.util.List;

public class ImageModelDiffCallback extends DiffUtil.Callback {
    private final List<ImageModel> newList;
    private final List<ImageModel> oldList;

    public ImageModelDiffCallback(List<ImageModel> list, List<ImageModel> list2) {
        this.oldList = list;
        this.newList = list2;
    }

    @Override // androidx.recyclerview.widget.DiffUtil.Callback
    public int getOldListSize() {
        return this.oldList.size();
    }

    @Override // androidx.recyclerview.widget.DiffUtil.Callback
    public int getNewListSize() {
        return this.newList.size();
    }

    @Override // androidx.recyclerview.widget.DiffUtil.Callback
    public boolean areItemsTheSame(int i, int i2) {
        return this.oldList.get(i).getPhotoUri().equals(this.newList.get(i2).getPhotoUri());
    }

    @Override // androidx.recyclerview.widget.DiffUtil.Callback
    public boolean areContentsTheSame(int i, int i2) {
        return this.oldList.get(i).equals(this.newList.get(i2));
    }

    @Override // androidx.recyclerview.widget.DiffUtil.Callback
    public Object getChangePayload(int i, int i2) {
        return super.getChangePayload(i, i2);
    }
}