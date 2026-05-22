package com.skylock.ai_cartoon.enhance;

/**
 * Interface for loading dialog functionality
 */
public interface Loading {
    /**
     * Show the loading dialog
     */
    void show();

    /**
     * Dismiss the loading dialog
     */
    void dismiss();

    /**
     * Check if loading dialog is showing
     */
    boolean isShowing();
}