package in.headrun.buzzinga;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


/**
 * Created by headrun on 31/8/15.
 */
public class BuzzingaApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.


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


