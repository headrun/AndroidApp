package in.headrun.buzzinga.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.openMenu)
    ImageView openMenu;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.badger)
    TextView badger;

    ActionBarDrawerToggle toggle;

    Utils utils;
    String Intent_opt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(7);

        toolbar.inflateMenu(R.menu.main);

        utils = new Utils(this);

        Bundle bundle = getIntent().getExtras();
        Intent_opt = bundle.getString(Constants.Intent_OPERATION);

        handleIntent(getIntent());

        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        int filter_count = utils.count_filter_sel();

        if (filter_count != 0) {
            badger.setText("" + filter_count);
            badger.setVisibility(View.VISIBLE);
        } else {
            badger.setVisibility(View.GONE);
        }

        setMenuCounter(R.id.filter, R.drawable.count_bg, filter_count);
        setMenuCounter(R.id.notify_me, R.drawable.count_bg, utils.getNotify_IntervellMills());

        getSupportActionBar().setTitle("");
        title.setText(utils.setTitle());
        call_homeFragment(Intent_opt);

        //onNavigationItemSelected(navigationView.getMenu().getItem(0));

        openMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawer.isDrawerOpen(navigationView)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }

            }
        });
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

    private void setMenuCounter(@IdRes int itemId, int drawable, int count) {
        LinearLayout text_filter_lay = (LinearLayout) navigationView.getMenu().findItem(itemId).getActionView();
        TextView text_filter_cnt = (TextView) text_filter_lay.findViewById(R.id.count);
        text_filter_cnt.setBackgroundResource(drawable);

        if (count > 0) {
            if (itemId == R.id.notify_me)
                text_filter_cnt.setText("" + 1);
            else
                text_filter_cnt.setText(String.valueOf(count));
            text_filter_cnt.setVisibility(View.VISIBLE);

        } else {
            text_filter_cnt.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

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
            getdate();
        } else if (id == R.id.notify_me) {
            notimyme(utils.userSession.getNotifyHour());
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

                            utils.userSession.clearsession(utils.userSession.TSESSION);
                            startActivity(new Intent(MainActivity.this, TwitterLogin.class));
                            finish();
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
                Intent_opt = Constants.Intent_TRACK;
            }
        }
    }

    public void notimyme(final String sel_item) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.notifyme));

        final String[] items = getResources().getStringArray(R.array.notify_hours);

        int pos = -1;

        utils.showLog(TAG, "sel item is" + sel_item, Config.Utils);

        if (!sel_item.toString().isEmpty())
            pos = Arrays.asList(items).indexOf(sel_item);

        utils.showLog(TAG, "sel item pos is " + pos, Config.Utils);
        final String[] sel_value = {""};
        builder.setSingleChoiceItems(items, pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                sel_value[0] = Arrays.asList(items).get(which).trim();
                /*int sel_hour;
                if (sel_value.contains("None")) {
                    sel_hour = which;
                } else {
                    sel_value = "" + sel_value.charAt(0);
                }
                sel_hour = Integer.valueOf(sel_value);
                utils.showLog(TAG, "sel item are " + sel_hour, Config.Utils);
*/


            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                utils.userSession.setNotifyHour(sel_value[0]);
                setMenuCounter(R.id.notify_me, R.drawable.count_bg, utils.getNotify_IntervellMills());
                utils.callService();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

    public void getdate() {

/*
        FragmentManager fm = getFragmentManager();
        DialogFragment newFragment = new FilterByDate();
        newFragment.show(fm, "datePicker");
*/

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        String from_date = utils.query_fromdate(Arrays.asList(utils.userSession.getFROM_DATE()));

        String to_date = utils.query_fromdate(Arrays.asList(utils.userSession.getTO_DATE()));

        utils.showLog(TAG, "calendar from date is " + from_date + " to date is " + to_date, Config.MainActivity);

        Calendar from_cal = Calendar.getInstance();
        Calendar to_cal = Calendar.getInstance();

        try {
            from_cal.setTime(sdf.parse(from_date));
        } catch (ParseException e) {
            e.printStackTrace();
            from_cal.setTime(new Date());
        }

        try {
            to_cal.setTime(sdf.parse(to_date));
        } catch (ParseException e) {
            e.printStackTrace();
            to_cal.setTime(new Date());
        }

        utils.showLog(TAG, "calendar from mnth is " + from_cal.get(Calendar.MONTH) +
                " to mnth is " + to_cal.get(Calendar.MONTH), Config.MainActivity);

        SmoothDateRangePickerFragment smoothDateRangePickerFragment =
                SmoothDateRangePickerFragment
                        .newInstance(new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                                         @Override
                                         public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                                                    int yearStart, int monthStart,
                                                                    int dayStart, int yearEnd,
                                                                    int monthEnd, int dayEnd) {
                                             String date = "You picked the following date range: \n"
                                                     + "From " + dayStart + "/" + (++monthStart)
                                                     + "/" + yearStart + " To " + dayEnd + "/"
                                                     + (++monthEnd) + "/" + yearEnd;

                                             utils.userSession.setFROM_DATE(yearStart + "-" + monthStart + "-" + dayStart);
                                             utils.userSession.setTO_DATE(yearEnd + "-" + monthEnd + "-" + dayEnd);

                                             utils.showLog(TAG, "date is " + utils.userSession.getFROM_DATE() + "  to  " +
                                                     utils.userSession.getTO_DATE(), Config.HOME_SCREEN);

                                             setMenuCounter(R.id.date_filter, R.drawable.count_bg, 1);
                                             utils.add_query_data();
                                             call_homeFragment(Constants.Intent_TRACK);
                                         }
                                     },
                                from_cal.get(Calendar.YEAR), from_cal.get(Calendar.MONTH), from_cal.get(Calendar.DATE),
                                to_cal.get(Calendar.YEAR), to_cal.get(Calendar.MONTH), to_cal.get(Calendar.DATE));

        Calendar cal_max = Calendar.getInstance();
        // cal.add(Calendar.DATE, 1);
        utils.showLog(TAG, "set max date calendar is " + cal_max.get(Calendar.MONTH) + " date " +
                cal_max.get(Calendar.DATE), Config.MainActivity);

        smoothDateRangePickerFragment.setMaxDate(cal_max);
        Calendar cal_main = Calendar.getInstance();
        cal_main.add(Calendar.MONTH, -6);
        smoothDateRangePickerFragment.setMinDate(cal_main);
        smoothDateRangePickerFragment.show(getFragmentManager(), "Buzzinga");
    }

    public void swipedata() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
    }

    public void call_homeFragment(String value) {
        Bundle bundle = new Bundle();
        if (value == null)
            value = "";
        bundle.putString(Constants.Intent_OPERATION, value);
        Fragment fragment = new HomeScreen();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();

    }
}
