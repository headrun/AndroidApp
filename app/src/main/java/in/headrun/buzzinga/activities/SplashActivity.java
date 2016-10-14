package in.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.BuildConfig;
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

        loged = new UserSession(SplashActivity.this).getTSESSION();

        if (Config.SPLASH)
            Log.i(TAG, "splash activty");

        Constants.sourse_xtags();
        Constants.gender_xtags();
        Constants.sentiment_xtags();

        // utils.addtoList();

        Bundle params = new Bundle();
        params.putString("Buzzinga", "opened");
        utils.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, params);
        utils.mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (action_type == false)
                    checkSession();


                //   splash_progress.setVisibility(View.GONE);
                finish();

            }
        }, Config.SPLASH_DISPLAY_LENGTH);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        onNewIntent(getIntent());


        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .build();*/
    }

    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            action_type = true;
            String search_data = data.substring(data.lastIndexOf("/") + 1).trim();
            String all_data = data.toString();
/*
            Uri contentUri = Uri.parse("content://com.recipe_app/recipe/").buildUpon()
                    .appendPath(recipeId).build();
*/
            utils.showLog(TAG, "search data is" + all_data + "app indexing uri is" + search_data, Config.SPLASH);

            if (loged.length() > 0
                    && search_data != null && !search_data.isEmpty()) {
                utils.userSession.setTrackKey(search_data);
                utils.userSession.clearsession(utils.userSession.TACK_SEARCH_KEY);
                utils.add_query_data();

                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, search_data);
                utils.mFirebaseAnalytics.logEvent("Track", params);
                utils.mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

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
                // Constants.listdetails = Constants.articlelist_Details;
                // Log.i(TAG, "track key is" + userSession.getTrackKey() + "article size " + Constants.listdetails + "Constants.articlelist_Details" + Constants.articlelist_Details);
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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Splash Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse(Constants.BUZZINGA_URI),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse(Constants.APP_INDEXING_URL)
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Splash Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse(Constants.BUZZINGA_URI),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse(Constants.APP_INDEXING_URL)
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
