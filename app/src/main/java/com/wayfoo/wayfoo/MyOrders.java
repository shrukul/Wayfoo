package com.wayfoo.wayfoo;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import java.util.List;

public class MyOrders extends Fragment {

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;

    private static final String TAG = "My Orders";
    private List<OrderListModel> feedsList;
    private OrderListAdapter adapter;
    private ProgressBar progressBar;
    AsyncHttpTask a;
    ImageView b;
    Button retry;
    TextView errText;
    RelativeLayout myorders;
    Snackbar snackbar;
    LinearLayout lyt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_card_view_order,
                container, false);

        getActivity().setTitle("Order History");

        lyt = (LinearLayout) rootView.findViewById(R.id.errLayout);
        myorders = (RelativeLayout) rootView.findViewById(R.id.myorders);
        errText = (TextView) rootView.findViewById(R.id.errText);

        PrefManager pref = new PrefManager(getContext());
        String email = pref.getEmail();
        MCrypt mcrypt = new MCrypt();
        final String url;
        try {
            url = "http://www.wayfoo.com/php/orderHistory.php?email=" +  MCrypt.bytesToHex(mcrypt.encrypt(email));
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
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setHasFixedSize(true);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
            a = new AsyncHttpTask();
            a.execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                Log.d(TAG, "statusCode "+statusCode);

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
                lyt.setVisibility(View.GONE);
                adapter = new OrderListAdapter(getActivity(), feedsList);
                mRecyclerView.setAdapter(adapter);
            } else if (result == 0) {
                errText.setText("You haven't placed any orders yet.");
                lyt.setVisibility(View.VISIBLE);
                snackbar = Snackbar
                        .make(myorders, "You haven't placed any orders yet.", Snackbar.LENGTH_INDEFINITE)
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
                        .make(myorders, "No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
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
            feedsList = new ArrayList<OrderListModel>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                OrderListModel item = new OrderListModel();
                item.setTitle(post.optString("Hotel"));
                item.setTotal(post.optString("Total"));
                item.setID(post.optString("ID"));
                item.setTable(post.optString("TableName"));
                item.setPay(post.optString("Payment"));
                item.setOID(post.optString("OID"));
                item.setDispName(post.optString("DispName"));
                item.setAddr(post.optString("Addr"));
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
