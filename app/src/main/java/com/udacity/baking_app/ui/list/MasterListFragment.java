/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.baking_app.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.baking_app.R;
import com.udacity.baking_app.data.database.Recipe;
import com.udacity.baking_app.data.network.ServiceGenerator;
import com.udacity.baking_app.ui.detail.RecipeDetailListActivity;
import com.udacity.baking_app.utils.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

import static com.udacity.baking_app.data.network.ServiceGenerator.EXTRA_BUNDLE;
import static com.udacity.baking_app.data.network.ServiceGenerator.EXTRA_DATA;


// This fragment displays all of the AndroidMe images in one large list
// The list appears as a grid of images
public class MasterListFragment extends Fragment implements RecipeDBAdapter.RecipeDBAdapterOnClickHandler {
    private RecyclerView mRecyclerView;
    private RecipeDBAdapter mRecipeDBAdapter;
    private MainActivityViewModel mViewModel;


//    private TextView mErrorMessageDisplay;
    private TextView mRecipeErrorMessageDisplay;

    public static final String LOG_TAG = MasterListFragment.class.getSimpleName();


    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    // Mandatory empty constructor
    public MasterListFragment() {
    }


    // Inflates the GridView of all AndroidMe images
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_recipes);

        /* This TextView is used to display errors and will be hidden if there are no errors */
//        mErrorMessageDisplay = (TextView) rootView.findViewById(R.id.tv_movie_error_message_display);

        mRecipeErrorMessageDisplay = (TextView) rootView.findViewById(R.id.tv_recipe_error_message_display);
        final int SPAN_COUNT = getResources().getInteger(R.integer.gallery_columns);

        /*
         * GridLayoutManager for GridView.
         * SPAN_COUNT parameter defines number of columns.
         */
        GridLayoutManager layoutManager
                = new GridLayoutManager(getActivity(), SPAN_COUNT);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Pass in 'this' as the RecipeDBAdapterOnClickHandler
        /*
         * The RecipeDBAdapter is responsible for linking movie data with the Views that
         * will end up displaying our movie data.
         */
        mRecipeDBAdapter = new RecipeDBAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mRecipeDBAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         */
        MainViewModelFactory mainViewModelFactory = InjectorUtils.provideMainActivityViewModelFactory(getActivity());
        mViewModel = ViewModelProviders.of(this, mainViewModelFactory).get(MainActivityViewModel.class);

        loadRecipeDataInViewModel();
        // Return the root view
        return rootView;
    }

    /* @Override
   public void onSaveInstanceState(Bundle outState) {
        outState.putString(SORT_KEY, sort_by);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState!=null)
        sort_by=savedInstanceState.getString(SORT_KEY);
        super.onActivityCreated(savedInstanceState);
    }*/


    private void loadRecipeDataInViewModel() {
        LiveData<List<Recipe>> recipesList = mViewModel.getRecipesList();
        recipesList.observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipeList) {
                if(ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: swap Recipes sort_by ");
                mRecipeDBAdapter.setRecipeData(recipeList);
                // Show the movie list or the loading screen based on whether the movie data exists
                // and is loaded
                if (recipeList != null && recipeList.size() != 0) {
                    if(ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: show mRecyclerView");
                    showRecipeDataView();
                } else {
                    if(ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: showErrorMessage");
                    showErrorMessage();
                }
            }
        });
    }


    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showRecipeDataView() {
        // First, make sure the error is invisible
//        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecipeErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the movie data is visible
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the internet connection error message visible and hide the RecipeRespopnse
     * View.
     */
    private void showErrorMessage() {
//        if (sort_by.equals(ServiceGenerator.ORDER_POPULARITY) || sort_by.equals(ServiceGenerator.ORDER_TOPRATED)) {
            // First, hide the currently visible data
            if(ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: mErrorMessageDisplay ");
            mRecyclerView.setVisibility(View.INVISIBLE);
            mRecipeErrorMessageDisplay.setVisibility(View.VISIBLE);
            // Then, show the internet error
           /*  mErrorMessageDisplay.setVisibility(View.VISIBLE);
       } else if (sort_by.equals(ServiceGenerator.ORDER_FAVOURITE)) {
            if(ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: mRecipeErrorMessageDisplay " + sort_by);
            // First, hide the currently visible data and internet connection error message
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.INVISIBLE);
            // Then, show the Favourite movie data error
            mRecipeErrorMessageDisplay.setVisibility(View.VISIBLE);
        }*/
    }


    //On Clicking GridItem, this method is called.
    @Override
    public void itemClickListener(final Recipe recipe) {
//        mViewModel.setFavouriteMovieById(recipe.getId());
        ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
        recipeList.add(recipe);
       /* ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();
        ingredientList.addAll(recipe.getIngredients());*/

        if(ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: itemClickListener " + recipe.getId());
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_DATA, recipeList);
        Intent intent = new Intent(getActivity(), RecipeDetailListActivity.class);
        intent.putExtra(EXTRA_BUNDLE, bundle);
        startActivity(intent);
    }
}
