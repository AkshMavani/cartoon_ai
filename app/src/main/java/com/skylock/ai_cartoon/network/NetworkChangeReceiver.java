package com.skylock.ai_cartoon.network;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.util.Constants;


/* loaded from: classes5.dex */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private Activity activity;

    public NetworkChangeReceiver(Activity activity) {
        this.activity = activity;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (this.activity.isDestroyed()) {
            return;
        }
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this.activity, R.style.SheetDialog);
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.dismiss();
                Constants.IS_SHOW_NETWORK_DISCONNECTED = false;
            }
            Constants.IS_NETWORK_CONNECTED = true;
            return;
        }
        Constants.IS_NETWORK_CONNECTED = false;
        if (!Constants.IS_GO_HOME.booleanValue() || Constants.IS_SHOW_NETWORK_DISCONNECTED.booleanValue()) {
            return;
        }
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_network_error);
        ((TextView) bottomSheetDialog.findViewById(R.id.tvGoSetting)).setOnClickListener(new View.OnClickListener() { // from class: mobi.zeezoo.photoenhancer.network.NetworkChangeReceiver$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NetworkChangeReceiver.lambda$onReceive$0(bottomSheetDialog, view);
            }
        });
        bottomSheetDialog.getWindow().getDecorView().setSystemUiVisibility(5380);
        bottomSheetDialog.getWindow().clearFlags(8);
        // from class: mobi.zeezoo.photoenhancer.network.NetworkChangeReceiver.1
// android.content.DialogInterface.OnDismissListener
        bottomSheetDialog.setOnDismissListener(dialogInterface -> Constants.IS_SHOW_NETWORK_DISCONNECTED = false);
        if (this.activity.isFinishing()) {
            return;
        }
        bottomSheetDialog.show();
        Constants.IS_SHOW_NETWORK_DISCONNECTED = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onReceive$0(Dialog dialog, View view) {
        dialog.dismiss();
        Constants.IS_SHOW_NETWORK_DISCONNECTED = false;
    }
}
