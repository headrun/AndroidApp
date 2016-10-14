package in.headrun.buzzinga.activities;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.BuildConfig;
import in.headrun.buzzinga.BuzzingaRequest;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.adapters.SearchListDataAdapter;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.doto.SearchArticles;
import in.headrun.buzzinga.utils.Utils;

/**
 * Created by headrun on 7/7/15.
 */
public class HomeScreen extends Fragment implements View.OnClickListener, Utils.setOnItemClickListner {

    public String TAG = HomeScreen.this.getClass().getSimpleName();

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
    public String Intent_opt = "";

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    StringRequest serverRequest;

    SearchListDataAdapter searchAdapter;
    JSONObject jobj, jobj_result, jobj_hit;
    JSONArray jobj_hits;

    LinearLayoutManager mLinearLayout;
    boolean scroll_loading = true;
    public static boolean Swipe_loading = true;
    ArrayList<SearchArticles> searchArticleSwipe;

    public static Parcelable state;

    public final int SEARCH = 1;
    public final int SCROLL = 2;

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            Intent_opt = bundle.getString(Constants.Intent_OPERATION);
            state = null;
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

        utils = new Utils(getActivity());
        mLinearLayout = new LinearLayoutManager(getActivity());

        readBundle(getArguments());

        if (Constants.Intent_TRACK.equals(Intent_opt))
            Constants.SEARCHARTICLES.clear();


        display_data.setHasFixedSize(true);
        display_data.setLayoutManager(mLinearLayout);
        searchAdapter = new SearchListDataAdapter(getActivity(), Constants.SEARCHARTICLES);
        display_data.setAdapter(searchAdapter);
        searchAdapter.setClickListener(this);


        newarticle.setOnClickListener(this);


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

                    if (utils.isNetwrokConnection()) {

                        if (scroll_loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            if (swipeRefreshLayout.isRefreshing() == false && !Constants.scroolid.equals("1")) {

                                scroll_loading = false;
                                utils.add_query_data();

                                Constants.SEARCHARTICLES.add(null);
                                searchAdapter.notifyItemInserted(Constants.SEARCHARTICLES.size() - 1);

                                servercall(SCROLL);
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

                if (utils.isNetwrokConnection()) {
                    Swipe_loading = false;
                    utils.add_query_data();


                    servercall(SEARCH);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    network_error_snackbar();
                }
            }
        });


        setSate();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onstart");

        utils.add_query_data();
        if (Intent_opt.equals(Constants.Intent_TRACK))
            servercall(SEARCH);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onresume");
        state = display_data.getLayoutManager().onSaveInstanceState();
    }

    public void setSate() {
        if (state != null) {
            display_data.getLayoutManager().onRestoreInstanceState(state);
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
        Log.i(TAG, "onDestory");
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
                utils.add_query_data();
                servercall(SEARCH);
                break;
        }
    }

    public void getServer_response(String URL_request, String clubbedquery) {

        BuzzingaRequest.getInstance(getActivity()).cancelRequestQueue(TAG);
        txt_info.setVisibility(View.GONE);
        final String clubbed_query = clubbedquery;

        if (utils.isNetwrokConnection()) {

            if (swipeRefreshLayout.isRefreshing() == true || scroll_loading == false)
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
                    } else
                        progressbar.setVisibility(View.GONE);
                    removeScrollProgess();

                }
            }) {

                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    if (swipeRefreshLayout.isRefreshing() == true)
                        params.put("tz", utils.userSession.getTIMEZONE());

                    params.put("clubbed_query", clubbed_query);
                    params.put("setup", utils.userSession.getSETUP());

                    utils.showLog(TAG, "params are " + params, Config.HOME_SCREEN);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    String sessionid = utils.userSession.getTSESSION();
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
            BuzzingaRequest.getInstance(getActivity()).addToRequestQueue(serverRequest);

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

    public void article_loading(String response) {

        removeScrollProgess();
        if (swipeRefreshLayout.isRefreshing())
            Constants.SEARCHARTICLES.clear();

        getJsonData(response);

        state = display_data.getLayoutManager().onSaveInstanceState();
        // articleDetails();
        searchAdapter.notifyDataSetChanged();
        if (Constants.SEARCHARTICLES.size() > 0) {
            utils.userSession.setLatestDate(Constants.SEARCHARTICLES.get(0).source.DATE_ADDED);
        }

        setSate();

        if (swipeRefreshLayout.isRefreshing() == true || Intent_opt.equals(Constants.Intent_NOTHING)) {
            Intent_opt = "";
        }

    }

    @Override
    public void itemClicked(View view, int position) {


        if (utils.isNetwrokConnection()) {
            Intent i = new Intent(getActivity(), ArticleWebDisplay.class);
            i.putExtra("pos", position);

            startActivity(i);
            getActivity().overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
        } else {
            network_error_snackbar();
        }

    }

    public void getJsonData(String data) {

        try {

            jobj = new JSONObject(data);
            utils.showLog(TAG, "jobj is " + jobj.toString(), Config.HOME_SCREEN);

            if (jobj.getString("error").equals("0")) {

                jobj_result = new JSONObject(jobj.getString("result"));
                jobj_hit = new JSONObject(jobj_result.getString("hits"));
                jobj_hits = new JSONArray(jobj_hit.getString("hits"));
                Constants.scroolid = jobj_result.optString("_scroll_id");

                if (jobj_hits.length() > 0) {
                    utils.showLog(TAG, "jobj_hits is " + jobj_hits.toString(), Config.HOME_SCREEN);

                    if (swipeRefreshLayout.isRefreshing() == true) {
                        searchArticleSwipe = new ArrayList<SearchArticles>();
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
                    utils.warning_info(txt_info, Constants.NO_RECORD);
                } else {
                    txt_info.setVisibility(View.GONE);
                }
            }
            utils.showLog(TAG, "Articles size " + Constants.SEARCHARTICLES.size(), Config.HOME_SCREEN);

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

            utils.showLog(TAG, "title is" + item.source.TITLE +
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
            getServer_response(ServerConfig.search, utils.searchQuery());
        else if (SCROLL == type)
            getServer_response(ServerConfig.SCROLL, utils.scrollQuery());
    }

    public void network_error_snackbar() {
        Snackbar.make(getActivity().findViewById(R.id.homescreen_lay), "No internet connection!", Snackbar.LENGTH_SHORT)
                .show();
    }
}
