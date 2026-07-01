/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.linkbubble.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

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
