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
     * Starts this service to perform action with the given parameters. If
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
     * Handle action in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRecipeIngredients( ArrayList<Ingredient> ingredientsList) {
        // Always update widgets
        if(ServiceGenerator.LOCAL_LOGD)
        Log.d(LOG_TAG,"su:"+"startActionUpdateRecipeWidgets" );

        startActionUpdateRecipeWidgets(this, ingredientsList);
    }


    private void handleActionUpdateRecipeWidgets(ArrayList<Ingredient> ingredientsList) {
        //Update widgets
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, IngredientAppWidgetProvider.class));
        //Trigger data update to handle the ListView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        //Now update all widgets
        IngredientAppWidgetProvider.updateRecipeWidgets(this, appWidgetManager, appWidgetIds, ingredientsList);
    }
}
