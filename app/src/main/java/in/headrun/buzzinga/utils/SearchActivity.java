package in.headrun.buzzinga.utils;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import in.headrun.buzzinga.R;
import in.headrun.buzzinga.config.Config;

/**
 * Created by sujith on 23/8/16.
 */
public class SearchActivity extends Activity {

    public String TAG = SearchActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            new Utils(this).showLog(TAG, "search string is " + query, Config.SearchActivity);
        }
    }
}
