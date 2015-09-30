package in.headrun.buzzinga.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

import in.headrun.buzzinga.BuzzingaApplication;
import in.headrun.buzzinga.activities.HomeScreen;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.doto.QueryData;

/**
 * Created by headrun on 23/7/15.
 */
public class FilterByDate extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    BuzzingaApplication buzzapp = new BuzzingaApplication();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        int monthe = month + 1;
        Log.i("Log_tag", "date is" + year + "-" + monthe + "-" + day);
        StringBuilder setdate = new StringBuilder();
        setdate.append(year + "-" + monthe + "-" + day);

        if (HomeScreen.DATEFLAG == 0) {
            HomeScreen.DATEFLAG = 1;
            HomeScreen.fromdate.setText(setdate.toString());
            buzzapp.BFROMDATE.add(HomeScreen.fromdate.getText().toString());
        } else {
            HomeScreen.todate.setText(setdate.toString());
            buzzapp.BTODATE.add(HomeScreen.todate.getText().toString());
        }
    }

    public void add_query_data() {
        buzzapp.QueryString.clear();
        buzzapp.QueryString.add(new QueryData(Constants.TRACKKEY, buzzapp.BTRACKKEY));
        buzzapp.QueryString.add(new QueryData(Constants.FROMDATE, buzzapp.BFROMDATE));
        buzzapp.QueryString.add(new QueryData(Constants.TODATE, buzzapp.BTODATE));
        buzzapp.QueryString.add(new QueryData(Constants.LOCATION, buzzapp.BLOCATION));
        buzzapp.QueryString.add(new QueryData(Constants.LANGUAGE, buzzapp.BLANGUAGE));
        buzzapp.QueryString.add(new QueryData(Constants.SEARCHKEY, buzzapp.BSEARCHKEY));
        buzzapp.QueryString.add(new QueryData(Constants.SOURCES, buzzapp.BSOURCES));
        buzzapp.QueryString.add(new QueryData(Constants.GENDER, buzzapp.BGENDER));
        buzzapp.QueryString.add(new QueryData(Constants.SENTIMENT, buzzapp.BSENTIMENT));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        add_query_data();
    }
}



