package com.example.x00075294.impulsevideo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devbrackets.android.exomedia.core.video.scale.ScaleType;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;
import com.squareup.okhttp.OkHttpClient;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import Model.Comment;
import Model.CommentAdapter;
import Model.Profile;
import Model.Video;

import static com.example.x00075294.impulsevideo.LaunchActivity.SHAREDPREFFILE;
import static com.example.x00075294.impulsevideo.LaunchActivity.TOKENPREF;
import static com.example.x00075294.impulsevideo.LaunchActivity.USERIDPREF;
import static com.example.x00075294.impulsevideo.VideoPreviewActivity.isConnectedToInternet;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ViewActivity extends AppCompatActivity {
    private static final String TAG = "IMP: VideoView ->";
    private MobileServiceTable<Comment> mCommentTable;
    private List<Comment> commentsResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Bundle extras = getIntent().getExtras();
        Uri myUri = Uri.parse(extras.getString("videoUri"));
        String title = extras.getString("title");
        String description = extras.getString("desc");
        final String vidId = extras.getString("vidId");
        final String profileId = extras.getString("prof");
        final View tog = (View) findViewById(R.id.video_view_info);
        Drawable myIcon = getResources().getDrawable(R.drawable.ic_chrome_reader_mode_black_24dp);
        Drawable myIcon2 = getResources().getDrawable(R.drawable.ic_replay_black_24dp);
        final VideoView v = (VideoView) findViewById(R.id.video_view_watch);
        /**
         * make connection to app back end
         * set timeout time longer
         * Will fail if no internet
         * **/
        if (isConnectedToInternet(this)) {
            try {
                MobileServiceClient mClient = new MobileServiceClient(
                        "https://impulsevid.azurewebsites.net",
                        this
                );
                Log.v(TAG, " Succesful Hookup");
                // Extend timeout from default of 10s to 20s
                mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                    @Override
                    public OkHttpClient createOkHttpClient() {
                        OkHttpClient client = new OkHttpClient();
                        client.setReadTimeout(20, TimeUnit.SECONDS);
                        client.setWriteTimeout(20, TimeUnit.SECONDS);
                        return client;
                    }
                });
                //local copy of table for manipulations to be performed on
                MobileServiceTable<Video> mVideoTable = mClient.getTable(Video.class);
                MobileServiceTable<Profile> mProfileTable = mClient.getTable(Profile.class);
                mCommentTable = mClient.getTable("Comment",Comment.class);
                //load the verified user from google sign in
                if (loadUserTokenCache(mClient)) {
                    Log.v(TAG, "Found Previous Login");
                }
                //download user profile details and add to acreen
            } catch (MalformedURLException e) {
                Log.v(TAG, "MAlformed Url");
                //createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
            } catch (Exception e) {
                //createAndShowDialog(e, "Error");
                Log.v(TAG, "General Error");
            }
        } else {
            //noinspection deprecation
            Snackbar.make(v, "No Internet Connection please check Settings", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (isConnectedToInternet(getBaseContext())) {
                                finish();
                                startActivity(getIntent());
                            }
                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                    .show();
        }
        v.setScaleType(ScaleType.NONE);
        new LoadVideoComments().execute(vidId);
        FloatingActionButton comment = (FloatingActionButton) findViewById(R.id.ViewFab);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LovelyTextInputDialog(v.getContext(), R.style.TintTheme)
                        .setTopColorRes(R.color.primary)
                        .setTitle("Enter Comment")
                        .setMessage("Be friendly :)")
                        .setIcon(R.drawable.ic_insert_comment_black_24dp)
                        .setInputFilter("Invalid Entry", new LovelyTextInputDialog.TextFilter() {
                            @Override
                            public boolean check(String text) {
                                return text.length() > 4;
                            }
                        })
                        .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                            @Override
                            public void onTextInputConfirmed(String text) {
                                Comment c = new Comment(profileId, vidId, text);
                                new uploadComment().execute(c);
                                Toast.makeText(ViewActivity.this, "Comment Posted", Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
            }
        });
        final TextView titletext = (TextView) findViewById(R.id.view_disp_title);
        final TextView desc = (TextView) findViewById(R.id.view_desc);
        Button dismiss = (Button) findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tog.setVisibility(View.INVISIBLE);
            }
        });
        titletext.setText(title);
        desc.setText(description);
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
                tog.setVisibility(View.VISIBLE);
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

    private boolean loadUserTokenCache(MobileServiceClient client) {
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        String userId = prefs.getString(USERIDPREF, null);
        if (userId == null)
            return false;
        String token = prefs.getString(TOKENPREF, null);
        if (token == null)
            return false;
        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        client.setCurrentUser(user);
        return true;
    }

    class uploadComment extends AsyncTask<Comment, Void, Void> {
        ProgressDialog pd;
        Profile lookup;

        @Override
        protected Void doInBackground(Comment... comments) {
            mCommentTable.insert(comments[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ViewActivity.this);
            pd.setMessage("loading");
            pd.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.dismiss();
            super.onPostExecute(result);
            finish();
            startActivity(getIntent());
        }
    }

    private void populateCommentList(List<Comment> a) {
        // Construct the data source
        ArrayList<Comment> arrayOfUsers = (ArrayList<Comment>) a;
        // Create the adapter to convert the array to views
        CommentAdapter adapter = new CommentAdapter(this, arrayOfUsers);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.commentResult);
        listView.setAdapter(adapter);
    }

    private class LoadVideoComments extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            try {
                commentsResults = mCommentTable
                        .where()
                        .field("videoID").eq(strings[0]).orderBy("createdAt", QueryOrder.Descending)
                        .execute()
                        .get();
                Log.v(TAG,"Found " + commentsResults.size() + "Comments");
            } catch (Exception e) {
                // Output the stack trace.
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            populateCommentList(commentsResults);

        }
    }
}
