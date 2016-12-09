package com.wayfoo.wayfoo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.wayfoo.wayfoo.helper.PrefManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Axle on 15/03/2016.
 */
public class Search extends AppCompatActivity {
    private RecyclerView rv;
    public SearchView search1;

    private static final String TAG = "search";
    private List<FeedItemHotel> persons;
    private DatabaseHandler db;
    private Cursor c;
    List<String> favList;
    private MyRecyclerAdapterHotel adapter;
    private static final String SELECT_SQL = "SELECT * FROM contacts";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        search1 = (SearchView) findViewById(R.id.search);
        rv = (RecyclerView) findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(new LinearLayoutManager(this));

        db = new DatabaseHandler(this);
        initializeData();
        PrefManager pref = new PrefManager(this);
        String fav = pref.getFavMenu();
        Log.e("fav","dd"+fav);
        if (fav != null) {
            favList = new LinkedList(Arrays.asList(fav.split(",")));
        }
        adapter = new MyRecyclerAdapterHotel(this, persons,(MyApplication) getApplication()
                ,getIntent().getExtras().getString("hotelName"),favList);
        rv.setAdapter(adapter);
        search1.setIconifiedByDefault(false);
        search1.setOnQueryTextListener(listener);
    }

    private void initializeData() {

        persons = new ArrayList<FeedItemHotel>();
        List<FeedItemHotel> contacts = db.getAllContacts();

        for (FeedItemHotel cn : contacts) {
            FeedItemHotel item = new FeedItemHotel();
            item.setTitle(cn.getTitle());
            item.setType(cn.getType());
            item.setPrice(cn.getPrice());
            item.setVeg(cn.getVeg());
            item.setAmt(cn.getAmt());
            persons.add(item);
        }
    }

    OnQueryTextListener listener = new OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String query) {
            query = query.toLowerCase();

            final List<FeedItemHotel> filteredList = new ArrayList<>();

            for (int i = 0; i < persons.size(); i++) {

                final String text = persons.get(i).getTitle().toLowerCase();
                if (text.contains(query)) {
                    filteredList.add(persons.get(i));
                }
            }

            rv.setLayoutManager(new LinearLayoutManager(Search.this));
            String fav = getIntent().getExtras().getString("favmenu");
            Log.e("fav","dd"+fav);
            if (fav != null) {
                favList = new LinkedList(Arrays.asList(fav.split(",")));
            }
            adapter = new MyRecyclerAdapterHotel(Search.this, filteredList,(MyApplication) getApplication()
                    ,getIntent().getExtras().getString("hotelName"),favList);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            return true;

        }

        public boolean onQueryTextSubmit(String query) {
            return false;
        }
    };

}
