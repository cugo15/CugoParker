package com.example.cugoparker.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.cugoparker.model.ParkSpots;

@Database(entities = {ParkSpots.class}, version = 1)

public abstract class ParkSpotsDb extends RoomDatabase {
    public abstract ParkSpotsDao parkSpotsDao();

}
