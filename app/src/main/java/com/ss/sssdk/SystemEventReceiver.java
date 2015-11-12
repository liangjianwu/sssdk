package com.ss.sssdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ljw on 15/11/11.
 */
public class SystemEventReceiver extends BroadcastReceiver {
    @Override

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent("com.ss.sssdk.PushMessageService"));
        }
    }

}
