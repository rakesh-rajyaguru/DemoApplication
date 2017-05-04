package com.rakesh.demoapplication.fragment;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.rakesh.demoapplication.R;
import com.rakesh.demoapplication.adminpolicy.PolicySetupActivity;
import com.rakesh.demoapplication.receiver.DemoDeviceAdminReceiver;

public class DevicePolicyFragment extends Fragment implements
        OnCheckedChangeListener, View.OnClickListener {
    static final String TAG = "DevicePolicyDemoActivity";
    static final int ACTIVATION_REQUEST = 47; // identifies our request id
    DevicePolicyManager devicePolicyManager;
    ComponentName demoDeviceAdmin;
    ToggleButton toggleButton;
    Activity mActivity;
    Button btnLockDevice, btnResetDevice, btnSetPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        if (getView() != null) {
            toggleButton = (ToggleButton)
                    getView().findViewById(R.id.toggle_device_admin);
            btnLockDevice = (Button)
                    getView().findViewById(R.id.button_lock_device);
            btnLockDevice.setOnClickListener(this);
            btnResetDevice = (Button)
                    getView().findViewById(R.id.button_reset_device);
            btnResetDevice.setOnClickListener(this);
            btnSetPassword = (Button)
                    getView().findViewById(R.id.button_set_Password);
            btnSetPassword.setOnClickListener(this);
            toggleButton.setOnCheckedChangeListener(this);

            // Initialize Device Policy Manager service and our receiver class
            devicePolicyManager = (DevicePolicyManager) mActivity.getSystemService(Context.DEVICE_POLICY_SERVICE);
            demoDeviceAdmin = new ComponentName(mActivity, DemoDeviceAdminReceiver.class);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_lock_device:
                // We lock the screen
                Toast.makeText(mActivity, "Locking device...", Toast.LENGTH_LONG).show();
                try {
                    devicePolicyManager.lockNow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button_reset_device:
                Toast.makeText(mActivity, "RESETing device now - all user data will be ERASED to " +
                        "factory settings", Toast.LENGTH_LONG).show();
                Snackbar.make(btnResetDevice, "This will erase all data, Continue ?",
                        Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            devicePolicyManager.wipeData(ACTIVATION_REQUEST);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).show();
                break;
            case R.id.button_set_Password:
                startActivity(new Intent(mActivity, PolicySetupActivity.class));
                break;
        }
    }

    /**
     * Called when the state of toggle button changes. In this case, we send an
     * intent to activate the device policy administration.
     */
    @Override
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        if (isChecked) {
            // Activate device administration
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    demoDeviceAdmin);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Your boss told you to do this");
            startActivityForResult(intent, ACTIVATION_REQUEST);
        }
    }

    /**
     * Called when startActivityForResult() call is completed. The result of
     * activation could be success of failure, mostly depending on user okaying
     * this app's request to administer the device.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVATION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    toggleButton.setChecked(true);
                } else {
                    toggleButton.setChecked(false);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

}