/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rakesh.demoapplication.adminpolicy;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.rakesh.demoapplication.receiver.DemoDeviceAdminReceiver;

public class Policy {
    public static final int REQUEST_ADD_DEVICE_ADMIN = 1;
    // Password quality values.  This list must match the list
    // found in res/values/arrays.xml
    final static int[] PASSWORD_QUALITY_VALUES = new int[] {
        DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED,
        DevicePolicyManager.PASSWORD_QUALITY_SOMETHING,
        DevicePolicyManager.PASSWORD_QUALITY_NUMERIC,
        DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC,
        DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC,
        DevicePolicyManager.PASSWORD_QUALITY_COMPLEX
    };
    private static final String APP_PREF = "APP_PREF";
    private static final String KEY_PASSWORD_LENGTH = "PW_LENGTH";
    private static final String KEY_PASSWORD_QUALITY = "PW_QUALITY";
    private static final String KEY_PASSWORD_MIN_UPPERCASE = "PW_MIN_UPPERCASE";
    private int mPasswordQuality;
    private int mPasswordLength;
    private int mPasswordMinUpperCase;
    private Context mContext;
    private DevicePolicyManager mDPM;
    private ComponentName mPolicyAdmin;

    public Policy(Context context) {
        mContext = context;
        mPasswordQuality = -1;
        mPasswordLength = 0;
        mPasswordMinUpperCase = 0;
        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mPolicyAdmin = new ComponentName(context, DemoDeviceAdminReceiver.class);
    }

    /**
     * Saves the policy parameters.
     *
     * @param passwordQuality Password quality.
     * @param passwordLength Password minimum length.
     * @param passwordMinUppercase Password minimum number of upper case alphabets.
     */
    public void saveToLocal(int passwordQuality, int passwordLength, int passwordMinUppercase) {
        SharedPreferences.Editor editor =
                mContext.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit();
        if (mPasswordQuality != passwordQuality) {
            editor.putInt(KEY_PASSWORD_QUALITY, passwordQuality);
            mPasswordQuality = passwordQuality;
        }
        if (mPasswordLength != passwordLength) {
            editor.putInt(KEY_PASSWORD_LENGTH, passwordLength);
            mPasswordLength = passwordLength;
        }
        if (mPasswordMinUpperCase != passwordMinUppercase) {
            editor.putInt(KEY_PASSWORD_MIN_UPPERCASE, passwordMinUppercase);
            mPasswordMinUpperCase = passwordMinUppercase;
        }
        editor.apply();
    }

    public void readFromLocal() {
        SharedPreferences prefs = mContext.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        mPasswordQuality = prefs.getInt(KEY_PASSWORD_QUALITY, -1);
        mPasswordLength = prefs.getInt(KEY_PASSWORD_LENGTH, -1);
        mPasswordMinUpperCase = prefs.getInt(KEY_PASSWORD_MIN_UPPERCASE, -1);
    }

    /**
     * Getter for password quality.
     *
     * @return
     */
    public int getPasswordQuality() { return mPasswordQuality; }

    /**
     * Getter for password length.
     *
     * @return
     */
    public int getPasswordLength() { return mPasswordLength; }

    /**
     * Getter for password minimum upper case alphabets.
     *
     * @return
     */
    public int getPasswordMinUpperCase() { return mPasswordMinUpperCase; }

    /**
     * Getter for the policy administrator ComponentName object.
     *
     * @return
     */
    public ComponentName getPolicyAdmin() { return mPolicyAdmin; }

    /**
     * Indicates whether the device administrator is currently active.
     *
     * @return
     */
    public boolean isAdminActive() {
        return mDPM.isAdminActive(mPolicyAdmin);
    }

    public boolean isActivePasswordSufficient() {
        return mDPM.isActivePasswordSufficient();
    }

    public boolean isDeviceSecured() {
        return isAdminActive() && isActivePasswordSufficient();
    }

    /**
     * Configure policy defined in the object.
     */
    public void configurePolicy() {
        mDPM.setPasswordQuality(mPolicyAdmin, PASSWORD_QUALITY_VALUES[mPasswordQuality]);
        mDPM.setPasswordMinimumLength(mPolicyAdmin, mPasswordLength);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mDPM.setPasswordMinimumUpperCase(mPolicyAdmin, mPasswordMinUpperCase);
        }
    }

    /**
     * Through the PolicyAdmin receiver, the app can use this to trap various device
     * administration events, such as password change, incorrect password entry, etc.
     *
     */
//    public static class PolicyAdmin extends DeviceAdminReceiver {
//
//        @Override
//        public void onDisabled(Context context, Intent intent) {
//            // Called when the app is about to be deactivated as a device administrator.
//            // Deletes previously stored password policy.
//            super.onDisabled(context, intent);
//            SharedPreferences prefs = context.getSharedPreferences(APP_PREF, Activity.MODE_PRIVATE);
//            prefs.edit().clear().apply();
//        }
//    }
}

