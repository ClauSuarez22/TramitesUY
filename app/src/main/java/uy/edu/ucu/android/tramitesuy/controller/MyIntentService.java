package uy.edu.ucu.android.tramitesuy.controller;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

        File output = new File(Environment.getExternalStorageDirectory(),
                FILE_NAME);
        if (output.exists()) {
            output.delete();
        }

        HttpURLConnection connection;
        InputStream stream = null;
        FileOutputStream fos = null;
        try {

            URL url = new URL(FILE_URL);
            connection = (HttpURLConnection) url.openConnection();
            stream = connection.getInputStream();
            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(stream, 8192);
            byte data[] = new byte[1024];
            fos = new FileOutputStream(output.getPath());
            int next;
            int fileSize = connection.getContentLength();
            long downloaded = 0;
            publishProgress(0);
            while ((next = input.read(data)) != -1) {
                downloaded += next;
                if(downloaded % 5 == 0){
                    publishProgress((int)(downloaded * 100) / fileSize);
                }
                fos.write(data, 0, next);
            }

            result = Activity.RESULT_OK;

        } catch (Exception e) {
            e.printStackTrace();

            result = Activity.RESULT_CANCELED;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        publishResults(result);

    }

    private void publishProgress(int progress) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Download in progress")
                .setContentText(progress + " % downloaded")
                .setProgress(100, progress, false)
                .setSmallIcon(R.mipmap.ic_launcher);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    private void publishResults(int result) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Download finished")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, ResultsActivity.class), 0));
        if(result == Activity.RESULT_OK)
            mBuilder.setContentText("Download complete");
        else
            mBuilder.setContentText("Download failed");

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
