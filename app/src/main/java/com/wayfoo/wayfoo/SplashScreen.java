package com.wayfoo.wayfoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wayfoo.wayfoo.helper.PrefManager;

public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // refer this link - https://www.bignerdranch.com/blog/splash-screens-the-right-way/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                PrefManager prefs = new PrefManager(SplashScreen.this);

                if (prefs.loggedInAndVerfified()) {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                } else if (prefs.loggedInNotVerfified()) {
                    Intent i = new Intent(SplashScreen.this, SmsActivity.class);

                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashScreen.this, Login.class);

                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

}