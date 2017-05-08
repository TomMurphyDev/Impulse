package com.example.x00075294.impulsevideo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import Model.BlobInformation;
import Model.Profile;
import Model.Video;
import Model.VideoBlobInformation;

import static com.example.x00075294.impulsevideo.LaunchActivity.SHAREDPREFFILE;
import static com.example.x00075294.impulsevideo.LaunchActivity.TOKENPREF;
import static com.example.x00075294.impulsevideo.LaunchActivity.USERIDPREF;

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
    EditText title,description;
    Spinner cat;
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
        View parentLayout = findViewById(R.id.content_video_preview);
        //ui hookup
        title = (EditText) findViewById(R.id.editTextTitle);
        description = (EditText) findViewById(R.id.editDescription);
        cat = (Spinner) findViewById(R.id.spinner);
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
                if(validateForm()){
                    //temp final variable for uploading to
                    String ti = title.getText().toString();
                    String desc = description.getText().toString();
                    String category = cat.getSelectedItem().toString();
                    Video up = new Video(ti,desc,category);
                    new uploadVideo().execute(up);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * make connection to app back end
         * set timeout time longer
         * Will fail if no internet
         * **/
        if(isConnectedToInternet(this))
        {
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
                //load the verified user from google sign in
                if (loadUserTokenCache(mClient)) {
                    Log.v(TAG, "Found Previous Login");
                }
                //download user profile details and add to acreen
            } catch (MalformedURLException e) {
                Log.v(TAG, "MAlformed Url");
                //createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
            } catch (Exception e){
                //createAndShowDialog(e, "Error");
                Log.v(TAG, "General Error");
            }
        }
        else{
            fab.setVisibility(View.INVISIBLE);
            Snackbar.make(parentLayout, "No Internet Connection please check Settings", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(isConnectedToInternet(getBaseContext()))
                            {
                                finish();
                                startActivity(getIntent());
                            }
                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
        }

    }

    private boolean validateForm() {
        boolean valid = true;
        String ti = title.getText().toString();
        String desc = description.getText().toString();
        if (ti.isEmpty() || ti.length() <4) {
            title.setError("Must Not be Empty or less than 4 characters!");
            valid = false;
        } else {
            title.setError(null);
        }
        if (desc.isEmpty() || desc.length() < 4 || desc.length() > 240) {
            description.setError("Location should be between 4 and 240 characters");
            valid = false;
        } else {
            description.setError(null);
        }
        return valid;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, " Succesful Activity start run ui and inflate");
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
    public static boolean isConnectedToInternet(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    private void loadMain() {
        Log.v(TAG, "Insert passed");
        Log.v(TAG, "Load main ..... ");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    class uploadVideo extends AsyncTask<Video, Void, Void> {
        final SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        String profileId = prefs.getString(USERIDPREF, null);
        String vidUrl;
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(VideoPreviewActivity.this);
            pd.setMessage("loading");
            pd.show();
        }
        @Override
        protected Void doInBackground(Video... videos) {
            Video up;
            if(profileId != null) {
                profileId = profileId.substring(4);
            }
            // Do your request
            try
            {
                // Retrieve storage account from connection-string.
                CloudStorageAccount storageAccount = CloudStorageAccount.parse(BlOB_CONN);
                // Create the blob client.
                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
                // Get a reference to a container.
                // The container name must be lower case
                CloudBlobContainer container = blobClient.getContainerReference(profileId);
                // Create the container if it does not exist.
                container.createIfNotExists();
                // Define the path to a local file.
                Bundle extras = getIntent().getExtras();
                Uri vidUri=  Uri.parse(extras.getString("videoUri"));
                CloudBlockBlob blob = container.getBlockBlobReference(UUID.randomUUID().toString().toLowerCase()+".mp4");
                BlobContainerPermissions open = new BlobContainerPermissions();
                open.setPublicAccess(BlobContainerPublicAccessType.BLOB);
                container.uploadPermissions(open);
                Log.v(TAG, "Uploading file to " +container.getName()+ "");
                InputStream fileInputStream=getBaseContext().getContentResolver().openInputStream(vidUri);
                //File source = new FilefromUri(path);
                blob.upload(fileInputStream,fileInputStream.available());
                vidUrl = blob.getUri().toURL().toString();
                Log.v(TAG, "Located at " + blob.getUri().toURL().toString());

                CloudQueueClient queueClient = storageAccount.createCloudQueueClient();
                CloudQueue vidQ = queueClient.getQueueReference("videorequest");
                vidQ.createIfNotExists();
                VideoBlobInformation convert = new VideoBlobInformation(blob.getUri(),blob.getName(),profileId,blob.getName());
                CloudQueueMessage test = new CloudQueueMessage(new Gson().toJson(convert));
                vidQ.addMessage(test);
                SharedPreferences.Editor editor = prefs.edit();
                String IMAGEBlOB = "blob";
                editor.putString(IMAGEBlOB,blob.getUri().toString());
                editor.apply();
                try {
                    up =videos[0];
                    up.setId(blob.getName());
                    up.setBlobUrl(vidUrl);
                    up.setProfileID(profileId);
                    mVideoTable.insert(up).get();
                } catch (InterruptedException e) {
                    Log.v(TAG, e.getMessage());
                }
            }
            catch (Exception e)
            {
                // Output the stack trace.
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pd != null)
            {
                Log.v(TAG, "Upload Completed :)");
                pd.dismiss();
                loadMain();
                finish();
            }
        }
    }
}
