package in.headrun.buzzinga.activities;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;

import in.headrun.buzzinga.*;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.doto.QueryData;
import in.headrun.buzzinga.doto.SearchDetails;
import in.headrun.buzzinga.doto.Utils;
import in.headrun.buzzinga.utils.ConnectionSettings;
import in.headrun.buzzinga.utils.FilterByDate;
import in.headrun.buzzinga.utils.JsonData;
import in.headrun.buzzinga.utils.SearchListData;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by headrun on 7/7/15.
 */
public class HomeScreen extends AppCompatActivity implements View.OnClickListener {

    public static ListView display_data;
    public static View content_lay;
    public static TextView fromdate, todate;
    public static SwipeRefreshLayout swipeRefreshLayout;
    public static String Intent_opt;
    public static int DATEFLAG;
    public static View footerView;
    static Context context;
    public String TAG = HomeScreen.this.getClass().getSimpleName();
    BuzzingaApplication buzzapp;
    AlertDialog alertDialog;

    Utils utils;
    @Bind(R.id.filterpanel)
    View filterpanel;
    @Bind(R.id.webview_lay)
    View webview_lay;

    @Bind(R.id.filtersource_lay)
    View filtersource_lay;
    @Bind(R.id.filter_image)
    ImageView filter_image;
    @Bind(R.id.filtersource)
    TextView filtersource;

    @Bind(R.id.filterdate_lay)
    View filterdate_lay;
    @Bind(R.id.sort_iamge)
    ImageView sort_iamge;


    @Bind(R.id.filterdate)
    TextView filtersourcebtn;
    @Bind(R.id.webview)
    WebView webview;
    @Bind(R.id.closebtn)
    ImageView closebtn;

    @Bind(R.id.bydatefilter)
    Button bydatefilter;
    @Bind(R.id.listfooter)
    View Listfooter;

    @Bind(R.id.progressBar)
    ProgressBar progress;
    @Bind(R.id.browser_progress)
    ProgressBar browser_progress;


    UserSession userSession;
    SearchListData search_adapter;
    LayoutInflater inflater;
    StringRequest stringRequest, serverRequest;
    android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();
        Intent_opt = data.getString(Constants.Intent_OPERATION);

        handleIntent(getIntent());
        if (Config.HOME_SCREEN)
            Log.i(TAG, "HOME SCREEN");
        utils = new Utils(getApplication());
        display_data = (ListView) findViewById(R.id.result_listview);
        content_lay = findViewById(R.id.content_lay);
        fromdate = (TextView) findViewById(R.id.fromdate);
        todate = (TextView) findViewById(R.id.todate);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        context = getApplication();

        userSession = new UserSession(HomeScreen.this);
        buzzapp = new BuzzingaApplication();

        alertDialog = new AlertDialog.Builder(this).create();
        inflater = this.getLayoutInflater();

        bydatefilter.setOnClickListener(this);
        closebtn.setOnClickListener(this);

        fromdate.setOnClickListener(this);
        todate.setOnClickListener(this);

        filtersource_lay.setOnClickListener(this);
        filter_image.setOnClickListener(this);
        filtersource.setOnClickListener(this);

        filterdate_lay.setOnClickListener(this);
        sort_iamge.setOnClickListener(this);
        filtersourcebtn.setOnClickListener(this);
        search_adapter = new SearchListData(HomeScreen.this, 1);

        content_lay.setVisibility(View.GONE);
        filterpanel.setVisibility(View.GONE);
        webview_lay.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        Listfooter.setVisibility(View.GONE);

        actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.buzz_logo);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle(userSession.getTrackKey());

        display_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SearchDetails details = (SearchDetails) display_data.getAdapter().getItem(position);
                String geturl = details.getUrl();

                if (ConnectionSettings.isConnected(HomeScreen.this)) {
                    Intent i = new Intent(HomeScreen.this, ArticleWebDisplay.class);
                    i.putExtra("url", geturl);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplication(), "Network error", Toast.LENGTH_LONG).show();
                }
            }
        });

        display_data.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastIndexInScreen = visibleItemCount + firstVisibleItem;
                int visibleChildCount = (display_data.getLastVisiblePosition() - display_data.getFirstVisiblePosition()) + 1;

                if (display_data.getCount() != 0 && lastIndexInScreen > visibleChildCount && lastIndexInScreen >= totalItemCount - 5 && !Config.SwipeLoading) {

                    if (!Constants.scroolid.equals("1")) {
                        Log.i(TAG, "Scrolling" + Constants.scroolid);
                        footerView = inflater.inflate(R.layout.listviewfooter, null);
                        display_data.addFooterView(footerView);
                        Config.SwipeLoading = true;
                        getServer_response(ServerConfig.SCROLL);
                    }

                }
            }
        });

        swipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#ff0000"),
                Color.parseColor("#00ff00"),
                Color.parseColor("#0000ff"),
                Color.parseColor("#f234ab"));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()

        {
            @Override
            public void onRefresh() {
                Constants.swipedata = true;

                getServer_response(ServerConfig.search);
            }
        });

        if (Constants.state != null) {

            display_data.onRestoreInstanceState(Constants.state);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Intent_opt.contains(Constants.Intent_TRACK)) {
            Constants.listdetails.clear();
            getServer_response(ServerConfig.search);
        } else if (Intent_opt.equals(Constants.Intent_NOtify)) {
            Log.i(TAG, "clear data for notification");

            Constants.listdetails.clear();
            getServer_response(ServerConfig.search);
        } else if (Intent_opt.equals(Constants.Intent_NOTHING)) {
            article_laoding(Constants.listdetails);
            content_lay.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        webview.onResume();
        Constants.state = display_data.onSaveInstanceState();
    }

    @Override
    public void onPause() {
        super.onPause();
        utils.add_query_data();
        webview.onPause();
        Constants.state = display_data.onSaveInstanceState();
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
                Constants.listdetails.clear();
                userSession.setTACK_SEARCH_KEY(Constants.SEARCHSTRING);
                BuzzingaApplication.BSEARCHKEY.clear();
                BuzzingaApplication.BSEARCHKEY.add(userSession.gettTACK_SEARCH_KEY());
                HomeScreen.display_data.setAdapter(null);
                Log.i(TAG, "key word search");
                actionBar.setTitle(userSession.getTrackKey() + "," + Constants.SEARCHSTRING);
                getServer_response(ServerConfig.search);
            }
        }

    }

    public void getdate() {

        FragmentManager fm = getFragmentManager();
        DialogFragment newFragment = new FilterByDate();
        newFragment.show(fm, "datePicker");
    }

    private void webSettings(String url) {

        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.clearCache(true);
        webview.clearHistory();
        webview.loadUrl("");


        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i(TAG, "started webview");
                browser_progress.setProgress(0);
                browser_progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i(TAG, "finished webview");
                browser_progress.setProgress(100);
                browser_progress.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.i(TAG, "load resource" + url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.i(TAG, "webview error code" + errorCode + "\n description" + description);
            }
        });


        webview.loadUrl(url.toString());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.filtersource_lay:
                // cancelRequest();
                Config.SwipeLoading = false;
                startActivity(new Intent(this, Filtering.class));
                break;

            case R.id.filter_image:
                //cancelRequest();
                Config.SwipeLoading = false;
                startActivity(new Intent(this, Filtering.class));
                break;

            case R.id.filtersource:
                //cancelRequest();
                Config.SwipeLoading = false;
                startActivity(new Intent(this, Filtering.class));
                break;


            case R.id.filterdate:
                //ActionBar.hide();
                filterpanel.setVisibility(View.VISIBLE);
                content_lay.setVisibility(View.VISIBLE);
                webview_lay.setVisibility(View.GONE);
                break;
            case R.id.sort_iamge:
                filterpanel.setVisibility(View.VISIBLE);
                content_lay.setVisibility(View.VISIBLE);
                webview_lay.setVisibility(View.GONE);
                break;

            case R.id.filterdate_lay:
                filterpanel.setVisibility(View.VISIBLE);
                content_lay.setVisibility(View.VISIBLE);
                webview_lay.setVisibility(View.GONE);
                break;

            case R.id.closebtn:
                filterpanel.setVisibility(View.GONE);
                break;

            case R.id.fromdate:
                BuzzingaApplication.BFROMDATE.clear();
                DATEFLAG = 0;
                getdate();
                utils.add_query_data();
                break;

            case R.id.todate:
                BuzzingaApplication.BTODATE.clear();
                getdate();
                utils.add_query_data();
                break;

            case R.id.bydatefilter:
                Config.SwipeLoading = false;
                filterpanel.setVisibility(View.GONE);
                Constants.listdetails.clear();
                display_data.setAdapter(null);
                Constants.BFROMDATE.clear();
                Constants.BTODATE.clear();
                Constants.BFROMDATE.add(fromdate.getText().toString());
                Constants.BTODATE.add(todate.getText().toString());
                utils.add_query_data();
                getServer_response(ServerConfig.search);
                userSession.setFROM_DATE(fromdate.getText().toString().trim());
                userSession.setTO_DATE(todate.getText().toString().trim());
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        Log.i(TAG, "userSession.isBUZZ_NOTIFY_SEL()" + userSession.isBUZZ_NOTIFY_SEL());
        menu.findItem(R.id.action_track).setChecked(userSession.isBUZZ_NOTIFY_SEL());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            stringrequest();

        } else if (id == R.id.action_track) {
            Boolean track_check;
            track_check = userSession.isBUZZ_NOTIFY_SEL();
            Log.i(TAG, "track_check" + track_check);
            item.setChecked(track_check);
            Log.i(TAG, " item.setChecked(track_check)" + item.setChecked(track_check));
            if (item.isChecked()) {
                userSession.setBUZZ_NOTIFY_SEL(false);
                item.setChecked(userSession.isBUZZ_NOTIFY_SEL());
                Log.i(TAG, "stop the service");
                // stopService(new Intent(getBaseContext(), BuzzNotification.class));

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, BuzzingaNotification.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                alarmManager.cancel(pendingIntent);
            } else {
                userSession.setBUZZ_NOTIFY_SEL(true);
                track_check = userSession.isBUZZ_NOTIFY_SEL();
                item.setChecked(track_check);
                Log.i(TAG, "false  item.setChecked(track_check)" + item.setChecked(track_check));

                //startService(new Intent(getBaseContext(), BuzzNotification.class));

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, BuzzingaNotification.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5 * 60 * 1000, pendingIntent);
                Log.i(TAG, "start the service");
            }
        } else if (id == android.R.id.home) {
            Config.SwipeLoading = false;
            Intent i = new Intent(HomeScreen.this, TrackKeyWord.class);
            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }

    public void stringrequest() {
        if (new ConnectionSettings().isConnected(getApplication())) {
            progress.setVisibility(View.VISIBLE);
            stringRequest = new StringRequest(Request.Method.GET, ServerConfig.SERVER_ENDPOINT + ServerConfig.logout,
                    new Response.Listener<String>() {
                        @Override
                        public ArrayList<SearchDetails> onResponse(String response) {

                            Log.d(TAG, "string response is" + response);

                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(context, BuzzingaNotification.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                            alarmManager.cancel(pendingIntent);
                            // stopService(new Intent(HomeScreen.this, .class));
                            new UserSession(HomeScreen.this).clearsession();
                            startActivity(new Intent(HomeScreen.this, TwitterLogin.class));
                            progress.setVisibility(View.GONE);
                            return null;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "string error response is" + error);
                    progress.setVisibility(View.GONE);
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

    public void getServer_response(String URL_request) {

        final String clubbed_query;
        if (new ConnectionSettings().isConnected(getApplication())) {
            if (swipeRefreshLayout.isRefreshing() || Config.SwipeLoading)
                progress.setVisibility(View.GONE);
            else {
                progress.setVisibility(View.VISIBLE);
            }

            Log.i(TAG, "Config.SwipeLoading" + Config.SwipeLoading);
            if (Config.SwipeLoading) {
                clubbed_query = "{\"scroll_id\":\"" + Constants.scroolid + "\",\"scroll_timeout\":\"10m\"}";
            } else if (Intent_opt.equals(Constants.Intent_NOtify)) {

                Constants.BFROMDATE.clear();
                Constants.BTODATE.clear();
                Constants.BFROMDATE.add("");
                Constants.BTODATE.add("");
                utils.add_query_data();

                //clubbed_query = query.queryform(query.Date_added_toquery());
                userSession.set_search_Clubbedquery(utils.getquerydata(Constants.QueryString));
                clubbed_query = userSession.get_search_Clubbedquery();
            } else {
                Log.i(TAG, "search");

                userSession.set_search_Clubbedquery(utils.getquerydata(Constants.QueryString));
                clubbed_query = userSession.get_search_Clubbedquery();
            }

            Intent_opt = "";

            userSession.setSETUP(Constants.SETUP);
            userSession.setTIMEZONE(Utils.timezone());

            Log.i(TAG, "url is" + ServerConfig.SERVER_ENDPOINT + URL_request);
            serverRequest = new StringRequest(Request.Method.POST, ServerConfig.SERVER_ENDPOINT + URL_request,
                    new Response.Listener<String>() {
                        @Override
                        public ArrayList<SearchDetails> onResponse(String response) {

                            ArrayList<SearchDetails> list_response = new JsonData().getJsonData(response);
                            if (list_response.isEmpty()) {
                                Toast.makeText(HomeScreen.this, "No  articles found", Toast.LENGTH_LONG).show();
                            } else {
                                article_laoding(list_response);
                            }
                            progress.setVisibility(View.GONE);
                            content_lay.setVisibility(View.VISIBLE);

                            if (swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);
                            if (Config.SwipeLoading) {
                                display_data.removeFooterView(footerView);
                                Config.SwipeLoading = false;
                            }

                            return null;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    error.printStackTrace();


                    if (error instanceof TimeoutError) {

                        Log.i(TAG, "time out error");

                    } else if (error.networkResponse != null && error.networkResponse.data != null) {
                        byte[] error_resp = error.networkResponse.data;
                        webview.loadData(new String(error.networkResponse.data), "text/html", "UTF-8");
                        webview_lay.setVisibility(View.VISIBLE);
                    }


                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);
                    if (Config.SwipeLoading) {

                        display_data.removeFooterView(footerView);
                        Config.SwipeLoading = false;
                    }
                    progress.setVisibility(View.GONE);
                    content_lay.setVisibility(View.VISIBLE);
                }
            }) {

                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    UserSession usersess = new UserSession(HomeScreen.this);
                    Log.i(TAG, "!Config.SwipeLoading" + !Config.SwipeLoading);
                    if (!Config.SwipeLoading) {
                        params.put("tz", usersess.getTIMEZONE());
                        Log.i(TAG, "time zone is" + usersess.getTIMEZONE());
                    }
                    params.put("clubbed_query", clubbed_query);
                    params.put("setup", usersess.getSETUP());
                    Log.i(TAG, "query" + clubbed_query + "\nsetup" + usersess.getSETUP());

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    String sessionid = new UserSession(HomeScreen.this).getTSESSION();
                    if (sessionid.length() > 0) {
                        Log.i(TAG, "session id is" + sessionid);
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

                    return headers;
                }

            };
            serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                    2000,
                    5,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            serverRequest.setTag(TAG);
            BuzzingaRequest.getInstance(getApplication()).addToRequestQueue(serverRequest);

        } else {
            progress.setVisibility(View.GONE);
            content_lay.setVisibility(View.VISIBLE);

            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
            if (Config.SwipeLoading) {
                display_data.removeFooterView(footerView);
                Config.SwipeLoading = false;
            }
            Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
        }
    }

    public void article_laoding(ArrayList<SearchDetails> list_response) {

        Constants.state = display_data.onSaveInstanceState();
        SearchListData articles_listdata = new SearchListData(context, list_response);
        articles_listdata.notifyDataSetChanged();
        display_data.setAdapter(new SearchListData(context, list_response));
        SearchDetails fistitem = (SearchDetails) display_data.getItemAtPosition(0);
        userSession.setLatestDate(fistitem.getArticledate());
        if (Config.SwipeLoading || Intent_opt.equals(Constants.Intent_NOTHING)) {
            Log.i(TAG, "Constant state is" + Constants.state.toString());
            display_data.onRestoreInstanceState(Constants.state);
            Intent_opt = "";
        } else {
            if (display_data.getCount() < 0)
                display_data.setSelection(0);
            Constants.state = display_data.onSaveInstanceState();
            display_data.onRestoreInstanceState(Constants.state);
        }
    }


    public void cancelRequest() {
        if (serverRequest != null) {
            Log.i(TAG, "cancel the request");
            BuzzingaRequest.getInstance(getApplication()).cancelRequestQueue(TAG);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (serverRequest != null)
            BuzzingaRequest.getInstance(getApplication()).cancelRequestQueue(TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.add_query_data();
    }
}
