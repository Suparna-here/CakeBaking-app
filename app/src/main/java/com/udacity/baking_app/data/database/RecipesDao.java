package com.udacity.baking_app.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RecipesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);

    @Query("SELECT * from Recipe")
    LiveData<List<Recipe>> getListOfRecipes();

    @Query("SELECT * from Recipe WHERE id=:Id")
    LiveData<Recipe> getRecipeById(long Id);

    @Query("DELETE from Recipe")
    void deleteRecipe();
}
