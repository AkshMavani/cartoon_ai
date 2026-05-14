package com.skylock.ai_cartoon.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.skylock.ai_cartoon.databinding.ItemChooseMultiplePhotosBinding;
import com.skylock.ai_cartoon.model.ImageModel;
import com.skylock.ai_cartoon.util.EmptyUtils;

import java.util.List;

public class ChooseMultiplePhotosAdapter extends RecyclerView.Adapter<ChooseMultiplePhotosAdapter.ChooseMultiplePhotos> {
    private DeletePhotoListener deletePhotoListener;
    private List<ImageModel> list;

    /* loaded from: classes2.dex */
    public interface DeletePhotoListener {
        void onRemove(ImageModel imageModel);
    }

    public ChooseMultiplePhotosAdapter(List<ImageModel> list) {
        this.list = list;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ChooseMultiplePhotos onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ChooseMultiplePhotos(ItemChooseMultiplePhotosBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ChooseMultiplePhotos holder, int position) {
        final ImageModel imageModel = list.get(position);
        Context context = holder.binding.getRoot().getContext();

        Integer icon = imageModel.getIcon();
        if (icon != null && icon != 0) {
            Glide.with(context)
                    .load(icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.imgItemPhotoLibrary);
        } else {
            Glide.with(context)
                    .load(imageModel.getPhotoUri())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.imgItemPhotoLibrary);
        }

        holder.binding.itemDelete.setOnClickListener(v -> {
            if (deletePhotoListener != null) {
                deletePhotoListener.onRemove(imageModel);
            }
        });
    }


    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        if (EmptyUtils.isNotEmpty(this.list)) {
            return this.list.size();
        }
        return 0;
    }

    public void setPhotos(List<ImageModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    /* loaded from: classes2.dex */
    public static class ChooseMultiplePhotos extends RecyclerView.ViewHolder {
        ItemChooseMultiplePhotosBinding binding;

        public ChooseMultiplePhotos(ItemChooseMultiplePhotosBinding itemChooseMultiplePhotosBinding) {
            super(itemChooseMultiplePhotosBinding.getRoot());
            this.binding = itemChooseMultiplePhotosBinding;
            ViewGroup.LayoutParams layoutParams = itemChooseMultiplePhotosBinding.root.getLayoutParams();
            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            layoutParams.width = (int) (displayMetrics.widthPixels / 3.5f);
            layoutParams.height = (int) (displayMetrics.widthPixels / 3.5f);
            itemChooseMultiplePhotosBinding.root.requestLayout();
        }
    }

    public void setDeletePhotoListener(DeletePhotoListener deletePhotoListener) {
        this.deletePhotoListener = deletePhotoListener;
    }
}
