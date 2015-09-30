package in.headrun.buzzinga.doto;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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

import in.headrun.buzzinga.BuzzingaApplication;
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

    BuzzingaApplication buzzapp = new BuzzingaApplication();

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


    public String getquerydata(ArrayList<QueryData> data) {

        Log.i(TAG, "Array query data size is" + data.size());
        if (data.size() > 0) {
            for (QueryData i : data) {
                String item = i.getBkey();
                Log.i(TAG, "key is" + item);
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
        } else {
            return formquery(userSession.getTrackKey(), userSession.gettTACK_SEARCH_KEY(), source_query, sentiment_query, gender_query, location_query, language_query, userSession.getFROM_DATE(), userSession.getTO_DATE());
        }
    }

    public String formquery(String trackkey, String searckkey, String source, String sentiment, String gender, String loc, String lang, String fromdate, String todate) {


        setupdate(fromdate, todate);
        String query = "(" + trackkey + ")" + check_query_value(searckkey) + check_query_value(source)
                + check_query_value(sentiment) + check_query_value(gender) + check_query_value(loc) + check_query_value(lang);

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
                Log.i(TAG, "source xtag is" + get_source_xtag(source_type));
            }
            if (queryvalue.toString().trim().length() > 0)
                return " AND (" + queryvalue.toString() + ")";
            else
                return "";

        }
    }

    public String get_source_xtag(String sourcetype) {

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
        return "";
    }

    public void query_loc_source() {

        String pref = "";

        if (buzzapp.BLOCATION.size() > 0) {
            Log.i(TAG, "loc size is query_loc_source" + buzzapp.BLOCATION.size());

            Constants.rss_specific_xtags = "";
            Constants.twitter_specific_xtags = "";
            Constants.googleplus_specific_xtags = "";
            Constants.facebook_specific_xtags = "";

            Constants.rss_specific_xtags += "(";
            Constants.twitter_specific_xtags += "(";
            Constants.googleplus_specific_xtags += "(";
            Constants.facebook_specific_xtags += "(";

            for (int i = 0; i < buzzapp.BLOCATION.size(); i++) {
                String loc = buzzapp.BLOCATION.get(i).toLowerCase();

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

        Log.i(TAG, "from date item is" + item.size());
        if (item.isEmpty() || item.get(0) == null || item.get(0).isEmpty()) {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.DATE, -30);
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            return now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE) + "T00:00:00";

        } else {
            return item.get(0) + "T00:00:00";
        }

    }

    public String query_todate(ArrayList<String> item) {
        if (item.isEmpty() || item.get(0) == null || item.get(0).isEmpty()) {
            Calendar now = Calendar.getInstance();
            return now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE) + "T23:59:59";
        } else {
            return item.get(0) + "T23:59:59";
        }
    }

    public String query_language(ArrayList<String> item) {
        Log.i(TAG, "lang size is" + item.size());
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
        Log.i(TAG, "loc size is" + item.size());
        queryvalue.setLength(0);
        String pref = "";
        if (item.size() > 0) {
            for (int i = 0; i < item.size(); i++) {
                queryvalue.append(pref);
                pref = " OR ";
                queryvalue.append("xtags:" + item.get(i) + "_country_manual_parent OR xtags:" + item.get(i) + "_country_auto");

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

    public void Buzz_notify(int article_count) {

        Intent intent = new Intent(context, HomeScreen.class);
        intent.putExtra(Constants.Intent_OPERATION, Constants.Intent_NOtify);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(article_count + "New Articles are come")
                .setSmallIcon(R.drawable.buzz_logo)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mNotifyMgr.notify(0, mBuilder.build());

    }


    public void Buzz_notification(int article_count) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.buzz_logo)
                        .setContentTitle("My notification")
                        .setAutoCancel(true)
                        .setContentText(article_count + "New Articles are come");

        Intent resultIntent = new Intent(context, HomeScreen.class);
        resultIntent.putExtra(Constants.Intent_OPERATION, Constants.Intent_NOtify);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(HomeScreen.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());

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

        String from_date = timestamp(Long.parseLong(userSession.getLatestDate()));
        String to_date = timestamp(0);
        String count_clubbed_query = userSession.getClubbedquery() + " AND dt_added:[" + from_date + " TO " + to_date + "]";

        setupdate(from_date, to_date);

        return count_clubbed_query;
    }
}
