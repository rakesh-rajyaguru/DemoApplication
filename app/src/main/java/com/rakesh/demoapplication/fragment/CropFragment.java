package com.rakesh.demoapplication.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rakesh.demoapplication.R;
import com.rakesh.demoapplication.Utils.CropDemoPreset;
import com.rakesh.demoapplication.Utils.CropImageViewOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CropFragment extends Fragment implements View.OnClickListener {

    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private MainFragment mCurrentFragment;
    private Uri mCropImageUri;
    private CropImageViewOptions mCropImageViewOptions = new CropImageViewOptions();
    //endregion
    private FrameLayout container;
    private ScrollView navigationDrawer;
    private TextView drawerOptionLoad;
    private TextView drawerOptionOval;
    private TextView drawerOptionRect;
    private TextView drawerOptionCustomizedOverlay;
    private TextView drawerOptionMinMaxOverride;
    private TextView drawerOptionScaleCenter;
    private TextView drawerOptionToggleScale;
    private TextView drawerOptionToggleShape;
    private TextView drawerOptionToggleGuidelines;
    private TextView drawerOptionToggleAspectRatio;
    private TextView drawerOptionToggleAutoZoom;
    private TextView drawerOptionToggleMaxZoom;
    private TextView drawerOptionSetInitialCropRect;
    private TextView drawerOptionResetCropRect;
    private TextView drawerOptionToggleMultitouch;
    private TextView drawerOptionToggleShowOverlay;
    private TextView drawerOptionToggleShowProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.crop_fragment_layout, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        container = (FrameLayout) view.findViewById(R.id.container);
        navigationDrawer = (ScrollView) view.findViewById(R.id.navigation_drawer);
        drawerOptionLoad = (TextView) view.findViewById(R.id.drawer_option_load);
        drawerOptionLoad.setOnClickListener(this);
        drawerOptionOval = (TextView) view.findViewById(R.id.drawer_option_oval);
        drawerOptionOval.setOnClickListener(this);
        drawerOptionRect = (TextView) view.findViewById(R.id.drawer_option_rect);
        drawerOptionRect.setOnClickListener(this);
        drawerOptionCustomizedOverlay = (TextView) view.findViewById(R.id.drawer_option_customized_overlay);
        drawerOptionCustomizedOverlay.setOnClickListener(this);
        drawerOptionMinMaxOverride = (TextView) view.findViewById(R.id.drawer_option_min_max_override);
        drawerOptionMinMaxOverride.setOnClickListener(this);
        drawerOptionScaleCenter = (TextView) view.findViewById(R.id.drawer_option_scale_center);
        drawerOptionScaleCenter.setOnClickListener(this);
        drawerOptionToggleScale = (TextView) view.findViewById(R.id.drawer_option_toggle_scale);
        drawerOptionToggleScale.setOnClickListener(this);
        drawerOptionToggleShape = (TextView) view.findViewById(R.id.drawer_option_toggle_shape);
        drawerOptionToggleShape.setOnClickListener(this);
        drawerOptionToggleGuidelines = (TextView) view.findViewById(R.id.drawer_option_toggle_guidelines);
        drawerOptionToggleGuidelines.setOnClickListener(this);
        drawerOptionToggleAspectRatio = (TextView) view.findViewById(R.id.drawer_option_toggle_aspect_ratio);
        drawerOptionToggleAspectRatio.setOnClickListener(this);
        drawerOptionToggleAutoZoom = (TextView) view.findViewById(R.id.drawer_option_toggle_auto_zoom);
        drawerOptionToggleAutoZoom.setOnClickListener(this);
        drawerOptionToggleMaxZoom = (TextView) view.findViewById(R.id.drawer_option_toggle_max_zoom);
        drawerOptionToggleMaxZoom.setOnClickListener(this);
        drawerOptionSetInitialCropRect = (TextView) view.findViewById(R.id.drawer_option_set_initial_crop_rect);
        drawerOptionSetInitialCropRect.setOnClickListener(this);
        drawerOptionResetCropRect = (TextView) view.findViewById(R.id.drawer_option_reset_crop_rect);
        drawerOptionResetCropRect.setOnClickListener(this);
        drawerOptionToggleMultitouch = (TextView) view.findViewById(R.id.drawer_option_toggle_multitouch);
        drawerOptionToggleMultitouch.setOnClickListener(this);
        drawerOptionToggleShowOverlay = (TextView) view.findViewById(R.id.drawer_option_toggle_show_overlay);
        drawerOptionToggleShowOverlay.setOnClickListener(this);
        drawerOptionToggleShowProgressBar = (TextView) view.findViewById(R.id.drawer_option_toggle_show_progress_bar);
        drawerOptionToggleShowProgressBar.setOnClickListener(this);
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, R.string.main_drawer_open, R.string.main_drawer_close);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            setMainFragmentByPreset(CropDemoPreset.RECT);
        }
    }


    public void setCurrentFragment(MainFragment fragment) {
        mCurrentFragment = fragment;
    }

    public void setCurrentOptions(CropImageViewOptions options) {
        mCropImageViewOptions = options;
        updateDrawerTogglesByOptions(options);
    }


//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        mDrawerToggle.syncState();
//        mCurrentFragment.updateCurrentCropViewOptions();
//    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crop_drawer_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_open_drawer) {
            mDrawerLayout.openDrawer(GravityCompat.END);
            return true;
        } else {
            return mDrawerToggle.onOptionsItemSelected(item) || mCurrentFragment != null &&
                    mCurrentFragment.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }

    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(getActivity(), data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {

                mCurrentFragment.setImageUri(imageUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(getActivity());
            } else {
                Toast.makeText(getActivity(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCurrentFragment.setImageUri(mCropImageUri);
            } else {
                Toast.makeText(getActivity(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawer_option_load:
                if (CropImage.isExplicitCameraPermissionRequired(getActivity())) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                } else {
                    CropImage.startPickImageActivity(getActivity());
                }
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_oval:
                setMainFragmentByPreset(CropDemoPreset.CIRCULAR);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_rect:
                setMainFragmentByPreset(CropDemoPreset.RECT);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_customized_overlay:
                setMainFragmentByPreset(CropDemoPreset.CUSTOMIZED_OVERLAY);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_min_max_override:
                setMainFragmentByPreset(CropDemoPreset.MIN_MAX_OVERRIDE);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_scale_center:
                setMainFragmentByPreset(CropDemoPreset.SCALE_CENTER_INSIDE);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_toggle_scale:
                mCropImageViewOptions.scaleType = mCropImageViewOptions.scaleType == CropImageView.ScaleType.FIT_CENTER
                        ? CropImageView.ScaleType.CENTER_INSIDE : mCropImageViewOptions.scaleType == CropImageView.ScaleType.CENTER_INSIDE
                        ? CropImageView.ScaleType.CENTER : mCropImageViewOptions.scaleType == CropImageView.ScaleType.CENTER
                        ? CropImageView.ScaleType.CENTER_CROP : CropImageView.ScaleType.FIT_CENTER;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_toggle_shape:
                mCropImageViewOptions.cropShape = mCropImageViewOptions.cropShape == CropImageView.CropShape.RECTANGLE
                        ? CropImageView.CropShape.OVAL : CropImageView.CropShape.RECTANGLE;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_toggle_guidelines:
                mCropImageViewOptions.guidelines = mCropImageViewOptions.guidelines == CropImageView.Guidelines.OFF
                        ? CropImageView.Guidelines.ON : mCropImageViewOptions.guidelines == CropImageView.Guidelines.ON
                        ? CropImageView.Guidelines.ON_TOUCH : CropImageView.Guidelines.OFF;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_toggle_aspect_ratio:
                if (!mCropImageViewOptions.fixAspectRatio) {
                    mCropImageViewOptions.fixAspectRatio = true;
                    mCropImageViewOptions.aspectRatio = new Pair<>(1, 1);
                } else {
                    if (mCropImageViewOptions.aspectRatio.first == 1 && mCropImageViewOptions.aspectRatio.second == 1) {
                        mCropImageViewOptions.aspectRatio = new Pair<>(4, 3);
                    } else if (mCropImageViewOptions.aspectRatio.first == 4 && mCropImageViewOptions.aspectRatio.second == 3) {
                        mCropImageViewOptions.aspectRatio = new Pair<>(16, 9);
                    } else if (mCropImageViewOptions.aspectRatio.first == 16 && mCropImageViewOptions.aspectRatio.second == 9) {
                        mCropImageViewOptions.aspectRatio = new Pair<>(9, 16);
                    } else {
                        mCropImageViewOptions.fixAspectRatio = false;
                    }
                }
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_toggle_auto_zoom:
                mCropImageViewOptions.autoZoomEnabled = !mCropImageViewOptions.autoZoomEnabled;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_toggle_max_zoom:
                mCropImageViewOptions.maxZoomLevel = mCropImageViewOptions.maxZoomLevel == 4 ? 8
                        : mCropImageViewOptions.maxZoomLevel == 8 ? 2 : 4;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_set_initial_crop_rect:
                mCurrentFragment.setInitialCropRect();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_reset_crop_rect:
                mCurrentFragment.resetCropRect();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_toggle_multitouch:
                mCropImageViewOptions.multitouch = !mCropImageViewOptions.multitouch;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_toggle_show_overlay:
                mCropImageViewOptions.showCropOverlay = !mCropImageViewOptions.showCropOverlay;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_toggle_show_progress_bar:
                mCropImageViewOptions.showProgressBar = !mCropImageViewOptions.showProgressBar;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            default:
                Toast.makeText(getActivity(), "Unknown drawer option clicked", Toast.LENGTH_LONG).show();
        }
    }


    private void setMainFragmentByPreset(CropDemoPreset demoPreset) {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance(demoPreset))
                .commit();
    }

    @SuppressLint("StringFormatMatches")
    private void updateDrawerTogglesByOptions(CropImageViewOptions options) {
        ((TextView) getView().findViewById(R.id.drawer_option_toggle_scale)).setText(getResources().getString(R.string.drawer_option_toggle_scale, options.scaleType.name()));
        ((TextView) getView().findViewById(R.id.drawer_option_toggle_shape)).setText(getResources().getString(R.string.drawer_option_toggle_shape, options.cropShape.name()));
        ((TextView) getView().findViewById(R.id.drawer_option_toggle_guidelines)).setText(getResources().getString(R.string.drawer_option_toggle_guidelines, options.guidelines.name()));
        ((TextView) getView().findViewById(R.id.drawer_option_toggle_multitouch)).setText(getResources().getString(R.string.drawer_option_toggle_multitouch, Boolean.toString(options.multitouch)));
        ((TextView) getView().findViewById(R.id.drawer_option_toggle_show_overlay)).setText(getResources().getString(R.string.drawer_option_toggle_show_overlay, Boolean.toString(options.showCropOverlay)));
        ((TextView) getView().findViewById(R.id.drawer_option_toggle_show_progress_bar)).setText(getResources().getString(R.string.drawer_option_toggle_show_progress_bar, Boolean.toString(options.showProgressBar)));

        String aspectRatio = "FREE";
        if (options.fixAspectRatio) {
            aspectRatio = options.aspectRatio.first + ":" + options.aspectRatio.second;
        }
        ((TextView) getView().findViewById(R.id.drawer_option_toggle_aspect_ratio)).setText(getResources().getString(R.string.drawer_option_toggle_aspect_ratio, aspectRatio));

        ((TextView) getView().findViewById(R.id.drawer_option_toggle_auto_zoom)).setText(getResources().getString(R.string.drawer_option_toggle_auto_zoom, options.autoZoomEnabled ? "Enabled" : "Disabled"));
        ((TextView) getView().findViewById(R.id.drawer_option_toggle_max_zoom)).setText(getResources().getString(R.string.drawer_option_toggle_max_zoom, options.maxZoomLevel));
    }


}
