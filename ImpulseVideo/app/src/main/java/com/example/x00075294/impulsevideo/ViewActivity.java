package com.example.x00075294.impulsevideo;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import com.devbrackets.android.exomedia.core.video.scale.ScaleType;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Drawable myIcon = getResources().getDrawable( R.drawable.ic_chrome_reader_mode_black_24dp);
        Drawable myIcon2 = getResources().getDrawable( R.drawable.ic_replay_black_24dp);
        final VideoView v = (VideoView) findViewById(R.id.video_view_watch);
        v.setScaleType(ScaleType.NONE);
        Bundle extras = getIntent().getExtras();
        Uri myUri = Uri.parse(extras.getString("videoUri"));
        v.setVideoURI(myUri);
        v.getVideoControls().setNextButtonRemoved(false);
        v.getVideoControls().setPreviousButtonRemoved(false);
        v.getVideoControls().setNextDrawable(myIcon2);
        v.getVideoControls().setPreviousDrawable(myIcon);
        v.getVideoControls().setButtonListener(new VideoControlsButtonListener() {
            @Override
            public boolean onPlayPauseClicked() {
                return false;
            }

            @Override
            public boolean onPreviousClicked() {
                return false;
            }

            @Override
            public boolean onNextClicked() {
                v.restart();
                return false;
            }

            @Override
            public boolean onRewindClicked() {
                return false;
            }

            @Override
            public boolean onFastForwardClicked() {
                return false;
            }
        });
    }
}
