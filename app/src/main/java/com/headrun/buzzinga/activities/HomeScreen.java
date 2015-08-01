package com.headrun.buzzinga.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.headrun.buzzinga.R;
import com.headrun.buzzinga.UserSession;
import com.headrun.buzzinga.config.Config;
import com.headrun.buzzinga.config.Constants;
import com.headrun.buzzinga.doto.Listitems;
import com.headrun.buzzinga.doto.SearchDetails;
import com.headrun.buzzinga.doto.Test;
import com.headrun.buzzinga.utils.FilterByDate;
import com.headrun.buzzinga.utils.ListViewAdapter;
import com.headrun.buzzinga.utils.SearchListData;

import java.util.ArrayList;


/**
 * Created by headrun on 7/7/15.
 */
public class HomeScreen extends Activity implements CompoundButton.OnCheckedChangeListener {

    public String TAG = HomeScreen.this.getClass().getSimpleName();

    public ListView listview;                                ////  Listview display yhe source list
    public static ListView display_data;                     ////  Display the results
    public View listview_lay, filterpanel, webview_lay;                                ///// Layout of menu button.
    public EditText textsearch;                              ////  Search the text
    public static TextView fromdate, todate;
    public static TextView browsertitle;
    public WebView webview;
    public static SwipeRefreshLayout swipeRefreshLayout;

    Test buzztest;                                           ////  Call  the Test class
    UserSession userSession;
    SearchListData search_adapter;
    public static ProgressBar progress, browserprogess;                      ////  progress bar after clicking the checkbox
    public static String searchstring;                       ////  Assign edite text value
    public ArrayList<Listitems> sources;                     ////  Added the sources in linst view
    public ImageView menu_source, imageView;                 ////  Image view for icon and  menu
    ListViewAdapter adapter;                                 //// Calling Listview Adapter class
    public static String filterdate;
    public static int DATEFLAG;
    public static boolean flag_loading;

    public ArrayList<String> slistmitem = new ArrayList<String>();  //// Selected sources are added to list
    public ArrayList<String> sourcesel = new ArrayList<String>();   //// xtags are added
    public static StringBuilder sourcequery = new StringBuilder();  //// from the Elstics search query


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        if (Config.HOME_SCREEN)
            Log.i(TAG, "HOME SCREEN");
        userSession = new UserSession(HomeScreen.this);

        listview = (ListView) findViewById(R.id.listView);
        // listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        display_data = (ListView) findViewById(R.id.result_listview);
        menu_source = (ImageView) findViewById(R.id.menu_source);
        imageView = (ImageView) findViewById(R.id.imageView);
        webview = (WebView) findViewById(R.id.webview);
        listview_lay = findViewById(R.id.list_layout);
        filterpanel = findViewById(R.id.filterpanel);
        webview_lay = findViewById(R.id.webview_lay);
        fromdate = (TextView) findViewById(R.id.fromdate);
        todate = (TextView) findViewById(R.id.todate);
        browsertitle = (TextView) findViewById(R.id.browsertitle);
        textsearch = (EditText) findViewById(R.id.searchtext);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        browserprogess = (ProgressBar) findViewById(R.id.browserprogress);
        browserprogess.setVisibility(View.GONE);
        buzztest = new Test(getApplication());
        search_adapter = new SearchListData(HomeScreen.this, 1);
        listview_lay.setVisibility(View.GONE);
        filterpanel.setVisibility(View.GONE);
        webview_lay.setVisibility(View.GONE);
        webSettings();
        storedata();
        textsearch.requestFocus();

        textsearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {

                    // Perform action on key press hide the Enter key
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    searchquery(true);
                    return true;
                }
                return false;
            }
        });

        ////  Hide the Menu
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listview_lay.setVisibility(View.GONE);
            }
        });

        //// Display the Menu
        menu_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Config.HOME_SCREEN)
                    Log.i(TAG, "DISPALY THE LISTVIEW");
                listview_lay.setVisibility(View.VISIBLE);

            }
        });

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
                if (lastIndexInScreen >= totalItemCount && !Config.isLoading) {
                    Config.isLoading = true;
                    searchscroolquery();
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
                searchquery(false);
            }
        });

    }

    ///// Add the source to listview
    public void storedata() {

        sources = new ArrayList<Listitems>();
        sources.add(new Listitems("Facebook"));
        sources.add(new Listitems("Twitter"));
        sources.add(new Listitems("Google+"));
        sources.add(new Listitems("News"));
        sources.add(new Listitems("Blogs"));
        sources.add(new Listitems("Forums"));
        sources.add(new Listitems("YouTube"));
        sources.add(new Listitems("Flickr"));
        sources.add(new Listitems("Instagram"));
        sources.add(new Listitems("Tumblr"));
        sources.add(new Listitems("Linkedin"));
        sources.add(new Listitems("Quora"));


        adapter = new ListViewAdapter(HomeScreen.this, sources);
        listview.setAdapter(adapter);


    }

    //// Check box cllick menthod getting  the selected items
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        listview_lay.setVisibility(View.GONE);

        int pos = listview.getPositionForView(compoundButton);
        if (pos != ListView.INVALID_POSITION) {
            Listitems selectitem = sources.get(pos);
            selectitem.setSelectd(b);

        }
        searchquery(true);
    }

    //// Validate the edit text value
    private Boolean search_validation() {
        searchstring = textsearch.getText().toString().trim();
        displaydate("search string" + searchstring);
        if (searchstring.length() > 0)
            return true;
        else
            Toast.makeText(HomeScreen.this, getResources().getString(R.string.missongsearch), Toast.LENGTH_LONG).show();

        return false;
    }

    public String selectsource() {

        slistmitem.clear();                                     ////clear the Arraylist
        for (Listitems checked : sources)
            if (checked.isSelectd()) {
                displaydate("selected source is" + checked.getSourcename());
                slistmitem.add(checked.getSourcename());
            }

        sourcesel.clear();                                     //// clear the array list

        String prefix = "";
        sourcequery.setLength(0);

        if (!slistmitem.isEmpty()) {
            for (int i = 0; i < slistmitem.size(); i++) {
                String s_item = slistmitem.get(i).toLowerCase();
                Log.i("Log_tag", "source sel len is" + slistmitem.size() + "string is" + s_item);
                if (s_item.toLowerCase().contains("facebook"))
                    s_item = "fb";
                if (s_item.toLowerCase().contains("google+"))
                    s_item = "google";

                String[] sourcelist = getResources().getStringArray(R.array.sourecselection);
                if (search_validation()) {
                    for (String source_list : sourcelist) {
                        Log.i("Log_tag", "source is" + s_item + "xtag is" + source_list);
                        if (source_list.toLowerCase().contains(s_item)) {
                            Log.i("Log_tag", "match" + searchstring);
                            sourcesel.add(source_list);
                            sourcequery.append(prefix);
                            prefix = " OR ";
                            sourcequery.append("xtags:" + source_list);
                        }
                    }
                    return sourcequery.toString();
                }
            }


        }
        return "1";
    }

    ////filter by date method
    public void filterBydate(View v) {
        Log.i(TAG, "filterBydate method");
        filterpanel.setVisibility(View.VISIBLE);


    }

    //// fromdate edittext onclick method
    public void setFromDate(View v) {
        DATEFLAG = 0;
        getdate();
        displaydate("fromdate");

    }

    //// todate edittext onclick method
    public void setToDate(View v) {
        getdate();
        displaydate("to date");

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


    //// date search by date  filter button
    public void bydatefilter(View v) {

        filterpanel.setVisibility(View.GONE);
        if (search_validation())
            searchquery(true);
    }

    public void getdate() {

        FragmentManager fm = getFragmentManager();
        DialogFragment newFragment = new FilterByDate();
        newFragment.show(fm, "datePicker");
    }

    public void displaydate(String data) {

        Log.i("Log_tag", "data is" + data);
    }

    ////close the date filter pannel
    public void close_filterpannel(View v) {

        filterpanel.setVisibility(View.GONE);
    }

    private void webSettings() {

        webview.setWebViewClient(new MyBrowser());
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setUseWideViewPort(true);
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

    public void setValue(int progress) {
        this.progress.setProgress(progress);
    }

    public void closeBrowser(View v) {
        webview_lay.setVisibility(View.GONE);
        HomeScreen.browserprogess.setVisibility(View.GONE);
    }

    public void searchquery(boolean procesing) {

        if (search_validation()) {
            if (procesing)
                progress.setVisibility(View.VISIBLE);

            buzztest.buzzdata(searchstring, selectsource(), GetFromDate(), getToDate());
        }
    }

    public void searchscroolquery() {
        if (!Constants.scroolid.equals("1"))
            Log.i(TAG, "calling the scool query");
        buzztest.buzzdata(Constants.scroolid);
    }

}
