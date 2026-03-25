package com.parth.raktsaathi;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parth.raktsaathi.databinding.ActivityMyLocationBinding;

import java.io.IOException;
import java.util.List;

public class MyLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMyLocationBinding binding;
    LocationManager locationManager;

    public static final int REQUEST_LOCATION_PERMISSION = 1;
    double latitude, longitude;
    String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MyLocationActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MyLocationActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else if(ActivityCompat.checkSelfPermission(MyLocationActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MyLocationActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_LOCATION_PERMISSION);
        }
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            //geocoder class converts latitude and longitude to address
                            Geocoder geocoder = new Geocoder(MyLocationActivity.this);

                            try {
                                List<Address> addressesList = geocoder.getFromLocation(latitude, longitude, 1);
                                address = addressesList.get(0).getAddressLine(0) + "," +
                                        addressesList.get(0).getLocality() + "," + addressesList.get(0).getCountryName();

                                LatLng mycurrentLocation = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(mycurrentLocation).title(address));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mycurrentLocation, 16)
                                        , 5000, null);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

            );
        }
        else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,5000,
                    10,
                    new LocationListener(){
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            Geocoder geocoder1 = new Geocoder(MyLocationActivity.this);
                            try {
                                List<Address> addressesList1 = geocoder1.getFromLocation(latitude, longitude, 1);
                                address = addressesList1.get(0).getAddressLine(0) + "," +
                                        addressesList1.get(0).getLocality() + "," + addressesList1.get(0).getCountryName();

                                LatLng mycurrentLocation = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(mycurrentLocation).title(address));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mycurrentLocation, 16)
                                        , 5000, null);


                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    });
            }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*
        LatLng MyLocation= new LatLng(20.689677,77.006122 );
        mMap.addMarker(new MarkerOptions().position(MyLocation).title("Sindhi Colony"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(MyLocation));
     //   mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MyLocation,15));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MyLocation,16),2000,null);
        mMap.addCircle(new CircleOptions()
                .center(MyLocation)
                .fillColor(Color.parseColor("#0800EA"))
                .strokeColor(Color.parseColor("#000000"))
                .radius(150));


        LatLng mkcity = new LatLng(20.689677,77.006122 );
        mMap.addMarker(new MarkerOptions().position(mkcity).title("MyCollage"));
        mMap.addPolyline(new PolylineOptions()
                        .add(MyLocation,mkcity)
                        .color(Color.BLUE)
                        .width(5));

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        */
    }
    }

