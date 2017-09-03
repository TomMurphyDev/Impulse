package com.impulsevid.x00075294.impulsevideo;

/**
 * Created by Thomas Murphy X00075294 on 01/03/2017.
 */
/***************************************************************************************
 *    Title: <Adding Authorization to your Android Application>
 *    Author: Microsoft
 *    Date: 1/3/17
 *    Code version: 1.0
 *    Availability: https://github.com/MicrosoftDocs/azure-docs
 *
 ***************************************************************************************/
import android.content.Intent;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceIdService;
//holder class for interim object
public class MyInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDService";

    @Override
    public void onTokenRefresh() {

        Log.d(TAG, "Refreshing GCM Registration Token");

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}