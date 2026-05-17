package com.skylock.ai_cartoon.util;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.callback.ProcessingListener;
import com.skylock.ai_cartoon.databinding.DialogErrorProcessingBinding;
import com.skylock.ai_cartoon.viewmodel.CartoonViewModel.ErrorEvent;

/**
 * ErrorProcessingDialog — Production-ready dialog for all error cases.
 * <p>
 * Usage from Activity:
 * <p>
 * ErrorProcessingDialog.display(
 * getSupportFragmentManager(),
 * ErrorEvent.NO_INTERNET,   // drives the title + message shown
 * new ProcessingListener() {
 * public void onRetry()  { viewModel.retryFromScratch(); progress = 0; autoProcess(); }
 * public void onCancel() { finish(); }
 * }
 * );
 * <p>
 * The dialog is non-cancelable: the user MUST tap Retry or Cancel.
 */
public class ErrorProcessingDialog extends DialogFragment {

    private static final String TAG = "ErrorProcessingDialog";
    private static final String ARG_TYPE = "error_type";

    private DialogErrorProcessingBinding binding;
    private ProcessingListener listener;

    // ── Factory ───────────────────────────────────────────────────────────────

    public static ErrorProcessingDialog display(
            @NonNull FragmentManager fm,
            @NonNull ErrorEvent errorEvent,
            @NonNull ProcessingListener listener) {

        // Avoid duplicate dialogs
        if (fm.findFragmentByTag(TAG) != null) return null;

        ErrorProcessingDialog dialog = new ErrorProcessingDialog();
        dialog.listener = listener;

        Bundle args = new Bundle();
        args.putString(ARG_TYPE, errorEvent.name());
        dialog.setArguments(args);

        dialog.show(fm, TAG);
        return dialog;
    }

    /**
     * Legacy overload — kept for backward compatibility.
     * Shows a generic "something went wrong" dialog.
     */
    public static ErrorProcessingDialog display(
            @NonNull FragmentManager fm,
            @NonNull ProcessingListener listener) {
        return display(fm, ErrorEvent.SERVER_ERROR, listener);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DialogErrorProcessingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false); // User MUST act — no dismiss by tapping outside

        // Resolve which error type we are showing
        ErrorEvent errorEvent = ErrorEvent.SERVER_ERROR;
        if (getArguments() != null) {
            try {
                errorEvent = ErrorEvent.valueOf(
                        getArguments().getString(ARG_TYPE, ErrorEvent.SERVER_ERROR.name()));
            } catch (IllegalArgumentException ignored) { /* fall through to default */ }
        }

        applyErrorContent(errorEvent);
        setupButtons();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            // Full-width card with transparent background handled by CardView
            dialog.getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Content ───────────────────────────────────────────────────────────────

    /**
     * Map each ErrorEvent to the correct title + message strings.
     * Add or adjust cases to match your strings.xml entries.
     */
    private void applyErrorContent(ErrorEvent event) {
        String title;
        String message;

        switch (event) {
            case NO_INTERNET:
                title = getString(R.string.dialog_title_no_internet);
                // "No Internet Connection"
                message = getString(R.string.dialog_msg_no_internet);
                // "Please check your connection and try again."
                break;

            case SERVER_TIMEOUT:
                title = getString(R.string.dialog_title_timeout);
                // "Request Timed Out"
                message = getString(R.string.dialog_msg_timeout);
                // "The server took too long to respond. Please try again."
                break;

            case MAX_POLL_EXCEEDED:
                title = getString(R.string.dialog_title_taking_long);
                // "Still Working…"
                message = getString(R.string.dialog_msg_taking_long);
                // "Your cartoon is taking longer than expected. Retry to check again."
                break;

            case NO_RESPONSE:
                title = getString(R.string.dialog_title_server_error);
                // "Server Error"
                message = getString(R.string.dialog_msg_no_response);
                // "The server didn't send a valid response. Please try again."
                break;

            case SERVER_ERROR:
            default:
                title = getString(R.string.label_sorry_an_error_occurred);
                message = getString(R.string.label_content_sorry_an_error_occurred);
                break;
        }

        binding.tvDialogTitle.setText(title);
        binding.tvDialogMessage.setText(message);
    }

    private void setupButtons() {
        binding.tvTryAgain.setOnClickListener(v -> {
            dismissSafely();
            if (listener != null) listener.onRetry();
        });

        binding.tvOk.setOnClickListener(v -> {
            dismissSafely();
            if (listener != null) listener.onCancel();
        });
    }

    private void dismissSafely() {
        try {
            if (isAdded()) dismissAllowingStateLoss();
        } catch (Exception e) {
            // Fragment already detached — safe to swallow
        }
    }
}