package com.skylock.ai_cartoon.enhance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.databinding.ItemResultBinding;
import com.skylock.ai_cartoon.util.Constants;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes5.dex */
public class ResultItemAdapter extends RecyclerView.Adapter<ResultItemAdapter.ResultItemHolder> {
    private final Context context;
    private List<ResultItem> resultItems;

    public ResultItemAdapter(Context context, List<ResultItem> list) {
        new ArrayList();
        this.context = context;
        this.resultItems = list;
    }

    public ResultItemAdapter(Context context) {
        this.resultItems = new ArrayList();
        this.context = context;
    }

    public void setResultItems(List<ResultItem> list) {
        this.resultItems = list;
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ResultItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ResultItemHolder(this, ItemResultBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(final ResultItemHolder resultItemHolder, int i) {
        ResultItem resultItem = this.resultItems.get(i);
        Glide.with(this.context)
                .asBitmap()
                .load(resultItem.getUrlAfter())
                .into(new CustomTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(
                            @NonNull Bitmap bitmap,
                            @Nullable Transition<? super Bitmap> transition
                    ) {

                        resultItemHolder.binding.imgItemCartoonAI.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) resultItemHolder.binding.rootItemCartoonAI.getLayoutParams();
        int dpToPixel = Constants.dpToPixel(3.0f);
        int dpToPixel2 = Constants.dpToPixel(3.0f);
        if (i == 0 || i == this.resultItems.size() - 1) {
            int dpToPixel3 = i == 0 ? Constants.dpToPixel(6.0f) : dpToPixel;
            if (i != 0) {
                dpToPixel = Constants.dpToPixel(6.0f);
            }
            marginLayoutParams.setMargins(dpToPixel3, dpToPixel2, dpToPixel, dpToPixel2);
        } else {
            marginLayoutParams.setMargins(dpToPixel, dpToPixel2, dpToPixel, dpToPixel2);
        }
        resultItemHolder.binding.rootItemCartoonAI.requestLayout();
        if (resultItem.getSelected().booleanValue()) {
            resultItemHolder.binding.rootItemCartoonAI.setBackground(this.context.getDrawable(R.drawable.bg_result_selected));
        } else {
            resultItemHolder.binding.rootItemCartoonAI.setBackground(this.context.getDrawable(R.color.transparent));
        }
        resultItemHolder.binding.tvName.setText(resultItem.getName());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.resultItems.size();
    }

    public ResultItem getResultItem() {
        for (ResultItem resultItem : this.resultItems) {
            if (resultItem.getSelected().booleanValue()) {
                return resultItem;
            }
        }
        return null;
    }

    public int getItemIndexSelected() {
        for (ResultItem resultItem : this.resultItems) {
            if (resultItem.getSelected().booleanValue()) {
                return this.resultItems.indexOf(resultItem);
            }
        }
        return 0;
    }

    /* loaded from: classes5.dex */
    public class ResultItemHolder extends RecyclerView.ViewHolder {
        ItemResultBinding binding;

        public ResultItemHolder(ResultItemAdapter resultItemAdapter, ItemResultBinding itemResultBinding) {
            super(itemResultBinding.getRoot());
            this.binding = itemResultBinding;
        }
    }
}
