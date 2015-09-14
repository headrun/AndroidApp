package in.headrun.buzzinga.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.doto.SearchDetails;


public class JsonData {


    public ArrayList<SearchDetails> swipelist;
    String title, url, text, date, author, sentiment, gender, article_type;
    String TAG = JsonData.this.getClass().getSimpleName();


    public ArrayList<SearchDetails> getJsonData(String data) {

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
                        Log.i(TAG,"url is"+url);
                        text = jobj_source.optString("text");
                        date = jobj_source.getString("_added");
                        JSONArray json_xtags = jobj_source.getJSONArray("xtags");
                        JSONObject json_author = jobj_source.optJSONObject("author");
                        if (json_author != null)
                            author = json_author.optString("name");

                        xtags_separate(json_xtags);

                        if (Constants.swipedata) {
                           // Log.i(TAG, "swipe data added");
                            swipelist.add(new SearchDetails(title, url, text, date, author, sentiment, article_type));

                        } else {
                            Constants.listdetails.add(new SearchDetails(title, url, text, date, author, sentiment, article_type));
                        }
                    }
                    Constants.scroolid = jobj_result.getString("_scroll_id");

                    //Log.i(TAG, "scroolid is" + Constants.scroolid);
                    if (!swipelist.isEmpty()) {
                        if (Constants.listdetails.size() > 0) {
                            for (int i = 0; i < swipelist.size(); i++) {

                                if (swipelist.get(i).getUrl().contains(Constants.listdetails.get(0).getUrl())) {
                                    Constants.newarticles = i;
                                    Constants.finddata = true;
                                    Constants.swipedata = false;
                                    Log.i("Log_tag", "search pos is" + Constants.newarticles);
                                }
                            }

                            if (Constants.finddata)
                                for (int j = 0; j <= Constants.newarticles; j++) {
                                    Constants.listdetails.add(j, swipelist.get(j));
                                }

                        } else {
                            Constants.listdetails = swipelist;
                        }

                    }

                } else {

                    Constants.scroolid = "1";
                }
                Log.i(TAG, "Constants.listdetails size is" + Constants.listdetails.size());

                return Constants.listdetails;
               /*
                resultadapter = new SearchListData(context, Constants.listdetails);
                resultadapter.notifyDataSetChanged();
                HomeScreen.display_data.setAdapter(resultadapter);
                HomeScreen.content_lay.setVisibility(View.VISIBLE);
                HomeScreen.progress.setVisibility(View.GONE);
                HomeScreen.display_data.removeFooterView(HomeScreen.footerView);
                Config.SwipeLoading = false;

*/
            } else {
                return Constants.listdetails;
  /*              Constants.scroolid = "1";
                Constants.listdetails.add(new SearchDetails());
                resultadapter = new SearchListData(context, Constants.listdetails);
                resultadapter.notifyDataSetChanged();
                HomeScreen.display_data.setAdapter(resultadapter);
                HomeScreen.content_lay.setVisibility(View.VISIBLE);
                HomeScreen.progress.setVisibility(View.GONE);
                Config.SwipeLoading = false;
    */
            }
            //      HomeScreen.swipeRefreshLayout.setRefreshing(false);
        } catch (JSONException e) {
            Log.i(TAG, "exception" + e);
            e.printStackTrace();
        }
        return Constants.listdetails;
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

        if (xtags.length() > 0) {
            Set<String> source_keys = Constants.source_map.keySet();
            for (String key : source_keys) {
                if (xtags.toString().contains(key)) {
                    article_type = key;
                    break;
                }
                if (xtags.toString().contains("fb"))
                    article_type = Constants.FACEBOOK;
            }

            Set<String> sentiment_keys = Constants.sentiment_map.keySet();
            for (String sentiment_key : sentiment_keys) {
                if (xtags.toString().contains(sentiment_key)) {
                    sentiment = sentiment_key;
                    break;
                }
            }
            Set<String> gender_keys = Constants.gender_map.keySet();
            for (String gender_key : gender_keys) {
                if (xtags.toString().contains(gender_key)) {
                    gender = gender_key;
                    break;
                }


            }

        }
    }
}
