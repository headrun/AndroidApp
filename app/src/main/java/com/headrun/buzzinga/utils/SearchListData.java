package com.headrun.buzzinga.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

        SearchDetails item = getItem(position);
        holder.title.setText(item.getTitle());
        holder.text.setText(item.getText());
        holder.url.setText(item.getUrl());
        Log.i("Log_tag", item.getUrl());
        if (!item.getAuthor().equals("1"))
            holder.author.setText("By" + item.getAuthor());


        long seconds = Long.parseLong(item.getArticledate());
        long millis = seconds * 1000;
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        holder.articledate.setText(sdf.format(date));


        return itemView;
    }
}

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
