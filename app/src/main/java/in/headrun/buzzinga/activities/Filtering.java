package in.headrun.buzzinga.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.BuzzingaApplication;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.adapters.FilterTitleAdapter;
import in.headrun.buzzinga.adapters.ListViewAdapter;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.doto.Listitems;
import in.headrun.buzzinga.utils.Utils;



/**
 * Created by headrun on 6/8/15.
 */
public class Filtering extends AppCompatActivity implements View.OnClickListener, Utils.setOnItemClickListner {

    public String TAG = Filtering.this.getClass().getSimpleName();

    @Bind(R.id.filter_titles)
    ListView filter_titles;
    @Bind(R.id.filter_items)
    ListView filter_items;

    @Bind(R.id.clearfilter)
    Button clearfilter;
    @Bind(R.id.applyfilter)
    Button applyfilter;

    @Bind(R.id.autosearch)
    EditText autosearch;

    String Sourcestatus = "";
    FilterStatus sel_source_items;

    ListViewAdapter list_adapter;

    FilterTitleAdapter titleadapter;
    int sel_title_pos = -1;

    public static List<String> sel_source_list;
    public static List<String> sel_sentiment_list;
    public static List<String> sel_gender_list;
    public static List<String> sel_loc_list;
    public static List<String> sel_lang_list;

    public static List<Listitems> loc_item_list;
    public static List<Listitems> lang_item_lsit;

    String[] filtertitles;

    public static int first_source, first_sentiment, first_gender, first_loc, first_lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sourcefilterlay);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.buzz_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Filters");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        
        Bundle params = new Bundle();
        params.putString("Open_Filter", "Filter");
        BuzzingaApplication.getmFirebaseAnalytics().logEvent("open_filter_event", params);
        BuzzingaApplication.getmFirebaseAnalytics().setAnalyticsCollectionEnabled(true);

        first_source = first_sentiment = first_gender = first_loc = first_lang = 0;

        clearfilter.setOnClickListener(this);
        applyfilter.setOnClickListener(this);

        sel_source_list = new ArrayList<>();
        sel_sentiment_list = new ArrayList<>();
        sel_gender_list = new ArrayList<>();
        sel_loc_list = new ArrayList<>();
        sel_lang_list = new ArrayList<>();

        loc_item_list = new ArrayList<>();
        lang_item_lsit = new ArrayList<>();

        filtertitles = getResources().getStringArray(R.array.filtertitles);
        final TypedArray filtertitleimages = getResources().obtainTypedArray(R.array.titleimages);

        Utils.add_query_data();
        titleadapter = new FilterTitleAdapter(Filtering.this, filtertitles, filtertitleimages);
        filter_titles.setAdapter(titleadapter);
        titleadapter.notifyDataSetChanged();
        filter_titles.clearFocus();

        filter_titles.post(new Runnable() {

            @Override
            public void run() {
                try {
                    if (filter_titles.getAdapter().getCount() > 0) {

                        View view = filter_titles.getChildAt(0);
                        sel_title_pos = 0;
                        view.setBackgroundColor(ContextCompat.getColor(Filtering.this, R.color.white_smoke));
                        TextView item = (TextView) view.findViewById(R.id.texttilte);
                        item.setTextColor(ContextCompat.getColor(Filtering.this, R.color.black));

                        filterselection(FilterStatus.SOURCES);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        filter_titles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < filter_titles.getAdapter().getCount(); i++) {

                    View item_view = filter_titles.getChildAt(i);
                    item_view.setBackgroundColor(0);
                    //   item_view.setBackgroundColor(ContextCompat.getColor(Filtering.this, R.color.grey));
                    TextView item = (TextView) item_view.findViewById(R.id.texttilte);
                    item.setTextColor(ContextCompat.getColor(Filtering.this, R.color.white));
                    //item.setTextColor(Color.);
                }

                view.setBackgroundColor(ContextCompat.getColor(Filtering.this, R.color.white_smoke));
                TextView item = (TextView) view.findViewById(R.id.texttilte);
                item.setTextColor(ContextCompat.getColor(Filtering.this, R.color.black));

                String filteritem = filter_titles.getItemAtPosition(position).toString().toUpperCase();
                sel_title_pos = position;
                FilterStatus filtering = FilterStatus.valueOf(filteritem);
                filterselection(filtering);
            }
        });


        autosearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = autosearch.getText().toString().toLowerCase();
                list_adapter.filter(text);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Filtering.this, MainActivity.class);
        i.putExtra(Constants.Intent_OPERATION, Constants.Intent_NOTHING);
        startActivity(i);
        this.overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);

    }


    ////set the adapter to listview
    public void FilterAdapter(List<Listitems> items, boolean search_enable) {

        if (search_enable)
            autosearch.setVisibility(View.VISIBLE);
        else
            autosearch.setVisibility(View.GONE);

        list_adapter = new ListViewAdapter(Filtering.this, items);
        list_adapter.setonitemclickListner(this);
        filter_items.setAdapter(list_adapter);
        filter_items.setTextFilterEnabled(true);

    }


    /*prepare the selected filter title list
    * @param enum of filter status title
    * */
    public void filterselection(FilterStatus filtering) {
        sel_source_items = null;

        switch (filtering) {

            case SOURCES:
                sel_source_items = FilterStatus.SOURCES;

                if (first_lang == 0 || Constants.FILTERSOURSOURE.size() <= 0) {
                    listOfFilterSources();
                }

                FilterAdapter(Constants.FILTERSOURSOURE, false);

                break;
            case SENTIMENT:
                sel_source_items = FilterStatus.SENTIMENT;

                if (first_sentiment == 0 || Constants.FILTERSENTIMENT.size() <= 0) {
                    listOfFilterSentiment();
                }

                FilterAdapter(Constants.FILTERSENTIMENT, false);
                break;
            case GENDER:
                sel_source_items = FilterStatus.GENDER;

                if (first_gender == 0 || Constants.FILTERGENDER.size() <= 0) {
                    listOfFilterGender();
                }

                FilterAdapter(Constants.FILTERGENDER, false);

                break;
            case LANGUAGE:
                sel_source_items = FilterStatus.LANGUAGE;
                autosearch.getText().clear();
                Sourcestatus = "lang";

                if (first_lang == 0 || Constants.FILTERLANG.size() <= 0) {
                    listOfFilterLang();
                }

                FilterAdapter(Constants.FILTERLANG, true);

                break;
            case LOCATION:
                sel_source_items = FilterStatus.LOCATION;
                autosearch.getText().clear();
                Sourcestatus = "loc";

                if (first_loc == 0 || Constants.FILTERLOC.size() <= 0) {
                    listOfFilterLocation();
                }

                FilterAdapter(Constants.FILTERLOC, true);

                break;
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.clearfilter) {

            clearfilters();

        } else if (v.getId() == R.id.applyfilter) {

            if (first_source != 0)
                sourcequery();

            if (first_sentiment != 0)
                sentimentquery();

            if (first_gender != 0)
                genderquery();

            if (first_loc != 0)
                locquery();

            if (first_lang != 0)
                langquery();


            Bundle params = new Bundle();
            params.putString("apply_filter", "filter");
            BuzzingaApplication.getmFirebaseAnalytics().logEvent("apply_filter_event", params);
            BuzzingaApplication.getmFirebaseAnalytics().setAnalyticsCollectionEnabled(true);

            Intent i = new Intent(Filtering.this, MainActivity.class);
            i.putExtra(Constants.Intent_OPERATION, Constants.Intent_TRACK);
            startActivity(i);
            this.overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);

        }
    }

    /***
     * prepare the source list  *
     */
    public void listOfFilterSources() {
        Constants.FILTERSOURSOURE.clear();

        Utils.source_xtags();

        for (Map.Entry<String, String> entry : Constants.sources_list.entrySet()) {
            String value = entry.getKey().toUpperCase();
            Constants.FILTERSOURSOURE.add(new Listitems(entry.getKey(), value, source_check(value)));
        }

        sortlist(Constants.FILTERSOURSOURE);

    }

    /***
     * prepare the sentiment list
     ****/
    public void listOfFilterSentiment() {
        Constants.FILTERSENTIMENT.clear();

        Log.i(TAG, "get filter sentiment data");

        Utils.sentiment_xtags();

        for (Map.Entry<String, String> entry : Constants.sentiment_map.entrySet()) {
            String value = entry.getKey().toUpperCase();
            Constants.FILTERSENTIMENT.add(new Listitems(entry.getKey(), value, sentiment_check(value)));
        }
        sortlist(Constants.FILTERSENTIMENT);


    }

    /***
     * prepare the gender list
     */
    public void listOfFilterGender() {
        Constants.FILTERGENDER.clear();

        Utils.genter_xtags();

        for (Map.Entry<String, String> gendervalue : Constants.gender_map.entrySet()) {
            String value = gendervalue.getKey().toUpperCase();
            Constants.FILTERGENDER.add(new Listitems(gendervalue.getKey(), value, gender_check(value)));
        }
        sortlist(Constants.FILTERGENDER);


    }

    ////prepare the  Language list
    public void listOfFilterLang() {

        Constants.FILTERLANG.clear();

        Constants.FILTERLANG.add(new Listitems("gv", "MANX", lang_check("gv")));
        Constants.FILTERLANG.add(new Listitems("gu", "GUJARATI", lang_check("gu")));
        Constants.FILTERLANG.add(new Listitems("sco", "SCOTS", lang_check("sco")));
        Constants.FILTERLANG.add(new Listitems("gd", "SCOTS_GAELIC", lang_check("gd")));
        Constants.FILTERLANG.add(new Listitems("ga", "IRISH", lang_check("ga")));
        Constants.FILTERLANG.add(new Listitems("gn", "GUARANI", lang_check("gn")));
        Constants.FILTERLANG.add(new Listitems("gl", "GALICIAN", lang_check("gl")));
        Constants.FILTERLANG.add(new Listitems("lg", "GANDA", lang_check("lg")));
        Constants.FILTERLANG.add(new Listitems("lb", "LUXEMBOURGISH", lang_check("lb")));
        Constants.FILTERLANG.add(new Listitems("la", "LATIN", lang_check("la")));
        Constants.FILTERLANG.add(new Listitems("ln", "LINGALA", lang_check("ln")));
        Constants.FILTERLANG.add(new Listitems("tw", "TWI", lang_check("tw")));
        Constants.FILTERLANG.add(new Listitems("tt", "TATAR", lang_check("tt")));
        Constants.FILTERLANG.add(new Listitems("tr", "TURKISH", lang_check("tr")));
        Constants.FILTERLANG.add(new Listitems("ts", "TSONGA", lang_check("ts")));
        Constants.FILTERLANG.add(new Listitems("lv", "LATVIAN", lang_check("lv")));
        Constants.FILTERLANG.add(new Listitems("to", "TONGA", lang_check("to")));
        Constants.FILTERLANG.add(new Listitems("lt", "LITHUANIAN", lang_check("lt")));
        Constants.FILTERLANG.add(new Listitems("tk", "TURKMEN", lang_check("tk")));
        Constants.FILTERLANG.add(new Listitems("th", "THAI", lang_check("th")));
        Constants.FILTERLANG.add(new Listitems("ti", "TIGRINYA", lang_check("ti")));
        Constants.FILTERLANG.add(new Listitems("tg", "TAJIK", lang_check("tg")));
        Constants.FILTERLANG.add(new Listitems("te", "TELUGU", lang_check("te")));
        Constants.FILTERLANG.add(new Listitems("fil", "TAGALOG", lang_check("fil")));
        Constants.FILTERLANG.add(new Listitems("ta", "TAMIL", lang_check("ta")));
        Constants.FILTERLANG.add(new Listitems("yi", "YIDDISH", lang_check("yi")));
        Constants.FILTERLANG.add(new Listitems("yo", "YORUBA", lang_check("yo")));
        Constants.FILTERLANG.add(new Listitems("de", "GERMAN", lang_check("de")));
        Constants.FILTERLANG.add(new Listitems("da", "DANISH", lang_check("da")));
        Constants.FILTERLANG.add(new Listitems("-pt-BR", "PORTUGUESE_B", lang_check("-pt-BR")));
        Constants.FILTERLANG.add(new Listitems("dv", "DHIVEHI", lang_check("dv")));
        Constants.FILTERLANG.add(new Listitems("sr-ME", "MONTENEGRIN", lang_check("sr-ME")));
        Constants.FILTERLANG.add(new Listitems("crp", "CREOLES_AND_PIDGINS_ OTHER", lang_check("crp")));
        Constants.FILTERLANG.add(new Listitems("qu", "QUECHUA", lang_check("qu")));
        Constants.FILTERLANG.add(new Listitems("cpf", "CREOLES_AND_PIDGINS_ FRENCH_BASED", lang_check("cpf")));
        Constants.FILTERLANG.add(new Listitems("ut", "TG_UNKNOWN_LANGUAGE", lang_check("ut")));
        Constants.FILTERLANG.add(new Listitems("el", "GREEK", lang_check("el")));
        Constants.FILTERLANG.add(new Listitems("eo", "ESPERANTO", lang_check("eo")));
        Constants.FILTERLANG.add(new Listitems("en", "ENGLISH", lang_check("en")));
        Constants.FILTERLANG.add(new Listitems("zh", "CHINESE", lang_check("zh")));
        Constants.FILTERLANG.add(new Listitems("bo", "TIBETAN", lang_check("bo")));
        Constants.FILTERLANG.add(new Listitems("za", "ZHUANG", lang_check("za")));
        Constants.FILTERLANG.add(new Listitems("eu", "BASQUE", lang_check("eu")));
        Constants.FILTERLANG.add(new Listitems("zu", "ZULU", lang_check("zu")));
        Constants.FILTERLANG.add(new Listitems("cpe", "CREOLES_AND_PIDGINS_ ENGLISH_BASED", lang_check("cpe")));
        Constants.FILTERLANG.add(new Listitems("es", "SPANISH", lang_check("es")));
        Constants.FILTERLANG.add(new Listitems("ba", "BASHKIR", lang_check("BASHKIR")));
        Constants.FILTERLANG.add(new Listitems("ru", "RUSSIAN", lang_check("ru")));
        Constants.FILTERLANG.add(new Listitems("rw", "KINYARWANDA", lang_check("rw")));
        Constants.FILTERLANG.add(new Listitems("kl", "GREENLANDIC", lang_check("kl")));
        Constants.FILTERLANG.add(new Listitems("rm", "RHAETO_ROMANCE", lang_check("rm")));
        Constants.FILTERLANG.add(new Listitems("rn", "RUNDI", lang_check("rn")));
        Constants.FILTERLANG.add(new Listitems("ro", "ROMANIAN", lang_check("ro")));
        Constants.FILTERLANG.add(new Listitems("be", "BELARUSIAN", lang_check("be")));
        Constants.FILTERLANG.add(new Listitems("bg", "BULGARIAN", lang_check("bg")));
        Constants.FILTERLANG.add(new Listitems("uk", "UKRAINIAN", lang_check("uk")));
        Constants.FILTERLANG.add(new Listitems("wo", "WOLOF", lang_check("wo")));
        Constants.FILTERLANG.add(new Listitems("bn", "BENGALI", lang_check("bn")));
        Constants.FILTERLANG.add(new Listitems("jw", "JAVANESE", lang_check("jw")));
        Constants.FILTERLANG.add(new Listitems("bh", "BIHARI", lang_check("bh")));
        Constants.FILTERLANG.add(new Listitems("bi", "BISLAMA", lang_check("bi")));
        Constants.FILTERLANG.add(new Listitems("br", "BRETON", lang_check("br")));
        Constants.FILTERLANG.add(new Listitems("bs", "BOSNIAN", lang_check("bs")));
        Constants.FILTERLANG.add(new Listitems("ja", "JAPANESE", lang_check("ja")));
        Constants.FILTERLANG.add(new Listitems("om", "OROMO", lang_check("om")));
        Constants.FILTERLANG.add(new Listitems("syr", "SYRIAC", lang_check("syr")));
        Constants.FILTERLANG.add(new Listitems("pt-PT", "PORTUGUESE_P", lang_check("pt-PT")));
        Constants.FILTERLANG.add(new Listitems("oc", "OCCITAN", lang_check("oc")));
        Constants.FILTERLANG.add(new Listitems("sit-NP", "LIMBU", lang_check("sit-NP")));
        Constants.FILTERLANG.add(new Listitems("lo", "LAOTHIAN", lang_check("lo")));
        Constants.FILTERLANG.add(new Listitems("or", "ORIYA", lang_check("or")));
        Constants.FILTERLANG.add(new Listitems("xh", "XHOSA", lang_check("xh")));
        Constants.FILTERLANG.add(new Listitems("mr", "MARATHI", lang_check("mr")));
        Constants.FILTERLANG.add(new Listitems("co", "CORSICAN", lang_check("co")));
        Constants.FILTERLANG.add(new Listitems("ca", "CATALAN", lang_check("ca")));
        Constants.FILTERLANG.add(new Listitems("cy", "WELSH", lang_check("cy")));
        Constants.FILTERLANG.add(new Listitems("cs", "CZECH", lang_check("cs")));
        Constants.FILTERLANG.add(new Listitems("ps", "PASHTO", lang_check("ps")));
        Constants.FILTERLANG.add(new Listitems("pt", "PORTUGUESE", lang_check("pt")));
        Constants.FILTERLANG.add(new Listitems("chr", "CHEROKEE", lang_check("chr")));
        Constants.FILTERLANG.add(new Listitems("pa", "PUNJABI", lang_check("pa")));
        Constants.FILTERLANG.add(new Listitems("pl", "POLISH", lang_check("pl")));
        Constants.FILTERLANG.add(new Listitems("hy", "ARMENIAN", lang_check("hy")));
        Constants.FILTERLANG.add(new Listitems("hr", "CROATIAN", lang_check("hr")));
        Constants.FILTERLANG.add(new Listitems("iu", "INUKTITUT", lang_check("iu")));
        Constants.FILTERLANG.add(new Listitems("ht", "HAITIAN_CREOLE", lang_check("ht")));
        Constants.FILTERLANG.add(new Listitems("hu", "HUNGARIAN", lang_check("hu")));
        Constants.FILTERLANG.add(new Listitems("hi", "HINDI", lang_check("hi")));
        Constants.FILTERLANG.add(new Listitems("ha", "HAUSA", lang_check("ha")));
        Constants.FILTERLANG.add(new Listitems("he", "HEBREW", lang_check("he")));
        Constants.FILTERLANG.add(new Listitems("mg", "MALAGASY", lang_check("mg")));
        Constants.FILTERLANG.add(new Listitems("uz", "UZBEK", lang_check("uz")));
        Constants.FILTERLANG.add(new Listitems("ml", "MALAYALAM", lang_check("ml")));
        Constants.FILTERLANG.add(new Listitems("mo", "MOLDAVIAN", lang_check("mo")));
        Constants.FILTERLANG.add(new Listitems("mn", "MONGOLIAN", lang_check("mn")));
        Constants.FILTERLANG.add(new Listitems("mi", "MAORI", lang_check("mi")));
        Constants.FILTERLANG.add(new Listitems("ik", "INUPIAK", lang_check("ik")));
        Constants.FILTERLANG.add(new Listitems("mk", "MACEDONIAN", lang_check("mk")));
        Constants.FILTERLANG.add(new Listitems("ur", "URDU", lang_check("ur")));
        Constants.FILTERLANG.add(new Listitems("mt", "MALTESE", lang_check("mt")));
        Constants.FILTERLANG.add(new Listitems("un", "UNKNOWN", lang_check("un")));
        Constants.FILTERLANG.add(new Listitems("ms", "MALAY", lang_check("ms")));
        Constants.FILTERLANG.add(new Listitems("sr", "SERBIAN", lang_check("sr")));
        Constants.FILTERLANG.add(new Listitems("ug", "UIGHUR", lang_check("ug")));
        Constants.FILTERLANG.add(new Listitems("my", "BURMESE", lang_check("my")));
        Constants.FILTERLANG.add(new Listitems("aa", "AFAR", lang_check("aa")));
        Constants.FILTERLANG.add(new Listitems("ab", "ABKHAZIAN", lang_check("ab")));
        Constants.FILTERLANG.add(new Listitems("ss", "SISWANT", lang_check("ss")));
        Constants.FILTERLANG.add(new Listitems("af", "AFRIKAANS", lang_check("af")));
        Constants.FILTERLANG.add(new Listitems("tn", "TSWANA", lang_check("tn")));
        Constants.FILTERLANG.add(new Listitems("vi", "VIETNAMESE", lang_check("vi")));
        Constants.FILTERLANG.add(new Listitems("is", "ICELANDIC", lang_check("is")));
        Constants.FILTERLANG.add(new Listitems("am", "AMHARIC", lang_check("am")));
        Constants.FILTERLANG.add(new Listitems("it", "ITALIAN", lang_check("it")));
        Constants.FILTERLANG.add(new Listitems("sv", "SWEDISH", lang_check("sv")));
        Constants.FILTERLANG.add(new Listitems("ay", "AYMARA", lang_check("ay")));
        Constants.FILTERLANG.add(new Listitems("as", "ASSAMESE", lang_check("as")));
        Constants.FILTERLANG.add(new Listitems("ar", "ARABIC", lang_check("ar")));
        Constants.FILTERLANG.add(new Listitems("km", "KHMER", lang_check("km")));
        Constants.FILTERLANG.add(new Listitems("et", "ESTONIAN", lang_check("et")));
        Constants.FILTERLANG.add(new Listitems("ia", "INTERLINGUA", lang_check("ia")));
        Constants.FILTERLANG.add(new Listitems("kha", "KHASI", lang_check("kha")));
        Constants.FILTERLANG.add(new Listitems("az", "AZERBAIJANI", lang_check("az")));
        Constants.FILTERLANG.add(new Listitems("ie", "INTERLINGUE", lang_check("ie")));
        Constants.FILTERLANG.add(new Listitems("id", "INDONESIAN", lang_check("id")));
        Constants.FILTERLANG.add(new Listitems("ks", "KASHMIRI", lang_check("ks")));
        Constants.FILTERLANG.add(new Listitems("nl", "DUTCH", lang_check("nl")));
        Constants.FILTERLANG.add(new Listitems("nn", "NORWEGIAN_N", lang_check("nn")));
        Constants.FILTERLANG.add(new Listitems("na", "NAURU", lang_check("na")));
        Constants.FILTERLANG.add(new Listitems("nb", "NORWEGIAN", lang_check("nb")));
        Constants.FILTERLANG.add(new Listitems("ne", "NEPALI", lang_check("ne")));
        Constants.FILTERLANG.add(new Listitems("vo", "VOLAPUK", lang_check("vo")));
        Constants.FILTERLANG.add(new Listitems("fr", "FRENCH", lang_check("fr")));
        Constants.FILTERLANG.add(new Listitems("sm", "SAMOAN", lang_check("sm")));
        Constants.FILTERLANG.add(new Listitems("fy", "FRISIAN", lang_check("fy")));
        Constants.FILTERLANG.add(new Listitems("fa", "PERSIAN", lang_check("fa")));
        Constants.FILTERLANG.add(new Listitems("fi", "FINNISH", lang_check("fi")));
        Constants.FILTERLANG.add(new Listitems("fj", "FIJIAN", lang_check("fj")));
        Constants.FILTERLANG.add(new Listitems("sa", "SANSKRIT", lang_check("sa")));
        Constants.FILTERLANG.add(new Listitems("fo", "FAROESE", lang_check("fo")));
        Constants.FILTERLANG.add(new Listitems("ka", "GEORGIAN", lang_check("ka")));
        Constants.FILTERLANG.add(new Listitems("kk", "KAZAKH", lang_check("kk")));
        Constants.FILTERLANG.add(new Listitems("zh-TW", "CHINESET", lang_check("zh-TW")));
        Constants.FILTERLANG.add(new Listitems("sq", "ALBANIAN", lang_check("sq")));
        Constants.FILTERLANG.add(new Listitems("ko", "KOREAN", lang_check("ko")));
        Constants.FILTERLANG.add(new Listitems("kn", "KANNADA", lang_check("kn")));
        Constants.FILTERLANG.add(new Listitems("su", "SUNDANESE", lang_check("su")));
        Constants.FILTERLANG.add(new Listitems("st", "SESOTHO", lang_check("st")));
        Constants.FILTERLANG.add(new Listitems("sk", "SLOVAK", lang_check("sk")));
        Constants.FILTERLANG.add(new Listitems("si", "SINHALESE", lang_check("si")));
        Constants.FILTERLANG.add(new Listitems("sh", "SERBO_CROATIAN", lang_check("sh")));
        Constants.FILTERLANG.add(new Listitems("so", "SOMALI", lang_check("so")));
        Constants.FILTERLANG.add(new Listitems("sn", "SHONA", lang_check("sn")));
        Constants.FILTERLANG.add(new Listitems("ku", "KURDISH", lang_check("ku")));
        Constants.FILTERLANG.add(new Listitems("sl", "SLOVENIAN", lang_check("sl")));
        Constants.FILTERLANG.add(new Listitems("cpp", "CREOLES_AND_PIDGINS _PORTUGUESE_BASED", lang_check("cpp")));
        Constants.FILTERLANG.add(new Listitems("dz", "DZONGKHA", lang_check("dz")));
        Constants.FILTERLANG.add(new Listitems("ky", "KYRGYZ", lang_check("ky")));
        Constants.FILTERLANG.add(new Listitems("sg", "SANGO", lang_check("sg")));
        Constants.FILTERLANG.add(new Listitems("sw", "SWAHILI", lang_check("sw")));
        Constants.FILTERLANG.add(new Listitems("sd", "SINDHI", lang_check("sd")));

        sortlist(Constants.FILTERLANG);
//        fillAdapter(Constants.FILTERLANG, true, sel_lang_array, first_lang, FilterStatus.LANGUAGE);

        lang_item_lsit.addAll(Constants.FILTERLANG);

    }

    ////prepare the  Location list
    public void listOfFilterLocation() {

        Constants.FILTERLOC.clear();

        Constants.FILTERLOC.add(new Listitems("afghanistan", "Afghanistan", loc_check("afghanistan")));
        Constants.FILTERLOC.add(new Listitems("albania", "Albania", loc_check("albania")));
        Constants.FILTERLOC.add(new Listitems("algeria", "Algeria", loc_check("algeria")));
        Constants.FILTERLOC.add(new Listitems("ancient_egypt", "Ancient Egypt", loc_check("ancient_egypt")));
        Constants.FILTERLOC.add(new Listitems("angola", "Angola", loc_check("angola")));
        Constants.FILTERLOC.add(new Listitems("araucanía_region", "Araucanía Region", loc_check("araucanía_region")));
        Constants.FILTERLOC.add(new Listitems("argentina", "Argentina", loc_check("argentina")));
        Constants.FILTERLOC.add(new Listitems("armenia", "Armenia", loc_check("armenia")));
        Constants.FILTERLOC.add(new Listitems("australia", "Australia", loc_check("australia")));
        Constants.FILTERLOC.add(new Listitems("austria", "Austria", loc_check("austria")));
        Constants.FILTERLOC.add(new Listitems("azerbaijan", "Azerbaijan", loc_check("azerbaijan")));
        Constants.FILTERLOC.add(new Listitems("bangladesh", "Bangladesh", loc_check("bangladesh")));
        Constants.FILTERLOC.add(new Listitems("barbados", "Barbados", loc_check("barbados")));
        Constants.FILTERLOC.add(new Listitems("belarus", "Belarus", loc_check("belarus")));
        Constants.FILTERLOC.add(new Listitems("belgium", "Belgium", loc_check("belgium")));
        Constants.FILTERLOC.add(new Listitems("benin", "Benin", loc_check("benin")));
        Constants.FILTERLOC.add(new Listitems("bolivia", "Bolivia", loc_check("bolivia")));
        Constants.FILTERLOC.add(new Listitems("bosnia_(region)", "Bosnia (Region)", loc_check("bosnia_(region)")));
        Constants.FILTERLOC.add(new Listitems("bosnia_and_herzegovina", "Bosnia And Herzegovina", loc_check("bosnia_and_herzegovina")));
        Constants.FILTERLOC.add(new Listitems("botswana", "Botswana", loc_check("botswana")));
        Constants.FILTERLOC.add(new Listitems("brazil", "Brazil", loc_check("brazil")));
        Constants.FILTERLOC.add(new Listitems("brunei", "Brunei", loc_check("brunei")));
        Constants.FILTERLOC.add(new Listitems("bulgaria", "Bulgaria", loc_check("bulgaria")));
        Constants.FILTERLOC.add(new Listitems("burma", "Burma", loc_check("burma")));
        Constants.FILTERLOC.add(new Listitems("cambodia", "Cambodia", loc_check("cambodia")));
        Constants.FILTERLOC.add(new Listitems("canada", "Canada", loc_check("canada")));
        Constants.FILTERLOC.add(new Listitems("cape_verde", "Cape Verde", loc_check("cape_verde")));
        Constants.FILTERLOC.add(new Listitems("chile", "Chile", loc_check("chile")));
        Constants.FILTERLOC.add(new Listitems("China", "China", loc_check("China")));
        Constants.FILTERLOC.add(new Listitems("colombia", "Colombia", loc_check("colombia")));
        Constants.FILTERLOC.add(new Listitems("costa_rica", "Costa Rica", loc_check("costa_rica")));
        Constants.FILTERLOC.add(new Listitems("croatia", "Croatia", loc_check("croatia")));
        Constants.FILTERLOC.add(new Listitems("cuba", "Cuba", loc_check("cuba")));
        Constants.FILTERLOC.add(new Listitems("czech_republic", "Czech Republic", loc_check("czech_republic")));
        Constants.FILTERLOC.add(new Listitems("côte_d'ivoire", "Côte D'Ivoire", loc_check("côte_d'ivoire")));
        Constants.FILTERLOC.add(new Listitems("de_jure", "De Jure", loc_check("de_jure")));
        Constants.FILTERLOC.add(new Listitems("denmark", "Denmark", loc_check("denmark")));
        Constants.FILTERLOC.add(new Listitems("djibouti", "Djibouti", loc_check("djibouti")));
        Constants.FILTERLOC.add(new Listitems("dominican_republic", "Dominican Republic", loc_check("dominican_republic")));
        Constants.FILTERLOC.add(new Listitems("ecuador", "Ecuador", loc_check("ecuador")));
        Constants.FILTERLOC.add(new Listitems("egypt", "Egypt", loc_check("egypt")));
        Constants.FILTERLOC.add(new Listitems("el_salvador", "El Salvador", loc_check("el_salvador")));
        Constants.FILTERLOC.add(new Listitems("estonia", "Estonia", loc_check("estonia")));
        Constants.FILTERLOC.add(new Listitems("ethiopia", "Ethiopia", loc_check("ethiopia")));
        Constants.FILTERLOC.add(new Listitems("fiji", "Fiji", loc_check("fiji")));
        Constants.FILTERLOC.add(new Listitems("finland", "Finland", loc_check("finland")));
        Constants.FILTERLOC.add(new Listitems("florida", "Florida", loc_check("florida")));
        Constants.FILTERLOC.add(new Listitems("france", "France", loc_check("france")));

        Constants.FILTERLOC.add(new Listitems("georgia", "Georgia", loc_check("georgia")));
        Constants.FILTERLOC.add(new Listitems("german_reich", "German Reich", loc_check("german_reich")));
        Constants.FILTERLOC.add(new Listitems("germany", "Germany", loc_check("germany")));
        Constants.FILTERLOC.add(new Listitems("ghana", "Ghana", loc_check("ghana")));
        Constants.FILTERLOC.add(new Listitems("global", "Global", loc_check("global")));
        Constants.FILTERLOC.add(new Listitems("greece", "Greece", loc_check("greece")));
        Constants.FILTERLOC.add(new Listitems("greenland", "Greenland", loc_check("greenland")));
        Constants.FILTERLOC.add(new Listitems("grenada", "Grenada", loc_check("grenada")));

        Constants.FILTERLOC.add(new Listitems("guam", "Guam", loc_check("guam")));
        Constants.FILTERLOC.add(new Listitems("guatemala", "Guatemala", loc_check("guatemala")));
        Constants.FILTERLOC.add(new Listitems("guinea", "Guinea", loc_check("guinea")));
        Constants.FILTERLOC.add(new Listitems("honduras", "Honduras", loc_check("honduras")));
        Constants.FILTERLOC.add(new Listitems("hungary", "Hungary", loc_check("hungary")));
        Constants.FILTERLOC.add(new Listitems("iceland", "Iceland", loc_check("iceland")));
        Constants.FILTERLOC.add(new Listitems("india", "India", loc_check("india")));
        Constants.FILTERLOC.add(new Listitems("indonesia", "Indonesia", loc_check("indonesia")));
        Constants.FILTERLOC.add(new Listitems("iran", "Iran", loc_check("iran")));
        Constants.FILTERLOC.add(new Listitems("iraq", "Iraq", loc_check("iraq")));
        Constants.FILTERLOC.add(new Listitems("ireland", "Ireland", loc_check("ireland")));
        Constants.FILTERLOC.add(new Listitems("israel", "Israel", loc_check("israel")));
        Constants.FILTERLOC.add(new Listitems("italy", "Italy", loc_check("italy")));
        Constants.FILTERLOC.add(new Listitems("jamaica", "Jamaica", loc_check("jamaica")));
        Constants.FILTERLOC.add(new Listitems("japan", "Japan", loc_check("japan")));
        Constants.FILTERLOC.add(new Listitems("jordan", "Jordan", loc_check("jordan")));
        Constants.FILTERLOC.add(new Listitems("kazakhstan", "Kazakhstan", loc_check("kazakhstan")));
        Constants.FILTERLOC.add(new Listitems("kenya", "Kenya", loc_check("kenya")));
        Constants.FILTERLOC.add(new Listitems("korea", "Korea", loc_check("korea")));
        Constants.FILTERLOC.add(new Listitems("Kosovo", "Kosovo", loc_check("Kosovo")));
        Constants.FILTERLOC.add(new Listitems("kuwait", "Kuwait", loc_check("kuwait")));
        Constants.FILTERLOC.add(new Listitems("kyrgyzstan", "Kyrgyzstan", loc_check("kyrgyzstan")));
        Constants.FILTERLOC.add(new Listitems("laos", "Laos", loc_check("laos")));
        Constants.FILTERLOC.add(new Listitems("latvia", "Latvia", loc_check("latvia")));
        Constants.FILTERLOC.add(new Listitems("lebanon", "Lebanon", loc_check("lebanon")));
        Constants.FILTERLOC.add(new Listitems("liberia", "Liberia", loc_check("liberia")));
        Constants.FILTERLOC.add(new Listitems("libya", "Libya", loc_check("libya")));
        Constants.FILTERLOC.add(new Listitems("lithuania", "Lithuania", loc_check("lithuania")));
        Constants.FILTERLOC.add(new Listitems("lutsk_raion", "Lutsk Raion", loc_check("lutsk_raion")));
        Constants.FILTERLOC.add(new Listitems("macedonia", "Macedonia", loc_check("macedonia")));
        Constants.FILTERLOC.add(new Listitems("madagascar", "Madagascar", loc_check("madagascar")));
        Constants.FILTERLOC.add(new Listitems("malaysia", "Malaysia", loc_check("malaysia")));
        Constants.FILTERLOC.add(new Listitems("mali", "Mali", loc_check("mali")));
        Constants.FILTERLOC.add(new Listitems("malta", "Malta", loc_check("malta")));
        Constants.FILTERLOC.add(new Listitems("mexico", "Mexico", loc_check("mexico")));
        Constants.FILTERLOC.add(new Listitems("midway_island", "Midway Island", loc_check("midway_island")));
        Constants.FILTERLOC.add(new Listitems("moldova", "Moldova", loc_check("moldova")));
        Constants.FILTERLOC.add(new Listitems("mongolia", "Mongolia", loc_check("Mongolia")));
        Constants.FILTERLOC.add(new Listitems("montenegro", "Montenegro", loc_check("montenegro")));
        Constants.FILTERLOC.add(new Listitems("morocco", "Morocco", loc_check("morocco")));
        Constants.FILTERLOC.add(new Listitems("mozambique", "Mozambique", loc_check("mozambique")));
        Constants.FILTERLOC.add(new Listitems("namibia", "Namibia", loc_check("namibia")));
        Constants.FILTERLOC.add(new Listitems("nepal", "Nepal", loc_check("nepal")));
        Constants.FILTERLOC.add(new Listitems("netherlands", "Netherlands", loc_check("netherlands")));
        Constants.FILTERLOC.add(new Listitems("new_zealand", "New Zealand", loc_check("new_zealand")));
        Constants.FILTERLOC.add(new Listitems("newfoundland", "Newfoundland", loc_check("newfoundland")));
        Constants.FILTERLOC.add(new Listitems("niue", "Niue", loc_check("niue")));
        Constants.FILTERLOC.add(new Listitems("north_america", "North America", loc_check("north_america")));
        Constants.FILTERLOC.add(new Listitems("north_korea", "North Korea", loc_check("north_korea")));
        Constants.FILTERLOC.add(new Listitems("norway", "Norway", loc_check("Norway")));
        Constants.FILTERLOC.add(new Listitems("o'higgins_region", "O'Higgins Region", loc_check("o'higgins_region")));
        Constants.FILTERLOC.add(new Listitems("oman", "Oman", loc_check("oman")));
        Constants.FILTERLOC.add(new Listitems("pakistan", "Pakistan", loc_check("pakistan")));
        Constants.FILTERLOC.add(new Listitems("panama", "Panama", loc_check("panama")));
        Constants.FILTERLOC.add(new Listitems("papua_new_guinea", "Papua New Guinea", loc_check("papua_new_guinea")));
        Constants.FILTERLOC.add(new Listitems("paraguay", "Paraguay", loc_check("paraguay")));
        Constants.FILTERLOC.add(new Listitems("peru", "Peru", loc_check("peru")));
        Constants.FILTERLOC.add(new Listitems("philippines", "Philippines", loc_check("philippines")));
        Constants.FILTERLOC.add(new Listitems("portugal", "Portugal", loc_check("portugal")));
        Constants.FILTERLOC.add(new Listitems("qatar", "Qatar", loc_check("qatar")));
        Constants.FILTERLOC.add(new Listitems("republic_of_cape_verde", "Republic Of Cape Verde", loc_check("republic_of_cape_verde")));
        Constants.FILTERLOC.add(new Listitems("republic_of_fiji", "Republic Of Fiji", loc_check("republic_of_fiji")));
        Constants.FILTERLOC.add(new Listitems("republic_of_ireland", "Republic Of Ireland", loc_check("Republic Of Ireland")));
        Constants.FILTERLOC.add(new Listitems("republic_of_macedonia", "Republic Of Macedonia", loc_check("republic_of_macedonia")));
        Constants.FILTERLOC.add(new Listitems("republic_of_the_marshall_islands", "Republic Of The Marshall Islands", loc_check("republic_of_the_marshall_islands")));
        Constants.FILTERLOC.add(new Listitems("romania", "Romania", loc_check("romania")));
        Constants.FILTERLOC.add(new Listitems("russia", "Russia", loc_check("russia")));
        Constants.FILTERLOC.add(new Listitems("saint_lucia", "Saint Lucia", loc_check("saint_lucia")));
        Constants.FILTERLOC.add(new Listitems("saint_vincent_and_the_grenadines", "Saint Vincent And The Grenadines",
                loc_check("saint_vincent_and_the_grenadines")));
        Constants.FILTERLOC.add(new Listitems("samoa", "Samoa", loc_check("samoa")));
        Constants.FILTERLOC.add(new Listitems("saskatchewan", "Saskatchewan", loc_check("saskatchewan")));
        Constants.FILTERLOC.add(new Listitems("saudi_arabia", "Saudi Arabia", loc_check("saudi_arabia")));
        Constants.FILTERLOC.add(new Listitems("scotland", "Scotland", loc_check("scotland")));
        Constants.FILTERLOC.add(new Listitems("seoul", "Seoul", loc_check("seoul")));
        Constants.FILTERLOC.add(new Listitems("serbia", "Serbia", loc_check("serbia")));
        Constants.FILTERLOC.add(new Listitems("singapore", "Singapore", loc_check("singapore")));
        Constants.FILTERLOC.add(new Listitems("slovakia", "Slovakia", loc_check("slovakia")));
        Constants.FILTERLOC.add(new Listitems("slovenia", "Slovenia", loc_check("slovenia")));
        Constants.FILTERLOC.add(new Listitems("solomon_islands", "Solomon Islands", loc_check("solomon_islands")));
        Constants.FILTERLOC.add(new Listitems("south_africa", "South Africa", loc_check("south_africa")));
        Constants.FILTERLOC.add(new Listitems("south_korea", "South Korea", loc_check("south_korea")));
        Constants.FILTERLOC.add(new Listitems("spain", "Spain", loc_check("spain")));
        Constants.FILTERLOC.add(new Listitems("sri_lanka", "Sri Lanka", loc_check("sri_lanka")));
        Constants.FILTERLOC.add(new Listitems("suriname", "Suriname", loc_check("suriname")));
        Constants.FILTERLOC.add(new Listitems("sweden", "Sweden", loc_check("sweden")));
        Constants.FILTERLOC.add(new Listitems("switzerland", "Switzerland", loc_check("switzerland")));
        Constants.FILTERLOC.add(new Listitems("são_tomé_and_príncipe", "São Tomé And Príncipe", loc_check("são_tomé_and_príncipe")));
        Constants.FILTERLOC.add(new Listitems("taiwan", "Taiwan", loc_check("taiwan")));
        Constants.FILTERLOC.add(new Listitems("tanzania", "Tanzania", loc_check("tanzania")));
        Constants.FILTERLOC.add(new Listitems("thailand", "Thailand", loc_check("thailand")));
        Constants.FILTERLOC.add(new Listitems("the_democratic_republic_of_congo", "The Democratic Republic Of Congo",
                loc_check("the_democratic_republic_of_congo")));
        Constants.FILTERLOC.add(new Listitems("togo", "Togo", loc_check("togo")));
        Constants.FILTERLOC.add(new Listitems("tonga", "Tonga", loc_check("tonga")));
        Constants.FILTERLOC.add(new Listitems("trinidad_and_tobago", "Trinidad And Tobago", loc_check("trinidad_and_tobago")));
        Constants.FILTERLOC.add(new Listitems("tunisia", "Tunisia", loc_check("tunisia")));
        Constants.FILTERLOC.add(new Listitems("turkey", "Turkey", loc_check("turkey")));
        Constants.FILTERLOC.add(new Listitems("turkmenistan", "Turkmenistan", loc_check("turkmenistan")));
        Constants.FILTERLOC.add(new Listitems("uae", "UAE", loc_check("uae")));
        Constants.FILTERLOC.add(new Listitems("usa", "USA", loc_check("usa")));
        Constants.FILTERLOC.add(new Listitems("ukraine", "Ukraine", loc_check("ukraine")));
        Constants.FILTERLOC.add(new Listitems("united_arab_emirates", "United Arab Emirates", loc_check("united_arab_emirates")));
        Constants.FILTERLOC.add(new Listitems("united_kingdom", "United Kingdom", loc_check("united_kingdom")));
        Constants.FILTERLOC.add(new Listitems("uruguay", "Uruguay", loc_check("uruguay")));
        Constants.FILTERLOC.add(new Listitems("uzbekistan", "Uzbekistan", loc_check("uzbekistan")));
        Constants.FILTERLOC.add(new Listitems("vanuatu", "Vanuatu", loc_check("vanuatu")));
        Constants.FILTERLOC.add(new Listitems("venezuela", "Venezuela", loc_check("venezuela")));
        Constants.FILTERLOC.add(new Listitems("vietnam", "Vietnam", loc_check("vietnam")));
        Constants.FILTERLOC.add(new Listitems("yemen", "Yemen", loc_check("yemen")));
        Constants.FILTERLOC.add(new Listitems("zambia", "Zambia", loc_check("zambia")));
        Constants.FILTERLOC.add(new Listitems("zimbabwe", "Zimbabwe", loc_check("zimbabwe")));
        Constants.FILTERLOC.add(new Listitems("hong_kong", "Hong Kong", loc_check("hong_kong")));

        sortlist(Constants.FILTERLOC);

        loc_item_list.addAll(Constants.FILTERLOC);
    }

    /***
     * check the sentiment existing or not in previous selection
     *
     * @param sentiment checm item
     ***/
    public boolean sentiment_check(String sentiment) {

        if (Constants.BSENTIMENT.size() > 0)
            for (int i = 0; i < Constants.BSENTIMENT.size(); i++) {
                if (Constants.BSENTIMENT.get(i).toLowerCase().contains(sentiment.toLowerCase())) {
                    return true;
                }
            }
        return false;
    }

    /***
     * check the source existing or not in previous selection
     *
     * @param source check item
     **/
    public boolean source_check(String source) {

        if (Constants.BSOURCES.size() > 0) {
            for (int i = 0; i < Constants.BSOURCES.size(); i++) {
                if (Constants.BSOURCES.get(i).toLowerCase().contains(source.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    /***
     * check the gender existing or not in previous selection
     *
     * @param gender check item
     ***/
    public boolean gender_check(String gender) {

        if (Constants.BGENDER.size() > 0)
            for (int i = 0; i < Constants.BGENDER.size(); i++) {
                if (Constants.BGENDER.get(i).toLowerCase().contains(gender.toLowerCase())) {
                    return true;
                }
            }
        return false;
    }

    /***
     * check the Language existing or not in previous selection
     *
     * @param lang check item
     ***/
    public boolean lang_check(String lang) {

        if (Constants.BLANGUAGE.size() > 0)
            for (int i = 0; i < Constants.BLANGUAGE.size(); i++) {
                if (Constants.BLANGUAGE.get(i).toLowerCase().contains(lang.toLowerCase())) {
                    return true;
                }
            }

        return false;
    }

    /****
     * check the Loaction existing or not in previous selection
     *
     * @param loc check item
     ***/
    public boolean loc_check(String loc) {

        if (Constants.BLOCATION.size() > 0)
            for (int i = 0; i < Constants.BLOCATION.size(); i++) {
                if (Constants.BLOCATION.get(i).toLowerCase().contains(loc.toLowerCase())) {
                    return true;
                }
            }

        return false;
    }

    ////clear all the selection filters
    public void clearfilters() {

        Constants.BSOURCES.clear();
        Constants.BSENTIMENT.clear();
        Constants.BGENDER.clear();
        Constants.BLANGUAGE.clear();
        Constants.BLOCATION.clear();

        BuzzingaApplication.getUserSession().clearsession(BuzzingaApplication.getUserSession().Sources_data);
        BuzzingaApplication.getUserSession().clearsession(BuzzingaApplication.getUserSession().Sentiment_data);
        BuzzingaApplication.getUserSession().clearsession(BuzzingaApplication.getUserSession().Gender_data);
        BuzzingaApplication.getUserSession().clearsession(BuzzingaApplication.getUserSession().Loc_data);
        BuzzingaApplication.getUserSession().clearsession(BuzzingaApplication.getUserSession().Lang_data);

        Bundle params = new Bundle();
        params.putString("clear_Filter", "filter");
        BuzzingaApplication.getmFirebaseAnalytics().logEvent("clear_Filter_event", params);
        BuzzingaApplication.getmFirebaseAnalytics().setAnalyticsCollectionEnabled(true);


        Intent i = new Intent(Filtering.this, MainActivity.class);
        i.putExtra(Constants.Intent_OPERATION, Constants.Intent_TRACK);
        startActivity(i);
        this.overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }

    /***
     * sort the list items based on the source name
     *
     * @param list sorted the Listems list
     ***/
    public void sortlist(List<Listitems> list) {
        Collections.sort(list, new Comparator<Listitems>() {
            public int compare(Listitems item1, Listitems item2) {
                return item1.getSourcename().compareToIgnoreCase(item2.getSourcename());
            }
        });
    }

    ////prepare the Sentiment Query
    public void sentimentquery() {
        Constants.BSENTIMENT.clear();
        BuzzingaApplication.getUserSession().clearsession(BuzzingaApplication.getUserSession().Sentiment_data);

        Constants.BSENTIMENT.addAll(sel_sentiment_list);

        String sentiment = Constants.BSENTIMENT.toString();
        BuzzingaApplication.getUserSession().setSentiment_data(sentiment);

        Log.i(TAG, "selected session sentimentquery is" + BuzzingaApplication.getUserSession().getSentiment_data());
    }

    ////prepare the  Gender Query
    public void genderquery() {
        BuzzingaApplication.getUserSession().clearsession(BuzzingaApplication.getUserSession().Gender_data);
        Constants.BGENDER.clear();

        Constants.BGENDER.addAll(sel_gender_list);
        String gender = Constants.BGENDER.toString();
        BuzzingaApplication.getUserSession().setGender_data(gender);

        Log.i(TAG, "selected session genderquery is" + BuzzingaApplication.getUserSession().getGender_data());
    }

    ////prepare the Location Query
    public void locquery() {
        BuzzingaApplication.getUserSession().clearsession(BuzzingaApplication.getUserSession().Loc_data);
        Constants.BLOCATION.clear();

        Utils.showLog(TAG, "selected session locquery is" + sel_loc_list.toString(), Config.Filtering);

        Constants.BLOCATION.addAll(sel_loc_list);
        String loc = Constants.BLOCATION.toString();
        BuzzingaApplication.getUserSession().setLoc_data(loc);

        Log.i(TAG, "selected session locquery is" + BuzzingaApplication.getUserSession().getLoc_data());

    }

    ////prepare the Language Query
    public void langquery() {
        BuzzingaApplication.getUserSession().clearsession(BuzzingaApplication.getUserSession().Lang_data);
        Constants.BLANGUAGE.clear();

        Utils.showLog(TAG, "selected session lang is" + sel_lang_list.toString(), Config.Filtering);

        Constants.BLANGUAGE.addAll(sel_lang_list);

        String lang = Constants.BLANGUAGE.toString();
        BuzzingaApplication.getUserSession().setLang_data(lang);

        Log.i(TAG, "selected session langquery is" + BuzzingaApplication.getUserSession().getLang_data());
    }

    ////prepare the Source Query
    public void sourcequery() {


        BuzzingaApplication.getUserSession().clearsession(BuzzingaApplication.getUserSession().Sources_data);
        Constants.BSOURCES.clear();

        Constants.BSOURCES.addAll(sel_source_list);
        String source = Constants.BSOURCES.toString();
        BuzzingaApplication.getUserSession().setSources_data(source);
        Log.i(TAG, "selected session sourcequery is" + BuzzingaApplication.getUserSession().getSources_data());

    }

    @Override
    public void itemClicked(View view, int position) {

        Listitems item = (Listitems) filter_items.getAdapter().getItem(position);

        int pos = -1;
        if (sel_source_items == FilterStatus.LANGUAGE) {
            pos = lang_item_lsit.indexOf(item);
        } else if (sel_source_items == FilterStatus.LOCATION)
            pos = loc_item_list.indexOf(item);

        if (item.isSelectd())
            item.setSelectd(false);
        else
            item.setSelectd(true);

        Utils.showLog(TAG, "item is " + item.isSelectd(), Config.Filtering);

        if (sel_source_items == FilterStatus.LANGUAGE) {
            if (pos != -1)
                lang_item_lsit.get(pos).setSelectd(item.isSelectd());
        } else if (sel_source_items == FilterStatus.LOCATION)
            if (pos != -1)
                loc_item_list.get(pos).setSelectd(item.isSelectd());

        selSourceItem(sel_source_items);

        Utils.updateListviewItem(filter_items, position);

    }

    //// filter Status
    public enum FilterStatus {
        SOURCES,
        SENTIMENT,
        GENDER,
        LANGUAGE,
        LOCATION
    }

    /****
     * update the selection items count
     *
     * @param filtering status of selectd Filter title item
     **/
    public void selSourceItem(FilterStatus filtering) {

        switch (filtering) {

            case SOURCES:

                if (first_source == 0)
                    first_source++;

                sel_itemList(sel_source_list, Constants.FILTERSOURSOURE);
                break;
            case SENTIMENT:

                if (first_sentiment == 0)
                    first_sentiment++;

                sel_itemList(sel_sentiment_list, Constants.FILTERSENTIMENT);
                break;
            case GENDER:

                if (first_gender == 0)
                    first_gender++;

                sel_itemList(sel_gender_list, Constants.FILTERGENDER);
                break;

            case LANGUAGE:
                if (first_lang == 0)
                    first_lang++;

                sel_itemList(sel_lang_list, lang_item_lsit);
                break;

            case LOCATION:
                if (first_loc == 0)
                    first_loc++;

                sel_itemList(sel_loc_list, loc_item_list);
                break;
        }
    }

    /****
     * update the Title selectin count
     *
     * @param sel_pos update the sel count items at Filter title bage.
     ****/
    public void updateListview(int sel_pos) {

        if (sel_pos != -1)
            Utils.updateListviewItem(filter_titles, sel_pos);
    }

    /****
     * copy the sparseboolean array to list
     *
     * @param sel_item_list update the seletedd items list.
     * @param list_items    selected Filter title items.
     ****/
    public void sel_itemList(List<String> sel_item_list, List<Listitems> list_items) {

        Utils.showLog(TAG, "sel items before clear " + sel_item_list.toString(), Config.Filtering);
        sel_item_list.clear();

        for (Listitems item : list_items) {
            if (item.isSelectd())
                sel_item_list.add(item.getXtag());
        }
        Utils.showLog(TAG, "to items are " + sel_item_list.toString(), Config.Filtering);
        updateListview(sel_title_pos);
    }

}

