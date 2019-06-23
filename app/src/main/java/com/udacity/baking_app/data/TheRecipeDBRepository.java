package com.udacity.baking_app.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.udacity.baking_app.AppExecutors;
import com.udacity.baking_app.data.database.Ingredient;
import com.udacity.baking_app.data.database.IngredientsDao;
import com.udacity.baking_app.data.database.Recipe;
import com.udacity.baking_app.data.database.RecipesDao;
import com.udacity.baking_app.data.database.Step;
import com.udacity.baking_app.data.database.StepsDao;
import com.udacity.baking_app.data.network.RecipesService;
import com.udacity.baking_app.data.network.ServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TheRecipeDBRepository {
    private static final String LOG_TAG = TheRecipeDBRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();

    private static TheRecipeDBRepository mInstance;
    private final RecipesDao mRecipesDao;
    private final IngredientsDao mIngredientsDao;
    private final StepsDao mStepsDao;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;
    private MutableLiveData<List<Recipe>> recipeData;

    private TheRecipeDBRepository(AppExecutors executors, RecipesDao recipesDao, IngredientsDao ingredientsDao, StepsDao stepsDao) {
        mRecipesDao = recipesDao;
        mIngredientsDao = ingredientsDao;
        mStepsDao = stepsDao;
        mExecutors = executors;
        recipeData = new MutableLiveData<List<Recipe>>();
        setRecipesFromServer();//Network
        // As long as the repository exists, observe the network LiveData.
        // If that LiveData changes, update the database.
        recipeData.observeForever(newRecipesFromNetwork -> {
            if (newRecipesFromNetwork == null) return;
            mExecutors.diskIO().execute(() -> {
                // Deletes old recipes data
                mRecipesDao.deleteRecipe();
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "Old recipes deleted");
                // Insert new recipes data into database
                for (int i = 0; i < newRecipesFromNetwork.size(); i++) {
                    Recipe recipe = newRecipesFromNetwork.get(i);
                    mRecipesDao.insertRecipe(recipe);
                    List<Step> stepList = recipe.getSteps();
                    for (int j = 0; j < stepList.size(); j++) {
                        Step step = stepList.get(j);
                        mStepsDao.insertStep(new Step(step.getStep_Id(), step.getId(), step.getShortDescription(), step.getDescription(), step.getVideoURL(), step.getThumbnailURL(), recipe.getId()));
                    }

                    List<Ingredient> ingredientList = recipe.getIngredients();
                    for (int k = 0; k < ingredientList.size(); k++) {
                        Ingredient ingredient = ingredientList.get(k);
                        mIngredientsDao.insertIngredient(new Ingredient(ingredient.getIngredient_id(), ingredient.getQuantity(), ingredient.getMeasure(), ingredient.getIngredient(), recipe.getId()));
                    }
                }
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "New recipes inserted");
            });
        });
    }


    public LiveData<List<Recipe>> getRecipesListFromRepository() {
        initializeData();
        return mInstance.mRecipesDao.getListOfRecipes();
    }

    public Recipe getRecipeByRecipeIdFromRepository(long recipe_Id) {
        initializeData();
        return mInstance.mRecipesDao.getRecipeById(recipe_Id);
    }

    public LiveData<List<Ingredient>> getIngredientsByRecipeIdFromRepository(long recipe_Id) {
        initializeData();
        return mInstance.mIngredientsDao.getIngredientsByRecipeId(recipe_Id);
    }

    public List<Ingredient> getIngredientsListByRecipeIdFromRepository(long recipe_Id) {
        initializeData();
        return mInstance.mIngredientsDao.getIngredientsListByRecipeId(recipe_Id);
    }

    public LiveData<List<Step>> getStepsByRecipeIdFromRepository(long recipe_Id) {
        initializeData();
        return mInstance.mStepsDao.getStepsByRecipeId(recipe_Id);
    }

    public synchronized static TheRecipeDBRepository getInstance(AppExecutors executors, RecipesDao recipesDao, IngredientsDao ingredientsDao, StepsDao stepsDao) { //
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: Getting the repository");
        if (mInstance == null) {
            synchronized (LOCK) {
                mInstance = new TheRecipeDBRepository(executors, recipesDao, ingredientsDao, stepsDao); //
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: Made new repository");
            }
        }
        return mInstance;
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    public synchronized void initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return;
        mInitialized = true;
    }

    /**
     * This is used to get the Recipes response from Udacity Server
     *
     * @return Response of retrofit
     */
    public void setRecipesFromServer() {//String sort_by
        initializeData();
        RecipesService.RecipeAPI client = ServiceGenerator.createService(RecipesService.RecipeAPI.class);
        Call<List<Recipe>> call = client.getRecipeResponse();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Network SUCCESS");
                    // user object available
                    List<Recipe> recipelist = response.body();
                    recipeData.postValue(recipelist);
                } else {
                    // error response, no access to resource?
                    // user object not available
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Network Error");
                    recipeData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                // something went completely south (like no internet connection)
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d("Network", "su: No Internet" + t.getMessage());
                recipeData.postValue(null);
            }
        });

    }
}
