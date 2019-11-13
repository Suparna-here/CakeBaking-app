package com.udacity.baking_app.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.baking_app.R;
import com.udacity.baking_app.data.TheRecipeDBRepository;
import com.udacity.baking_app.data.database.Recipe;
import com.udacity.baking_app.data.network.ServiceGenerator;
import com.udacity.baking_app.ui.detail.RecipeDetailListActivity;
import com.udacity.baking_app.utils.InjectorUtils;

import java.util.ArrayList;

import static com.udacity.baking_app.data.network.ServiceGenerator.EXTRA_BUNDLE;
import static com.udacity.baking_app.data.network.ServiceGenerator.EXTRA_DATA;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientAppWidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = IngredientAppWidgetProvider.class.getSimpleName();

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, long recipeId, String recipeName, int appWidgetId) {
        RemoteViews rv = getIngredientListRemoteView(context, recipeId, recipeName);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    /**
     * Creates and returns the RemoteViews to be displayed in the GridView mode widget
     *
     * @param context The context
     * @return The RemoteViews for the GridView mode widget
     */
    private static RemoteViews getIngredientListRemoteView(Context context, long recipeId, String recipeName) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_app_widget);

        views.setTextViewText(R.id.tv_recipe_name, recipeName);
        // Set the GridWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.widget_list_view, intent);
        Bundle bundle = new Bundle();
        if (recipeId != ServiceGenerator.INVALID_RECIPE_ID) {
            TheRecipeDBRepository repository = InjectorUtils.provideRepository(context.getApplicationContext());

            Recipe recipeData = repository.getRecipeByRecipeIdFromRepository(recipeId);
            ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
            recipeList.add(recipeData);

            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: itemClickListener " + recipeData.getId());
            bundle.putParcelableArrayList(EXTRA_DATA, recipeList);
        }

        // Set the RecipeDetailListActivity intent to launch when clicked
        Intent appIntent = new Intent(context, RecipeDetailListActivity.class);
        appIntent.putExtra(EXTRA_BUNDLE, bundle);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(appIntent);


        // Get the PendingIntent containing the entire back stack
        PendingIntent appPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setPendingIntentTemplate(R.id.widget_list_view, appPendingIntent);
        // Handle empty gardens
        views.setEmptyView(R.id.widget_list_view, R.id.empty_view);
        return views;
    }

    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        long recipeId = InjectorUtils.getSavedIngredientRecipeId(context);
        String recipeName = InjectorUtils.getSavedIngredientRecipeName(context);
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: " + recipeName);
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipeId, recipeName, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI
        RecipeIngredientService.startActionUpdateRecipeWidgets(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        RecipeIngredientService.startActionUpdateRecipeWidgets(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

