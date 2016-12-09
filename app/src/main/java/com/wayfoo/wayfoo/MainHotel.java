package com.wayfoo.wayfoo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;
import com.wayfoo.wayfoo.helper.PrefManager;

import java.util.List;

public class MainHotel extends AppCompatActivity {

    private Toolbar mToolbar;
    private static final String TAG = "Menu Card";
    private CoordinatorLayout coordinatorLayout;

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainHotel.this);
        builder.setCancelable(true);
        builder.setMessage("Are you sure you want to Exit?");
        builder.setTitle("Leave Restaurant");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PrefManager pref = new PrefManager(getApplicationContext());
                pref.setFavMenu(null);
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

    private void initView() {
        MenuCard fragment1 = new MenuCard();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment1, "Menu")
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_card);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.nav);

        initView();
        BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setItemsFromMenu(R.menu.nav_menu_bottom, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(int itemId) {
                switch (itemId) {
                    case R.id.recent_item:
                        MenuCard fragment1 = new MenuCard();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame, fragment1, "Menu")
                                .commit();
                        break;
                    case R.id.favorite_item:
                        FavMenu fragment2 = new FavMenu();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame, fragment2, "Favourites")
                                .commit();
                        break;
                }
            }
        });

        // Set the color for the active tab. Ignored on mobile when there are more than three tabs.
        bottomBar.setActiveTabColor("#ff0bc6a0");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        try {
            getSupportActionBar().setTitle(getIntent().getExtras().getString("hotelName"));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e){

        }


    }

}
