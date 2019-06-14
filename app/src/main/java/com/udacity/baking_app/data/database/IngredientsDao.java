package com.udacity.baking_app.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface IngredientsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIngredient(Ingredient ingredient);

    @Query("SELECT * from Ingredient WHERE recipe_id=:recipe_id")
    LiveData<List<Ingredient>> getIngredientsByRecipeId(long recipe_id);

    @Query("DELETE from Ingredient WHERE recipe_id=:recipe_id")
    void deleteIngredientsByRecipeId(long recipe_id);

   /* @Query("SELECT * from Recipe WHERE id=:Id")
    LiveData<Recipe> getFavouriteMovieById(long Id);

    @Query("SELECT count(*) from Recipe")
    int countAllFavouriteMovies();

    @Query("SELECT count(*) from Recipe WHERE id=:id")
    int countInFavourite(long id);

    @Query("DELETE from Recipe WHERE id=:Id")
    void deleteMovieFromFavourite(long Id);*/
}
