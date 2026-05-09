/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.linkbubble.util;

import android.util.Log;

import com.linkbubble.BuildConfig;

/**
 * Crash/log facade. Currently delegates to android.util.Log only.
 *
 * TODO: Wire up to Firebase Crashlytics — see DEV-PLAN §4.
 * Methods to delegate when integrated:
 *   log()                 -> FirebaseCrashlytics.getInstance().log()
 *   logHandledException() -> FirebaseCrashlytics.getInstance().recordException()
 *   setString/Int/...     -> FirebaseCrashlytics.getInstance().setCustomKey()
 * The 137 call sites across the project remain unchanged so the integration
 * is a single-file edit.
 */
public class CrashTracking {

    private static final String TAG = "CrashTracking";

    public static void logHandledException(Throwable throwable) {
        Log.e(TAG, "Handled exception", throwable);
    }

    private static void logKeyValue(String key, Object value) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, key + ": " + value);
        } else {
            Log.i(TAG, key + ": " + value);
        }
    }

    public static void setInt(String key, int value) {
        logKeyValue(key, value);
    }

    public static void setDouble(String key, double value) {
        logKeyValue(key, value);
    }

    public static void setFloat(String key, float value) {
        logKeyValue(key, value);
    }

    public static void setString(String key, String string) {
        logKeyValue(key, string);
    }

    public static void setBool(String key, boolean value) {
        logKeyValue(key, value);
    }

    public static void log(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        } else {
            Log.i(TAG, message);
        }
    }
}
