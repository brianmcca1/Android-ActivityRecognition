package com.tutsplus.activityrecognition;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public GoogleApiClient mApiClient;
    private Location lastLocation;
    private LocationCallback mLocationCallback;
    private PendingIntent pendingIntent;
    private static final String LIBRARY_REQ_ID = "LIBRARY";
    private static final String FULLER_REQ_ID = "FULLER";
    private static final float GEOFENCE_RADIUS = 500.0f; // in meters
    private PendingIntent geoFencePendingIntent;
    private final int LIBRARY_REQ_CODE = 0;
    private final int FULLER_REQ_CODE = 1;
    private StepReceiver stepReceiver;
    int speed = 0;//0 is still, 1 is walking and 2 is running
    int fullerCount = 0;
    int libraryCount = 0;
    TextView moveText;
    ImageView moveImage;
    TextView fullerVisits;
    TextView libraryVisits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moveText = (TextView) findViewById(R.id.MovementText);
        moveImage = (ImageView) findViewById(R.id.MovementImage);
        fullerVisits = (TextView) findViewById(R.id.FullerVisits);
        libraryVisits = (TextView) findViewById(R.id.LibraryVisits);

        fullerVisits.setText("Visits to Fuller labs geoFence: " + fullerCount);
        libraryVisits.setText("Visits to Library geoFence: " + libraryCount);
        final Button StillButton = (Button) findViewById(R.id.StillButton);
        StillButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                speed = 0;
                moveText.setText(R.string.move_still);
                moveImage.setImageResource(R.mipmap.still);
            }
        });

        final Button WalkButton = (Button) findViewById(R.id.WalkButton);
        WalkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                speed = 1;
                moveText.setText(R.string.move_walk);
                moveImage.setImageResource(R.mipmap.walking);
            }
        });

        final Button RunButton = (Button) findViewById(R.id.RunButton);
        RunButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                speed = 2;
                moveText.setText(R.string.move_run);
                moveImage.setImageResource(R.mipmap.running);
            }
        });

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addApi (LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        this.startGeofence(LIBRARY_REQ_ID);
        this.startGeofence(FULLER_REQ_ID);
        mApiClient.connect();
    }
    @Override
    public void onStart(){
        super.onStart();
        stepReceiver = new StepReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(StepsService.MIN_STEPS_TRIGGER_ACTION);
        registerReceiver(stepReceiver, intentFilter);
    }

    @Override
    public void onStop(){
        super.onStop();
        unregisterReceiver(stepReceiver);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent( this, ActivityRecognizedService.class );
        pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 3000, pendingIntent );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Get last known location
    private void getLastKnownLocation() {

        if ( checkPermission() ) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
            if ( lastLocation != null ) {

                startLocationUpdates();
            } else {
                startLocationUpdates();
            }
        }

    }

    private LocationRequest locationRequest;
    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL =  1000;
    private final int FASTEST_INTERVAL = 900;

    // Start location Updates
    private void startLocationUpdates(){

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, locationRequest, pendingIntent);
    }

    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
        return;
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    @Override
    public void onProviderEnabled(String provider){
        return;
    }

    @Override
    public void onProviderDisabled(String provider){
        return;
    }

    // Create a Geofence
    private Geofence createGeofence(LatLng latLng, float radius, String req ) {
        return new Geofence.Builder()
                .setRequestId(req)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest(Geofence geofence ) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }


    private PendingIntent createGeofencePendingIntent(int reqCode) {
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( this, GeofenceTransitionService.class);
        return PendingIntent.getService(
                this, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request, int reqCode) {

        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    mApiClient,
                    request,
                    createGeofencePendingIntent(reqCode)
            );
    }

    // Start Geofence creation process
    private void startGeofence(String req) {
        LatLng location = null;
        int reqCode = 0;
        switch(req){
            case(LIBRARY_REQ_ID):
                location = new LatLng(42.274213, -71.806352);
                reqCode = LIBRARY_REQ_CODE;
                break;
            case(FULLER_REQ_ID):
                location = new LatLng(42.275042, -71.806391);
                reqCode = FULLER_REQ_CODE;
                break;

        }
        Geofence geofence = createGeofence( location, GEOFENCE_RADIUS, req );
        GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
        addGeofence( geofenceRequest, reqCode );
    }

    private class StepReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
            // So we should communicate with MainActivity to increment the count of the relevant geofence,
            // and make the appropriate Toast
            if (!intent.getBooleanExtra(StepsService.INTENT_EXTRA_TRIGGER_FLAG, false)) {
                return;
            }
            String geofenceId = intent.getStringExtra(StepsService.INTENT_EXTRA_GEOFENCE_REQ_ID);
            String location;
            if(geofenceId.equals(FULLER_REQ_ID)){
                location = "Fuller Labs";
                fullerCount++;
                fullerVisits.setText("Visits to Fuller labs geoFence: " + fullerCount);
            } else {
                location = "Gordon Library";
                libraryCount++;
                libraryVisits.setText("Visits to Library geoFence: " + libraryCount);
            }
            Toast.makeText(getParent(),
                    "You have taken 6 steps inside the " + location + "Geofence, incrementing counter",
                    Toast.LENGTH_LONG).show();
        }
    }


}
