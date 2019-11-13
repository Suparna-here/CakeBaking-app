package com.udacity.baking_app.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.udacity.baking_app.R;
import com.udacity.baking_app.data.database.Recipe;
import com.udacity.baking_app.data.network.ServiceGenerator;

import java.util.ArrayList;

import static com.udacity.baking_app.data.network.ServiceGenerator.EXTRA_BUNDLE;
import static com.udacity.baking_app.data.network.ServiceGenerator.EXTRA_DATA;

public class RecipeDetailListActivity extends AppCompatActivity {

    private RecipeDetailMasterListFragment detailListFragment;
    private PlayerFragment playerFragment;

    private long recipe_Id;
    private boolean twoPane;


    private static final String LOG_TAG = RecipeDetailListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: onCreate");
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        Bundle bundle = intent.getBundleExtra(EXTRA_BUNDLE);
        ArrayList<Recipe> recipeArrayList = bundle.getParcelableArrayList(EXTRA_DATA);

        if (recipeArrayList == null) {
            // EXTRA_DATA not found in intent
            closeOnError();
            return;
        }

        Recipe recipe = recipeArrayList.get(0);
        if (recipe == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recipe_Id = recipe.getId();
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: recipe_Id=" + recipe_Id);

        setTitle(recipe.getName());
        setContentView(R.layout.activity_recipe_detail);
        if (findViewById(R.id.video_container_tab) != null) {
            twoPane = true;
            Bundle bundleFragment = new Bundle();
//            bundleFragment.putLong(ServiceGenerator.RECIPE_ID, recipe_Id);
            bundleFragment.putBoolean(ServiceGenerator.IS_TWO_PANE, twoPane);
            FragmentManager fragmentManager = getSupportFragmentManager();
            playerFragment = new PlayerFragment();
            playerFragment.setArguments(bundleFragment);
            fragmentManager.beginTransaction()
                    .add(R.id.video_container_tab, new PlayerFragment()).commit();
        }

        if (savedInstanceState == null) {
            Bundle bundleFragment = new Bundle();
            bundleFragment.putLong(ServiceGenerator.RECIPE_ID, recipe_Id);
            bundleFragment.putBoolean(ServiceGenerator.IS_TWO_PANE, twoPane);
            bundleFragment.putString(ServiceGenerator.RECIPE_NAME, recipe.getName());
            FragmentManager fragmentManager = getSupportFragmentManager();
            detailListFragment = new RecipeDetailMasterListFragment();
            detailListFragment.setArguments(bundleFragment);
            fragmentManager.beginTransaction()
                    .replace(R.id.master_recipe_detail_list_fragment_container, detailListFragment).commit();
        }
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: Detail Activity ");
    }

    @Override
    protected void onResume() {
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "onResume 1");
        super.onResume();
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "onResume 2");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "onRestoreInstanceState 1");
        if (ServiceGenerator.LOCAL_LOGD)
            super.onRestoreInstanceState(savedInstanceState);
        Log.d(LOG_TAG, "onRestoreInstanceState 2");
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
