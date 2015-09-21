package in.headrun.buzzinga.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import in.headrun.buzzinga.R;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.doto.SearchDetails;

/**
 * Created by headrun on 10/7/15.
 */
public class SearchListData extends ArrayAdapter<SearchDetails> {

    public int count;
    String TAG = SearchListData.this.getClass().getSimpleName();
    Context context;
    ArrayList<SearchDetails> listdata;
    LayoutInflater inflater;

    public SearchListData(Context context, int resource) {
        super(context, resource);
    }

    public SearchListData(Context context, ArrayList<SearchDetails> listdata) {
        super(context, R.layout.search_data, listdata);
        Log.i(TAG, "search data adapter is" + listdata.size());

        this.context = context;
        this.listdata = new ArrayList<>();
        this.listdata = new ArrayList<SearchDetails>();
        if (this.listdata.isEmpty()) {
            this.listdata.addAll(listdata);
        } else {
            this.listdata.notifyAll();
            this.listdata.addAll(listdata);
        }

    }

    public int checklistsize() {

        if (listdata.isEmpty())
            return 1;
        count = listdata.size();
        return count;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.search_data, parent, false);

        Log.i(TAG, "searchDetails view");
        SearchHolder holder = new SearchHolder();

        holder.title = (TextView) itemView.findViewById(R.id.title);
        holder.text = (TextView) itemView.findViewById(R.id.text);
        holder.url = (TextView) itemView.findViewById(R.id.url);
        holder.articledate = (TextView) itemView.findViewById(R.id.articledate);
        holder.author = (TextView) itemView.findViewById(R.id.author);
        holder.sentimentcolor = (TextView) itemView.findViewById(R.id.sentimetncolor);
        holder.article_icon = (ImageView) itemView.findViewById(R.id.source_icon);
        holder.title_lay = itemView.findViewById(R.id.title_lay);
        holder.article_lay = itemView.findViewById(R.id.article_lay);
        SearchDetails item = getItem(position);


        if (item.getTitle().length() > 0)
            holder.title.setText(item.getTitle());
        else {
            Log.i(TAG, "articley type" + item.getArticle_type());
            if (item.getArticle_type() != null)
                if (item.getArticle_type().equals(Constants.TWITTER))
                    holder.title.setText("@" + item.getAuthor());
        }

        holder.url.setText(item.getUrl());

        if (item.getText() != null) {
            holder.text.setText(item.getText());
            holder.text.setVisibility(View.VISIBLE);
        }

        if (item.getAuthor() != null)
            holder.author.setText("By - " + item.getAuthor());


        int icon = source_icon(item.article_type);
        if (icon != 0)
            holder.article_icon.setImageResource(icon);

        long seconds = Long.parseLong(item.getArticledate());
        Log.i(TAG, "article time is" + seconds);
        long millis = seconds * 1000;
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.getDefault());
        holder.articledate.setText(sdf.format(date));

        Log.i(TAG, "epoch time is\t" + item.getArticledate() + "\ntime is\t" + sdf.format(date));

        holder.article_lay.setBackgroundResource(applySentimentColor(item.getSentiment()));

        return itemView;
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

    private int source_icon(String type) {

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

    private static class SearchHolder {

        public TextView title;
        public TextView text;
        public TextView url;
        public TextView articledate;
        public TextView author;
        public TextView sentimentcolor;
        public ImageView article_icon;
        public View title_lay;
        public View article_lay;
    }


}