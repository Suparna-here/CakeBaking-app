package com.udacity.baking_app.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities={Recipe.class, Step.class, Ingredient.class},version=1, exportSchema = false)
public abstract class RecipesDatabase extends RoomDatabase {
    // The associated DAOs for the database
    public abstract RecipesDao recipesDao();
    public abstract StepsDao stepsDao();
    public abstract IngredientsDao ingredientsDao();

    private static final String DATABASE_NAME="recipes_database";
    //For Singleton instantiation
    private static final Object LOCK=new Object();
    private static volatile RecipesDatabase mInstance;

    public static RecipesDatabase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LOCK) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(context.getApplicationContext(),
                            RecipesDatabase.class, RecipesDatabase.DATABASE_NAME).build();
                }
            }
        }
        return mInstance;
    }
}
