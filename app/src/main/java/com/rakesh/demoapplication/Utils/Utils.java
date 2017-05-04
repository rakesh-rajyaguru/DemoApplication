/*
 * Copyright (c) 2015 DigitasLBi. All rights reserved. (TBC)
 */
package com.rakesh.demoapplication.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Toast;

import com.rakesh.demoapplication.MainActivity;
import com.rakesh.demoapplication.R;

/**
 * Utility class for the application
 */
public class Utils {

    public static void showConnectionMessage(final MainActivity mContext, final boolean isClose) {
        try {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface
                    .OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent intent = new Intent(
                                    Settings.ACTION_WIRELESS_SETTINGS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                            dialog.dismiss();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            Toast.makeText(mContext, mContext.getResources().getString(R.string
                                    .msg_connection), Toast.LENGTH_LONG).show();
                            if (isClose) {
                                mContext.onBackPressed();
                            }
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.appCompatDialog);
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
            builder.setMessage(mContext.getResources().getString(R.string.NoInternet))
                    .setPositiveButton("Settings",
                            dialogClickListener)
                    .setNegativeButton(mContext.getResources().getString(android.R.string.cancel)
                            , dialogClickListener)
                    .setCancelable(false).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLocationMessage(final Context mContext) {
        try {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface
                    .OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                            dialog.dismiss();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    mContext, R.style.appCompatDialog);
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
            builder.setMessage(mContext.getResources().getString(R.string.msg_location))
                    .setPositiveButton("Settings",
                            dialogClickListener)
                    .setNegativeButton(mContext.getResources().getString(android.R.string.cancel)
                            , dialogClickListener)
                    .setCancelable(false).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean postLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;

    }

    public static void setPrefrence(Context mContext, String pkgname, int value) {
        SharedPreferences mshprefrence = mContext.getSharedPreferences(pkgname, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mshprefrence.edit();
        edit.putInt(pkgname, value);
        edit.apply();
    }

    public static int getPrefrence(Context mContext, String pkgname) {
        SharedPreferences mshprefrence = mContext.getSharedPreferences(pkgname, Context.MODE_PRIVATE);
        return mshprefrence.getInt(pkgname, 0);
    }

    public static float dipToPixels(final Context _context, final float _dipValue) {
        final DisplayMetrics metrics = _context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _dipValue, metrics);
    }


}



