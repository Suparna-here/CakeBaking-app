package com.udacity.baking_app.ui.list;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.baking_app.R;
import com.udacity.baking_app.data.network.ServiceGenerator;

public class MainActivity extends AppCompatActivity {

    private MasterListFragment masterListFragment;

    private static final String SORT_KEY = "Ordering_Sequence";
    private String sort_by;

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        FragmentManager fragmentManager = getSupportFragmentManager();
        masterListFragment= (MasterListFragment) fragmentManager.findFragmentById(R.id.master_list_fragment_main);

        if(ServiceGenerator.LOCAL_LOGD)
        Log.d(LOG_TAG, "su: sort By "+sort_by);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // Respond to the action bar's Up/Home button
        //no inspection Simplifiable If Statement

       /*  if (id == R.id.action_popularity) {//Show Popular Movies
           sort_by = ServiceGenerator.ORDER_POPULARITY;
            masterListFragment.setRecipesBasedOnSortOrder(sort_by);
            return true;
        } else if (id == R.id.action_toprated) {//Show Top Rated Movies
            sort_by = ServiceGenerator.ORDER_TOPRATED;
            masterListFragment.setRecipesBasedOnSortOrder(sort_by);
            return true;
        } else if (id == R.id.action_favourite) {//Show Top Favourite Movies
            sort_by = ServiceGenerator.ORDER_FAVOURITE;
            masterListFragment.setRecipesBasedOnSortOrder(sort_by);
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
}
