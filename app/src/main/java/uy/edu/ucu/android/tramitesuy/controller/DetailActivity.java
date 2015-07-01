package uy.edu.ucu.android.tramitesuy.controller;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import uy.edu.ucu.android.parser.model.Category;
import uy.edu.ucu.android.parser.model.Location;
import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;

public class DetailActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Detalle de tramite");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if ( extras != null ) {
            String proceedingId = extras.getString("proceedingId");
            String categoryId = extras.getString("categoryId");

            Proceeding proceeding = EntitiesService.getProceedingById(EntitiesService.categoryProceeding, proceedingId);

            String whereLocation = ProceedingsContract.LocationEntry.COLUMN_PROC_KEY + " = ?";
            String[] whereArgsLocation = {proceeding.getId().toString()};

            Cursor locationCursor = this.getContentResolver().query(
                    ProceedingsContract.LocationEntry.buildAllLocationUri(),
                    null,
                    whereLocation,
                    whereArgsLocation,
                    null);

            List<Location> locations = EntitiesService.getAllLocations(locationCursor);
            proceeding.getWhenAndWhere().setLocations(locations);

            Category categoryProceeding = EntitiesService.getCategoryById( EntitiesService.categories, categoryId );
            List<Category> listCategories = new ArrayList<>();
            listCategories.add(categoryProceeding);
            proceeding.setCategories( listCategories );

            Button button = (Button) findViewById(R.id.ver_mapa);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(DetailActivity.this, MapActivity.class);
                    intent.putExtra("proceedingId", getIntent().getExtras().getString("proceedingId"));
                    startActivity(intent);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Intent homeIntent = new Intent(this, MainActivity.class);
                //startActivity(homeIntent);
                finish();
                break;
            default:
                break;
        }

        return true;
    }
}
