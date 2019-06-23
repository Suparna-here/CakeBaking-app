package com.udacity.baking_app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.udacity.baking_app.AppExecutors;
import com.udacity.baking_app.data.TheRecipeDBRepository;
import com.udacity.baking_app.data.database.RecipesDatabase;
import com.udacity.baking_app.data.network.ServiceGenerator;
import com.udacity.baking_app.ui.detail.RecipeDetailViewModelFactory;
import com.udacity.baking_app.ui.list.MainViewModelFactory;

import static com.udacity.baking_app.data.network.ServiceGenerator.INVALID_RECIPE_ID;
import static com.udacity.baking_app.data.network.ServiceGenerator.INVALID_RECIPE_NAME;
import static com.udacity.baking_app.data.network.ServiceGenerator.RECIPE_ID;
import static com.udacity.baking_app.data.network.ServiceGenerator.RECIPE_NAME;

public class InjectorUtils {
    private static final String LOG_TAG =InjectorUtils.class.getName();

    public static TheRecipeDBRepository provideRepository(Context context ) {
        RecipesDatabase database = RecipesDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        if(ServiceGenerator.LOCAL_LOGD)
        Log.d(LOG_TAG,"su: get an instance of TheRecipeDBRepository");
        return TheRecipeDBRepository.getInstance(executors, database.recipesDao(), database.ingredientsDao(), database.stepsDao());
    }

   public static RecipeDetailViewModelFactory provideRecipeDetailListViewModelFactory(Context context, long recipe_Id) {
        TheRecipeDBRepository repository = provideRepository(context.getApplicationContext());
        return new RecipeDetailViewModelFactory(repository, recipe_Id);
    }

     public static MainViewModelFactory provideMainActivityViewModelFactory(Context context) {
        TheRecipeDBRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);
    }

    /**
     * Method to get saved recipe name
     *
     * @param context Context
     */
    public static String getSavedIngredientRecipeName(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(RECIPE_NAME, INVALID_RECIPE_NAME);
    }

    /**
     * Method to get saved recipe Id
     *
     * @param context Context
     */
    public static long getSavedIngredientRecipeId(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(RECIPE_ID, INVALID_RECIPE_ID);
    }


    /**
     * Method to save recipe Id, recipe name
     *
     * @param context Context
     * @param recipeName Name of recipe to be saved
     */
    public static void setSavedIngredientDetails(Context context, long recipeId, String recipeName){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(RECIPE_ID, recipeId);
        editor.putString(RECIPE_NAME,recipeName);
        editor.apply();
    }

}
