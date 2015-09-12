package in.headrun.buzzinga;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Created by headrun on 22/7/15.
 */
public class UserSession {

    public static final String TSESSION = "null";
    private static final String PREFER_NAME = "BUZZINGA";
    public String TAG = UserSession.this.getClass().getSimpleName();
    public SharedPreferences pref;
    public Context _context;
    public Editor editor;
    int PRIVATE_MODE = 0;

    public static final String CLUBBEDQUERY="";
    public static final String TIMEZONE="";
    public static final String SETUP="";
    public static final String SCROLLID="";


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
        Log.i("Log_tag", "sessiondata is" + clubbedquery);
        editor.putString(CLUBBEDQUERY, clubbedquery);
        editor.commit();
    }

    public String getClubbedquery() {
        String clubbedquery = pref.getString(CLUBBEDQUERY, "");
        return clubbedquery;
    }

    public void setTIMEZONE(String TIMEZONE) {
        Log.i("Log_tag", "sessiondata is" + TIMEZONE);
        editor.putString(TIMEZONE, TIMEZONE);
        editor.commit();
    }


    public String getTIMEZONE() {
        String timezone = pref.getString(TIMEZONE,"");
        return timezone;
    }
    public void setSETUP(String setup){
        editor.putString(SETUP,setup);
        editor.commit();
    }

    public String getSETUP() {
        String setup=pref.getString(SETUP,"");
        return setup;
    }
    public void setSCROLLID(String scrollid){

        editor.putString(SCROLLID,scrollid);
        editor.commit();
    }

    public String getSCROLLID() {
        String scrollid=pref.getString(SCROLLID,"");
        return scrollid;
    }
}

