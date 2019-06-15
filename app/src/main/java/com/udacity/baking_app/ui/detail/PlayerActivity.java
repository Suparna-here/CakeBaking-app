/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.baking_app.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.udacity.baking_app.R;
import com.udacity.baking_app.data.database.Step;
import com.udacity.baking_app.data.network.ServiceGenerator;

import java.util.ArrayList;

import static com.udacity.baking_app.data.network.ServiceGenerator.EXTRA_BUNDLE;
import static com.udacity.baking_app.data.network.ServiceGenerator.EXTRA_DATA;

/**
 * A fullscreen activity to play audio or video streams.
 */
public class PlayerActivity extends AppCompatActivity {

    // bandwidth meter to measure and estimate bandwidth
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String TAG = "PlayerActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        Bundle bundle = intent.getBundleExtra(EXTRA_BUNDLE);
        ArrayList<Step> stepList = bundle.getParcelableArrayList(EXTRA_DATA);
        if (stepList == null) {
            // EXTRA_DATA not found in intent
            closeOnError();
            return;
        }

        Step step = stepList.get(0);
        if (step == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }
        setTitle(bundle.getString(ServiceGenerator.RECIPE_NAME));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_video);

        Bundle bundleStep = new Bundle();
        bundleStep.putParcelableArrayList(EXTRA_DATA, stepList);
        PlayerFragment videoFragment = new PlayerFragment();
        videoFragment.setArguments(bundleStep);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.video_fragment_container, videoFragment).commit();
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
