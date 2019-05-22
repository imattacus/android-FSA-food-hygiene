package com.example.portfolio.models;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities={Establishment.class}, version=4)
public abstract class FavouritesDatabase extends RoomDatabase {
    public abstract FavouritesDao favouritesDao();
}
