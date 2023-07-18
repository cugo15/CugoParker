package com.example.cugoparker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.cugoparker.R;
import com.example.cugoparker.adapter.ParkAdapter;
import com.example.cugoparker.databinding.ActivityMainBinding;
import com.example.cugoparker.databinding.ActivitySplashScreenBinding;
import com.example.cugoparker.model.ParkSpots;
import com.example.cugoparker.roomdb.ParkSpotsDao;
import com.example.cugoparker.roomdb.ParkSpotsDb;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ParkSpotsDb db;
    ParkSpotsDao parkSpotsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        db = Room.databaseBuilder(getApplicationContext(),ParkSpotsDb.class,"Parkspot")
                //.allowMainThreadQueries()
                .build();

        Intent infint = getIntent();
        String info = infint.getStringExtra("info");

        if(info.equals("fav")){
            binding.textView3.setText("FavoritePlaces");
            parkSpotsDao = db.parkSpotsDao();
            compositeDisposable.add(parkSpotsDao.getFav()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(MainActivity.this::handleResponse));
            binding.gofav.setVisibility(View.GONE);
            binding.goBack.setImageResource(R.drawable.gghome);
            binding.goBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                    intent.putExtra("name","main");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }
        else{
            parkSpotsDao = db.parkSpotsDao();
            compositeDisposable.add(parkSpotsDao.getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(MainActivity.this::handleResponse));
            binding.gofav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    intent.putExtra("info","fav");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            binding.goBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }
    private void handleResponse(List<ParkSpots> parkSpotsList){
        binding.rw.setLayoutManager(new LinearLayoutManager(this));
        ParkAdapter parkAdapter = new ParkAdapter(parkSpotsList);
        binding.rw.setAdapter(parkAdapter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}