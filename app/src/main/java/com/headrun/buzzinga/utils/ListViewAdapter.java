package com.headrun.buzzinga.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filterable;
import android.widget.TextView;

import com.headrun.buzzinga.R;
import com.headrun.buzzinga.doto.Listitems;

import java.util.ArrayList;


public class ListViewAdapter extends ArrayAdapter<Listitems> implements Filterable {

    // Declare Variables
    Context context;
    //String[] source;
    ArrayList<Listitems> source;
    LayoutInflater inflater;
    ArrayList<Listitems> selectlist = new ArrayList<Listitems>();
    // ValueFilter valueFilter;

    public ListViewAdapter(Context context, ArrayList<Listitems> source) {

        super(context, R.layout.source_layout, source);
        this.context = context;
        this.source = new ArrayList<Listitems>();
        this.source.addAll(source);

        selectlist = source;
    }

    private static class listholder {
        public CheckBox filtercheckbox;
        public TextView filtertext;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        listholder holder = null;

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.source_layout, null);
            holder = new listholder();

            holder.filtercheckbox = (CheckBox) convertView.findViewById(R.id.filtercheckbox);
            holder.filtertext = (TextView) convertView.findViewById(R.id.filtertext);
            // holder.filtercheckbox.setOnCheckedChangeListener((HomeScreen) context);

            convertView.setTag(holder);

            holder.filtercheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CheckBox cb = (CheckBox) v;
                    Listitems item = (Listitems) cb.getTag();
                    item.setSelectd(cb.isChecked());


                }

            });


        } else {
            holder = (listholder) convertView.getTag();
        }

        Listitems item = source.get(position);

        holder.filtertext.setText(item.getSourcename());
        holder.filtercheckbox.setChecked(item.isSelectd());
        holder.filtercheckbox.setTag(item);

        return convertView;
    }

  /*  @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }


    public class ValueFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {

                ArrayList<Listitems> filterList = new ArrayList<Listitems>();
                for (int i = 0; i < selectlist.size(); i++) {
                    if ((selectlist.get(i).getSourcename().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {

                        Listitems item = new Listitems(selectlist.get(i).getXtag(), selectlist.get(i).getSourcename(), selectlist.get(i).isSelectd());

                        filterList.add(item);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = selectlist.size();
                results.values = selectlist;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            source = (ArrayList<Listitems>) results.values;
            notifyDataSetChanged();
        }
    }
    */
}