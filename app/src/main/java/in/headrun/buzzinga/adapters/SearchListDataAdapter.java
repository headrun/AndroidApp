package in.headrun.buzzinga.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.doto.SearchArticles;
import in.headrun.buzzinga.utils.TimeAgo;
import in.headrun.buzzinga.utils.Utils;

/**
 * Created by headrun on 10/7/15.
 */
public class SearchListDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    String TAG = SearchListDataAdapter.this.getClass().getSimpleName();
    Context context;
    List<SearchArticles> listdata = new ArrayList<>();
    LayoutInflater inflater;
    ViewItemHolder item_holder = null;
    Utils utils;
    TimeAgo time_ago;

    private final int VIEW_PROG = 0;
    private final int VIEW_ITEM = 1;
    private final int VIEW_TWITTER = 2;
    private final int VIEW_GOOGLE = 3;
    private final int VIEW_FACEBOOK = 4;

    private Utils.setOnItemClickListner onitemclicklistner = null;

    public SearchListDataAdapter(Context context, List<SearchArticles> listdata) {

        Log.i(TAG, "search data adapter is" + listdata.size());

        this.context = context;
        this.listdata = listdata;
        utils = new Utils(context);
        time_ago = new TimeAgo(context);
    }

    public void setClickListener(Utils.setOnItemClickListner onitemclicklistner) {
        this.onitemclicklistner = onitemclicklistner;
    }

    @Override
    public int getItemViewType(int position) {
        if (listdata.get(position) != null) {
            if (listdata.get(position) instanceof SearchArticles) {
                /*SearchArticles article_item = listdata.get(position);
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
                }*/
                return VIEW_ITEM;
            }
        }
        return VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;

        if (viewType == VIEW_TWITTER || viewType == VIEW_GOOGLE || viewType == VIEW_FACEBOOK || viewType == VIEW_ITEM) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.twitter_lay_adapter, parent, false);
            vh = new TwitterViewItemHolder(v);

        } else if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.search_new_lay, parent, false);
            vh = new ViewItemHolder(v);

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

        String author = null;
        String text = null;
        String img_url = null;
        String source = null;
        String time = null;
        String sentiment_type = null;
        int color = 0;
        int source_icon = -1;

        if (item != null && item.source != null) {
            source = sourType(item.source.XTAGS);
            sentiment_type = sentimentType(item.source.XTAGS);
            color = applySentimentColor(sentiment_type);
            time = time_ago.timeAgo(Long.parseLong(item.source.DATE_ADDED));
            text = item.source.TEXT;
            if (item.source.AUTHOR != null)
                author = item.source.AUTHOR.NAME;
            if (source != null)
                source_icon = sourceicon(source);
        }

        if (holder instanceof ViewItemHolder) {

            item_holder = ((ViewItemHolder) holder);

            try {
                if (item.source.AUTHOR.NAME != null)
                    author = item.source.AUTHOR.NAME;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (item.source.TITLE != null && item.source.TITLE.length() > 0) {
                item_holder.item1.setText(item.source.TITLE);
                item_holder.item1.setVisibility(View.VISIBLE);
            } else if (author != null) {
                item_holder.item1.setText("@" + author);
                item_holder.item1.setVisibility(View.VISIBLE);
            } else {
                item_holder.item1.setVisibility(View.GONE);
                utils.showLog(TAG, position + " title is empty ", Config.SearchListDataAdapter);
            }

            if (text != null) {
                item_holder.item2.setText(text);
                item_holder.item2.setVisibility(View.VISIBLE);
            }

            utils.showLog(TAG, "\nxtags 1  is \t" + item.source.XTAGS.toString(), Config.SearchListDataAdapter);

            item_holder.author.setText("By - " + source);
            item_holder.article_lay.setBackgroundResource(color);
            item_holder.articledate.setText("" + time);

        } else if (holder instanceof TwitterViewItemHolder) {

            final TwitterViewItemHolder twitter_holder = ((TwitterViewItemHolder) holder);

            twitter_holder.article_time.setText("" + time);
            twitter_holder.source_icon.setImageResource(source_icon);


            if (text != null) {
                twitter_holder.article_text.setText(text);
            }


            setImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar), twitter_holder.article_img);

            //twitter_holder.twitter_article_lay.setBackgroundResource(color);
            if (sentiment_type != null)
                twitter_holder.sentiment_type.setImageDrawable(ContextCompat.getDrawable(context, getEmoji(sentiment_type)));


            if (item.source != null && item.source.original_data != null && item.source.original_data.user_data != null) {

                SearchArticles.UserData data = item.source.original_data.user_data;


                if (data.profile_image_url != null && !data.profile_image_url.isEmpty()) {
                    img_url = data.profile_image_url;
                } else if (data.profile_image_url_https != null && !data.profile_image_url_https.isEmpty()) {
                    img_url = data.profile_image_url_https;
                }

                img_url = TwitterImage(img_url);

                if (img_url != null)
                    Glide.with(context).load(img_url).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            setImage(((BitmapDrawable) resource).getBitmap(), twitter_holder.article_img);

                        }
                    });

                if (data.name != null)
                    twitter_holder.display_name.setText(data.name);
                else
                    twitter_holder.display_name.setVisibility(View.GONE);

                if (data.screen_name != null)
                    twitter_holder.article_name.setText("@" + data.screen_name);
                else
                    twitter_holder.article_name.setVisibility(View.GONE);

            } else if (item.source.original_data != null && item.source.original_data.google_user != null &&
                    item.source.original_data.google_user.google_img != null) {

                img_url = item.source.original_data.google_user.google_img.img_url;
                img_url = GoogleImage(img_url);
                if (img_url != null) {
                    Glide.with(context).load(img_url).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            setImage(((BitmapDrawable) resource).getBitmap(), twitter_holder.article_img);

                        }
                    });
                }

                if (author != null)
                    twitter_holder.display_name.setText(author);
                twitter_holder.article_name.setVisibility(View.GONE);
            } else {
                try {
                    if (author != null) {
                        author = item.source.AUTHOR.NAME;
                        twitter_holder.display_name.setText(author);
                        twitter_holder.article_name.setVisibility(View.GONE);
                        twitter_holder.article_img.setImageResource(source_icon);
                        twitter_holder.source_icon.setVisibility(View.GONE);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            ((ProgrssHolder) holder).progress.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public class ViewItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.item1)
        public TextView item1;
        @Bind(R.id.item2)
        public TextView item2;
        /*@Bind(R.id.item3)
        public TextView item3;*/
        @Bind(R.id.author)
        public TextView author;
        @Bind(R.id.articledate)
        public TextView articledate;
        @Bind(R.id.article_lay)
        public RelativeLayout article_lay;

        public ViewItemHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (onitemclicklistner != null) {
                onitemclicklistner.itemClicked(v, getAdapterPosition());
            }

        }
    }

    public class TwitterViewItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.article_img)
        public ImageView article_img;
        @Bind(R.id.display_name)
        public TextView display_name;
        @Bind(R.id.article_name)
        public TextView article_name;
        @Bind(R.id.article_time)
        public TextView article_time;
        @Bind(R.id.article_text)
        public TextView article_text;
        @Bind(R.id.twitter_article_lay)
        public LinearLayout twitter_article_lay;
        @Bind(R.id.source_icon)
        public ImageView source_icon;
        @Bind(R.id.sentiment_type)
        public ImageView sentiment_type;

        public TwitterViewItemHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            twitter_article_lay.setOnClickListener(this);

            Utils.changeFontFace(display_name, "fonts/MuseoSans-700.otf", context);
            //Utils.changeFontFace(article_name,"fonts/MuseoSans-500.otf",context);
            Utils.changeFontFace(article_time, "fonts/MuseoSans-500.otf", context);
            Utils.changeFontFace(article_text, "fonts/MuseoSans-300.otf", context);

        }

        @Override
        public void onClick(View v) {

            if (onitemclicklistner != null) {
                onitemclicklistner.itemClicked(v, getAdapterPosition());
            }
        }
    }

    public class ProgrssHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.progres)
        public ProgressBar progress;

        public ProgrssHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {

        }

    }

    public String sourType(List<String> xtag) {

        if (xtag != null && xtag.size() > 0) {
            utils.source_xtags();
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
            utils.showLog(TAG, " source items  are empty list ", Config.SearchListDataAdapter);
        }
        return "";
    }

    public String genderType(List<String> xtag) {

        if (xtag.size() > 0) {
            utils.genter_xtags();
            Set<String> gender_keys = Constants.gender_map.keySet();
            for (String gender_key : gender_keys)
                if (xtag.contains(gender_key))
                    return gender_key;
        }
        return "";
    }

    public String sentimentType(List<String> xtag) {

        if (xtag != null && xtag.size() > 0) {
            utils.sentiment_xtags();
            Set<String> sentiment_keys = Constants.sentiment_map.keySet();
            /*for (String sentiment_key : sentiment_keys)
                if (xtag.contains(sentiment_key))
                    return sentiment_key;*/

            for (String tag : xtag) {
                if (tag.contains(Constants.NEGATIVE))
                    return Constants.NEGATIVE;
                else if (tag.contains(Constants.POSITIVE))
                    return Constants.POSITIVE;
                else if (tag.contains(Constants.NEUTRAL))
                    return Constants.NEUTRAL;
            }
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

    private int getEmoji(String sentimet) {

        if (sentimet != null)
            if (sentimet.contains(Constants.POSITIVE)) {
                return R.drawable.positive1;
            } else if (sentimet.contains(Constants.NEGATIVE)) {
                return R.drawable.negative1;
            } else
                return R.drawable.neutral1;

        return R.drawable.neutral1;
    }


    private int applySentimentemoji(String sentimet) {

        if (sentimet != null)
            if (sentimet.contains(Constants.POSITIVE)) {
                return 0x1F642;
            } else if (sentimet.contains(Constants.NEGATIVE)) {
                return 0x2639;
            } else
                return 0x1F610;

        return 0x1F610;
    }


    private int sourceicon(String type) {

        if (type != null) {
            utils.showLog(TAG, " source icon type " + type, Config.SearchListDataAdapter);
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
            utils.showLog(TAG, " source icon type is null ", Config.SearchListDataAdapter);
        }
        return 0;
    }

    public void setImage(Bitmap bitmap, ImageView avatar_img) {

        if (bitmap != null) {
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(),
                    Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true));
            drawable.setCircular(true);
            avatar_img.setImageDrawable(drawable);

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


}

