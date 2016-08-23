package in.headrun.buzzinga;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.utils.Utils;


/**
 * Created by headrun on 28/9/15.
 */
public class BuzzingaNotification extends BroadcastReceiver {

    String TAG = BuzzingaNotification.this.getClass().getSimpleName();

    UserSession userSession;
    Utils utils;

    @Override
    public void onReceive(final Context context, Intent intent) {
        userSession = new UserSession(context);
        utils = new Utils(context);
        utils.add_query_data();
        if (utils.isNetwrokConnection()) {
            utils.serverCallnotificationCount();

        }
    }
}

