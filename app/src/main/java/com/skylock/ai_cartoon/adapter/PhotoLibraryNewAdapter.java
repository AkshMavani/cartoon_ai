package com.skylock.ai_cartoon.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.skylock.ai_cartoon.databinding.ItemPhotoLibraryBinding;
import com.skylock.ai_cartoon.model.ImageModel;
import com.skylock.ai_cartoon.util.Constants;

import java.util.ArrayList;
import java.util.List;


public class PhotoLibraryNewAdapter extends RecyclerView.Adapter<PhotoLibraryNewAdapter.PhotoLibraryHolder> {

    private String from;
    private OnRemovePhoto onRemovePhoto;
    private final List<ImageModel> photos = new ArrayList<>();

    public interface OnRemovePhoto {
        void onRemove(int position);
    }

    public PhotoLibraryNewAdapter(String from) {
        this.from = from;
    }

    public List<ImageModel> getPhotos() {
        return this.photos;
    }

    @Override
    public PhotoLibraryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemPhotoLibraryBinding binding = ItemPhotoLibraryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new PhotoLibraryHolder(binding);
    }

    @Override
    public void onBindViewHolder(PhotoLibraryHolder holder, final int position) {
        ImageModel  imageModel = this.photos.get(position);
        ViewGroup.LayoutParams layoutParams = holder.binding.getRoot().getLayoutParams();

        if (this.from.equals("preview")) {
            layoutParams.width = Constants.dpToPixel(60.0f);
            layoutParams.height = Constants.dpToPixel(60.0f);
            holder.binding.btnClose.setVisibility(View.VISIBLE);

            FrameLayout.LayoutParams cvParams =
                    (FrameLayout.LayoutParams) holder.binding.cvItemPhotoLibrary2.getLayoutParams();

            if (imageModel.getSelectNumber().intValue() == 1) {
                cvParams.setMargins(5, 5, 5, 5);
            } else {
                cvParams.setMargins(0, 0, 0, 0);
            }

            holder.binding.btnClose.setVisibility(
                    Constants.MULTI_PHOTOS_NEW.size() <= 1 ? View.GONE : View.VISIBLE
            );

        } else {
            FrameLayout.LayoutParams cvParams =
                    (FrameLayout.LayoutParams) holder.binding.cvItemPhotoLibrary.getLayoutParams();
            int margin = Constants.dpToPixel(4.0f);
            cvParams.setMargins(margin, margin, margin, margin);
        }

        holder.binding.getRoot().requestLayout();

        // Demo preview visibility
        holder.binding.demo.setVisibility(
                imageModel.getUriDemoPreview() == null ? View.GONE : View.VISIBLE
        );

        Log.i("File", " library uri photo.getUriDemoPreview(): " + imageModel.getUriDemoPreview());
        Log.i("File", " library uri photo.getPhotoUri(): " + imageModel.getPhotoUri());

        // Load image with Glide
        Glide.with(holder.binding.getRoot().getContext())
                .load(imageModel.getUriDemoPreview() != null
                        ? imageModel.getUriDemoPreview()
                        : imageModel.getPhotoUri())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.imgItemPhotoLibrary);

        // Camera item visibility
        holder.binding.imgItemPhotoLibrary.setVisibility(imageModel.isCamera() ? View.GONE : View.VISIBLE);
        holder.binding.itemCamera.setVisibility(imageModel.isCamera() ? View.VISIBLE : View.GONE);

        // Remove button click listener
        holder.binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRemovePhoto != null) {
                    onRemovePhoto.onRemove(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.photos.size();
    }

    public void setPhotos(List<ImageModel> newPhotos) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new ImageModelDiffCallback(this.photos, newPhotos)
        );
        this.photos.clear();
        this.photos.addAll(newPhotos);
        diffResult.dispatchUpdatesTo(this);
    }

    public void setOnRemovePhoto(OnRemovePhoto onRemovePhoto) {
        this.onRemovePhoto = onRemovePhoto;
    }

    public static class PhotoLibraryHolder extends RecyclerView.ViewHolder {
        ItemPhotoLibraryBinding binding;

        public PhotoLibraryHolder(ItemPhotoLibraryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}