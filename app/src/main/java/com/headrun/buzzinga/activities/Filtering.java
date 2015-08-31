package com.headrun.buzzinga.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.headrun.buzzinga.R;
import com.headrun.buzzinga.config.Constants;
import com.headrun.buzzinga.doto.Listitems;
import com.headrun.buzzinga.doto.Test;
import com.headrun.buzzinga.utils.FilterTitleAdapter;
import com.headrun.buzzinga.utils.ListViewAdapter;

import java.util.Iterator;

/**
 * Created by headrun on 6/8/15.
 */
public class Filtering extends AppCompatActivity implements View.OnClickListener {
    //@InjectView(R.id.filter_titles)
    ListView filter_titles, filter_sourceslist, filter_sentiment, filter_gender, filter_location, filter_language;
    Button clearfilter, applyfilter;
    EditText autosearch;
    ListViewAdapter adapter;
    SearchManager searchmanager;
    String Sourcestatus;

    Test buzztest;
    public static StringBuilder sourcequery = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sourcefilter_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //ButterKnife.inject(this);

        filter_titles = (ListView) findViewById(R.id.filter_titles);
        filter_sourceslist = (ListView) findViewById(R.id.filter_sourceslist);
        filter_sentiment = (ListView) findViewById(R.id.filter_sentiment);
        filter_gender = (ListView) findViewById(R.id.filter_gender);
        filter_location = (ListView) findViewById(R.id.filter_location);
        filter_language = (ListView) findViewById(R.id.filter_language);
        clearfilter = (Button) findViewById(R.id.clearfilter);
        applyfilter = (Button) findViewById(R.id.applyfilter);
        autosearch = (EditText) findViewById(R.id.autosearch);
        searchmanager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        buzztest = new Test(getApplication());

        clearfilter.setOnClickListener(this);
        applyfilter.setOnClickListener(this);

        String[] filtertitles = getResources().getStringArray(R.array.filtertitles);
        TypedArray filtertitleimages = getResources().obtainTypedArray(R.array.titleimages);

        //ArrayAdapter<String> filter_titles_adapter = new ArrayAdapter<String>(this, R.layout.source_titles, filtertitles);
        FilterTitleAdapter titleadapter = new FilterTitleAdapter(Filtering.this, filtertitles, filtertitleimages);
        filter_titles.setAdapter(titleadapter);

        filter_titles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < filter_titles.getAdapter().getCount(); i++) {
                    filter_titles.getChildAt(i).setBackgroundColor(Color.parseColor("#d62a2a2a"));
                }
                parent.getChildAt(position).setBackgroundColor(Color.GRAY);

                Log.i("selected data  is", "" + filter_titles.getItemAtPosition(position).toString().toUpperCase());
                String filteritem = filter_titles.getItemAtPosition(position).toString().toUpperCase();

                FilterStatus filtering = FilterStatus.valueOf(filteritem);

                filterselection(filtering);
            }
        });


        listOfFilterSources();
        listOfFilterSentiment();
        listOfFilterGender();
        listOfFilterLocation();
        listOfFilterLang();
        // ceratelists();

        autosearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());

                if (Sourcestatus.contains("lang")) {
                    if (TextUtils.isEmpty(s.toString()))
                        filter_language.clearTextFilter();
                    else
                        filter_language.setFilterText(s.toString());

                }else{
                    if (TextUtils.isEmpty(s.toString()))
                        filter_location.clearTextFilter();
                    else
                        filter_location.setFilterText(s.toString());

                }
            }
                @Override
                public void afterTextChanged (Editable s){

                }
            }

            );
        }

        public void filterselection (FilterStatus filtering){

            switch (filtering) {

                case SOURCES:
                    filterSource();
                    break;
                case SENTIMENT:
                    filterSentiment();
                    break;
                case GENDER:
                    filterGender();
                    break;
                case LANGUAGE:
                    autosearch.getText().clear();
                    Sourcestatus = "lang";
                    filterLang();
                    break;
                case LOCATION:
                    autosearch.getText().clear();
                    Sourcestatus = "loc";
                    filterLocation();
                    break;
            }

        }

        public void filterSource () {
            Log.i("filter by", " source");

            filter_sourceslist.setVisibility(View.VISIBLE);
            filter_sentiment.setVisibility(View.GONE);
            filter_gender.setVisibility(View.GONE);
            filter_language.setVisibility(View.GONE);
            filter_location.setVisibility(View.GONE);
            autosearch.setVisibility(View.GONE);
        }

        public void filterSentiment () {
            Log.i("filter by", " Sentiment");

            filter_sourceslist.setVisibility(View.GONE);
            filter_sentiment.setVisibility(View.VISIBLE);
            filter_gender.setVisibility(View.GONE);
            filter_language.setVisibility(View.GONE);
            filter_location.setVisibility(View.GONE);
            autosearch.setVisibility(View.GONE);

        }

        public void filterGender () {
            Log.i("filter by", " gender");

            filter_sourceslist.setVisibility(View.GONE);
            filter_sentiment.setVisibility(View.GONE);
            filter_gender.setVisibility(View.VISIBLE);
            filter_language.setVisibility(View.GONE);
            filter_location.setVisibility(View.GONE);
            autosearch.setVisibility(View.GONE);

        }

        public void filterLang () {
            Log.i("filter by", " Lang");

            filter_sourceslist.setVisibility(View.GONE);
            filter_sentiment.setVisibility(View.GONE);
            filter_gender.setVisibility(View.GONE);
            filter_language.setVisibility(View.VISIBLE);
            filter_location.setVisibility(View.GONE);
            autosearch.setVisibility(View.VISIBLE);
        }

        public void filterLocation () {
            Log.i("filter by", " Location");

            filter_sourceslist.setVisibility(View.GONE);
            filter_sentiment.setVisibility(View.GONE);
            filter_gender.setVisibility(View.GONE);
            filter_language.setVisibility(View.GONE);
            filter_location.setVisibility(View.VISIBLE);
            autosearch.setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick (View v){
            if (v.getId() == R.id.clearfilter) {


            } else if (v.getId() == R.id.applyfilter) {

                if (HomeScreen.search_validation()) {
                    String sourcequery = sourcequery();
                    String sentimentquery = sentimentquery();
                    String genderquery = genderquery();
                    String locquery = locquery();
                    String langquery = langquery();
                    Log.i("Log_Tag", "source querry is" + sourcequery + "\nsentiment" + sentimentquery + "\n genderquery" + genderquery + "loc query" + locquery + "lang query" + langquery);
                    Constants.scroolid = "1";
                    Constants.listdetails.clear();
                    buzztest.buzzdata(Constants.SEARCHSTRING, sourcequery, genderquery, sentimentquery, "1", "1", locquery, langquery);
                    startActivity(new Intent(getApplication(),HomeScreen.class));
                } else {
                    Toast.makeText(getApplication(), "Enter the search string", Toast.LENGTH_LONG).show();
                }
            }
        }

        public static String sourcequery () {

            String prefix = "";
            sourcequery.setLength(0);
            Constants.sources_selected.clear();
            for (Listitems sourceslist : Constants.FILTERSOURSOURE) {

                if (sourceslist.isSelectd()) {
                    Constants.sources_selected.add(sourceslist.getSourcename());
                    sourcequery.append(prefix);
                    prefix = " OR ";
                    sourcequery.append("xtags:" + sourceslist.getXtag());
                }

            }
            if (sourcequery.length() > 0)
                return sourcequery.toString();

            return "1";
        }

        public static String sentimentquery () {
            String prefix = "";
            sourcequery.setLength(0);
            Constants.sentiment_selected.clear();
            for (Listitems sentimentlist : Constants.FILTERSENTIMENT) {

                if (sentimentlist.isSelectd()) {
                    Constants.sentiment_selected.add(sentimentlist.getSourcename());
                    Log.i("Log_tag", "sentiment_selected size is" + Constants.sentiment_selected.size());
                    sourcequery.append(prefix);
                    prefix = " OR ";
                    sourcequery.append("xtags:" + sentimentlist.getXtag());
                }

            }
            if (sourcequery.length() > 0)
                return sourcequery.toString();

            return "1";

        }

        public static String genderquery () {

            String prefix = "";
            sourcequery.setLength(0);
            Constants.gender_selected.clear();
            for (Listitems genderlist : Constants.FILTERGENDER) {

                if (genderlist.isSelectd()) {
                    Constants.gender_selected.add(genderlist.getSourcename());
                    sourcequery.append(prefix);
                    prefix = " OR ";

                    sourcequery.append("xtags:" + genderlist.getXtag());
                }

            }
            if (sourcequery.length() > 0)
                return sourcequery.toString();

            return "1";

        }

        public static String locquery () {

            String prefix = "";
            sourcequery.setLength(0);
            Constants.gender_selected.clear();
            for (Listitems loclist : Constants.FILTERLOC) {

                if (loclist.isSelectd()) {
                    Constants.gender_selected.add(loclist.getSourcename());
                    sourcequery.append(prefix);
                    prefix = " OR ";
                    sourcequery.append("xtags:" + loclist.getXtag() + "_country_manual_parent");
                }

            }
            if (sourcequery.length() > 0)
                return sourcequery.toString();

            return "1";

        }

        public static String langquery () {

            String prefix = "";
            sourcequery.setLength(0);
            Constants.lang_selected.clear();
            for (Listitems langlist : Constants.FILTERLANG) {

                if (langlist.isSelectd()) {
                    Constants.lang_selected.add(langlist.getSourcename());
                    sourcequery.append(prefix);
                    prefix = " OR ";
                    sourcequery.append("xtags:" + langlist.getXtag() + "_language_auto");
                }

            }
            if (sourcequery.length() > 0)
                return sourcequery.toString();

            return "1";
        }


        public enum FilterStatus {
            SOURCES,
            SENTIMENT,
            GENDER,
            LANGUAGE,
            LOCATION
        }

        public void listOfFilterSources () {
            Constants.FILTERSOURSOURE.clear();
            Constants.FILTERSOURSOURE.add(new Listitems("fbpages_sourcetype_manual", "Facebook", source_check("Facebook")));
            Constants.FILTERSOURSOURE.add(new Listitems("twitter_search_sourcetype_manual", "Twitter", source_check("Twitter")));
            Constants.FILTERSOURSOURE.add(new Listitems("googleplus_search_sourcetype_manual", "Google+", source_check("Google+")));
            Constants.FILTERSOURSOURE.add(new Listitems("news_sourcetype_manual_parent", "News", source_check("News")));
            Constants.FILTERSOURSOURE.add(new Listitems("blogs_sourcetype_manual_parent", "Blogs", source_check("Blogs")));
            Constants.FILTERSOURSOURE.add(new Listitems("forums_sourcetype_manual_parent", "Forums", source_check("Forums")));
            Constants.FILTERSOURSOURE.add(new Listitems("youtube_search_sourcetype_manual", "YouTube", source_check("YouTube")));
            Constants.FILTERSOURSOURE.add(new Listitems("flickr_search_sourcetype_manual", "Flickr", source_check("Flickr")));
            Constants.FILTERSOURSOURE.add(new Listitems("instagram_search_sourcetype_manual", "Instagram", source_check("Instagram")));
            Constants.FILTERSOURSOURE.add(new Listitems("tumblr_search_sourcetype_manual", "Tumblr", source_check("Tumblr")));
            Constants.FILTERSOURSOURE.add(new Listitems("linkedin_search_sourcetype_manual", "Linkedin", source_check("Linkedin")));
            Constants.FILTERSOURSOURE.add(new Listitems("quora_sourcetype_manual_parent", "Quora", source_check("Quora")));

            adapter = new ListViewAdapter(Filtering.this, Constants.FILTERSOURSOURE);
            filter_sourceslist.setAdapter(adapter);

        }

        public void listOfFilterSentiment () {
            Constants.FILTERSENTIMENT.clear();
            Constants.FILTERSENTIMENT.add(new Listitems("positive_sentiment_final", "Positive", sentiment_check("Positive")));
            Constants.FILTERSENTIMENT.add(new Listitems("negative_sentiment_final", "Negative", sentiment_check("Negative")));
            Constants.FILTERSENTIMENT.add(new Listitems("neutral_sentiment_final", "Neutral", sentiment_check("Neutral")));

            adapter = new ListViewAdapter(Filtering.this, Constants.FILTERSENTIMENT);
            filter_sentiment.setAdapter(adapter);

        }

        public void listOfFilterGender () {
            Constants.FILTERGENDER.clear();
            Constants.FILTERGENDER.add(new Listitems("male_gender_final", "Male", gender_check("Male")));
            Constants.FILTERGENDER.add(new Listitems("female_gender_final", "Female", gender_check("Female")));
            Constants.FILTERGENDER.add(new Listitems("unclassified_gender_final", "Unclassified", gender_check("Unclassified")));

            adapter = new ListViewAdapter(Filtering.this, Constants.FILTERGENDER);
            filter_gender.setAdapter(adapter);
        }


        public void listOfFilterLang () {
            Constants.FILTERLANG.clear();
            // Constants.FILTERLANG.add(new Listitems("en_language_auto", "English", lang_check("English")));
            Constants.FILTERLANG.add(new Listitems("gv", "MANX", lang_check("MANX")));
            Constants.FILTERLANG.add(new Listitems("gu", "GUJARATI", lang_check("GUJARATI")));
            Constants.FILTERLANG.add(new Listitems("sco", "SCOTS", lang_check("SCOTS")));
            Constants.FILTERLANG.add(new Listitems("gd", "SCOTS_GAELIC", lang_check("SCOTS_GAELIC")));
            Constants.FILTERLANG.add(new Listitems("ga", "IRISH", lang_check("IRISH")));
            Constants.FILTERLANG.add(new Listitems("gn", "GUARANI", lang_check("GUARANI")));
            Constants.FILTERLANG.add(new Listitems("gl", "GALICIAN", lang_check("GALICIAN")));
            Constants.FILTERLANG.add(new Listitems("lg", "GANDA", lang_check("GANDA")));
            Constants.FILTERLANG.add(new Listitems("lb", "LUXEMBOURGISH", lang_check("LUXEMBOURGISH")));
            Constants.FILTERLANG.add(new Listitems("la", "LATIN", lang_check("LATIN")));
            Constants.FILTERLANG.add(new Listitems("ln", "LINGALA", lang_check("LINGALA")));
            Constants.FILTERLANG.add(new Listitems("tw", "TWI", lang_check("TWI")));
            Constants.FILTERLANG.add(new Listitems("tt", "TATAR", lang_check("TATAR")));
            Constants.FILTERLANG.add(new Listitems("tr", "TURKISH", lang_check("TURKISH")));
            Constants.FILTERLANG.add(new Listitems("ts", "TSONGA", lang_check("TSONGA")));
            Constants.FILTERLANG.add(new Listitems("lv", "LATVIAN", lang_check("LATVIAN")));
            Constants.FILTERLANG.add(new Listitems("to", "TONGA", lang_check("TONGA")));
            Constants.FILTERLANG.add(new Listitems("lt", "LITHUANIAN", lang_check("LITHUANIAN")));
            Constants.FILTERLANG.add(new Listitems("tk", "TURKMEN", lang_check("TURKMEN")));
            Constants.FILTERLANG.add(new Listitems("th", "THAI", lang_check("THAI")));
            Constants.FILTERLANG.add(new Listitems("ti", "TIGRINYA", lang_check("TIGRINYA")));
            Constants.FILTERLANG.add(new Listitems("tg", "TAJIK", lang_check("TAJIK")));
            Constants.FILTERLANG.add(new Listitems("te", "TELUGU", lang_check("TELUGU")));
            Constants.FILTERLANG.add(new Listitems("fil", "TAGALOG", lang_check("TAGALOG")));
            Constants.FILTERLANG.add(new Listitems("ta", "TAMIL", lang_check("TAMIL")));
            Constants.FILTERLANG.add(new Listitems("yi", "YIDDISH", lang_check("YIDDISH")));
            Constants.FILTERLANG.add(new Listitems("yo", "YORUBA", lang_check("YORUBA")));
            Constants.FILTERLANG.add(new Listitems("de", "GERMAN", lang_check("GERMAN")));
            Constants.FILTERLANG.add(new Listitems("da", "DANISH", lang_check("DANISH")));
            Constants.FILTERLANG.add(new Listitems("-pt-BR", "PORTUGUESE_B", lang_check("PORTUGUESE_B")));
            Constants.FILTERLANG.add(new Listitems("dv", "DHIVEHI", lang_check("DHIVEHI")));
            Constants.FILTERLANG.add(new Listitems("sr-ME", "MONTENEGRIN", lang_check("MONTENEGRIN")));
            Constants.FILTERLANG.add(new Listitems("crp", "CREOLES_AND_PIDGINS_OTHER", lang_check("CREOLES_AND_PIDGINS_OTHER")));
            Constants.FILTERLANG.add(new Listitems("qu", "QUECHUA", lang_check("QUECHUA")));
            Constants.FILTERLANG.add(new Listitems("cpf", "CREOLES_AND_PIDGINS_FRENCH_BASED", lang_check("CREOLES_AND_PIDGINS_FRENCH_BASED")));
            Constants.FILTERLANG.add(new Listitems("ut", "TG_UNKNOWN_LANGUAGE", lang_check("TG_UNKNOWN_LANGUAGE")));
            Constants.FILTERLANG.add(new Listitems("el", "GREEK", lang_check("GREEK")));
            Constants.FILTERLANG.add(new Listitems("eo", "ESPERANTO", lang_check("ESPERANTO")));
            Constants.FILTERLANG.add(new Listitems("en", "ENGLISH", lang_check("ENGLISH")));
            Constants.FILTERLANG.add(new Listitems("zh", "CHINESE", lang_check("CHINESE")));
            Constants.FILTERLANG.add(new Listitems("bo", "TIBETAN", lang_check("TIBETAN")));
            Constants.FILTERLANG.add(new Listitems("za", "ZHUANG", lang_check("ZHUANG")));
            Constants.FILTERLANG.add(new Listitems("eu", "BASQUE", lang_check("BASQUE")));
            Constants.FILTERLANG.add(new Listitems("zu", "ZULU", lang_check("ZULU")));
            Constants.FILTERLANG.add(new Listitems("cpe", "CREOLES_AND_PIDGINS_ENGLISH_BASED", lang_check("CREOLES_AND_PIDGINS_ENGLISH_BASED")));
            Constants.FILTERLANG.add(new Listitems("es", "SPANISH", lang_check("SPANISH")));
            Constants.FILTERLANG.add(new Listitems("ba", "BASHKIR", lang_check("BASHKIR")));
            Constants.FILTERLANG.add(new Listitems("ru", "RUSSIAN", lang_check("RUSSIAN")));
            Constants.FILTERLANG.add(new Listitems("rw", "KINYARWANDA", lang_check("KINYARWANDA")));
            Constants.FILTERLANG.add(new Listitems("kl", "GREENLANDIC", lang_check("GREENLANDIC")));
            Constants.FILTERLANG.add(new Listitems("rm", "RHAETO_ROMANCE", lang_check("RHAETO_ROMANCE")));
            Constants.FILTERLANG.add(new Listitems("rn", "RUNDI", lang_check("RUNDI")));
            Constants.FILTERLANG.add(new Listitems("ro", "ROMANIAN", lang_check("ROMANIAN")));
            Constants.FILTERLANG.add(new Listitems("be", "BELARUSIAN", lang_check("BELARUSIAN")));
            Constants.FILTERLANG.add(new Listitems("bg", "BULGARIAN", lang_check("BULGARIAN")));
            Constants.FILTERLANG.add(new Listitems("uk", "UKRAINIAN", lang_check("UKRAINIAN")));
            Constants.FILTERLANG.add(new Listitems("wo", "WOLOF", lang_check("WOLOF")));
            Constants.FILTERLANG.add(new Listitems("bn", "BENGALI", lang_check("BENGALI")));
            Constants.FILTERLANG.add(new Listitems("jw", "JAVANESE", lang_check("JAVANESE")));
            Constants.FILTERLANG.add(new Listitems("bh", "BIHARI", lang_check("BIHARI")));
            Constants.FILTERLANG.add(new Listitems("bi", "BISLAMA", lang_check("BISLAMA")));
            Constants.FILTERLANG.add(new Listitems("br", "BRETON", lang_check("BRETON")));
            Constants.FILTERLANG.add(new Listitems("bs", "BOSNIAN", lang_check("BOSNIAN")));
            Constants.FILTERLANG.add(new Listitems("ja", "JAPANESE", lang_check("JAPANESE")));
            Constants.FILTERLANG.add(new Listitems("om", "OROMO", lang_check("OROMO")));
            Constants.FILTERLANG.add(new Listitems("syr", "SYRIAC", lang_check("SYRIAC")));
            Constants.FILTERLANG.add(new Listitems("pt-PT", "PORTUGUESE_P", lang_check("PORTUGUESE_P")));
            Constants.FILTERLANG.add(new Listitems("oc", "OCCITAN", lang_check("OCCITAN")));
            Constants.FILTERLANG.add(new Listitems("sit-NP", "LIMBU", lang_check("LIMBU")));
            Constants.FILTERLANG.add(new Listitems("lo", "LAOTHIAN", lang_check("LAOTHIAN")));
            Constants.FILTERLANG.add(new Listitems("or", "ORIYA", lang_check("ORIYA")));
            Constants.FILTERLANG.add(new Listitems("xh", "XHOSA", lang_check("XHOSA")));
            Constants.FILTERLANG.add(new Listitems("mr", "MARATHI", lang_check("MARATHI")));
            Constants.FILTERLANG.add(new Listitems("co", "CORSICAN", lang_check("CORSICAN")));
            Constants.FILTERLANG.add(new Listitems("ca", "CATALAN", lang_check("CATALAN")));
            Constants.FILTERLANG.add(new Listitems("cy", "WELSH", lang_check("WELSH")));
            Constants.FILTERLANG.add(new Listitems("cs", "CZECH", lang_check("CZECH")));
            Constants.FILTERLANG.add(new Listitems("ps", "PASHTO", lang_check("PASHTO")));
            Constants.FILTERLANG.add(new Listitems("pt", "PORTUGUESE", lang_check("PORTUGUESE")));
            Constants.FILTERLANG.add(new Listitems("chr", "CHEROKEE", lang_check("CHEROKEE")));
            Constants.FILTERLANG.add(new Listitems("pa", "PUNJABI", lang_check("PUNJABI")));
            Constants.FILTERLANG.add(new Listitems("pl", "POLISH", lang_check("POLISH")));
            Constants.FILTERLANG.add(new Listitems("hy", "ARMENIAN", lang_check("ARMENIAN")));
            Constants.FILTERLANG.add(new Listitems("hr", "CROATIAN", lang_check("CROATIAN")));
            Constants.FILTERLANG.add(new Listitems("iu", "INUKTITUT", lang_check("INUKTITUT")));
            Constants.FILTERLANG.add(new Listitems("ht", "HAITIAN_CREOLE", lang_check("HAITIAN_CREOLE")));
            Constants.FILTERLANG.add(new Listitems("hu", "HUNGARIAN", lang_check("HUNGARIAN")));
            Constants.FILTERLANG.add(new Listitems("hi", "HINDI", lang_check("HINDI")));
            Constants.FILTERLANG.add(new Listitems("ha", "HAUSA", lang_check("HAUSA")));
            Constants.FILTERLANG.add(new Listitems("he", "HEBREW", lang_check("HEBREW")));
            Constants.FILTERLANG.add(new Listitems("mg", "MALAGASY", lang_check("MALAGASY")));
            Constants.FILTERLANG.add(new Listitems("uz", "UZBEK", lang_check("UZBEK")));
            Constants.FILTERLANG.add(new Listitems("ml", "MALAYALAM", lang_check("MALAYALAM")));
            Constants.FILTERLANG.add(new Listitems("mo", "MOLDAVIAN", lang_check("MOLDAVIAN")));
            Constants.FILTERLANG.add(new Listitems("mn", "MONGOLIAN", lang_check("MONGOLIAN")));
            Constants.FILTERLANG.add(new Listitems("mi", "MAORI", lang_check("MAORI")));
            Constants.FILTERLANG.add(new Listitems("ik", "INUPIAK", lang_check("INUPIAK")));
            Constants.FILTERLANG.add(new Listitems("mk", "MACEDONIAN", lang_check("MACEDONIAN")));
            Constants.FILTERLANG.add(new Listitems("ur", "URDU", lang_check("URDU")));
            Constants.FILTERLANG.add(new Listitems("mt", "MALTESE", lang_check("MALTESE")));
            Constants.FILTERLANG.add(new Listitems("un", "UNKNOWN", lang_check("UNKNOWN")));
            Constants.FILTERLANG.add(new Listitems("ms", "MALAY", lang_check("MALAY")));
            Constants.FILTERLANG.add(new Listitems("sr", "SERBIAN", lang_check("SERBIAN")));
            Constants.FILTERLANG.add(new Listitems("ug", "UIGHUR", lang_check("UIGHUR")));
            Constants.FILTERLANG.add(new Listitems("my", "BURMESE", lang_check("BURMESE")));
            Constants.FILTERLANG.add(new Listitems("aa", "AFAR", lang_check("AFAR")));
            Constants.FILTERLANG.add(new Listitems("ab", "ABKHAZIAN", lang_check("ABKHAZIAN")));
            Constants.FILTERLANG.add(new Listitems("ss", "SISWANT", lang_check("SISWANT")));
            Constants.FILTERLANG.add(new Listitems("af", "AFRIKAANS", lang_check("AFRIKAANS")));
            Constants.FILTERLANG.add(new Listitems("tn", "TSWANA", lang_check("TSWANA")));
            Constants.FILTERLANG.add(new Listitems("vi", "VIETNAMESE", lang_check("VIETNAMESE")));
            Constants.FILTERLANG.add(new Listitems("is", "ICELANDIC", lang_check("ICELANDIC")));
            Constants.FILTERLANG.add(new Listitems("am", "AMHARIC", lang_check("AMHARIC")));
            Constants.FILTERLANG.add(new Listitems("it", "ITALIAN", lang_check("ITALIAN")));
            Constants.FILTERLANG.add(new Listitems("sv", "SWEDISH", lang_check("SWEDISH")));
            Constants.FILTERLANG.add(new Listitems("ay", "AYMARA", lang_check("AYMARA")));
            Constants.FILTERLANG.add(new Listitems("as", "ASSAMESE", lang_check("ASSAMESE")));
            Constants.FILTERLANG.add(new Listitems("ar", "ARABIC", lang_check("ARABIC")));
            Constants.FILTERLANG.add(new Listitems("km", "KHMER", lang_check("KHMER")));
            Constants.FILTERLANG.add(new Listitems("et", "ESTONIAN", lang_check("ESTONIAN")));
            Constants.FILTERLANG.add(new Listitems("ia", "INTERLINGUA", lang_check("INTERLINGUA")));
            Constants.FILTERLANG.add(new Listitems("kha", "KHASI", lang_check("KHASI")));
            Constants.FILTERLANG.add(new Listitems("az", "AZERBAIJANI", lang_check("AZERBAIJANI")));
            Constants.FILTERLANG.add(new Listitems("ie", "INTERLINGUE", lang_check("INTERLINGUE")));
            Constants.FILTERLANG.add(new Listitems("id", "INDONESIAN", lang_check("INDONESIAN")));
            Constants.FILTERLANG.add(new Listitems("ks", "KASHMIRI", lang_check("KASHMIRI")));
            Constants.FILTERLANG.add(new Listitems("nl", "DUTCH", lang_check("DUTCH")));
            Constants.FILTERLANG.add(new Listitems("nn", "NORWEGIAN_N", lang_check("NORWEGIAN_N")));
            Constants.FILTERLANG.add(new Listitems("na", "NAURU", lang_check("NAURU")));
            Constants.FILTERLANG.add(new Listitems("nb", "NORWEGIAN", lang_check("NORWEGIAN")));
            Constants.FILTERLANG.add(new Listitems("ne", "NEPALI", lang_check("NEPALI")));
            Constants.FILTERLANG.add(new Listitems("vo", "VOLAPUK", lang_check("VOLAPUK")));
            Constants.FILTERLANG.add(new Listitems("fr", "FRENCH", lang_check("FRENCH")));
            Constants.FILTERLANG.add(new Listitems("sm", "SAMOAN", lang_check("SAMOAN")));
            Constants.FILTERLANG.add(new Listitems("fy", "FRISIAN", lang_check("FRISIAN")));
            Constants.FILTERLANG.add(new Listitems("fa", "PERSIAN", lang_check("PERSIAN")));
            Constants.FILTERLANG.add(new Listitems("fi", "FINNISH", lang_check("FINNISH")));
            Constants.FILTERLANG.add(new Listitems("fj", "FIJIAN", lang_check("FIJIAN")));
            Constants.FILTERLANG.add(new Listitems("sa", "SANSKRIT", lang_check("SANSKRIT")));
            Constants.FILTERLANG.add(new Listitems("fo", "FAROESE", lang_check("FAROESE")));
            Constants.FILTERLANG.add(new Listitems("ka", "GEORGIAN", lang_check("GEORGIAN")));
            Constants.FILTERLANG.add(new Listitems("kk", "KAZAKH", lang_check("KAZAKH")));
            Constants.FILTERLANG.add(new Listitems("zh-TW", "CHINESET", lang_check("CHINESET")));
            Constants.FILTERLANG.add(new Listitems("sq", "ALBANIAN", lang_check("ALBANIAN")));
            Constants.FILTERLANG.add(new Listitems("ko", "KOREAN", lang_check("KOREAN")));
            Constants.FILTERLANG.add(new Listitems("kn", "KANNADA", lang_check("KANNADA")));
            Constants.FILTERLANG.add(new Listitems("su", "SUNDANESE", lang_check("SUNDANESE")));
            Constants.FILTERLANG.add(new Listitems("st", "SESOTHO", lang_check("SESOTHO")));
            Constants.FILTERLANG.add(new Listitems("sk", "SLOVAK", lang_check("SLOVAK")));
            Constants.FILTERLANG.add(new Listitems("si", "SINHALESE", lang_check("SINHALESE")));
            Constants.FILTERLANG.add(new Listitems("sh", "SERBO_CROATIAN", lang_check("SERBO_CROATIAN")));
            Constants.FILTERLANG.add(new Listitems("so", "SOMALI", lang_check("SOMALI")));
            Constants.FILTERLANG.add(new Listitems("sn", "SHONA", lang_check("SHONA")));
            Constants.FILTERLANG.add(new Listitems("ku", "KURDISH", lang_check("KURDISH")));
            Constants.FILTERLANG.add(new Listitems("sl", "SLOVENIAN", lang_check("SLOVENIAN")));
            Constants.FILTERLANG.add(new Listitems("cpp", "CREOLES_AND_PIDGINS_PORTUGUESE_BASED", lang_check("CREOLES_AND_PIDGINS_PORTUGUESE_BASED")));
            Constants.FILTERLANG.add(new Listitems("dz", "DZONGKHA", lang_check("DZONGKHA")));
            Constants.FILTERLANG.add(new Listitems("ky", "KYRGYZ", lang_check("KYRGYZ")));
            Constants.FILTERLANG.add(new Listitems("sg", "SANGO", lang_check("SANGO")));
            Constants.FILTERLANG.add(new Listitems("sw", "SWAHILI", lang_check("SWAHILI")));
            Constants.FILTERLANG.add(new Listitems("sd", "SINDHI", lang_check("SINDHI")));

            adapter = new ListViewAdapter(Filtering.this, Constants.FILTERLANG);
            filter_language.setAdapter(adapter);
            filter_language.setTextFilterEnabled(true);
        }

        public void listOfFilterLocation () {
            Constants.FILTERLOC.clear();
            //Constants.FILTERLOC.add(new Listitems("india_country_manual_parent", "India", loc_check("India")));

            Constants.FILTERLOC.add(new Listitems("afghanistan", "Afghanistan", loc_check("Afghanistan")));
            Constants.FILTERLOC.add(new Listitems("albania", "Albania", loc_check("Albania")));
            Constants.FILTERLOC.add(new Listitems("algeria", "Algeria", loc_check("Algeria")));
            Constants.FILTERLOC.add(new Listitems("ancient_egypt", "Ancient Egypt", loc_check("Ancient Egypt")));
            Constants.FILTERLOC.add(new Listitems("angola", "Angola", loc_check("Angola")));
            Constants.FILTERLOC.add(new Listitems("araucanía_region", "Araucanía Region", loc_check("Araucanía Region")));
            Constants.FILTERLOC.add(new Listitems("argentina", "Argentina", loc_check("Argentina")));
            Constants.FILTERLOC.add(new Listitems("armenia", "Armenia", loc_check("Armenia")));
            Constants.FILTERLOC.add(new Listitems("australia", "Australia", loc_check("Australia")));
            Constants.FILTERLOC.add(new Listitems("austria", "Austria", loc_check("Austria")));
            Constants.FILTERLOC.add(new Listitems("azerbaijan", "Azerbaijan", loc_check("Azerbaijan")));
            Constants.FILTERLOC.add(new Listitems("bangladesh", "Bangladesh", loc_check("Bangladesh")));
            Constants.FILTERLOC.add(new Listitems("barbados", "Barbados", loc_check("Barbados")));
            Constants.FILTERLOC.add(new Listitems("belarus", "Belarus", loc_check("Belarus")));
            Constants.FILTERLOC.add(new Listitems("belgium", "Belgium", loc_check("Belgium")));
            Constants.FILTERLOC.add(new Listitems("benin", "Benin", loc_check("Benin")));
            Constants.FILTERLOC.add(new Listitems("bolivia", "Bolivia", loc_check("Bolivia")));
            Constants.FILTERLOC.add(new Listitems("bosnia_(region)", "Bosnia (Region)", loc_check("Bosnia (Region)")));
            Constants.FILTERLOC.add(new Listitems("bosnia_and_herzegovina", "Bosnia And Herzegovina", loc_check("Bosnia And Herzegovina")));
            Constants.FILTERLOC.add(new Listitems("botswana", "Botswana", loc_check("Botswana")));
            Constants.FILTERLOC.add(new Listitems("brazil", "Brazil", loc_check("Brazil")));
            Constants.FILTERLOC.add(new Listitems("brunei", "Brunei", loc_check("Brunei")));
            Constants.FILTERLOC.add(new Listitems("bulgaria", "Bulgaria", loc_check("Bulgaria")));
            Constants.FILTERLOC.add(new Listitems("burma", "Burma", loc_check("Burma")));
            Constants.FILTERLOC.add(new Listitems("cambodia", "Cambodia", loc_check("Cambodia")));
            Constants.FILTERLOC.add(new Listitems("canada", "Canada", loc_check("Canada")));
            Constants.FILTERLOC.add(new Listitems("cape_verde", "Cape Verde", loc_check("Cape Verde")));
            Constants.FILTERLOC.add(new Listitems("chile", "Chile", loc_check("Chile")));
            Constants.FILTERLOC.add(new Listitems("China", "China", loc_check("China")));
            Constants.FILTERLOC.add(new Listitems("colombia", "Colombia", loc_check("Colombia")));
            Constants.FILTERLOC.add(new Listitems("costa_rica", "Costa Rica", loc_check("Costa Rica")));
            Constants.FILTERLOC.add(new Listitems("croatia", "Croatia", loc_check("Croatia")));
            Constants.FILTERLOC.add(new Listitems("cuba", "Cuba", loc_check("Cuba")));
            Constants.FILTERLOC.add(new Listitems("czech_republic", "Czech Republic", loc_check("Czech Republic")));
            Constants.FILTERLOC.add(new Listitems("côte_d'ivoire", "Côte D'Ivoire", loc_check("Côte D'Ivoire")));
            Constants.FILTERLOC.add(new Listitems("de_jure", "De Jure", loc_check("De Jure")));
            Constants.FILTERLOC.add(new Listitems("denmark", "Denmark", loc_check("Denmark")));
            Constants.FILTERLOC.add(new Listitems("djibouti", "Djibouti", loc_check("Djibouti")));
            Constants.FILTERLOC.add(new Listitems("dominican_republic", "Dominican Republic", loc_check("Dominican Republic")));
            Constants.FILTERLOC.add(new Listitems("ecuador", "Ecuador", loc_check("Ecuador")));
            Constants.FILTERLOC.add(new Listitems("egypt", "Egypt", loc_check("Egypt")));
            Constants.FILTERLOC.add(new Listitems("el_salvador", "El Salvador", loc_check("El Salvador")));
            Constants.FILTERLOC.add(new Listitems("estonia", "Estonia", loc_check("Estonia")));
            Constants.FILTERLOC.add(new Listitems("ethiopia", "Ethiopia", loc_check("Ethiopia")));
            Constants.FILTERLOC.add(new Listitems("fiji", "Fiji", loc_check("Fiji")));
            Constants.FILTERLOC.add(new Listitems("finland", "Finland", loc_check("Finland")));
            Constants.FILTERLOC.add(new Listitems("florida", "France", loc_check("France")));
            Constants.FILTERLOC.add(new Listitems("france", "Estonia", loc_check("Estonia")));
            Constants.FILTERLOC.add(new Listitems("georgia", "Georgia", loc_check("Georgia")));
            Constants.FILTERLOC.add(new Listitems("german_reich", "German Reich", loc_check("German Reich")));
            Constants.FILTERLOC.add(new Listitems("germany", "Germany", loc_check("Germany")));
            Constants.FILTERLOC.add(new Listitems("ghana", "Ghana", loc_check("Ghana")));
            Constants.FILTERLOC.add(new Listitems("global", "Global", loc_check("Global")));
            Constants.FILTERLOC.add(new Listitems("greece", "Greece", loc_check("Greece")));
            Constants.FILTERLOC.add(new Listitems("greenland", "Greenland", loc_check("Greenland")));
            Constants.FILTERLOC.add(new Listitems("grenada", "Grenada", loc_check("Grenada")));
            Constants.FILTERLOC.add(new Listitems("guam", "Guam", loc_check("Guam")));
            Constants.FILTERLOC.add(new Listitems("guatemala", "Guatemala", loc_check("Guatemala")));
            Constants.FILTERLOC.add(new Listitems("guinea", "Guinea", loc_check("Guinea")));
            Constants.FILTERLOC.add(new Listitems("honduras", "Honduras", loc_check("Honduras")));
            Constants.FILTERLOC.add(new Listitems("hungary", "Hungary", loc_check("Hungary")));
            Constants.FILTERLOC.add(new Listitems("iceland", "Iceland", loc_check("Iceland")));
            Constants.FILTERLOC.add(new Listitems("india", "India", loc_check("India")));
            Constants.FILTERLOC.add(new Listitems("indonesia", "Indonesia", loc_check("Indonesia")));
            Constants.FILTERLOC.add(new Listitems("iran", "Iran", loc_check("Iran")));
            Constants.FILTERLOC.add(new Listitems("iraq", "Iraq", loc_check("Iraq")));
            Constants.FILTERLOC.add(new Listitems("ireland", "Ireland", loc_check("Ireland")));
            Constants.FILTERLOC.add(new Listitems("israel", "Israel", loc_check("Israel")));
            Constants.FILTERLOC.add(new Listitems("italy", "Italy", loc_check("Italy")));
            Constants.FILTERLOC.add(new Listitems("jamaica", "Jamaica", loc_check("Jamaica")));
            Constants.FILTERLOC.add(new Listitems("japan", "Japan", loc_check("Japan")));
            Constants.FILTERLOC.add(new Listitems("jordan", "Jordan", loc_check("Jordan")));
            Constants.FILTERLOC.add(new Listitems("kazakhstan", "Kazakhstan", loc_check("Kazakhstan")));
            Constants.FILTERLOC.add(new Listitems("kenya", "Kenya", loc_check("Kenya")));
            Constants.FILTERLOC.add(new Listitems("korea", "Korea", loc_check("Korea")));
            Constants.FILTERLOC.add(new Listitems("Kosovo", "Kosovo", loc_check("Kosovo")));
            Constants.FILTERLOC.add(new Listitems("kuwait", "Kuwait", loc_check("Kuwait")));
            Constants.FILTERLOC.add(new Listitems("kyrgyzstan", "Kyrgyzstan", loc_check("Kyrgyzstan")));
            Constants.FILTERLOC.add(new Listitems("laos", "Laos", loc_check("Laos")));
            Constants.FILTERLOC.add(new Listitems("latvia", "Latvia", loc_check("Latvia")));
            Constants.FILTERLOC.add(new Listitems("lebanon", "Lebanon", loc_check("Lebanon")));
            Constants.FILTERLOC.add(new Listitems("liberia", "Liberia", loc_check("Liberia")));
            Constants.FILTERLOC.add(new Listitems("libya", "Libya", loc_check("Libya")));
            Constants.FILTERLOC.add(new Listitems("lithuania", "Lithuania", loc_check("Lithuania")));
            Constants.FILTERLOC.add(new Listitems("lutsk_raion", "Lutsk Raion", loc_check("Lutsk Raion")));
            Constants.FILTERLOC.add(new Listitems("macedonia", "Macedonia", loc_check("Macedonia")));
            Constants.FILTERLOC.add(new Listitems("madagascar", "Madagascar", loc_check("Madagascar")));
            Constants.FILTERLOC.add(new Listitems("malaysia", "Malaysia", loc_check("Malaysia")));
            Constants.FILTERLOC.add(new Listitems("mali", "Mali", loc_check("Mali")));
            Constants.FILTERLOC.add(new Listitems("malta", "Malta", loc_check("Malta")));
            Constants.FILTERLOC.add(new Listitems("mexico", "Mexico", loc_check("Mexico")));
            Constants.FILTERLOC.add(new Listitems("midway_island", "Midway Island", loc_check("Midway Island")));
            Constants.FILTERLOC.add(new Listitems("moldova", "Moldova", loc_check("Moldova")));
            Constants.FILTERLOC.add(new Listitems("mongolia", "Mongolia", loc_check("Mongolia")));
            Constants.FILTERLOC.add(new Listitems("montenegro", "Montenegro", loc_check("Montenegro")));
            Constants.FILTERLOC.add(new Listitems("morocco", "Morocco", loc_check("Morocco")));
            Constants.FILTERLOC.add(new Listitems("mozambique", "Mozambique", loc_check("Mozambique")));
            Constants.FILTERLOC.add(new Listitems("namibia", "Namibia", loc_check("Namibia")));
            Constants.FILTERLOC.add(new Listitems("nepal", "Nepal", loc_check("Nepal")));
            Constants.FILTERLOC.add(new Listitems("netherlands", "Netherlands", loc_check("Netherlands")));
            Constants.FILTERLOC.add(new Listitems("new_zealand", "New Zealand", loc_check("New Zealand")));
            Constants.FILTERLOC.add(new Listitems("newfoundland", "Newfoundland", loc_check("Newfoundland")));
            Constants.FILTERLOC.add(new Listitems("niue", "Niue", loc_check("Niue")));
            Constants.FILTERLOC.add(new Listitems("north_america", "North America", loc_check("North America")));
            Constants.FILTERLOC.add(new Listitems("north_korea", "North Korea", loc_check("North Korea")));
            Constants.FILTERLOC.add(new Listitems("norway", "Norway", loc_check("Norway")));
            Constants.FILTERLOC.add(new Listitems("o'higgins_region", "O'Higgins Region", loc_check("O'Higgins Region")));
            Constants.FILTERLOC.add(new Listitems("oman", "Oman", loc_check("Oman")));
            Constants.FILTERLOC.add(new Listitems("pakistan", "Pakistan", loc_check("Pakistan")));
            Constants.FILTERLOC.add(new Listitems("panama", "Panama", loc_check("Panama")));
            Constants.FILTERLOC.add(new Listitems("papua_new_guinea", "Papua New Guinea", loc_check("Papua New Guinea")));
            Constants.FILTERLOC.add(new Listitems("paraguay", "Paraguay", loc_check("Paraguay")));
            Constants.FILTERLOC.add(new Listitems("peru", "Peru", loc_check("Peru")));
            Constants.FILTERLOC.add(new Listitems("philippines", "Philippines", loc_check("Philippines")));
            Constants.FILTERLOC.add(new Listitems("portugal", "Portugal", loc_check("Portugal")));
            Constants.FILTERLOC.add(new Listitems("qatar", "Qatar", loc_check("Qatar")));
            Constants.FILTERLOC.add(new Listitems("republic_of_cape_verde", "Republic Of Cape Verde", loc_check("Republic Of Cape Verde")));
            Constants.FILTERLOC.add(new Listitems("republic_of_fiji", "Republic Of Fiji", loc_check("Republic Of Fiji")));
            Constants.FILTERLOC.add(new Listitems("republic_of_ireland", "Republic Of Ireland", loc_check("Republic Of Ireland")));
            Constants.FILTERLOC.add(new Listitems("republic_of_macedonia", "Republic Of Macedonia", loc_check("Republic Of Macedonia")));
            Constants.FILTERLOC.add(new Listitems("republic_of_the_marshall_islands", "Republic Of The Marshall Islands", loc_check("Republic Of The Marshall Islands")));
            Constants.FILTERLOC.add(new Listitems("romania", "Romania", loc_check("Romania")));
            Constants.FILTERLOC.add(new Listitems("russia", "Russia", loc_check("Russia")));
            Constants.FILTERLOC.add(new Listitems("saint_lucia", "Saint Lucia", loc_check("Saint Lucia")));
            Constants.FILTERLOC.add(new Listitems("saint_vincent_and_the_grenadines", "Saint Vincent And The Grenadines", loc_check("Saint Vincent And The Grenadines")));
            Constants.FILTERLOC.add(new Listitems("samoa", "Samoa", loc_check("Samoa")));
            Constants.FILTERLOC.add(new Listitems("saskatchewan", "Saskatchewan", loc_check("Saskatchewan")));
            Constants.FILTERLOC.add(new Listitems("saudi_arabia", "Saudi Arabia", loc_check("Saudi Arabia")));
            Constants.FILTERLOC.add(new Listitems("scotland", "Scotland", loc_check("Scotland")));
            Constants.FILTERLOC.add(new Listitems("seoul", "Seoul", loc_check("Seoul")));
            Constants.FILTERLOC.add(new Listitems("serbia", "Serbia", loc_check("Serbia")));
            Constants.FILTERLOC.add(new Listitems("singapore", "Singapore", loc_check("Singapore")));
            Constants.FILTERLOC.add(new Listitems("slovakia", "Slovakia", loc_check("Slovakia")));
            Constants.FILTERLOC.add(new Listitems("slovenia", "Slovenia", loc_check("Slovenia")));
            Constants.FILTERLOC.add(new Listitems("solomon_islands", "Solomon Islands", loc_check("Solomon Islands")));
            Constants.FILTERLOC.add(new Listitems("south_africa", "South Africa", loc_check("South Africa")));
            Constants.FILTERLOC.add(new Listitems("south_korea", "South Korea", loc_check("South Korea")));
            Constants.FILTERLOC.add(new Listitems("spain", "Spain", loc_check("Spain")));
            Constants.FILTERLOC.add(new Listitems("sri_lanka", "Sri Lanka", loc_check("Sri Lanka")));
            Constants.FILTERLOC.add(new Listitems("suriname", "Suriname", loc_check("Suriname")));
            Constants.FILTERLOC.add(new Listitems("sweden", "Sweden", loc_check("Sweden")));
            Constants.FILTERLOC.add(new Listitems("switzerland", "Switzerland", loc_check("Switzerland")));
            Constants.FILTERLOC.add(new Listitems("são_tomé_and_príncipe", "São Tomé And Príncipe", loc_check("São Tomé And Príncipe")));
            Constants.FILTERLOC.add(new Listitems("taiwan", "Taiwan", loc_check("Taiwan")));
            Constants.FILTERLOC.add(new Listitems("tanzania", "Tanzania", loc_check("Tanzania")));
            Constants.FILTERLOC.add(new Listitems("thailand", "Thailand", loc_check("Thailand")));
            Constants.FILTERLOC.add(new Listitems("the_democratic_republic_of_congo", "The Democratic Republic Of Congo", loc_check("The Democratic Republic Of Congo")));
            Constants.FILTERLOC.add(new Listitems("togo", "Togo", loc_check("Togo")));
            Constants.FILTERLOC.add(new Listitems("tonga", "Tonga", loc_check("Tonga")));
            Constants.FILTERLOC.add(new Listitems("trinidad_and_tobago", "Trinidad And Tobago", loc_check("Trinidad And Tobago")));
            Constants.FILTERLOC.add(new Listitems("tunisia", "Tunisia", loc_check("Tunisia")));
            Constants.FILTERLOC.add(new Listitems("turkey", "Turkey", loc_check("Turkey")));
            Constants.FILTERLOC.add(new Listitems("turkmenistan", "Turkmenistan", loc_check("Turkmenistan")));
            Constants.FILTERLOC.add(new Listitems("uae", "UAE", loc_check("UAE")));
            Constants.FILTERLOC.add(new Listitems("usa", "USA", loc_check("USA")));
            Constants.FILTERLOC.add(new Listitems("ukraine", "Ukraine", loc_check("Ukraine")));
            Constants.FILTERLOC.add(new Listitems("united_arab_emirates", "United Arab Emirates", loc_check("United Arab Emirates")));
            Constants.FILTERLOC.add(new Listitems("united_kingdom", "United Kingdom", loc_check("United Kingdom")));
            Constants.FILTERLOC.add(new Listitems("uruguay", "Uruguay", loc_check("Uruguay")));
            Constants.FILTERLOC.add(new Listitems("uzbekistan", "Uzbekistan", loc_check("Uzbekistan")));
            Constants.FILTERLOC.add(new Listitems("vanuatu", "Vanuatu", loc_check("Vanuatu")));
            Constants.FILTERLOC.add(new Listitems("venezuela", "Venezuela", loc_check("Venezuela")));
            Constants.FILTERLOC.add(new Listitems("vietnam", "Vietnam", loc_check("Vietnam")));
            Constants.FILTERLOC.add(new Listitems("yemen", "Yemen", loc_check("Yemen")));
            Constants.FILTERLOC.add(new Listitems("zambia", "Zambia", loc_check("Zambia")));
            Constants.FILTERLOC.add(new Listitems("zimbabwe", "Zimbabwe", loc_check("Zimbabwe")));
            Constants.FILTERLOC.add(new Listitems("hong_kong", "Hong Kong", loc_check("Hong Kong")));

            adapter = new ListViewAdapter(Filtering.this, Constants.FILTERLOC);
            filter_location.setAdapter(adapter);
            filter_location.setTextFilterEnabled(true);

        }


        public boolean sentiment_check (String sentiment){
            Log.i("Log_tag", "sentiment_selected size is" + Constants.sentiment_selected.size() + "sentiment is" + sentiment);
            if (Constants.sentiment_selected.size() > 0)
                if (Constants.sentiment_selected.contains(sentiment)) {
                    Log.i("Log_tag", "seniment  is" + true);
                    return true;
                }
            return false;
        }

        public boolean source_check (String source){
            Log.i("Log_tag", "sentiment_selected size is" + Constants.sources_selected.size() + "source is" + source);
            if (Constants.sources_selected.size() > 0)
                if (Constants.sources_selected.contains(source)) {
                    Log.i("Log_tag", "source  is" + true);
                    return true;
                }
            return false;
        }

        public boolean gender_check (String gender){
            Log.i("Log_tag", "sentiment_selected size is" + Constants.gender_selected.size() + "gender is" + gender);
            if (Constants.gender_selected.size() > 0)
                if (Constants.gender_selected.contains(gender)) {
                    Log.i("Log_tag", "gender is" + true);
                    return true;
                }
            return false;
        }

        public boolean lang_check (String lang){
            Log.i("Log_tag", "sentiment_selected size is" + Constants.lang_selected.size() + "lang is" + lang);
            if (Constants.lang_selected.size() > 0)
                if (Constants.lang_selected.contains(lang)) {
                    Log.i("Log_tag", "Lang  is" + true);
                    return true;
                } else {
                    if (lang.contains("English")) {
                        return true;
                    }
                }
            return false;
        }

        public boolean loc_check (String loc){
            Log.i("Log_tag", "sentiment_selected size is" + Constants.loc_selected.size() + "loc is" + loc);
            if (Constants.loc_selected.size() > 0)
                if (Constants.loc_selected.contains(loc)) {
                    Log.i("Log_tag", "Loc  is" + true);
                    return true;
                }
            return false;
        }

        public void ceratelists () {

            Iterator<Listitems> sourcelist = Constants.FILTERSOURSOURE.iterator();
            while (sourcelist.hasNext()) {
                Log.i("Log_tag", "source is" + sourcelist.next().getXtag());
                Constants.sourceslist.add(sourcelist.next().getXtag());
            }
        }
    }