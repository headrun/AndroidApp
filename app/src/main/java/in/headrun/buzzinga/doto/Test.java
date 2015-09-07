package in.headrun.buzzinga.doto;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import in.headrun.buzzinga.BuzzingaApplication;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.activities.HomeScreen;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.utils.ConnectionSettings;

public class Test {

    String TAG = Test.this.getClass().getSimpleName();
    static Context context;
    public static Gson gson;
    public static String rsstype = " AND xtags:rss_sourcetype_manual";
    public static String userQuery = null;
    public String which_funn = null;

    public String setup;

    public String que;

    public Test(Context context) {
        this.context = context;

    }

    public void buzzdata(String searchstring, String sources, String fromdate, String todate) {
        setup = setupdate(check_fromdate(fromdate), check_todate(todate));

        que = "(" + searchstring + ")" + check_searchsources(sources) + " AND dt_added:[" + check_fromdate(fromdate) + " TO " + check_todate(todate) + "]";
        userQuery = queryform(que);
        which_funn = "query";
        callserver(check_scrollid("1"), userQuery, setup, which_funn);
    }

    public void buzzdata(String searchstring, String sources, String gender, String sentiment, String fromdate, String todate, String Loc, String Lang) {
        setup = setupdate(check_fromdate(fromdate), check_todate(todate));

        que = "(" + searchstring + ")" + check_searchsources(sources) + check_gender(gender) + check_sentiment(sentiment) + check_loc(Loc) + check_lang(Lang) + " AND dt_added:[" + check_fromdate(fromdate) + " TO " + check_todate(todate) + "]";
        userQuery = queryform(que);
        /*userQuery = "{'query': {'query_string': {'query':'" + searchstring + check_searchsources(sources) + check_gender(gender) + check_sentiment(sentiment) + check_loc(Loc) + check_lang(Lang) + " AND dt_added:[" + check_fromdate(fromdate) + " TO " + check_todate(todate) + "]', 'fields':['title', 'text'],  'use_dis_max':true}}, 'sort':[{'dt_added':{'order':'desc'}}], 'size':10}";
        */
        which_funn = "query";
        Log.i("query is", userQuery);

        callserver(check_scrollid("1"), userQuery, setup, which_funn);
    }

    public void buzzdata(String searchstring, String sources, String gender, String sentiment, String fromdate, String todate, String Loc, String Lang, String scrool_id) {
        setup = setupdate(check_fromdate(fromdate), check_todate(todate));


        que = "(" + searchstring + ")" + check_searchsources(sources) + check_gender(gender) + check_sentiment(sentiment) + check_loc(Loc) + check_lang(Lang) + " AND dt_added:[" + check_fromdate(fromdate) + " TO " + check_todate(todate) + "]";
        userQuery = queryform(que);

        which_funn = "query";
        Log.i("query is", userQuery);

        callserver(check_scrollid(scrool_id), userQuery, setup, which_funn);
    }


    private void callserver(String scrool_id, String query, String setup, String which_funn) {

        if (ConnectionSettings.isConnected(context)) {

            new getresponce().execute(scrool_id, query, setup, which_funn);

        } else {
            Log.i("Log_tag", "network failed");
        }
    }


    public static Object parseJson(String data) {
        return gson.fromJson(data, Object.class);
    }


    private String toCurrentDate() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE);

    }

    private String fromCurrentDate() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MONTH, -1);
        return now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE);


    }

    private void querydisplay(String query) {
        Log.i("Log_tag", "query is" + query);
    }

    private String check_searchstring(String searchstring) {
        if (!searchstring.equals("1"))
            return searchstring;
        return "";

    }

    private String check_searchsources(String searchsources) {
        if (!searchsources.equals("1"))
            return " AND (" + searchsources + ")";
        return "";
    }

    private String check_fromdate(String fromdate) {

        if (!fromdate.equals("1"))
            return fromdate;
        return fromCurrentDate();
    }

    private String check_todate(String todate) {
        if (!todate.equals("1"))
            return todate;
        return toCurrentDate();
    }

    private String check_gender(String gender) {
        if (!gender.equals("1"))
            return " AND (" + gender + ")";
        return "";

    }

    private String check_sentiment(String sentiment) {
        if (!sentiment.equals("1"))
            return " AND (" + sentiment + ")";
        return "";
    }


    private String check_loc(String loc) {
        if (!loc.equals("1"))
            return " AND (" + loc + ")";
        return "";
    }


    private String check_lang(String lang) {
        if (!lang.equals("1"))
            return " AND (" + lang + ")";
        return "";
    }


    private String check_scrollid(String scrollid) {
        if (!scrollid.equals("1"))
            return scrollid;
        return "";
    }

    public class getresponce extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "scrool id is" + Constants.scroolid);
            if (Constants.scroolid.equals("1")) {
                HomeScreen.progress.setVisibility(View.VISIBLE);
                Log.i(TAG, "scrool is" + Constants.scroolid.equals("1"));
            }
        }

        @Override
        protected String doInBackground(String... params) {

            final String scrool_id = params[0];
            final String clubbed_query = params[1];
            final String setup = params[2];
            final String Tzone = timezone();

            Log.i(TAG, "\n" + params[0] + "\n type is" + setup + "\ntime zone is" + Tzone + "scroll id is" + scrool_id);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerConfig.SERVER_ENDPOINT + ServerConfig.search,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d(TAG, "string response is" + response);

                            //new JsonData(context, response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "string error response is" + error.getMessage() + "\nmessage is" + error.getMessage());
                }
            }) {

                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    if (!scrool_id.equals("1"))
                        params.put("scroll_id", scrool_id);
                    else
                        params.put("tz", Tzone);
                    params.put("clubbed_query", clubbed_query);
                    params.put("setup", setup);

                    return params;
                }

                ;

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    String sessionid = new UserSession(context).getTSESSION();
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
            BuzzingaApplication.get().getRequestQueue().add(stringRequest);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }

    public String timezone() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        Log.i(TAG, "time zone is" + mTimeZone + "\n offeset is" + mGMTOffset);
        System.out.printf("GMT offset is %s hours", TimeUnit.MINUTES.convert(mGMTOffset, TimeUnit.MILLISECONDS));
        long tz = TimeUnit.MINUTES.convert(mGMTOffset, TimeUnit.MILLISECONDS);

        if (tz > 0)
            return String.valueOf(-tz);
        else
            return String.valueOf(Math.abs(tz));
    }

    public String setupdate(String fromdate, String todate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date FromDate = new Date();
        Date ToDate = new Date();
        try {
            FromDate = dateFormat.parse(fromdate);
            ToDate = dateFormat.parse(todate);
            long days = (ToDate.getTime() - FromDate.getTime()) / (24 * 60 * 60 * 1000);
            Log.i(TAG, "days is" + days);
            if (days > 30)
                return "main";
            else
                return "mini";

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "mini";
    }

    public String queryform(String query_data) {


        JSONObject main = new JSONObject();
        JSONObject mainquery = new JSONObject();

        JSONObject subquery = new JSONObject();
        JSONObject dataquery = new JSONObject();
        JSONObject query_string = new JSONObject();
        JSONObject order = new JSONObject();
        JSONObject dt_added = new JSONObject();
        JSONObject text = new JSONObject();
        JSONObject title = new JSONObject();
        JSONObject fields = new JSONObject();
        JSONObject fieldsobj = new JSONObject();
        JSONObject scroll = new JSONObject();
        JSONArray fieldsarray = new JSONArray();
        JSONArray sortarray = new JSONArray();
        JSONArray indexes_array = new JSONArray();

        try {
            dataquery.put("query", que);
            dataquery.put("use_dis_max", true);
            fieldsarray.put("title");
            fieldsarray.put("text");
            dataquery.put("fields", fieldsarray);

            query_string.put("query_string", dataquery);
            mainquery.put("query", query_string);

            order.put("order", "desc");
            sortarray.put(dt_added.put("dt_added", order));

            mainquery.put("sort", sortarray);
            mainquery.put("size", 15);
            fields.put("text", text);
            fields.put("title", title);
            fieldsobj.put("fields", fields);
            mainquery.put("highlight", fieldsobj);
            main.put("query", mainquery);

            indexes_array.put("socialdata");
            main.put("indexes", indexes_array);
            main.put("doc_types", "item");

            scroll.put("scroll", "15m");

            main.put("query_params", scroll);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return main.toString();

    }
}
