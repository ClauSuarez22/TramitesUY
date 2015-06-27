package uy.edu.ucu.android.tramitesuy.controller;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;

import uy.edu.ucu.android.tramitesuy.R;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1;

    public MyBroadcastReceiver(){
        super();

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()){
            case MyIntentService.NOTIFICATION_STARTED: {
                break;
            }
            case MyIntentService.NOTIFICATION_PROGRESS: {
                int progress = intent.getIntExtra(MyIntentService.PROGRESS, 0);
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                mBuilder.setContentTitle("Download in progress")
                        .setContentText(progress + " % downloaded")
                        .setProgress(100, progress, false)
                        .setSmallIcon(R.mipmap.ic_launcher);
                notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                break;
            }
            case MyIntentService.NOTIFICATION_FINISHED: {

                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    int resultCode = bundle.getInt(MyIntentService.RESULT);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                    mBuilder.setContentTitle("Download finished")
                            .setSmallIcon(R.mipmap.ic_launcher);

                    if (resultCode == Activity.RESULT_OK) {
                        mBuilder.setContentText("Download complete");

                    } else {
                        mBuilder.setContentText("Download failed");
                    }
                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }
                break;

            }
        }


    }
}
