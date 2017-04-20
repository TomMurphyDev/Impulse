package com.example.x00075294.impulsevideo;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import Model.Video;

public class VideoPreviewActivity extends AppCompatActivity {
    /*
     * Member Values For debugging and app service access
    **/
    private static final String TAG = "IMP:VidUp -->" ;
    private static final String BlOB_CONN ="DefaultEndpointsProtocol=https;AccountName=impstaging;AccountKey=1BDdeZYFU+DLLrMLaHwcqPcSdzPT20rASvuZZ3wsVWxdq3SGJjZL2Xt4ACiaIiwvRgQfHyiJrz2YFgfGNyaWvg==;EndpointSuffix=core.windows.net;";
    private static final String BlOB_KEY ="1BDdeZYFU+DLLrMLaHwcqPcSdzPT20rASvuZZ3wsVWxdq3SGJjZL2Xt4ACiaIiwvRgQfHyiJrz2YFgfGNyaWvg==";
    private static final String BLOB_NAME = "impstaging;";
    public static final String storageConnectionString = BlOB_CONN+ BLOB_NAME+BlOB_KEY;
    private MobileServiceTable<Video> mVideoTable;
    /*
    *ui elements to manipulate
    * Handlers
     */
    /**
     * On create called when activity is created provides initial connection to app service
     * and layout inflation
     * assigns on click properties to ui elements
     * **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        VideoView prev = (VideoView) findViewById(R.id.vipPrev);
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(prev);
        prev.setMediaController(mediaController);
        Bundle extras = getIntent().getExtras();
        Uri myUri=  Uri.parse(extras.getString("videoUri"));
        prev.setVideoURI(myUri);
        prev.start();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, " Succesful Activity start run ui and inflate");
    }

}
