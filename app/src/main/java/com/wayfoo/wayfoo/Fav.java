package com.wayfoo.wayfoo;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wayfoo.wayfoo.helper.MCrypt;
import com.wayfoo.wayfoo.helper.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Axle on 29/08/2016.
 */
public class Fav extends Fragment {

    private View rootView;
    private RecyclerView mRecyclerView;

    private static final String TAG = "Fav";
    String url;
    Snackbar snackbar;
    LinearLayout lyt;
    private List<FeedItem> feedsList,feedsListFinal;
    private MyRecyclerAdapterFav adapter;
    private ProgressBar progressBar;
    AsyncHttpTask a;
    String fav;
    ImageView b;
    List<String> favList;
    Button retry;
    TextView errText;
    RelativeLayout favourites;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_card_view,
                container, false);

        lyt = (LinearLayout) rootView.findViewById(R.id.errLayout);
        favourites = (RelativeLayout) rootView.findViewById(R.id.favourites);
        errText = (TextView) rootView.findViewById(R.id.errText);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        MCrypt mcrypt = new MCrypt();
        PrefManager pref = new PrefManager(getContext());
        try {
            url = "http://www.wayfoo.com/php/hotellistfav.php?email=" + MCrypt.bytesToHex(mcrypt.encrypt(pref.getEmail()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        retry = (Button) rootView.findViewById(R.id.retry);
        retry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                lyt.setVisibility(View.INVISIBLE);
                snackbar.dismiss();
                a = new AsyncHttpTask();
                a.execute(url);
            }
        });
        getActivity().setTitle("Favourites");
        String loc = pref.getLocation();
        Log.d("fav", pref.getEmail());
        a = new AsyncHttpTask();
        a.execute(url);
        return rootView;
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

                Log.d(TAG, "respone: "+statusCode);

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
                        System.out.println("response here" + response);
                        parseResult(response.toString());
                        result = 1;
                    } catch (Exception E) {
                        result = 0;
                    }
                } else {
                    result = 0;
                }
            } catch (Exception e) {
                Log.d(TAG, "error" + String.valueOf(e));
                result = -1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressBar.setVisibility(View.GONE);

            Log.d(TAG,"result: "+fav);
            if (result == 1) {
                if (fav != null) {
                    favList = new LinkedList(Arrays.asList(fav.split(",")));
                }
                feedsListFinal = new ArrayList<FeedItem>();
                try {
                    for (FeedItem f : feedsList) {
                        if (favList.contains(f.getID())) {
                            feedsListFinal.add(f);
                        }
                    }
                }catch(NullPointerException e){

                }
                adapter = new MyRecyclerAdapterFav(getActivity(), feedsListFinal, favList);
                mRecyclerView.setAdapter(adapter);
                lyt.setVisibility(View.GONE);

                if ((fav.equals("") || fav.equals(null) || fav.equals(" "))) {
                    errText.setText("No Favourites.");
                    lyt.setVisibility(View.VISIBLE);
                    retry.setVisibility(View.GONE);
                    snackbar = Snackbar
                            .make(rootView, "No Favourites.", Snackbar.LENGTH_INDEFINITE)
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
            } else {
                errText.setText("No Internet Connection.");
                lyt.setVisibility(View.VISIBLE);
                retry.setVisibility(View.VISIBLE);
                snackbar = Snackbar
                        .make(favourites, "No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
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
            JSONObject obj = response.optJSONObject("output");
            JSONArray posts = obj.optJSONArray("output");
            feedsList = new ArrayList<FeedItem>();
            try {
                JSONObject obj2 = response.optJSONObject("fav");
                JSONArray posts2 = obj2.optJSONArray("fav");
                for (int i = 0; i < posts2.length(); i++) {
                    JSONObject post = posts2.optJSONObject(i);
                    fav = post.optString("CID");
                }
            } catch (NullPointerException e) {

            }
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                FeedItem item = new FeedItem();
                item.setTitle(post.optString("Name"));
                item.setPlace(post.optString("Place"));
                item.setThumbnail(post.optString("Image"));
                item.setID(post.optString("ID"));
                item.setDisName(post.optString("DisplayName"));
                item.setRSum(post.optString("Rate_Sum"));
                item.setRNum(post.optString("Rate_Num"));
                item.setAvail(post.optInt("Avail"));
                item.setTabs(post.optString("Tabs"));
                item.setTime(post.optString("Time"));
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

