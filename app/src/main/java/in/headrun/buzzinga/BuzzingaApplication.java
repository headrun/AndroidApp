package in.headrun.buzzinga;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import in.headrun.buzzinga.doto.QueryData;

/**
 * Created by headrun on 31/8/15.
 */
public class BuzzingaApplication extends Application {

    private static final String TAG = BuzzingaApplication.class.getSimpleName();

    private static BuzzingaApplication instance = null;

    public static ArrayList<String> BTRACKKEY = new ArrayList<>();
    public static ArrayList<String> BSEARCHKEY = new ArrayList<>();
    public static ArrayList<String> BSOURCES = new ArrayList<>();
    public static ArrayList<String> BGENDER = new ArrayList<>();
    public static ArrayList<String> BSENTIMENT = new ArrayList<>();
    public static ArrayList<String> BLOCATION = new ArrayList<>();
    public static ArrayList<String> BLANGUAGE = new ArrayList<>();
    public static ArrayList<String> BFROMDATE = new ArrayList<>();
    public static ArrayList<String> BTODATE = new ArrayList<>();

    public static ArrayList<QueryData> QueryString = new ArrayList<QueryData>();

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


