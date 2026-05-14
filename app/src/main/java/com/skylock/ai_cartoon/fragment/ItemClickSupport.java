package com.skylock.ai_cartoon.fragment;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.skylock.ai_cartoon.R;

public class ItemClickSupport {
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private final RecyclerView mRecyclerView;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() { // from class: mobi.zeezoo.photoenhancer.utils.ItemClickSupport.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (ItemClickSupport.this.mOnItemClickListener != null) {
                ItemClickSupport.this.mOnItemClickListener.onItemClicked(ItemClickSupport.this.mRecyclerView, ItemClickSupport.this.mRecyclerView.getChildViewHolder(view).getAdapterPosition(), view);
            }
        }
    };
    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() { // from class: mobi.zeezoo.photoenhancer.utils.ItemClickSupport.2
        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View view) {
            if (ItemClickSupport.this.mOnItemLongClickListener == null) {
                return false;
            }
            return ItemClickSupport.this.mOnItemLongClickListener.onItemLongClicked(ItemClickSupport.this.mRecyclerView, ItemClickSupport.this.mRecyclerView.getChildViewHolder(view).getAdapterPosition(), view);
        }
    };
    private RecyclerView.OnChildAttachStateChangeListener mAttachListener = new RecyclerView.OnChildAttachStateChangeListener() { // from class: mobi.zeezoo.photoenhancer.utils.ItemClickSupport.3
        @Override // androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
        public void onChildViewDetachedFromWindow(View view) {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
        public void onChildViewAttachedToWindow(View view) {
            if (ItemClickSupport.this.mOnItemClickListener != null) {
                view.setOnClickListener(ItemClickSupport.this.mOnClickListener);
            }
            if (ItemClickSupport.this.mOnItemLongClickListener != null) {
                view.setOnLongClickListener(ItemClickSupport.this.mOnLongClickListener);
            }
        }
    };

    /* loaded from: classes3.dex */
    public interface OnItemClickListener {
        void onItemClicked(RecyclerView recyclerView, int i, View view);
    }

    /* loaded from: classes3.dex */
    public interface OnItemLongClickListener {
        boolean onItemLongClicked(RecyclerView recyclerView, int i, View view);
    }

    private ItemClickSupport(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        recyclerView.setTag(R.id.item_click_support, this);
        recyclerView.addOnChildAttachStateChangeListener(this.mAttachListener);
    }

    public static ItemClickSupport addTo(RecyclerView recyclerView) {
        ItemClickSupport itemClickSupport = (ItemClickSupport) recyclerView.getTag(R.id.item_click_support);
        return itemClickSupport == null ? new ItemClickSupport(recyclerView) : itemClickSupport;
    }

    public static ItemClickSupport removeFrom(RecyclerView recyclerView) {
        ItemClickSupport itemClickSupport = (ItemClickSupport) recyclerView.getTag(R.id.item_click_support);
        if (itemClickSupport != null) {
            itemClickSupport.detach(recyclerView);
        }
        return itemClickSupport;
    }

    public ItemClickSupport setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }

    public ItemClickSupport setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
        return this;
    }

    private void detach(RecyclerView recyclerView) {
        recyclerView.removeOnChildAttachStateChangeListener(this.mAttachListener);
        recyclerView.setTag(R.id.item_click_support, null);
    }
}