package com.wayfoo.wayfoo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.wayfoo.wayfoo.helper.PrefManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainHotel extends AppCompatActivity {

    private Toolbar mToolbar;
    private static final String TAG = "Menu Card";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    List<String> tabsTitle;
    String tabsList;

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
                finish();
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
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.search:
                startActivity(new Intent(MainHotel.this, Search.class));
                this.finish();
                break;
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_card);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        System.out.println(("One"));
                        break;
                    case 1:
                        System.out.println(("Two"));

                        break;
                    case 2:
                        System.out.println(("Three"));

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        PrefManager prefs = new PrefManager(getApplicationContext());
        prefs.setPriceSum(0);
        System.out.println("Initial Sum is " + prefs.getPriceSum());
    }

    private void setupViewPager(ViewPager viewPager) {
//        viewPager.setOffscreenPageLimit(3);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        tabsList = getIntent().getExtras().getString("tabs");
        tabsTitle = Arrays.asList(tabsList.split(","));
        for (int i = 0; i < tabsTitle.size(); i++) {
            Fragment_menu mi = new Fragment_menu();
//            Bundle b = new Bundle();
//            b.putString("key", tabsTitle.get(i));
//            mi.setArguments(b);
            adapter.addFragment(mi, tabsTitle.get(i));
        }
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final List<Bundle> mFragmentBundle = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public Fragment getItem(int position) {
            Fragment fm = mFragmentList.get(position);
            Bundle b = new Bundle();
            b.putString("key", mFragmentTitleList.get(position));
            Log.d("title", mFragmentTitleList.get(position));
            fm.setArguments(b);
            return fm;
        }

/*        public Fragment getItem(int position) {
            Fragment_menu fm = Fragment_menu.newInstance();
            Bundle b = new Bundle();
            b.putString("key", mFragmentTitleList.get(position));
            fm.setArguments(b);
            return fm;
        }*/

/*        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }*/

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

/*        @Override
        public Parcelable saveState() {
            // Do Nothing
            return null;
        }*/

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
