package in.headrun.buzzinga.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import in.headrun.buzzinga.adapters.SearchListDataAdapter;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.dashboard.ListViewMultiChartActivity;
import in.headrun.buzzinga.doto.SearchArticles;
import in.headrun.buzzinga.utils.Utils;

/**
 * Created by headrun on 7/7/15.
 */
public class HomeScreen extends AppCompatActivity implements View.OnClickListener, Utils.setOnItemClickListner {


    public String TAG = HomeScreen.this.getClass().getSimpleName();

    @Bind(R.id.filtersource)
    TextView filtersource;

    @Bind(R.id.filterdate)
    TextView filterdate;

    @Bind(R.id.newarticle)
    TextView newarticle;

    @Bind(R.id.txt_info)
    TextView txt_info;

    @Bind(R.id.progressBar)
    ProgressBar progressbar;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.result_listview)
    RecyclerView display_data;

    Utils utils;
    public String Intent_opt;

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    StringRequest stringRequest, serverRequest;


    SearchListDataAdapter searchAdapter;
    JSONObject jobj, jobj_result, jobj_hit;
    JSONArray jobj_hits;

    LinearLayoutManager mLinearLayout;
    boolean scroll_loading = true;
    public static boolean Swipe_loading = true;
    ArrayList<SearchArticles> searchArticleSwipe;

    public static Parcelable state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        ButterKnife.bind(this);

        utils = new Utils(this);
        mLinearLayout = new LinearLayoutManager(this);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setLogo(R.drawable.buzz_logo);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(utils.userSession.getTrackKey());

        Bundle data = getIntent().getExtras();
        Intent_opt = data.getString(Constants.Intent_OPERATION);

        handleIntent(getIntent());

        if (Intent_opt.equals(Constants.TRACKKEY))
            Constants.SEARCHARTICLES.clear();

        display_data.setHasFixedSize(true);
        display_data.setLayoutManager(mLinearLayout);
        searchAdapter = new SearchListDataAdapter(this, Constants.SEARCHARTICLES);
        display_data.setAdapter(searchAdapter);
        searchAdapter.setClickListener(this);


        newarticle.setOnClickListener(this);
        filtersource.setOnClickListener(this);
        filterdate.setOnClickListener(this);

        display_data.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    visibleItemCount = mLinearLayout.getChildCount();
                    totalItemCount = mLinearLayout.getItemCount();
                    pastVisiblesItems = mLinearLayout.findFirstCompletelyVisibleItemPosition();

                    if (scroll_loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        if (swipeRefreshLayout.isRefreshing() == false && !Constants.scroolid.equals("1") && utils.isNetwrokConnection()) {

                            scroll_loading = false;
                            utils.add_query_data();

                            Constants.SEARCHARTICLES.add(null);
                            searchAdapter.notifyItemInserted(Constants.SEARCHARTICLES.size() - 1);

                            getServer_response(ServerConfig.SCROLL, utils.scrollQuery());
                        }
                    }
                }
            }
        });

        swipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#ff0000"),
                Color.parseColor("#00ff00"),
                Color.parseColor("#0000ff"),
                Color.parseColor("#f234ab"));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(true);
                if (newarticle.getVisibility() == View.VISIBLE)
                    newarticle.setVisibility(View.GONE);

                if (utils.isNetwrokConnection()) {
                    Swipe_loading = false;
                    utils.add_query_data();
                    getServer_response(ServerConfig.search, utils.searchQuery());
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        if (state != null) {
            display_data.getLayoutManager().onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onstart");
        utils.add_query_data();
        if (Intent_opt.equals(Constants.Intent_TRACK))
            getServer_response(ServerConfig.search, utils.searchQuery());

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onresume");

        state = display_data.getLayoutManager().onSaveInstanceState();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onpause");
        Intent_opt = Constants.Intent_NOTHING;
        //
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onstop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestory");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Constants.SEARCHSTRING = intent.getStringExtra(SearchManager.QUERY);
            if (Constants.SEARCHSTRING.trim().length() > 0) {

                if (utils.isNetwrokConnection()) {
                    utils.userSession.setTACK_SEARCH_KEY(Constants.SEARCHSTRING);

                    Constants.SEARCHARTICLES.clear();
                    searchAdapter.notifyDataSetChanged();

                    getSupportActionBar().setTitle(utils.userSession.getTrackKey() + " AND " +
                            utils.userSession.gettTACK_SEARCH_KEY());

                    utils.add_query_data();
                    getServer_response(ServerConfig.search, utils.searchQuery());
                }
            }
        }

    }

    public void getdate() {

       /* FragmentManager fm = getFragmentManager();
        DialogFragment newFragment = new FilterByDate();
        newFragment.show(fm, "datePicker");*/


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

                                utils.add_query_data();
                                getServer_response(ServerConfig.search, utils.searchQuery());
                            }
                        });
        smoothDateRangePickerFragment.show(getFragmentManager(), "Buzzinga");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.filtersource:
                startActivity(new Intent(this, Filtering.class));
                break;

            case R.id.filterdate:
                getdate();
                break;

            case R.id.newarticle:
                newarticle.setVisibility(View.GONE);
                utils.add_query_data();
                getServer_response(ServerConfig.search, utils.searchQuery());
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        menu.findItem(R.id.action_track).setChecked(utils.userSession.isBUZZ_NOTIFY_SEL());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            stringrequest();
        } else if (id == R.id.dashboard) {
            Log.i(TAG, "TAG is" + TAG);
            if (TAG.equals("ListViewMultiChartActivity")) {
                startActivity(new Intent(this, HomeScreen.class));
            } else {
                startActivity(new Intent(this, ListViewMultiChartActivity.class));
            }

        } else if (id == R.id.action_track) {

            Boolean track_check;
            track_check = utils.userSession.isBUZZ_NOTIFY_SEL();

            item.setChecked(track_check);

            if (item.isChecked()) {
                utils.userSession.setBUZZ_NOTIFY_SEL(false);
                item.setChecked(utils.userSession.isBUZZ_NOTIFY_SEL());
                Log.i(TAG, "stop the service");
                // stopService(new Intent(getBaseContext(), BuzzNotification.class));
                try {
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(this, BuzzingaNotification.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                    alarmManager.cancel(pendingIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                utils.userSession.setBUZZ_NOTIFY_SEL(true);
                track_check = utils.userSession.isBUZZ_NOTIFY_SEL();

                item.setChecked(track_check);
                Log.i(TAG, "false  item.setChecked(track_check)" + item.setChecked(track_check));
                try {
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(this, BuzzingaNotification.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1 * 1 * 1000, 3 * 60 * 1000, pendingIntent);
                    Log.i(TAG, "start the service");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "start the service");
            }
        } else if (id == android.R.id.home) {
            startActivity(new Intent(HomeScreen.this, TrackKeyWord.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void stringrequest() {
        if (utils.isNetwrokConnection()) {
            progressbar.setVisibility(View.VISIBLE);
            stringRequest = new StringRequest(Request.Method.GET, ServerConfig.SERVER_ENDPOINT + ServerConfig.logout,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d(TAG, "string response is" + response);

                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(HomeScreen.this, BuzzingaNotification.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(HomeScreen.this, 0, intent, 0);
                            alarmManager.cancel(pendingIntent);
                            // stopService(new Intent(HomeScreen.this, .class));
                            ;
                            utils.userSession.clearsession(utils.userSession.TSESSION);
                            startActivity(new Intent(HomeScreen.this, TwitterLogin.class));
                            progressbar.setVisibility(View.GONE);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "string error response is" + error);
                    progressbar.setVisibility(View.GONE);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("sessionid", new UserSession(HomeScreen.this).getTSESSION());
                    return params;
                }
            };
            stringRequest.setTag(TAG);

            BuzzingaRequest.getInstance(getApplication()).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
        }
    }

    public void getServer_response(String URL_request, String clubbedquery) {

        BuzzingaRequest.getInstance(getApplication()).cancelRequestQueue(TAG);
        txt_info.setVisibility(View.GONE);
        final String clubbed_query = clubbedquery;

        if (utils.isNetwrokConnection()) {

            if (swipeRefreshLayout.isRefreshing() == true)
                progressbar.setVisibility(View.GONE);
            else
                progressbar.setVisibility(View.VISIBLE);


            utils.userSession.setSETUP(Constants.SETUP);
            utils.userSession.setTIMEZONE(Utils.timezone());

            Log.i(TAG, "url is" + ServerConfig.SERVER_ENDPOINT + URL_request);
            serverRequest = new StringRequest(Request.Method.POST, ServerConfig.SERVER_ENDPOINT + URL_request,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            utils.showLog(TAG, "resposne is " + response.toString(), Config.HOME_SCREEN);


                            article_loading(response);

                            if (swipeRefreshLayout.isRefreshing() == true) {
                                Swipe_loading = true;
                                swipeRefreshLayout.setRefreshing(false);
                            } else
                                progressbar.setVisibility(View.GONE);

                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    error.printStackTrace();

                    if (error instanceof ServerError) {

                        new AlertDialog.Builder(HomeScreen.this).setTitle("Buzzing Error Message").
                                setMessage("Serer error").
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                    }
                                }).show();
                    } else if (error instanceof AuthFailureError && error.networkResponse.data != null) {

                    } else if (error instanceof TimeoutError) {

                        Log.i(TAG, "time out error");

                    } else if (error.networkResponse != null && error.networkResponse.data != null) {
                        byte[] error_resp = error.networkResponse.data;

                    }

                    if (swipeRefreshLayout.isRefreshing()) {
                        Swipe_loading = true;
                        swipeRefreshLayout.setRefreshing(false);
                    } else
                        progressbar.setVisibility(View.GONE);
                    scroll_loading = true;
                }
            }) {

                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    UserSession usersess = new UserSession(HomeScreen.this);

                    if (swipeRefreshLayout.isRefreshing() == true)
                        params.put("tz", usersess.getTIMEZONE());

                    params.put("clubbed_query", clubbed_query);
                    params.put("setup", usersess.getSETUP());

                    utils.showLog(TAG, "params are " + params, Config.HOME_SCREEN);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    String sessionid = new UserSession(HomeScreen.this).getTSESSION();
                    if (sessionid.length() > 0) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("sessionid");
                        builder.append("=");
                        builder.append(sessionid);
                        if (headers.containsKey("Cookie")) {
                            builder.append("; ");
                            builder.append(headers.get("Cookie"));
                        }
                        headers.put("Cookie", builder.toString());
                    }

                    utils.showLog(TAG, "headers are " + headers, Config.HOME_SCREEN);
                    return headers;
                }
            };
            serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30 * 1000,
                    3,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            serverRequest.setTag(TAG);
            BuzzingaRequest.getInstance(getApplication()).addToRequestQueue(serverRequest);

        } else {

            if (swipeRefreshLayout.isRefreshing()) {
                Swipe_loading = true;
                swipeRefreshLayout.setRefreshing(false);
            } else
                progressbar.setVisibility(View.GONE);
            scroll_loading = true;

        }

    }

    public void article_loading(String response) {


        if (scroll_loading == false) {
            Constants.SEARCHARTICLES.remove(Constants.SEARCHARTICLES.size() - 1);
            searchAdapter.notifyItemRemoved(Constants.SEARCHARTICLES.size());
            scroll_loading = true;
        }

        getJsonData(response);

        state = display_data.getLayoutManager().onSaveInstanceState();

        searchAdapter.notifyDataSetChanged();
        display_data.getLayoutManager().onRestoreInstanceState(state);

        if (swipeRefreshLayout.isRefreshing() == true || Intent_opt.equals(Constants.Intent_NOTHING)) {
            Intent_opt = "";
        }

    }

    @Override
    public void itemClicked(View view, int position) {

        String geturl = "";
        if (Constants.SEARCHARTICLES.size() > position)
            geturl = Constants.SEARCHARTICLES.get(position).source.URL;

        if (utils.isNetwrokConnection()) {
            Intent i = new Intent(HomeScreen.this, ArticleWebDisplay.class);
            i.putExtra("url", geturl);
            startActivity(i);
        } else {
            Toast.makeText(getApplication(), "Network error", Toast.LENGTH_LONG).show();
        }

    }

    public void getJsonData(String data) {

        try {
            jobj = new JSONObject(data);
            if (jobj.getString("error").equals("0")) {

                jobj_result = new JSONObject(jobj.getString("result"));
                jobj_hit = new JSONObject(jobj_result.getString("hits"));
                jobj_hits = new JSONArray(jobj_hit.getString("hits"));
                Constants.scroolid = jobj_result.optString("_scroll_id");

                if (jobj_hits.length() > 0) {

                    if (swipeRefreshLayout.isRefreshing() == true) {
                        searchArticleSwipe = new ArrayList<SearchArticles>();
                        addArticletoList(searchArticleSwipe, jobj_hits.toString());
                        Constants.SEARCHARTICLES.clear();
                        Constants.SEARCHARTICLES.addAll(searchArticleSwipe);
                    } else {
                        addArticletoList(Constants.SEARCHARTICLES, jobj_hits.toString());
                    }

                } else {
                    Constants.scroolid = "1";
                }

                if (Constants.SEARCHARTICLES.size() <= 0) {
                    txt_info.setVisibility(View.VISIBLE);
                    utils.warning_info(txt_info, Constants.NO_RECORD);
                } else {
                    txt_info.setVisibility(View.GONE);
                }
            }
            Log.i(TAG, "Articles size " + Constants.SEARCHARTICLES.size());

            //   articleDetails();
        } catch (JSONException e) {
            Log.i(TAG, "exception" + e);
            e.printStackTrace();
        }

    }

    public void addArticletoList(ArrayList<SearchArticles> list, String response) {

        list.addAll((ArrayList<SearchArticles>) new Gson().fromJson(response,
                new TypeToken<ArrayList<SearchArticles>>() {
                }.getType()));

    }

    public void articleDetails() {
        for (SearchArticles item : Constants.SEARCHARTICLES) {
            long seconds = Long.parseLong(item.source.DATE_ADDED);
            // Log.i(TAG, "article time is" + seconds);
            long millis = seconds * 1000;
            Date date = new Date(millis);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.getDefault());

            utils.showLog(TAG, "url is \t" + item.source.URL + "\ntime is\t" + sdf.format(date) + "\nauthor is" +
                    item.source.AUTHOR.NAME, Config.SearchListDataAdapter);
        }
    }
}
