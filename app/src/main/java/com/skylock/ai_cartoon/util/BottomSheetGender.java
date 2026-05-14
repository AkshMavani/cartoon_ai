package com.skylock.ai_cartoon.util;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;

import com.skylock.ai_cartoon.databinding.BottomSheetChooseGenderBinding;
import com.skylock.ai_cartoon.model.Gender;

public final class BottomSheetGender extends BaseBottomSheetDialog<BottomSheetChooseGenderBinding> {

    private OnGenderClickListener onGenderClickListener;
    private long lastClickTime = 0;

    public interface OnGenderClickListener {
        void onGenderClick(Gender gender);
    }

    public OnGenderClickListener getOnGenderClickListener() {
        return onGenderClickListener;
    }

    public void setOnGenderClickListener(OnGenderClickListener listener) {
        this.onGenderClickListener = listener;
    }

    @Override
    protected BottomSheetChooseGenderBinding inflateBinding(LayoutInflater layoutInflater) {
        return BottomSheetChooseGenderBinding.inflate(layoutInflater);
    }

    @Override
    protected void initData() {}

    @Override
    protected void initView() {
        setCancelable(false);
    }

    @Override
    protected void initListener() {
        getBinding().btnFemale.setOnClickListener(v -> onGenderSelected(Gender.FEMALE));
        getBinding().btnMale.setOnClickListener(v -> onGenderSelected(Gender.MALE));
        getBinding().btnOther.setOnClickListener(v -> onGenderSelected(Gender.OTHER));
    }

    private void onGenderSelected(Gender gender) {
        if (!isSafeClick()) return;
        saveGenderToPrefs(gender);
        if (onGenderClickListener != null) {
            onGenderClickListener.onGenderClick(gender);
        }
        dismiss();
    }

    private void saveGenderToPrefs(Gender gender) {
        if (getContext() == null) return;
        /*getContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
                .edit()
                .putString("selected_gender", gender.name())
                .apply();*/
        Log.e("Sharepref", "saveGenderToPrefs: "+gender.name());
        SharePrefUtils.setGenderString("selected_gender",gender.name());
    }

    private boolean isSafeClick() {
        long now = SystemClock.elapsedRealtime();
        if (now - lastClickTime < 600) return false;
        lastClickTime = now;
        return true;
    }
}