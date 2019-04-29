package com.sanquan.indoorunit.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sanquan.indoorunit.activity.LoginActivity;
import com.sanquan.indoorunit.activity.MainActivity;

/**
 * 作者：zyf on 2019/3/19.
 * 用途：TODO
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent receive) {
        if (receive.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent intent = new Intent(context,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}