package in.headrun.buzzinga.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.utils.Utils;

/**
 * Created by sujith on 19/10/16.
 */
public class DateSelection_AdapterView extends RecyclerView.Adapter<DateSelection_AdapterView.MyViewHolder> {

    private List<String> horizontalList;
    private Context mcontext;
    private Utils.setOnItemDateSelClickListner onitemclicklistner = null;

    public DateSelection_AdapterView(Context mcontext, List<String> horizontalList) {
        this.horizontalList = horizontalList;
        this.mcontext = mcontext;
    }

    public void setClickListener(Utils.setOnItemDateSelClickListner onitemclicklistner) {
        this.onitemclicklistner = onitemclicklistner;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.date_sel_item_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.txtView.setText(horizontalList.get(position));

    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtView;

        public MyViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.txtView);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (onitemclicklistner != null) {
                onitemclicklistner.date_sel_itemClicked(v, getAdapterPosition());
            }
        }
    }
}