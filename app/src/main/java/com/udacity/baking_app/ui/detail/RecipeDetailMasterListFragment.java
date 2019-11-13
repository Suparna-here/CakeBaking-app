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

package com.udacity.baking_app.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.baking_app.R;
import com.udacity.baking_app.data.database.Ingredient;
import com.udacity.baking_app.data.database.Step;
import com.udacity.baking_app.data.network.ServiceGenerator;
import com.udacity.baking_app.utils.InjectorUtils;
import com.udacity.baking_app.widget.RecipeIngredientService;

import java.util.ArrayList;
import java.util.List;

import static com.udacity.baking_app.data.network.ServiceGenerator.EXTRA_BUNDLE;
import static com.udacity.baking_app.data.network.ServiceGenerator.EXTRA_DATA;
import static com.udacity.baking_app.data.network.ServiceGenerator.RECIPE_NAME;


// This fragment displays all of the AndroidMe images in one large list
// The list appears as a grid of images
public class RecipeDetailMasterListFragment extends Fragment implements RecipeDetailListDBAdapter.RecipeDetailListDBAdapterOnClickHandler {
    private ImageButton mIngredientAddToWidgetButton;
    private TextView mIngredientView;
    private RecyclerView mRecyclerView;
    private RecipeDetailListDBAdapter mRecipeStepDetailListDBAdapter;
    private RecipeDetailActivityViewModel mViewModel;
    private long recipe_Id;
    private String recipe_Name;
    private boolean twoPane;

    private TextView mRecipeErrorMessageDisplay;
    private PlayerFragment playerFragment;

    private static final String LOG_TAG = RecipeDetailMasterListFragment.class.getSimpleName();


    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    // Mandatory empty constructor
    public RecipeDetailMasterListFragment() {
    }


    // Inflates the GridView of all AndroidMe images
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        recipe_Id = bundle.getLong(ServiceGenerator.RECIPE_ID);
        twoPane = bundle.getBoolean(ServiceGenerator.IS_TWO_PANE);
        recipe_Name = bundle.getString(ServiceGenerator.RECIPE_NAME);

        final View rootView = inflater.inflate(R.layout.fragment_master_list_detail, container, false);
        mIngredientView = (TextView) rootView.findViewById(R.id.tv_ingredients_list);
        mIngredientAddToWidgetButton = (ImageButton) rootView.findViewById(R.id.iv_favourite_button);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_recipe_steps);

        mRecipeErrorMessageDisplay = (TextView) rootView.findViewById(R.id.tv_recipe_error_message_display);

        /*
         * GridLayoutManager for GridView.
         * SPAN_COUNT parameter defines number of columns.
         */
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Pass in 'this' as the RecipeDBAdapterOnClickHandler
        /*
         * The RecipeDBAdapter is responsible for linking movie data with the Views that
         * will end up displaying our movie data.
         */
        mRecipeStepDetailListDBAdapter = new RecipeDetailListDBAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mRecipeStepDetailListDBAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         */
        RecipeDetailViewModelFactory detailViewModelFactory = InjectorUtils.provideRecipeDetailListViewModelFactory(getActivity(), recipe_Id);
        mViewModel = ViewModelProviders.of(getActivity(), detailViewModelFactory).get(RecipeDetailActivityViewModel.class);

        loadRecipeDataInViewModel();
        // Return the root view
        return rootView;
    }

    private void loadRecipeDataInViewModel() {
        LiveData<List<Ingredient>> ingredientList = mViewModel.getIngredientList();
        ingredientList.observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredientList) {
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: swap Ingredients of Recipe ");
                StringBuffer stringBuffer = new StringBuffer();
                for (Ingredient ingredient : ingredientList) {
                    stringBuffer.append("\u2022 " + ingredient.getQuantity() + " " +
                            ingredient.getIngredient() + " " + ingredient.getMeasure() + "\n");
                }
                mIngredientView.setText(stringBuffer);
                mIngredientAddToWidgetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), R.string.add_ingredient_to_widget, Toast.LENGTH_LONG).show();
                        InjectorUtils.setSavedIngredientDetails(getActivity(), recipe_Id, recipe_Name);
                        RecipeIngredientService.startActionUpdateIngredient(getActivity());
                    }
                });
            }
        });


        LiveData<List<Step>> stepList = mViewModel.getStepList();
        stepList.observe(this, new Observer<List<Step>>() {
            @Override
            public void onChanged(@Nullable List<Step> stepList) {
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: swap Steps of Recipe ");
                mRecipeStepDetailListDBAdapter.setStepDataOfRecipe(stepList);
            }
        });
    }


    /**
     * This method will make the View for the recipe data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showDetailListDataView() {
        // First, make sure the error is invisible
        mRecipeErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the movie data is visible
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the internet connection error message visible and hide the RecipeRespopnse
     * View.
     */
    private void showErrorMessage() {
        // First, hide the currently visible data
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: mErrorMessageDisplay ");
        mRecyclerView.setVisibility(View.INVISIBLE);
        mRecipeErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    //On Clicking GridItem, this method is called.
    @Override
    public void itemClickListener(final Step step) {
        if (twoPane) {
            ArrayList<Step> stepList = new ArrayList<Step>();
            stepList.add(step);
            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: itemClickListener " + step.getStep_Id());
            Bundle bundleFragment = new Bundle();
            bundleFragment.putBoolean(ServiceGenerator.IS_TWO_PANE, twoPane);
            bundleFragment.putParcelableArrayList(EXTRA_DATA, stepList);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            playerFragment = new PlayerFragment();
            playerFragment.setArguments(bundleFragment);
            fragmentManager.beginTransaction()
                    .replace(R.id.video_container_tab, playerFragment).commit();

        } else {
            ArrayList<Step> stepList = new ArrayList<Step>();
            stepList.add(step);
            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: itemClickListener " + step.getStep_Id());
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(EXTRA_DATA, stepList);
            bundle.putString(RECIPE_NAME, recipe_Name);

            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra(EXTRA_BUNDLE, bundle);
            startActivity(intent);
        }
    }
}
