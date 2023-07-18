package com.example.cugoparker.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.Manifest;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import com.example.cugoparker.R;
import com.example.cugoparker.databinding.ParkhereDialogBinding;
import com.example.cugoparker.model.ParkSpots;
import com.example.cugoparker.roomdb.ParkSpotsDao;
import com.example.cugoparker.roomdb.ParkSpotsDb;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.example.cugoparker.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    ParkhereDialogBinding parkhereDialogBinding;
    ActivityResultLauncher<String> permissionLauncher;
    LocationManager locationManager;
    LocationListener locationListener;
    Dialog dialog;
    ParkSpotsDb db;
    ParkSpotsDao parkSpotsDao;
    Double savelatitude;
    Double savelongitude;
    List<Address> addressList;
    Date currenttime;
    ParkSpots selectedParkSpot;
    String adress;
    Geocoder geocoder;
    String formatedDate;
    String datetimeformat;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        registerLauncher();
        datetimeformat = "MM/dd/yyyy kk:mm:ss";
        savelatitude = 0.0;
        savelongitude = 0.0;
        adress = "";
        dialog = new Dialog(this);
        db = Room.databaseBuilder(getApplicationContext(),ParkSpotsDb.class,"Parkspot")
                //.allowMainThreadQueries()
                .build();
        parkSpotsDao = db.parkSpotsDao();

        binding.goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
            }
        });
        binding.parkHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this,MainActivity.class);
                intent.putExtra("info","all");
                startActivity(intent);
            }
        });
        binding.favPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this,MainActivity.class);
                intent.putExtra("info","fav");
                startActivity(intent);
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intentt = getIntent();
        String intentInfo =intentt.getStringExtra("name");
        if(intentInfo.equals("main")){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                }
            };
            if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(binding.getRoot(), "Permission needed", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    }).show();
                }
                else {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            }
            else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastKnownLocation!=null){
                    LatLng lastUserLocation = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                }
                mMap.setMyLocationEnabled(true);
            }
        }else{
            mMap.clear();
            binding.infocard.setVisibility(View.VISIBLE);
            binding.ll1.setVisibility(View.GONE);
            binding.ll2.setVisibility(View.GONE);
            selectedParkSpot = (ParkSpots) intentt.getSerializableExtra("parkspot");
            binding.navdate.setText(selectedParkSpot.time);
            binding.navadr.setText(selectedParkSpot.adress);
            binding.navnot.setText(selectedParkSpot.note);
            binding.navtitle.setText(selectedParkSpot.title);
            LatLng parkletlang = new LatLng(selectedParkSpot.latitude,selectedParkSpot.longitude);
            mMap.addMarker(new MarkerOptions().position(parkletlang).title(selectedParkSpot.title).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("markerbm",80,106))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkletlang,17));
        }

    }
    private void registerLauncher() {
        permissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result) {
                            //permission granted
                            if (ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (lastLocation != null) {
                                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                                }
                                mMap.setMyLocationEnabled(true);

                            }
                        } else {
                            //permission denied
                            Snackbar.make(binding.getRoot(), "Permission needed", Snackbar.LENGTH_LONG).setAction("Give permission", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                                }
                            }).show();
                        }
                    }

                });
    }
    public void parkHere(View view){
        mMap.clear();
        openDialog();

        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(binding.getRoot(), "Permission needed", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).show();
            }
            else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location lastKnownLocation =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation!=null){
                savelatitude= lastKnownLocation.getLatitude();
                savelongitude= lastKnownLocation.getLongitude();
                LatLng lastUserLocation = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                mMap.addMarker(new MarkerOptions().position(lastUserLocation).title("You are going to park here...").icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("markerbm",75,100))));

                getadress(savelatitude,savelongitude);
            }

            mMap.setMyLocationEnabled(true);
        }
        parkhereDialogBinding.parkherebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currenttime = Calendar.getInstance().getTime();
                formatedDate = (String) DateFormat.format(datetimeformat, currenttime);
                ParkSpots parkSpots = new ParkSpots(parkhereDialogBinding.entertitle.getText().toString(),parkhereDialogBinding.enternote.getText().toString()
                        ,savelatitude,savelongitude,false,formatedDate,adress);
                    compositeDisposable.add(parkSpotsDao.insert(parkSpots)
                    .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(MapsActivity.this::handleResponse));
                    dialog.dismiss();
                    mMap.clear();
            }
        });


    }
    private  void handleResponse(){
        Intent intent = new Intent(MapsActivity.this,MainActivity.class);
        intent.putExtra("info","all");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void openDialog(){
        parkhereDialogBinding = ParkhereDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(parkhereDialogBinding.getRoot());
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        parkhereDialogBinding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                dialog.dismiss();
            }
        });

    }
    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public void getadress(double latte,double longte){

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            addressList = geocoder.getFromLocation(latte,longte,1);
            adress = addressList.get(0).getAddressLine(0);
            Log.d("mmmm",adress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

}


