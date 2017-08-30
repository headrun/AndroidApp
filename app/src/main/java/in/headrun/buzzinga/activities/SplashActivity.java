package in.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.utils.Utils;


/**
 * Created by headrun on 7/7/15.
 */
public class SplashActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.


    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    public static String TAG = SplashActivity.class.getClass().getSimpleName();
    public String loged;
    @Bind(R.id.splash_progress)
    ProgressBar splash_progress;
    @Bind(R.id.splashscreen)
    ImageView splashscreen;
    @Bind(R.id.version_name)
    TextView version_name;

    UserSession userSession;
    Utils utils;
    Boolean action_type = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscren);
        ButterKnife.bind(this);

        utils = new Utils(SplashActivity.this);
        userSession = new UserSession(SplashActivity.this);
        userSession.setTACK_SEARCH_KEY("devineni uma");

        loged = new UserSession(SplashActivity.this).getTSESSION();

        if (Config.SPLASH)
            Log.i(TAG, "splash activty");

        Constants.sourse_xtags();
        Constants.gender_xtags();
        Constants.sentiment_xtags();

        Bundle params = new Bundle();
        params.putString("Buzzinga", "opened");
        utils.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, params);
        utils.mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (action_type == false)
                    checkSession();
                finish();

            }
        }, Config.SPLASH_DISPLAY_LENGTH);
        onNewIntent(getIntent());

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            version_name.setText("Version " + version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            action_type = true;
            String search_data = data.substring(data.lastIndexOf("/") + 1).trim();
            String all_data = data.toString();

            utils.showLog(TAG, "search data is" + all_data + "app indexing uri is" + search_data, Config.SPLASH);

            if (loged.length() > 0
                    && search_data != null && !search_data.isEmpty()) {
                utils.userSession.setTrackKey(search_data);
                utils.userSession.clearsession(utils.userSession.TACK_SEARCH_KEY);
                utils.add_query_data();


                startActivity(new Intent(getApplication(), MainActivity.class)
                        .putExtra(Constants.Intent_OPERATION, Constants.Intent_TRACK));

                this.overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);

            } else {
                checkSession();
            }
        }
    }

    public void checkSession() {

        Log.i("Log_tag", "loged session is" + loged);
        if (loged.length() > 0) {

            String track_key = userSession.getTrackKey() == null ? "" : userSession.getTrackKey().trim();

            if (track_key.isEmpty()) {
                startActivity(new Intent(this, TrackKeyWord.class));
            } else {
                Constants.BTRACKKEY.add(userSession.getTrackKey());
                utils.add_query_data();

                startActivity(new Intent(getApplication(), MainActivity.class).
                        putExtra(Constants.Intent_OPERATION, Constants.Intent_TRACK));

            }
        } else {
            startActivity(new Intent(this, TwitterLogin.class));
        }
        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
