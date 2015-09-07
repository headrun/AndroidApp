package in.headrun.buzzinga.doto;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;

/**
 * Created by headrun ng 4/9/15.
 */
public class Utils {

    public String TAG = Utils.this.getClass().getSimpleName();
    Context context;
    ArrayList<QueryData> query = new ArrayList<QueryData>();
    StringBuilder queryvalue = new StringBuilder();


    public static String trackkey_query, search_query, source_query, gender_query, sentiment_query, location_query, language_query, scrollid_query, fromdate_query, todate_queruy;

    public Utils(Context context) {
        this.context = context;

    }

    public String getquerydata(ArrayList<QueryData> data) {

        for (QueryData i : data) {

            String item = i.getBkey();

            switch (item) {
                case Constants.TRACKKEY:
                    trackkey_query = query_trackkey(i.getBvalue());
                    break;
                case Constants.SEARCHKEY:
                    search_query = query_searchkey(i.getBvalue());
                    break;
                case Constants.SOURCES:
                    if (Config.Utils)
                        Log.i(TAG, "source checking");
                    source_query = query_sources(i.getBvalue());
                    break;
                case Constants.GENDER:
                    gender_query = query_gender(i.getBvalue());
                    break;
                case Constants.SENTIMENT:
                    sentiment_query = query_sentiment(i.getBvalue());
                    break;
                case Constants.LOCATION:
                    location_query = query_location(i.getBvalue());
                    break;
                case Constants.LANGUAGE:
                    language_query = query_language(i.getBvalue());
                    break;
                case Constants.FROMDATE:
                    fromdate_query = query_fromdate(i.getBvalue());
                    break;
                case Constants.TODATE:
                    todate_queruy = query_todate(i.getBvalue());
                    break;

            }
        }

        return formquery(trackkey_query, search_query, source_query, sentiment_query, gender_query, location_query, language_query, fromdate_query, todate_queruy);
    }

    public String formquery(String trackkey, String searckkey, String source, String sentiment, String gender, String loc, String lang, String fromdate, String todate) {

        setupdate(fromdate, todate);
        String query = "(" + trackkey + ")" + check_query_value(searckkey) + check_query_value(source)
                + check_query_value(sentiment) + check_query_value(gender) + check_query_value(loc) + check_query_value(lang)
                + " AND dt_added:[" + fromdate + " TO " + todate + "]";

        return queryform(query);
    }

    public String check_query_value(String query_value) {
        Log.i(TAG, "check_query value is" + query_value);
        if (query_value != null)
            return query_value.toString();
        else
            return "";
    }


    public String query_searchkey(ArrayList<String> item) {
        queryvalue.setLength(0);
        if (item.size() > 0) {
           String pref = " AND ";
            for (String skey : item) {
                queryvalue.append(pref);

                queryvalue.append(skey);
            }

            if (Config.Utils)
                Log.i(TAG, "search key is" + queryvalue.toString());


            return queryvalue.toString();
        } else
            return "";
    }

    public String query_trackkey(ArrayList<String> item) {
        queryvalue.setLength(0);
        if (item.size() > 0) {
            String pref = "";
            for (String tkey : item) {
                queryvalue.append(pref);
                pref = " AND ";
                queryvalue.append(tkey);
            }
            if (Config.Utils)
                Log.i(TAG, "track key is" + queryvalue.toString());
            return queryvalue.toString();
        } else
            return "";
    }


    public String query_sources(ArrayList<String> item) {
        queryvalue.setLength(0);
        if (Config.Utils)
            Log.i(TAG, "size is" + item.size());
        String pref = " AND ";
        if (item.size() > 0) {

            for (int i = 0; i < item.size(); i++) {
                String value = item.get(i).toLowerCase();
                if (value.length() > 0)
                    if (Constants.source_map.containsKey(value)) {

                        queryvalue.append(pref);
                        pref = " OR ";
                        queryvalue.append((String) Constants.source_map.get(value));

                    }
            }
        }
        if (queryvalue.length() > 0) {

            if (Config.Utils)
                Log.i(TAG, "source return value is" + queryvalue.toString() + "return size is" + queryvalue.length());
            return queryvalue.toString();

        } else {

            if (Config.Utils)
                Log.i(TAG, "source map else part is" + "\n Constants.source_map size is" + Constants.source_map.size());

            for (Map.Entry<String, String> values : Constants.source_map.entrySet()) {
                if (Config.Utils)
                    Log.i(TAG, "source map value 1 is" + values.getKey() + "\n value 2 is" + values.getValue());
                queryvalue.append(pref);
                pref = " OR ";
                queryvalue.append(values.getValue());
            }
            if (Config.Utils)
                Log.i(TAG, "source return string " + queryvalue.toString());
            return queryvalue.toString();
        }

    }

    public String query_gender(ArrayList<String> item) {
        queryvalue.setLength(0);
        String pref = " AND ";
        if (item.size() > 0) {
            int gen_value = -0;
            for (int i = 0; i < item.size(); i++) {
                String value = item.get(i).toLowerCase();
                if (Constants.gender_map_values.containsKey(value)) {
                    gen_value += Constants.gender_map_values.get(value);
                }
            }
            if (Math.abs(gen_value) == 0) {
                for (Map.Entry<String, String> gender_xtag : Constants.gender_map.entrySet()) {
                    queryvalue.append(pref);
                    pref = " OR ";
                    queryvalue.append((String) Constants.source_map.get(gender_xtag));
                }
                return queryvalue.toString();
            } else if (Math.abs(gen_value) == 1)
                return Constants.gender_map.get(Constants.MALE);
            else if (Math.abs(gen_value) == 2)
                return Constants.gender_map.get(Constants.FEMALE);
            else if (Math.abs(gen_value) == 3)
                return Constants.gender_map.get(Constants.UNCLASSIFIED);
        }
        return queryvalue.toString();
    }

    public String query_sentiment(ArrayList<String> item) {
        queryvalue.setLength(0);
        String pref = " AND ";
        if (item.size() > 0) {
            for (int i = 0; i < item.size(); i++) {
                String value = item.get(i).toLowerCase();
                if (Constants.sentiment_map.containsKey(value)) {
                    queryvalue.append(pref);
                    pref = " OR ";
                    queryvalue.append((String) Constants.source_map.get(value));
                }
            }
            return queryvalue.toString();
        }
        return queryvalue.toString();
    }


    public String query_fromdate(ArrayList<String> item) {

        if (item.size() <= 0) {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.DATE, -30);
            return now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE);
        } else
            return item.get(0);
    }


    public String query_todate(ArrayList<String> item) {
        if (item.size() <= 0) {
            Calendar now = Calendar.getInstance();
            return now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE);
        } else
            return item.get(0);

    }


    public String query_language(ArrayList<String> item) {
        queryvalue.setLength(0);
        String pref = " AND ";
        if (item.size() > 0) {
            for (int i = 0; i < item.size(); i++) {
                queryvalue.append(pref);
                pref = " OR ";
                queryvalue.append("xtags:" + item.get(i) + "_language_auto");

            }
            return queryvalue.toString();
        }

        return queryvalue.toString();
    }

    public String query_location(ArrayList<String> item) {
        queryvalue.setLength(0);
        String pref = " AND ";
        if (item.size() > 0) {
            for (int i = 0; i < item.size(); i++) {
                queryvalue.append(pref);
                pref = " OR ";
                queryvalue.append("xtags:" + item.get(i) + "_country_manual_parent");

            }
            return queryvalue.toString();
        }

        return queryvalue.toString();
    }


    public String query_scrollid(ArrayList<String> item) {
        return item.get(0);
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
            dataquery.put("query", query_data);
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

    public void setupdate(String fromdate, String todate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date FromDate = new Date();
        Date ToDate = new Date();
        try {
            FromDate = dateFormat.parse(fromdate);
            ToDate = dateFormat.parse(todate);
            long days = (ToDate.getTime() - FromDate.getTime()) / (24 * 60 * 60 * 1000);
            Log.i(TAG, "days is" + days);
            if (days > 30)
                Constants.SETUP = "main";
            else
                Constants.SETUP = "mini";

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static String timezone() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();

        System.out.printf("GMT offset is %s hours", TimeUnit.MINUTES.convert(mGMTOffset, TimeUnit.MILLISECONDS));
        long tz = TimeUnit.MINUTES.convert(mGMTOffset, TimeUnit.MILLISECONDS);

        if (tz > 0)
            return String.valueOf(-tz);
        else
            return String.valueOf(Math.abs(tz));
    }


    public static void add_query_data() {
        Constants.QueryString.clear();
        Constants.QueryString.add(new QueryData(Constants.TRACKKEY, Constants.BTRACKKEY));
        Constants.QueryString.add(new QueryData(Constants.FROMDATE, Constants.BFROMDATE));
        Constants.QueryString.add(new QueryData(Constants.TODATE, Constants.BTODATE));
        Constants.QueryString.add(new QueryData(Constants.SEARCHKEY, Constants.BSEARCHKEY));
        Constants.QueryString.add(new QueryData(Constants.SOURCES, Constants.BSOURCES));
        Constants.QueryString.add(new QueryData(Constants.GENDER, Constants.BGENDER));
        Constants.QueryString.add(new QueryData(Constants.SENTIMENT, Constants.BSENTIMENT));
        Constants.QueryString.add(new QueryData(Constants.LOCATION, Constants.BLOCATION));
        Constants.QueryString.add(new QueryData(Constants.LANGUAGE, Constants.BLANGUAGE));

    }

}
