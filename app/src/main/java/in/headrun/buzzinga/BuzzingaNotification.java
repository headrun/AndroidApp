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

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerConfig.SERVER_ENDPOINT + ServerConfig.count,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            utils.showLog(TAG, "response  " + response, Config.BuzzingaNotification);
                            try {
                                JSONObject jobj_reult = new JSONObject(response);
                                if (jobj_reult.optInt("error") == 0) {
                                    JSONObject json_result = new JSONObject(jobj_reult.optString("result"));
                                    long article_count = json_result.optInt("count");
                                    if (article_count > 0) {
                                        try {
                                            new Utils(context).Buzz_notification(article_count);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    utils.showLog(TAG, "error are " + error.toString(), Config.BuzzingaNotification);

                }
            }) {

                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("clubbed_utils", utils.count_query());
                    params.put("tz", userSession.getTIMEZONE());
                    params.put("setup", userSession.getSETUP());

                    utils.showLog(TAG, "params are " + params, Config.BuzzingaNotification);
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
                    utils.showLog(TAG, "headers are " + headers, Config.BuzzingaNotification);
                    return headers;
                }

            };
            stringRequest.setTag(TAG);
            BuzzingaRequest.getInstance(context).addToRequestQueue(stringRequest);
        }
    }
}

