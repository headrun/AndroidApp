package in.headrun.buzzinga.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.R;

/**
 * Created by headrun on 19/9/15.
 */
public class ArticleDisplay extends Activity
{
    @Bind(R.id.article_webview)
    WebView article_webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        ButterKnife.bind(this);

        Bundle data=getIntent().getExtras();
        String url=data.getString("loading_url");
        Log.i("log_tag","url is"+url);

        article_webview.getSettings().setJavaScriptEnabled(true);

        final Activity activity = this;
        article_webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        article_webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        article_webview.loadUrl(url);
    }
}
