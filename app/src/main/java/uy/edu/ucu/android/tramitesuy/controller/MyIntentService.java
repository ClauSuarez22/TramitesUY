package uy.edu.ucu.android.tramitesuy.controller;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.util.Utils;

public class MyIntentService extends IntentService {

    public static final String NOTIFICATION_STARTED = "uy.edu.ucu.android.tramitesuy.service.receiver.STARTED";
    public static final String NOTIFICATION_PROGRESS= "uy.edu.ucu.android.tramitesuy.service.receiver.PROGRESS";
    public static final String NOTIFICATION_FINISHED = "uy.edu.ucu.android.tramitesuy.service.receiver.FINISHED";
    public static final String PROGRESS = "PROGRESS";
    public static final String RESULT = "RESULT";

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
            Utils.loadProceedings(getApplicationContext());
            result = Activity.RESULT_OK;

        } catch (Exception e) {
            e.printStackTrace();

            result = Activity.RESULT_CANCELED;
        } finally {

        }

        publishResults(result);

    }

    private void publishResults(int result) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("TrámitesUY")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, ProceedingsListDetailActivity.class), 0));
        if(result == Activity.RESULT_OK)
            mBuilder.setContentText("Se completó la sincronización de datos");
        else
            mBuilder.setContentText("Error en la sincronización de datos");

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Intent intent = new Intent(MyIntentService.this, ProceedingsListDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
