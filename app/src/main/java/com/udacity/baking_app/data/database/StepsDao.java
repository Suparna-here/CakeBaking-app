package com.udacity.baking_app.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface StepsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStep(Step step);

    @Query("SELECT * from Step WHERE recipe_id=:recipe_id")
    LiveData<List<Step>> getStepsByRecipeId(long recipe_id);

    @Query("DELETE from Step WHERE recipe_id=:recipe_id")
    void deleteStepsByRecipeId(long recipe_id);


   /* @Query("SELECT * from Recipe WHERE id=:Id")
    LiveData<Recipe> getFavouriteMovieById(long Id);

    @Update
    void update(Repo... repos);

    @Delete
    void delete(Repo... repos);
    @Query("SELECT * FROM repo")
    List<Repo> getAllRepos();

    @Query("SELECT * FROM repo WHERE userId=:userId")
    List<Repo> findRepositoriesForUser(final int userId);

    @Query("SELECT count(*) from Recipe")
    int countAllFavouriteMovies();

    @Query("SELECT count(*) from Recipe WHERE id=:id")
    int countInFavourite(long id);

    @Query("DELETE from Recipe WHERE id=:Id")
    void deleteMovieFromFavourite(long Id);*/
}
