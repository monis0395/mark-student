package com.mark.mark;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class DailyPeriodActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private StringRes sr;
    TextView nav_username, nav_user;
    Context self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success2);
        self = DailyPeriodActivity.this;
        setTitle("Today");
        sideNavInit();

        new AsyncFetch(DailyPeriodActivity.this, "daily.php");
    }

    void sideNavInit() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);
        View nav_header = navigationView.getHeaderView(0);
        nav_username = (TextView) nav_header.findViewById(R.id.username);
        nav_user = (TextView) nav_header.findViewById(R.id.user);
        nav_username.setText(UserDetails.studentid);
        nav_user.setText(UserDetails.sname);
    }

    public void scanNFC(View arg0) {

        String tagUID = "C09705f7";
        String location = "61";
        long time = System.currentTimeMillis();
        SharedPreferences sp = self.getSharedPreferences("MarkUserDetails", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("NFC_Location", location);
        editor.putString("NFC_TimeMills", time + "");
        editor.putString("NFC_UID", tagUID);
        editor.apply();

        Intent intent = new Intent(DailyPeriodActivity.this, ScanFPActivity.class);
        intent.putExtra("subject", "Enterprise Resource Planning");
        startActivity(intent);
    }

    private class AsyncFetch extends GlobalAsyncTask {

        AsyncFetch(Context context, String url) {
            super(context, url);
            execute();
        }

        @Override
        public Uri.Builder urlBuilder() {
            return new Uri.Builder()
                    .appendQueryParameter("clid", UserDetails.clid)
                    .appendQueryParameter("batchid", UserDetails.batchid);
        }

        @Override
        public void goPostExecute(String result, String content) {

            try {
                JSONArray jArray = new JSONArray(result);

                TextView textPeriod;
                textPeriod = (TextView) findViewById(R.id.textPeriod);
                textPeriod.setText("/ " + jArray.length() + "");

                DailyPeriod dp = new DailyPeriod();

                AdapterDailyPeriod mAdapter = new AdapterDailyPeriod(DailyPeriodActivity.this, dp.parseList(jArray));
                RecyclerView dailyPeriod = (RecyclerView) findViewById(R.id.dailyPeriod);
                dailyPeriod.setAdapter(mAdapter);
                dailyPeriod.setLayoutManager(new LinearLayoutManager(DailyPeriodActivity.this));

            } catch (JSONException e) {
                Toast.makeText(DailyPeriodActivity.this, e.toString(), Toast.LENGTH_LONG).show();
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
        getMenuInflater().inflate(R.menu.success2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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

        if (id == R.id.nav_today) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_changeHost) {
            Util.changeHost(self);

        } else if (id == R.id.nav_logout) {
            UserDetails ud = new UserDetails(self);
            ud.clearDetails();

            Intent intent = new Intent(DailyPeriodActivity.this, LoginActivity.class);
            startActivity(intent);
            DailyPeriodActivity.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
