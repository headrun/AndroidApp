package in.headrun.buzzinga.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetLinkClickListener;
import com.twitter.sdk.android.tweetui.TweetUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


import butterknife.BindView;
import butterknife.ButterKnife;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.core.BuzzingaNetowrkServices;
import in.headrun.buzzinga.core.ResponseListener;
import in.headrun.buzzinga.doto.SearchArticles;
import in.headrun.buzzinga.utils.TimeAgo;
import in.headrun.buzzinga.utils.Utils;


/**
 * Created by headrun on 10/7/15.
 */
public class SearchListDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String TAG = SearchListDataAdapter.this.getClass().getSimpleName();
    Context context;
    LinkedList<SearchArticles> listdata;

    TimeAgo time_ago;

    private final int VIEW_PROG = 0;
    private final int VIEW_ITEM = 1;
    private final int VIEW_TWITTER = 2;
    private final int VIEW_GOOGLE = 3;
    private final int VIEW_FACEBOOK = 4;

    private Utils.setOnItemClickListner onitemclicklistner = null;
    private Utils.progressBarListner mprogressBarListner = null;

    public SearchListDataAdapter(Context context, LinkedList<SearchArticles> listdata) {

        Log.i(TAG, "search data adapter is" + listdata.size());
        this.context = context;
        this.listdata = listdata;
        time_ago = new TimeAgo(context);

    }

    public void setClickListener(Utils.setOnItemClickListner onitemclicklistner,
                                 Utils.progressBarListner mprogressBarListner) {
        this.onitemclicklistner = onitemclicklistner;
        this.mprogressBarListner = mprogressBarListner;
    }

    @Override
    public int getItemViewType(int position) {
        if (listdata.get(position) != null) {
            if (listdata.get(position) instanceof SearchArticles) {
                SearchArticles article_item = listdata.get(position);
                SearchArticles.Source article_source = article_item.source;
                List<String> items = article_source.XTAGS;
                String source = sourType(items);

                if (!source.isEmpty()) {
                    if (source.contains(Constants.TWITTER)) {
                        return VIEW_TWITTER;
                    } else if (source.contains(Constants.GOOGLEPLUS)) {
                        return VIEW_GOOGLE;
                    } else if (source.contains(Constants.FACEBOOK))
                        return VIEW_FACEBOOK;
                }
                return VIEW_ITEM;
            }
        }
        return VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;

        if (viewType == VIEW_TWITTER) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.twitterembedded, parent, false);
            vh = new TwetterEmbedded(v);

        } else if (viewType == VIEW_FACEBOOK) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.webview_facebook, parent, false);
            vh = new FacebookEmbedded(v);

        } else if (viewType == VIEW_GOOGLE || viewType == VIEW_ITEM) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.genral_article_view, parent, false);
            vh = new GenralArticleViewHolder(v);

        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progess_layout, parent, false);
            vh = new ProgrssHolder(v);
        }
        return vh;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        SearchArticles item = listdata.get(position);

        String source = null;
        String time = null;
        String sentiment_type = null;

        if (item != null && item.source != null) {
            source = sourType(item.source.XTAGS);
            sentiment_type =Utils.sentimentType(item.source.XTAGS);
            time = time_ago.timeAgo(Long.parseLong(item.source.DATE_ADDED));
        }

        if (holder instanceof TwetterEmbedded) {

            final TwetterEmbedded twitter_holder = ((TwetterEmbedded) holder);

            if (item.source != null && item.source.original_data != null) {
                List<SearchArticles.Urls> urls = item.source.original_data.entities.entite_urls;
                String tweet_id = item.source.original_data.id_str;
                // showTwitterView(context, tweet_id, twitter_holder.tweet_view, position);

                twitter_holder.sentiment_img.setImageResource(Utils.getEmoji(sentiment_type));

                new ShowTweet(context, tweet_id, twitter_holder.tweet_view, position);
            }

        } else if (holder instanceof FacebookEmbedded) {
            final FacebookEmbedded fb_holder = ((FacebookEmbedded) holder);


                fb_holder.sentiment_img.setImageResource(Utils.getEmoji(sentiment_type));


            if (source == Constants.FACEBOOK) {
                setfbdata(fb_holder.webview, item.source.URL);
            }


        } else if (holder instanceof GenralArticleViewHolder) {

            final GenralArticleViewHolder article_view = ((GenralArticleViewHolder) holder);

            try {

                article_view.og_title.setText("" + item.source.TITLE);
                article_view.og_description.setText("" + item.source.TEXT);
                String text_url = item.source.URL;
                String host_name = "";

                if (!text_url.isEmpty()) {
                    URL aURL = new URL(text_url);
                    host_name = aURL.getHost();
                    article_view.og_url.setText("" + host_name + "\n" + time);


                        article_view.og_sentiment.setImageResource(Utils.getEmoji(sentiment_type));

                    Glide.with(context).load(ServerConfig.FETCH_ICON + host_name).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            setImage(((BitmapDrawable) resource).getBitmap(), article_view.favicon);

                        }
                    });

                    if (item.source.IMAGE_LINK != null) {
                        if (!item.source.IMAGE_LINK.isEmpty()) {
                            article_view.og_image.setVisibility(View.VISIBLE);
                            Glide.with(context).load(item.source.IMAGE_LINK).into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                    if(resource instanceof BitmapDrawable)
                                    setImage(((BitmapDrawable) resource).getBitmap(), article_view.og_image);

                                }
                            });
                        } else {
                            article_view.og_image.setVisibility(View.GONE);
                        }
                    } else {
                        new GetLinkData(article_view.og_image, context, text_url, item, position, VIEW_ITEM);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ((ProgrssHolder) holder).progress.setIndeterminate(true);
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }


    public class TwetterEmbedded extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tweet_view)
        RelativeLayout tweet_view;
        @BindView(R.id.sentiment_img)
        ImageView sentiment_img;

        public TwetterEmbedded(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tweet_view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (onitemclicklistner != null) {
                onitemclicklistner.itemClicked(v, getAdapterPosition(),Constants.TWITTER);
            }
        }
    }

    public class FacebookEmbedded extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {

        @BindView(R.id.webview)
        public WebView webview;
        @BindView(R.id.sentiment_img)
        ImageView sentiment_img;

        public FacebookEmbedded(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            webview.setOnClickListener(this);
            webview.setOnTouchListener(this);
        }

        @Override
        public void onClick(View v) {

            if (onitemclicklistner != null) {
                onitemclicklistner.itemClicked(v, getAdapterPosition(),Constants.FACEBOOK);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            /*if (event.getAction() == KeyEvent.ACTION_DOWN) {

                if (onitemclicklistner != null) {
                    onitemclicklistner.itemClicked(v, getAdapterPosition());
                }
                return true;
            }*/

            return false;
        }
    }

    public class ProgrssHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.progres)
        public ProgressBar progress;

        public ProgrssHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {

        }

    }

    public class GenralArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.og_image)
        ImageView og_image;
        @BindView(R.id.og_title)
        TextView og_title;
        @BindView(R.id.og_description)
        TextView og_description;
        @BindView(R.id.favicon)
        ImageView favicon;
        @BindView(R.id.og_sentiment)
        ImageView og_sentiment;
        @BindView(R.id.og_url)
        TextView og_url;

        public GenralArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (onitemclicklistner != null) {
                onitemclicklistner.itemClicked(v, getAdapterPosition(),Constants.BLOGS);
            }
        }


    }

    public String sourType(List<String> xtag) {

        if (xtag != null && xtag.size() > 0) {
            Utils.source_xtags();
            Set<String> source_keys = Constants.source_map.keySet();

            for (String key : source_keys) {

                if (xtag.toString().toLowerCase().contains(key.toLowerCase() + "_")) {
                    return key;
                }
                if (key.contains("facebook") && (xtag.toString().toLowerCase().contains("fb" + "_") ||
                        xtag.toString().toLowerCase().contains("fbpages" + "_"))) {
                    return key;
                }
            }
        } else {
            Utils.showLog(TAG, " source items  are empty list ", Config.SearchListDataAdapter);
        }
        return "";
    }

    public String genderType(List<String> xtag) {

        if (xtag.size() > 0) {
            Utils.genter_xtags();
            Set<String> gender_keys = Constants.gender_map.keySet();
            for (String gender_key : gender_keys)
                if (xtag.contains(gender_key))
                    return gender_key;
        }
        return "";
    }



    private int applySentimentColor(String sentimet) {

        if (sentimet != null)
            if (sentimet.contains(Constants.POSITIVE)) {
                return R.drawable.pos_sentiment;
            } else if (sentimet.contains(Constants.NEGATIVE)) {
                return R.drawable.neg_sentiment;
            } else
                return R.drawable.neu_sentiment;

        return R.drawable.neu_sentiment;
    }



   /* private int sourceicon(String type) {

        if (type != null) {
            Utils.showLog(TAG, " source icon type " + type, Config.SearchListDataAdapter);
            switch (type) {
                case Constants.FACEBOOK:
                    return R.drawable.ic_facebook;
                case Constants.TWITTER:
                    return R.drawable.ic_twitter;
                case Constants.NEWS:
                    return R.drawable.ic_news;
                case Constants.BLOGS:
                    return R.drawable.ic_blog;
                case Constants.FORUMS:
                    return R.drawable.forum;
                case Constants.TUMBLR:
                    return R.drawable.tumblr;
                case Constants.QUORA:
                    return R.drawable.quora;
                case Constants.FLICKR:
                    return R.drawable.flickr;
                case Constants.INSTAGRAM:
                    return R.drawable.instagram;
                case Constants.YOUTUBE:
                    return R.drawable.ic_youtube;
                case Constants.GOOGLEPLUS:
                    return R.drawable.gplus;
                case Constants.LINKDIN:
                    return R.drawable.linkedin;
            }
            return 0;
        } else {
            Utils.showLog(TAG, " source icon type is null ", Config.SearchListDataAdapter);
        }
        return 0;
    }*/

    public void setImage(Bitmap bitmap, ImageView avatar_img) {

        if (bitmap != null) {
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(),
                    Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true));
            // drawable.setCircular(true);
            avatar_img.setImageDrawable(drawable);
        }else{
            avatar_img.setVisibility(View.GONE);
        }
    }

    private String TwitterImage(String img) {

        if (img != null && !TextUtils.isEmpty(img)) {

            List<String> data = new ArrayList<>(Arrays.asList("normal.jpg", "normal.png", "bigger.png", "bigger.jpg"));
            boolean is_jpg = true;
            List<String> split_img = new ArrayList<>(Arrays.asList(img.split("_")));
            int pos = -1;
            for (String type : data) {
                if (split_img.contains(type)) {
                    pos = split_img.indexOf(type);
                    if (pos != -1) {
                        is_jpg = type.contains("jpg");
                        split_img.remove(pos);
                        String url = TextUtils.join("_", split_img);
                        if (is_jpg) {
                            return url + ".jpg";
                        } else {
                            return url + ".png";
                        }
                    }
                }
            }
        }

        return img;
    }

    private String GoogleImage(String img) {

        if (img != null && !img.isEmpty()) {
            List<String> urls = new ArrayList<String>(Arrays.asList(img.split("\\?")));
            for (int i = 0; i < urls.size(); i++) {
                if (urls.get(i).contains("sz")) {
                    urls.remove(i);
                    return TextUtils.join("\\?", urls);
                }
            }

        }
        return img;
    }


    public void setfbdata(WebView webview, String url) {

        String html = "<!doctype html> <html> <head></head> <body> " +
                "<div id=\"fb-root\"></div> <script>(function(d, s, id) { var js, fjs = d.getElementsByTagName(s)[0]; if (d.getElementById(id)) return; js = d.createElement(s); js.id = id; js.src = \"//connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.6\"; fjs.parentNode.insertBefore(js, fjs); }(document, 'script', 'facebook-jssdk'));</script> " +
                "<div class=\"fb-post\" data-href=\"" + url + "\" " +
                ">" +
                "</div> </body> </html>";

        setwebdata(webview, url, html);
    }


    public void setwebdata(WebView webview, String url, String html) {
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        // webview.loadData(url, "text/html", "UTF-8");

        webview.setWebViewClient(new UriWebViewClient());
        webview.setWebChromeClient(new UriChromeClient());

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setSupportZoom(false);
        webview.getSettings().setBuiltInZoomControls(false);
        CookieManager.getInstance().setAcceptCookie(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        if (Build.VERSION.SDK_INT >= 21) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true);
        }

        if (!url.isEmpty())
            webview.loadDataWithBaseURL(url, html, "text/html", "UTF-8", null);
        else
            webview.loadData(html, "text/html; charset=utf-8", "UTF-8");

    }

    class UriChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView mWebviewPop = new WebView(context);
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient());
            mWebviewPop.setWebChromeClient(this);
            mWebviewPop.getSettings().setJavaScriptEnabled(true);
            mWebviewPop.getSettings().setDomStorageEnabled(true);
            mWebviewPop.getSettings().setSupportZoom(false);
            mWebviewPop.getSettings().setBuiltInZoomControls(false);
            mWebviewPop.getSettings().setSupportMultipleWindows(true);
            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            //  mContainer.addView(mWebviewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();

            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
        }
    }

    private class UriWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            String host = Uri.parse(url).getHost();

            return !host.equals("m.facebook.com");

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i(TAG, "url " + url + " view " + view);

            view.loadUrl("javascript:window.HtmlViewer.showHTML" +
                    "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

        }
    }

    class GetLinkData implements ResponseListener<String> {

        View articl_view;
        SearchArticles article;
        int pos, display_type;
        String url;

        GetLinkData(View articl_view, Context mContext, String url, SearchArticles article, int pos, int display_type) {
            this.articl_view = articl_view;
            this.article = article;
            this.pos = pos;
            this.url = url;
            this.display_type = display_type;
            new BuzzingaNetowrkServices().getwebLinkData(mContext, url, this);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
            article.source.setIMAGE_LINK("");
            listdata.set(pos, article);
            articl_view.setVisibility(View.GONE);
        }

        @Override
        public void onResponse(String response) {
            try {
                if (display_type == VIEW_ITEM) {
                    Document doc = Jsoup.parse(response);
                    doc.title();

                    String image_link = "";
                    Elements ele_img = null;
                    ele_img = doc.select("meta[property=" + Constants.IMAGE + "]");
                    if (ele_img == null)
                        ele_img = doc.select("meta[property=" + Constants.IMAGE_1 + "]");
                    if (ele_img == null)
                        ele_img = doc.select("meta[property=" + Constants.TWITTER_IMAGE + "]");


                    image_link = ele_img.attr("content");
                    article.source.setIMAGE_LINK(image_link);
                    listdata.set(pos, article);

                    if (!image_link.isEmpty()) {
                        articl_view.setVisibility(View.VISIBLE);
                        Log.i(TAG, "pos " + pos + " img" + image_link);

                        Glide.with(context).load(image_link).into(new SimpleTarget<Drawable>() {

                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                try{
                                setImage(((BitmapDrawable) resource).getBitmap(), (ImageView) articl_view);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    setImage(null, (ImageView) articl_view);
                                }
                            }

                        });

                    } else {
                        articl_view.setVisibility(View.GONE);
                    }

                } else if (display_type == VIEW_FACEBOOK) {

                    article.source.setFB_DATA(response);
                    listdata.set(pos, article);

                    setwebdata((WebView) articl_view, "", response);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ShowTweet extends Callback<Tweet> {

        final WeakReference<RelativeLayout> mWeakReference;
        int pos;

        ShowTweet(final Context context, final String tweetId,
                  final RelativeLayout tweet_embedded, final int pos) {

            this.pos = pos;
            mWeakReference = new WeakReference<RelativeLayout>(tweet_embedded);
            TweetUtils.loadTweet(Long.parseLong(tweetId), this);
        }

        @Override
        public void success(Result<Tweet> result) {
            final CompactTweetView compact_vew = new CompactTweetView(context, result.data,
                    R.style.tw__TweetLightStyle);

            compact_vew.setTweetLinkClickListener(new TweetLinkClickListener() {
                @Override
                public void onLinkClick(Tweet tweet, String url) {

                    if (onitemclicklistner != null) {
                        onitemclicklistner.itemClicked(compact_vew, pos,Constants.TWITTER);
                    }
                }
            });

            compact_vew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onitemclicklistner != null) {
                        onitemclicklistner.itemClicked(v, pos,Constants.TWITTER);
                    }
                }
            });

            if (mWeakReference != null) {
                RelativeLayout mtweetview = mWeakReference.get();
                if (mtweetview != null)
                    mtweetview.addView(compact_vew);
            }

        }

        @Override
        public void failure(TwitterException exception) {
            exception.printStackTrace();
        }
    }

}

