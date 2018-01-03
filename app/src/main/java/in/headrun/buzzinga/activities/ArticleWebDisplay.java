package in.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.net.MalformedURLException;
import java.net.URL;


import butterknife.BindView;
import butterknife.ButterKnife;
import in.headrun.buzzinga.BuzzingaApplication;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.doto.SearchArticles;
import in.headrun.buzzinga.utils.Utils;

/**
 * Created by headrun on 22/9/15.
 */
public class ArticleWebDisplay extends AppCompatActivity {

    public String TAG = ArticleWebDisplay.this.getClass().getSimpleName();

    @BindView(R.id.article_webview)
    WebView article_webview;
    @BindView(R.id.article_browser_progress)
    ProgressBar article_progress;

    String url = "", title = "";
    int pos;
    SearchArticles article_details;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();
        pos = data.getInt("pos");

        if (Constants.SEARCHARTICLES.size() >= pos)
            article_details = Constants.SEARCHARTICLES.get(pos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close_grey);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(7);


        if (article_details != null) {

            title = article_details.source.TITLE;

            if (!title.isEmpty())
                getSupportActionBar().setTitle(title);
            else if (!article_details.source.AUTHOR.NAME.trim().isEmpty())
                getSupportActionBar().setTitle("@ " + article_details.source.AUTHOR.NAME.trim().trim());
            else
                getSupportActionBar().setTitle("");

            url = article_details.source.URL;
            if (!url.isEmpty()) {
                try {
                    URL aURL = new URL(url.toString());
                    getSupportActionBar().setSubtitle(aURL.getHost());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            } else
                getSupportActionBar().setSubtitle("");


        }

        if (!url.trim().isEmpty())
            webSettings(url.toString());

       /* articlebrowser_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                article_webview.destroy();
                Intent i = new Intent(ArticleWebDisplay.this, MainActivity.class);
                i.putExtra(Constants.Intent_OPERATION, Constants.Intent_NOTHING);
                startActivity(i);
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            }
        });
*/
        Bundle params = new Bundle();
        params.putString("open_link", url);
        BuzzingaApplication.getmFirebaseAnalytics().logEvent("article_open", params);
        BuzzingaApplication.getmFirebaseAnalytics().setAnalyticsCollectionEnabled(true);
    }

    private void webSettings(String url) {

        article_webview.getSettings().setJavaScriptEnabled(true);
        article_webview.clearCache(true);
        article_webview.clearHistory();
        article_webview.loadUrl("");

        article_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i(TAG, "started webview");
                article_progress.setProgress(0);
                article_progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i(TAG, "finished webview");
                article_progress.setProgress(100);
                article_progress.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.i(TAG, "load resource" + url);
            }

           /* @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.i(TAG, "webview error code" + errorCode + "\n description" + description);
            }*/
        });


        article_webview.loadUrl(url.toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.artical_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.invite) {

            ShareCompat.IntentBuilder
                    .from(this) // getActivity() or activity field if within Fragment
                    .setText(title + "\n" + url)
                    .setChooserTitle(title)
                    .setType("text/plain") // most general text sharing MIME type
                    .setChooserTitle("Buzzinga Analytics")
                    .startChooser();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*startActivity(new Intent(this, MainActivity.class).
                putExtra(Constants.Intent_OPERATION, Constants.Intent_NOTHING).
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK));*/
        overridePendingTransition(R.anim.move_right_out_activity, R.anim.move_left_in_activity);
    }
}

