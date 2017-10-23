package in.headrun.buzzinga.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import in.headrun.buzzinga.BuildConfig;
import in.headrun.buzzinga.BuzzingaApplication;
import in.headrun.buzzinga.BuzzingaNotification;
import in.headrun.buzzinga.BuzzingaRequest;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.activities.MainActivity;
import in.headrun.buzzinga.activities.Pager;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.doto.QueryData;

/**
 * Created by headrun ng 4/9/15.
 */
public class Utils {

    public static String trackkey_query, search_query, source_query, gender_query, sentiment_query, location_query, language_query, scrollid_query, fromdate_query, todate_queruy;
    public String TAG = Utils.this.getClass().getSimpleName();
    static Context context;
    ArrayList<QueryData> query = new ArrayList<QueryData>();
    StringBuilder queryvalue = new StringBuilder();
    public UserSession userSession;

    BuzzingaApplication buzzapp = new BuzzingaApplication();
    JobScheduler job_scheduler;
    AlarmManager alarmManager;
    public FirebaseAnalytics mFirebaseAnalytics;

    public Utils(Context context) {
        this.context = context;
        userSession = new UserSession(context);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static String timezone() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        Log.i("TAG", "GMToffset" + mGMTOffset);
        long tz = TimeUnit.MINUTES.convert(mGMTOffset, TimeUnit.MILLISECONDS);

        if (tz > 0)
            return String.valueOf(-tz);
        else
            return String.valueOf(Math.abs(tz));
    }

    public String getquerydata(List<QueryData> data) {

        Log.i(TAG, "Array query data size is" + data.size());
        if (data.size() > 0) {
            for (QueryData i : data) {
                String item = i.getBkey();
                Log.i(TAG, "key is" + item);
                switch (item) {
                    case Constants.TRACKKEY:
                        trackkey_query = query_trackkey(i.getBvalue());
                        Log.i(TAG, "Track key is " + trackkey_query);
                        break;
                    case Constants.SEARCHKEY:
                        search_query = query_searchkey(i.getBvalue());
                        Log.i(TAG, "Search key is" + search_query);
                        break;
                    case Constants.SOURCES:
                        Log.i(TAG, "search values is" + Arrays.asList(i.getBvalue()));
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
        Log.i(TAG, "no values for search");
        return "";
    }

    public String formquery(String trackkey, String searckkey, String source, String sentiment, String gender, String loc, String lang, String fromdate, String todate) {

        setupdate(fromdate, todate);
        String search_key = "(\"" + trackkey + "\"" +
                (!check_query_value(searckkey).isEmpty() ?
                        " AND \"" + check_query_value(searckkey) + "\"" : "") + ")";

        String query = search_key + check_query_value(source)
                + check_query_value(sentiment) + check_query_value(gender) +
                check_query_value(loc) + check_query_value(lang);

        userSession.setClubbedquery(query);

        String query_date = userSession.getClubbedquery() + " AND dt_added:[" + fromdate + " TO " + todate + "]";

        Log.i(TAG, "query_date" + query_date);

        return queryform(query_date);

    }

    public String check_query_value(String query_value) {
        if (query_value != null)
            return query_value.toString();
        else
            return "";
    }

    public String query_searchkey(List<String> item) {
        queryvalue.setLength(0);
        Log.i(TAG, "search key size is" + item.size());
        if (item.size() > 1) {
            if (item.get(0) != null) {
                String pref = "";
                for (String skey : item) {
                    queryvalue.append(pref);
                    pref = " AND ";
                    queryvalue.append(skey.trim());
                }
                return queryvalue.toString();
            }
        } else
            return userSession.gettTACK_SEARCH_KEY();

        return "";
    }

    public String query_trackkey(List<String> item) {
        queryvalue.setLength(0);
        if (item.size() > 1) {
            if (item.get(0) != null) {
                String pref = "";
                for (String tkey : item) {
                    queryvalue.append(pref);
                    pref = " AND ";
                    queryvalue.append(tkey.trim());
                }
                return queryvalue.toString();
            }
        } else if (!userSession.getTrackKey().equals("0")) {
            return userSession.getTrackKey();
        }
        return "";

    }

    public String query_sources(List<String> item) {
        queryvalue.setLength(0);
        String pref = "";
        query_loc_source();
        Log.i(TAG, " sel sourse list is " + Arrays.asList(item).toString());
        if (item.size() >= 1) {
            for (int i = 0; i < item.size(); i++) {
                String value = item.get(i).toLowerCase();
                if (!value.isEmpty()) {
                    Log.i(TAG, " sel sourse value is " + value);
                    if (queryvalue.toString().isEmpty())
                        queryvalue.append("");
                    else
                        pref = " OR ";
                    Log.i(TAG, "sel sourse alue is" + value);
                    queryvalue.append(get_source_xtag(value));
                    Log.i(TAG, " sourse query string is " + queryvalue.toString());
                }
            }
        }

        if (queryvalue.length() > 0) {
            return " AND (" + queryvalue.toString() + ")";
        } else {
            for (Map.Entry<String, String> values : Constants.source_map.entrySet()) {
                String source_type = values.getKey();
                if (queryvalue.toString().isEmpty())
                    queryvalue.append("");
                else
                    pref = " OR ";
                queryvalue.append(get_source_xtag(source_type));
                Log.i(TAG, "source xtag is" + get_source_xtag(source_type));
            }
            if (queryvalue.toString().trim().length() > 0)
                return " AND (" + queryvalue.toString() + ")";
            else
                return "";

        }
    }

    public String get_source_xtag(String sourcetype) {
        if (!sourcetype.isEmpty()) {
            sourcetype = sourcetype.trim().toLowerCase();
            if (Constants.source_map.containsKey(sourcetype)) {
                int specific_xtag_length;
                Log.i(TAG, "source type is" + sourcetype);

                if (sourcetype.contains(Constants.FACEBOOK)) {
                    specific_xtag_length = Constants.facebook_specific_xtags.trim().length();
                    Log.i(TAG, " FACEBOOK specific_xtag_length" + specific_xtag_length);
                    return "(" + Constants.source_map.get(sourcetype) +
                            (specific_xtag_length > 0 ? " AND " + Constants.facebook_specific_xtags : "") + ")";

                } else if (sourcetype.contains(Constants.GOOGLEPLUS)) {
                    Log.i(TAG, "google plus");
                    specific_xtag_length = Constants.googleplus_specific_xtags.trim().length();
                    Log.i(TAG, " GOOGLEPLUS specific_xtag_length" + specific_xtag_length);

                    return "(" + Constants.source_map.get(sourcetype) +
                            (specific_xtag_length > 0 ? " AND " + Constants.googleplus_specific_xtags : "") + ")";

                } else if (sourcetype.contains(Constants.TWITTER)) {
                    specific_xtag_length = Constants.twitter_specific_xtags.trim().length();
                    Log.i(TAG, "TWITTER specific_xtag_length" + specific_xtag_length);
                    return "(" + Constants.source_map.get(sourcetype) +
                            (specific_xtag_length > 0 ? " AND " + Constants.twitter_specific_xtags : "") + ")";
                } else if (sourcetype.contains(Constants.FORUMS) || sourcetype.contains(Constants.NEWS) ||
                        sourcetype.contains(Constants.BLOGS)) {
                    specific_xtag_length = Constants.rss_specific_xtags.trim().length();
                    Log.i(TAG, " FORUMS specific_xtag_length" + specific_xtag_length);
                    return "(" + Constants.source_map.get(sourcetype) +
                            (specific_xtag_length > 0 ? " AND " + Constants.rss_specific_xtags : "") + ")";
                } else {
                    return Constants.source_map.get(sourcetype);
                }
            }
        }
        Log.i(TAG, "sourse key does't contains");
        return "";
    }

    public void query_loc_source() {

        String pref = "";

        if (Constants.BLOCATION.size() > 1) {
            Log.i(TAG, "loc size is query_loc_source" + Constants.BLOCATION.size());

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

                Log.i(TAG, "loc valus is" + loc);

                Constants.facebook_specific_xtags += pref + "xtags:" + loc + "_country_manual_parent";
                Constants.rss_specific_xtags += pref + "xtags:" + loc + "_country_manual_parent";

                Constants.twitter_specific_xtags += pref + "xtags:" + loc + "_country_auto";
                Constants.googleplus_specific_xtags += pref + "xtags:" + loc + "_country_auto";

                pref = " OR ";
            }


            Constants.rss_specific_xtags += ")";
            Constants.twitter_specific_xtags += ")";
            Constants.googleplus_specific_xtags += ")";
            Constants.facebook_specific_xtags += ")";


        } else {
            Constants.rss_specific_xtags = "";
            Constants.twitter_specific_xtags = "";
            Constants.googleplus_specific_xtags = "";
            Constants.facebook_specific_xtags = "";
        }

        Log.i(TAG, "Constants.rss_specific_xtags" + Constants.rss_specific_xtags +
                "\nConstants.twitter_specific_xtags" + Constants.twitter_specific_xtags +
                "\nConstants.googleplus_specific_xtags" + Constants.googleplus_specific_xtags +
                "\nConstants.facebook_specific_xtags" + Constants.facebook_specific_xtags);
    }

    public String query_gender(List<String> item) {
        queryvalue.setLength(0);
        String pref = "";
        String value = "";
        Log.i(TAG, " sel query_gender list is " + Arrays.asList(item).toString());
        if (item.size() >= 1) {
            int gen_value = -0;
            for (int i = 0; i < item.size(); i++) {
                value = item.get(i).toLowerCase();
                if (!value.isEmpty()) {
                    Log.i(TAG, "gender" +
                            " value is \t" + value);
                    if (Constants.gender_map_values.containsKey(value)) {
                        gen_value += Constants.gender_map_values.get(value);
                    }
                }
            }
            if (!value.isEmpty())
                if (Math.abs(gen_value) == 0) {
                    for (Map.Entry<String, String> gender_xtag : Constants.gender_map.entrySet()) {
                        queryvalue.append(pref);
                        pref = " OR ";
                        queryvalue.append(Constants.source_map.get(gender_xtag));
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

    public String query_sentiment(List<String> item) {
        queryvalue.setLength(0);
        String pref = "";
        if (item.size() >= 1) {
            for (int i = 0; i < item.size(); i++) {
                String value = item.get(i).toLowerCase();
                if (!value.isEmpty() && Constants.sentiment_map.containsKey(value)) {
                    queryvalue.append(pref);
                    pref = " OR ";
                    queryvalue.append(Constants.sentiment_map.get(value));
                }
            }

        }
        return queryvalue.toString().isEmpty() ? "" : " AND (" + queryvalue.toString() + ")";

    }

    public String query_fromdate(List<String> item) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat sdf1 = new SimpleDateFormat("'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Log.i(TAG, "from date item is" + item.size());
        Calendar now = Calendar.getInstance();
        long offset = now.getTimeInMillis() - now.getTimeZone().getRawOffset();
        now.setTimeInMillis(offset);
        Log.i(TAG, "time mills" + offset);

        String from_date = "";
        for (String time_todate : item) {
            if (time_todate != null && !time_todate.isEmpty()) {
                from_date = item.get(0);
                break;
            }
        }

        if (from_date.isEmpty()) {
            now.add(Calendar.DATE, -30);
            from_date = sdf2.format(now.getTime());
            userSession.setFROM_DATE(sdf2.format(now.getTime()));
        }

        return from_date + "T00:00:00";
    }

    public String query_todate(List<String> item) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdf1 = new SimpleDateFormat("'T'HH:mm:ss", Locale.getDefault());
        Calendar now = Calendar.getInstance();

        long offset = now.getTimeInMillis() - now.getTimeZone().getRawOffset();
        now.setTimeInMillis(offset);
        Log.i(TAG, "time mills" + offset);


        String todate = "";
        for (String time_todate : item) {
            if (time_todate != null && !time_todate.isEmpty()) {
                todate = item.get(0) + sdf1.format(now.getTime());

                break;
            }
        }

        if (todate.isEmpty()) {
            todate = sdf.format(now.getTime()) + sdf1.format(now.getTime());
            userSession.setTO_DATE(sdf.format(now.getTime()));
        }
        /*try {
            Date sel_date = sdf.parse(todate);

            String today_date_str = sdf.format(now.getTime());

            Date today_date = sdf.parse(today_date_str);

            if (today_date.equals(sel_date)) {

                todate = todate + sdf1.format(now.getTime());
            } else {
                todate = todate + "T23:59:59";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        return todate;
    }

    public String query_language(List<String> item) {
        Log.i(TAG, "lang size is" + item.size());
        queryvalue.setLength(0);
        String pref = "";
        if (item.size() >= 1) {
            for (int i = 0; i < item.size(); i++) {
                if (!item.get(i).isEmpty()) {
                    queryvalue.append(pref);
                    pref = " OR ";
                    queryvalue.append("xtags:" + item.get(i) + "_language_auto");
                }
            }

        }

        return queryvalue.toString().isEmpty() ? "" : " AND (" + queryvalue.toString() + ")";
    }

    public String query_location(List<String> item) {
        Log.i(TAG, "loc size is" + item.size());
        queryvalue.setLength(0);
        String pref = "";
        if (item.size() >= 1) {
            for (int i = 0; i < item.size(); i++) {
                if (!item.get(i).isEmpty()) {
                    queryvalue.append(pref);
                    pref = " OR ";
                    queryvalue.append("xtags:" + item.get(i) + "_country_manual_parent OR xtags:" + item.get(i) + "_country_auto");

                }
            }

        }

        return queryvalue.toString().isEmpty() ? "" : " AND (" + queryvalue.toString() + ")";
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

        Log.i(TAG, "count_clubbed_query is" + main.toString());
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
            if (Math.abs(days) > 30)
                Constants.SETUP = "main";
            else
                Constants.SETUP = "mini";

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public void Buzz_notification(long article_count) throws Exception {

        /*if (screendisplay()) {
            // HomeScreen.newarticle.setText(article_count + " New Articles");
            // HomeScreen.newarticle.setVisibility(View.VISIBLE);
            Log.i(TAG, "article count" + article_count);
        } else {*/
        Log.i(TAG, "Buzz Notification article count" + article_count);

        String context_text = article_count + "new articles found for your keyword \t" + userSession.getTrackKey() +
                (!userSession.gettTACK_SEARCH_KEY().isEmpty() ? " and " + userSession.gettTACK_SEARCH_KEY() : "");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.buzz_logo)
                        .setContentTitle("Buzzinga " + "keyword" + " Alert")
                        .setAutoCancel(true)
                        .setContentText(context_text)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(context_text));

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra(Constants.Intent_OPERATION, Constants.Intent_TRACK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());

        //}
    }

    public String timestamp(long ecpoch_value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        if (ecpoch_value != 0) {
            long millis = ecpoch_value * 1000;
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(millis);
            Log.i(TAG, "article time is" + now.getTime());
            long offset = now.getTimeInMillis() - now.getTimeZone().getRawOffset();
            now.setTimeInMillis(offset);
            return sdf.format(now.getTime());
        } else {
            Calendar now = Calendar.getInstance();
            Log.i(TAG, "current time is" + now.getTime());
            long offset = now.getTimeInMillis() - now.getTimeZone().getRawOffset();
            now.setTimeInMillis(offset);
            return sdf.format(now.getTime());
        }
    }

    public String Date_added_toquery() {

        String from_date = timestamp(Long.parseLong(userSession.getLatestDate()));
        String to_date = timestamp(0);
        String count_clubbed_query = userSession.getClubbedquery() + " AND dt_added:[" + from_date + " TO " + to_date + "]";
        setupdate(from_date, to_date);
        return count_clubbed_query;
    }

    public void clear_all_data() {
        Constants.SEARCHSTRING = "";
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

    public void add_filtering_data() {
        clear_all_data();

        Log.i(TAG, "sel data are sources " + userSession.getSources_data() + " \ngender " + userSession.getGender_data() +
                "\n language " + userSession.getLang_data() + "\n location " + userSession.getLoc_data());


        Constants.BSOURCES.addAll(Arrays.asList(stringtoArray(userSession.getSources_data())));

        for (String item : Constants.BSOURCES) {
            Log.i(TAG, "copy sel sources data is " + item.toString() + "len is " + Constants.BSOURCES.size());
        }

        Collections.addAll(Constants.BGENDER, stringtoArray(userSession.getGender_data()));
        Collections.addAll(Constants.BSENTIMENT, stringtoArray(userSession.getSentiment_data()));
        Collections.addAll(Constants.BLANGUAGE, stringtoArray(userSession.getLang_data()));
        Collections.addAll(Constants.BLOCATION, stringtoArray(userSession.getLoc_data()));
        Constants.BFROMDATE.add(userSession.getFROM_DATE());
        Constants.BTODATE.add(userSession.getTO_DATE());
        Constants.BTRACKKEY.add(userSession.getTrackKey());
        Constants.BSEARCHKEY.add(userSession.gettTACK_SEARCH_KEY());

    }

    public String[] stringtoArray(String value) {
        return value.replaceAll("\\[|\\]", "").split(",");
    }

    public int countIS(String val) {

        val = val.replaceAll("\\[|\\]", "");
        if (!val.isEmpty())
            return val.split(",").length;
        return 0;
    }

    public void add_query_data() {
        add_filtering_data();
        Constants.QueryString.clear();
        Log.i(TAG, "Constants.BLANGUAGE " + Constants.BLANGUAGE.toString());
        Constants.QueryString.add(new QueryData(Constants.TRACKKEY, Constants.BTRACKKEY));
        Constants.QueryString.add(new QueryData(Constants.FROMDATE, Constants.BFROMDATE));
        Constants.QueryString.add(new QueryData(Constants.TODATE, Constants.BTODATE));
        Constants.QueryString.add(new QueryData(Constants.LOCATION, Constants.BLOCATION));
        Constants.QueryString.add(new QueryData(Constants.LANGUAGE, Constants.BLANGUAGE));
        Constants.QueryString.add(new QueryData(Constants.SEARCHKEY, Constants.BSEARCHKEY));
        Constants.QueryString.add(new QueryData(Constants.SOURCES, Constants.BSOURCES));
        Constants.QueryString.add(new QueryData(Constants.GENDER, Constants.BGENDER));
        Constants.QueryString.add(new QueryData(Constants.SENTIMENT, Constants.BSENTIMENT));

        for (QueryData item : Constants.QueryString)
            Log.i(TAG, " key is " + item.getBkey() + "\n value " + item.getBvalue());
    }

    public boolean screendisplay() {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        for (ActivityManager.RunningTaskInfo i : services)
            if (services.get(0).topActivity.getPackageName().toString().
                    equalsIgnoreCase(context.getPackageName().toString())) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
                    isActivityFound = pm.isInteractive();
                } else {
                    isActivityFound = pm.isScreenOn();
                }
                Log.i(TAG, "is screen on" + isActivityFound);
                return isActivityFound;
            }
        Log.i(TAG, "is screen on" + isActivityFound);
        return isActivityFound;
    }

    public String searchQuery() {
        Log.i(TAG, "search");
        userSession.set_search_Clubbedquery(getquerydata(Constants.QueryString));
        return userSession.get_search_Clubbedquery();
    }

    public String refreshSearchQuery() {
        Log.i(TAG, "search");
        Constants.BFROMDATE.clear();
        Constants.BTODATE.clear();
        Constants.BFROMDATE.add(userSession.getLatestDate());
        Constants.BTODATE.add(timestamp(0));
        userSession.set_search_Clubbedquery(getquerydata(Constants.QueryString));
        return userSession.get_search_Clubbedquery();
    }

    public String scrollQuery() {
        return "{\"scroll_id\":\"" + Constants.scroolid + "\",\"scroll_timeout\":\"10m\"}";
    }

    public void clearSessionData() {
        userSession.editor.clear();
        userSession.editor.commit();
    }

    public interface setOnItemClickListner {

        public void itemClicked(View view, int position);

    }

    public interface setOnItemDateSelClickListner {

        public void date_sel_itemClicked(View view, int position);

    }


    public void showLog(String TAG, String msg, boolean value) {

        if (value && BuildConfig.DEBUG == true) {
            Log.wtf(TAG, msg);
        }
    }

    public static boolean isNetwrokConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void warning_info(TextView text_view, int msg_type) {

        if (msg_type == Constants.NO_RECORD) {
            text_view.setText(context.getString(R.string.norecord));
            text_view.setCompoundDrawables(null, ResourcesCompat.getDrawable(context.getResources(), R.drawable.info, null), null, null);
        }
    }

    ////update listview items
    public void updateListviewItem(ListView listview, int pos) {
        int visiblePosition = listview.getFirstVisiblePosition();
        View view = listview.getChildAt(pos - visiblePosition);
        listview.getAdapter().getView(pos, view, listview);
    }

    public void callService() {

        int is_hour = getNotify_IntervellMills();

        if (is_hour != 0) {

            Long mills = Long.valueOf(is_hour * 60 * 60 * 1000);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                showLog(TAG, "worked on lollipop and above ", Config.Utils);

                job_scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

                stopService();

                JobInfo job = new JobInfo.Builder(Constants.JOBID, new ComponentName(context,
                        BuzzingaNotificationService.class))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setRequiresCharging(true)
                        .setPeriodic(mills)
                        .build();

                int jobId = job_scheduler.schedule(job);
                if (job_scheduler.schedule(job) > 0) {
                    showLog(TAG,
                            "Successfully scheduled job: " + jobId,
                            Config.Utils);
                } else {
                    showLog(TAG,
                            "RESULT_FAILURE: " + jobId,
                            Config.Utils);
                }
            } else {

                showLog(TAG, "worked on below lollipop  ", Config.Utils);

                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, BuzzingaNotification.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + 1 * 1 * 1000,
                        mills,
                        pendingIntent);

            }
        } else {
            stopService();
        }
    }

    public void stopService() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            if (job_scheduler == null)
                job_scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            List<JobInfo> allPendingJobs = job_scheduler.getAllPendingJobs();
            String s = "";
            for (JobInfo j : allPendingJobs) {
                int jId = j.getId();
                job_scheduler.cancel(jId);
                s += "jobScheduler.cancel(" + jId + " )";
            }
            showLog(TAG, s, Config.Utils);

            job_scheduler.cancelAll();

        } else {
            if (alarmManager == null)
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, BuzzingaNotification.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmManager.cancel(pendingIntent);
        }

    }

    public int getNotify_IntervellMills() {

        String item_hour = userSession.getNotifyHour();
        int hour_is = 1;

        if (!item_hour.isEmpty()) {
            if (!item_hour.equals("None")) {
                hour_is = Integer.valueOf("" + item_hour.charAt(0));
            } else {
                hour_is = 0;
            }
            showLog(TAG, "notify hour is" + hour_is, Config.Utils);
        } else {
            userSession.setNotifyHour("1 hour");
        }
        return hour_is;
    }

    public void serverCallnotificationCount() {

        showLog(TAG, "start count call ", Config.Utils);
        if (!userSession.getTrackKey().isEmpty()) {
            showLog(TAG, "call count call " + ServerConfig.SERVER_ENDPOINT + ServerConfig.count, Config.Utils);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerConfig.SERVER_ENDPOINT + ServerConfig.count,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            showLog(TAG, "count response  " + response, Config.BuzzingaNotification);
                            try {
                                JSONObject jobj_reult = new JSONObject(response);


                                if (jobj_reult.optInt("error") == 0) {
                                    JSONObject json_result = new JSONObject(jobj_reult.optString("result"));
                                    long article_count = json_result.optInt("count");

                                    if (article_count > 0) {
                                        try {
                                            Buzz_notification(article_count);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showLog(TAG, "error are " + error.toString(), Config.BuzzingaNotification);

                }
            }) {

                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("clubbed_query", count_query());
                    params.put("tz", userSession.getTIMEZONE());
                    params.put("setup", userSession.getSETUP());

                    showLog(TAG, "params are " + params, Config.BuzzingaNotification);
                    return params;
                }

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
                    showLog(TAG, "headers are " + headers, Config.BuzzingaNotification);
                    return headers;
                }

            };
            stringRequest.setTag(TAG);
            BuzzingaRequest.getInstance(context).addToRequestQueue(stringRequest);
        }
    }

    public String setTitle() {

        StringBuilder title = new StringBuilder();
        title.append(userSession.getTrackKey());
        title.append(!userSession.gettTACK_SEARCH_KEY().isEmpty() ? " and " + userSession.gettTACK_SEARCH_KEY() : "");
        return title.toString();
    }

    public int count_filter_sel() {

        int count = 0;

        add_filtering_data();

        if (!Constants.BSOURCES.toString().replaceAll("\\[|\\]", "").isEmpty()) {
            showLog(TAG, "source count is " + Constants.BSOURCES.size() +
                    " string is " + Constants.BSOURCES.toString(), Config.Utils);
            count++;
        }
        if (!Constants.BGENDER.toString().replaceAll("\\[|\\]", "").isEmpty()) {
            showLog(TAG, "gender count is " + Constants.BGENDER.size() +
                    " string is " + Constants.BGENDER.toString(), Config.Utils);
            count++;
        }
        if (!Constants.BSENTIMENT.toString().replaceAll("\\[|\\]", "").isEmpty()) {

            showLog(TAG, "sentiment count is " + Constants.BSENTIMENT.size() +
                    " string is " + Constants.BSENTIMENT.toString(), Config.Utils);

            count++;
        }
        if (!Constants.BLOCATION.toString().replaceAll("\\[|\\]", "").isEmpty()) {

            showLog(TAG, "location count is " + Constants.BLOCATION.size() +
                    " string is " + Constants.BLOCATION.toString(), Config.Utils);

            count++;
        }
        if (!Constants.BLANGUAGE.toString().replaceAll("\\[|\\]", "").isEmpty()) {


            showLog(TAG, "language count is " + Constants.BLANGUAGE.size() +
                    " string is " + Constants.BLANGUAGE.toString(), Config.Utils);

            count++;
        }

        return count;
    }

    public int count_sel_notifyme() {
        int count = 0;
        if (getNotify_IntervellMills() != 0) {
            count++;
        }
        return count;
    }

    public void source_xtags() {
        if (Constants.sources_list.size() <= 0)
            Constants.sourse_xtags();
    }

    public void sentiment_xtags() {
        if (Constants.sentiment_map.size() <= 0)
            Constants.sentiment_xtags();
    }

    public void genter_xtags() {
        if (Constants.gender_map.size() <= 0)
            Constants.gender_xtags();
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static void callToMobile(String mobile_number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + mobile_number));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }

    }

    public void composeEmail(String[] addresses) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }


    public void getdate() {

/*
        FragmentManager fm = getFragmentManager();
        DialogFragment newFragment = new FilterByDate();
        newFragment.show(fm, "datePicker");
*/

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        String from_date = query_fromdate(Arrays.asList(userSession.getFROM_DATE()));

        String to_date = query_todate(Arrays.asList(userSession.getTO_DATE()));

        showLog(TAG, "calendar from date is " + from_date + " to date is " + to_date, Config.Utils);

        Calendar from_cal = Calendar.getInstance();
        Calendar to_cal = Calendar.getInstance();

        try {
            from_cal.setTime(sdf.parse(from_date));
        } catch (ParseException e) {
            e.printStackTrace();
            from_cal.setTime(new Date());
        }

        try {
            to_cal.setTime(sdf.parse(to_date));
        } catch (ParseException e) {
            e.printStackTrace();
            to_cal.setTime(new Date());
        }

        showLog(TAG, "calendar from mnth is " + from_cal.get(Calendar.MONTH) +
                " to mnth is " + to_cal.get(Calendar.MONTH), Config.MainActivity);

        SmoothDateRangePickerFragment smoothDateRangePickerFragment =
                SmoothDateRangePickerFragment
                        .newInstance(new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                                         @Override
                                         public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                                                    int yearStart, int monthStart,
                                                                    int dayStart, int yearEnd,
                                                                    int monthEnd, int dayEnd) {
                                             String date = "You picked the following date range: \n"
                                                     + "From " + dayStart + "/" + (++monthStart)
                                                     + "/" + yearStart + " To " + dayEnd + "/"
                                                     + (++monthEnd) + "/" + yearEnd;

                                             userSession.setFROM_DATE(yearStart + "-" + monthStart + "-" + dayStart);
                                             userSession.setTO_DATE(yearEnd + "-" + monthEnd + "-" + dayEnd);

                                             showLog(TAG, "date is " + userSession.getFROM_DATE() + "  to  " +
                                                     userSession.getTO_DATE(), Config.HOME_SCREEN);

                                             // setMenuCounter(R.id.date_filter, R.drawable.count_bg, 1);
                                             add_query_data();

                                            /* Bundle params = new Bundle();
                                             params.putString("date", "apply_date");
                                             mFirebaseAnalytics.logEvent("Apply_Date", params);
                                             mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
*/
                                             call_homeFragment(Constants.Intent_TRACK);
                                         }
                                     },
                                from_cal.get(Calendar.YEAR), from_cal.get(Calendar.MONTH), from_cal.get(Calendar.DATE),
                                to_cal.get(Calendar.YEAR), to_cal.get(Calendar.MONTH), to_cal.get(Calendar.DATE));

        Calendar cal_max = Calendar.getInstance();
        // cal.add(Calendar.DATE, 1);
        showLog(TAG, "set max date calendar is " + cal_max.get(Calendar.MONTH) + " date " +
                cal_max.get(Calendar.DATE), Config.MainActivity);

        smoothDateRangePickerFragment.setMaxDate(cal_max);
        Calendar cal_main = Calendar.getInstance();
        cal_main.add(Calendar.MONTH, -6);
        smoothDateRangePickerFragment.setMinDate(cal_main);
        try {
            smoothDateRangePickerFragment.show(((Activity) context).getFragmentManager(), "Buzzinga");
        } catch (Exception ClassCastException) {
            smoothDateRangePickerFragment.show(((FragmentActivity) context).getFragmentManager(), "Buzzinga");
        }
    }

    public void call_homeFragment(String value) {

        Bundle bundle = new Bundle();
        if (value == null)
            value = "";
        bundle.putString(Constants.Intent_OPERATION, value);
        Fragment fragment = new Pager();
        fragment.setArguments(bundle);
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();

    }

    public String dispalyDateFormate(String sel_date) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM,yy ");
        Date date;
        String dsp_date = "";
        if (!sel_date.isEmpty()) {
            try {
                date = sdf.parse(sel_date);
                dsp_date = sdf1.format(date);

            } catch (ParseException e) {
                e.printStackTrace();

            }

        } else {
            dsp_date = sdf1.format(cal.getTime());
        }
        return dsp_date;
    }


    public static void changeFontFace(TextView txtview, String type, Context context) {

        txtview.setTypeface(Typeface.createFromAsset(context.getAssets(), type));
    }
}




