/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.linkbubble.util;

import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.linkbubble.BuildConfig;

/**
 * Crash/log facade that forwards to Firebase Crashlytics.
 *
 * Crashlytics auto-initializes via its ContentProvider, so no init call is
 * needed in MainApplication. Uncaught exceptions and ANRs are reported
 * automatically; this class adds:
 *   - log()                 -> breadcrumb attached to next crash
 *   - logHandledException() -> non-fatal exception report
 *   - setXxx()              -> custom keys attached to next crash
 *
 * In debug builds we also emit a Logcat line so the same information is
 * visible during local development.
 */
public class CrashTracking {

    private static final String TAG = "CrashTracking";

    private static FirebaseCrashlytics crashlytics() {
        return FirebaseCrashlytics.getInstance();
    }

    public static void logHandledException(Throwable throwable) {
        Log.e(TAG, "Handled exception", throwable);
        crashlytics().recordException(throwable);
    }

    public static void setInt(String key, int value) {
        crashlytics().setCustomKey(key, value);
        if (BuildConfig.DEBUG) Log.d(TAG, key + ": " + value);
    }

    public static void setDouble(String key, double value) {
        crashlytics().setCustomKey(key, value);
        if (BuildConfig.DEBUG) Log.d(TAG, key + ": " + value);
    }

    public static void setFloat(String key, float value) {
        crashlytics().setCustomKey(key, value);
        if (BuildConfig.DEBUG) Log.d(TAG, key + ": " + value);
    }

    public static void setString(String key, String string) {
        crashlytics().setCustomKey(key, string);
        if (BuildConfig.DEBUG) Log.d(TAG, key + ": " + string);
    }

    public static void setBool(String key, boolean value) {
        crashlytics().setCustomKey(key, value);
        if (BuildConfig.DEBUG) Log.d(TAG, key + ": " + value);
    }

    public static void log(String message) {
        crashlytics().log(message);
        if (BuildConfig.DEBUG) Log.d(TAG, message);
    }
}
