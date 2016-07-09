package com.wayfoo.wayfoo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.wayfoo.wayfoo.helper.PrefManager;
import com.wayfoo.wayfoo.payu.PayuInterface;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by shrukul on 15/4/16.
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);

        parentLayout = findViewById(android.R.id.content);

        initView();

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                Fragment fragment;
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                //Checking if the item is in checked state or not, if not make it in checked state
                menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        Home fragment_main = new Home();
                        fragmentTransaction.replace(R.id.frame, fragment_main);
                        fragmentTransaction.commit();
                        return true;
                    case R.id.orderHistory:
                        MyOrders fragment_orders = new MyOrders();
                        fragmentTransaction.replace(R.id.frame, fragment_orders);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(MainActivity.this,Settings.class));
                        return true;
                    case R.id.payu:
                        startActivity(new Intent(MainActivity.this, PayuInterface.class));
                        return true;
                    case R.id.signout:
                        Intent it = new Intent(MainActivity.this, Login.class);
                        PrefManager prefs = new PrefManager(MainActivity.this);

                        prefs.clearSession();
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        if(accessToken!=null)
                        LoginManager.getInstance().logOut();

                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(it);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        getProfile();
    }

    private void getProfile() {

        PrefManager pref = new PrefManager(getApplicationContext());
        TextView email = (TextView) (navigationView.getHeaderView(0).findViewById(R.id.email));
        TextView username = (TextView) (navigationView.getHeaderView(0).findViewById(R.id.username));

        email.setText(pref.getEmail());
        username.setText(pref.getName());
        String personPhoto = pref.getImage();

        CircleImageView profile = (CircleImageView) (navigationView.getHeaderView(0).findViewById(R.id.profile_image));

        Bitmap profilepic= null;
        if (personPhoto != null) {
            byte[] decodedByte = Base64.decode(personPhoto, 0);
            profilepic = BitmapFactory
                    .decodeByteArray(decodedByte, 0, decodedByte.length);

        } else {
            profilepic = BitmapFactory.decodeResource(getResources(),R.drawable.profile);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            profile.setImageDrawable(new BitmapDrawable(profilepic));
        }
    }

    private void initView() {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Home fragment_main = new Home();
        fragmentTransaction.replace(R.id.frame, fragment_main);
        fragmentTransaction.commit();
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            navigationView.getMenu().getItem(0).setChecked(true);
            while (getSupportFragmentManager().getBackStackEntryCount() > 0)
                getSupportFragmentManager().popBackStackImmediate();
        } else {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
