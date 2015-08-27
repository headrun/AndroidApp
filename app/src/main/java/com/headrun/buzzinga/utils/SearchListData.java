package com.headrun.buzzinga.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.headrun.buzzinga.R;
import com.headrun.buzzinga.doto.SearchDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by headrun on 10/7/15.
 */
public class SearchListData extends ArrayAdapter<SearchDetails> {

    String TAG=SearchListData.this.getClass().getSimpleName();
    Context context;
    ArrayList<SearchDetails> listdata;
    LayoutInflater inflater;
    public int count;

    public SearchListData(Context context, int resource) {
        super(context, resource);
    }

    public SearchListData(Context context, ArrayList<SearchDetails> listdata) {
        super(context, R.layout.search_data, listdata);
        Log.i("Log_tag", "search data adapter is" + listdata.size());

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

    private static class SearchHolder {

        public TextView title;
        public TextView text;
        public TextView url;
        public TextView articledate;
        public TextView author;
        public TextView sentimentcolor;
        public ImageView article_icon;
        public View title_lay;
        public TextView sentimetncolor;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.search_data, parent, false);

        SearchHolder holder = new SearchHolder();

        holder.title = (TextView) itemView.findViewById(R.id.title);
        holder.text = (TextView) itemView.findViewById(R.id.text);
        holder.url = (TextView) itemView.findViewById(R.id.url);
        holder.articledate = (TextView) itemView.findViewById(R.id.articledate);
        holder.author = (TextView) itemView.findViewById(R.id.author);
        holder.sentimentcolor = (TextView) itemView.findViewById(R.id.sentimetncolor);
        holder.article_icon = (ImageView) itemView.findViewById(R.id.source_icon);
        holder.title_lay = itemView.findViewById(R.id.title_lay);
        holder.sentimetncolor = (TextView)itemView.findViewById(R.id.sentimetncolor);


        SearchDetails item = getItem(position);
        holder.title.setText(item.getTitle());

        holder.url.setText(item.getUrl());

        if (item.getText()!=null) {
            holder.text.setText(item.getText());
           // holder.text.setBackgroundResource(R.drawable.roundborder);
            holder.text.setVisibility(View.VISIBLE);
        }

        if (item.getAuthor() !=null)
            holder.author.setText("By - " + item.getAuthor());

        holder.sentimentcolor.setBackgroundColor(Color.parseColor("#5cb85c"));
        holder.article_icon.setImageResource(R.drawable.filter);
        long seconds = Long.parseLong(item.getArticledate());
        long millis = seconds * 1000;
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        holder.articledate.setText(sdf.format(date));
        Log.i("Log_tag","sentiment color is"+item.getSentiment()+"drawable  is"+applySentimentColor(item.getSentiment()));
        holder.sentimetncolor.setBackgroundResource(applySentimentColor(item.getSentiment()));

        return itemView;
    }

    private int applySentimentColor(String sentimet){

        if(sentimet!=null)
        if(sentimet.contains("positive")) {
         Log.i(TAG,"pos");
            return R.drawable.pos_sentiment;
        }else if (sentimet.contains("negative")) {
            Log.i(TAG,"neg");
            return R.drawable.neg_sentiment;
        }
        Log.i(TAG,"neu");
        return R.drawable.neu_sentiment;

    }

}

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
