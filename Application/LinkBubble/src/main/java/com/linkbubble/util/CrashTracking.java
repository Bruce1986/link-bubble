/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.linkbubble.util;

import android.util.Log;

import com.linkbubble.BuildConfig;

public class CrashTracking {

    private static final String TAG = "CrashTracking";

    public static void logHandledException(Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Handled exception", throwable);
        }
    }

    public static void setInt(String key, int value) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, key + ": " + value);
        }
    }

    public static void setDouble(String key, double value) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, key + ": " + value);
        }
    }

    public static void setFloat(String key, float value) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, key + ": " + value);
        }
    }

    public static void setString(String key, String string) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, key + ": " + string);
        }
    }

    public static void setBool(String key, boolean value) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, key + ": " + value);
        }
    }

    public static void log(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }
}
