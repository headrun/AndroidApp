package in.headrun.buzzinga.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Arrays;


import butterknife.BindView;
import butterknife.ButterKnife;
import in.headrun.buzzinga.BuzzingaApplication;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.activities.MainActivity;
import in.headrun.buzzinga.activities.TrackKeyWord;
import in.headrun.buzzinga.activities.TwitterLogin;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.services.TweetLoadService;
import in.headrun.buzzinga.utils.Utils;


/**
 * Created by headrun on 7/7/15.
 */
public class SplashActivity extends Activity {

    public static String TAG = SplashActivity.class.getClass().getSimpleName();
    public String loged;


    @BindView(R.id.lottie)
    LottieAnimationView lottie;

    Boolean action_type = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscren);
        ButterKnife.bind(this);

        lottie.setAnimation("ripple_loading_animation.json");
        lottie.loop(true);
        lottie.playAnimation();

        Constants.BTRACKKEY.addAll(Arrays.asList(getResources().getStringArray(R.array.track_keywords)));

        if (Constants.BTRACKKEY.size() > 0)
            BuzzingaApplication.getUserSession().setTrackKey(Constants.BTRACKKEY.toString());

        loged = new UserSession(SplashActivity.this).getTSESSION();

        if (Config.SPLASH)
            Log.i(TAG, "splash activty");

        Constants.sourse_xtags();
        Constants.gender_xtags();
        Constants.sentiment_xtags();

        Bundle params = new Bundle();
        params.putString("Buzzinga", "opened");
        BuzzingaApplication.getmFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.APP_OPEN, params);
        BuzzingaApplication.getmFirebaseAnalytics().setAnalyticsCollectionEnabled(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startService(new Intent(SplashActivity.this, TweetLoadService.class));

                if (action_type == false)
                    checkSession();
                finish();

            }
        }, Config.SPLASH_DISPLAY_LENGTH);

        onNewIntent(getIntent());

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
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

            Utils.showLog(TAG, "search data is" + all_data + "app indexing uri is" + search_data, Config.SPLASH);

            if (loged.length() > 0
                    && search_data != null && !search_data.isEmpty()) {
                BuzzingaApplication.getUserSession().setTrackKey(search_data);
                BuzzingaApplication.getUserSession().clearsession(BuzzingaApplication.getUserSession().TACK_SEARCH_KEY);
                Utils.add_query_data();

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
            String track_key = BuzzingaApplication.getUserSession().getTrackKey() == null ? "" : BuzzingaApplication.getUserSession().getTrackKey().trim();
            String data = track_key.replaceAll("\\[|\\]", "");
            if (data.isEmpty()) {
                startActivity(new Intent(this, TrackKeyWord.class));
            } else {
                Constants.BTRACKKEY.add(BuzzingaApplication.getUserSession().getTrackKey());
                BuzzingaApplication.getUserSession().setFROM_DATE("");
                BuzzingaApplication.getUserSession().setTO_DATE("");
                Utils.add_query_data();

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
