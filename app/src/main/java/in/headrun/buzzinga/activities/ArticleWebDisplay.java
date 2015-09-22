package in.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.config.Constants;

/**
 * Created by headrun on 22/9/15.
 */
public class ArticleWebDisplay extends Activity {

    public String TAG = ArticleWebDisplay.this.getClass().getSimpleName();

    @Bind(R.id.article_closebrowser)
    ImageView articlebrowser_close;
    @Bind(R.id.article_browsertitle)
    TextView article_url_disp;
    @Bind(R.id.article_webview)
    WebView article_webview;
    @Bind(R.id.article_browser_progress)
    ProgressBar article_progress;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
         ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();
        url = data.getString("url");

        Log.i(TAG, "artcile url is" + url.toString());
        if (!url.toString().isEmpty())
            article_url_disp.setText(url.toString());
        webSettings(url.toString());

        articlebrowser_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                article_webview.destroy();
                Intent i = new Intent(ArticleWebDisplay.this, HomeScreen.class);
                i.putExtra(Constants.Intent_OPERATION, Constants.Intent_NOTHING);
                startActivity(i);
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            }
        });

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

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.i(TAG, "webview error code" + errorCode + "\n description" + description);
            }
        });


        article_webview.loadUrl(url.toString());

    }

}

