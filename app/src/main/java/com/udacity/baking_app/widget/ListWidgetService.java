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
 /*   private LiveData<Recipe> recipeData;
    private static final String[] arr_Text={"Suparna", "Koushik", "Shounak"};*/
    private static final String LOG_TAG = ListRemoteViewsFactory.class.getSimpleName();
//    Cursor mCursor; = new LiveData<List<Ingredient>>()

    public ListRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {
        if(ServiceGenerator.LOCAL_LOGD)
        Log.d(LOG_TAG, "su: onCreate");
    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        // Get all plant info ordered by creation time
      /*  Uri PLANT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(
                PLANT_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_CREATION_TIME
        );*/
        long recipeId = InjectorUtils.getSavedIngredientRecipeId(mContext);
        if(ServiceGenerator.LOCAL_LOGD)
        Log.d(LOG_TAG, "su: onDataSetChanged recipeId "+recipeId);
       if (recipeId != INVALID_RECIPE_ID) {
           if(ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: doing databse operation "+recipeId);
            ingredientData =IngredientAppWidgetProvider.mIngredientsList;
          /*List<Recipe> recipeList = TheRecipeDBRepository.getRecipesFromServer();
            RecipesDatabase database = RecipesDatabase.getInstance(mContext.getApplicationContext());
            ingredientData = database.ingredientsDao().getIngredientsByRecipeId(recipeId);
            recipeData = database.recipesDao().getRecipeById(recipeId);;*/

           if(ServiceGenerator.LOCAL_LOGD)
               Log.d(LOG_TAG, "su: onDataSetChanged Received Database data "+ingredientData.get(1).getIngredient());
       }
       /*if(recipeData!=null)
       {
           if(ServiceGenerator.LOCAL_LOGD)
               Log.d(LOG_TAG, "su: onDataSetChanged Received Database data ");
       }*/
    }

    @Override
    public void onDestroy() {

    }// mCursor.close();

    @Override
    public int getCount() {
        if (ingredientData == null) return 0;
        return ingredientData.size();
     /* if(arr_Text==null)return 0;
      return arr_Text.length;*/
    }

    @Override
    public RemoteViews getViewAt(int position) {
       /* if (mCursor == null || mCursor.getCount() == 0) return null;
        mCursor.moveToPosition(position);
        int idIndex = mCursor.getColumnIndex(PlantContract.PlantEntry._ID);
        int createTimeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        int waterTimeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int plantTypeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

        long plantId = mCursor.getLong(idIndex);
        int plantType = mCursor.getInt(plantTypeIndex);
        long createdAt = mCursor.getLong(createTimeIndex);
        long wateredAt = mCursor.getLong(waterTimeIndex);
        long timeNow = System.currentTimeMillis();*/
        if(ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: getViewAt1");
        if (ingredientData == null) return null;
//        ingredientData.observeForever(new List<Ingredient>());
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget_ingredient_item);

//        Ingredient ingredient = ingredientData.getValue().get(position);
        // Update the plant image
        /* int imgRes = PlantUtils.getPlantImageRes(mContext, timeNow - createdAt, timeNow - wateredAt, plantType);*/
        views.setTextViewText(R.id.widget_ingredient_name, ingredientData.get(position).getIngredient());
//        String quantity_measure=String.valueOf(ingredientData.get(position).getQuantity()) + " " + ingredientData.get(position).getMeasure();
        views.setTextViewText(R.id.widget_ingredient_amount, ingredientData.get(position).getMeasure());

//      Always hide the water drop in GridView mode
//      views.setViewVisibility(R.id.widget_water_button, View.GONE);

        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: getViewAt2 ");//+ recipePresent.getId()
      /*Recipe recipePresent = recipeData.getValue();
        ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
        recipeList.add(recipePresent);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_DATA, recipeList);*/

        Intent fillInIntent = new Intent();
//        fillInIntent.putExtra(EXTRA_BUNDLE, bundle);
//        views.setOnClickFillInIntent(R.id.widget_plant_image, fillInIntent);
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
           /* Ingredient ingredient = ingredientData.getValue().get(position);
            return ingredient.getIngredient_id()
           return position;;*/
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
