package in.headrun.buzzinga.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import in.headrun.buzzinga.R;
import in.headrun.buzzinga.doto.Listitems;

public class ListViewAdapter extends BaseAdapter{

    String TAG=ListViewAdapter.this.getClass().getSimpleName();
    // Declare Variables
    Context context;
    //String[] source;
    ArrayList<Listitems> source = null;
    ArrayList<Listitems> selectlist = null;
    LayoutInflater inflater;


    public ListViewAdapter(Context context, ArrayList<Listitems> source) {
        this.context = context;
        this.source = source;
        Log.i(TAG,"size is"+source.size()+"list 1 is"+source.get(1).getSourcename());
        //this.source.addAll(source);
        this.selectlist = source;
    }



    private static class listholder {
        public CheckBox filtercheckbox;
        public TextView filtertext;
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


}
