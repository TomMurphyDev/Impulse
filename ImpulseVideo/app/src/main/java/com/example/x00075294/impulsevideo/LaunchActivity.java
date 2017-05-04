package com.example.x00075294.impulsevideo;
import android.content.DialogInterface;
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
//azure imports
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
//Login in imports
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;

import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import Model.Profile;
/**
 *Launcher activity login and cache of user details
 */
@SuppressWarnings("deprecation")
public class LaunchActivity extends AppCompatActivity {
    //Storage variable for client application
    private MobileServiceClient mClient;
    private MobileServiceTable<Profile> mToDoTable;
    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
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
        View parentLayout = findViewById(R.id.activity_launch);
        if(isConnectedToInternet(this))
        {
            //connect to app service
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
                Log.v(TAG, " Client factory set up");
                //try get the table that matches the model class Profile
                try
                {
                    mToDoTable = mClient.getTable(Profile.class);
                }
                catch(Exception c)
                {
                    Log.v(TAG, c.getMessage() );
                }
            } catch (MalformedURLException e) {
                Log.v(TAG, "Malformed Url" );
                //createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
            } catch (Exception e) {
                //createAndShowDialog(e, "Error");
                Log.v(TAG, "General Error : " + e.getMessage());
            }
            //begin sign
            authenticate();
        }
        else{
            Snackbar.make(parentLayout, "No Internet Connection please check Settings", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(isConnectedToInternet(getBaseContext()))
                            {
                                finish();
                            }
                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
        }
    }
    /**
     * Run an ASync task on the corresponding executor
     * Android Apps Quickstart
     * Accessed 16/02/17
     */
    private void addItemInTable(Profile item) throws ExecutionException, InterruptedException {
        mToDoTable.insert(item).get();
    }
    private void addProfile() {
        if (mClient == null) {
            return;
        }
        // Create a new Profile
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        final Profile initial = new Profile(prefs.getString(USERIDPREF, null).substring(4));
        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    addItemInTable(initial);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                } catch (final Exception e) {
                    Log.v(TAG, "Insert error : " + e.getMessage());
                    Log.v(TAG, "Insert error : " + e.getLocalizedMessage());
                    createAndShowDialogFromTask(e);
                }
                return null;
            }
        };
        runAsyncTask(task);
    }
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }
    private void authenticate() {
        Log.v(TAG, "Begin Auth");
        // We first try to load a token cache if one exists.
        if (loadUserTokenCache(mClient)) {
            Log.v(TAG, "Found Previous Login in Shared Preferences but need to refresh token for user experience");
            SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
            String token = prefs.getString(TOKENPREF, null);
            ListenableFuture<MobileServiceUser> mLogin = mClient.login(MobileServiceAuthenticationProvider.Google);
            Futures.addCallback(mLogin, new FutureCallback<MobileServiceUser>() {
                @Override
                public void onFailure(@NonNull Throwable exc) {
                    Log.v(TAG, " Login "+ exc.getLocalizedMessage());
                    Log.v(TAG, "tkn:  "+ mClient.getCurrentUser().getAuthenticationToken());
                    createAndShowDialog("Token expired u must log in");
                }
                @Override
                public void onSuccess(MobileServiceUser user) {
                    Log.v(TAG, "Successful Login Moving on ");
                    cacheUserToken(user);
                    loadMain();
                }
            });
        }
        else
        {
            // Login using the Google provider.
            Log.v(TAG, "Begin Auth First Timer");
            ListenableFuture<MobileServiceUser> mLogin = mClient.login(MobileServiceAuthenticationProvider.Google);
            Futures.addCallback(mLogin, new FutureCallback<MobileServiceUser>() {
                @Override
                public void onFailure(Throwable exc) {
                    //createAndShowDialog("You must log in. Login Required");
                }
                @Override
                public void onSuccess(MobileServiceUser user) {
                    Log.v(TAG, "Successful Login Moving on after pushing on ");
                    Log.v(TAG, "tkn on success:  "+ mClient.getCurrentUser().getAuthenticationToken());
                    cacheUserToken(user);
                    addProfile();
                    loadMain();
                }
            });
        }
    }

    private void loadMain() {
        Log.v(TAG, "Insert passed");
        Log.v(TAG, "Load main ..... ");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void createAndShowDialogFromTask(final Exception exception) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception);
            }
        });
    }
    /**
     * Creates a dialog and shows it
     *  @param exception The exception to show in the dialog
     *
     */
    private void createAndShowDialog(Exception exception) {
        Throwable ex = exception;
        if (exception.getCause() != null) {
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage());
    }
    /**
     * Creates a dialog and shows it
     *  @param message The dialog message
     *
     */
    private void createAndShowDialog(final String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("Error");
        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Login using the Google provider.
                ListenableFuture<MobileServiceUser> mLogin = mClient.login(MobileServiceAuthenticationProvider.Google);
                Futures.addCallback(mLogin, new FutureCallback<MobileServiceUser>() {
                    @Override
                    public void onFailure(@NonNull Throwable exc) {
                        //createAndShowDialog("You must log in. Login Required");
                    }
                    @Override
                    public void onSuccess(MobileServiceUser user) {
                        Log.v(TAG, "Successful Login Moving on after pushing on ");
                        Log.v(TAG, "tkn on success:  "+ mClient.getCurrentUser().getAuthenticationToken());
                        cacheUserToken(user);
                        addProfile();
                        loadMain();
                    }
                });
            }
        });
        builder.setIcon(R.drawable.ic_person_black_24dp);
        builder.create().show();
    }
    private static boolean isConnectedToInternet(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    private void cacheUserToken(MobileServiceUser user) {
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        Log.v(TAG, "Here is id ---> : " + user.getUserId());
        editor.putString(USERIDPREF, user.getUserId());
        editor.putString(TOKENPREF, user.getAuthenticationToken());
        editor.apply();
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
        Log.v(TAG, "Here is tkn from load value---> : " + user.getAuthenticationToken());
        mClient = client;
        mClient.setCurrentUser(user);
        return true;
    }
}

