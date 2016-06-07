package com.wayfoo.wayfoo;

/**
 * Created by Axle on 02/02/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.wayfoo.wayfoo.gcmservice.RegistrationIntentService;
import com.wayfoo.wayfoo.helper.PrefManager;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class Login extends Activity implements OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0, REQUEST_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 400;
    private static final String L_TAG = "Login";

    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;

    private SignInButton btnSignIn;
    private ImageView imgProfilePic;
    PrefManager prefs;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    LinearLayout fb_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.login);

        callbackManager = CallbackManager.Factory.create();
//        loginButton = (LoginButton) findViewById(R.id.login_button);

        prefs = new PrefManager(getApplicationContext());

        if (prefs.isFirstTime()) {
            Intent i = new Intent(Login.this, Intro.class);

            Log.d("Splash", "Splash screen");

            startActivity(i);

            prefs.setFirstTime();
        }

        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);

        //Initializing google signin option
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Initializing signinbutton
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(this);
        btnSignIn.setSize(SignInButton.SIZE_WIDE);
        btnSignIn.setScopes(gso.getScopeArray());

        setGoogleButtonText(btnSignIn,"SIGN IN WITH GOOGLE");

        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        fb_btn = (LinearLayout) findViewById(R.id.fb_btn);

        fb_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile"));
            }
        });

//        loginButton.setBackgroundResource(R.drawable.fb_btn);
//        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//        loginButton.setCompoundDrawablePadding(0);
//        loginButton.setPadding(0, 0, 0, 0);
//        loginButton.setText("");

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    PrefManager prefs = new PrefManager(getApplicationContext());
                                    prefs.createUnverifiedLogin(object.getString("name"), object.getString("email"));
                                    prefs = new PrefManager(getApplicationContext());
                                    prefs.putLocation("Mangalore");
//                                    URL image_value= null;
//                                    try {
//                                        image_value = new URL("https://graph.facebook.com/"
//                                                + object.getString("id") + "/picture?type=large");
//                                    } catch (MalformedURLException e) {
//                                        e.printStackTrace();
//                                    }
//                                    Bitmap bmp = null;
//                                    try {
//                                        bmp = BitmapFactory.decodeStream(image_value.openConnection().getInputStream());
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
                                    final String userFacebookId = object.getString("id");
                                    new AsyncTask<Void, Void, Bitmap>()
                                    {
                                        @Override
                                        protected Bitmap doInBackground(Void... params)
                                        {
                                            if (userFacebookId == null)
                                                return null;

                                            String url = String.format(
                                                    "https://graph.facebook.com/%s/picture",
                                                    userFacebookId);
                                            InputStream inputStream = null;
                                            try {
                                                inputStream = new URL(url).openStream();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                                            return bitmap;
                                        }

                                        @Override
                                        protected void onPostExecute(Bitmap bitmap)
                                        {
                                            if (bitmap != null
                                                    && !isChangingConfigurations()
                                                    && !isFinishing()){
                                                PrefManager prefs = new PrefManager(getApplicationContext());
                                                prefs.putProfImage(encodeTobase64(bitmap));
                                                Log.d("img", String.valueOf(bitmap));
                                                Toast.makeText(getApplicationContext(), "User is connected!", Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(Login.this, SmsActivity.class);

                                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                                startActivity(i);
                                                finish();
                                            }
                                        }
                                    }.execute();
                                    Looper.loop();
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        });

/*        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    PrefManager prefs = new PrefManager(getApplicationContext());
                                    prefs.createUnverifiedLogin(object.getString("name"), object.getString("email"));
                                    prefs = new PrefManager(getApplicationContext());
                                    prefs.putLocation("Mangalore");
//                                    URL image_value= null;
//                                    try {
//                                        image_value = new URL("https://graph.facebook.com/"
//                                                + object.getString("id") + "/picture?type=large");
//                                    } catch (MalformedURLException e) {
//                                        e.printStackTrace();
//                                    }
//                                    Bitmap bmp = null;
//                                    try {
//                                        bmp = BitmapFactory.decodeStream(image_value.openConnection().getInputStream());
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
                                    final String userFacebookId = object.getString("id");
                                    new AsyncTask<Void, Void, Bitmap>()
                                    {
                                        @Override
                                        protected Bitmap doInBackground(Void... params)
                                        {
                                            if (userFacebookId == null)
                                                return null;

                                            String url = String.format(
                                                    "https://graph.facebook.com/%s/picture",
                                                    userFacebookId);
                                            InputStream inputStream = null;
                                            try {
                                                inputStream = new URL(url).openStream();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                                            return bitmap;
                                        }

                                        @Override
                                        protected void onPostExecute(Bitmap bitmap)
                                        {
                                            if (bitmap != null
                                                    && !isChangingConfigurations()
                                                    && !isFinishing()){
                                                PrefManager prefs = new PrefManager(getApplicationContext());
                                                prefs.putProfImage(encodeTobase64(bitmap));
                                                Log.d("img", String.valueOf(bitmap));
                                                Toast.makeText(getApplicationContext(), "User is connected!", Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(Login.this, SmsActivity.class);

                                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                                startActivity(i);
                                                finish();
                                            }
                                        }
                                    }.execute();
                                    Looper.loop();
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        }); */


    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, responseCode, intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btnSignIn.setOnClickListener(this);
                } else {
                    finish();
                }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        Log.d(L_TAG, "Handling Part");
        Log.d(L_TAG, "Sign-in Result : " + result.getStatus());

        if (result.isSuccess()) {
            getProfileInformation(result);
            Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
            Intent i = new Intent(Login.this, SmsActivity.class);

            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(i);
            finish();
        }
    }


    private void getProfileInformation(GoogleSignInResult result) {
        if (result.isSuccess()) {
            String personName = result.getSignInAccount().getDisplayName();
            String personPhotoUrl = result.getSignInAccount().getPhotoUrl().toString();
            String email = result.getSignInAccount().getEmail();

            Log.e(L_TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);

            PrefManager prefs = new PrefManager(this);
            prefs.createUnverifiedLogin(personName, email);

            RegGCM();

            personPhotoUrl = personPhotoUrl.substring(0,
                    personPhotoUrl.length() - 2)
                    + PROFILE_PIC_SIZE;

            new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

        } else {
            Toast.makeText(getApplicationContext(),
                    "Person information is null", Toast.LENGTH_LONG).show();
        }
    }

    private void RegGCM() {

        // Start IntentService to register this application with GCM.
        Intent intent2 = new Intent(this, RegistrationIntentService.class);
        startService(intent2);
    }

    protected void setGoogleButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                Log.d(L_TAG, "gsignin");
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            PrefManager prefs = new PrefManager(getApplicationContext());
            prefs.putProfImage(encodeTobase64(result));

            prefs = new PrefManager(getApplicationContext());
            prefs.putLocation("Mangalore");
        }
    }

    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d(L_TAG, "Image : " + imageEncoded);
        return imageEncoded;
    }

}