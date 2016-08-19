package in.headrun.buzzinga.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.activities.Filtering;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.utils.Utils;

/**
 * Created by headrun on 10/7/15.
 */
public class FilterTitleAdapter extends BaseAdapter {

    public String TAG = FilterTitleAdapter.class.getSimpleName();

    public Context context;
    public String[] titles;
    public TypedArray images;
    LayoutInflater inflater;
    Utils utils;
    TitleHolder holder = null;


    public FilterTitleAdapter(Context context, String[] titles, TypedArray images) {

        this.context = context;
        this.titles = titles;
        this.images = images;
        utils = new Utils(context);
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

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.source_titles, null);
            holder = new TitleHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TitleHolder) convertView.getTag();
        }

        holder.title.setText(titles[position]);
        utils.showLog(TAG, "sel " + titles[position] + " item count is " + selCount(titles[position]), Config.FilterTitleAdapter);
        holder.sel_count.setText(selCount(titles[position]));

        return convertView;
    }

    public class TitleHolder {

        //  public ImageView titleimage;
        @Bind(R.id.texttilte)
        public TextView title;
        @Bind(R.id.sel_count)
        public TextView sel_count;

        public TitleHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

    public String selCount(String sel_source) {

        int count = 0;
        if (!sel_source.isEmpty())
            switch (sel_source.toLowerCase()) {

                case "sources":
                    count = sel_count(Filtering.sel_source_array);
                    if (count <= 0 && Filtering.first_source == 0)
                        count = utils.countIS(utils.userSession.getSources_data());
                    break;
                case "sentiment":
                    count = sel_count(Filtering.sel_sentiment_array);
                    if (count <= 0 && Filtering.first_sentiment == 0)
                        count = utils.countIS(utils.userSession.getSentiment_data());
                    break;
                case "gender":
                    count = sel_count(Filtering.sel_gender_array);
                    if (count <= 0 && Filtering.first_gender == 0)
                        count = utils.countIS(utils.userSession.getGender_data());
                    break;
                case "location":
                    count = sel_count(Filtering.sel_loc_array);
                    if (count <= 0 && Filtering.first_loc == 0)
                        count = utils.countIS(utils.userSession.getLoc_data());
                    break;
                case "language":
                    count = sel_count(Filtering.sel_lang_array);
                    if (count <= 0 && Filtering.first_lang == 0)
                        count = utils.countIS(utils.userSession.getLang_data());
                    break;

            }
        return count != 0 ? "" + count : "";
    }

    public int sel_count(SparseBooleanArray sparse_array) {

        int count = 0;
        for (int i = 0; i < sparse_array.size(); i++) {
            int key = sparse_array.keyAt(i);
            if (sparse_array.get(key))
                count++;
        }
        return count;

    }

}

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
