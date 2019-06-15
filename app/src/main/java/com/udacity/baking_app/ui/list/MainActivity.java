package com.udacity.baking_app.ui.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.udacity.baking_app.R;
import com.udacity.baking_app.data.network.ServiceGenerator;
import com.udacity.baking_app.idlingResource.SimpleIdlingResource;

public class MainActivity extends AppCompatActivity {

    private MasterListFragment masterListFragment;

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        FragmentManager fragmentManager = getSupportFragmentManager();
        masterListFragment = (MasterListFragment) fragmentManager.findFragmentById(R.id.master_list_fragment_main);

        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: onCreate ");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

   /* @Override
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
        return super.onOptionsItemSelected(item);
    }*/
}
