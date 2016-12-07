package com.wayfoo.wayfoo;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.widget.ImageView;

/**
 * Created by Axle on 02/02/2016.
 */
public class Profile extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageView a, b, c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.profile);
        a = (ImageView) findViewById(R.id.imageView);
        b = (ImageView) findViewById(R.id.imageView2);
        c = (ImageView) findViewById(R.id.imageView3);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        a.getLayoutParams().width = width / 5;
        a.getLayoutParams().height = width / 5;
        b.getLayoutParams().width = width / 5;
        b.getLayoutParams().height = width / 5;
        c.getLayoutParams().width = width / 5;
        c.getLayoutParams().height = width / 5;

    }
}
