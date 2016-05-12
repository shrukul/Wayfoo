package com.wayfoo.wayfoo.gcmservice;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import com.wayfoo.wayfoo.R;
import com.wayfoo.wayfoo.helper.PrefManager;

/**
 * Created by shrukul on 27/1/16.
 */
// abbreviated tag name
public class RegistrationIntentService extends IntentService {

    SharedPreferences preferences;
    String token;

    // abbreviated tag name
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences("wayfooPref", 0);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API

        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.gcm_defaultSenderId);
        try {
            // request token that will be used by the server to send push notifications
            token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.d(TAG, "GCM Registration Token: " + token);

            PrefManager prefs = new PrefManager(getApplicationContext());

            prefs.setRegid(token);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
