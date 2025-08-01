/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.linkbubble;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.linkbubble.ui.NotificationCloseAllActivity;
import com.linkbubble.ui.NotificationHideActivity;
import com.linkbubble.ui.NotificationUnhideActivity;
import com.linkbubble.util.Analytics;
import com.squareup.otto.Subscribe;
import java.util.Vector;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class MainService extends Service {

    private static final String BCAST_CONFIGCHANGED = "android.intent.action.CONFIGURATION_CHANGED";
    private static final String OPENED_URL_FROM_RESTORE = "restore";

    private boolean mRestoreComplete;

    public static class ShowDefaultNotificationEvent {
    }

    public static class ShowUnhideNotificationEvent {
    }

    public static class OnDestroyMainServiceEvent {}

    public static class ReloadMainServiceEvent {
        public ReloadMainServiceEvent(Context context) {
            mContext = context;
        }

        public Context mContext;
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String cmd = intent != null ? intent.getStringExtra("cmd") : null;

        MainController mainController = MainController.get();
        if (mainController == null || intent == null || cmd == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        long urlLoadStartTime = intent.getLongExtra("start_time", System.currentTimeMillis());
        if (cmd.compareTo("open") == 0) {
            String url = intent.getStringExtra("url");
            if (url != null) {
                String openedFromAppName = intent.getStringExtra("openedFromAppName");
                mainController.openUrl(url, urlLoadStartTime, true, openedFromAppName);
            }
        } else if (cmd.compareTo("restore") == 0) {
            if (!mRestoreComplete) {
                String [] urls = intent.getStringArrayExtra("urls");
                if (urls != null) {
                    int startOpenTabCount = mainController.getActiveTabCount();

                    for (int i = 0; i < urls.length; i++) {
                        String urlAsString = urls[i];
                        if (urlAsString != null && !urlAsString.equals(Constant.WELCOME_MESSAGE_URL)) {
                            boolean setAsCurrentTab = false;
                            if (startOpenTabCount == 0) {
                                setAsCurrentTab = i == urls.length - 1;
                            }

                            mainController.openUrl(urlAsString, urlLoadStartTime, setAsCurrentTab, OPENED_URL_FROM_RESTORE);
                        }
                    }
                }
                mRestoreComplete = true;
            }
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        mRestoreComplete = false;

        setTheme(Settings.get().getDarkThemeEnabled() ? R.style.MainServiceThemeDark : R.style.MainServiceThemeLight);

        super.onCreate();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            startMyOwnForeground();
//        else
//            startForeground(1, new Notification());



        showDefaultNotification();

        Config.init(this);
        Settings.get().onOrientationChange();



        MainController.create(this, new MainController.EventHandler() {
                @Override
                public void onDestroy() {
                    Settings.get().saveBubbleRestingPoint();
                    stopSelf();
                }
            });

        //Intent i = new Intent();
        //i.setData(Uri.parse("https://t.co/uxMl3bWtMP"));
        //i.setData(Uri.parse("http://t.co/oOyu7GBZMU"));
        //i.setData(Uri.parse("http://goo.gl/abc57"));
        //i.setData(Uri.parse("https://bitly.com/QtQET"));
        //i.setData(Uri.parse("http://www.duckduckgo.com"));
        //openUrl("https://www.duckduckgo.com");
        //openUrl("http://www.duckduckgo.com", true);
        //openUrl("https://t.co/uxMl3bWtMP", true);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BCAST_CONFIGCHANGED);
        registerReceiver(mBroadcastReceiver, filter);

        registerReceiver(mDialogReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");

        filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenReceiver, filter);

    }

//    public Notification getNotification(PendingIntent hidePendingIntent) {
//        String channel;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            channel = createChannel();
//        else {
//            channel = "";
//        }
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel)
//                .setSmallIcon(R.drawable.ic_stat)
//                .setContentTitle(getString(R.string.app_name));
//        Notification notification = mBuilder
//                .setSmallIcon(R.drawable.ic_stat)
//                .setPriority(Notification.PRIORITY_MIN)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText(getString(R.string.notification_default_summary))
//                //.addAction(R.drawable.ic_action_eye_closed_dark, getString(R.string.notification_action_hide), hidePendingIntent)
//                //.addAction(R.drawable.ic_action_cancel_dark, getString(R.string.notification_action_close_all), closeAllPendingIntent)
//                .addAction(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT ? R.drawable.ic_action_cancel_white : R.drawable.ic_action_cancel_dark, getString(R.string.notification_action_close_all), closeAllPendingIntent)
//                .setGroup(Constant.NOTIFICATION_GROUP_KEY_ARTICLES)
//                .setGroupSummary(true)
//                .setLocalOnly(true)
//                .setContentIntent(hidePendingIntent)
//                .build();
//
//
//        return notification;
//    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = "snap map fake location ";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel("snap map channel", name, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return "snap map channel";
    }

    /** code to post/handler request for permission */
    public final static int REQUEST_CODE = 65535;/*(see edit II)*/




    @Override
    public void onDestroy() {
        MainApplication.postEvent(MainService.this, new OnDestroyMainServiceEvent());
        MainApplication.unregisterForBus(this, this);
        unregisterReceiver(mScreenReceiver);
        unregisterReceiver(mDialogReceiver);
        unregisterReceiver(mBroadcastReceiver);
        MainController.destroy();
        super.onDestroy();
    }

    private void cancelCurrentNotification() {
        stopForeground(true);
        //Log.d("blerg", "cancelCurrentNotification()");
    }

    private void showDefaultNotification() {
        Intent closeAllIntent = new Intent(this, NotificationCloseAllActivity.class);
        closeAllIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent closeAllPendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), closeAllIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent hideIntent = new Intent(this, NotificationHideActivity.class);
        hideIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent hidePendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), hideIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_stat)
                .setContentTitle(getString(R.string.app_name));
        Notification notification = mBuilder
                .setSmallIcon(R.drawable.ic_stat)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_default_summary))
                //.addAction(R.drawable.ic_action_eye_closed_dark, getString(R.string.notification_action_hide), hidePendingIntent)
                //.addAction(R.drawable.ic_action_cancel_dark, getString(R.string.notification_action_close_all), closeAllPendingIntent)
                .addAction(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT ? R.drawable.ic_action_cancel_white : R.drawable.ic_action_cancel_dark, getString(R.string.notification_action_close_all), closeAllPendingIntent)
                .setGroup(Constant.NOTIFICATION_GROUP_KEY_ARTICLES)
                .setGroupSummary(true)
                .setLocalOnly(true)
                .setContentIntent(hidePendingIntent)
                .build();



        /*
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_default_summary))
                //.addAction(R.drawable.ic_action_eye_closed_dark, getString(R.string.notification_action_hide), hidePendingIntent)
                //.addAction(R.drawable.ic_action_cancel_dark, getString(R.string.notification_action_close_all), closeAllPendingIntent)
                .addAction(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT ? R.drawable.ic_action_cancel_white : R.drawable.ic_action_cancel_dark, getString(R.string.notification_action_close_all), closeAllPendingIntent)
                .setGroup(Constant.NOTIFICATION_GROUP_KEY_ARTICLES)
                .setGroupSummary(true)
                .setLocalOnly(true)
                .setContentIntent(hidePendingIntent);
                */

        // Nuke all previous notifications
        NotificationManagerCompat.from(this).cancel(NotificationUnhideActivity.NOTIFICATION_ID);
        NotificationManagerCompat.from(this).cancel(NotificationHideActivity.NOTIFICATION_ID);

        startForeground(NotificationHideActivity.NOTIFICATION_ID, notification);
    }

    private void showUnhideHiddenNotification() {
        Intent unhideIntent = new Intent(this, NotificationUnhideActivity.class);
        unhideIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent unhidePendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), unhideIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent closeAllIntent = new Intent(this, NotificationCloseAllActivity.class);
        closeAllIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent closeAllPendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), closeAllIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_unhide_summary))
                .setLocalOnly(true)
                //.addAction(R.drawable.ic_action_eye_open_dark, getString(R.string.notification_action_unhide), unhidePendingIntent)
                //.addAction(R.drawable.ic_action_cancel_dark, getString(R.string.notification_action_close_all), closeAllPendingIntent)
                .addAction(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT ? R.drawable.ic_action_cancel_white : R.drawable.ic_action_cancel_dark, getString(R.string.notification_action_close_all), closeAllPendingIntent)
                .setContentIntent(unhidePendingIntent);

        // Nuke all previous notifications
        NotificationManagerCompat.from(this).cancel(NotificationUnhideActivity.NOTIFICATION_ID);
        NotificationManagerCompat.from(this).cancel(NotificationHideActivity.NOTIFICATION_ID);
        startForeground(NotificationUnhideActivity.NOTIFICATION_ID, notificationBuilder.build());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onShowDefaultNotificationEvent(ShowDefaultNotificationEvent event) {
        cancelCurrentNotification();
        showDefaultNotification();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onShowUnhideNotificationEvent(ShowUnhideNotificationEvent event) {
        cancelCurrentNotification();
        showUnhideHiddenNotification();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onReloadMainServiceEvent(ReloadMainServiceEvent event) {
        stopSelf();

        final Vector<String> urls = Settings.get().loadCurrentTabs();
        MainApplication.restoreLinks(event.mContext, urls.toArray(new String[urls.size()]));
    }


    private BroadcastReceiver mDialogReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent myIntent) {
            if (myIntent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                MainController.get().onCloseSystemDialogs();
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent myIntent) {
            if ( myIntent.getAction().equals( BCAST_CONFIGCHANGED ) ) {
                MainController.get().onOrientationChanged();
            }
        }
    };

    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainController.get().updateScreenState(intent.getAction());
        }
    };

    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            return android.provider.Settings.canDrawOverlays(context);
        } else {
            if (android.provider.Settings.canDrawOverlays(context)) return true;
            try {
                WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                if (mgr == null) return false; //getSystemService might return null
                View viewToAdd = new View(context);
                WindowManager.LayoutParams params = new WindowManager.LayoutParams(0, 0, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
                viewToAdd.setLayoutParams(params);
                mgr.addView(viewToAdd, params);
                mgr.removeView(viewToAdd);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
