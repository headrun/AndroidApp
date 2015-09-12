package in.headrun.buzzinga.activities;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.BuzzNotification;
import in.headrun.buzzinga.BuzzingaRequest;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.doto.SearchDetails;
import in.headrun.buzzinga.doto.Test;
import in.headrun.buzzinga.doto.Utils;
import in.headrun.buzzinga.utils.FilterByDate;
import in.headrun.buzzinga.utils.JsonData;
import in.headrun.buzzinga.utils.SearchListData;

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

    public ProgressDialog mProgressDialog;

    Utils query;
    @Bind(R.id.filterpanel)
    View filterpanel;
    @Bind(R.id.webview_lay)
    View webview_lay;
    @Bind(R.id.filtersource_lay)
    View filtersource_lay;
    @Bind(R.id.filterdate_lay)
    View filterdate_lay;
    @Bind(R.id.browsertitle)
    TextView browsertitle;
    @Bind(R.id.filterdate)
    TextView filtersourcebtn;
    @Bind(R.id.webview)
    WebView webview;
    @Bind(R.id.closebtn)
    ImageView closebtn;
    @Bind(R.id.closebrowser)
    ImageView closebrowser;
    @Bind(R.id.bydatefilter)
    Button bydatefilter;

    @Bind(R.id.progressBar)
    ProgressBar progress;
    Test buzztest;
    UserSession userSession;
    SearchListData search_adapter;
    LayoutInflater inflater;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();
        Intent_opt = data.getString(Constants.Intent_OPERATION);

        handleIntent(getIntent());
        if (Config.HOME_SCREEN)
            Log.i(TAG, "HOME SCREEN");
        query = new Utils(getApplication());
        display_data = (ListView) findViewById(R.id.result_listview);
        content_lay = findViewById(R.id.content_lay);
        fromdate = (TextView) findViewById(R.id.fromdate);
        todate = (TextView) findViewById(R.id.todate);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        context = getApplication();

        userSession = new UserSession(HomeScreen.this);

        inflater = this.getLayoutInflater();

        filtersourcebtn.setOnClickListener(this);
        // filterdatebtn.setOnClickListener(this);
        bydatefilter.setOnClickListener(this);
        closebtn.setOnClickListener(this);
        closebrowser.setOnClickListener(this);
        fromdate.setOnClickListener(this);
        todate.setOnClickListener(this);
        filtersource_lay.setOnClickListener(this);
        filterpanel.setOnClickListener(this);

        buzztest = new Test(getApplication());
        search_adapter = new SearchListData(HomeScreen.this, 1);

        content_lay.setVisibility(View.GONE);
        filterpanel.setVisibility(View.GONE);
        webview_lay.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.buzz_logo);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.BTRACKKEY.get(0));
        webSettings();

        display_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SearchDetails details = (SearchDetails) display_data.getAdapter().getItem(position);
                String geturl = details.getUrl();
                browsertitle.setText(details.getTitle() + "....");
                webview_lay.setVisibility(View.VISIBLE);
                webview.loadUrl(geturl);
                Log.i("Log_tag", "selectd url is" + geturl);

            }
        });

        display_data.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastIndexInScreen = visibleItemCount + firstVisibleItem;
                if (display_data.getCount() != 0 && lastIndexInScreen >= totalItemCount - 5) {
                    Log.i(TAG, "scrolling is on");
                    Config.SwipeLoading = true;
                    footerView = inflater.inflate(R.layout.listviewfooter, null);
                    display_data.addFooterView(footerView);
                    Log.i(TAG, "listview footer count" + display_data.getFooterViewsCount());
                    getServer_response();
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
                getServer_response();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Intent_opt.contains(Constants.Intent_TRACK)) {
            //new getresponce().execute();
            getServer_response();
        } else if (Intent_opt.contains(Constants.Intent_NOtifi)) {
            display_data.setVisibility(View.VISIBLE);
            content_lay.setVisibility(View.VISIBLE);
            Log.i(TAG, "notification");
        }
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
                Log.i("Log_tag", "display_data length is" + display_data.getCount());
                Constants.BSEARCHKEY.clear();
                Constants.BSEARCHKEY.add(Constants.SEARCHSTRING);

                getServer_response();
            }
        }
    }

    public void getdate() {

        FragmentManager fm = getFragmentManager();
        DialogFragment newFragment = new FilterByDate();
        newFragment.show(fm, "datePicker");
    }

    private void webSettings() {

        webview.setWebViewClient(new MyBrowser());
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filtersource_lay:
                startActivity(new Intent(this, Filtering.class));
                break;
            case R.id.filterdate:
                content_lay.setVisibility(View.VISIBLE);
                filterpanel.setVisibility(View.VISIBLE);
                webview_lay.setVisibility(View.GONE);
                break;
            case R.id.closebtn:
                filterpanel.setVisibility(View.GONE);
                break;
            case R.id.fromdate:
                Constants.BFROMDATE.clear();
                DATEFLAG = 0;
                getdate();
                break;
            case R.id.todate:
                Constants.BTODATE.clear();
                getdate();
                break;
            case R.id.bydatefilter:
                filterpanel.setVisibility(View.GONE);

                getServer_response();
                break;
            case R.id.closebrowser:
                webview_lay.setVisibility(View.GONE);
                webview.destroy();
                progress.setProgress(100);
                break;
            case R.id.filterpanel:
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout)
            stringrequest();
        if (id == R.id.action_track) {
            if (item.isChecked()) {
                item.setChecked(false);
                stopService(new Intent(getBaseContext(), BuzzNotification.class));
            } else {
                item.setChecked(true);
                startService(new Intent(getBaseContext(), BuzzNotification.class));
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void stringrequest() {

        progress.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ServerConfig.SERVER_ENDPOINT + ServerConfig.logout,
                new Response.Listener<String>() {
                    @Override
                    public ArrayList<SearchDetails> onResponse(String response) {

                        Log.d(TAG, "string response is" + response);
                        stopService(new Intent(HomeScreen.this, BuzzNotification.class));
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

            ;
        };
        stringRequest.setTag(TAG);
        //BuzzingaApplication.get().getRequestQueue().add(stringRequest);
        BuzzingaRequest.getInstance(getApplication()).addToRequestQueue(stringRequest);
    }

    public void getServer_response() {

        if (swipeRefreshLayout.isRefreshing() || display_data.getFooterViewsCount() == 1)
            progress.setVisibility(View.GONE);
        else
            progress.setVisibility(View.VISIBLE);

        userSession.setSCROLLID(Constants.scroolid);
        userSession.setClubbedquery(query.getquerydata(Constants.QueryString));
        userSession.setSETUP(Constants.SETUP);
        userSession.setTIMEZONE(Utils.timezone());

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ServerConfig.SERVER_ENDPOINT + ServerConfig.search,
                new Response.Listener<String>() {
                    @Override
                    public ArrayList<SearchDetails> onResponse(String response) {

                        ArrayList<SearchDetails> list_response = new JsonData().getJsonData(response);
                        if (list_response.isEmpty()) {
                            Toast.makeText(HomeScreen.this, "No  articles found", Toast.LENGTH_LONG).show();
                        } else {
                            display_data.setAdapter(new SearchListData(context, list_response));
                        }
                        progress.setVisibility(View.GONE);

                        content_lay.setVisibility(View.VISIBLE);

                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                        display_data.removeFooterView(footerView);

                        return null;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "string error response is" + error.getMessage() + "\nmessage is" + error.getMessage());
                Toast.makeText(HomeScreen.this, "No Resuls found try again", Toast.LENGTH_LONG).show();
                progress.setVisibility(View.GONE);

                content_lay.setVisibility(View.VISIBLE);

                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                display_data.removeFooterView(footerView);

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                if (!userSession.getSCROLLID().equals("1")) {
                    Log.i(TAG, "qury on scrool");
                    params.put("scroll_id", userSession.getSCROLLID());
                } else {
                    Log.i(TAG, "qury on TZone");
                    params.put("tz", userSession.getTIMEZONE());
                }
                params.put("clubbed_query", userSession.getClubbedquery());
                params.put("setup", userSession.getSETUP());

                return params;
            }

            ;

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

            ;
        };


        //BuzzingaApplication.get().getRequestQueue().add(stringRequest);
        BuzzingaRequest.getInstance(getApplication()).addToRequestQueue(stringRequest);
    }


    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            progress.setProgress(100);
            super.onPageFinished(view, url);

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            progress.setProgress(0);
            super.onPageStarted(view, url, favicon);
        }
    }

    public static void display_articles(ArrayList<SearchDetails> article_list) {

        display_data.setAdapter(new SearchListData(context, article_list));
        content_lay.setVisibility(View.VISIBLE);
    }
}
