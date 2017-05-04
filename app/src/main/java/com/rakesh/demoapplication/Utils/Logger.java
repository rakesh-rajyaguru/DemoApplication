/*
 * Copyright (c) 2015 DigitasLBi. All rights reserved. (TBC)
 */
package com.rakesh.demoapplication.Utils;

import android.util.Log;

public class Logger {

    static Boolean isShowLog = true;

    public static void e(String tag, String message) {
        if (isShowLog) {
            Log.e(tag, message);
        }
    }
}
