package com.skylock.ai_cartoon.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object NetworkErrorDialog {
    fun show(context: Context?, errorType: ErrorType, listener: ErrorDialogListener?) {
        if (context == null) return

        AlertDialog.Builder(context)
            .setTitle(errorType.title)
            .setMessage(errorType.message)
            .setCancelable(false)
            .setPositiveButton(
                "Retry",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    dialog!!.dismiss()
                    if (listener != null) listener.onRetry()
                })
            .setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    dialog!!.dismiss()
                    if (listener != null) listener.onCancel()
                })
            .show()
    }

    enum class ErrorType(title: String, message: String) {
        NO_INTERNET(
            "No Internet Connection",
            "Please check your connection and try again."
        ),
        SERVER_TIMEOUT(
            "Server Timeout",
            "The server is taking too long to respond. Please try again."
        ),
        SERVER_ERROR(
            "Processing Failed",
            "Something went wrong on the server. Please try again."
        ),
        MAX_POLL_EXCEEDED(
            "Still Processing",
            "Your image is taking longer than expected. Please try again."
        ),
        NO_RESPONSE(
            "No Response",
            "Could not reach the server. Please check your connection."
        );

        val title: String?
        val message: String?

        init {
            this.title = title
            this.message = message
        }
    }

    interface ErrorDialogListener {
        fun onRetry()
        fun onCancel()
    }
}