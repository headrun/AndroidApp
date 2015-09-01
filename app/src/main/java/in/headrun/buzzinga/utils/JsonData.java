package in.headrun.buzzinga.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import in.headrun.buzzinga.activities.HomeScreen;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.doto.SearchDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class JsonData {

    Context context;
    public ArrayList<SearchDetails> swipelist;
    String title, url, text, date, author, sentiment, gender, article_type;
    String TAG = JsonData.this.getClass().getSimpleName();

    // File fileWithinMyDir;

    public JsonData(Context context, String data) {
        this.context = context;
        logLargeString(data);
        swipelist = new ArrayList<SearchDetails>();
        SearchListData resultadapter;
        int searchpos = 0;

        try {
            JSONObject jobj = new JSONObject(data);
            if (jobj.getString("error").equals("0")) {

                JSONObject jobj_result = new JSONObject(jobj.getString("result"));
                JSONObject jobj_hit = new JSONObject(jobj_result.getString("hits"));
                JSONArray jobj_hits = new JSONArray(jobj_hit.getString("hits"));
                Log.i("Log_tag", "json result length " + jobj_hits.length());
                if (jobj_hits.length() > 0) {
                    for (int hits = 0; hits < jobj_hits.length(); hits++) {
                        JSONObject jobj_data = jobj_hits.getJSONObject(hits);
                        JSONObject jobj_source = new JSONObject(jobj_data.getString("_source"));

                        title = jobj_source.getString("title");
                        url = jobj_source.getString("url");
                        text = jobj_source.optString("text");
                        date = jobj_source.getString("_added");
                        JSONArray json_xtags = jobj_source.getJSONArray("xtags");
                        // author = jobj_source.getJSONObject("author").optString("name","null");
                        //Log.i(TAG,"author is"+author);
                        JSONObject json_author = jobj_source.optJSONObject("author");


                        if (json_author != null)
                           author= json_author.optString("name");


                        xtags_separate(json_xtags);

                        Log.i("Log_tag", "data is \ntitle" + title + "\nurl" + url + "\ntext" + text + "\ndate" + date + "\nauthor" + author + "\nsentiment" + sentiment + "\narticle_type" + article_type + "gender" + gender);

                        Log.i(TAG,"in jason Constants.swipedata"+Constants.swipedata);
                        if (Constants.swipedata) {
                            Log.i(TAG,"swipe data added");
                            swipelist.add(new SearchDetails(title, url, text, date, author, sentiment, article_type));

                        } else {

                            Constants.listdetails.add(new SearchDetails(title, url, text, date, author, sentiment, article_type));
                        }
                    }
                    Constants.scroolid = jobj_result.getString("_scroll_id");
                } else {
                    Constants.scroolid = "1";
                }
                if (!swipelist.isEmpty()) {

                    for (int i = 0; i < swipelist.size(); i++) {
                        Log.i("Log_ag", "swipe new url is" + swipelist.get(i).getUrl() + "\n url is" + Constants.listdetails.get(0).getUrl());
                        if (swipelist.get(i).getUrl().contains(Constants.listdetails.get(0).getUrl())) {
                            searchpos = i;
                            Constants.finddata = true;
                            Constants.swipedata = false;
                            Log.i("Log_tag", "search pos is" + searchpos);
                        }
                    }
                    if (Constants.finddata)
                        for (int j = 0; j <= searchpos; j++) {
                            Constants.listdetails.add(j, swipelist.get(j));
                        }

                }
                //checkdata();

                resultadapter = new SearchListData(context, Constants.listdetails);
                resultadapter.notifyDataSetChanged();
                HomeScreen.display_data.setAdapter(resultadapter);
                HomeScreen.content_lay.setVisibility(View.VISIBLE);
                HomeScreen.progress.setVisibility(View.GONE);
                HomeScreen.display_data.removeFooterView(HomeScreen.footerView);
              //  HomeScreen. footerView.setVisibility(View.GONE);
                Config.SwipeLoading = false;
                // Log.i("Log_tag", "data fetched");

            } else {
                Constants.scroolid = "1";
                Constants.listdetails.add(new SearchDetails());
                resultadapter = new SearchListData(context, Constants.listdetails);
                resultadapter.notifyDataSetChanged();
                HomeScreen.display_data.setAdapter(resultadapter);
                HomeScreen.content_lay.setVisibility(View.VISIBLE);
                HomeScreen.progress.setVisibility(View.GONE);
                Config.SwipeLoading = false;
            }
            HomeScreen.swipeRefreshLayout.setRefreshing(false);
        } catch (JSONException e) {
            Log.i(TAG, "exception" + e);
            e.printStackTrace();
        }

    }

    public void logLargeString(String str) {

        if (str.length() > 3000) {
            String ss = str.substring(0, 3000);
            Log.i("", ss);
            logLargeString(str.substring(3000));
        } else
            Log.i("", str);
    }

    private void xtags_separate(JSONArray xtags) {

        Log.i(TAG, "source list size is" + Constants.sourceslist.size() +
                "sentiment list size is" + Constants.sentimentlist.size() +
                "gender list size is" + Constants.genderlist.size());
        for (int i = 0; i < xtags.length(); i++) {

            try {
                Log.i(TAG, "source compared" + xtags.get(i).toString());
                if (Constants.sourceslist.contains(xtags.get(i).toString())) {
                    Log.i(TAG, " matched" + xtags.get(i).toString());
                    article_type = xtags.get(i).toString();
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        for (int i = 0; i < xtags.length(); i++) {

            try {
                Log.i(TAG, "senment" + xtags.get(i).toString());
                if (Constants.sentimentlist.contains(xtags.get(i).toString())) {
                    Log.i(TAG, " matched" + xtags.get(i).toString());
                    sentiment = xtags.get(i).toString();
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        for (int i = 0; i < xtags.length(); i++) {

            try {
                Log.i(TAG, "gender" + xtags.get(i).toString());
                if (Constants.genderlist.contains(xtags.get(i).toString())) {
                    Log.i(TAG, " matched" + xtags.get(i).toString());
                    gender = xtags.get(i).toString();
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
