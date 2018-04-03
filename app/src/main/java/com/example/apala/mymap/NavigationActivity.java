package com.example.apala.mymap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private String mFileName = "locationData";
    private ArrayList<LatLng> mLatLngs = new ArrayList<LatLng>();
    private PrintWriter mWriter;
    private Drawable[] mLayer = new Drawable[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);
        mLayer[1] = getResources().getDrawable(R.drawable.circle);

        createToolBar();


        createGoogleMap();

        createNavigationView();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //open the file writer
        try {
            mWriter = new PrintWriter(openFileOutput(mFileName, Context.MODE_PRIVATE));
        } catch (IOException e){
            e.printStackTrace();
        }

        for(LatLng p: mLatLngs){
            mWriter.println(p.latitude + ":" + p.longitude);
        }

        mWriter.close();

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
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.locator);
        View v = getLayoutInflater().inflate(R.layout.toolbar_menu_layout,null);
        MenuItemCompat.setActionView(item, v);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(false);

        // Add a marker in Sydney and move the camera

        readAndAddToMapFromFile();

        for (LatLng point : mLatLngs) {
            mMap.addMarker(new MarkerOptions().position(point));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                mLatLngs.add(point);

                //mWriter.println(point.latitude + ":" + point.longitude);

                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(point.latitude, point.longitude)).title("New Marker");
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_button));
                mMap.addMarker(marker);
            }
        });



    }

    private void readAndAddToMapFromFile(){
        BufferedReader reader = null;
        File file = new File(getFilesDir(), mFileName);
        if (!file.exists()) return;

        try {
            reader = new BufferedReader(new InputStreamReader(openFileInput(mFileName)));

            // do reading, usually loop until end of file reading
            String readLine;
            while ((readLine = reader.readLine()) != null) {
                String [] strings = readLine.split(":");
                LatLng point = new LatLng(Double.parseDouble(strings[0]), Double.parseDouble(strings[1]));
                mLatLngs.add(point);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void createNavigationView(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //Create Header
        View headerView = navigationView.getHeaderView(0);

//        imageView.setImageResource(viewId);
//        InputStream is = getAssets().open(assetPath);
//        Drawable drawable = Drawable.createFromStream(is, null);
        mLayer[0] = getDrawable("NavigationBar_icons/ProfilePic_128.png");
        LayerDrawable layerDrawable;
        layerDrawable = new LayerDrawable(mLayer);

        ImageView profileImg = (ImageView)headerView.findViewById(R.id.imageView);
        profileImg.setImageDrawable(layerDrawable);

        setDrawableToImageView(headerView, R.id.imageView, "NavigationBar_icons/ProfilePic_128.png");
        setDrawableToImageView(headerView, R.id.imageStar, "NavigationBar_icons/Star_128.png");

        //Create Menu
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.book_spot).setIcon(getDrawable("NavigationBar_icons/Book_128.png"));
        menu.findItem(R.id.list_spot).setIcon(getDrawable("NavigationBar_icons/house.png"));
        menu.findItem(R.id.past_booking).setIcon(getDrawable("NavigationBar_icons/PastBooking_128.png"));
        menu.findItem(R.id.payment).setIcon(getDrawable("NavigationBar_icons/Payment_128.png"));
        menu.findItem(R.id.promo).setIcon(getDrawable("NavigationBar_icons/Promo_128.png"));
        menu.findItem(R.id.wallet).setIcon(getDrawable("NavigationBar_icons/wallet_128.png"));
        menu.findItem(R.id.profile).setIcon(getDrawable("NavigationBar_icons/Profile_128.png"));
        menu.findItem(R.id.rating).setIcon(getDrawable("NavigationBar_icons/Rating_128.png"));
        menu.findItem(R.id.help).setIcon(getDrawable("NavigationBar_icons/Help_128.png"));
        menu.findItem(R.id.legal).setIcon(getDrawable("NavigationBar_icons/legal_128.png"));
        menu.findItem(R.id.logout).setIcon(getDrawable("NavigationBar_icons/Logout_128.png"));



        navigationView.setNavigationItemSelectedListener(this);

    }

    private void createGoogleMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Some random LatLngs added
        LatLng sydney = new LatLng(-34, 151);
        mLatLngs.add(sydney);

        LatLng sanFrancisco = new LatLng(37.77, -122.42);
        mLatLngs.add(sanFrancisco);

        LatLng sanJose = new LatLng(37.34, -121.89);
        mLatLngs.add(sanJose);

        LatLng cupertino = new LatLng(37.32, -122.03);
        mLatLngs.add(cupertino);

    }

    private void createToolBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

    }

    private void setDrawableToImageView(View view, int viewId, String assetPath){
        try {
            ImageView imageView = (ImageView)view.findViewById(viewId);
            InputStream is = getAssets().open(assetPath);
            Drawable drawable = Drawable.createFromStream(is, null);
            imageView.setImageDrawable(drawable);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Drawable getDrawable(String assetPath){

        try {
            InputStream is = getAssets().open(assetPath);
            return Drawable.createFromStream(is, null);
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;

    }

}
