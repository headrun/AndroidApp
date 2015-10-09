package in.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.utils.ConnectionSettings;

/**
 * Created by headrun on 21/7/15.
 */
public class TwitterLogin extends Activity {

    String TAG = TwitterLogin.this.getClass().getSimpleName();

    @Bind(R.id.twitter_login_button)
    ImageView loginButton;
    @Bind(R.id.progressbar)
    ProgressBar progressbar;
    @Bind(R.id.twitter_btn)
    View twitter_btn;
    @Bind(R.id.twitter_auth_lay)
    View twitter_auth_lay;
    @Bind(R.id.webview)
    WebView webview;


    String cookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitterlogin);
        ButterKnife.bind(this);


        twitter_btn.setVisibility(View.VISIBLE);
        twitter_auth_lay.setVisibility(View.GONE);
        webview.setWebViewClient(new MyBrowser());
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().getAllowContentAccess();
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        // webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        Log.d(TAG, "loading urls is" + ServerConfig.SERVER_ENDPOINT + ServerConfig.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ConnectionSettings.isConnected(TwitterLogin.this)) {
                    twitter_btn.setVisibility(View.GONE);
                    twitter_auth_lay.setVisibility(View.VISIBLE);

                    webview.loadUrl(ServerConfig.SERVER_ENDPOINT + ServerConfig.login);

                } else {
                    Toast.makeText(getApplication(), "Network error", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.i(TAG, "url loading");
            Log.i(TAG,"loading url is"+url);
            view.loadUrl(url);

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressbar.setProgress(100);
            progressbar.setVisibility(View.GONE);
            Log.d(TAG, "TITLE is" + view.getTitle() + "\nurl is" +url);


            if (view.getOriginalUrl() != null) {
                cookie = CookieManager.getInstance().getCookie(url);

                if (cookie != null) {
                    String[] cookie_extract = cookie.split(";");
                    for (String cookivalue : cookie_extract) {
                        if (cookivalue.contains("sessionid")) {
                            String[] temp1 = cookivalue.split("=");
                            Log.i(TAG, "session id is" + temp1[1]);
                            new UserSession(TwitterLogin.this).setTSESSION(temp1[1]);
                        }
                    }

                    if (new UserSession(TwitterLogin.this).getTSESSION().length() > 0)
                        startActivity(new Intent(TwitterLogin.this, TrackKeyWord.class));
                }

            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressbar.setProgress(0);
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            Log.i(TAG,"error"+error.getUrl());
            handler.proceed(); // Ignore SSL certificate errors

        }
    }

}