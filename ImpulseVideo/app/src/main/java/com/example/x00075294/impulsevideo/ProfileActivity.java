package com.example.x00075294.impulsevideo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import Model.Profile;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.x00075294.impulsevideo.LaunchActivity.SHAREDPREFFILE;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "IMP:Profile -->: ";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String IMAGELOCAL ="prof";
    public MobileServiceClient mClient;
    private MobileServiceTable<Profile> mProfileTable;
    /**
     * Progress spinner to use for table operations
     */
    private ProgressBar mProgressBar;
    public CircleImageView prof;
    public Bitmap bm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileSearch();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            mClient = new MobileServiceClient(
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
            mProfileTable = mClient.getTable(Profile.class);
        } catch (MalformedURLException e) {
            Log.v(TAG, "MAlformed Url");
            //createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            //createAndShowDialog(e, "Error");
            Log.v(TAG, "General Error");
        }

        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        String imageId = prefs.getString(IMAGELOCAL, null);
        prof = (CircleImageView) findViewById(R.id.profile_image);
        if(imageId != null)
        {
            Uri imgUri = Uri.parse(imageId);
            try {
                bm = getBitmapFromUri(imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v(TAG,  "To here " + imgUri.toString());
        }
        prof.setImageBitmap(bm);
    }
    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");

        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }
    private void updateProfile() throws MobileServiceException, ExecutionException, InterruptedException {
        //assume that mClient Successful in onCreate
        if (mClient == null) {
            return;
        }
        //Async task for long running background applications
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //checkItemInTable();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //getList.setText("U hit he button");
                        }
                    });
                } catch (final Exception e) {
                    //createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        runAsyncTask(task);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = getBitmapFromUri(uri);
                Log.v(TAG, String.valueOf(bitmap));
                SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(IMAGELOCAL,uri.toString());
                editor.commit();
                CircleImageView profile = (CircleImageView) findViewById(R.id.profile_image);
                profile.setImageBitmap(bitmap);
               //ImageView imageView = (ImageView) findViewById(R.id.imageView);
                // imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void getStuff() throws MobileServiceException, ExecutionException, InterruptedException {
        //assume that mClient Successful in onCreate
        if (mClient == null) {
            return;
        }
        //Async task for long running background applications
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //checkItemInTable();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                } catch (final Exception e) {
                    //createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        runAsyncTask(task);
    }
    public Bitmap loadImage(Uri in) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), in);
        return bitmap;
    }
}
