package in.headrun.buzzinga.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

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
        return listdata.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.search_article_lay, parent, false);
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

        if (holder instanceof ViewItemHolder) {

            item_holder = ((ViewItemHolder) holder);

            SearchArticles item = listdata.get(position);

            if (item.source.TITLE != null && item.source.TITLE.length() > 0) {
                item_holder.item1.setText(item.source.TITLE);
            } else {
                utils.showLog(TAG, position + " title is empty ", Config.SearchListDataAdapter);
            }

            String author = "";

            try {
                if (item.source.AUTHOR.NAME != null)
                    author = item.source.AUTHOR.NAME;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!author.isEmpty()) {
                item_holder.author.setText("By - " + author);
            } else {
                utils.showLog(TAG, position + " author is empty ", Config.SearchListDataAdapter);
            }

            item_holder.item2.setText(item.source.URL);

            if (item.source.TEXT != null) {
                item_holder.item3.setText(item.source.TEXT);
                item_holder.item3.setVisibility(View.VISIBLE);
            }

            utils.showLog(TAG,
                    "\nxtags 1  is \t" + item.source.XTAGS.toString(),

                    Config.SearchListDataAdapter);
            String source = sourType(item.source.XTAGS);
            int icon = sourceicon(source);
            if (icon != 0) {
                if (!item_holder.item1.getText().toString().isEmpty()) {
                    item_holder.item1.setVisibility(View.VISIBLE);
                    item_holder.item1.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
                    item_holder.item2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else {
                    item_holder.item1.setVisibility(View.GONE);
                    item_holder.item2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    item_holder.item2.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
                }
            } else {
                utils.showLog(TAG, position + " icon is empty ", Config.SearchListDataAdapter);
            }

            item_holder.article_lay.setBackgroundResource(applySentimentColor(sentimentType(item.source.XTAGS)));

            long seconds = Long.parseLong(item.source.DATE_ADDED);

            long millis = seconds * 1000;
            Date date = new Date(millis);
            String time = time_ago.timeAgo(seconds);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.getDefault());

            utils.showLog(TAG, "author is " + author + "epoch time is " + seconds + "local  date is  " + sdf.format(date),
                    Config.SearchListDataAdapter);

            item_holder.articledate.setText("" + time);

            utils.showLog(TAG, "author is " + author + "epoch time is " + seconds + " time ago date is  " + time,
                    Config.SearchListDataAdapter);


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
        @Bind(R.id.item3)
        public TextView item3;
        @Bind(R.id.author)
        public TextView author;
        @Bind(R.id.articledate)
        public TextView articledate;
        @Bind(R.id.article_lay)
        public CardView article_lay;


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

        if (xtag.size() > 0) {
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

        if (xtag.size() > 0) {
            utils.sentiment_xtags();
            Set<String> sentiment_keys = Constants.sentiment_map.keySet();
            for (String sentiment_key : sentiment_keys)
                if (xtag.contains(sentiment_key + "_sentiment_final"))
                    return sentiment_key;
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

    private int sourceicon(String type) {

        if (type != null) {
            utils.showLog(TAG, " source icon type " + type, Config.SearchListDataAdapter);
            switch (type) {
                case Constants.FACEBOOK:
                    return R.drawable.fb;
                case Constants.TWITTER:
                    return R.drawable.twitter;
                case Constants.NEWS:
                    return R.drawable.news;
                case Constants.BLOGS:
                    return R.drawable.blogs;
                case Constants.FORUMS:
                    return R.drawable.forum;
                case Constants.TUMBLR:
                    return R.drawable.tumblr;
                case Constants.QUORA:
                    return R.drawable.quora;
                case Constants.FLICKR:
                    return R.drawable.flikckr;
                case Constants.INSTAGRAM:
                    return R.drawable.instagram;
                case Constants.YOUTUBE:
                    return R.drawable.youtube;
                case Constants.GOOGLEPLUS:
                    return R.drawable.googleplus;
                case Constants.LINKDIN:
                    return R.drawable.linkdin;
            }
            return 0;
        } else {
            utils.showLog(TAG, " source icon type is null ", Config.SearchListDataAdapter);
        }
        return 0;
    }

}

