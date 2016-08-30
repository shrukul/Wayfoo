package com.wayfoo.wayfoo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axle on 09/02/2016.
 */
public class Fragment_start extends Fragment {
    private RecyclerView rv_start;

    private static final String TAG = "Starters";
    private List<FeedItemHotel> persons;
    private DatabaseHandler db;
    private Cursor c;
    private MyRecyclerAdapterHotel adapter;

    @Override
    public void onResume() {
        initializeData();
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_card_view_hotel,
                container, false);
        rv_start = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        rv_start.setHasFixedSize(true);
        rv_start.setItemAnimator(new DefaultItemAnimator());
        rv_start.setLayoutManager(new LinearLayoutManager(getActivity()));

        db = new DatabaseHandler(getActivity());
        initializeData();
        adapter = new MyRecyclerAdapterHotel(getActivity(), persons);
        adapter.notifyDataSetChanged();
        rv_start.setAdapter(adapter);
        db.close();
        return rootView;
    }

    private void initializeData() {

        persons = new ArrayList<FeedItemHotel>();
        FeedItemHotel feed = new FeedItemHotel();
        List<FeedItemHotel> contacts = db.getAllContacts();

        int i = 0;
        for (FeedItemHotel cn : contacts) {
            i++;
            if (cn.getType().equals("Snacks")) {
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
        Log.d("start", String.valueOf(i));
    }
}
