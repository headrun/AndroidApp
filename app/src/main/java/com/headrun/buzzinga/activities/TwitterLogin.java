package com.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.headrun.buzzinga.R;
import com.headrun.buzzinga.UserSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

/**
 * Created by headrun on 21/7/15.
 */
public class TwitterLogin extends Activity {

    private TwitterLoginButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitterlogin);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                Log.i("Log_tag", "result is" + result.data.toString());

                new UserSession(TwitterLogin.this).setTSESSION(result.data.toString());
                /*
                   TwitterSession session = Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

                TwitterAuthConfig authConfig = TwitterCore.getInstance().getAuthConfig();
                OAuthSigning oauthSigning = new OAuthSigning(authConfig, authToken);

                Log.i("Log_tag","token is"+token+"secret key is"+secret);
                   */
                homeScreenIntent();
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);

        Log.i("Log_tag", "on activiry result");

    }

    private void homeScreenIntent() {
        startActivity(new Intent(TwitterLogin.this, HomeScreen.class));
    }
}
