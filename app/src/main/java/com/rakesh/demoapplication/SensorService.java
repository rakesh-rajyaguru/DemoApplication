package com.rakesh.demoapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rakesh.demoapplication.Utils.RecordingSession;
import com.rakesh.demoapplication.Utils.Utils;
import com.rakesh.demoapplication.detectors.Detector;
import com.rakesh.demoapplication.detectors.LollipopDetector;
import com.rakesh.demoapplication.detectors.PreLollipopDetector;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by fabio
 * on 30/01/2016.
 */
public class SensorService extends Service {
    private Detector detector;
    private Context mContext;
    private Timer timer;
    private TimerTask timerTask;
    private RecordingSession session = null;
    private int resultCode;
    private Intent resultData;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mContext = this;
        if (session == null) {
            MediaProjectionManager mgr =
                    (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            MediaProjection projection =
                    mgr.getMediaProjection(resultCode, resultData);
            session =
                    new RecordingSession(this, projection);
        }
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (session != null) {
            session.stop();
            session = null;
        }
        stoptimertask();
        Intent broadcastIntent = new Intent("com.rakesh.demoapplication.RestartSensor");
        sendBroadcast(broadcastIntent);
    }


    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000); //
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
                        stopRecorder();
                        startRecorder();
                    } else {
                        stopRecorder();
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

    synchronized private void startRecorder() {
        if (session == null) {
            MediaProjectionManager mgr =
                    (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            MediaProjection projection =
                    mgr.getMediaProjection(resultCode, resultData);
            session =
                    new RecordingSession(this, projection);
        }
        session.start();
    }

    synchronized private void stopRecorder() {
        if (session != null) {
            session.stop();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String getForegroundApp(Context context) {
        if (Utils.postLollipop())
            detector = new LollipopDetector();
        else
            detector = new PreLollipopDetector();
        return detector.getForegroundApp(context);
    }


}