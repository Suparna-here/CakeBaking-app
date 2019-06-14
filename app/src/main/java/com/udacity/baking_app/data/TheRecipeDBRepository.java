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

    /*private MutableLiveData<List<MovieTrailerResponse.Trailer>> trailerData;
    private MutableLiveData<List<MovieReviewResponse.Review>> reviewData;
    private MutableLiveData<Boolean> isInternetErrorForTrailer;
    private MutableLiveData<Boolean> isInternetErrorForReview;*/

//    private MutableLiveData<Recipe> movieMutableLiveData;


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

//        movieMutableLiveData = new MutableLiveData<Recipe>();
      /*  isInternetErrorForTrailer = new MutableLiveData<Boolean>();
        setTrailerErrorMessage();
        isInternetErrorForReview = new MutableLiveData<Boolean>();
        setReviewErrorMessage();
        trailerData = new MutableLiveData<List<MovieTrailerResponse.Trailer>>();
        reviewData = new MutableLiveData<List<MovieReviewResponse.Review>>();*/
    }

   /* private void setTrailerErrorMessage() {

        isInternetErrorForTrailer.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isInternetError) {

            }
        });
    }

    private void setReviewErrorMessage() {

        isInternetErrorForReview.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isInternetError) {

            }
        });
    }*/

    public LiveData<List<Recipe>> getRecipesListFromRepository() {
        initializeData();
//        recipeData;
       /* mInstance.mIngredientsDao.getIngredientsByRecipeId(2);
        mInstance.mStepsDao.getStepsByRecipeId(2);*/
        return mInstance.mRecipesDao.getListOfRecipes();
    }

    public LiveData<Recipe> getRecipeByRecipeIdFromRepository(long recipe_Id) {
        initializeData();
        return mInstance.mRecipesDao.getRecipeById(recipe_Id);
    }

    public LiveData<List<Ingredient>> getIngredientsByRecipeIdFromRepository(long recipe_Id) {
        initializeData();
        return mInstance.mIngredientsDao.getIngredientsByRecipeId(recipe_Id);
    }

    public LiveData<List<Step>> getStepsByRecipeIdFromRepository(long recipe_Id) {
        initializeData();
        return mInstance.mStepsDao.getStepsByRecipeId(recipe_Id);
    }

    /* public MutableLiveData<List<MovieTrailerResponse.Trailer>> getTrailerListFromRepository(long movieId) {
        setTrailerListByMovieId(movieId);
        return trailerData;
    }

    public MutableLiveData<List<MovieReviewResponse.Review>> getReviewListFromRepository(long movieId) {
        setReviewListByMovieId(movieId);
        return reviewData;
    }*/

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
     * Database related operations
     **/
    /*public void insertMovieToFavourite(final Recipe recipe) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mRecipesDao.insertMovieToFavourite(recipe);
                movieMutableLiveData.postValue(recipe);
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: recipe Insertion");
            }
        });

    }

    public void deleteMovieFromFavourite(final long id) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int count = mRecipesDao.countInFavourite(id);
                if (count > 0) {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: count =" + count + " deleteMovieFromFavourite " + id);
                    mRecipesDao.deleteMovieFromFavourite(id);
                    movieMutableLiveData.postValue(null);
                } else {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: No movie for Deletion");
                }
            }
        });
    }

    public void setFavouriteMovieById(long movieId) {
        initializeData();
        final LiveData<Recipe> movieLiveData = mRecipesDao.getFavouriteMovieById(movieId);
        movieLiveData.observeForever(new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                movieMutableLiveData.postValue(movieLiveData.getValue());
            }
        });
    }

    public MutableLiveData<Recipe> getFavouriteMovieById(long movieId) {
        return movieMutableLiveData;
    }

    private void setMoviesBasedOnFavourite(final String sort_order) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int count = mRecipesDao.countAllFavouriteMovies();
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: number of Favourite movies " + count);
                final LiveData<List<Recipe>> movieList = mRecipesDao.getFavouriteMovies();
                movieList.observeForever(new Observer<List<Recipe>>() {
                    @Override
                    public void onChanged(@Nullable List<Recipe> recipes) {
                        if (sort_order.equals(ServiceGenerator.ORDER_FAVOURITE)) {
                            recipeData.postValue(movieList.getValue());
                            if (ServiceGenerator.LOCAL_LOGD)
                                Log.d(LOG_TAG, "su: Select LiveData Number of Favourite recipes=" + movieList.getValue().size());
                        }
                    }
                });
            }
        });
    }

    private void setRecipes() {
        setRecipesFromServer();
    }

    private void setMoviesBasedOnTopRated() {
        setRecipesFromServer(ServiceGenerator.ORDER_TOPRATED);
    }

    public void setRecipesBasedOnSortOrder() {
        initializeData();

        setRecipes();
        if (sort_by.equals(ServiceGenerator.ORDER_POPULARITY)) {
        }else if (sort_by.equals(ServiceGenerator.ORDER_TOPRATED)) {
            setMoviesBasedOnTopRated();
        } else if (sort_by.equals(ServiceGenerator.ORDER_FAVOURITE)) {
            setMoviesBasedOnFavourite(sort_by);
            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: returning setRecipesBasedOnSortOrder " + sort_by);
        }
    } */


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

   /* public static List<Recipe> getRecipesFromServer() {//String sort_by
//        initializeData();
        MutableLiveData<List<Recipe>> recipeList=new MutableLiveData<List<Recipe>>();
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
                    recipeList.postValue(recipelist);
                } else {
                    // error response, no access to resource?
                    // user object not available
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Network Error");
                    recipeList.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                // something went completely south (like no internet connection)
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d("Network", "su: No Internet" + t.getMessage());
                recipeList.postValue(null);
            }
        });

        return recipeList.getValue();
    }*/
      /* private void setTrailerListByMovieId(long movieId) {
        RecipesService.RecipeAPI client = ServiceGenerator.createService(RecipesService.RecipeAPI.class);
        Call<MovieTrailerResponse> call = null;

        call = client.fetchTrailers(Long.toString(movieId), BuildConfig.API_KEY);

        call.enqueue(new Callback<MovieTrailerResponse>() {
            @Override
            public void onResponse(Call<MovieTrailerResponse> call, Response<MovieTrailerResponse> response) {
                if (response.isSuccessful()) {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Network SUCCESS");
                    // user object available
                    List<MovieTrailerResponse.Trailer> trailerList = response.body().getMovieTrailerlist();
                    isInternetErrorForTrailer.postValue(false);//No internet Error
                    trailerData.postValue(trailerList);
                } else {
                    // error response, no access to resource?
                    // user object not available
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Data Error");
                    isInternetErrorForTrailer.postValue(false);
                    trailerData.postValue(null);
                }

            }

            @Override
            public void onFailure(Call<MovieTrailerResponse> call, Throwable t) {
                // something went completely south (like no internet connection)
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: No Internet" + t.getMessage());
                isInternetErrorForTrailer.postValue(true);
                trailerData.postValue(null);

            }
        });
    }

    private void setReviewListByMovieId(long movieId) {
        RecipesService.RecipeAPI client = ServiceGenerator.createService(RecipesService.RecipeAPI.class);
        Call<MovieReviewResponse> call = null;
        call = client.fetchReviews(Long.toString(movieId), BuildConfig.API_KEY);
        call.enqueue(new Callback<MovieReviewResponse>() {
            @Override
            public void onResponse(Call<MovieReviewResponse> call, Response<MovieReviewResponse> response) {
                if (response.isSuccessful()) {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Network SUCCESS");
                    // user object available
                    List<MovieReviewResponse.Review> reviewList = response.body().getMovieReviewlist();
                    isInternetErrorForReview.postValue(false);//No internet Error
                    reviewData.postValue(reviewList);
                } else {
                    // error response, no access to resource?
                    // user object not available
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Data Error");
                    isInternetErrorForReview.postValue(false);
                    reviewData.postValue(null);
                }

            }

            @Override
            public void onFailure(Call<MovieReviewResponse> call, Throwable t) {
                // something went completely south (like no internet connection)
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: No Internet" + t.getMessage());
                isInternetErrorForReview.postValue(true);
                reviewData.postValue(null);

            }
        });
    }

    public MutableLiveData<Boolean> getIsInternetErrorForTrailer() {
        return isInternetErrorForTrailer;
    }

    public MutableLiveData<Boolean> getIsInternetErrorForReview() {
        return isInternetErrorForReview;
    }*/
}
