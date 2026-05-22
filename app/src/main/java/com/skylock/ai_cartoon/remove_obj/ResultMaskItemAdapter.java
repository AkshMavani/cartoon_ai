package com.skylock.ai_cartoon.remove_obj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.databinding.ItemResultMaskRemoveBinding;

import java.util.ArrayList;
import java.util.List;

public class ResultMaskItemAdapter extends RecyclerView.Adapter<ResultMaskItemAdapter.ResultMaskItemHolder> {

    private final Context context;
    private List<MaskRemove> resultItems = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public ResultMaskItemAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public List<MaskRemove> getResultItems() {
        return this.resultItems;
    }

    public void setResultItems(List<MaskRemove> list) {
        this.resultItems = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ResultMaskItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ItemResultMaskRemoveBinding binding = ItemResultMaskRemoveBinding.inflate(
                LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ResultMaskItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(ResultMaskItemHolder holder, int position) {
        MaskRemove maskRemove = resultItems.get(position);

        // Load image
        Glide.with(context)
                .asBitmap()
                .load(maskRemove.getObjUrl())
                .apply(new RequestOptions()
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                .into(holder.binding.imgItemCartoonAI);

        // Update selection background
        if (maskRemove.getSelected() && !maskRemove.isDisable()) {
            holder.binding.rootItemCartoonAI.setBackground(
                    context.getDrawable(R.drawable.bg_result_selected));
        } else {
            holder.binding.rootItemCartoonAI.setBackground(
                    context.getDrawable(R.drawable.bg_result_white_selected));
        }

        // Set alpha for disabled items
        holder.binding.rootItemCartoonAI.setAlpha(maskRemove.isDisable() ? 0.7f : 1.0f);

        // Set click listener on the item root
        holder.binding.rootItemCartoonAI.setOnClickListener(v -> {
            if (onItemClickListener != null && !maskRemove.isDisable()) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultItems.size();
    }

    public List<MaskRemove> getResultItemSelected() {
        ArrayList<MaskRemove> selected = new ArrayList<>();
        for (MaskRemove maskRemove : resultItems) {
            if (maskRemove.getSelected()) {
                selected.add(maskRemove);
            }
        }
        return selected;
    }

    public void setResultItemSelected(int id) {
        int position = -1;
        for (int i = 0; i < resultItems.size(); i++) {
            if (resultItems.get(i).getId() == id) {
                position = i;
                resultItems.get(i).setSelected(!resultItems.get(i).getSelected());
                break;
            }
        }
        if (position != -1) {
            notifyItemChanged(position);
        }
    }

    public int getItemIndexSelected() {
        for (int i = 0; i < resultItems.size(); i++) {
            if (resultItems.get(i).getSelected()) {
                return i;
            }
        }
        return 0;
    }

    public void setItemIndexSelected(int position) {
        if (position < 0 || position >= resultItems.size()) return;
        MaskRemove item = resultItems.get(position);
        item.setSelected(!item.getSelected());
        notifyItemChanged(position);
    }

    public void setAllSelectedMask() {
        for (MaskRemove maskRemove : resultItems) {
            maskRemove.setSelected(true);
        }
        notifyDataSetChanged();
    }

    public boolean isCheckTickAll() {
        int selectedCount = 0;
        for (MaskRemove maskRemove : resultItems) {
            if (maskRemove.getSelected()) {
                selectedCount++;
            }
        }
        return selectedCount == resultItems.size() && !resultItems.isEmpty();
    }

    public void unCheckTickAll() {
        for (MaskRemove maskRemove : resultItems) {
            maskRemove.setSelected(false);
        }
        notifyDataSetChanged();
    }

    public void setListDisableMaskObject() {
        for (MaskRemove maskRemove : resultItems) {
            if (maskRemove.getSelected()) {
                maskRemove.setDisable(true);
            }
        }
    }

    public String getListMasks() {
        ArrayList<MaskRemove> selectedMasks = new ArrayList<>();
        for (MaskRemove maskRemove : resultItems) {
            if (maskRemove.getSelected() && !maskRemove.isDisable()) {
                selectedMasks.add(maskRemove);
            }
        }
        return new Gson().toJson(selectedMasks);
    }

    // Interface for click events
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // ViewHolder class
    public static class ResultMaskItemHolder extends RecyclerView.ViewHolder {
        ItemResultMaskRemoveBinding binding;

        public ResultMaskItemHolder(ItemResultMaskRemoveBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}