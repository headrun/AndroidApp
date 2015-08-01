package com.headrun.buzzinga.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.headrun.buzzinga.R;
import com.headrun.buzzinga.activities.HomeScreen;
import com.headrun.buzzinga.doto.Listitems;

import java.util.ArrayList;


public class ListViewAdapter extends ArrayAdapter<Listitems> {

    // Declare Variables
    Context context;
    //String[] source;
    ArrayList<Listitems> source;
    LayoutInflater inflater;
    ArrayList<String>selectitems=new ArrayList<String>();
    public ListViewAdapter(Context context, ArrayList<Listitems> source) {

        super(context, R.layout.source_layout, source);
        this.context = context;
        this.source = new ArrayList<Listitems>();
        this.source.addAll(source);


    }

    private static class listholder {
        public CheckBox sourcename;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.source_layout, parent, false);
        final listholder holder = new listholder();

        holder.sourcename = (CheckBox) itemView.findViewById(R.id.source_text);
        holder.sourcename.setOnCheckedChangeListener((HomeScreen) context);

        Listitems item = source.get(position);

        holder.sourcename.setText(item.getSourcename());
        holder.sourcename.setChecked(item.isSelectd());
        holder.sourcename.setTag(item);


        return itemView;
    }
}