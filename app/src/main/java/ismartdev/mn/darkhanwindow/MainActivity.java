package ismartdev.mn.darkhanwindow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;


import com.android.volley.cache.DiskLruBasedCache;
import com.android.volley.cache.plus.SimpleImageLoader;

import ismartdev.mn.darkhanwindow.menu.HomeFragment;
import ismartdev.mn.darkhanwindow.menu.ItemFragment;
import ismartdev.mn.darkhanwindow.util.CircleImageView;
import ismartdev.mn.darkhanwindow.util.GCMClientManager;
import ismartdev.mn.darkhanwindow.util.Services;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sp;
    String PROJECT_NUMBER = "mybook-1243";
    private SimpleImageLoader mImageFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("user", 1);
        DiskLruBasedCache.ImageCacheParams cacheParams = new DiskLruBasedCache.ImageCacheParams(getApplicationContext(), "CacheDirectory");
        cacheParams.setMemCacheSizePercent(0.5f);

        mImageFetcher = new SimpleImageLoader(this, cacheParams);
        mImageFetcher.setMaxImageSize(300);

        if (!sp.getBoolean("isregisteredGCM", false)) {
            final GCMClientManager pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
            pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
                @Override
                public void onSuccess(String registrationId, boolean isNewRegistration) {

                    Log.d("Registration id", registrationId);
                    final SharedPreferences sp = getSharedPreferences(getPackageName(),
                            Context.MODE_PRIVATE);
                    sp.edit().putString(Services.PROPERTY_REG_ID, registrationId).commit();
                    Services.registerGCM(MainActivity.this, registrationId);

                }

                @Override
                public void onFailure(String ex) {
                    super.onFailure(ex);
                }
            });
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initHeader(navigationView);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }

    private void initHeader(NavigationView v) {
        View headerVw = v.getHeaderView(0);
        CircleImageView image = (CircleImageView) headerVw.findViewById(R.id.nav_header_driver_img);
        TextView name = (TextView) headerVw.findViewById(R.id.nav_header_name);


        if (sp.getBoolean("isLogin", false)) {
            mImageFetcher.setDefaultDrawable(R.drawable.nodriver);
            mImageFetcher.get(sp.getString("profile_pic_url", ""), image);

            name.setText(sp.getString("display_name", ""));
        } else {

            name.setText(R.string.click_to_login);
            headerVw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent login = new Intent(MainActivity.this, LoginAc.class);
                    startActivity(login);
                }
            });
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_home) {
            fragmentTransaction.replace(R.id.main_frame, ItemFragment.newInstance());
            fragmentTransaction.commit();
            // Handle the camera action
        } else if (id == R.id.nav_ac) {

        } else if (id == R.id.nav_ac) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
