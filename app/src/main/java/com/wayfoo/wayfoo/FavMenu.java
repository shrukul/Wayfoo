package com.wayfoo.wayfoo;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wayfoo.wayfoo.helper.PrefManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Axle on 09/12/2016.
 */
public class FavMenu extends Fragment {

    private View rootView;
    private RecyclerView rv_start;
    String key = null;
    List<String> favList;

    private static final String TAG = "fav menu card";
    private List<FeedItemHotel> persons;
    private DatabaseHandler db;
    private Cursor c;
    private MyRecyclerAdapterHotelFav adapter;

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.search);
        item.setVisible(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_card_view_hotel,
                container, false);

        rv_start = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        rv_start.setHasFixedSize(true);
        rv_start.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        db = new DatabaseHandler(getActivity());
        rv_start.clearOnScrollListeners();
        PrefManager pref = new PrefManager(getActivity());
        String fav = pref.getFavMenu();
        if (fav != null) {
            favList = new LinkedList(Arrays.asList(fav.split(",")));
        }
        initializeData();
        adapter = new MyRecyclerAdapterHotelFav(getActivity(), persons, (MyApplication) getActivity().getApplication()
                , getActivity().getIntent().getExtras().getString("hotelName"),favList);
        rv_start.setAdapter(adapter);
        db.close();

        return rootView;

    }

    private void initializeData() {

        persons = new ArrayList<FeedItemHotel>();
        FeedItemHotel feed = new FeedItemHotel();
        List<FeedItemHotel> contacts = db.getAllContacts();

        for (FeedItemHotel cn : contacts) {
            if(favList!=null && favList.contains(String.valueOf(cn.getID()))) {
                FeedItemHotel item = new FeedItemHotel();
                item.setTitle(cn.getTitle());
                item.setType(cn.getType());
                item.setPrice(cn.getPrice());
                item.setVeg(cn.getVeg());
                item.setAmt(cn.getAmt());
                item.setItemID(cn.getItemID());
                persons.add(item);
            }
        }
    }
}
