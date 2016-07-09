package com.wayfoo.wayfoo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
        System.out.println("Base Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_app);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        container = (FrameLayout) findViewById(R.id.container);
        mainlayout = (CoordinatorLayout) findViewById(R.id.fulllayout);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(R.string.app_name);
            toolbar.setTitleTextColor(Color.WHITE);
        }
    }
}