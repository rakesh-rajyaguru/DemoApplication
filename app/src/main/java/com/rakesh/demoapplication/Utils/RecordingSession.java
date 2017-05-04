/***
 Copyright (c) 2015 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.rakesh.demoapplication.Utils;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.ToneGenerator;
import android.media.projection.MediaProjection;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class RecordingSession
        implements MediaScannerConnection.OnScanCompletedListener {
    private static final int VIRT_DISPLAY_FLAGS =
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY |
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private final File output;
    private final Context ctxt;
    private final ToneGenerator beeper;
    private MediaRecorder recorder;
    private MediaProjection projection;
    private VirtualDisplay vdisplay;


    public RecordingSession(Context ctxt, MediaProjection projection) {
        this.ctxt = ctxt.getApplicationContext();
        this.projection = projection;
        this.beeper = new ToneGenerator(
                AudioManager.STREAM_NOTIFICATION, 100);

        output = new File(ctxt.getCacheDir(), "andcorder.mp4");
        if (output.getParentFile().mkdirs()) {
            Toast.makeText(ctxt, "Parent directory Creted", Toast.LENGTH_LONG).show();
        }
    }

    public void start() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) ctxt.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int density = metrics.densityDpi;

        recorder = new MediaRecorder();
        recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setVideoFrameRate(60);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        recorder.setVideoSize(width, height);
        recorder.setMaxDuration(40);
        recorder.setVideoEncodingBitRate(60);
        recorder.setOutputFile(output.getAbsolutePath());

        try {
            recorder.prepare();
            vdisplay = projection.createVirtualDisplay("andcorder",
                    width, height, density,
                    VIRT_DISPLAY_FLAGS, recorder.getSurface(), null, null);
            beeper.startTone(ToneGenerator.TONE_PROP_ACK);
            recorder.start();
        } catch (IOException e) {
            throw new RuntimeException("Exception preparing recorder", e);
        }
    }


    public void stop() {
        projection.stop();
        recorder.stop();
        recorder.release();
        vdisplay.release();

        MediaScannerConnection.scanFile(ctxt,
                new String[]{output.getAbsolutePath()}, null, this);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        beeper.startTone(ToneGenerator.TONE_PROP_NACK);
    }
}
