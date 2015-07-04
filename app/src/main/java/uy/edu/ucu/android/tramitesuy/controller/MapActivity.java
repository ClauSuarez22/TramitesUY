package uy.edu.ucu.android.tramitesuy.controller;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

public class MapActivity extends AppCompatActivity
    implements OnMapReadyCallback,  LoaderManager.LoaderCallbacks<Cursor>,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int LOCATION_PROCEEDINGS = 0;

    private Integer mProceedingId;
    private GoogleMap mMap;

    private static final int REQUEST_CONN_FAILED = 1000;

    private TextView mCurrentLocationInfoText;

    private GoogleApiClient mGoogleApiClient;
    private MarkerOptions mMarkerOption;


    private android.location.Location mLastLocation;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MapFragment mapFragment = (MapFragment)getFragmentManager()
                .findFragmentById(R.id.locations_map);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if ( extras != null ) {
            mProceedingId = extras.getInt("proceedingId");
        }
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
        Uri baseUri = ProceedingsContract.LocationEntry.buildLocationProceeding(mProceedingId);
        return new CursorLoader(this, baseUri,
                null, null, null, null);
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
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Connected to Google Play services
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            mMarkerOption = new MarkerOptions()
                    .title("Posición Actual")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            updateLocation();
            createLocationRequest();
        }else{
            LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (android.location.LocationListener) this);
                Log.d("Network", "Network");
                if (locationManager != null) {
                    mLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    mMarkerOption = new MarkerOptions()
                            .title("Posición Actual")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    updateLocation();
                    //createLocationRequest();
                }
            }
        }
    }

    protected LocationRequest createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        mRequestingLocationUpdates = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        mRequestingLocationUpdates = true;
        LocationServices.FusedLocationApi.
                requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        mLastLocation = location;
        updateLocation();
    }

    private void updateLocation(){
        if (mLastLocation != null && mMap != null) {
            Marker m = mMap.addMarker(mMarkerOption);
            m.setPosition(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
        Log.d("MainActivity", "Connection Suspended by " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        Log.d("MapActivity", "Connection Failed by " + result.getErrorCode());
        if(result.hasResolution()){
            try{
                result.startResolutionForResult(this, REQUEST_CONN_FAILED);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                mGoogleApiClient.connect(); // try to connect again
            }
        }else {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, REQUEST_CONN_FAILED)
                    .show();
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data){
        super.onActivityResult(reqCode, resCode, data);

        if(reqCode == REQUEST_CONN_FAILED){
            if(resCode == RESULT_OK){
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }

    }

    private class GetAddressAsyncTask extends AsyncTask<Location, Integer, LatLng> {

        private Context mContext;
        private Location mLocationAddress;
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
            String address = params[0].getAddress() + "," + params[0].getCity() + "," + params[0].getState() + ",Uruguay";
            mLocationAddress = params[0];
            try{
                URL url = new URL(String.format(geocodingUrl, URLEncoder.encode(address, "UTF-8"), "AIzaSyBK4SZsUqTDjZutCSpTZxGjeYkfA1VOQSU"));
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();
                while (scanner.hasNextLine()){
                    sb.append(scanner.nextLine());
                }

                String response = sb.toString();
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
                String city = "";
                if(mLocationAddress.getCity() != null) {
                    city = mLocationAddress.getCity() + ", ";
                }
                if(mMap != null ){
                    if(mLastLocation != null){
                        float[] distance = new float[1];
                        android.location.Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                                address.latitude, address.longitude, distance);
                        Float distanceKm = distance[0] / 1000;
                        mMap.addMarker(new MarkerOptions()
                                .position(address)
                                .title(mLocationAddress.getAddress())
                                .snippet(city + mLocationAddress.getState() + ". Distancia: " + String.format("%.2f", distanceKm) + "Km." ));
                    }else{
                        mMap.addMarker(new MarkerOptions()
                                .position(address)
                                .title(mLocationAddress.getAddress())
                                .snippet(city + mLocationAddress.getState() ));
                    }
                }
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
