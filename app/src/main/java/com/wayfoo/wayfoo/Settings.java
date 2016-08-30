package com.wayfoo.wayfoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by mi0184 on 10/06/16.
 */
public class Settings extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolbar;
    Button privacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Settings");

        privacy = (Button) findViewById(R.id.privacy);
        privacy.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Intent it = new Intent(Settings.this, WebviewActivity.class);

        switch (view.getId()) {
            case R.id.privacy:
                it.putExtra("url", "http://www.wayfoo.com/privacy.html");
                startActivity(it);
                return;
            case R.id.terms:
                return;
        }
    }
}
