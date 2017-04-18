package com.example.x00075294.impulsevideo;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import Model.Profile;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.x00075294.impulsevideo.LaunchActivity.SHAREDPREFFILE;
import static com.example.x00075294.impulsevideo.LaunchActivity.TOKENPREF;
import static com.example.x00075294.impulsevideo.LaunchActivity.USERIDPREF;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "IMP:Profile -->: ";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String IMAGELOCAL ="prof";
    private static final String BlOB_CONN ="DefaultEndpointsProtocol=https;AccountName=impstaging;AccountKey=1BDdeZYFU+DLLrMLaHwcqPcSdzPT20rASvuZZ3wsVWxdq3SGJjZL2Xt4ACiaIiwvRgQfHyiJrz2YFgfGNyaWvg==;EndpointSuffix=core.windows.net;";
    private static final String BlOB_KEY ="1BDdeZYFU+DLLrMLaHwcqPcSdzPT20rASvuZZ3wsVWxdq3SGJjZL2Xt4ACiaIiwvRgQfHyiJrz2YFgfGNyaWvg==";
    private static final String BLOB_NAME = "impstaging;";
    public static final String storageConnectionString = BlOB_CONN+ BLOB_NAME+BlOB_KEY;
    private MobileServiceTable<Profile> mProfileTable;
    private Bitmap bm;
    //ui elements to manipulate
    private EditText uName;
    private EditText loca;
    private EditText bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        uName = (EditText) findViewById(R.id.input_uname);
        loca = (EditText) findViewById(R.id.input_loc);
        bio = (EditText) findViewById(R.id.input_bio);
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
        /*
      Progress spinner to use for table operations
     */
        ProgressDialog mProgressDialog = new ProgressDialog(this);
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
            mProfileTable = mClient.getTable(Profile.class);
            if (loadUserTokenCache(mClient)) {
                Log.v(TAG, "Found Previous Login");
            }
            new LoadProfileDetails().execute();
        } catch (MalformedURLException e) {
            Log.v(TAG, "MAlformed Url");
            //createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            //createAndShowDialog(e, "Error");
            Log.v(TAG, "General Error");
        }
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        String imageId = prefs.getString(IMAGELOCAL, null);
        String profileId = prefs.getString(USERIDPREF, null);
        if(profileId != null) {
            profileId = profileId.substring(4);
            Log.v(TAG, " "+profileId);
        }
        CircleImageView prof = (CircleImageView) findViewById(R.id.profile_image);
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
        Button upload = (Button) findViewById(R.id.imageButton);
        final String finalProfileId = profileId;
        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if(validateForm()){
                    String userName = uName.getText().toString();
                    String location = loca.getText().toString();
                    String bi = bio.getText().toString();
                    Profile p = new Profile(finalProfileId,userName,location,bi);
                    new uploadProfilePhoto().execute(p);
                }
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
    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    private void performFileSearch() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        }
        else{
            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            final int takeFlags = data.getFlags() & (
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            try {
                Bitmap bitmap = getBitmapFromUri(uri);
                Log.v(TAG, String.valueOf(bitmap));
                SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(IMAGELOCAL,uri.toString());
                editor.commit();
                CircleImageView profile = (CircleImageView) findViewById(R.id.profile_image);
                profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public Bitmap loadImage(Uri in) throws IOException {
        return MediaStore.Images.Media.getBitmap(getContentResolver(), in);
    }
    private boolean validateForm() {
        boolean valid = true;
        String userName = uName.getText().toString();
        String location = loca.getText().toString();
        String bi = bio.getText().toString();
        if (userName.isEmpty() || userName.length() <4) {
            uName.setError("Must Not be Empty or less than 4 characters!");
            valid = false;
        } else {
            uName.setError(null);
        }

        if (location.isEmpty() || location.length() < 4 || location.length() > 20) {
            loca.setError("Location should be between 4 and 10 characters");
            valid = false;
        } else {
            loca.setError(null);
        }
        if (bi.isEmpty() || bi.length() < 4 || bi.length() > 200) {
            loca.setError("Location should be between 4 and 200 characters");
            valid = false;
        } else {
            bio.setError(null);
        }
        return valid;
    }

    class uploadProfilePhoto extends AsyncTask<Profile, Void, Void> {
        final SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        String profileId = prefs.getString(USERIDPREF, null);
        String profUrl;
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ProfileActivity.this);
            pd.setMessage("loading");
            pd.show();
        }
        @Override
        protected Void doInBackground(Profile... profiles) {
            Profile p;
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
                final String filePath = prefs.getString(IMAGELOCAL,null);
                // Create or overwrite the "myimage.jpg" blob with contents from a local file.
                CloudBlockBlob blob = container.getBlockBlobReference("profileImg.jpeg"); // "file:///mnt/sdcard/FileName.mp3"
                Log.v(TAG, "Uploading file");
                Uri vidPath = Uri.parse(filePath);
                InputStream fileInputStream=getBaseContext().getContentResolver().openInputStream(vidPath);
                //File source = new FilefromUri(path);
                blob.upload(fileInputStream,fileInputStream.available());
                profUrl = blob.getUri().toURL().toString();
                Log.v(TAG, "Located at " + blob.getUri().toURL().toString());
                SharedPreferences.Editor editor = prefs.edit();
                String IMAGEBlOB = "blob";
                editor.putString(IMAGEBlOB,blob.getUri().toString());
                editor.apply();
                try {
                    p =profiles[0];
                    p.setUrl(profUrl);
                    mProfileTable.update(p).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
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
            }
        }
    }
    class LoadProfileDetails extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Profile lookup;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ProfileActivity.this);
            pd.setMessage("loading");
            pd.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
            String profileId = prefs.getString(USERIDPREF, null);
            if(profileId != null) {
                profileId = profileId.substring(4);
            }
            // Do your request
            try
            {
                lookup = mProfileTable.lookUp(profileId).get();

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
                //uName.setText(result.getClass().getName());
                //Log.v(TAG,lookup.getUsername());
                uName.setText(lookup.getUsername(), TextView.BufferType.EDITABLE);
                loca.setText(lookup.getLocation(), TextView.BufferType.EDITABLE);
                bio.setText(lookup.getBio(), TextView.BufferType.EDITABLE);
                Log.v(TAG, "Download Completed :)");
                pd.dismiss();
            }
        }
    }
}