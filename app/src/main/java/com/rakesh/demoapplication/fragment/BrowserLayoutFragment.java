package com.rakesh.demoapplication.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rakesh.demoapplication.MainActivity;
import com.rakesh.demoapplication.R;
import com.rakesh.demoapplication.customview.Backable;

public class BrowserLayoutFragment extends Fragment implements Backable {
    MainActivity mainActivity;
    private WebView webview;
    private ProgressDialog pd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.browser_layout, null);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(mainActivity);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(100);
        webview = (WebView) view.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setGeolocationEnabled(true);
        webview.setWebChromeClient(new webChromeClient());
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl("http://www.google.co.in/");
    }


    @Override
    public boolean onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
            return true;
        } else {
            return false;
        }

    }


    private class webChromeClient extends WebChromeClient {


        @Override
        public void onReceivedTitle(WebView view, String title) {
            mainActivity.setTitle(title); //Set Activity tile to page title.
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress >= 99) {
                pd.dismiss();
            } else {
                pd.show();
                pd.setProgress(newProgress);

            }

        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

}
