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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.doto.SearchDetails;
import in.headrun.buzzinga.doto.Utils;
import in.headrun.buzzinga.utils.ConnectionSettings;

/**
 * Created by headrun on 28/9/15.
 */
public class BuzzingaNotification extends BroadcastReceiver {

    String TAG = BuzzingaNotification.this.getClass().getSimpleName();

    UserSession userSession;
    Utils query;

    @Override
    public void onReceive(final Context context, Intent intent) {
        userSession = new UserSession(context);
        query = new Utils(context);

        if (ConnectionSettings.isConnected(context)) {
            Log.i(TAG, "count request start");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerConfig.SERVER_ENDPOINT + ServerConfig.count,
                    new Response.Listener<String>() {
                        @Override
                        public ArrayList<SearchDetails> onResponse(String response) {

                            Log.d(TAG, "string response is" + response);
                            try {
                                JSONObject jobj_reult = new JSONObject(response);
                                if (jobj_reult.optInt("error") == 0) {
                                    JSONObject json_result = new JSONObject(jobj_reult.optString("result"));
                                    long article_count = json_result.optInt("count");

                                    try {
                                        new Utils(context).Buzz_notification(article_count);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            return null;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "string error response values" + error);

                }
            }) {

                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("clubbed_query", query.count_query());
                    params.put("tz", userSession.getTIMEZONE());
                    params.put("setup", userSession.getSETUP());
                    Log.i(TAG, " count tz" + userSession.getTIMEZONE() + "setup" + userSession.getSETUP());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    String sessionid = new UserSession(context).getTSESSION();
                    if (sessionid.length() > 0) {
                        Log.i(TAG, "session id is" + sessionid);
                        StringBuilder builder = new StringBuilder();
                        builder.append("sessionid");
                        builder.append("=");
                        builder.append(sessionid);
                        if (headers.containsKey("Cookie")) {
                            builder.append("; ");
                            builder.append(headers.get("Cookie"));
                        }
                        headers.put("Cookie", builder.toString());
                    }

                    return headers;
                }

            };
            stringRequest.setTag(TAG);
            BuzzingaRequest.getInstance(context).addToRequestQueue(stringRequest);
        }
    }
}

