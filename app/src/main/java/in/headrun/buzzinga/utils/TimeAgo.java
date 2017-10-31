package in.headrun.buzzinga.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.headrun.buzzinga.R;

public class TimeAgo {

    protected Context context;

    public TimeAgo(Context context) {
        this.context = context;
    }

    public String timeAgo(Date date) {
        return timeAgo(date.getTime());
    }

    @SuppressLint("StringFormatInvalid")
    public String timeAgo(long epochtime) {

        long millis = epochtime * 1000;
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM yy", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM", Locale.getDefault());
        String local_time = sdf.format(date);
        Date local_date;

        try {
            local_date = sdf.parse(local_time);
        } catch (Exception e) {
            local_date = new Date();
        }

        long diff = new Date().getTime() - local_date.getTime();

        Resources r = context.getResources();

        String prefix = r.getString(R.string.time_ago_prefix);
        String suffix = r.getString(R.string.time_ago_suffix);

        double seconds = Math.abs(diff) / 1000;
        double minutes = seconds / 60;
        double hours = minutes / 60;
        double days = hours / 24;
        double years = days / 365;
        boolean display_ago = true;

        String words;

        if (seconds < 45) {
            words = r.getString(R.string.time_ago_seconds, Math.abs(seconds));
        } else if (seconds < 90) {
            words = r.getString(R.string.time_ago_seconds, Math.abs(seconds));
        } else if (minutes < 45) {
            words = r.getString(R.string.time_ago_minutes, Math.round(minutes));
        } else if (minutes < 90) {
            words = r.getString(R.string.time_ago_hours, 1);
        } else if (hours < 24) {
            words = r.getString(R.string.time_ago_hours, Math.round(hours));
        } /*else if (hours < 42) {
            words = r.getString(R.string.time_ago_day, 1);
        } else if (days < 30) {
            words = r.getString(R.string.time_ago_days, Math.round(days));
        } else if (days < 45) {
            words = r.getString(R.string.time_ago_month, 1);
        }*/ else if (days < 365) {
            //words = r.getString(R.string.time_ago_months, Math.round(days / 30));
            words = sdf2.format(date);
            display_ago = false;
        } /*else if (years < 1.5) {
            words = r.getString(R.string.time_ago_year, 1);
        } */ else {
            // words = r.getString(R.string.time_ago_years, Math.round(years));
            words = sdf1.format(date);
            display_ago = false;
        }

        StringBuilder sb = new StringBuilder();

        if (prefix != null && prefix.length() > 0) {
            sb.append(prefix).append(" ");
        }

        sb.append(words);

        if (suffix != null && suffix.length() > 0 && display_ago == true) {
            sb.append(" ").append(suffix);
        }

        return sb.toString().trim();
    }
}
