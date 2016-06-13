package com.wayfoo.wayfoo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Axle on 09/03/2016.
 */
public class Intermediate extends AppCompatActivity {

    AsyncHttpTask a;
    String name,table,hotelName;
    protected ProgressBar progressBar;
    Toolbar mToolbar;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intermediate);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        name = getIntent().getExtras().getString("title");
        table = getIntent().getExtras().getString("table");
        hotelName = getIntent().getExtras().getString("hotelName");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Loading ...");

        System.out.println(name + " " + table);
        final String url = "http://wayfoo.com/hotel.php?name="+name;
        a=new AsyncHttpTask();
        a.execute(url);
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();
                // 200 represents HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(
                            new InputStreamReader(
                                    urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1; // Successful
                } else {
                    result = 0; // "Failed to fetch data!";
                }
            } catch (Exception e) {
            }
            return result; // "Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            progressBar.setVisibility(View.GONE);

            if (result == 1) {
                //startActivity(new Intent(Intermediate.this,Main.class));
                Intent i=new Intent(Intermediate.this,MainHotel.class);
                i.putExtra("title",name);
                i.putExtra("table",table);
                i.putExtra("hotelName",hotelName);
                startActivity(i);
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Intermediate.this);
                builder.setCancelable(true);
                builder.setMessage("Something seems to be wrong with the internet.");
                builder.setTitle("Oops!!");
                builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                AlertDialog a=builder.create();
                a.show();
                Button bq = a.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button bq2 = a.getButton(DialogInterface.BUTTON_POSITIVE);
            }
        }
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("output");
            DatabaseHandler db = new DatabaseHandler(Intermediate.this);
            List<FeedItemHotel> c = db.getAllContacts();
            for (FeedItemHotel cn : c) {
                db.deleteContact(cn);
            }
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                db.addContact(new FeedItemHotel(post.optString("Name"), post.optString("Price"),post.optString("NonVeg"),"0",
                        post.optString("Type")));
            }
            db.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        if (a != null) a.cancel(true);
        super.onDestroy();
    }
}
