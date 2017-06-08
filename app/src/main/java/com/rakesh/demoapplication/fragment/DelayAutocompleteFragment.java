package com.rakesh.demoapplication.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.rakesh.demoapplication.MainActivity;
import com.rakesh.demoapplication.R;
import com.rakesh.demoapplication.Utils.ServiceUtils;
import com.rakesh.demoapplication.Utils.Utils;
import com.rakesh.demoapplication.adapter.BookAutoCompleteAdapter;
import com.rakesh.demoapplication.pojo.Example;
import com.rakesh.demoapplication.pojo.PojoPlace;
import com.rakesh.demoapplication.pojo.Prediction;
import com.rakesh.demoapplication.pojo.Result;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rakesh_rajyaguru
 * on 23-Jan-17.
 */

public class DelayAutocompleteFragment extends Fragment {
    private final String API_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?" +
            "key=AIzaSyAgyo5MDeezpXoxZCD19ng5aBGNagYlBhw&sensor=true&input=";
    private MainActivity mActivity;
    private BookAutoCompleteAdapter adapter;
    private List<Prediction> resultList = new ArrayList<>();
    private GoogleMap mGoogleMap;
    private Marker customMarker;
    private MaterialSearchView mSearchView;
    private ProgressBar progress;
    private Prediction book;

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.e(getClass().getSimpleName(), "onCreateView");
        return inflater.inflate(R.layout.content_main, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.e(getClass().getSimpleName(), "onCreate");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        initilizeMap();
        Log.e(getClass().getSimpleName(), "onActivity Created");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(getClass().getSimpleName(), "onActivity Result");
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.e(getClass().getSimpleName(), "onAttachFragment");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(getClass().getSimpleName(), "onAttach");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(getClass().getSimpleName(), "on Attach (Depricated)");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(getClass().getSimpleName(), "Destroy View");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
        Log.e(getClass().getSimpleName(), "Option menu created");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.e(getClass().getSimpleName(), "Option menu prepared");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(getClass().getSimpleName(), "Option item selected ");
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        Log.e(getClass().getSimpleName(), "Option menu destroy ");
    }

    private void initComponent() {
        if (getView() == null)
            return;
        View v = getView();
//        final DelayAutoCompleteTextView bookTitle = (DelayAutoCompleteTextView)
//                v.findViewById(R.id.et_book_title);
//        bookTitle.setAutoCompleteDelay(DelayAutoCompleteTextView.DEFAULT_AUTOCOMPLETE_DELAY);
//        adapter = new BookAutoCompleteAdapter(mActivity, API_URL);
//        bookTitle.setAdapter(adapter); // 'this' is Activity instance
//        bookTitle.setLoadingIndicator(
//                (android.widget.ProgressBar) v.findViewById(R.id.pb_loading_indicator));

//        bookTitle.setEnabled(ServiceUtils.checkConnectivity(mActivity));
        progress = (ProgressBar) getView().findViewById(R.id.pb_loading_indicator);
        mSearchView = (MaterialSearchView) v.findViewById(R.id.searchView);
        mSearchView.setVoiceSearch(true);
        adapter = new BookAutoCompleteAdapter(mActivity, resultList);
        mSearchView.setAdapter(adapter);
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (ServiceUtils.checkConnectivity(mActivity)) {
                    if (book != null) {
                        new RetrieveFeedTask().execute(book.getPlaceId());
                    }
                } else {
                    Utils.showConnectionMessage(mActivity, true);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                new myplace().execute(newText);
                return false;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                book = null;
            }

            @Override
            public void onSearchViewClosed() {
            }
        });
        mSearchView.setOnItemClickListener((adapterView, view, position, id) -> {
            book = (Prediction) adapterView.getItemAtPosition(position);
            mSearchView.setQuery(book.getDescription(), false);
        });
    }

    public void initilizeMap() {
        if (checkPlayServices()) {
            SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().add(R.id.map_location, mMapFragment)
                    .commit();
            mMapFragment.getMapAsync(googleMap -> {
                mGoogleMap = googleMap;
                if (mGoogleMap != null) {
                    mGoogleMap.clear();
                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
                    mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.022505, 72.571362)
                            , 10));
                    initComponent();
                }
            });
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
                errorDlg.setOnCancelListener(dialog -> {
                    errorDlg.dismiss();
                    mActivity.onBackPressed();
                });
                errorDlg.show();
            }
            return false;
        }
        return true;
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
                @Override
                public void onGlobalLayout() {
                    LatLngBounds bounds = new LatLngBounds.Builder().include(customMarker.getPosition()).build();
                    mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                }
            });
        }
    }

    private class RetrieveFeedTask extends AsyncTask<String, Void, String> {
        String API_URL = "https://maps.googleapis.com/maps/api/place/details/json?" +
                "key=AIzaSyAgyo5MDeezpXoxZCD19ng5aBGNagYlBhw&placeid=";


        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
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
                mSearchView.closeSearch();
            }
            Log.i("INFO", response);
            progress.setVisibility(View.GONE);
        }
    }

    public class myplace extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!ServiceUtils.checkConnectivity(mActivity)) {
                this.cancel(true);
            }
            progress.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... input) {
            try {
                String tmp = URLEncoder.encode(input[0], "UTF-8");
                URL url = new URL(API_URL + tmp);
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
                return null;
            }

        }

        protected void onPostExecute(String response) {
            if (response != null && response.trim().length() > 0) {
                Gson gson = new Gson();
                PojoPlace ps = gson.fromJson(response, PojoPlace.class);
                List<Prediction> locations = ps.getPredictions();
                resultList.clear();
                resultList.addAll(locations);
                adapter.notifyDataSetChanged();
                mSearchView.showSuggestions();

            }
            progress.setVisibility(View.GONE);
        }
    }


}
