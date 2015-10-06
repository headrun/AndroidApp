package in.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.BuzzingaApplication;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.doto.Utils;


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
    UserSession userSession;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSession = new UserSession(SplashActivity.this);
        setContentView(R.layout.splashscren);
        ButterKnife.bind(this);
        utils = new Utils(SplashActivity.this);

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
            if (userSession.getTrackKey().trim().length() < 0 || userSession.getTrackKey() == null)
                startActivity(new Intent(this, TrackKeyWord.class));
            else {
                utils.clear_all_data();

                Log.i(TAG, "track key is" + userSession.getTrackKey());
                Constants.BTRACKKEY.add(userSession.getTrackKey());
                utils.add_query_data();
                Constants.listdetails.clear();
                Intent intent = new Intent(getApplication(), HomeScreen.class);
                intent.putExtra(Constants.Intent_OPERATION, Constants.Intent_TRACK);
                startActivity(intent);
            }
        else
            startActivity(new Intent(this, TwitterLogin.class));
    }
}
