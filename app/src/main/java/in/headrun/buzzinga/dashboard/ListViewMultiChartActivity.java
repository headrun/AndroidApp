
package in.headrun.buzzinga.dashboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import in.headrun.buzzinga.R;
import in.headrun.buzzinga.dashboard.listviewitems.ChartItem;
import in.headrun.buzzinga.dashboard.listviewitems.LineChartItem;
import in.headrun.buzzinga.dashboard.listviewitems.PieChartItem;

public class ListViewMultiChartActivity extends AppCompatActivity {

    public String TAG = ListViewMultiChartActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN.WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_listview_chart);

        ListView lv = (ListView) findViewById(R.id.listView1);

        ArrayList<ChartItem> list = new ArrayList<ChartItem>();

        list.add(new LineChartItem(generateDataLine(1), getApplicationContext()));
        list.add(new PieChartItem(generateDataPie(2), getApplicationContext()));

        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);
    }

    /**
     * adapter that supports 3 different item types
     */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private LineData generateDataLine(int cnt) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();

        e1.add(new Entry(40, 1));
        e1.add(new Entry(60, 2));
        e1.add(new Entry(80, 3));
        e1.add(new Entry(100, 4));

        LineDataSet d1 = new LineDataSet(e1, "New DataSet " + cnt + ", (1)");
        d1.setLineWidth(3.5f);
        d1.setCircleSize(5.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[2]);
        d1.setDrawValues(false);

        ArrayList<Entry> e2 = new ArrayList<Entry>();

        e2.add(new Entry(100, 1));
        e2.add(new Entry(40, 2));
        e2.add(new Entry(60, 3));
        e2.add(new Entry(80, 4));

        LineDataSet d2 = new LineDataSet(e2, "New DataSet " + cnt + ", (2)");
        d2.setLineWidth(2.5f);
        d2.setCircleSize(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[1]);
        d2.setDrawValues(false);

        ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
        sets.add(d1);
        sets.add(d2);

        LineData cd = new LineData(getMonths(), sets);
        return cd;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */


    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private PieData generateDataPie(int cnt) {

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < 4; i++) {
            entries.add(new Entry((int) (Math.random() * 70) + 30, i));
        }

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        PieData cd = new PieData(getQuarters(), d);
        return cd;
    }

    private ArrayList<String> getQuarters() {

        ArrayList<String> q = new ArrayList<String>();
        q.add("1st Quarter");
        q.add("2nd Quarter");
        q.add("3rd Quarter");
        q.add("4th Quarter");

        return q;
    }


    private ArrayList<String> getMonths() {

        ArrayList<String> m = new ArrayList<String>();
        m.add("Jan");
        m.add("Feb");
        m.add("Mar");
        m.add("Apr");

        return m;
    }

        /*
            private ArrayList<String> getMonths () {
        SimpleDateFormat sdf = new SimpleDateFormat("MM dd", Locale.getDefault());
        ArrayList<String> m = new ArrayList<String>();
        Calendar calNow = Calendar.getInstance();
        Date fromdate = calNow.getTime();
        calNow.add(Calendar.MONTH, -1);
        Date todate = calNow.getTime();
        calNow.setTime(fromdate);

        Log.i("TAG", "fromdate is" + calNow.getTime() + "\n to date is" + todate);

        while (calNow.getTime().before(todate)) {
            calNow.add(Calendar.DATE, -1);
            Log.i(TAG,"date is"+sdf.format(calNow.getTime()));
            m.add(sdf.format(calNow.getTime()));
        }
        Log.i("TAG", "date list is" + m.toString());
        return m;
    }

    ArrayList<String> dates = new ArrayList<String>(25);
    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(toDate);
    while (cal2.getTime().before(newDateString)) {
        cal2.add(Calendar.DATE, 1);
        String datelist=(format.format(cal2.getTime()));
        dates.add(datelist); }
    System.out.println(dates);
    System.out.println(dates.get(3));

    */
}
