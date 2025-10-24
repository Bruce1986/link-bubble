/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.linkbubble.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.linkbubble.R;
import com.linkbubble.Settings;

/**
 * Centralised helper for checking and responding to the POST_NOTIFICATIONS runtime permission.
 */
public final class NotificationPermissionHelper {

    private NotificationPermissionHelper() {
    }

    /**
     * Returns {@code true} when Android requires the POST_NOTIFICATIONS runtime permission.
     */
    public static boolean requiresRuntimePermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    /**
     * Returns {@code true} if the app can currently post notifications.
     */
    public static boolean hasPermission(@NonNull Context context) {
        if (!requiresRuntimePermission()) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Determines whether the UI should show an educational rationale dialog.
     */
    public static boolean shouldShowRationale(@NonNull Activity activity) {
        return requiresRuntimePermission()
                && ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.POST_NOTIFICATIONS);
    }

    public static boolean wasPermissionPermanentlyDenied() {
        Settings settings = Settings.get();
        return settings != null && settings.wasNotificationPermissionPermanentlyDenied();
    }

    public static void markPermissionPermanentlyDenied() {
        setPermissionPermanentlyDenied(true);
    }

    public static void clearPermanentlyDeniedFlag() {
        setPermissionPermanentlyDenied(false);
    }

    private static void setPermissionPermanentlyDenied(boolean denied) {
        Settings settings = Settings.get();
        if (settings == null) {
            return;
        }
        settings.setNotificationPermissionPermanentlyDenied(denied);
    }

    /**
     * Shows a non-blocking toast when the permission was permanently denied.
     */
    public static void showPermissionDeniedMessage(@NonNull final Context context) {
        if (!wasPermissionPermanentlyDenied()) {
            return;
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context.getApplicationContext(),
                R.string.notification_permission_required_toast, Toast.LENGTH_LONG).show());
    }
}
