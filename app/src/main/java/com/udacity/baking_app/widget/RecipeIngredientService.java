package com.udacity.baking_app.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.udacity.baking_app.R;
import com.udacity.baking_app.data.database.Ingredient;
import com.udacity.baking_app.data.network.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import static com.udacity.baking_app.data.network.ServiceGenerator.ACTION_UPDATE_INGREDIENT;
import static com.udacity.baking_app.data.network.ServiceGenerator.ACTION_UPDATE_RECIPE_WIDGETS;
import static com.udacity.baking_app.data.network.ServiceGenerator.INGREDIENT_Bundle;
import static com.udacity.baking_app.data.network.ServiceGenerator.INGREDIENT_LIST;

public class RecipeIngredientService extends IntentService {

    public static final String LOG_TAG = RecipeIngredientService.class.getSimpleName();


    public RecipeIngredientService() {
        super("RecipeIngredientService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void startActionUpdateIngredient(Context context, List<Ingredient> ingredientList) {//
        ArrayList<Ingredient> arrayList=new ArrayList<Ingredient>();
        arrayList.addAll(ingredientList);

        Intent intent = new Intent(context, RecipeIngredientService.class);
        intent.setAction(ACTION_UPDATE_INGREDIENT);
        if(ingredientList!=null) {
            Bundle bundle=new Bundle();
            bundle.putParcelableArrayList(INGREDIENT_LIST, arrayList);
            intent.putExtra(INGREDIENT_Bundle,bundle);
        }
        context.startService(intent);
    }

    /**
     * Starts this service to perform UpdatePlantWidgets action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateRecipeWidgets(Context context, ArrayList<Ingredient> ingredientsList) {
        Intent intent = new Intent(context, RecipeIngredientService.class);
        intent.setAction(ACTION_UPDATE_RECIPE_WIDGETS);
        if(ingredientsList!=null){
            Bundle bundle=new Bundle();
            bundle.putParcelableArrayList(INGREDIENT_LIST, ingredientsList);
            intent.putExtra(INGREDIENT_Bundle,bundle);
        }
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_INGREDIENT.equals(action)) {
                Bundle bundle = intent.getBundleExtra(INGREDIENT_Bundle);
                if(bundle!=null) {
                    ArrayList<Ingredient> ingredientsList = bundle.getParcelableArrayList(INGREDIENT_LIST);
                    handleActionRecipeIngredients(ingredientsList);
                }else handleActionRecipeIngredients(null);
            } else if (ACTION_UPDATE_RECIPE_WIDGETS.equals(action)) {
                Bundle bundle = intent.getBundleExtra(INGREDIENT_Bundle);
                if(bundle!=null) {
                    ArrayList<Ingredient> ingredientsList = bundle.getParcelableArrayList(INGREDIENT_LIST);
                    handleActionUpdateRecipeWidgets(ingredientsList);
                }else
                handleActionUpdateRecipeWidgets(null);
            }

        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRecipeIngredients( ArrayList<Ingredient> ingredientsList) {
      /*  Uri SINGLE_PLANT_URI = ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build(), plantId);
        ContentValues contentValues = new ContentValues();
        long timeNow = System.currentTimeMillis();
        contentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        // Update only if that plant is still alive
        getContentResolver().update(
                SINGLE_PLANT_URI,
                contentValues,
                PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME + ">?",
                new String[]{String.valueOf(timeNow - PlantUtils.MAX_AGE_WITHOUT_WATER)});
        // Always update widgets after watering plants*/
        if(ServiceGenerator.LOCAL_LOGD)
        Log.d(LOG_TAG,"su:"+"startActionUpdateRecipeWidgets" );

        startActionUpdateRecipeWidgets(this, ingredientsList);
    }


    private void handleActionUpdateRecipeWidgets(ArrayList<Ingredient> ingredientsList) {
        //Query to get the plant that's most in need for water (last watered)
        /*Uri PLANT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        Cursor cursor = getContentResolver().query(
                PLANT_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME
        );
        // Extract the plant details
        int imgRes = R.drawable.grass; // Default image in case our garden is empty
        boolean canWater = false; // Default to hide the water drop button
        long plantId = INVALID_PLANT_ID;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int idIndex = cursor.getColumnIndex(PlantContract.PlantEntry._ID);
            int createTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
            int waterTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
            int plantTypeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);
            plantId = cursor.getLong(idIndex);
            long timeNow = System.currentTimeMillis();
            long wateredAt = cursor.getLong(waterTimeIndex);
            long createdAt = cursor.getLong(createTimeIndex);
            int plantType = cursor.getInt(plantTypeIndex);
            cursor.close();
            canWater = (timeNow - wateredAt) > PlantUtils.MIN_AGE_BETWEEN_WATER &&
                    (timeNow - wateredAt) < PlantUtils.MAX_AGE_WITHOUT_WATER;
            imgRes = PlantUtils.getPlantImageRes(this, timeNow - createdAt, timeNow - wateredAt, plantType);
        }*/
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, IngredientAppWidgetProvider.class));
        //Trigger data update to handle the ListView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view); // ingredient_app_widget
        //Now update all widgets
        IngredientAppWidgetProvider.updateRecipeWidgets(this, appWidgetManager, appWidgetIds, ingredientsList);
    }
}
