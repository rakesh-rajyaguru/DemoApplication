package com.rakesh.demoapplication.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hendrix.pdfmyxml.PdfDocument;
import com.hendrix.pdfmyxml.viewRenderer.AbstractViewRenderer;
import com.rakesh.demoapplication.MainActivity;
import com.rakesh.demoapplication.R;
import com.rakesh.demoapplication.SensorService;
import com.rakesh.demoapplication.Utils.Utils;
import com.rakesh.demoapplication.adapter.AppAdapter;
import com.rakesh.demoapplication.pojo.AppList;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by rakesh_rajyaguru
 * on 23-Jan-17.
 */

public class AplicatinTrackerFragment extends Fragment {
    MainActivity mActivity;
    Intent mServiceIntent;
    ListView userInstalledApps;
    List<AppList> installedApps = new ArrayList<>();
    List<AbstractViewRenderer> pagelist = new ArrayList<>();
    AppAdapter installedAppAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.e(getClass().getSimpleName(), "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.e("Method Call", "onCreateView");
        return inflater.inflate(R.layout.app_uptime_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            initComponent();
        }
        Log.e("Method Call", "onActivity Created");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            initComponent();
        }
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.e("Method Call", "onAttachFragment");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("Method Call", "onCreateView");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e("Method Call", "on Attach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("Method Call", "on Detach");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("Method Call", "onHiden change:" + hidden);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
        return super.onOptionsItemSelected(item);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initComponent() {
        if (getView() == null) {
            return;
        }
        userInstalledApps = (ListView) getView().findViewById(R.id.installed_app_list);
        installedAppAdapter = new AppAdapter(mActivity, installedApps);
        userInstalledApps.setAdapter(installedAppAdapter);
        mServiceIntent = new Intent(mActivity, SensorService.class);
        if (needsUsageStatsPermission()) {
            if (hasUsageStatsPermission(mActivity)) {
                if (!isMyServiceRunning(SensorService.class)) {
                    mActivity.startService(mServiceIntent);
                }
            } else {
                requestUsageStatsPermission();
            }

        } else {
            if (!isMyServiceRunning(SensorService.class)) {
                mActivity.startService(mServiceIntent);
            }
        }
        try {
            getInstalledApps();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePage();
                GeneratePdfDocument(mActivity);
            }
        });
    }


    private void getInstalledApps() throws Exception {
        List<AppList> res = new ArrayList<>();
        List<ApplicationInfo> packages = mActivity.getPackageManager()
                .getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            int pos = Utils.getPrefrence(mActivity, packageInfo.packageName);
            if (pos > 0) {
                String appname = packageInfo.loadLabel(mActivity.getPackageManager()).toString();
                Drawable icon = packageInfo.loadIcon(mActivity.getPackageManager());
                PackageInfo info = mActivity.getPackageManager().getPackageInfo(packageInfo.packageName, 0);
                Field field = PackageInfo.class.getField("firstInstallTime");
                String appFile = packageInfo.sourceDir;
                long timestamp1 = field.getLong(info);
                long timestamp2 = new File(appFile).lastModified(); //Epoch Time
                boolean isSytemApp = isSystemPackage(packageInfo);
                res.add(new AppList(appname, packageInfo.packageName, pos, icon, timestamp1,
                        timestamp2, isSytemApp));
            }
        }
        installedApps.clear();
        installedApps.addAll(res);
        installedAppAdapter.notifyDataSetChanged();
    }

    private boolean isSystemPackage(ApplicationInfo pkgInfo) {
        return ((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mActivity
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }


    @CallSuper
    public void onDestroyView() {
        mActivity.stopService(mServiceIntent);
        super.onDestroyView();
        Log.e("Method Call", "onDestroyViewView");
    }

    private boolean needsUsageStatsPermission() {
        return postLollipop() && !hasUsageStatsPermission(mActivity);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void requestUsageStatsPermission() {
        if (!hasUsageStatsPermission(mActivity)) {
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 200);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 200) {
            if (hasUsageStatsPermission(mActivity)) {
                if (!isMyServiceRunning(SensorService.class)) {
                    mActivity.startService(mServiceIntent);
                }
            }
        }

    }

    private boolean postLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void generatePage() {
        AbstractViewRenderer page = new AbstractViewRenderer(mActivity, R.layout.mylistview) {
            @Override
            protected void initView(View view) {
                updateview(view);
            }
        };
        pagelist.add(page);
    }


    private void updateview(View view) {

        List<AppList> tmplist = installedApps;
        ListView mlistview = (ListView) view.findViewById(R.id.mlist_view);
        AppAdapter tmp = new AppAdapter(mActivity, tmplist);
        mlistview.setAdapter(tmp);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void GeneratePdfDocument(Context ctx) {
        try {
            PdfDocument doc = new PdfDocument(ctx);
            for (int i = 0; i < pagelist.size(); i++) {
                doc.addPage(pagelist.get(i));
            }
            doc.setRenderWidth(getResources().getDisplayMetrics().widthPixels);
            doc.setRenderHeight(getResources().getDisplayMetrics().heightPixels);
            doc.setOrientation(PdfDocument.A4_MODE.PORTRAIT);
            doc.setProgressTitle(R.string.gen_pdf_file);
            doc.setProgressMessage(R.string.gen_please_wait);
            doc.setFileName("ApplicationList");
            doc.setSaveDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            doc.setInflateOnMainThread(false);
            doc.setListener(new PdfDocument.Callback() {
                @Override
                public void onComplete(File file) {
                    Log.i(PdfDocument.TAG_PDF_MY_XML, "Complete");
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });

            doc.createPdf(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
