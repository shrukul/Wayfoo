package com.wayfoo.wayfoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wayfoo.wayfoo.helper.MCrypt;

/**
 * Created by mi0184 on 10/06/16.
 */
public class Settings extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Settings";
    Toolbar mToolbar;
    Button privacy,terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, ""+MainActivity.getOauth().substring(0,16));
        Log.d(TAG, ""+MainActivity.getOauth().substring(16,32));

        MCrypt mcrypt = new MCrypt();
        try {
            String encrypted = MCrypt.bytesToHex(mcrypt.encrypt("Shrukul"));
            Log.d(TAG, encrypted);
            String decrypted = new String( mcrypt.decrypt(encrypted));
            Log.d(TAG, decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.settings);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Settings");

        privacy = (Button) findViewById(R.id.privacy);
        privacy.setOnClickListener(this);

        terms = (Button) findViewById(R.id.terms);
        terms.setOnClickListener(this);
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
                it.putExtra("url", "http://www.wayfoo.com/terms.html");
                startActivity(it);
                return;
        }
    }
}
