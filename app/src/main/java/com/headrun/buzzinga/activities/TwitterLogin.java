package com.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.headrun.buzzinga.R;

/**
 * Created by headrun on 21/7/15.
 */
public class TwitterLogin extends Activity {

    String TAG = TwitterLogin.this.getClass().getSimpleName();
    //String url="http://www.javacodegeeks.com";
    String url = "http://beta.buzzinga.com/login/twitter";

    Button loginButton;
    View twitter_btn,twitter_auth_lay;
    WebView webview;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitterlogin);

        loginButton = (Button) findViewById(R.id.twitter_login_button);
        twitter_btn=findViewById(R.id.twitter_btn);
        twitter_auth_lay=findViewById(R.id.twitter_auth_lay);
        webview = (WebView) findViewById(R.id.webview);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);


        twitter_btn.setVisibility(View.VISIBLE);
        twitter_auth_lay.setVisibility(View.GONE);
        webview.setWebViewClient(new MyBrowser());
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().getAllowContentAccess();
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");


        // webview.getSettings().setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
        webview.getSettings().getJavaScriptCanOpenWindowsAutomatically();
        webview.getSettings().setGeolocationEnabled(true);

        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        Log.d(TAG, "loading urls is" + url);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                twitter_btn.setVisibility(View.GONE);
                twitter_auth_lay.setVisibility(View.VISIBLE);
                webview.loadUrl(url.toString());
            }
        });
       /* loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                Log.i("Log_tag", "result is" + result.data.toString());

                new UserSession(TwitterLogin.this).setTSESSION(result.data.toString());

                   TwitterSession session = Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

                TwitterAuthConfig authConfig = TwitterCore.getInstance().getAuthConfig();
                OAuthSigning oauthSigning = new OAuthSigning(authConfig, authToken);

                Log.i("Log_tag","token is"+token+"secret key is"+secret);

                homeScreenIntent();
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });

        */
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.i(TAG, "url loading");
            view.loadUrl(url);

            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressbar.setProgress(100);
            progressbar.setVisibility(View.GONE);
            Log.d(TAG, "TITLE is" + view.getTitle() + "\nurl is" + view.getOriginalUrl());

            if (view.getOriginalUrl() != null)
                if (view.getOriginalUrl().toString().contains("http://beta.buzzinga.com/profile/"))
                    webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");


        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressbar.setProgress(0);
            progressbar.setVisibility(View.VISIBLE);
        }
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);

        Log.i("Log_tag", "on activiry result"+data.toString());

    }

    private void homeScreenIntent() {
        startActivity(new Intent(TwitterLogin.this, HomeScreen.class));
    }
    */
    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html)
        {
            Log.i(TAG, html);
            if(html.toString().contains("An error occured, please contact Admin"))
               startActivity(new Intent(TwitterLogin.this,HomeScreen.class));
               // Log.i(TAG,"Already User hass a login");
        }
    }

    }