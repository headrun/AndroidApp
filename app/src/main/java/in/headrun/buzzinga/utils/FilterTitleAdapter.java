package in.headrun.buzzinga.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import in.headrun.buzzinga.R;

/**
 * Created by headrun on 10/7/15.
 */
public class FilterTitleAdapter extends BaseAdapter {

    public Context context;
    public String[] titles;
    public TypedArray images;
    LayoutInflater inflater;


    public FilterTitleAdapter(Context context, String[] titles,TypedArray images) {

        this.context = context;
        this.titles = titles;
        this.images=images;
    }


    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return titles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class TitleHolder {
        public ImageView titleimage;
        public TextView title;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TitleHolder holder=null;
        if(convertView ==null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.source_titles, null);
             holder = new TitleHolder();

            holder.title = (TextView) convertView.findViewById(R.id.texttilte);
            holder.titleimage = (ImageView) convertView.findViewById(R.id.titleimage);

            holder.title.setText(titles[position]);
            holder.titleimage.setImageResource(images.getResourceId(position,-1));
            holder.titleimage.setTag(titles[position]);

        }else{
            holder=(TitleHolder)convertView.getTag();
        }
        return convertView;
    }
}

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
