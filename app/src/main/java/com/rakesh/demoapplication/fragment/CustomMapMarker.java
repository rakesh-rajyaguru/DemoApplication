package com.rakesh.demoapplication.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.rakesh.demoapplication.MainActivity;
import com.rakesh.demoapplication.R;
import com.rakesh.demoapplication.Utils.ServiceUtils;
import com.rakesh.demoapplication.Utils.Utils;
import com.rakesh.demoapplication.pojo.Example;
import com.rakesh.demoapplication.pojo.Result;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CustomMapMarker extends Fragment implements OnMapReadyCallback {
    private View mView;
    private MainActivity mActivity;
    private GoogleMap mGoogleMap;
    private String mPlace;
    private Marker customMarker;

    public static CustomMapMarker newInstance(String result) {
        CustomMapMarker fragment = new CustomMapMarker();
        Bundle args = new Bundle();
        args.putString("place", result);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.map_layout, container, false);
        mPlace = getArguments().getString("place");
        Log.e(getClass().getSimpleName(), "onCreateView");
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mView = getView();
        initilizeMap();
        Log.e(getClass().getSimpleName(), "onActivity Created");
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.e(getClass().getSimpleName(), "onAttachFragment");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(getClass().getSimpleName(), "onCreateView");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(getClass().getSimpleName(), "on Attach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(getClass().getSimpleName(), "on Detach");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(getClass().getSimpleName(), "onHiden change:" + hidden);
    }

    private void initView() {
        if (ServiceUtils.checkConnectivity(mActivity)) {
            new RetrieveFeedTask().execute(mPlace);
        } else {
            Utils.showConnectionMessage(mActivity, true);
        }

    }


    public void initilizeMap() {
        if (checkPlayServices()) {
            SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().add(R.id.map_location, mMapFragment)
                    .commit();
            mMapFragment.getMapAsync(this);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(mActivity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                final Dialog errorDlg = GoogleApiAvailability.getInstance()
                        .getErrorDialog(mActivity, resultCode,
                                9000);
                errorDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        errorDlg.dismiss();
                        mActivity.onBackPressed();
                    }
                });
                errorDlg.show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap paramGoogleMap) {
        mGoogleMap = paramGoogleMap;
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
            mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.022505, 72.571362)
                    , 10));
            initView();
        }
    }

    private void setUpMap(final Result mResult) {

        View marker = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_marker_layout, null);
        TextView numTxt = (TextView) marker.findViewById(R.id.num_txt);
        numTxt.setText(mResult.getName());

        customMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(mResult.getGeometry().getLocation().getLat(),
                        mResult.getGeometry().getLocation().getLng()))
                .title(mResult.getName())
                .snippet(mResult.getFormattedAddress())
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mActivity, marker))));
        final View mapView = getChildFragmentManager().findFragmentById(R.id.map_location).getView();
        if (mapView == null) {
            return;
        }
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressLint("NewApi")
                // We check which build version we are using.
                @Override
                public void onGlobalLayout() {
                    LatLngBounds bounds = new LatLngBounds.Builder().include(customMarker.getPosition()).build();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                }
            });
        }
    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private class RetrieveFeedTask extends AsyncTask<String, Void, String> {
        String API_URL = "https://maps.googleapis.com/maps/api/place/details/json?" +
                "key=AIzaSyAgyo5MDeezpXoxZCD19ng5aBGNagYlBhw&placeid=";


        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... input) {
            try {
                URL url = new URL(API_URL + input[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return "";
            }
        }

        protected void onPostExecute(String response) {
            if (response != null && response.trim().length() > 0) {
                Gson gson = new Gson();
                Example ps = gson.fromJson(response, Example.class);
                setUpMap(ps.getResult());
            }
            Log.i("INFO", response);
        }
    }
}