package in.headrun.buzzinga;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;

import in.headrun.buzzinga.doto.QueryData;

/**
 * Created by headrun on 31/8/15.
 */
public class BuzzingaApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Tabj139fabGgqyg8pOBSnw";
    private static final String TWITTER_SECRET = "eIULy4JeljkZN8JpWXXa4o2b2puUjq73PP6zaVIVk";


    private static final String TAG = BuzzingaApplication.class.getSimpleName();

    private static BuzzingaApplication instance = null;



    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    public static BuzzingaApplication get() {
        return instance;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));


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


    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}


