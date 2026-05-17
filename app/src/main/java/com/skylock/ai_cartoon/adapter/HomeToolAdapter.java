package com.skylock.ai_cartoon.adapter;


import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;

import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.base.BaseAdapterRecyclerView;
import com.skylock.ai_cartoon.databinding.ItemHomeToolBinding;
import com.skylock.ai_cartoon.model.ToolEnhance;
import com.skylock.ai_cartoon.util.ViewExtKt;

import kotlin.jvm.internal.Intrinsics;

public final class HomeToolAdapter extends BaseAdapterRecyclerView<ToolEnhance, ItemHomeToolBinding> {

    public HomeToolAdapter() {
        // Invokes the original Kotlin default constructor signature super(null, 1, null)
        super(null, 1, null);
    }

    @Override
    protected ItemHomeToolBinding inflateBinding(LayoutInflater inflater, ViewGroup parent) {
        Intrinsics.checkNotNullParameter(inflater, "inflater");
        Intrinsics.checkNotNullParameter(parent, "parent");

        ItemHomeToolBinding inflate = ItemHomeToolBinding.inflate(inflater, parent, false);
        Intrinsics.checkNotNullExpressionValue(inflate, "inflate(...)");
        return inflate;
    }

    @Override
    protected void bindData(ItemHomeToolBinding binding, ToolEnhance item, int position) {
        Intrinsics.checkNotNullParameter(binding, "binding");
        Intrinsics.checkNotNullParameter(item, "item");

        // 1. Calculate dynamic icon size based on display metrics widthPixels / 6.7f
        ViewGroup.LayoutParams layoutParams = binding.icon.getLayoutParams();
        Intrinsics.checkNotNullExpressionValue(Resources.getSystem().getDisplayMetrics(), "getDisplayMetrics(...)");

        int calculatedWidth = (int) (Resources.getSystem().getDisplayMetrics().widthPixels / 6.7f);
        layoutParams.width = calculatedWidth;
        layoutParams.height = calculatedWidth;
        binding.icon.requestLayout();

        // 2. Map resource data from the Java model
        binding.icon.setImageResource(item.getIcon());
        binding.tvTool.setText(item.getName());

        // 3. Match position-specific visual badges exactly
        if (position == 1) {
            AppCompatImageView icHot = binding.icHot;
            Intrinsics.checkNotNullExpressionValue(icHot, "icHot");
            ViewExtKt.toVisible(icHot);

            binding.icHot.setImageResource(R.drawable.ic_supper);
            binding.icon.setBackgroundResource(R.drawable.bg_tool_hot);
            return;
        }

        if (position == 3) {
            AppCompatImageView icHot2 = binding.icHot;
            Intrinsics.checkNotNullExpressionValue(icHot2, "icHot");
            ViewExtKt.toVisible(icHot2);
        } else {
            AppCompatImageView icHot3 = binding.icHot;
            Intrinsics.checkNotNullExpressionValue(icHot3, "icHot");
            ViewExtKt.toGone(icHot3);
        }
    }
}