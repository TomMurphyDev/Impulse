package com.example.x00075294.impulsevideo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.test.ActivityTestCase;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import Model.Profile;
import Model.Video;
import Model.VideoAdapter;

import static com.example.x00075294.impulsevideo.LaunchActivity.SHAREDPREFFILE;
import static com.example.x00075294.impulsevideo.LaunchActivity.TOKENPREF;
import static com.example.x00075294.impulsevideo.LaunchActivity.USERIDPREF;
import static com.example.x00075294.impulsevideo.R.string.bio;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "IMP:Main -->: ";
    private static final int MY_PERMISSIONS_REQUEST_USE_CAMERA = 101;
    // --Commented out by Inspection (17/04/2017 23:26):private static final int MY_PERMISSIONS_WRITE_STORAGE = 102;
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static MobileServiceTable<Profile> mProfileTable;
    private MobileServiceTable<Video> mVideoTable;
    List<Video> results;

    @Override
    protected void onStart() {
        super.onStart();
        new LoadVideoDetails().execute("");
    }

    /**
     * @param savedInstanceState
     * initial connection made when added to stack
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // button to launch take video
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakeVideoIntent();
            }
        });
        //Handler hookup for nav drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO, Manifest.permission.MANAGE_DOCUMENTS}, MY_PERMISSIONS_REQUEST_USE_CAMERA);
        /**
         * make connection to app back end
         * set timeout time longer
         * Will fail if no internet
         * TODO add connection check  and snackbar warning if not
         * **/
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
            mVideoTable = mClient.getTable(Video.class);
            mProfileTable = mClient.getTable(Profile.class);
            //load the verified user from google sign in
            if (loadUserTokenCache(mClient)) {
                Log.v(TAG, "Connecting");
            }
            //download user profile details and add to acreen
        } catch (MalformedURLException e) {
            Log.v(TAG, "MAlformed Url");
            //createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e) {
            //createAndShowDialog(e, "Error");
            Log.v(TAG, "General Error");
        }
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
    private void populateVideoList(List<Video> a) {
        // Construct the data source
        ArrayList<Video> arrayOfUsers = (ArrayList<Video>) a;
        // Create the adapter to convert the array to views
        VideoAdapter adapter = new VideoAdapter(this, arrayOfUsers);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.resultlist);
        listView.setAdapter(adapter);
    }

    /**
     * Accessed on 2/3/16
     * <p>
     * Requesting permissions
     * <p>
     * link:https://developer.android.com/training/permissions/requesting.html
     * <p>
     * link:https://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_USE_CAMERA: {

                Map<String, Integer> perms = new HashMap<>();

                // Initial

                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);

                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.MANAGE_DOCUMENTS, PackageManager.PERMISSION_GRANTED);
                // Fill with results

                for (int i = 0; i < permissions.length; i++)

                    perms.put(permissions[i], grantResults[i]);

                // Check for ACCESS_FINE_LOCATION

                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

                        && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                        && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

                    // All Permissions Granted

                } else {

                    // Permission Denied

                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)

                            .show();

                }

            }

        }
    }

    /**
     * close drawer when pressed
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            loadProfile();
        } else if (id == R.id.nav_my_videos) {
            SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
            String userId = prefs.getString(USERIDPREF, null);
            userId = userId.substring(4);
            new LoadMyVideos().execute(userId);
        } else if (id == R.id.nav_trends) {
            new LoadVideoDetails().execute("");
        } else if (id == R.id.nav_Analytics) {

        } else if (id == R.id.nav_comedy) {
            new LoadVideoDetails().execute("comedy");
        } else if (id == R.id.nav_music) {
            new LoadVideoDetails().execute("music");
        }
        else if (id == R.id.nav_news) {
            new LoadVideoDetails().execute("news");
        }
        else if (id == R.id.nav_sport) {
            new LoadVideoDetails().execute("sport");
        }
        else if (id == R.id.nav_other) {
            new LoadVideoDetails().execute("other");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void loadProfile() {
        Log.v(TAG, "Load profile ..... ");
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void loadVideo() {
        Log.v(TAG, "Load Video..... ");
        //Intent intent = new Intent(this, FullscreenActivity.class);
        //startActivity(intent);
    }

    private void loadPreview(Uri v) {
        Log.v(TAG, "Load preview ..... ");
        Intent intent = new Intent(this, VideoPreviewActivity.class);
        intent.putExtra("videoUri", v.toString());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            loadPreview(videoUri);
        }
    }
    class LoadMyVideos extends AsyncTask<String, Void, Void> {
        ProgressDialog pd;
        Profile lookup;

        @Override
        protected Void doInBackground(String... strings) {
            SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
            String profileId = prefs.getString(USERIDPREF, null);
            if (profileId != null) {
                profileId = profileId.substring(4);
            }
                try {
                    results = mVideoTable
                            .where()
                            .field("profileID").eq(strings[0]).and().field("available").eq(true).orderBy("createdAt", QueryOrder.Descending)
                            .execute()
                            .get();
                } catch (Exception e) {
                    // Output the stack trace.
                    e.printStackTrace();
                }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("loading");
            pd.show();
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.v(TAG,"Found " + results.size() + " Videos");
            populateVideoList(results);
            pd.dismiss();
        }
    }
    public class LoadProfile extends AsyncTask<String,Void,Profile> {
        TextView t;
        Profile lookup;
        public LoadProfile(TextView p)
        {
            this.t = p;
        }
        @Override
        protected Profile doInBackground(String... strings) {
            try {
                lookup = mProfileTable.lookUp(strings[0]).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Profile profile) {
            super.onPostExecute(profile);
            if (lookup!= null) {
                t.setText(lookup.getUsername());
            }
        }
    }
    class LoadVideoDetails extends AsyncTask<String, Void, Void> {
        ProgressDialog pd;


        @Override
        protected Void doInBackground(String... strings) {
            SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
            String profileId = prefs.getString(USERIDPREF, null);
            if (profileId != null) {
                profileId = profileId.substring(4);
            }
            if (strings[0].isEmpty()) {
                // Do your request
                try {
                    results = mVideoTable
                            .where().field("available").eq(true).orderBy("createdAt", QueryOrder.Descending)
                            .execute()
                            .get();
                } catch (Exception e) {
                    // Output the stack trace.
                    e.printStackTrace();
                }
            }
            else
            {
                try {
                    results = mVideoTable
                            .where()
                            .field("category").eq(strings[0]).and().field("available").eq(true).orderBy("createdAt", QueryOrder.Descending)
                            .execute()
                            .get();
                } catch (Exception e) {
                    // Output the stack trace.
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("loading");
            pd.show();
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.v(TAG,"Found " + results.size() + " Videos");
            populateVideoList(results);
                pd.dismiss();
            }
        }
}
