package com.skylock.ai_cartoon.base;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

public abstract class BaseFragment<VB extends ViewBinding> extends Fragment {

    private VB _binding;
    private boolean isCurrentVisible;
    private boolean mIsFirstVisible = true;

    public abstract VB inflateLayout(LayoutInflater inflater, ViewGroup container);

    public void initData() {}
    public void initView() {}
    public void initListener() {}

    protected final boolean getIsCurrentVisible() {
        return isCurrentVisible;
    }

    protected final void setCurrentVisible(boolean value) {
        this.isCurrentVisible = value;
    }

    @NonNull
    protected final VB getBinding() {
        if (_binding != null) return _binding;
        throw new IllegalStateException("ViewBinding is only valid between onCreateView and onDestroyView.");
    }

    private boolean isParentInvisible() {
        Fragment parent = getParentFragment();
        return (parent instanceof BaseFragment) && !((BaseFragment<?>) parent).isCurrentVisible;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        _binding = inflateLayout(inflater, container);
        return getBinding().getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
        initListener();
    }

    public void onBack() {
        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden() && !isCurrentVisible) {
            dispatchUserVisibleHint(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isCurrentVisible) {
            dispatchUserVisibleHint(false);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        dispatchUserVisibleHint(!hidden);
    }

    private void dispatchUserVisibleHint(boolean visible) {
        if ((visible && isParentInvisible()) || isCurrentVisible == visible) return;

        isCurrentVisible = visible;
        if (visible) {
            if (getView() == null) return;
            if (mIsFirstVisible) {
                mIsFirstVisible = false;
                onFragmentFirstVisible();
            }
            onFragmentResume();
        } else {
            if (!mIsFirstVisible) {
                onFragmentPause();
            }
        }
    }

    public void onFragmentFirstVisible() {
        initData();
        initView();
    }

    public void onFragmentResume() {}

    public void onFragmentPause() {}

    @Override
    public void onDestroyView() {
        _binding = null;
        super.onDestroyView();
    }
}