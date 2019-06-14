package com.udacity.baking_app.ui.list;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.udacity.baking_app.data.TheRecipeDBRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final TheRecipeDBRepository mRepository;


    public MainViewModelFactory(TheRecipeDBRepository repository) {
        this.mRepository = repository;

    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainActivityViewModel(mRepository);
    }
}
