package com.skylock.ai_cartoon.base;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

public abstract class BaseAdapterRecyclerView<T, VB extends ViewBinding> extends RecyclerView.Adapter<BaseViewHolder<VB>> {

    private VB binding;
    private List<T> dataList;
    private boolean isOnAttach;
    private Function2<? super T, ? super Integer, Unit> setOnClickItem;

    // Default Constructor
    public BaseAdapterRecyclerView(Object o, int i, Object object) {
        this.dataList = new ArrayList<>();
    }

    // Constructor mimicking Kotlin's ArrayList parameter signature
    public BaseAdapterRecyclerView(ArrayList<T> arrayList) {
        this.dataList = arrayList == null ? new ArrayList<T>() : arrayList;
    }

    protected abstract void bindData(VB binding, T item, int position);

    protected abstract VB inflateBinding(LayoutInflater inflater, ViewGroup parent);

    protected final boolean getIsOnAttach() {
        return this.isOnAttach;
    }

    protected final void setOnAttach(boolean z) {
        this.isOnAttach = z;
    }

    @NonNull
    @Override
    public BaseViewHolder<VB> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        LayoutInflater from = LayoutInflater.from(parent.getContext());
        Intrinsics.checkNotNullExpressionValue(from, "from(...)");

        this.binding = inflateBinding(from, parent);
        VB vb = this.binding;
        if (vb != null) {
            BaseViewHolder<VB> viewHolder = new BaseViewHolder<>(vb);
            bindViewClick(viewHolder, viewType);
            return viewHolder;
        }
        throw new IllegalArgumentException("Required value was null.");
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        Intrinsics.checkNotNullParameter(recyclerView, "recyclerView");
        super.onAttachedToRecyclerView(recyclerView);
        this.isOnAttach = true;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        Intrinsics.checkNotNullParameter(recyclerView, "recyclerView");
        super.onDetachedFromRecyclerView(recyclerView);
        this.isOnAttach = false;
    }

    public final void setOnClickItem(Function2<? super T, ? super Integer, Unit> listener) {
        this.setOnClickItem = listener;
    }

    public void bindViewClick(@NonNull final BaseViewHolder<VB> viewHolder, int viewType) {
        Intrinsics.checkNotNullParameter(viewHolder, "viewHolder");
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getBindingAdapterPosition();
                if (position == -1 || setOnClickItem == null) {
                    return;
                }
                T item = CollectionsKt.getOrNull(dataList, position);
                setOnClickItem.invoke(item, position);
            }
        });
    }

    public final List<T> getDataList() {
        return this.dataList;
    }

    public void setDataList(@NonNull final Collection<? extends T> data) {
        Intrinsics.checkNotNullParameter(data, "data");
        DiffUtil.DiffResult calculateDiff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return getDataList().size();
            }

            @Override // androidx.recyclerview.widget.DiffUtil.Callback
            public int getNewListSize() {
                return data.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                T oldItem = getDataList().get(oldItemPosition);
                T newItem = CollectionsKt.elementAt(data, newItemPosition);
                return BaseAdapterRecyclerView.this.areItemsTheSame(oldItem, newItem);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                T oldItem = getDataList().get(oldItemPosition);
                T newItem = CollectionsKt.elementAt(data, newItemPosition);
                return BaseAdapterRecyclerView.this.areContentsTheSame(oldItem, newItem);
            }
        });

        Intrinsics.checkNotNullExpressionValue(calculateDiff, "calculateDiff(...)");
        this.dataList.clear();
        this.dataList.addAll(data);
        calculateDiff.dispatchUpdatesTo(this);
    }

    public final void setDataList$Core_release(List<T> list) {
        Intrinsics.checkNotNullParameter(list, "<set-?>");
        this.dataList = list;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<VB> holder, int position) {
        Intrinsics.checkNotNullParameter(holder, "holder");
        bindData(holder.getBinding(), this.dataList.get(position), position);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<VB> holder, int position, @NonNull List<Object> payloads) {
        Intrinsics.checkNotNullParameter(holder, "holder");
        Intrinsics.checkNotNullParameter(payloads, "payloads");
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            bindData(holder.getBinding(), this.dataList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return this.dataList.size();
    }

    public void setData(int index, T data) {
        if (index >= this.dataList.size()) {
            return;
        }
        this.dataList.set(index, data);
        notifyItemChanged(index);
    }

    public void removeAt(int position) {
        if (position >= this.dataList.size()) {
            return;
        }
        this.dataList.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(T data) {
        int indexOf = this.dataList.indexOf(data);
        if (indexOf == -1) {
            return;
        }
        removeAt(indexOf);
    }

    public void clearData() {
        this.dataList.clear();
        notifyDataSetChanged();
    }

    protected boolean areItemsTheSame(T oldItem, T newItem) {
        return Intrinsics.areEqual(oldItem, newItem);
    }

    protected boolean areContentsTheSame(T oldItem, T newItem) {
        return Intrinsics.areEqual(oldItem, newItem);
    }

    public void addDataList(@NonNull Collection<? extends T> data) {
        Intrinsics.checkNotNullParameter(data, "data");
        int previousSize = this.dataList.size();
        this.dataList.addAll(data);
        notifyItemRangeInserted(previousSize, data.size());
    }
}