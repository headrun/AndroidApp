package in.headrun.buzzinga.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;
import in.headrun.buzzinga.BuildConfig;
import in.headrun.buzzinga.BuzzingaApplication;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.adapters.DateSelection_AdapterView;
import in.headrun.buzzinga.adapters.SearchListDataAdapter;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.doto.SearchArticles;
import in.headrun.buzzinga.utils.Utils;

/**
 * Created by headrun on 7/7/15.
 */
public class HomeScreen extends Fragment
        implements View.OnClickListener,
        Utils.setOnItemClickListner,
        Utils.progressBarListner,
        Utils.setOnItemDateSelClickListner {

    public String TAG = HomeScreen.this.getClass().getSimpleName();

    @BindView(R.id.newarticle)
    TextView newarticle;
    @BindView(R.id.sel_date)
    TextView sel_date;

    @BindView(R.id.txt_info)
    TextView txt_info;

    @BindView(R.id.progressBar)
    ProgressBar progressbar;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.result_listview)
    RecyclerView display_data;
    @BindView(R.id.horizontal_recycler_view)
    RecyclerView horizontal_recycler_view;

    public String Intent_opt = "";

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    StringRequest serverRequest;

    SearchListDataAdapter searchAdapter;
    DateSelection_AdapterView dateSelAdapter;
    JSONObject jobj, jobj_result, jobj_hit;
    JSONArray jobj_hits;

    LinearLayoutManager mLinearLayout;
    boolean scroll_loading = true;
    public static boolean Swipe_loading = true;
    LinkedList<SearchArticles> searchArticleSwipe;

    // public static Parcelable state;

    public final int SEARCH = 1;
    public final int SCROLL = 2;

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            Intent_opt = bundle.getString(Constants.Intent_OPERATION);
        }
    }

    public HomeScreen() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //getWindow().requestFeature(Window.FEATURE_PROGRESS);
        View v = inflater.inflate(R.layout.homescreen, container, false);
        ButterKnife.bind(this, v);

        mLinearLayout = new LinearLayoutManager(getActivity());

        //hide he date range
        if (this.getResources().getBoolean(R.bool.show_date_range)) {
            sel_date.setVisibility(View.VISIBLE);
        } else {
            sel_date.setVisibility(View.GONE);
        }

        readBundle(getArguments());

        if (Constants.Intent_TRACK.equals(Intent_opt)) {
            Constants.SEARCHARTICLES.clear();
            Constants.state = null;
        }

        horizontal_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        dateSelAdapter = new DateSelection_AdapterView(getActivity(), Constants.DATE_SEL_LIST);
        horizontal_recycler_view.setAdapter(dateSelAdapter);
        dateSelAdapter.setClickListener(this);

        display_data.setHasFixedSize(true);
        display_data.setLayoutManager(mLinearLayout);
        searchAdapter = new SearchListDataAdapter(getActivity(), Constants.SEARCHARTICLES);
        display_data.setAdapter(searchAdapter);
        searchAdapter.setClickListener(this, this);

        newarticle.setOnClickListener(this);
        sel_date.setOnClickListener(this);

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

                    if (Utils.isNetwrokConnection(getActivity())) {

                        if (scroll_loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            if (swipeRefreshLayout.isRefreshing() == false && !Constants.scroolid.equals("1")) {

                                try {
                                    scroll_loading = false;
                                    Utils.add_query_data();

                                    Constants.SEARCHARTICLES.add(null);
                                    searchAdapter.notifyItemInserted(Constants.SEARCHARTICLES.size() - 1);

                                    servercall(SCROLL);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        network_error_snackbar();
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

                if (Utils.isNetwrokConnection(getActivity())) {
                    Swipe_loading = false;
                    Utils.add_query_data();

                    servercall(SEARCH);

                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    network_error_snackbar();
                }
            }
        });

        setSate();

        setDisplayDate();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onstart");

        Utils.add_query_data();
        if (Intent_opt.equals(Constants.Intent_TRACK))
            servercall(SEARCH);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onresume");
        Constants.state = display_data.getLayoutManager().onSaveInstanceState();
    }

    public void setSate() {
        if (Constants.state != null) {
            display_data.getLayoutManager().onRestoreInstanceState(Constants.state);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onpause");
        Intent_opt = Constants.Intent_NOTHING;
        //
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onstop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDest1ory");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /*case R.id.filtersource:
                // startActivity(new Intent(this, Filtering.class));
                break;

            case R.id.filterdate:
                //getdate();
                break;*/

            case R.id.newarticle:
                newarticle.setVisibility(View.GONE);
                Utils.add_query_data();
                servercall(SEARCH);
                break;

            case R.id.sel_date:
                if (horizontal_recycler_view.getVisibility() == View.VISIBLE)
                    horizontal_recycler_view.setVisibility(View.GONE);
                else
                    horizontal_recycler_view.setVisibility(View.VISIBLE);

        }
    }

    public void getServer_response(int req_type, String URL_request, String clubbedquery) {

        setDisplayDate();
        final int type_req = req_type;
        BuzzingaApplication.get().cancelRequestQueue(TAG);
        txt_info.setVisibility(View.GONE);

        final String clubbed_query = clubbedquery;

        if (Utils.isNetwrokConnection(getActivity())) {

            if (swipeRefreshLayout.isRefreshing() == true || scroll_loading == false)
                progressbar.setVisibility(View.GONE);
            else
                progressbar.setVisibility(View.VISIBLE);

            BuzzingaApplication.getUserSession().setSETUP(Constants.SETUP);
            BuzzingaApplication.getUserSession().setTIMEZONE(Utils.timezone());

            Log.i(TAG, "url is" + ServerConfig.SERVER_ENDPOINT + URL_request);
            serverRequest = new StringRequest(Request.Method.POST, ServerConfig.SERVER_ENDPOINT + URL_request,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                jobj = new JSONObject(response);
                                if (jobj.getString("error").equals("0")) {
                                    Utils.showLog(TAG, "resposne is " + "get data", Config.HOME_SCREEN);
                                    article_loading(jobj, type_req);
                                } else {
                                    Toast.makeText(getActivity(), "Buzzinga get an some error", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Utils.RedirectLoginPage(getContext());
                            }
                            hideProgressBar();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    error.printStackTrace();
                    if (BuildConfig.DEBUG == true)
                        if (error.networkResponse != null && error.networkResponse.data != null)
                            startActivity(new Intent(getActivity(), ResponseErrorActivity.class).
                                    putExtra("error", error.networkResponse.data.toString()));

                    if (error instanceof ServerError) {

                        new AlertDialog.Builder(getActivity()).setTitle("Buzzing Error Message").
                                setMessage("Serer error").
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                    } else if (error instanceof AuthFailureError && error.networkResponse.data != null) {

                    } else if (error instanceof TimeoutError) {

                        Log.i(TAG, "time out error");

                    }

                    if (swipeRefreshLayout.isRefreshing()) {
                        Swipe_loading = true;
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    progressbar.setVisibility(View.GONE);
                    removeScrollProgess();

                }
            }) {

                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    if (swipeRefreshLayout.isRefreshing() == true)
                        params.put("tz", BuzzingaApplication.getUserSession().getTIMEZONE());

                    params.put("clubbed_query", clubbed_query);
                    params.put("setup", BuzzingaApplication.getUserSession().getSETUP());

                    Utils.showLog(TAG, "params are " + params, Config.HOME_SCREEN);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    String sessionid = BuzzingaApplication.getUserSession().getTSESSION();
                    if (sessionid.length() > 0) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("sessionid");
                        builder.append("=");
                        builder.append(sessionid);
                        //builder.append("");
                        if (headers.containsKey("Cookie")) {
                            builder.append("; ");
                            builder.append(headers.get("Cookie"));
                        }
                        headers.put("Cookie", builder.toString());
                    }

                    Utils.showLog(TAG, "headers are " + headers, Config.HOME_SCREEN);
                    return headers;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    Log.i(TAG, "data " + new String(response.data));
                    return super.parseNetworkResponse(response);
                }

                @Override
                protected void deliverResponse(String response) {
                    super.deliverResponse(response);
                }

                @Override
                public String getUrl() {
                    Log.i(TAG, "url is " + super.getUrl());
                    return super.getUrl();
                }

                @Override
                public String getOriginUrl() {
                    Log.i(TAG, " original url is " + super.getOriginUrl());
                    return super.getOriginUrl();
                }


            };
            serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30 * 1000,
                    3,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            serverRequest.setTag(TAG);
            BuzzingaApplication.get().addToRequestQueue(serverRequest);

        } else {

            if (swipeRefreshLayout.isRefreshing()) {
                Swipe_loading = true;
                swipeRefreshLayout.setRefreshing(false);
            } else
                progressbar.setVisibility(View.GONE);
            scroll_loading = true;

            network_error_snackbar();
        }

    }

    public void article_loading(JSONObject response, int req_type) {

        removeScrollProgess();
        if (swipeRefreshLayout.isRefreshing() || SEARCH == req_type)
            Constants.SEARCHARTICLES.clear();

        getJsonData(response);

        Constants.state = display_data.getLayoutManager().onSaveInstanceState();
        // articleDetails();
        searchAdapter.notifyDataSetChanged();

        if (Constants.SEARCHARTICLES.size() > 0) {
            BuzzingaApplication.getUserSession().setLatestDate(Constants.SEARCHARTICLES.get(0).source.DATE_ADDED);
        }

        setSate();

        if (swipeRefreshLayout.isRefreshing() == true || Intent_opt.equals(Constants.Intent_NOTHING)) {
            Intent_opt = "";
        }

    }

    @Override
    public void itemClicked(View view, int position) {


        if (Utils.isNetwrokConnection(getActivity())) {
            Intent i = new Intent(getActivity(), ArticleDetialView.class);
            i.putExtra("pos", position);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
        } else {
            network_error_snackbar();
        }

    }

    @Override
    public void itemClicked(View view, int position, String article_type) {

        if (Utils.isNetwrokConnection(getActivity())) {
            if(article_type.contains(Constants.BLOGS)){
                Intent i = new Intent(getActivity(), ArticleDetialView.class);
                i.putExtra("pos", position);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
            }else{
                Intent i = new Intent(getActivity(), ArticleWebDisplay.class);
                i.putExtra("pos", position);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
            }

        } else {
            network_error_snackbar();
        }
    }

    public void getJsonData(JSONObject jobj) {

        try {

            //utils.showLog(TAG, "jobj is " + jobj.toString(), Config.HOME_SCREEN);

            if (jobj.getString("error").equals("0")) {

                jobj_result = new JSONObject(jobj.getString("result"));
                jobj_hit = new JSONObject(jobj_result.getString("hits"));
                jobj_hits = new JSONArray(jobj_hit.getString("hits"));
                Constants.scroolid = jobj_result.optString("_scroll_id");

                if (jobj_hits.length() > 0) {
                    // utils.showLog(TAG, "jobj_hits is " + jobj_hits.toString(), Config.HOME_SCREEN);

                    if (swipeRefreshLayout.isRefreshing() == true) {
                        searchArticleSwipe = new LinkedList<>();
                        addArticletoList(searchArticleSwipe, jobj_hits.toString());
                        Constants.SEARCHARTICLES.addAll(searchArticleSwipe);
                    } else {
                        addArticletoList(Constants.SEARCHARTICLES, jobj_hits.toString());
                    }

                } else {
                    Constants.scroolid = "1";

                }

                if (Constants.SEARCHARTICLES.size() <= 0) {
                    txt_info.setVisibility(View.VISIBLE);
                    Utils.warning_info(txt_info, Constants.NO_RECORD, getActivity());
                } else {
                    txt_info.setVisibility(View.GONE);
                }
            }
            Utils.showLog(TAG, "Articles size " + Constants.SEARCHARTICLES.size(), Config.HOME_SCREEN);

            //   articleDetails();
        } catch (JSONException e) {
            Log.i(TAG, "exception" + e);
            e.printStackTrace();

        }

    }

    public void addArticletoList(LinkedList<SearchArticles> list, String response) {

        list.addAll((LinkedList<SearchArticles>) new Gson().fromJson(response,
                new TypeToken<LinkedList<SearchArticles>>() {
                }.getType()));

    }

    public void articleDetails() {
        for (SearchArticles item : Constants.SEARCHARTICLES) {
            long seconds = Long.parseLong(item.source.DATE_ADDED);
            // Log.i(TAG, "article time is" + seconds);
            long millis = seconds * 1000;
            Date date = new Date(millis);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.getDefault());

            Utils.showLog(TAG, "title is" + item.source.TITLE +
                            "\nurl is \t" + item.source.URL +
                            "\nxtags is \t" + item.source.XTAGS.toString() +
                            "\ntime is\t" + sdf.format(date) +
                            "\nauthor is" + item.source.AUTHOR.NAME,
                    Config.SearchListDataAdapter);
        }
    }

    public void removeScrollProgess() {
        if (scroll_loading == false) {
            Constants.SEARCHARTICLES.remove(Constants.SEARCHARTICLES.size() - 1);
            searchAdapter.notifyItemRemoved(Constants.SEARCHARTICLES.size());
            scroll_loading = true;
        }
    }

    @UiThread
    public void servercall(int type) {
        if (SEARCH == type)
            getServer_response(type, ServerConfig.search, Utils.searchQuery(getActivity()));
        else if (SCROLL == type)
            getServer_response(type, ServerConfig.SCROLL, Utils.scrollQuery());
    }

    public void network_error_snackbar() {
        Snackbar.make(getActivity().findViewById(R.id.homescreen_lay), "No internet connection!", Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void date_sel_itemClicked(View view, int position) {

        // Toast.makeText(getActivity(), "sel date is" + Constants.DATE_SEL_LIST.get(position), Toast.LENGTH_SHORT).show();
        horizontal_recycler_view.setVisibility(View.GONE);

        String sel_item = Constants.DATE_SEL_LIST.get(position);
        Calendar today_cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        if (Constants.CUSTOM_RANGE.equals(sel_item.trim())) {
            Utils.getdate(getActivity());
        } else {

            String from_date = "", to_date = "";
            if (Constants.TO_DAY.equals(sel_item.trim())) {

                to_date = sdf.format(today_cal.getTime());
                from_date = sdf.format(today_cal.getTime());

            } else if (Constants.YESTER_DAY.equals(sel_item.trim())) {

                today_cal.add(Calendar.DAY_OF_MONTH, -1);

                to_date = sdf.format(today_cal.getTime());
                from_date = sdf.format(today_cal.getTime());


            } else if (Constants.WEEK.equals(sel_item.trim())) {


                //BuzzingaApplication.getUserSession().setFROM_DATE(sdf.format(today_cal.getTime()));

                to_date = sdf.format(today_cal.getTime());
                today_cal.add(Calendar.WEEK_OF_MONTH, -1);
                from_date = sdf.format(today_cal.getTime());
                /*BuzzingaApplication.getUserSession().setTO_DATE(sdf.format(today_cal.getTime()));
                servercall(SEARCH);
*/




                /*
            String from_date = sdf.format(today_cal.getTime());
            today_cal.add(Calendar.DATE, -7);
            String to_date = sdf.format(today_cal.getTime());

            utils.showLog(TAG, "sel date is" + Constants.DATE_SEL_LIST.get(position) + "" +
                    "form date is " + from_date + " to date is" + to_date, Config.HOME_SCREEN);
*/


            } else if (Constants.THIS_MONTH.equals(sel_item.trim())) {

                today_cal.set(Calendar.DATE, 1);
                from_date = sdf.format(today_cal.getTime());

                to_date = sdf.format(Calendar.getInstance().getTime());


            } else if (Constants.LAST_MONTH.equals(sel_item.trim())) {

                to_date = sdf.format(today_cal.getTime());
                today_cal.add(Calendar.DATE, -30);
                from_date = sdf.format(today_cal.getTime());

            }

            BuzzingaApplication.getUserSession().setFROM_DATE(from_date);
            BuzzingaApplication.getUserSession().setTO_DATE(to_date);

            Utils.add_query_data();
            servercall(SEARCH);

            // Toast.makeText(getActivity(), "sel date is" + Constants.DATE_SEL_LIST.get(position), Toast.LENGTH_SHORT).show();

            Utils.showLog(TAG, "sel date is" + Constants.DATE_SEL_LIST.get(position) + "" +
                    "form date is " + BuzzingaApplication.getUserSession().getFROM_DATE() + " to date is" + BuzzingaApplication.getUserSession().getFROM_DATE(), Config.HOME_SCREEN);
        }
    }

    public void setDisplayDate() {

        sel_date.setText(Utils.dispalyDateFormate(BuzzingaApplication.getUserSession().getFROM_DATE()) + " TO " +
                Utils.dispalyDateFormate(BuzzingaApplication.getUserSession().getTO_DATE()));
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

        if (swipeRefreshLayout.isRefreshing() == true) {
            Swipe_loading = true;
            swipeRefreshLayout.setRefreshing(false);
        }
        progressbar.setVisibility(View.GONE);

    }
}
