package com.wayfoo.wayfoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Axle on 09/12/2016.
 */
public class MenuCard extends Fragment {

    private View rootView;
    private static final String TAG = "Menu Card";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    List<String> tabsTitle;
    String tabsList;

    public void func() {
        Intent it = new Intent(getActivity(), AdditionalInfo.class);

        Boolean flag = false;


        PrefManager pref = new PrefManager(getApplicationContext());
        pref.setTitle(getActivity().getIntent().getExtras().getString("title"));
        pref.setTable(getActivity().getIntent().getExtras().getString("table"));
        pref.setHotelName(getActivity().getIntent().getExtras().getString("hotelName"));
        startActivity(it);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
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
            case R.id.search:
                Intent i = new Intent(getActivity(), Search.class);
                i.putExtra("hotelName", getActivity().getIntent().getExtras().getString("hotelName"));
                PrefManager pref = new PrefManager(getActivity());
                String fav = pref.getFavMenu();
                i.putExtra("favmenu", fav);
                startActivity(i);
                break;
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.menucardhotel,
                container, false);

        FloatingActionButton cart = (FloatingActionButton) rootView.findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                func();
            }
        });
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
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

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
//        viewPager.setOffscreenPageLimit(3);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        tabsList = getActivity().getIntent().getExtras().getString("tabs");
        tabsTitle = Arrays.asList(tabsList.split(","));
        for (int i = 0; i < tabsTitle.size(); i++) {
            Fragment_menu mi = new Fragment_menu();
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
