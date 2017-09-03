package com.impulsevid.x00075294.impulsevideo;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
//Login in imports
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.impulsevid.x00075294.impulsevideo.MainActivity;
import com.impulsevid.x00075294.impulsevideo.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceActivityResult;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;
import com.yarolegovich.lovelydialog.LovelyDialogCompat;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import Model.Profile;
/**
 *Launcher activity login and cache of user details
 */
@SuppressWarnings("deprecation")
public class LaunchActivity extends AppCompatActivity {
    private static final int GOOGLE_LOGIN_REQUEST_CODE = 1;
    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;
    private MobileServiceTable<Profile> mProfileTable;

    // tag for log cat filtering
    private static final String TAG = "IMP:Launcher -->: ";
    /**
     * @param savedInstanceState
     * Layout inflation and connection to azure app service client
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        //assign mClient var to app service
        //set custom font on launch screen
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/KOMIKAX_.ttf");
        TextView myTextView = (TextView)findViewById(R.id.launch_title);
        myTextView.setTypeface(myTypeface);

        View parentLayout = findViewById(R.id.activity_launch);
        try {
            // Create the Mobile Service Client instance, using the provided
            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://impulsevid.azurewebsites.net",
                    this);
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
            Log.v(TAG,"There was an error creating the Mobile Service. Verify the URL");
        } catch (Exception e){
            Log.v(TAG,e.getMessage());
        }
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticate();
            }
        });
        Log.v(TAG,"Begin Authorization");

        // authenticate();
    }
    private void authenticate() {

        // If we failed to load a token cache, login and create a token cache

        Log.v(TAG,"Begin Google Login auth method");
        try{
            // Login with Google with offline permission. Offline permission is required by refresh tokens.
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("access_type", "offline");
            mClient.login("Google","impulsevid",GOOGLE_LOGIN_REQUEST_CODE,parameters);
        }
        catch(Exception e)
        {
            Log.v(TAG, "Begin Auth Error " + e.getMessage());
        }
        if(mClient.getCurrentUser() != null){
            cacheUserToken(mClient.getCurrentUser());
        }

    }
    /***************************************************************************************
     *    Title: <Adding Authorization to your Android Application>
     *    Author: Microsoft
     *    Date: 1/3/17
     *    Code version: 1.0
     *    Availability: https://github.com/MicrosoftDocs/azure-docs
     *
     ***************************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String provider = findProviderFromLoginRequestCode(requestCode);
        // When request completes
        if (resultCode == RESULT_OK) {
            // Check the request code matches the one we send in the login request
            if (requestCode == GOOGLE_LOGIN_REQUEST_CODE) {
                MobileServiceActivityResult result = mClient.onActivityResult(data);
                if (result.isLoggedIn()) {
                    // login succeeded
                    // login succeeded
                    final String text = String.format("%s Login succeeded.\nUserId: %s, authenticationToken: %s\n", provider,
                            mClient.getCurrentUser().getUserId(),
                            mClient.getCurrentUser().getAuthenticationToken());
                    Log.v(TAG,text);
                    mClient.refreshUser(new UserAuthenticationCallback() {
                        @Override
                        public void onCompleted(MobileServiceUser user, Exception exception, ServiceFilterResponse response) {
                            String text2;
                            if (user != null && exception == null) {
                                // refreshUser succeeded
                                text2 = String.format("%s RefreshUser succeeded.\nUserId: %s, authenticationToken: %s", provider, user.getUserId(), user.getAuthenticationToken());
                                Log.v(TAG,text2);
                            } else {
                                // refreshUser failed
                                //this always fails but forces a refresh of the profile tkn
                                text2 = String.format("%s RefreshUser failed.\nError: %s", provider, exception.getCause());
                                Log.v(TAG,text2);
                            }
                        }
                    });
                    if(mClient.getCurrentUser() != null){
                        cacheUserToken(mClient.getCurrentUser());
                        new ProfileCheck().execute();
                    }
                }
            }
        }
    }
    //google provider sign in selection
    private String findProviderFromLoginRequestCode(int requestCode) {
        String provider;
        switch (requestCode) {
            case GOOGLE_LOGIN_REQUEST_CODE:
                provider = "Google";
                break;
            default:
                throw new IllegalArgumentException("request code does not match any provider");
        }
        return provider;
    }
    //used in other activity to load details...
    private boolean loadUserTokenCache(MobileServiceClient client)
    {
        //open editor file sharedprefile
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        String userId = prefs.getString(USERIDPREF, null);
        if (userId == null)
            //if false no exiting id with that
            return false;
        String token = prefs.getString(TOKENPREF, null);
        if (token == null)
            //same as above
            return false;
        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        client.setCurrentUser(user);
        return true;
    }
    //This checks if the login with google id exists in the database
    class ProfileCheck extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Profile lookup;
        //load saved user id
        final SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        final String profileId = prefs.getString(USERIDPREF, null);
        //trim off prefix
        final String id = profileId.substring(4);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //purely for debug
            Log.v(TAG,"Db conn");
        }

        @Override
        protected Void doInBackground(Void... params) {
            // search for the logged in user if any
            try {
                lookup = mProfileTable.lookUp(id).get();
                Log.v(TAG,"Lookup in background " + lookup.getUsername());
            } catch (Exception e) {
                // Output Result
                Log.v(TAG,"NOT FOUND IN DB" + e.getMessage());
                Profile p = new Profile(id);
                try {
                    // create new record
                    Log.v(TAG,"Inserting new profile to db");
                    mProfileTable.insert(p).get();
                } catch (InterruptedException | ExecutionException f) {
                    Log.v(TAG,e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //on positive outcome load the MainActivity.class
            loadMain();
        }
    }
    private void cacheUserToken(MobileServiceUser user)
    {
        //update/ save the current logged in user
        Log.v(TAG, "Caching user from google auth ");
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(USERIDPREF, user.getUserId());
        editor.putString(TOKENPREF, user.getAuthenticationToken());
        editor.commit();
    }
    private void loadMain() {
        //load mainactivity class method
        Log.v(TAG, "Load main ..... ");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    //redundant as webview login handles no internet connection
    private static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
