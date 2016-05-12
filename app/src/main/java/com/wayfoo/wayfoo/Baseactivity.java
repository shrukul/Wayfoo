package com.wayfoo.wayfoo;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

/**
 * Created by Axle on 21/03/2016.
 */
public abstract class Baseactivity extends AppCompatActivity {

    public FrameLayout container;
    public android.support.v7.widget.Toolbar toolbar;
    public CoordinatorLayout mainlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        container = (FrameLayout) findViewById(R.id.container);
        mainlayout = (CoordinatorLayout) findViewById(R.id.fulllayout);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(R.string.app_name);
            toolbar.setTitleTextColor(Color.WHITE);
        }
    }

    public void setToolbarSubTittle(String header) {
        toolbar.setSubtitle(header);
    }

    public void setToolbarElevation(float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(value);
        }
    }

    // Method to set xml object reference.
    public abstract void setReference();


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}