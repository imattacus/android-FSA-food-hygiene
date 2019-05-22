package com.example.portfolio.models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface FavouritesDao {
    @Insert
    void insertFavourite(Establishment e);

    @Query("SELECT id FROM establishment")
    List<Integer> getFavouriteIDs();

    @Query("SELECT * FROM establishment")
    List<Establishment> getAllFavourites();

    @Delete
    void deleteFavourite(Establishment e);
}
