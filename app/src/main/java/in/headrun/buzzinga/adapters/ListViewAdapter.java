package in.headrun.buzzinga.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.doto.Listitems;
import in.headrun.buzzinga.utils.Utils;

public class ListViewAdapter extends BaseAdapter {

    String TAG = ListViewAdapter.this.getClass().getSimpleName();
    // Declare Variables
    Context context;
    Utils.setOnItemClickListner clicklistner;
    //String[] source;
    List<Listitems> source = null;
    List<Listitems> selectlist;
    LayoutInflater inflater;
    Utils utils;
    ViewHolder holder = null;
    Listitems item;
    public static int pos = -1;

    public ListViewAdapter(Context context, List<Listitems> source) {
        this.context = context;
        this.source = source;
        this.selectlist = new ArrayList<Listitems>();
        this.selectlist.addAll(source);
        utils = new Utils(context);

        utils.showLog(TAG, " filter len is " + source.size(), Config.ListViewAdapter);

    }

    public void setonitemclickListner(Utils.setOnItemClickListner cllicklistnerr) {
        clicklistner = cllicklistnerr;
    }


    @Override
    public int getCount() {
        return source.size();
    }

    @Override
    public Object getItem(int position) {
        return source.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.source_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        item = source.get(position);
        pos = position;
     /*   holder.filtertext.setText(item.getSourcename());
        holder.filtercheckbox.setChecked(item.isSelectd());
        holder.filtercheckbox.setTag(item);
*/


        holder.item_view.setText(item.getSourcename());
        holder.item_view.setChecked(item.isSelectd());

        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicklistner != null)
                    clicklistner.itemClicked(v, position);

            }
        });

      /*  holder.filtercheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox cb = (CheckBox) v;
                Listitems item = (Listitems) cb.getTag();
                item.setSelectd(cb.isChecked());

            }

        });*/
        return convertView;
    }

    class ViewHolder implements View.OnClickListener {
        /*@Bind(R.id.filtertext)
        TextView filtertext;

        @Bind(R.id.filtercheckbox)
        CheckBox filtercheckbox;*/

        @Bind(R.id.item_view)
        CheckedTextView item_view;

        @Bind(R.id.filter_item_lay)
        RelativeLayout filter_item_lay;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);


            filter_item_lay.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        source.clear();
        if (charText.length() == 0) {
            source.addAll(selectlist);
        } else {
            for (Listitems wp : selectlist) {
                if (wp.getSourcename().toLowerCase(Locale.getDefault()).contains(charText)) {
                    source.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}




