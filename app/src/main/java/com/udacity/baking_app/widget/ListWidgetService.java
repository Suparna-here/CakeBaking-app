package com.udacity.baking_app.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.baking_app.R;
import com.udacity.baking_app.data.database.Ingredient;
import com.udacity.baking_app.data.network.ServiceGenerator;
import com.udacity.baking_app.utils.InjectorUtils;

import java.util.List;

import static com.udacity.baking_app.data.network.ServiceGenerator.INVALID_RECIPE_ID;

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<Ingredient> ingredientData;
    private static final String LOG_TAG = ListRemoteViewsFactory.class.getSimpleName();

    public ListRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: onCreate");
    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        long recipeId = InjectorUtils.getSavedIngredientRecipeId(mContext);
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: onDataSetChanged recipeId " + recipeId);
        if (recipeId != INVALID_RECIPE_ID) {
            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: doing databse operation " + recipeId);
            ingredientData = IngredientAppWidgetProvider.mIngredientsList;
            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: onDataSetChanged Received Database data " + ingredientData.get(1).getIngredient());
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (ingredientData == null) return 0;
        return ingredientData.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: getViewAt1");
        if (ingredientData == null) return null;

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget_ingredient_item);
        Ingredient ingredient = ingredientData.get(position);
        views.setTextViewText(R.id.widget_ingredient_name, ingredient.getIngredient());

       /* StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(" " + ingredient.getQuantity() + " " +
                ingredient.getMeasure());*/


        views.setTextViewText(R.id.widget_ingredient_amount, ingredient.getMeasure());

        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: getViewAt2 ");

        Intent fillInIntent = new Intent();
        views.setOnClickFillInIntent(R.id.container_item_layout, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int position) {
        return ingredientData.get(position).getIngredient_id();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
