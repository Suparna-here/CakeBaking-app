package com.udacity.baking_app.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.baking_app.data.TheRecipeDBRepository;
import com.udacity.baking_app.data.database.Ingredient;
import com.udacity.baking_app.data.database.Step;

import java.util.List;

class RecipeDetailActivityViewModel extends ViewModel {
    public static final String LOG_TAG= RecipeDetailActivityViewModel.class.getSimpleName();
    // recipe list the user is looking at
    private final LiveData<List<Ingredient>> mIngredientList;
    private final LiveData<List<Step>> mStepList;
    private final TheRecipeDBRepository mRepository;
//    private final long mRecipe_Id;

    public RecipeDetailActivityViewModel(TheRecipeDBRepository repository, long recipeId) {
        this.mRepository=repository;
        mIngredientList=mRepository.getIngredientsByRecipeIdFromRepository(recipeId);
        mStepList=mRepository.getStepsByRecipeIdFromRepository(recipeId);
    }

    /*public void setIngredientsStepsOfRecipe(long recipeId){
        mIngredientList.postValue(mRepository.getIngredientsByRecipeIdFromRepository(recipeId).getValue());
        mStepList.postValue(mRepository.getStepsByRecipeIdFromRepository(recipeId).getValue());
    }*/

    public LiveData<List<Ingredient>> getIngredientList(){
        return mIngredientList;
    }

    public LiveData<List<Step>> getStepList(){
        return mStepList;
    }
}
