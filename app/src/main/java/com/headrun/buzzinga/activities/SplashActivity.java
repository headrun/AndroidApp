package com.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.headrun.buzzinga.R;
import com.headrun.buzzinga.UserSession;
import com.headrun.buzzinga.config.Config;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by headrun on 7/7/15.
 */
public class SplashActivity extends Activity {

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                checkSession();
                finish();

            }
        }, Config.SPLASH_DISPLAY_LENGTH);
    }

    public void checkSession() {

         loged = new UserSession(SplashActivity.this).getTSESSION();
        Log.i("Log_tag", "loged session is" + loged);
        if (!loged.equals("null"))
            startActivity(new Intent(this, HomeScreen.class));
        else
            startActivity(new Intent(this, TwitterLogin.class));


    }
}