package com.example.cugoparker.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.cugoparker.R;
import com.example.cugoparker.databinding.ParkRowBinding;
import com.example.cugoparker.model.ParkSpots;
import com.example.cugoparker.roomdb.ParkSpotsDao;
import com.example.cugoparker.roomdb.ParkSpotsDb;
import com.example.cugoparker.view.MapsActivity;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ParkAdapter extends RecyclerView.Adapter<ParkAdapter.ParkHolder> {
    List<ParkSpots> parkSpotsList;
    ParkSpotsDb db;
    ParkSpotsDao parkSpotsDao;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ParkAdapter(List<ParkSpots> parkSpotsList) {
        this.parkSpotsList = parkSpotsList;
    }

    @NonNull
    @Override
    public ParkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ParkRowBinding parkRowBinding = ParkRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ParkHolder(parkRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkHolder holder, @SuppressLint("RecyclerView") int position) {
        if(parkSpotsList.get(position).fav != true){
            holder.parkRowBinding.histfav.setImageResource(R.drawable.emptyheart);
        }
        else{
            holder.parkRowBinding.histfav.setImageResource(R.drawable.redheart);
        }
        holder.parkRowBinding.histtitle.setText(parkSpotsList.get(position).title);
        holder.parkRowBinding.histadress.setText(parkSpotsList.get(position).adress);
        holder.parkRowBinding.histdate.setText(parkSpotsList.get(position).time);
        holder.parkRowBinding.histnote.setText(parkSpotsList.get(position).note);
        holder.parkRowBinding.histtrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = Room.databaseBuilder(holder.itemView.getContext(),ParkSpotsDb.class,"Parkspot")
                        .build();
                parkSpotsDao = db.parkSpotsDao();
                compositeDisposable.add(parkSpotsDao.delete(parkSpotsList.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());
                parkSpotsList.remove(position);
                notifyDataSetChanged();
                compositeDisposable.clear();
            }
        });
        holder.parkRowBinding.histfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(parkSpotsList.get(position).fav != true){
                    db = Room.databaseBuilder(holder.itemView.getContext(),ParkSpotsDb.class,"Parkspot")
                            .build();
                    parkSpotsDao = db.parkSpotsDao();
                    parkSpotsList.get(position).fav=true;
                    compositeDisposable.add(parkSpotsDao.update(parkSpotsList.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());
                    holder.parkRowBinding.histfav.setImageResource(R.drawable.redheart);
                }
                else{
                    db = Room.databaseBuilder(holder.itemView.getContext(),ParkSpotsDb.class,"Parkspot")
                            .build();
                    parkSpotsDao = db.parkSpotsDao();
                    parkSpotsList.get(position).fav=false;
                    compositeDisposable.add(parkSpotsDao.update(parkSpotsList.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());
                    holder.parkRowBinding.histfav.setImageResource( R.drawable.emptyheart);
                }
            }
        });
        holder.parkRowBinding.histgomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(),MapsActivity.class);
                intent.putExtra("name","navigation");
                intent.putExtra("parkspot",parkSpotsList.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return parkSpotsList.size();
    }

    public class ParkHolder extends RecyclerView.ViewHolder{
        ParkRowBinding parkRowBinding;
        public ParkHolder(ParkRowBinding parkRowBinding) {
            super(parkRowBinding.getRoot());
            this.parkRowBinding = parkRowBinding;
        }
    }


}

