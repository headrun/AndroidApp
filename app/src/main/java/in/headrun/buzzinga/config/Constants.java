package in.headrun.buzzinga.config;


import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.headrun.buzzinga.doto.Listitems;
import in.headrun.buzzinga.doto.QueryData;
import in.headrun.buzzinga.doto.SearchArticles;


/**
 * Created by headrun on 30/7/15.
 */
public class Constants {

    public static final String DEEP_LINK_URL = "http://buzzinga.com/track/";
    public static final String APP_INDEXING_URL = "android-app://in.headrun.buzzinga/http/buzzinga.com/track/";
    public static final String BUZZINGA_URI = "http://buzzinga.com/track/";
    public static final String TRACKKEY = "trackkey";
    public static final String SEARCHKEY = "searchkey";
    public static final String SOURCES = "sources";
    public static final String GENDER = "gender";
    public static final String SENTIMENT = "sentiment";
    public static final String LOCATION = "location";
    public static final String LANGUAGE = "language";
    public static final String FROMDATE = "fromdate";
    public static final String TODATE = "todate";
    public static final String SCROLLID = "scrollid";
    public static final String FACEBOOK = "facebook";
    public static final String TWITTER = "twitter";
    public static final String NEWS = "news";
    public static final String BLOGS = "blogs";
    public static final String FORUMS = "forums";
    public static final String GOOGLEPLUS = "googleplus";
    public static final String YOUTUBE = "youtube";
    public static final String INSTAGRAM = "instagram";
    public static final String FLICKR = "flickr";
    public static final String TUMBLR = "tumblr";
    public static final String LINKDIN = "linkedin";
    public static final String QUORA = "quora";
    public static final String POSITIVE = "positive";
    public static final String NEGATIVE = "negative";
    public static final String NEUTRAL = "neutral";
    public static final String MALE = "male";
    public static final String FEMALE = "female";
    public static final String UNCLASSIFIED = "unclassified";
    public static String scroolid = "1";
    public static String FIRSTURL = "";
    public static boolean swipedata = false;
    public static boolean isScreenOn = false;
    public static boolean REQUEST_ENABLE = true;
    public static boolean BUZZ_NOTIFY = false;
    public static boolean CHANGE_CAL = false;
    public static String SEARCHSTRING;
    public static String USERNAME;
    public static String SETUP = "";
    public static Parcelable state;

    public static String Intent_OPERATION = "Operation";
    public static String Intent_TRACK = "search";
    public static String Intent_NOtify = "notification";
    public static String Intent_NOTHING = "nothing";
    public static int newarticles = -1;
    public static int JOBID = 1;

    public static Map<String, String> sources_list = new HashMap<>();
    public static Map<String, String> source_map = new HashMap<>();
    public static Map<String, String> gender_map = new HashMap<>();
    public static Map<String, Integer> gender_map_values = new HashMap<>();
    public static Map<String, String> sentiment_map = new HashMap<>();

    public static String facebook_specific_xtags = "";
    public static String twitter_specific_xtags = "";
    public static String rss_specific_xtags = "";
    public static String googleplus_specific_xtags = "";

    public static ArrayList<SearchArticles> SEARCHARTICLES = new ArrayList<SearchArticles>();

    public static List<Listitems> FILTERSOURSOURE = new ArrayList<Listitems>();
    public static List<Listitems> FILTERSENTIMENT = new ArrayList<Listitems>();
    public static List<Listitems> FILTERGENDER = new ArrayList<Listitems>();
    public static List<Listitems> FILTERLOC = new ArrayList<Listitems>();
    public static List<Listitems> FILTERLANG = new ArrayList<Listitems>();

    public static List<String> BTRACKKEY = new ArrayList<>();
    public static List<String> BSEARCHKEY = new ArrayList<>();
    public static List<String> BSOURCES = new ArrayList<>();
    public static List<String> BGENDER = new ArrayList<>();
    public static List<String> BSENTIMENT = new ArrayList<>();
    public static List<String> BLOCATION = new ArrayList<>();
    public static List<String> BLANGUAGE = new ArrayList<>();
    public static List<String> BFROMDATE = new ArrayList<>();
    public static List<String> BTODATE = new ArrayList<>();
    public static List<QueryData> QueryString = new ArrayList<QueryData>();

    public static List<Listitems> filterList;

    public static int NO_RECORD = 1;
    public static int NO_WIFI = 2;


    public static List<String> sel_loc_list = new ArrayList<>();
    public static List<String> sel_lang_list = new ArrayList<>();

    public static void sourse_xtags() {

        sources_list.put(FACEBOOK, FACEBOOK);
        sources_list.put(TWITTER, TWITTER);
        sources_list.put(NEWS, NEWS);
        sources_list.put(BLOGS, BLOGS);
        sources_list.put(FORUMS, FORUMS);
        sources_list.put(GOOGLEPLUS, GOOGLEPLUS);
        sources_list.put(YOUTUBE, YOUTUBE);
        sources_list.put(FLICKR, FLICKR);
        sources_list.put(INSTAGRAM, INSTAGRAM);
        sources_list.put(TUMBLR, TUMBLR);
        sources_list.put(LINKDIN, LINKDIN);
        sources_list.put(QUORA, QUORA);

        source_map.put(FACEBOOK, "(xtags:facebook_search_sourcetype_manual OR xtags:facebook_search_sourcetype_manual_parent OR xtags:fbpages_sourcetype_manual OR xtags:facebook_comments_sourcetype_manual)");

        source_map.put(TWITTER, "(xtags:twitter_search_sourcetype_manual OR xtags:twitter_streaming_sourcetype_manual)");

        source_map.put(NEWS, "(xtags:focused_crawlers_sourcetype_manual OR xtags:rss_sourcetype_manual) AND xtags:news_sourcetype_manual_parent");

        source_map.put(BLOGS, "(xtags:blogs_sourcetype_manual_parent OR xtags:wordpress_search_sourcetype_manual)");
        source_map.put(FORUMS, "(xtags:focused_crawlers_sourcetype_manual OR xtags:rss_sourcetype_manual) AND xtags:forums_sourcetype_manual_parent");

        source_map.put(GOOGLEPLUS, "(xtags:googleplus_search_sourcetype_manual)");
        source_map.put(YOUTUBE, "(xtags:youtube_search_sourcetype_manual)");
        source_map.put(FLICKR, "(xtags:flickr_search_sourcetype_manual)");
        source_map.put(INSTAGRAM, "(xtags:instagram_search_sourcetype_manual)");
        source_map.put(TUMBLR, "(xtags:tumblr_search_sourcetype_manual)");
        source_map.put(LINKDIN, "(xtags:linkedin_search_sourcetype_manual)");
        source_map.put(QUORA, "(xtags:quora_sourcetype_manual_parent)");

    }

    public static void gender_xtags() {
        gender_map_values.put(MALE, 1);
        gender_map_values.put(FEMALE, 2);
        gender_map_values.put(UNCLASSIFIED, -3);

        gender_map.put(MALE, "(xtags: male_gender_final OR xtags: male_gender_final_parent)");
        gender_map.put(FEMALE, "(xtags: female_gender_final OR xtags: female_gender_final_parent)");
        gender_map.put(UNCLASSIFIED, "-((xtags: male_gender_final OR xtags: male_gender_final_parent)" +
                " OR (xtags: female_gender_final OR xtags: female_gender_final_parent))");

    }

    public static void sentiment_xtags() {
        sentiment_map.put(NEUTRAL, "xtags:neutral_sentiment_final");
        sentiment_map.put(NEGATIVE, "xtags:negative_sentiment_final");
        sentiment_map.put(POSITIVE, "xtags:positive_sentiment_final");
    }
}