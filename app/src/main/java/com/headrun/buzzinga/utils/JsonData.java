package com.headrun.buzzinga.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.headrun.buzzinga.activities.HomeScreen;
import com.headrun.buzzinga.config.Config;
import com.headrun.buzzinga.config.Constants;
import com.headrun.buzzinga.doto.SearchDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by headrun on 10/7/15.
 */
public class JsonData {
    Context context;
   

    // File fileWithinMyDir;
    public JsonData(Context context, String data) {
        this.context = context;
        logLargeString(data);

        SearchListData resultadapter;

        try {
            JSONObject jobj = new JSONObject(data);
            if (jobj.getString("error").equals("0")) {

                JSONObject jobj_result = new JSONObject(jobj.getString("result"));
                JSONObject jobj_hit = new JSONObject(jobj_result.getString("hits"));
                JSONArray jobj_hits = new JSONArray(jobj_hit.getString("hits"));
                for (int hits = 0; hits < jobj_hits.length(); hits++) {
                    JSONObject jobj_data = jobj_hits.getJSONObject(hits);
                    JSONObject jobj_source = new JSONObject(jobj_data.getString("_source"));

                    String title = jobj_source.getString("title");
                    String url = jobj_source.getString("url");
                   // Log.i("Log_tag", "url is" + url);
                    String text = jobj_source.getString("text");
                    String date = jobj_source.getString("_added");
                    //String author = jobj_source.getJSONObject("author").getString("name");
                    String author = "1";
                    // Log.i("Log_tag","author is"+author);
                    Constants.listdetails.add(new SearchDetails(title, url, text, date, author));
                }

                checkdata();
                Constants.scroolid = jobj_result.getString("_scroll_id");

                resultadapter = new SearchListData(context, Constants.listdetails);
                HomeScreen.display_data.setAdapter(resultadapter);
                resultadapter.notifyDataSetChanged();
                HomeScreen.display_data.setVisibility(View.VISIBLE);
                HomeScreen.progress.setVisibility(View.GONE);
                Config.isLoading = false;
             //   Log.i("Log_tag", "data fetched");

            } else {
                Constants.listdetails.add(new SearchDetails());
                resultadapter = new SearchListData(context, Constants.listdetails);
                resultadapter.notifyDataSetChanged();
                HomeScreen.display_data.setAdapter(resultadapter);
                HomeScreen.display_data.setVisibility(View.VISIBLE);
                HomeScreen.progress.setVisibility(View.GONE);
                Config.isLoading = false;
            }
            HomeScreen.swipeRefreshLayout.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void logLargeString(String str) {

        if (str.length() > 1000) {
            String ss = str.substring(0, 1000);
            Log.i("", ss);
            logLargeString(str.substring(1000));
        } else
            Log.i("", str);
    }

    public void checkdata() {
        if (Constants.FIRSTURL.equals(Constants.listdetails.get(0).getUrl())) {
            Constants.FIRSTURL = Constants.listdetails.get(0).getUrl();

        } else {
            Constants.FIRSTURL = Constants.listdetails.get(0).getUrl();


        }

    }
}




