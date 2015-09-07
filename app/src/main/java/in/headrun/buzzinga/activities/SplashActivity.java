package in.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import in.headrun.buzzinga.R;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import io.fabric.sdk.android.Fabric;

/**
 * Created by headrun on 7/7/15.
 */
public class SplashActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.


    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Tabj139fabGgqyg8pOBSnw";
    private static final String TWITTER_SECRET = "eIULy4JeljkZN8JpWXXa4o2b2puUjq73PP6zaVIVk";
    public String loged;
    public static String TAG = SplashActivity.class.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.splashscren);

        if (Config.SPLASH)
            Log.i(TAG, "splash activty");
        Constants.xtags();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                checkSession();
                finish();

            }
        }, Config.SPLASH_DISPLAY_LENGTH);

        Constants.sourceslist.add("fbpages_sourcetype_manual");
        Constants.sourceslist.add("twitter_search_sourcetype_manual");
        Constants.sourceslist.add("googleplus_search_sourcetype_manual");
        Constants.sourceslist.add("news_sourcetype_manual_parent");
        Constants.sourceslist.add("blogs_sourcetype_manual_parent");
        Constants.sourceslist.add("forums_sourcetype_manual_parent");
        Constants.sourceslist.add("youtube_search_sourcetype_manual");
        Constants.sourceslist.add("flickr_search_sourcetype_manual");
        Constants.sourceslist.add("instagram_search_sourcetype_manual");
        Constants.sourceslist.add("tumblr_search_sourcetype_manual");
        Constants.sourceslist.add("linkedin_search_sourcetype_manual");
        Constants.sourceslist.add("quora_sourcetype_manual_parent");

        Constants.sentimentlist.add("positive_sentiment_final");
        Constants.sentimentlist.add("negative_sentiment_final");
        Constants.sentimentlist.add("neutral_sentiment_final");

        Constants.genderlist.add("male_gender_final");
        Constants.genderlist.add("female_gender_final");
        Constants.genderlist.add("unclassified_gender_final");

    }


    public void checkSession() {

        loged = new UserSession(SplashActivity.this).getTSESSION();
        Log.i("Log_tag", "loged session is" + loged);
        if (loged.length() > 0)
            startActivity(new Intent(this, TrackKeyWord.class));
        else
            startActivity(new Intent(this, TwitterLogin.class));
    }
}
