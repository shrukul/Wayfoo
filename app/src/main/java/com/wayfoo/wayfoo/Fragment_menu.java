package com.wayfoo.wayfoo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axle on 21/06/2016.
 */
public class Fragment_menu extends Fragment {
    private RecyclerView rv_start;
    String key = null;

    private static final String TAG = "menu card";
    private List<FeedItemHotel> persons;
    private DatabaseHandler db;
    private Cursor c;
    private MyRecyclerAdapterHotel adapter;

    @Override
    public void onResume() {
        super.onResume();
        initializeData();
        adapter = new MyRecyclerAdapterHotel(getActivity(), persons);
        rv_start.setAdapter(adapter);
        db.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        Log.d("destroyed", key);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_card_view_hotel,
                container, false);
        key = getArguments().getString("key");
        Log.d("created", key);
        rv_start = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        rv_start.setHasFixedSize(true);
//        rv_start.setItemAnimator(new DefaultItemAnimator());
        rv_start.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        db = new DatabaseHandler(getActivity());
        rv_start.clearOnScrollListeners();
        initializeData();
        adapter = new MyRecyclerAdapterHotel(getActivity(), persons);
//        adapter.notifyDataSetChanged();
        rv_start.setAdapter(adapter);
        db.close();
        return rootView;
    }

    public static Fragment_menu newInstance() {
        return new Fragment_menu();
    }

    private void initializeData() {

        persons = new ArrayList<FeedItemHotel>();
        FeedItemHotel feed = new FeedItemHotel();
        List<FeedItemHotel> contacts = db.getAllContacts();

        key = getArguments().getString("key");

        for (FeedItemHotel cn : contacts) {
            if (cn.getType().equals(key)) {
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
