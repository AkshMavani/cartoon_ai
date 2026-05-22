package com.skylock.ai_cartoon.enhance;

/**
 * Interface for processing callbacks
 */
public interface Processing {
    /**
     * Called when dialog should be dismissed
     */
    void onDismissDialog();

    /**
     * Called when dialog should be dismissed with a parameter
     */
    void onDismissDialog(Object result);

    /**
     * Called when processing starts
     */
    default void onProcessingStart() {
    }

    /**
     * Called when processing completes
     */
    default void onProcessingComplete() {
    }

    /**
     * Called when processing fails
     */
    default void onProcessingError(Exception error) {
    }
}