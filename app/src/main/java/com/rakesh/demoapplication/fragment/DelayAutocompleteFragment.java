package com.rakesh.demoapplication.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.gson.Gson;
import com.rakesh.demoapplication.MainActivity;
import com.rakesh.demoapplication.R;
import com.rakesh.demoapplication.Utils.ServiceUtils;
import com.rakesh.demoapplication.adapter.BookAutoCompleteAdapter;
import com.rakesh.demoapplication.customview.DelayAutoCompleteTextView;
import com.rakesh.demoapplication.pojo.PojoPlace;
import com.rakesh.demoapplication.pojo.Prediction;

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
    MainActivity mActivity;
    private final String API_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?" +
            "key=AIzaSyAgyo5MDeezpXoxZCD19ng5aBGNagYlBhw&sensor=true&input=";
    BookAutoCompleteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.e(getClass().getSimpleName(), "onCreateView");
        return inflater.inflate(R.layout.content_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        initComponent();
        Log.e(getClass().getSimpleName(), "onActivity Created");
    }


    private void initComponent() {
        if (getView() == null)
            return;
        View v = getView();
        final DelayAutoCompleteTextView bookTitle = (DelayAutoCompleteTextView)
                v.findViewById(R.id.et_book_title);
        bookTitle.setAutoCompleteDelay(DelayAutoCompleteTextView.DEFAULT_AUTOCOMPLETE_DELAY);
        adapter = new BookAutoCompleteAdapter(mActivity, API_URL);
        bookTitle.setAdapter(adapter); // 'this' is Activity instance
//        bookTitle.setLoadingIndicator(
//                (android.widget.ProgressBar) v.findViewById(R.id.pb_loading_indicator));
        bookTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Prediction book = (Prediction) adapterView.getItemAtPosition(position);
                bookTitle.setText(book.getDescription());
                mActivity.addFragment(CustomMapMarker.newInstance(book.getPlaceId()), "Location On Map");

            }
        });
        bookTitle.setEnabled(ServiceUtils.checkConnectivity(mActivity));
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


}
