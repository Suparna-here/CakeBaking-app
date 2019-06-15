package com.udacity.baking_app.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.baking_app.data.TheRecipeDBRepository;
import com.udacity.baking_app.data.database.Recipe;

import java.util.List;

public class MainActivityViewModel extends ViewModel {
    public static final String LOG_TAG = MainActivityViewModel.class.getSimpleName();
    // recipe list the user is looking at
    private final LiveData<List<Recipe>> mRecipeList;
    private final TheRecipeDBRepository mRepository;


    public MainActivityViewModel(TheRecipeDBRepository repository) {
        mRepository = repository;
        mRecipeList = mRepository.getRecipesListFromRepository();
    }


    public LiveData<List<Recipe>> getRecipesList() {
        return mRecipeList;
    }
}
