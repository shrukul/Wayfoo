package com.wayfoo.wayfoo;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

public class Home extends Fragment {

    private RecyclerView mRecyclerView;

    private static final String TAG = "Home";
    private List<FeedItem> feedsList;
    private MyRecyclerAdapter adapter;
    private ProgressBar progressBar;
    AsyncHttpTask a;
    String fav;
    ImageView b;
    List<String> favList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_hotel, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(getActivity().SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint("Search...");

        searchView.setOnSearchClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                setItemsVisibility(menu, searchItem, false);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setItemsVisibility(menu, searchItem, true);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query1) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                query = query.toLowerCase();

                final List<FeedItem> filteredList = new ArrayList<>();

                for (int i = 0; i < feedsList.size(); i++) {

                    final String text = feedsList.get(i).getTitle().toLowerCase();
                    if (text.contains(query)) {
                        filteredList.add(feedsList.get(i));
                    }
                }

                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                adapter = new MyRecyclerAdapter(getActivity().getApplicationContext(),filteredList, favList);
                mRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_card_view,
                container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        getActivity().setTitle("Wayfoo");
        PrefManager pref = new PrefManager(getContext());
        String loc = pref.getLocation();
        Log.d("fav",pref.getEmail());
        final String url = "http://wayfoo.com/hotellist.php?Location="+loc+"&email="+pref.getEmail();
        a = new AsyncHttpTask();
        a.execute(url);
        return rootView;
    }

    void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i=0; i<menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

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
                    result = 1;
                } else {
                    result = 0;
                }
            } catch (Exception e) {
                Log.d(TAG,"error" +  String.valueOf(e));
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressBar.setVisibility(View.GONE);

            if (result == 1) {
                if(fav!=null) {
                    favList = new LinkedList(Arrays.asList(fav.split(",")));
                }
                adapter = new MyRecyclerAdapter(getActivity(), feedsList,favList);
                mRecyclerView.setAdapter(adapter);
                Log.d("fav","ff"+fav);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setMessage("Something seems to be wrong with the internet.");
                builder.setTitle("Oops!!");
                builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        getActivity().finish();
                    }
                });
                AlertDialog a=builder.create();
                a.show();
                Button bq = a.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button bq2 = a.getButton(DialogInterface.BUTTON_POSITIVE);
//                bq.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
//                bq2.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }
        }
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONObject obj = response.optJSONObject("output");
            JSONArray posts = obj.optJSONArray("output");
            feedsList = new ArrayList<FeedItem>();

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
            try {
                JSONObject obj2 = response.optJSONObject("fav");
                JSONArray posts2 = obj2.optJSONArray("fav");
                Log.d("le", String.valueOf(posts2.length()));
                for (int i = 0; i < posts2.length(); i++) {
                    JSONObject post = posts2.optJSONObject(i);
                    fav = post.optString("CID");
                }
            } catch (NullPointerException e){

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.loc){
            Intent it = new Intent(getActivity(),Location.class);
            startActivity(it);
        }
        return super.onOptionsItemSelected(item);
    }
}
