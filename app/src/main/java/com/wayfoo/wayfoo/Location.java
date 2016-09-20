package com.wayfoo.wayfoo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wayfoo.wayfoo.helper.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Location extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;

    private static final String TAG = "Location";
    private List<LocationModel> feedsList;
    private LocationAdapter adapter;
    private ProgressBar progressBar;
    AsyncHttpTask a;
    ImageView b;

    Button retry;
    TextView errText;
    RelativeLayout location;
    Snackbar snackbar;
    LinearLayout lyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view_hl);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Location");

        lyt = (LinearLayout) findViewById(R.id.errLayout);
        location = (RelativeLayout) findViewById(R.id.location);
        errText = (TextView) findViewById(R.id.errText);

        PrefManager pref = new PrefManager(getApplicationContext());
        final String url = "http://wayfoo.com/location.php";

        retry = (Button) findViewById(R.id.retry);
        retry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                lyt.setVisibility(View.INVISIBLE);
                snackbar.dismiss();
                a = new AsyncHttpTask();
                a.execute(url);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        a = new AsyncHttpTask();
        a.execute(url);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = -1;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {
                    try {
                        BufferedReader r = new BufferedReader(
                                new InputStreamReader(
                                        urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            response.append(line);
                        }
                        parseResult(response.toString());
                        result = 1;
                    } catch (Exception E) {
                        result = 0;
                    }
                } else {
                    result = 0;
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
                result = -1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressBar.setVisibility(View.GONE);

            if (result == 1) {
                adapter = new LocationAdapter(Location.this, feedsList);
                mRecyclerView.setAdapter(adapter);
                lyt.setVisibility(View.GONE);
            } else if (result == 0) {
                errText.setText("No Locations available.");
                lyt.setVisibility(View.VISIBLE);
                snackbar = Snackbar
                        .make(location, "No Locations available.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        })
                        .setActionTextColor(Color.YELLOW);
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
                snackbar.show();
            } else {
                errText.setText("No Internet Connection.");
                lyt.setVisibility(View.VISIBLE);
                snackbar = Snackbar
                        .make(location, "No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        })
                        .setActionTextColor(Color.YELLOW);
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
                snackbar.show();
            }
        }
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("output");
            feedsList = new ArrayList<LocationModel>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                LocationModel item = new LocationModel();
                item.setTitle(post.optString("Name"));
                item.setThumbnail(post.optString("Image"));
                item.setID(post.optString("ID"));
                feedsList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        if (a != null) a.cancel(true);
        super.onDestroy();
    }

    public void onStop() {
        if (a != null) a.cancel(true);
        super.onStop();
    }

}
