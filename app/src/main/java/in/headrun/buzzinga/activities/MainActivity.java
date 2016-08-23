package in.headrun.buzzinga.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.BuzzingaNotification;
import in.headrun.buzzinga.BuzzingaRequest;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.utils.Utils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.progress_bar)
    ProgressBar progress_bar;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    Utils utils;
    String Intent_opt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        utils = new Utils(this);

        Bundle bundle = getIntent().getExtras();
        Intent_opt = bundle.getString(Constants.Intent_OPERATION);

        handleIntent(getIntent());

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        utils.call_homeFragment(Intent_opt);
        toolbar.setTitle(utils.setTitle());
        utils.callService();

        //onNavigationItemSelected(navigationView.getMenu().getItem(0));
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


/*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();    //remove all items
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onPrepareOptionsMenu(menu);
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //*** setOnQueryTextFocusChangeListener ***
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                utils.showLog(TAG, "search query is " + searchQuery, Config.MainActivity);
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });
        return true;
    }
/*

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

/*        if (id == R.id.search_result) {
            setTitle();
        } else */
        if (id == R.id.edit_keyword) {
            startActivity(new Intent(this, TrackKeyWord.class));
        } else if (id == R.id.filter) {
            startActivity(new Intent(this, Filtering.class));
        } else if (id == R.id.date_filter) {
            utils.getdate();
        } else if (id == R.id.notify_me) {
            utils.notimyme(utils.userSession.getNotifyHour());
        } else if (id == R.id.logout) {
            stringrequest();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void stringrequest() {
        if (utils.isNetwrokConnection()) {
            progress_bar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, ServerConfig.SERVER_ENDPOINT + ServerConfig.logout,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d(TAG, "string response is" + response);

                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(MainActivity.this, BuzzingaNotification.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                            alarmManager.cancel(pendingIntent);
                            // stopService(new Intent(HomeScreen.this, .class));
                            ;
                            utils.userSession.clearsession(utils.userSession.TSESSION);
                            startActivity(new Intent(MainActivity.this, TwitterLogin.class));
                            progress_bar.setVisibility(View.GONE);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "string error response is" + error);
                    progress_bar.setVisibility(View.GONE);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("sessionid", new UserSession(MainActivity.this).getTSESSION());
                    return params;
                }
            };
            stringRequest.setTag(TAG);

            BuzzingaRequest.getInstance(getApplication()).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
        }
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Constants.SEARCHSTRING = intent.getStringExtra(SearchManager.QUERY);
            searchview_text();
        }

    }

    private void searchview_text() {
        if (Constants.SEARCHSTRING.trim().length() > 0) {

            if (utils.isNetwrokConnection()) {
                utils.userSession.setTACK_SEARCH_KEY(Constants.SEARCHSTRING);

                utils.add_query_data();
                setTitle(utils.userSession.getTrackKey() + " AND " + utils.userSession.gettTACK_SEARCH_KEY());

                utils.call_homeFragment(Constants.Intent_TRACK);
            }
        }
    }

}
