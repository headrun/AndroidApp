package in.headrun.buzzinga.doto;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
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
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import in.headrun.buzzinga.R;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.activities.HomeScreen;
import in.headrun.buzzinga.config.Constants;

/**
 * Created by headrun ng 4/9/15.
 */
public class Utils {

    public static String trackkey_query, search_query, source_query, gender_query, sentiment_query, location_query, language_query, scrollid_query, fromdate_query, todate_queruy;
    public String TAG = Utils.this.getClass().getSimpleName();
    Context context;
    ArrayList<QueryData> query = new ArrayList<QueryData>();
    StringBuilder queryvalue = new StringBuilder();
    UserSession userSession;

    public Utils(Context context) {
        this.context = context;
        userSession = new UserSession(context);
    }

    public static String timezone() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
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
        Constants.QueryString.add(new QueryData(Constants.LOCATION, Constants.BLOCATION));
        Constants.QueryString.add(new QueryData(Constants.LANGUAGE, Constants.BLANGUAGE));
        Constants.QueryString.add(new QueryData(Constants.SEARCHKEY, Constants.BSEARCHKEY));
        Constants.QueryString.add(new QueryData(Constants.SOURCES, Constants.BSOURCES));
        Constants.QueryString.add(new QueryData(Constants.GENDER, Constants.BGENDER));
        Constants.QueryString.add(new QueryData(Constants.SENTIMENT, Constants.BSENTIMENT));

    }

    public static void clear_all_data() {
        Constants.BTRACKKEY.clear();
        Constants.BSEARCHKEY.clear();
        Constants.BTODATE.clear();
        Constants.BFROMDATE.clear();
        Constants.BLOCATION.clear();
        Constants.BLANGUAGE.clear();
        Constants.BSOURCES.clear();
        Constants.BGENDER.clear();
        Constants.BSENTIMENT.clear();

    }

    public String getquerydata(ArrayList<QueryData> data) {

        for (QueryData i : data) {

            String item = i.getBkey();

            switch (item) {
                case Constants.TRACKKEY:
                    trackkey_query = query_trackkey(i.getBvalue());
                        Log.i(TAG, "Track key is" + trackkey_query);

                    break;
                case Constants.SEARCHKEY:
                    search_query = query_searchkey(i.getBvalue());
                    Log.i(TAG, "Search key is" + search_query);
                    break;
                case Constants.SOURCES:
                    source_query = query_sources(i.getBvalue());
                    Log.i(TAG, "Source is" + source_query);
                    break;
                case Constants.GENDER:
                    gender_query = query_gender(i.getBvalue());
                    Log.i(TAG, "Gender is" + gender_query);
                    break;
                case Constants.SENTIMENT:
                    sentiment_query = query_sentiment(i.getBvalue());
                    Log.i(TAG, "Sentiment is" + sentiment_query);
                    break;
                case Constants.LOCATION:
                    location_query = query_location(i.getBvalue());
                    Log.i(TAG, "loc is" + location_query);
                    break;
                case Constants.LANGUAGE:
                    language_query = query_language(i.getBvalue());
                    Log.i(TAG, "lang is" + language_query);
                    break;
                case Constants.FROMDATE:
                    fromdate_query = query_fromdate(i.getBvalue());
                    Log.i(TAG, "from date is" + fromdate_query);
                    break;
                case Constants.TODATE:
                    todate_queruy = query_todate(i.getBvalue());
                    Log.i(TAG, "To date is" + todate_queruy);
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


        String countquery = "(" + trackkey + ")" + check_query_value(searckkey) + check_query_value(source)
                + check_query_value(sentiment) + check_query_value(gender) + check_query_value(loc) + check_query_value(lang);

        new UserSession(context).setClubbedquery(countquery);

        return queryform(query);

    }

    public String check_query_value(String query_value) {
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
                queryvalue.append(tkey.trim());
            }
            return queryvalue.toString();
        } else
            return "";


    }

    public String query_sources(ArrayList<String> item) {
        queryvalue.setLength(0);
        String pref = "";
        query_loc_source();

        if (item.size() > 0) {
            for (int i = 0; i < item.size(); i++) {
                String value = item.get(i).toLowerCase();
                queryvalue.append(pref);
                pref = " OR ";
                queryvalue.append(get_source_xtag(value));
            }
        }

        if (queryvalue.length() > 0) {
            return " AND (" + queryvalue.toString() + ")";

        } else

        {
            for (Map.Entry<String, String> values : Constants.source_map.entrySet()) {

                String source_type = values.getKey();
                queryvalue.append(pref);
                pref = " OR ";
                queryvalue.append(get_source_xtag(source_type));
                Log.i(TAG,"source xtag is"+get_source_xtag(source_type));
            }
            if( queryvalue.toString().trim().length()>0)
                return " AND (" + queryvalue.toString() + ")";
            else
                return "";

        }
    }

    public String get_source_xtag(String sourcetype) {

        if (Constants.source_map.containsKey(sourcetype)) {

            if (sourcetype == Constants.FACEBOOK) {
                return Constants.source_map.get(sourcetype) + (Constants.facebook_specific_xtags.trim().length() > 0 ? " AND " + Constants.facebook_specific_xtags : "");
            } else if (sourcetype == Constants.GOOGLEPLUS) {
                return Constants.source_map.get(sourcetype) + (Constants.googleplus_specific_xtags.trim().length() > 0 ? " AND " + Constants.googleplus_specific_xtags : "");

            } else if (sourcetype == Constants.TWITTER) {
                return Constants.source_map.get(sourcetype) + (Constants.twitter_specific_xtags.trim().length() > 0 ? " AND " + Constants.twitter_specific_xtags : "");
            } else if (sourcetype == Constants.FORUMS || sourcetype == Constants.NEWS || sourcetype.contains(Constants.BLOGS)) {
                return Constants.source_map.get(sourcetype) + (Constants.rss_specific_xtags.trim().length() > 0 ? " AND " + Constants.rss_specific_xtags : "");
            } else {
                return Constants.source_map.get(sourcetype);
            }

        }
        return "";
    }

    public void query_loc_source() {

        String pref = "";
        if (Constants.BLOCATION.size() > 0) {
            Constants.rss_specific_xtags = "";
            Constants.twitter_specific_xtags = "";
            Constants.googleplus_specific_xtags = "";
            Constants.facebook_specific_xtags = "";

            Constants.rss_specific_xtags += "(";
            Constants.twitter_specific_xtags += "(";
            Constants.googleplus_specific_xtags += "(";
            Constants.facebook_specific_xtags += "(";

            for (int i = 0; i < Constants.BLOCATION.size(); i++) {
                String loc = Constants.BLOCATION.get(i).toLowerCase();


                Constants.facebook_specific_xtags += pref + " (xtags:" + loc + "_country_manual_parent" +
                        " OR xtags:" + loc + "_country_auto)";

                Constants.rss_specific_xtags += pref + " xtags:" + loc + "_country_manual_parent";

                Constants.twitter_specific_xtags += pref + " xtags:" + loc + "_country_auto";
                Constants.googleplus_specific_xtags += pref + " xtags:" + loc + "_country_auto";

                pref = " OR ";
            }


            Constants.rss_specific_xtags += ")";
            Constants.twitter_specific_xtags += ")";
            Constants.googleplus_specific_xtags += ")";
            Constants.facebook_specific_xtags += ")";


        }
    }

    public String query_gender(ArrayList<String> item) {
        queryvalue.setLength(0);
        String pref = "";
        if (item.size() > 0) {
            int gen_value = -0;
            for (int i = 0; i < item.size(); i++) {
                String value = item.get(i).toLowerCase();
                Log.i(TAG, "gender" +
                        " value is \t" + value);
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
                return " AND (" + queryvalue.toString() + ")";
            } else if (Math.abs(gen_value) == 1)
                return " AND (" + Constants.gender_map.get(Constants.MALE) + ")";
            else if (Math.abs(gen_value) == 2)
                return " AND (" + Constants.gender_map.get(Constants.FEMALE) + ")";
            else if (Math.abs(gen_value) == 3)
                return " AND (" + Constants.gender_map.get(Constants.UNCLASSIFIED) + ")";
        }
        return queryvalue.toString();
    }

    public String query_sentiment(ArrayList<String> item) {
        queryvalue.setLength(0);
        String pref = "";
        if (item.size() > 0) {
            for (int i = 0; i < item.size(); i++) {
                String value = item.get(i).toLowerCase();
                if (Constants.sentiment_map.containsKey(value)) {
                    queryvalue.append(pref);
                    pref = " OR ";
                    queryvalue.append(Constants.sentiment_map.get(value));
                }
            }
            return " AND (" + queryvalue.toString() + ")";
        }
        return queryvalue.toString();
    }

    public String query_fromdate(ArrayList<String> item) {

        if (item.isEmpty() || item.size() <= 0) {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.DATE, -30);
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            return now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE) + "T00:00:00";

        } else {
            return item.get(0) + "T00:00:00";
        }

    }

    public String query_todate(ArrayList<String> item) {
        if (item.size() <= 0) {
            Calendar now = Calendar.getInstance();
            return now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE) + "T23:59:59";
        } else {
            return item.get(0) + "T23:59:59";
        }
    }

    public String query_language(ArrayList<String> item) {
        queryvalue.setLength(0);
        String pref = "";
        if (item.size() > 0) {
            for (int i = 0; i < item.size(); i++) {
                queryvalue.append(pref);
                pref = " OR ";
                queryvalue.append("xtags:" + item.get(i) + "_language_auto");

            }
            return " AND (" + queryvalue.toString() + ")";
        }

        return queryvalue.toString();
    }

    public String query_location(ArrayList<String> item) {
        queryvalue.setLength(0);
        String pref = "";
        if (item.size() > 0) {
            for (int i = 0; i < item.size(); i++) {
                queryvalue.append(pref);
                pref = " OR ";
                queryvalue.append("xtags:" + item.get(i) + "_country_manual_parent");

            }
            return " AND (" + queryvalue.toString() + ")";
        }

        return queryvalue.toString();
    }


    public String queryform(String query_data) {


        JSONObject main = new JSONObject();
        JSONObject mainquery = new JSONObject();

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

        Log.i(TAG, "search query is" + main.toString());
        return main.toString();
    }

    public String count_query() {
        JSONObject main = new JSONObject();
        JSONObject dataquery = new JSONObject();
        JSONObject query_string = new JSONObject();

        JSONArray fieldsarray = new JSONArray();

        JSONArray indexes_array = new JSONArray();

        try {

            dataquery.put("query", Date_added_toquery());
            dataquery.put("use_dis_max", true);
            fieldsarray.put("title");
            fieldsarray.put("text");
            dataquery.put("fields", fieldsarray);

            query_string.put("query_string", dataquery);

            indexes_array.put("socialdata");
            main.put("indexes", indexes_array);
            main.put("doc_types", "item");
            main.put("query", query_string);


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
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public void Buzz_notify(int article_count) {

        Intent intent = new Intent(context, HomeScreen.class);
        intent.putExtra(Constants.Intent_OPERATION, Constants.Intent_NOtify);

        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(article_count + "New Articles are come")
                        .setSmallIcon(R.drawable.buzz_logo)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        mNotifyMgr.notify(0, mBuilder.build());

    }


    public String timestamp(long ecpoch_value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        if (ecpoch_value != 0) {
            long millis = ecpoch_value * 1000;
            return sdf.format(new Date(millis));
        } else
            return sdf.format(new Date());

    }

    public String Date_added_toquery() {

        String count_clubbed_query = userSession.getClubbedquery() + " AND dt_added:[" + timestamp(Long.parseLong(userSession.getLatestDate())) + " TO " + timestamp(0) + "]";
        Log.i(TAG, "count_clubbed_query is" + count_clubbed_query);
        return count_clubbed_query;
    }
}
