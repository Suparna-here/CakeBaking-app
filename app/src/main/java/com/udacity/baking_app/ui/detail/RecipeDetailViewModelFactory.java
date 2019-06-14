package com.udacity.baking_app.ui.detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.udacity.baking_app.data.TheRecipeDBRepository;

public class RecipeDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory{
    private final TheRecipeDBRepository mRepository;
    private final long mRecipId;

    public RecipeDetailViewModelFactory(TheRecipeDBRepository repository, long recipeId) {//
        this.mRepository = repository;
        this.mRecipId = recipeId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new RecipeDetailActivityViewModel(mRepository, mRecipId); //
    }
}
