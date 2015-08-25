package com.headrun.buzzinga.config;

import com.headrun.buzzinga.doto.Listitems;
import com.headrun.buzzinga.doto.SearchDetails;
import com.twitter.sdk.android.core.TwitterAuthToken;

import java.util.ArrayList;

/**
 * Created by headrun on 30/7/15.
 */
public class Constants {

    public static String scroolid = "1";
    public static String FIRSTURL = "";
    public static boolean swipedata = false;
    public static boolean finddata = false;
    public static String SEARCHSTRING;
    public static String USERNAME;
    public static TwitterAuthToken AUTHTOKEN;

    public static ArrayList<SearchDetails> listdetails = new ArrayList<SearchDetails>();
    public static ArrayList<Listitems> FILTERSOURSOURE = new ArrayList<Listitems>();
    public static ArrayList<Listitems> FILTERSENTIMENT = new ArrayList<Listitems>();
    public static ArrayList<Listitems> FILTERGENDER = new ArrayList<Listitems>();
    public static ArrayList<Listitems> FILTERLOC = new ArrayList<Listitems>();
    public static ArrayList<Listitems> FILTERLANG = new ArrayList<Listitems>();

    public static ArrayList<String> sources_selected = new ArrayList<String>();
    public static ArrayList<String> sentiment_selected = new ArrayList<String>();
    public static ArrayList<String> gender_selected = new ArrayList<String>();
    public static ArrayList<String> loc_selected = new ArrayList<String>();
    public static ArrayList<String> lang_selected = new ArrayList<String>();

    public static ArrayList<String>sourceslist=new ArrayList<>();
    public static ArrayList<String>sentimentlist=new ArrayList<>();
    public static ArrayList<String>genderlist=new ArrayList<>();

    public static  ArrayList<Listitems> filterList;



}


