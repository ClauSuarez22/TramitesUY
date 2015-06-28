package uy.edu.ucu.android.tramitesuy.controller;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import uy.edu.ucu.android.parser.model.Category;
import uy.edu.ucu.android.parser.model.Location;
import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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
