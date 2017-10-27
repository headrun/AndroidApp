package in.headrun.buzzinga.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

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

import in.headrun.buzzinga.BuzzingaApplication;
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
    @Bind(R.id.search_view)
    MaterialSearchView searchView;

    ActionBarDrawerToggle toggle;


    String Intent_opt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);


        Bundle bundle = getIntent().getExtras();
        try {
            Intent_opt = bundle.getString(Constants.Intent_OPERATION);
        } catch (Exception e) {
            e.printStackTrace();
            Intent_opt = Constants.Intent_NOTHING;
        }

        toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        setbadge();

        setMenuCounter(R.id.filter, R.drawable.count_bg, Utils.count_filter_sel());
        setMenuCounter(R.id.notify_me, R.drawable.count_bg, Utils.getNotify_IntervellMills());

        getSupportActionBar().setTitle("");
        title.setText(Utils.setTitle(this));

        //onNavigationItemSelected(navigationView.getMenu().getItem(0));
        navigationView.getMenu().findItem(R.id.edit_keyword).setVisible(false);

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

        searchView.setEllipsize(true);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setVoiceSearch(false);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Utils.showLog(TAG, "submit query is " + query, Config.MainActivity);
                Constants.SEARCHSTRING = query;

                BuzzingaApplication.getUserSession().setTACK_SEARCH_KEY(Constants.SEARCHSTRING);
                searchView.closeSearch();

                     /*Track key send to firebase*/
                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, BuzzingaApplication.getUserSession().getTrackKey());
                params.putString(FirebaseAnalytics.Param.SEARCH_TERM, BuzzingaApplication.getUserSession().gettTACK_SEARCH_KEY());
                Utils.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, params);
                Utils.mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

                searchview_text();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                Utils.showLog(TAG, "searchview text change" + newText, Config.MainActivity);
                return true;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

                Utils.showLog(TAG, "itemis expanded", Config.MainActivity);
                String search_key = BuzzingaApplication.getUserSession().gettTACK_SEARCH_KEY();
                searchView.setQuery(search_key, false);

            }

            @Override
            public void onSearchViewClosed() {

                Utils.showLog(TAG, "itemis closed", Config.MainActivity);

            }
        });

        call_homeFragment(Intent_opt);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //  client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            finish();
           /* startActivity(new Intent(this, TrackKeyWord.class).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK));
            this.overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);*/
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

        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.item_search);

        searchView.setMenuItem(searchItem);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.edit_keyword) {
            startActivity(new Intent(this, TrackKeyWord.class).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK));
            this.overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        } else if (id == R.id.filter) {
            startActivity(new Intent(this, Filtering.class));
            this.overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        } else if (id == R.id.date_filter) {
            Utils.getdate(this);
        } else if (id == R.id.notify_me) {
            notimyme(BuzzingaApplication.getUserSession().getNotifyHour());
        } else if (id == R.id.invite) {
            // final Uri deepLink = buildDeepLink(Uri.parse(Constants.DEEP_LINK_URL), 0, false);
            shareDeepLink(Constants.DEEP_LINK_URL);
        } else if (id == R.id.contact_us) {
            // final Uri deepLink = buildDeepLink(Uri.parse(Constants.DEEP_LINK_URL), 0, false);
            startActivity(new Intent(this, Contact_Activiy.class));
            this.overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        } else if (id == R.id.logout) {
            stringrequest();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void stringrequest() {
        if (Utils.isNetwrokConnection(this)) {
            progress_bar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, ServerConfig.SERVER_ENDPOINT + ServerConfig.logout,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d(TAG, "string response is" + response);

                            Utils.clearSessionData();
                            startActivity(new Intent(MainActivity.this, TwitterLogin.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                            overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
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

            BuzzingaApplication.get().addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
        }
    }

    private void searchview_text() {

        if (Utils.isNetwrokConnection(this)) {

            title.setText(Utils.setTitle(this));
            Utils.add_query_data();
            Intent_opt = Constants.Intent_TRACK;
            call_homeFragment(Intent_opt);
        } else {
            Snackbar.make(this.findViewById(R.id.drawer_layout), "No internet connection!", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    public void notimyme(final String sel_item) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.notifyme));

        final String[] items = getResources().getStringArray(R.array.notify_hours);

        int pos = -1;

        Utils.showLog(TAG, "sel item is" + sel_item, Config.Utils);

        if (!sel_item.toString().isEmpty())
            pos = Arrays.asList(items).indexOf(sel_item);

        Utils.showLog(TAG, "sel item pos is " + pos, Config.Utils);
        final String[] sel_value = {""};
        if (pos != -1)
            sel_value[0] = Arrays.asList(items).get(pos);
        final int position = pos;
        builder.setSingleChoiceItems(items, pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which != -1)
                    sel_value[0] = Arrays.asList(items).get(which).trim();


            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                BuzzingaApplication.getUserSession().setNotifyHour(sel_value[0]);
                setMenuCounter(R.id.notify_me, R.drawable.count_bg, Utils.getNotify_IntervellMills());
                setbadge();

                Bundle params = new Bundle();
                params.putString("notify", sel_value[0]);
                Utils.mFirebaseAnalytics.logEvent("Apply_notify", params);
                Utils.mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

                Utils.callService(MainActivity.this);
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

        String from_date = Utils.query_fromdate(Arrays.asList(BuzzingaApplication.getUserSession().getFROM_DATE()),this);

        String to_date = Utils.query_todate(Arrays.asList(BuzzingaApplication.getUserSession().getTO_DATE()));

        Utils.showLog(TAG, "calendar from date is " + from_date + " to date is " + to_date, Config.MainActivity);

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

        Utils.showLog(TAG, "calendar from mnth is " + from_cal.get(Calendar.MONTH) +
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

                                             BuzzingaApplication.getUserSession().setFROM_DATE(yearStart + "-" + monthStart + "-" + dayStart);
                                             BuzzingaApplication.getUserSession().setTO_DATE(yearEnd + "-" + monthEnd + "-" + dayEnd);

                                             Utils.showLog(TAG, "date is " + BuzzingaApplication.getUserSession().getFROM_DATE() + "  to  " +
                                                     BuzzingaApplication.getUserSession().getTO_DATE(), Config.HOME_SCREEN);

                                             // setMenuCounter(R.id.date_filter, R.drawable.count_bg, 1);
                                             Utils.add_query_data();

                                             Bundle params = new Bundle();
                                             params.putString("date", "apply_date");
                                             Utils.mFirebaseAnalytics.logEvent("Apply_Date", params);
                                             Utils.mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

                                             call_homeFragment(Constants.Intent_TRACK);
                                         }
                                     },
                                from_cal.get(Calendar.YEAR), from_cal.get(Calendar.MONTH), from_cal.get(Calendar.DATE),
                                to_cal.get(Calendar.YEAR), to_cal.get(Calendar.MONTH), to_cal.get(Calendar.DATE));

        Calendar cal_max = Calendar.getInstance();
        // cal.add(Calendar.DATE, 1);
        Utils.showLog(TAG, "set max date calendar is " + cal_max.get(Calendar.MONTH) + " date " +
                cal_max.get(Calendar.DATE), Config.MainActivity);

        smoothDateRangePickerFragment.setMaxDate(cal_max);
        Calendar cal_main = Calendar.getInstance();
        cal_main.add(Calendar.MONTH, -6);
        smoothDateRangePickerFragment.setMinDate(cal_main);
        smoothDateRangePickerFragment.show(getFragmentManager(), "Buzzinga");
    }

    public void call_homeFragment(String value) {

        Bundle bundle = new Bundle();
        if (value == null)
            value = "";
        bundle.putString(Constants.Intent_OPERATION, value);
        Fragment fragment = new Pager();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();

    }

    public void setbadge() {

        if (Utils.count_filter_sel() != 0 || Utils.count_sel_notifyme() != 0) {
            badger.setText("");
            badger.setVisibility(View.VISIBLE);
        } else {
            badger.setVisibility(View.GONE);
        }

    }

    private void shareDeepLink(String deeplink) {

        Utils.showLog(TAG, "deep  link is" + deeplink, Config.SPLASH);
        ShareCompat.IntentBuilder
                .from(this) // getActivity() or activity field if within Fragment
                .setText("https://play.google.com/store/apps/details?id=in.headrun.buzzinga")
                .setType("text/plain") // most general text sharing MIME type
                .setChooserTitle("Buzzinga Analytics")
                .startChooser();
    }
}
