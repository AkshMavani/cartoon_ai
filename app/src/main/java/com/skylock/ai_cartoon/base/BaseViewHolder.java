package com.skylock.ai_cartoon.base;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import kotlin.jvm.internal.Intrinsics;

public class BaseViewHolder<VB extends ViewBinding> extends RecyclerView.ViewHolder {

    private final VB binding;

    public BaseViewHolder(@NonNull VB binding) {
        // Pass the root view from the ViewBinding to the standard ViewHolder constructor
        super(binding.getRoot());

        // Handle explicit null-safety checking matching the original framework
        Intrinsics.checkNotNullParameter(binding, "binding");
        this.binding = binding;
    }

    public final VB getBinding() {
        return this.binding;
    }
}