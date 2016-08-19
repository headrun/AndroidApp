package in.headrun.buzzinga.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.doto.SearchArticles;
import in.headrun.buzzinga.utils.Utils;

/**
 * Created by headrun on 10/7/15.
 */
public class SearchListDataAdapter extends RecyclerView.Adapter<SearchListDataAdapter.ViewHolder> {

    public int count;
    String TAG = SearchListDataAdapter.this.getClass().getSimpleName();
    Context context;
    List<SearchArticles> listdata = new ArrayList<>();
    LayoutInflater inflater;
    ViewHolder holder = null;
    Utils utils;
    private Utils.setOnItemClickListner onitemclicklistner = null;

    public SearchListDataAdapter(Context context, List<SearchArticles> listdata) {

        Log.i(TAG, "search data adapter is" + listdata.size());

        this.context = context;
        this.listdata = listdata;
        utils = new Utils(context);

    }

    public void setClickListener(Utils.setOnItemClickListner onitemclicklistner) {
        this.onitemclicklistner = onitemclicklistner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_article_lay, parent, false);

        holder = new ViewHolder(v);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        SearchArticles item = listdata.get(position);

        if (item.source.TITLE != null && item.source.TITLE.length() > 0)
            holder.item1.setText(item.source.TITLE);


        String author = "";

        if (item.source.AUTHOR.NAME != null)
            author = item.source.AUTHOR.NAME;

        if (!author.isEmpty())
            holder.author.setText("By - " + author);


        holder.item2.setText(item.source.URL);

        if (item.source.TEXT != null) {
            holder.item3.setText(item.source.TEXT);
            holder.item3.setVisibility(View.VISIBLE);
        }

        String source = sourType(item.source.XTAGS);
        int icon = sourceicon(source);
        if (icon != 0) {
            if (!holder.item1.getText().toString().isEmpty()) {
                holder.item1.setVisibility(View.VISIBLE);
                holder.item1.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
                holder.item2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                holder.item1.setVisibility(View.GONE);
                holder.item2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.item2.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
            }
        }

        holder.article_lay.setBackgroundResource(applySentimentColor(sentimentType(item.source.XTAGS)));

        long seconds = Long.parseLong(item.source.DATE_ADDED);
        Log.i(TAG, "article time is" + seconds);
        long millis = seconds * 1000;
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.getDefault());
        holder.articledate.setText(sdf.format(date));
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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


        /*  @Bind(R.id.title)
          public TextView title;
          @Bind(R.id.text)
          public TextView text;
          @Bind(R.id.url)
          public TextView url;
          @Bind(R.id.articledate)
          public TextView articledate;
          @Bind(R.id.author)
          public TextView author;

          @Bind(R.id.source_icon)
          public ImageView article_icon;
          @Bind(R.id.article_lay)
          public View article_lay;
  */
        public ViewHolder(View view) {
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

    public String sourType(List<String> xtag) {

        if (xtag.size() > 0) {
            Set<String> source_keys = Constants.source_map.keySet();

            for (String key : source_keys) {

                if (xtag.toString().toLowerCase().contains(key.toLowerCase() + "_"))
                    return key;

                if (key.contains("facebook") && (xtag.toString().toLowerCase().contains("fb" + "_") ||
                        xtag.toString().toLowerCase().contains("fbpages" + "_")))
                    return key;
            }
        }
        return "";
    }


    /*public String sourType(List<String> xtag) {

        if (xtag.size() > 0) {
            Set<String> source_keys = Constants.source_map.keySet();

            Iterator<String> iterte = source_keys.iterator();

            while (iterte.hasNext()) {
                String key = iterte.next().toLowerCase();

                if (xtag.toString().toLowerCase().contains(key) ||
                        xtag.toString().toLowerCase().contains("fb") || xtag.toString().toLowerCase().contains("fbpages"))
                    return key;
            }
        }
        return "";
    }*/

    public String genderType(List<String> xtag) {

        if (xtag.size() > 0) {
            Set<String> gender_keys = Constants.gender_map.keySet();
            for (String gender_key : gender_keys)
                if (xtag.contains(gender_key))
                    return gender_key;
        }
        return "";
    }

    public String sentimentType(List<String> xtag) {

        if (xtag.size() > 0) {
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

        if (type != null)
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
    }
}