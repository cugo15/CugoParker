package com.example.cugoparker.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cugoparker.model.ParkSpots;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ParkSpotsDao {

    @Query("SELECT * FROM ParkSpots")
    Flowable<List<ParkSpots>> getAll();
    @Insert
    Completable insert(ParkSpots parkSpots);
    @Delete
    Completable delete(ParkSpots parkSpots);
    @Update
    Completable update(ParkSpots parkSpots);

    @Query("SELECT * FROM ParkSpots WHERE fav = 1")
    Flowable<List<ParkSpots>> getFav();
}
