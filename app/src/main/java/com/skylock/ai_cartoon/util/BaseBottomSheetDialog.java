package com.skylock.ai_cartoon.util;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.skylock.ai_cartoon.R;

public abstract class BaseBottomSheetDialog<VB extends ViewBinding> extends BottomSheetDialogFragment {

    private VB binding;

    protected abstract VB inflateBinding(LayoutInflater inflater);

    protected abstract void initData();

    protected abstract void initView();

    protected abstract void initListener();

    @NonNull
    public final VB getBinding() {
        return binding;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use your existing SheetDialog style for consistent UI
        setStyle(STYLE_NORMAL, R.style.SheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = inflateBinding(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
        initListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}