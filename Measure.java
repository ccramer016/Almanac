package com.example.callan.almanac;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;


public class Measure extends FragmentActivity implements OnMapReadyCallback {

    LocationManager locationManager;
    double longitudeGPS, latitudeGPS;
    TextView longitudeValueGPS, latitudeValueGPS, tvArea, tvAcquisition;
    Button btnStart, btnStop, btnDone;
    ArrayList<Double> longis = new ArrayList<>();
    ArrayList<Double> latis = new ArrayList<>();
    ArrayList<LatLng> shapePolygon = new ArrayList<>();
        EditText editText;
    double area;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measure);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        tvArea = (TextView) findViewById(R.id.tvArea);
        longitudeValueGPS = (TextView) findViewById(R.id.longitudeValueGPS);
        latitudeValueGPS = (TextView) findViewById(R.id.latitudeValueGPS);
        tvAcquisition = (TextView) findViewById(R.id.acquisition);
        btnStart = (Button) findViewById(R.id.locationControllerGPS);
        btnStop = (Button) findViewById(R.id.button);
        btnDone = (Button) findViewById(R.id.button2);
        btnStop.setEnabled(false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    public void exitToHome(View view){
        Intent i = new Intent(this, Home.class);
        i.putExtra("area", String.valueOf(area));
        startActivity(i);
    }

    public void cancel(View view){
        Intent i = new Intent(this, Home.class);
        startActivity(i);
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public double findArea (ArrayList<Double> ar1, ArrayList<Double> ar2){
        double sum1 = 0;
        double sum2 = 0;
        for (int i = 0; i < ar1.size()-1; i++) {
            sum1 = sum1 + ar1.get(i)*ar2.get(i+1);
        }

        sum1 = sum1 + ar1.get(ar1.size()-1)*ar2.get(0);

        for (int i = 0; i < ar2.size()-1; i++) {
            sum2 = sum2 + ar2.get(i)*ar1.get(i+1);
        }

        sum2 = sum2 + ar2.get(ar2.size()-1)*ar1.get(0);
        return Math.abs(sum1-sum2) / 2 * 1.23 * Math.pow(10, 6) * 2.47;
    }

    public void endGPS(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                      return;
        }
        locationManager.removeUpdates(locationListenerGPS);
        longitudeValueGPS.setText("0.0000");
        latitudeValueGPS.setText("0.0000");
        btnDone.setVisibility(View.VISIBLE);
        area = findArea(longis, latis);
        tvArea.setText(String.valueOf(area));
        drawArea(shapePolygon);
    }

    public void drawArea (ArrayList<LatLng> ar1){
        mMap.addPolygon(new PolygonOptions()
                .addAll(ar1)
                .fillColor(Color.parseColor("#3bb2d0")));
    }

    public void getGPS(View view) {
         if (!checkLocation())
            return;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, locationListenerGPS);
        tvAcquisition.setVisibility(View.VISIBLE);
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueGPS.setText(longitudeGPS + "");
                    latitudeValueGPS.setText(latitudeGPS + "");
                    if(longitudeGPS!=0){
                        longis.add(longitudeGPS);
                        tvAcquisition.setVisibility(View.GONE);
                        LatLng temp = new LatLng(latitudeGPS, longitudeGPS);
                        shapePolygon.add(temp);
                        float zoomLevel = (float) 12.0;
                        mMap.addMarker(new MarkerOptions().position(temp));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp, zoomLevel));
                    }
                    if(latitudeGPS!=0){
                        latis.add(latitudeGPS);
                    }
                    Toast.makeText(Measure.this, "GPS Provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
