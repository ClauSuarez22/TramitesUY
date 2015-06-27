package uy.edu.ucu.android.tramitesuy.controller;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;
import uy.edu.ucu.android.tramitesuy.util.Utils;

public class MyIntentService extends IntentService {

    public static final String NOTIFICATION_STARTED = "uy.edu.ucu.android.servicedemo.service.receiver.STARTED";
    public static final String NOTIFICATION_PROGRESS= "uy.edu.ucu.android.servicedemo.service.receiver.PROGRESS";
    public static final String NOTIFICATION_FINISHED = "uy.edu.ucu.android.servicedemo.service.receiver.FINISHED";
    public static final String PROGRESS = "PROGRESS";
    public static final String RESULT = "RESULT";

    private final String FILE_URL =  "http://api.androidhive.info/progressdialog/hive.jpg";
    private final String FILE_NAME = "hive.jpg";

    private static final int NOTIFICATION_ID = 1;

    public MyIntentService(){
        super("MyIntentService");
    }

    public MyIntentService(String name) {
        super(name); // worker thread name
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int result;

        try {
            publishProgress(0);
            Utils.loadProceedings(getApplicationContext());
            result = Activity.RESULT_OK;

        } catch (Exception e) {
            e.printStackTrace();

            result = Activity.RESULT_CANCELED;
        } finally {

        }

        publishResults(result);

    }

    private void publishProgress(int progress) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Cargando trámites...")
                .setContentText(progress + " % downloaded")
                .setProgress(100, progress, false)
                .setSmallIcon(R.mipmap.ic_launcher);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    private void publishResults(int result) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Carga finalizada")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, ResultsActivity.class), 0));
        if(result == Activity.RESULT_OK)
            mBuilder.setContentText("Carga completa");
        else
            mBuilder.setContentText("Error en la carga");

//        // checking if category already exists
//        Cursor categoryCursor = context.getContentResolver().query(
//                ProceedingsContract.CategoryEntry.build....,
//                null,
//                null,
//                null,
//                null);

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
