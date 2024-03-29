package in.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.flaviofaria.kenburnsview.KenBurnsView;


import java.util.HashMap;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;
import in.headrun.buzzinga.BuzzingaApplication;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.config.ServerConfig;

import in.headrun.buzzinga.utils.Utils;

/**
 * Created by headrun on 21/7/15.
 */
public class TwitterLogin extends Activity {

    String TAG = TwitterLogin.this.getClass().getSimpleName();

    @BindView(R.id.twitter_login_button1) Button loginButton;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.twitter_btn)
    View twitter_btn;
    @BindView(R.id.twitter_auth_lay)
    View twitter_auth_lay;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.BlurImageView)
    KenBurnsView blurImageView;


    private Button btnuserdetails;
    public String stoken, token, userid, username;

    String cookie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitterlogin);

        ButterKnife.bind(this);

        Glide.with(this).load(R.drawable.buzz_bg).into(blurImageView);

        twitter_btn.setVisibility(View.VISIBLE);
        twitter_auth_lay.setVisibility(View.GONE);
        btnuserdetails = (Button) findViewById(R.id.btnuserdetails);
        webview.setWebViewClient(new MyBrowser());
        webview.getSettings().setLoadsImagesAutomatically(true);
        //webview.getSettings().setJavaScriptEnabled(true);
        //webview.getSettings().setDomStorageEnabled(true);
        //webview.getSettings().getAllowContentAccess();
        webview.getSettings().setLoadWithOverviewMode(true);
        // webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        //webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        Log.d(TAG, "loading urls is" + ServerConfig.SERVER_ENDPOINT + ServerConfig.login);


        loginButton.setOnClickListener(v -> {

            if (Utils.isNetwrokConnection(TwitterLogin.this)) {
                twitter_btn.setVisibility(View.GONE);
                twitter_auth_lay.setVisibility(View.VISIBLE);

                webview.loadUrl(ServerConfig.SERVER_ENDPOINT + ServerConfig.login);

            } else {
                Toast.makeText(getApplication(), "Network error", Toast.LENGTH_LONG).show();
            }
        });


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

            if (view.getOriginalUrl() != null) {

                cookie = CookieManager.getInstance().getCookie(url);

                if (cookie != null) {

                    String[] cookie_extract = cookie.split(";");
                    for (String cookivalue : cookie_extract) {
                        Log.i(TAG, "cookie value is" + cookivalue);
                    }

                    for (String cookivalue : cookie_extract) {

                        if (cookivalue.contains("sessionid")) {
                            String[] temp1 = cookivalue.split("=");
                            Log.i(TAG, "session id is" + temp1[1]);
                            new UserSession(TwitterLogin.this).setTSESSION(temp1[1]);
                        }
                    }

                    if (BuzzingaApplication.getUserSession().getTSESSION().length() > 0) {
                        Utils.callService(TwitterLogin.this);
                        //startActivity(new Intent(TwitterLogin.this, MainActivity.class));
                        Constants.BTRACKKEY.add(BuzzingaApplication.getUserSession().getTrackKey());
                        Utils.add_query_data();

                        String track_key = BuzzingaApplication.getUserSession().getTrackKey() == null ? "" : BuzzingaApplication.getUserSession().getTrackKey().trim();
                        String data = track_key.replaceAll("\\[|\\]", "");
                        if (data.isEmpty()) {
                            startActivity(new Intent(TwitterLogin.this, TrackKeyWord.class));
                        }else {
                            startActivity(new Intent(getApplication(), MainActivity.class).
                                    putExtra(Constants.Intent_OPERATION, Constants.Intent_TRACK));

                            overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                            finish();
                        }
                    }
                }
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressbar.setProgress(0);
            progressbar.setVisibility(View.VISIBLE);
        }
    }


    public void postdetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ServerConfig.SERVER_ENDPOINT + ServerConfig.logout,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, "string response is" + response);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "string error response is" + error);

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("clubbed_query", stoken);
                params.put("setup", token);
                params.put("setup", userid);
                params.put("setup", username);

                return params;
            }

        };

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}