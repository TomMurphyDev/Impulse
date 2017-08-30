package com.example.x00075294.impulsevideo;
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

import Azure.AzureServiceAdapter;
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
                                text2 = String.format("%s RefreshUser failed.\nError: %s", provider, exception.getCause());
                                Log.v(TAG,text2);
                            }
                        }
                    });
                    if(mClient.getCurrentUser() != null){
                        cacheUserToken(mClient.getCurrentUser());
                        new ProfileCheck().execute();
                        loadMain();
                    }
                }
            }
        }
    }

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

    private boolean loadUserTokenCache(MobileServiceClient client)
    {
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
    class ProfileCheck extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Profile lookup;
        final SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        final String profileId = prefs.getString(USERIDPREF, null);
        final String id = profileId.substring(4);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG,"Db conn");
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Do your request
            try {
                lookup = mProfileTable.lookUp(id).get();
                Log.v(TAG,"Lookup in background " + lookup.getUsername());
            } catch (Exception e) {
                // Output the stack trace.
                Log.v(TAG,"Lookup error" + e.getMessage());
                e.printStackTrace();
            }
            if (lookup != null) {
                Log.v(TAG,"Lookup found previous login so no need to insert");
            }
            else
            {
                Profile p = new Profile(id);
                try {
                    Log.v(TAG,"Inserting new profile to db");
                    mProfileTable.insert(p).get();
                } catch (InterruptedException e) {
                    Log.v(TAG,e.getMessage());
                } catch (ExecutionException e) {
                    Log.v(TAG,e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.v(TAG,"jUMP tO MAIN");
            super.onPostExecute(result);

        }
    }
    private void cacheUserToken(MobileServiceUser user)
    {
        Log.v(TAG, "Caching user from google auth ");
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(USERIDPREF, user.getUserId());
        editor.putString(TOKENPREF, user.getAuthenticationToken());
        editor.commit();
    }
    private void loadMain() {
        Log.v(TAG, "Load main ..... ");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}

