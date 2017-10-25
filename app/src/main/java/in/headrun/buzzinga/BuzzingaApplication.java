package in.headrun.buzzinga;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.tweetui.TweetUi;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;


/**
 * Created by headrun on 31/8/15.
 */
public class BuzzingaApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.


    private static final String TAG = BuzzingaApplication.class.getSimpleName();

    private static BuzzingaApplication instance = null;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private Twitter twitter;

    public static BuzzingaApplication get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        instance = this;
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });


        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(authConfig)
                .debug(true)
                .build();
        Twitter.initialize(config);


    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}


