package com.wayfoo.wayfoo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.wayfoo.wayfoo.helper.PrefManager;

import java.util.ArrayList;
import java.util.List;

public class MainHotel extends AppCompatActivity {

    private Toolbar mToolbar;
    private static final String TAG = "Menu Card";
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainHotel.this);
        builder.setCancelable(true);
        builder.setMessage("Are you sure you want to Exit?");
        builder.setTitle("Leave Restaurant");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(MainHotel.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        AlertDialog a = builder.create();
        a.show();
        Button bq = a.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button bq2 = a.getButton(DialogInterface.BUTTON_POSITIVE);
        bq.setTextColor(ContextCompat.getColor(MainHotel.this, R.color.colorPrimary));
        bq2.setTextColor(ContextCompat.getColor(MainHotel.this, R.color.colorPrimary));
    }

    public void func(View v) {
        Intent it = new Intent(MainHotel.this, AdditionalInfo.class);

        PrefManager pref = new PrefManager(getApplicationContext());
        pref.setTitle(getIntent().getExtras().getString("title"));
        pref.setTable(getIntent().getExtras().getString("table"));
        pref.setHotelName(getIntent().getExtras().getString("hotelName"));
        startActivity(it);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            startActivity(new Intent(MainHotel.this, Search.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_card);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        PrefManager prefs = new PrefManager(getApplicationContext());
        prefs.setPriceSum(0);
        System.out.println("Initial Sum is "+prefs.getPriceSum());
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Fragment_start(), "Snacks");
        adapter.addFragment(new Fragment_main(), "Main Course");
        adapter.addFragment(new Fragment_deserts(), "Desserts");
        adapter.addFragment(new Fragments_drinks(), "Drinks");
        viewPager.setAdapter(adapter);
        //viewPager.setOffscreenPageLimit(1);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public Fragment getItem(int position) {

            return mFragmentList.get(position);
//            switch(position) {
//
//                case 0: return  new Fragment_start();
//                case 1: return  new Fragment_main();
//                case 2: return  new Fragment_deserts();
//                case 3: return  new Fragments_drinks();
//            }
//            return new Fragment_start();
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
