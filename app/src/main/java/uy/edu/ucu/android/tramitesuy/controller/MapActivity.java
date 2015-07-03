package uy.edu.ucu.android.tramitesuy.controller;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import uy.edu.ucu.android.parser.model.Location;
import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;

public class MapActivity extends FragmentActivity
    implements OnMapReadyCallback,  LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOCATION_PROCEEDINGS = 0;
    String[] LOCATION_PROCEEDING = new String[] {
            ProceedingsContract.LocationEntry._ID,
            ProceedingsContract.LocationEntry.COLUMN_IS_URUGUAY,
            ProceedingsContract.LocationEntry.COLUMN_CITY,
            ProceedingsContract.LocationEntry.COLUMN_STATE,
            ProceedingsContract.LocationEntry.COLUMN_ADDRESS,
            ProceedingsContract.LocationEntry.COLUMN_COMMENTS,
            ProceedingsContract.LocationEntry.COLUMN_PHONE,
            ProceedingsContract.LocationEntry.COLUMN_TIME};

    private Integer mProceedingId;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MapFragment mapFragment = (MapFragment)getFragmentManager()
                .findFragmentById(R.id.locations_map);
        mapFragment.getMapAsync(this);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Sucursales");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if ( extras != null ) {
            mProceedingId = extras.getInt("proceedingId");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getSupportLoaderManager().initLoader(LOCATION_PROCEEDINGS, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = ProceedingsContract.LocationEntry.CONTENT_URI;
        String whereProceeding = ProceedingsContract.LocationEntry.COLUMN_PROC_KEY + " = ?";
        String[] whereArgsProceeding = {mProceedingId.toString()};
        return new CursorLoader(this, baseUri,
                LOCATION_PROCEEDING, whereProceeding, whereArgsProceeding, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null){
            data.moveToFirst();
            while (!data.isAfterLast()) {
                Location location = getLocation(data);
                new GetAddressAsyncTask(this)
                    .execute(location);
                data.moveToNext();
            }
            data.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class GetAddressAsyncTask extends AsyncTask<Location, Integer, LatLng> {

        private Context mContext;
        private String mAddress;
        private static final String geocodingUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s";

        public GetAddressAsyncTask(Context context){
            this.mContext = context;
        }

        @Override
        public void onPreExecute(){

        }

        @Override
        protected LatLng doInBackground(Location... params) {
            HttpURLConnection connection = null;
            LatLng cordinates = null;
            mAddress = params[0].getAddress() + "," + params[0].getCity() + "," + params[0].getState();
            try{
                URL url = new URL(String.format(geocodingUrl, URLEncoder.encode(mAddress, "UTF-8"), "AIzaSyBK4SZsUqTDjZutCSpTZxGjeYkfA1VOQSU"));
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();
                while (scanner.hasNextLine()){
                    sb.append(scanner.nextLine());
                }

                String response = sb.toString();
                Log.d(MapActivity.class.getSimpleName(), response);
                JSONArray jsonArray = new JSONObject(response).getJSONArray("results");
                if(jsonArray != null){
                    if(jsonArray.length() > 0){
                        JSONObject addressItem = jsonArray.getJSONObject(0);
                        JSONObject location = addressItem.getJSONObject("geometry").getJSONObject("location");
                        cordinates = new LatLng(location.getDouble("lat"),location.getDouble("lng"));
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                if(connection != null) connection.disconnect();
            }
            return cordinates;
        }

        @Override
        public void onPostExecute(LatLng address){
            if(address != null) {
                if(mMap != null ){
                    mMap.addMarker(new MarkerOptions()
                     .position(address)
                     .title(mAddress));
                }
            }
            else{
                Toast.makeText(mContext, "Error cargando sucursal para hacer el trámite", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static Location getLocation(Cursor cursor) {
        Location location = new Location();
        location.setAddress(cursor.getString(cursor.getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_ADDRESS)));
        location.setCity(cursor.getString(cursor.getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_CITY)));
        location.setComments(cursor.getString(cursor.getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_COMMENTS)));
        location.setIsUruguay(cursor.getString(cursor.getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_IS_URUGUAY)));
        location.setPhone(cursor.getString(cursor.getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_PHONE)));
        location.setState(cursor.getString(cursor.getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_STATE)));
        location.setTime(cursor.getString(cursor.getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_TIME)));
        return location;
    }
}
