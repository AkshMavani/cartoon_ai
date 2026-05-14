package com.skylock.ai_cartoon.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.skylock.ai_cartoon.databinding.ItemAddPhotoLibraryBinding;
import com.skylock.ai_cartoon.model.ImageModel;
import com.skylock.ai_cartoon.util.Constants;
import com.skylock.ai_cartoon.util.ExtensionKt;

import java.util.ArrayList;
import java.util.List;



public final class AddPhotoLibraryNewAdapter extends RecyclerView.Adapter<AddPhotoLibraryNewAdapter.PhotoLibraryHolder> {

    public interface OnRemovePhoto {
        void onRemove(int pos);
    }

    private final String from;
    private OnRemovePhoto onRemovePhoto;
    private final List<ImageModel> photos = new ArrayList<>();

    public AddPhotoLibraryNewAdapter(String from) {
        this.from = from;
    }

    public void setOnRemovePhoto(OnRemovePhoto onRemovePhoto) {
        this.onRemovePhoto = onRemovePhoto;
    }

    public List<ImageModel> getPhotos() {
        return photos;
    }

    public void setPhotos(List<ImageModel> newPhotos) {
        if (newPhotos == null) return;
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new ImageModelDiffCallback(photos, newPhotos));
        photos.clear();
        photos.addAll(newPhotos);
        result.dispatchUpdatesTo(this);
    }

    @Override
    public PhotoLibraryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemAddPhotoLibraryBinding binding = ItemAddPhotoLibraryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new PhotoLibraryHolder(binding);
    }

    @Override
    public void onBindViewHolder(PhotoLibraryHolder holder, int position) {
        ImageModel imageModel = photos.get(position);
        bindLayoutParams(holder, imageModel);
        bindImageContent(holder, imageModel);
        bindCloseButton(holder, position);
    }

    private void bindLayoutParams(PhotoLibraryHolder holder, ImageModel imageModel) {
        ViewGroup.LayoutParams rootParams = holder.binding.getRoot().getLayoutParams();

        if ("preview".equals(from)) {
            rootParams.width = Constants.dpToPixel(60.0f);
            rootParams.height = Constants.dpToPixel(60.0f);

            ViewGroup.LayoutParams cvParams = holder.binding.cvItemPhotoLibrary2.getLayoutParams();
            if (cvParams instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams frameParams = (FrameLayout.LayoutParams) cvParams;
                Integer selectNumber = imageModel.getSelectNumber();
                if (selectNumber != null && selectNumber == 1) {
                    frameParams.setMargins(5, 5, 5, 5);
                } else {
                    frameParams.setMargins(0, 0, 0, 0);
                }
            }

            int closeVisibility = Constants.MULTI_PHOTOS_NEW.size() <= 1 ? View.GONE : View.VISIBLE;
            holder.binding.btnClose.setVisibility(closeVisibility);
        } else {
            ViewGroup.LayoutParams cvParams = holder.binding.cvItemPhotoLibrary.getLayoutParams();
            if (cvParams instanceof FrameLayout.LayoutParams) {
                int margin = Constants.dpToPixel(4.0f);
                ((FrameLayout.LayoutParams) cvParams).setMargins(margin, margin, margin, margin);
            }
        }

        holder.binding.getRoot().requestLayout();
    }

    private void bindImageContent(PhotoLibraryHolder holder, ImageModel imageModel) {
        holder.binding.demo.setVisibility(
                imageModel.getUriDemoPreview() == null ? View.GONE : View.VISIBLE
        );

        String imageUri = imageModel.getUriDemoPreview() != null
                ? imageModel.getUriDemoPreview()
                : (imageModel.getPhotoUri() != null ? imageModel.getPhotoUri() : "");

        ExtensionKt.loadImage(holder.binding.imgItemPhotoLibrary, imageUri);

        holder.binding.imgItemPhotoLibrary.setVisibility(imageModel.isAddPhoto() ? View.GONE : View.VISIBLE);
        holder.binding.itemAddPhoto.setVisibility(imageModel.isAddPhoto() ? View.VISIBLE : View.GONE);
    }

    private void bindCloseButton(PhotoLibraryHolder holder, int position) {
        holder.binding.btnClose.setOnClickListener(v -> {
            if (onRemovePhoto != null) {
                onRemovePhoto.onRemove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static final class PhotoLibraryHolder extends RecyclerView.ViewHolder {
        private final ItemAddPhotoLibraryBinding binding;

        public PhotoLibraryHolder(ItemAddPhotoLibraryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ItemAddPhotoLibraryBinding getBinding() {
            return binding;
        }
    }

    private static final class ImageModelDiffCallback extends DiffUtil.Callback {
        private final List<ImageModel> oldList;
        private final List<ImageModel> newList;

        ImageModelDiffCallback(List<ImageModel> oldList, List<ImageModel> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            ImageModel oldItem = oldList.get(oldPos);
            ImageModel newItem = newList.get(newPos);
            if (oldItem.getId() != null && newItem.getId() != null) {
                return oldItem.getId().equals(newItem.getId());
            }
            String oldUri = oldItem.getPhotoUri();
            String newUri = newItem.getPhotoUri();
            if (oldUri != null && newUri != null) {
                return oldUri.equals(newUri);
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            ImageModel oldItem = oldList.get(oldPos);
            ImageModel newItem = newList.get(newPos);
            return oldItem.isSelected() == newItem.isSelected()
                    && oldItem.isAddPhoto() == newItem.isAddPhoto()
                    && oldItem.isCamera() == newItem.isCamera()
                    && java.util.Objects.equals(oldItem.getSelectNumber(), newItem.getSelectNumber())
                    && java.util.Objects.equals(oldItem.getUriDemoPreview(), newItem.getUriDemoPreview());
        }
    }
}