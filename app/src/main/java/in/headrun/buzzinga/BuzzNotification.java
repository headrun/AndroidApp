package in.headrun.buzzinga;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
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
import java.util.Timer;
import java.util.TimerTask;

import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.doto.SearchDetails;
import in.headrun.buzzinga.doto.Utils;

/**
 * Created by headrun on 8/9/15.
 */
public class BuzzNotification extends Service {

    Utils query;
    String TAG = BuzzNotification.this.getClass().getSimpleName();
    Timer time = new Timer();

    Handler handler = new Handler();
    UserSession userSession;

    @Override
    public void onCreate() {
        super.onCreate();

        query = new Utils(this);
        time.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        Log.i(TAG, "call the notification");
                        stringrequest();

                    }
                });

            }
        }, 0, 1000 * 5 * 60);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userSession = new UserSession(this.getApplication());

        return START_STICKY;
    }

    public void stringrequest() {

        Log.i(TAG, "string count quert test");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerConfig.SERVER_ENDPOINT + ServerConfig.count,
                new Response.Listener<String>() {
                    @Override
                    public ArrayList<SearchDetails> onResponse(String response) {

                        Log.d(TAG, "string response is" + response);
                        try {
                            JSONObject jobj_reult = new JSONObject(response);
                            if (jobj_reult.optInt("error") == 0) {
                                JSONObject json_result = new JSONObject(jobj_reult.optString("result"));
                                int article_count = json_result.optInt("count");
                                if (article_count > 0)
                                    new Utils(getApplication()).Buzz_notify(article_count);
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

                return params;
            }

            ;

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String sessionid = new UserSession(BuzzNotification.this).getTSESSION();
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

            ;

        };
        stringRequest.setTag(TAG);
        BuzzingaRequest.getInstance(getApplication()).addToRequestQueue(stringRequest);
    }
}
