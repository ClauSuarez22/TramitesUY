package uy.edu.ucu.android.tramitesuy.controller;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.List;

import uy.edu.ucu.android.parser.model.Category;
import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;
import uy.edu.ucu.android.tramitesuy.util.Utils;


public class Login extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        runIntentService();

        //Carga todas las categorias

        Cursor categoryCursor = this.getContentResolver().query(
                ProceedingsContract.CategoryEntry.buildAllCategoryUri(),
                null,
                null,
                null,
                null);

        List<Category> allCategories = EntitiesService.getAllCategories(categoryCursor);

        //El usuario selecciona la categoria 1

        final String categoryId = "1";

        String whereProceeding = ProceedingsContract.ProceedingEntry.COLUMN_CAT_KEY + " = ?";
        String[] whereArgsProceeding = {categoryId};

        Cursor proceedingCursor = this.getContentResolver().query(
                ProceedingsContract.ProceedingEntry.buildAllProceedingUri(),
                null,
                whereProceeding,
                whereArgsProceeding,
                null);

        List<Proceeding> proceedings = EntitiesService.getAllProceeding(proceedingCursor);

        Button button = (Button) findViewById(R.id.ver_detalle);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //El usuario selecciona el tramite 1
                String proceedingId = "1";
                String categId = categoryId;
                Intent intent = new Intent(Login.this, DetailActivity.class);
                intent.putExtra("proceedingId", proceedingId);
                intent.putExtra("categoryId", categId);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void runIntentService() {

        Intent serviceIntent = new Intent(this, MyIntentService.class);
        startService(serviceIntent);

    }
}
