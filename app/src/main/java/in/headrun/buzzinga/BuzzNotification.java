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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import in.headrun.buzzinga.activities.HomeScreen;
import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.doto.SearchDetails;
import in.headrun.buzzinga.doto.Utils;
import in.headrun.buzzinga.utils.JsonData;

/**
 * Created by headrun on 8/9/15.
 */
public class BuzzNotification extends Service {

    Utils query;
    String TAG = BuzzNotification.this.getClass().getSimpleName();
    Timer time = new Timer();

    Handler handler = new Handler();
    UserSession userSession = new UserSession(this.getApplication());

    @Override
    public void onCreate() {
        super.onCreate();

        query = new Utils(this);
        time.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        getdata();
                        query.Buzz_notification();
                    }
                });

            }
        }, 0, 1000 * 1 * 60);
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


        return START_STICKY;
    }

    public void getdata() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerConfig.SERVER_ENDPOINT + ServerConfig.search,
                new Response.Listener<String>() {
                    @Override
                    public ArrayList<SearchDetails> onResponse(String response) {

                        Log.d(TAG, "string response is" + response + "\n success response code");
                        ArrayList<SearchDetails> list_response = new JsonData().getJsonData(response);
                        HomeScreen.display_articles(list_response);
                        return list_response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "string error response is" + error.getMessage() + "\nmessage is" + error.getMessage());
            }
        }) {


            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tz", userSession.getTIMEZONE());
                params.put("clubbed_query", userSession.getClubbedquery());
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
        //BuzzingaApplication.get().getRequestQueue().add(stringRequest);
        BuzzingaRequest.getInstance(getApplication()).addToRequestQueue(stringRequest);

    }

}
