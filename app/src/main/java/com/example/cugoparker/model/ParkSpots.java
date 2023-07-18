package com.example.cugoparker.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class ParkSpots implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int pid;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "note")
    public String note;

    @ColumnInfo(name = "latitude")
    public Double latitude;

    @ColumnInfo(name = "longitude")
    public Double longitude;

    @ColumnInfo(name = "fav")
    public Boolean fav;

    @ColumnInfo(name = "time")
    public String time;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    @ColumnInfo(name = "adress")
    public String adress;

    public ParkSpots(String title, String note, Double latitude, Double longitude, Boolean fav, String time, String adress) {
        this.title = title;
        this.note = note;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fav = fav;
        this.time = time;
        this.adress = adress;
    }
}
