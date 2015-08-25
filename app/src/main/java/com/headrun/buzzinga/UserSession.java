package com.headrun.buzzinga;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Created by headrun on 22/7/15.
 */
public class UserSession {


    public SharedPreferences pref;
    public Context _context;
    public Editor editor;
    int PRIVATE_MODE = 0;

    private static final String PREFER_NAME = "BUZZINGA";
    public static final String TSESSION = "null";
    public static final String PICKERFROMDATE = "null";
    public static final String PICKERTODATE = "null";
    public UserSession(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }


    public String getTSESSION() {
        String sess = pref.getString(TSESSION, "null");
        Log.i("Log_Tag", "sess" + sess);
        return sess;
    }

    public void setTSESSION(String sessiondata) {
        Log.i("Log_tag", "sessiondata is" + sessiondata);
        editor.putString(TSESSION, sessiondata);
        editor.commit();
    }


}

