package com.example.x00075294.impulsevideo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "IMP:Main -->: ";
    private static final int MY_PERMISSIONS_REQUEST_USE_CAMERA = 101;
    private static final int MY_PERMISSIONS_WRITE_STORAGE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO,Manifest.permission.MANAGE_DOCUMENTS}, MY_PERMISSIONS_REQUEST_USE_CAMERA);
    }

    /**
     * Accessed on 2/3/16
     * <p>
     * Requesting permissions
     * <p>
     * link:https://developer.android.com/training/permissions/requesting.html
     * <p>
     * link:https://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_USE_CAMERA: {

                Map<String, Integer> perms = new HashMap<String, Integer>();

                // Initial

                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);

                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.MANAGE_DOCUMENTS, PackageManager.PERMISSION_GRANTED);
                // Fill with results

                for (int i = 0; i < permissions.length; i++)

                    perms.put(permissions[i], grantResults[i]);

                // Check for ACCESS_FINE_LOCATION

                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

                        && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                        && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

                    // All Permissions Granted

                } else {

                    // Permission Denied

                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)

                            .show();

                }

                return;

            }

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            loadProfile();
        } else if (id == R.id.nav_my_videos) {

        } else if (id == R.id.nav_trends) {

        } else if (id == R.id.nav_Analytics) {

        } else if (id == R.id.nav_comedy) {

        } else if (id == R.id.nav_music) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadProfile() {
        Log.v(TAG, "Load profile ..... ");
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
