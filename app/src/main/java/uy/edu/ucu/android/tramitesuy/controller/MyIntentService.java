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
import java.util.ArrayList;
import java.util.List;

import uy.edu.ucu.android.parser.model.Category;
import uy.edu.ucu.android.parser.model.Location;
import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.parser.model.WhenAndWhere;
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

        /*
        //A continuación se obtienen todas las categorias a mostrar en el combobox
        EntitiesService entitiesService = new EntitiesService();

        Cursor categoryCursor = this.getContentResolver().query(
                ProceedingsContract.CategoryEntry.buildAllCategoryUri(),
                null,
                null,
                null,
                null);

        List<Category> allCategories = entitiesService.getAllCategories(categoryCursor);

        //En allCategories tengo la informacion de todas las categorias a listar
        //Se supone que en el combobox de categorias se guarda, ademas del Nombre de la categoria, tambien su ID
        //entonces el usuario clickea en la categoria que tiene id 1, por ejemplo.

        String categoryId = "1"; //-----> por esto esta este 1 acá

        //A continuacion vamos a buscar todos los tramites de la categoria 1

        String whereProceeding = ProceedingsContract.ProceedingEntry.COLUMN_CAT_KEY + " = ?";
        String[] whereArgsProceeding = {categoryId};

        Cursor proceedingCursor = this.getContentResolver().query(
                ProceedingsContract.ProceedingEntry.buildAllProceedingUri(),
                null,
                whereProceeding,
                whereArgsProceeding,
                null);

        //Acá obtengo todos los tramites de la categoria 1 que tengo que listar en la Pantalla Incial
        List<Proceeding> proceedings = entitiesService.getAllProceeding(proceedingCursor);

        //Se supone que cuando listo todos los tramites, me guardo como info "oculta" el ID de cada uno
        //Entonces se supone que el usuario selecciona el tramite que tiene id 1, por ejemplo.

        String proceedingId = "1"; //-----> por esto esta este 1 acá

        //A continuación obtengo toda la informacion a mostrar sobre ese tramite
        Proceeding proceeding = entitiesService.getProceedingById(proceedings, proceedingId);

        String whereLocation = ProceedingsContract.LocationEntry.COLUMN_PROC_KEY + " = ?";
        String[] whereArgsLocation = {proceeding.getId().toString()};

        Cursor locationCursor = this.getContentResolver().query(
                ProceedingsContract.LocationEntry.buildAllLocationUri(),
                null,
                whereLocation,
                whereArgsLocation,
                null);

        List<Location> locations = entitiesService.getAllLocations(locationCursor);
        proceeding.getWhenAndWhere().setLocations(locations);

        Category categoryProceeding = entitiesService.getCategoryById( allCategories, categoryId );
        List<Category> listCategories = new ArrayList<>();
        listCategories.add(categoryProceeding);
        proceeding.setCategories( listCategories );

        //Aca ya termine de obtener toda la informacion relevante sobre el tramite y esta pronto
        //para mostrarlo en la pantalla siguiente

        */

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
