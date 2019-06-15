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


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.udacity.baking_app.R;
import com.udacity.baking_app.data.database.Step;
import com.udacity.baking_app.data.network.ServiceGenerator;

import static android.support.constraint.Constraints.TAG;
import static com.udacity.baking_app.data.network.ServiceGenerator.EXTRA_DATA;

// This fragment displays all of the AndroidMe images in one large list
// The list appears as a grid of images
public class PlayerFragment extends Fragment {
    // bandwidth meter to measure and estimate bandwidth
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private ComponentListener componentListener;

    private Step selectedStep;
    private String videoUrl;
    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    private TextView mStepDescView;
    private ImageView mNoVideoUrl;
    private boolean twoPane;

    public static final String LOG_TAG = PlayerFragment.class.getSimpleName();

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    // Mandatory empty constructor
    public PlayerFragment() {
    }


    // Inflates the GridView of all AndroidMe images
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            twoPane = bundle.getBoolean(ServiceGenerator.IS_TWO_PANE);
            selectedStep = (Step) bundle.getParcelableArrayList(EXTRA_DATA).get(0);
        }
        final View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        componentListener = new ComponentListener();

        playerView = rootView.findViewById(R.id.video_view);
        mNoVideoUrl = rootView.findViewById(R.id.tv_no_video_url);
        mStepDescView = rootView.findViewById(R.id.tv_step_description);
        if (bundle != null) {
            mStepDescView.setText(selectedStep.getDescription());
            videoUrl = selectedStep.getVideoURL();

            if (!TextUtils.isEmpty(videoUrl)) {
                showVideoDataView();
            } else showErrorMessage();
        }
        // Return the root view
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            if (!TextUtils.isEmpty(videoUrl))
                initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            if (!TextUtils.isEmpty(videoUrl))
                initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getActivity()),
                    new DefaultTrackSelector(), new DefaultLoadControl());
            player.addListener(componentListener);
            playerView.setPlayer(player);

            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
        }

        MediaSource mediaSource = buildMediaSource(Uri.parse(videoUrl));
        player.prepare(mediaSource, true, false);
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.removeListener(componentListener);
            player.release();
            player = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab", null,
                        DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                        DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true)).
                createMediaSource(uri);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

               /* | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION*/);
    }

    private class ComponentListener extends Player.DefaultEventListener {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch (playbackState) {
                case Player.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    break;
                case Player.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    break;
                case Player.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    break;
                case Player.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
            Log.d(TAG, "changed state to " + stateString + " playWhenReady: " + playWhenReady);
        }

    }


    /**
     * This method will make the View for the recipe data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showVideoDataView() {
        // First, make sure the error is invisible
        mNoVideoUrl.setVisibility(View.INVISIBLE);
        // Then, make sure the movie data is visible
        playerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the internet connection error message visible and hide the RecipeRespopnse
     * View.
     */
    private void showErrorMessage() {
        // First, hide the currently visible data
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: mErrorMessageDisplay ");
        playerView.setVisibility(View.GONE);
        // Then, show the internet error
        mNoVideoUrl.setVisibility(View.VISIBLE);
    }

}
