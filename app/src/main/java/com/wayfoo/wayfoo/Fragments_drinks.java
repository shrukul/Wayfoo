package com.wayfoo.wayfoo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axle on 09/02/2016.
 */
public class Fragments_drinks extends Fragment {

    private RecyclerView rv;

    private static final String TAG = "Drinks";
    private List<FeedItemHotel> persons;
    private DatabaseHandler db;
    private Cursor c;
    private MyRecyclerAdapterHotel adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_card_view_hotel,
                container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        db = new DatabaseHandler(getActivity());
        initializeData();
        adapter = new MyRecyclerAdapterHotel(getActivity(), persons);
        rv.setAdapter(adapter);
        db.close();
        return rootView;
    }

    private void initializeData() {

        persons = new ArrayList<FeedItemHotel>();
        FeedItemHotel feed = new FeedItemHotel();
        List<FeedItemHotel> contacts = db.getAllContacts();

        for (FeedItemHotel cn : contacts) {
            if (cn.getType().toString().trim().equals("Drinks")) {
                FeedItemHotel item = new FeedItemHotel();
                item.setTitle(cn.getTitle());
                item.setType(cn.getType());
                item.setPrice(cn.getPrice());
                item.setVeg(cn.getVeg());
                item.setAmt(cn.getAmt());
                persons.add(item);
            }
        }
    }
}
