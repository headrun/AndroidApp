package com.headrun.buzzinga.activities;

import android.app.DialogFragment;
import android.app.FragmentManager;
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

import com.headrun.buzzinga.R;
import com.headrun.buzzinga.UserSession;
import com.headrun.buzzinga.config.Config;
import com.headrun.buzzinga.config.Constants;
import com.headrun.buzzinga.doto.SearchDetails;
import com.headrun.buzzinga.doto.Test;
import com.headrun.buzzinga.utils.FilterByDate;
import com.headrun.buzzinga.utils.SearchListData;


/**
 * Created by headrun on 7/7/15.
 */
public class HomeScreen extends AppCompatActivity implements View.OnClickListener {

    public String TAG = HomeScreen.this.getClass().getSimpleName();


    public static ListView display_data;                     ////  Display the results

    public static View content_lay;
    public View filterpanel;
    public View webview_lay;                                ///// Layout of menu button.
    public View filtersource_lay,filterdate_lay;
    public static TextView fromdate, todate;
    public static TextView browsertitle;
    public WebView webview;
    public static SwipeRefreshLayout swipeRefreshLayout;
    public TextView filtersourcebtn, filterdatebtn;
    Button bydatefilter;
    public ImageView closebtn, closebrowser;

    Test buzztest;                                           ////  Call  the Test class
    UserSession userSession;
    SearchListData search_adapter;
    public static ProgressBar progress, browserprogess;                      ////  progress bar after clicking the checkbox

    public static int DATEFLAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        handleIntent(getIntent());
        if (Config.HOME_SCREEN)
            Log.i(TAG, "HOME SCREEN");


        userSession = new UserSession(HomeScreen.this);

        display_data = (ListView) findViewById(R.id.result_listview);
        webview = (WebView) findViewById(R.id.webview);
        content_lay = findViewById(R.id.content_lay);
        filterpanel = findViewById(R.id.filterpanel);
        webview_lay = findViewById(R.id.webview_lay);
        filtersource_lay=findViewById(R.id.filtersource_lay);
        filterdate_lay=findViewById(R.id.filterdate_lay);

        fromdate = (TextView) findViewById(R.id.fromdate);
        todate = (TextView) findViewById(R.id.todate);
        browsertitle = (TextView) findViewById(R.id.browsertitle);
        filtersourcebtn = (TextView) findViewById(R.id.filtersource);
        filterdatebtn = (TextView) findViewById(R.id.filterdate);
        bydatefilter = (Button) findViewById(R.id.bydatefilter);
        closebtn = (ImageView) findViewById(R.id.closebtn);
        closebrowser = (ImageView) findViewById(R.id.closebrowser);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        browserprogess = (ProgressBar) findViewById(R.id.browserprogress);

        filtersourcebtn.setOnClickListener(this);
        filterdatebtn.setOnClickListener(this);
        bydatefilter.setOnClickListener(this);
        closebtn.setOnClickListener(this);
        closebrowser.setOnClickListener(this);
        fromdate.setOnClickListener(this);
        todate.setOnClickListener(this);
        filtersource_lay.setOnClickListener(this);

        browserprogess.setVisibility(View.GONE);
        buzztest = new Test(getApplication());
        search_adapter = new SearchListData(HomeScreen.this, 1);
        content_lay.setVisibility(View.GONE);
        filterpanel.setVisibility(View.GONE);
        webview_lay.setVisibility(View.GONE);
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
                if (lastIndexInScreen >= totalItemCount - 1 && !Config.isLoading) {
                    Log.i("Log_tag", "start the fetching data");
                    Config.isLoading = true;

                    if (!Constants.scroolid.equals("1"))
                        Log.i(TAG, "calling the scool query");
                    buzztest.buzzdata(Constants.scroolid);
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
                //Log.i("Log_tag","swipedata is "+Constants.swipedata);
                searchquery(false);
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Constants.SEARCHSTRING = intent.getStringExtra(SearchManager.QUERY);
            Log.i("Log_tag", "home screen query is" + Constants.SEARCHSTRING);
            searchquery(true);
        }
    }

    //// Validate the edit text value
    public static Boolean search_validation() {

        Log.i("Log_tag", "search string" + Constants.SEARCHSTRING);
        if (Constants.SEARCHSTRING.length() > 0)
            return true;

        return false;
    }


    ////public get the fromdate
    public String GetFromDate() {
        String Getfromdate = fromdate.getText().toString().trim();
        if (Getfromdate.length() > 0) {
            return Getfromdate;
        }
        return "1";
    }

    public String getToDate() {

        String GetToDate = todate.getText().toString().trim();
        if (GetToDate.length() > 0)
            return GetToDate;
        return "1";
    }


    public void getdate() {

        FragmentManager fm = getFragmentManager();
        DialogFragment newFragment = new FilterByDate();
        newFragment.show(fm, "datePicker");
    }

    public void displaydate(String data) {

        Log.i("Log_tag", "data is" + data);
    }

    private void webSettings() {

        webview.setWebViewClient(new MyBrowser());
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // webview.getSettings().setBuiltInZoomControls(true);
        // webview.getSettings().setUseWideViewPort(true);
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
                DATEFLAG = 0;
                getdate();
                break;
            case R.id.todate:
                getdate();
                break;
            case R.id.bydatefilter:
                filterpanel.setVisibility(View.GONE);
                if (search_validation()) {
                    Constants.listdetails.clear();
                    searchquery(true);
                }
                break;
            case R.id.closebrowser:
                webview_lay.setVisibility(View.GONE);
                webview.destroy();
                HomeScreen.browserprogess.setProgress(100);
                ;

        }

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progress.setVisibility(View.GONE);
            HomeScreen.browserprogess.setProgress(100);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progress.setVisibility(View.VISIBLE);
            HomeScreen.browserprogess.setProgress(0);
            super.onPageStarted(view, url, favicon);
        }

    }


    public void searchquery(boolean procesing) {

        if (search_validation()) {
            if (procesing)
                progress.setVisibility(View.VISIBLE);

            buzztest.buzzdata(Constants.SEARCHSTRING, "1", GetFromDate(), getToDate());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      /*
        if (id == R.id.action_filter) {
            startActivity(new Intent(getApplication(), Filtering.class));
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

}
