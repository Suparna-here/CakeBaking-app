package com.udacity.baking_app.data.network;

import com.udacity.baking_app.data.database.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public class RecipesService {
    public interface RecipeAPI {
        @GET("topher/2017/May/59121517_baking/baking.json")
        Call<List<Recipe>> getRecipeResponse();
    }
}
