/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.linkbubble.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.linkbubble.R;

/**
 * Transparent, no-UI activity that requests the runtime POST_NOTIFICATIONS
 * permission on Android 13+. MainService launches it when it needs to show its
 * foreground notification but the permission has not been granted yet, so the
 * request also happens on entry paths (e.g. EntryActivity) that never open
 * HomeActivity.
 */
public class NotificationPermissionActivity extends Activity {

    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 34;

    /** Persisted so we only auto-prompt once per install instead of nagging on every recreation. */
    private static final String PREF_POST_NOTIFICATIONS_ASKED = "post_notifications_permission_asked";

    /**
     * True only when we should still auto-prompt for POST_NOTIFICATIONS: running on
     * Android 13+, the permission isn't granted, and we haven't already asked once.
     * Shared by every entry point (EntryActivity / HomeActivity / MainService) so the
     * prompt fires at most once across activity/process recreations.
     */
    public static boolean shouldAutoRequest(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return !PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_POST_NOTIFICATIONS_ASKED, false);
    }

    /** Record that the one automatic prompt has been issued. */
    public static void markAsked(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putBoolean(PREF_POST_NOTIFICATIONS_ASKED, true).apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {
            finish();
            return;
        }

        requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
                REQUEST_CODE_POST_NOTIFICATIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS
                && (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, R.string.notification_permission_denied_message, Toast.LENGTH_LONG).show();
        }

        finish();
    }
}
