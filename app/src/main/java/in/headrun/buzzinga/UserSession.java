package in.headrun.buzzinga;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Created by headrun on 22/7/15.
 */
public class UserSession {

    public static final String TSESSION = "tsession";
    private static final String PREFER_NAME = "BUZZINGA";
    public String TAG = UserSession.this.getClass().getSimpleName();
    public SharedPreferences pref;
    public Context _context;
    public Editor editor;
    int PRIVATE_MODE = 0;

    public static final String count_CLUBBEDQUERY="count_clubbedquery";
    public static final String search_CLUBBEDQUERY="search_clubbedquery";
    public static final String TIMEZONE="timezone";
    public static final String SETUP="setup";
    public static final String SCROLLID="scrollid";
    public static final String LATEST_DATE="laestdate";
    public static final String TRACK_KEY="trackkey";


    public UserSession(Context context) {
        this._context = context;
        Log.i(TAG,"context is\t"+context);
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }

    public String getTSESSION() {
        String sess = pref.getString(TSESSION, "");
        Log.i("Log_Tag", "sess" + sess);
        return sess;
    }

    public void setTSESSION(String sessiondata) {
        Log.i("Log_tag", "sessiondata is" + sessiondata);
        editor.putString(TSESSION, sessiondata);
        editor.commit();
    }

    public void clearsession() {
        Log.i(TAG, "before" + pref.getString(TSESSION, "null"));
        editor.remove(TSESSION);
        editor.commit();
        Log.i(TAG, "after" + pref.getString(TSESSION, "null"));
    }

    public void setClubbedquery(String clubbedquery) {
        Log.i(TAG, "before querry is" + clubbedquery);
        editor.putString(count_CLUBBEDQUERY, clubbedquery);
        editor.commit();
        String query = pref.getString(count_CLUBBEDQUERY, "");
        Log.i(TAG, "after querry is" + query);

    }

    public String getClubbedquery() {
        String clubbedquery = pref.getString(count_CLUBBEDQUERY, "");
        Log.i(TAG,"getcount querry is"+clubbedquery);
        return clubbedquery;
    }

    public void set_search_Clubbedquery(String clubbedquery) {
        Log.i(TAG, "before querry is" + clubbedquery);
        editor.putString(search_CLUBBEDQUERY, clubbedquery);
        editor.commit();
        String query = pref.getString(search_CLUBBEDQUERY, "");
        Log.i(TAG, "after querry is" + query);

    }

    public String get_search_Clubbedquery() {
        String clubbedquery = pref.getString(search_CLUBBEDQUERY, "");
        Log.i(TAG,"get querry is"+clubbedquery);
        return clubbedquery;
    }

    public void setTIMEZONE(String timezone) {
        Log.i(TAG, "before time zone is" + timezone);
        editor.putString(TIMEZONE, timezone);
        editor.commit();
        String time_zone = pref.getString(TIMEZONE,"");
        Log.i(TAG, "after time zone is" + time_zone);

    }


    public String getTIMEZONE() {
        String timezone = pref.getString(TIMEZONE,"null");
        Log.i(TAG,"get time zone is"+timezone);
        return timezone;
    }
    public void setSETUP(String setup){
        Log.i(TAG, "before set_up  is" + setup);
        editor.putString(SETUP, setup);
        editor.commit();
        String set_up=pref.getString(SETUP,"");
        Log.i(TAG, "after set_up  is" + set_up);

    }

    public String getSETUP() {

        String setup=pref.getString(SETUP,"null");
        Log.i(TAG,"get time setup is"+setup);
        return setup;
    }
    public void setSCROLLID(String scrollid){
        Log.i(TAG, "before scroolid is" + scrollid);
        editor.putString(SCROLLID, scrollid);
        editor.commit();

        String scroll_id=pref.getString(SCROLLID,"");
        Log.i(TAG, "after scroolid is" + scroll_id);

    }

    public String getSCROLLID() {
        String scrollid=pref.getString(SCROLLID,"null");
        Log.i(TAG,"get time scroolid is"+scrollid);
        return scrollid;
    }

    public void setLatestDate(String latestDate) {
        Log.i(TAG, "before latestDate is" + latestDate);
        editor.putString(LATEST_DATE, latestDate);
        editor.commit();
        String query = pref.getString(LATEST_DATE, "");
        Log.i(TAG, "after date is" + query);

    }

    public String getLatestDate() {
        String getLastestDate = pref.getString(LATEST_DATE, "0");
        Log.i(TAG,"get querry is"+getLastestDate);
        return getLastestDate;
    }


    public void setTrackKey(String trackKey) {
        Log.i(TAG, "before trackKey is" + trackKey);
        editor.putString(TRACK_KEY, trackKey);
        editor.commit();
        String query = pref.getString(TRACK_KEY, "");
        Log.i(TAG, "after TrackKey is" + query);

    }

    public String getTrackKey() {
        String getTrackKey = pref.getString(TRACK_KEY, "0");
        Log.i(TAG,"get TrackKey is"+getTrackKey);
        return getTrackKey;
    }

}

