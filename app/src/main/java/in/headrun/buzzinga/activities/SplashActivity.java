package in.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import butterknife.Bind;
import butterknife.ButterKnife;
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
    public static String TAG = SplashActivity.class.getClass().getSimpleName();
    public String loged;
    @Bind(R.id.splash_progress)
    ProgressBar splash_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.splashscren);
        ButterKnife.bind(this);
        splash_progress.setVisibility(View.VISIBLE);
        if (Config.SPLASH)
            Log.i(TAG, "splash activty");
        Constants.xtags();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                checkSession();
                splash_progress.setVisibility(View.GONE);
                finish();

            }
        }, Config.SPLASH_DISPLAY_LENGTH);


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
