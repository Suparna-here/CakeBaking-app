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




 /* private SimpleExoPlayer player;
  private PlayerView playerView;
  private ComponentListener componentListener;*/

/*  private long playbackPosition;
  private int currentWindow;
  private boolean playWhenReady = true;*/

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
        /* if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle = getIntent().getExtras();
            PlayerFragment videoFragment = new PlayerFragment();
            videoFragment.setArguments(bundle);
            fragmentCreated = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.video_fragment_container, videoFragment).commit();
        }*/
   /* componentListener = new ComponentListener();
    playerView = findViewById(R.id.video_view);*/
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
        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

  /*@Override
  public void onStart() {
    super.onStart();
    if (Util.SDK_INT > 23) {
      initializePlayer();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    hideSystemUi();
    if ((Util.SDK_INT <= 23 || player == null)) {
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
      // a factory to create an AdaptiveVideoTrackSelection
      TrackSelection.Factory adaptiveTrackSelectionFactory =
          new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
      // using a DefaultTrackSelector with an adaptive video selection factory
      player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this),
          new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());
      player.addListener(componentListener);
      player.addVideoDebugListener(componentListener);
      player.addAudioDebugListener(componentListener);
      playerView.setPlayer(player);
      player.setPlayWhenReady(playWhenReady);
      player.seekTo(currentWindow, playbackPosition);
    }
    MediaSource mediaSource = buildMediaSource(Uri.parse(getString(R.string.media_url_dash)));
    player.prepare(mediaSource, true, false);
  }

  private void releasePlayer() {
    if (player != null) {
      playbackPosition = player.getCurrentPosition();
      currentWindow = player.getCurrentWindowIndex();
      playWhenReady = player.getPlayWhenReady();
      player.removeListener(componentListener);
      player.removeVideoDebugListener(componentListener);
      player.removeAudioDebugListener(componentListener);
      player.release();
      player = null;
    }
  }

  private MediaSource buildMediaSource(Uri uri) {
    DataSource.Factory manifestDataSourceFactory = new DefaultHttpDataSourceFactory("ua");
    DashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(
        new DefaultHttpDataSourceFactory("ua", BANDWIDTH_METER));
    return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory)
        .createMediaSource(uri);
  }

  @SuppressLint("InlinedApi")
  private void hideSystemUi() {
    playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
        | View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
  }

  private class ComponentListener extends Player.DefaultEventListener implements
      VideoRendererEventListener, AudioRendererEventListener {

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

    // Implementing VideoRendererEventListener.

    @Override
    public void onVideoEnabled(DecoderCounters counters) {
      // Do nothing.
    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
      // Do nothing.
    }

    @Override
    public void onVideoInputFormatChanged(Format format) {
      // Do nothing.
    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {
      // Do nothing.
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
      // Do nothing.
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {
      // Do nothing.
    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {
      // Do nothing.
    }

    // Implementing AudioRendererEventListener.

    @Override
    public void onAudioEnabled(DecoderCounters counters) {
      // Do nothing.
    }

    @Override
    public void onAudioSessionId(int audioSessionId) {
      // Do nothing.
    }

    @Override
    public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
      // Do nothing.
    }

    @Override
    public void onAudioInputFormatChanged(Format format) {
      // Do nothing.
    }

    @Override
    public void onAudioSinkUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
      // Do nothing.
    }

    @Override
    public void onAudioDisabled(DecoderCounters counters) {
      // Do nothing.
    }

  }*/

}
