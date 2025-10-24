package com.linkbubble;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.linkbubble.util.NotificationPermissionHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

@RunWith(RobolectricTestRunner.class)
public class MainApplicationForegroundServiceTest {

    private RecordingContext mContext;

    @Before
    public void setUp() {
        Context base = ApplicationProvider.getApplicationContext();
        Settings.initModule(base);
        NotificationPermissionHelper.clearPermanentlyDeniedFlag();
        mContext = new RecordingContext(base);
    }

    @After
    public void tearDown() {
        Settings.deinitModule();
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.O)
    public void openLink_usesStartForegroundServiceOnOreoAndAbove() {
        boolean started = MainApplication.openLink(mContext, "http://example.com", null);

        assertTrue(started);
        assertTrue(mContext.startForegroundServiceCalled);
        assertFalse(mContext.startServiceCalled);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N_MR1)
    public void openLink_usesStartServiceBelowOreo() {
        boolean started = MainApplication.openLink(mContext, "http://example.com", null);

        assertTrue(started);
        assertFalse(mContext.startForegroundServiceCalled);
        assertTrue(mContext.startServiceCalled);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.TIRAMISU)
    public void openLink_requiresNotificationPermissionOnTiramisu() {
        boolean started = MainApplication.openLink(mContext, "http://example.com", null);

        assertFalse(started);
        assertFalse(mContext.startForegroundServiceCalled);
        assertFalse(mContext.startServiceCalled);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.TIRAMISU)
    public void openLink_startsForegroundServiceWhenPermissionGrantedOnTiramisu() {
        ShadowApplication.getInstance().grantPermissions(Manifest.permission.POST_NOTIFICATIONS);

        boolean started = MainApplication.openLink(mContext, "http://example.com", null);

        assertTrue(started);
        assertTrue(mContext.startForegroundServiceCalled);
        assertFalse(mContext.startServiceCalled);
    }

    private static class RecordingContext extends ContextWrapper {
        boolean startServiceCalled;
        boolean startForegroundServiceCalled;

        RecordingContext(Context base) {
            super(base);
        }

        @Override
        public ComponentName startService(Intent service) {
            startServiceCalled = true;
            return service.getComponent();
        }

        @Override
        public ComponentName startForegroundService(Intent service) {
            startForegroundServiceCalled = true;
            return service.getComponent();
        }
    }
}
