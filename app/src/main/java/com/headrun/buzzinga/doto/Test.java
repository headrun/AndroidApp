package com.headrun.buzzinga.doto;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudlibs.proxy.NaturalDeserializer;
import com.cloudlibs.proxy.Proxy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.headrun.buzzinga.utils.ConnectionSettings;
import com.headrun.buzzinga.utils.JsonData;

import java.util.Calendar;

public class Test {
    static Context context;
    public static Gson gson;
    public static String rsstype = " AND xtags:rss_sourcetype_manual";
    public static String userQuery = null;
    public String which_funn = null;

    public Test(Context context) {
        this.context = context;

    }

    public void buzzdata(String searchstring, String sources, String fromdate, String todate) {

        userQuery = "{'query': {'query_string': {'query':'" + searchstring + check_searchsources(sources) +" AND dt_added:[" + check_fromdate(fromdate) + " TO " + check_todate(todate) + "]','fields':['title', 'text'],'use_dis_max':true}},'sort':[{'dt_added':{'order':'desc'}}],'size':6}";
        which_funn = "query";
        callserver(userQuery, which_funn);
    }

    public void buzzdata(String searchstring, String sources, String gender, String sentiment, String fromdate, String todate,String Loc,String Lang) {

        userQuery = "{'query': {'query_string': {'query':'" + searchstring + check_searchsources(sources) + check_gender(gender) + check_sentiment(sentiment) + check_loc(Loc)+check_lang(Lang)+" AND dt_added:[" + check_fromdate(fromdate) + " TO " + check_todate(todate) + "]', 'fields':['title', 'text'],  'use_dis_max':true}}, 'sort':[{'dt_added':{'order':'desc'}}], 'size':10}";
        which_funn = "query";
        Log.i("query is",userQuery);
       callserver(userQuery, which_funn);
    }

    public void buzzdata(String scroolid) {
        if (!scroolid.equals("1")) {
            userQuery = scroolid;
            which_funn = "scrool";
            Log.i("query is",userQuery);
            callserver(userQuery, which_funn);
        }
    }

    private void callserver(String query, String which_funn) {
        querydisplay(userQuery);
        if (ConnectionSettings.isConnected(context)) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Object.class, new NaturalDeserializer());
            gson = gsonBuilder.create();
            Log.i("Log_tag", "call Test class");
            new buzztask().execute(query, which_funn);
        } else {
            Log.i("Log_tag", "network failed");
        }
    }


    public static Object parseJson(String data) {
        return gson.fromJson(data, Object.class);
    }

    private class buzztask extends AsyncTask<String, Void, String> {

        Object response;

        /*
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    HomeScreen.progress.setVisibility(View.VISIBLE);

                }

        */
        @Override
        protected String doInBackground(String... data) {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            querydisplay("bacckground" + data[0]);
            Proxy testProxy = new Proxy("http://api.cloudlibs.com/search/", "528b31c1c32dab5c3b000006");
            Log.i("Log_tag", "testproxy is" + testProxy);
            Object[] myStringArray = {"6T0msyzCxSIVZeD4P44VleWyEqqUjKpZt0m1ei1l", "JvsPE0hu3YXaA6VbJ7FGID9ec5EGH4vsAjTCorL6"};
            testProxy._set("PIPE", myStringArray);

            userQuery = data[0];
            String query_params = "{'scroll':'15m'}";

            if (data[1].equals("query")) {
                response = testProxy.fn("search").set("query",
                        parseJson(userQuery)).set("indices", "socialdata").set(
                        "doc_types", "item").set("query_params", parseJson(query_params)).call();
            } else if (data[1].equals("scrool")) {
                response = testProxy.fn("search_scroll").set("scroll_id", userQuery).set("scroll_timeout", "15m").call();
            }

            String jsonOutput = gson.toJson(response);
            return jsonOutput;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new JsonData(context, s);
        }
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

}
