package com.rakesh.demoapplication.receiver;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.rakesh.demoapplication.R;

/**
 * This is the component that is responsible for actual device administration.
 * It becomes the receiver when a policy is applied. It is important that we
 * subclass DeviceAdminReceiver class here and to implement its only required
 * method onEnabled().
 */
public class DemoDeviceAdminReceiver extends DeviceAdminReceiver {
    static final String TAG = "DemoDeviceAdminReceiver";
    private static final String APP_PREF = "APP_PREF";

    /**
     * Called when this application is approved to be a device administrator.
     */
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(context, R.string.device_admin_enabled,
                Toast.LENGTH_LONG).show();
        Log.d(TAG, "onEnabled");
    }

    /**
     * Called when this application is no longer the device administrator.
     */
    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        SharedPreferences prefs = context.getSharedPreferences(APP_PREF, Activity.MODE_PRIVATE);
        prefs.edit().clear().apply();
        Toast.makeText(context, R.string.device_admin_disabled,
                Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDisabled");
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
        Log.d(TAG, "onPasswordChanged");
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
        Log.d(TAG, "onPasswordFailed");
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
        Log.d(TAG, "onPasswordSucceeded");
    }

}
