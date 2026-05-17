package com.skylock.ai_cartoon.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.model.AIAvatarResultItem;

import java.util.List;

/**
 * AIAvatarResultAdapter
 * <p>
 * Horizontal RecyclerView adapter that displays all generated result thumbnails
 * for the currently selected cartoon style.
 * <p>
 * Layout expected: item_ai_avatar_result.xml
 * └── root: LinearLayout or CardView (clickable)
 * ├── ImageView  (id: imgResult)   — thumbnail of the generated image
 * ├── TextView   (id: tvTitle)     — "Result 1", "Result 2", …
 * └── View       (id: viewSelected) — visible border/indicator when selected
 * <p>
 * Usage in AIAvatarResultActivity:
 * adapter = new AIAvatarResultAdapter(this, resultItems, item -> loadImageIntoPreview(item));
 * binding.rvResult.setAdapter(adapter);
 * binding.rvResult.setLayoutManager(new LinearLayoutManager(this, HORIZONTAL, false));
 */
public class AIAvatarResultAdapter
        extends RecyclerView.Adapter<AIAvatarResultAdapter.ResultViewHolder> {

    private final Context context;
    private final List<AIAvatarResultItem> items;
    private final OnItemClickListener listener;
    public AIAvatarResultAdapter(
            @NonNull Context context,
            @NonNull List<AIAvatarResultItem> items,
            @NonNull OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_ai_avatar_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        AIAvatarResultItem item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public interface OnItemClickListener {
        void onItemClick(AIAvatarResultItem item, int position);
    }

    // ── ViewHolder ─────────────────────────────────────────────────────────

    class ResultViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgResult;
        private final TextView tvTitle;
        private final View viewSelected;  // selection indicator (border / highlight)

        ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            imgResult = itemView.findViewById(R.id.imgResult);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            viewSelected = itemView.findViewById(R.id.viewSelected);
        }

        void bind(AIAvatarResultItem item, int position) {
            // Title
            if (tvTitle != null) {
                tvTitle.setText(item.getTitle());
            }

            // Thumbnail: show active URL (beautified or original)
            String displayUrl = item.getActiveUrl();
            Glide.with(context)
                    .load(displayUrl)
                    .apply(new RequestOptions().transform(new RoundedCorners(16)))
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imgResult);

            // Selection indicator
            if (viewSelected != null) {
                viewSelected.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);
            }

            // Selected item border on the card / root
            itemView.setSelected(item.isSelected());

            // Click
            itemView.setOnClickListener(v -> listener.onItemClick(item, position));
        }
    }
}
