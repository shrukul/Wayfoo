package com.wayfoo.wayfoo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

                    System.out.println("this activity");

                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

}