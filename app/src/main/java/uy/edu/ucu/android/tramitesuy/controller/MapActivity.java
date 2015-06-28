package uy.edu.ucu.android.tramitesuy.controller;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.tramitesuy.R;

public class MapActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Bundle extras = getIntent().getExtras();
        if ( extras != null ) {
            String proceedingId = extras.getString("proceedingId");

            //Aca se obtiene el procedimiento en cuestion, ver atributo WhereAndWhen para cargar datos en mapa
            Proceeding proceeding = EntitiesService.getProceedingById(EntitiesService.categoryProceeding, proceedingId);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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
}
