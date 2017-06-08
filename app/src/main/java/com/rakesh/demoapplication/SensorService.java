package com.rakesh.demoapplication;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rakesh.demoapplication.Utils.Utils;
import com.rakesh.demoapplication.detectors.Detector;
import com.rakesh.demoapplication.detectors.LollipopDetector;
import com.rakesh.demoapplication.detectors.PreLollipopDetector;

import java.util.Timer;
import java.util.TimerTask;

import in.omerjerk.libscreenshotter.Screenshotter;


/**
 * Created by fabio
 * on 30/01/2016.
 */
public class SensorService extends Service {
    private Detector detector;
    private Context mContext;
    private Timer timer;
    private TimerTask timerTask;
    private Intent mData;
    private int resultcode;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e(getClass().getSimpleName(), "on start");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e(getClass().getSimpleName(), "on start command");
        mContext = this;
        startTimer();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(getClass().getSimpleName(), "onbind");
        return null;
    }

    public String getForegroundApp(Context context) {
        if (Utils.postLollipop())
            detector = new LollipopDetector();
        else
            detector = new PreLollipopDetector();
        return detector.getForegroundApp(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(getClass().getSimpleName(), "onDestroy");
        stoptimertask();
        Intent broadcastIntent = new Intent("com.rakesh.demoapplication.RestartSensor");
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.e(getClass().getSimpleName(), "onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(getClass().getSimpleName(), "onUnbind");
        return super.onUnbind(intent);
    }

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000); //
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(getClass().getSimpleName(), "on Low Memory");
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                final String foregroundApp = getForegroundApp(mContext);
                if (!foregroundApp.trim().equalsIgnoreCase(getPackageName())
                        && !foregroundApp.trim().equalsIgnoreCase("com.miui.home")) {
                    int i = Utils.getPrefrence(mContext, foregroundApp);
                    i++;
                    Utils.setPrefrence(mContext, foregroundApp, i);
                    Log.i("in timer", foregroundApp);
                    if (foregroundApp.equalsIgnoreCase("com.whatsapp")) {
                        Screenshotter.getInstance()
                                .setSize(720, 1280)
                                .takeScreenshot(SensorService.this, Activity.RESULT_OK, mData,
                                        bitmap -> {

                                        });
                    }
                }
            }
        };
    }


    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }





}