package com.udacity.baking_app.data.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net";
    public static final String YOUTUBE_URL="http://img.youtube.com/vi/";
    public static final String RECIPE_ID ="recipe_Id";
    public static final String IS_TWO_PANE="two_pane";

    public static final String RECIPE_NAME = "recipe_name";
    public static final long INVALID_RECIPE_ID = -1;
    public static final String INVALID_RECIPE_NAME = "";

    public static final String INGREDIENT_LIST="ingredient_list";
    public static final String INGREDIENT_Bundle="ingredient_bundle";
    public static final String ACTION_UPDATE_INGREDIENT = "com.udacity.baking_app.widget.action.update_recipe_ingredients";
    public static final String ACTION_UPDATE_RECIPE_WIDGETS = "com.udacity.baking_app.widget.action.update_recipe_widgets";

    public static final String EXTRA_BUNDLE = "extra_bundle";
    public static final String EXTRA_DATA = "extra_data";

    public static final boolean LOCAL_LOGD=true;

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();


    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}