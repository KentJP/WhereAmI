package com.easv.oe.whereami;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements IViewCallBack{

    public static String TAG = "qwerty";

    private int PERMISSION_REQUEST_CODE = 4;

    Switch swListening;
    Button btnLastKnownLoc;

    TextView currentLoc;
    TextView counter;
    TextView speed;

    LocationListener locListener;

    LocationManager locationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentLoc = findViewById(R.id.currentLoc);
        counter = findViewById(R.id.counter);
        speed = findViewById(R.id.txtSpeed);

        swListening = findViewById(R.id.swListening);

        swListening.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (swListening.isChecked())
                    MainActivity.this.startListening();
                else
                    MainActivity.this.stopListening();
            }
        });

        btnLastKnownLoc = findViewById(R.id.whereami);

        btnLastKnownLoc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainActivity.this.showLastKnownLocation();
            }
        });

        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locListener = null;

        requestGPSPermissions();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        stopListening();
        if (swListening.isChecked())
            swListening.setChecked(false);
    }

    protected void showLastKnownLocation() {
        Location location = lastKnownLocation();
        if (location == null)
        {
            Toast.makeText(getApplicationContext(), "Last known location is null",
                    Toast.LENGTH_LONG).show();
            return;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String msg = "Last known loc = Latitude: " + latitude + "\n" + "Longitude: "
                + longitude;

        currentLoc.setText(msg);

    }

    private void requestGPSPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d(TAG, "permission denied to USE GPS - requesting it");
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                return;

            } else
                Log.d(TAG, "permission to USE GPS granted!");
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode,
                                            String[] permissions,
                                            int[] grantResults)
    {
        Log.d(TAG, "Permission: " + permissions[0] + " - grantResult: " + grantResults[0]);
    }

    private Location lastKnownLocation() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                return locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        else {
            return locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }
        return null;
    }

    protected void startListening() {

        Log.d(TAG, "Start listening");
        locListener = new MyLocationListener(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000, // the minimal time (ms) between notifications
                        0, // the minimal distance (meter) between notifications
                        locListener);
            }
            else
            {
                Log.d(TAG, "Could not start listening - GPS permission not granted");

            }
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, // the minimal time (ms) between notifications
                    0, // the minimal distance (meter) between notifications
                    locListener);

        }
    }

    private void stopListening()
    {
        Log.d(TAG, "Stop listening");

        if (locListener == null) return;


        locationManager.removeUpdates(locListener);
    }

    private NumberFormat formatter = new DecimalFormat("#.##");

    // IViewCallBack

    public void setCurrentLocation(Location loc)
    {
        String latitude = formatter.format(loc.getLatitude());
        String longitude = formatter.format(loc.getLongitude());
        currentLoc.setText("Current Loc = Latitude: " + latitude + " Longitude: " + longitude);
    }

    public void setSpeed(double speedValue)
    { speed.setText("Speed = " + formatter.format(speedValue));}

    public void setCounter(int c)
    {
        counter.setText("Count = " + c);
    }





}
