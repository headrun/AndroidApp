package in.headrun.buzzinga.core;

import android.content.Context;

import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import in.headrun.buzzinga.BuzzingaApplication;


public class BaseService {

    //Opens connection port via GET Method
    protected void executeGetRequest(Context context, String url, Map<String, String> headers, Map<String, String> params, TypeToken typeToken, ResponseListener listener) {
        //url = NetworkUtils.getUrl(url, params);
        executeRequest(context, Request.Method.GET, url, headers, params, typeToken, listener);
    }

    //Opens connection port via POST Method
    protected void executePostRequest(Context context, String url, Map<String, String> headers, Map<String, String> params, TypeToken typeToken, ResponseListener listener) {
        executeRequest(context, Request.Method.POST, url, headers, params, typeToken, listener);
    }


    protected void executePatchRequest(Context context, String url, Map<String, String> headers, Map<String, String> params, TypeToken typeToken, ResponseListener listener) {
        executeRequest(context, Request.Method.PATCH, url, headers, params, typeToken, listener);
    }

    protected void executePutRequest(Context context, String url, Map<String, String> headers, Map<String, String> params, TypeToken typeToken, ResponseListener listener) {
        executeRequest(context, Request.Method.PUT, url, headers, params, typeToken, listener);
    }


    protected void executeDeleteRequest(Context context, String url, Map<String, String> headers, Map<String, String> params, TypeToken typeToken, ResponseListener listener) {
        executeRequest(context, Request.Method.DELETE, url, headers, params, typeToken, listener);
    }

    //Initiate volley request queue
    protected void executeRequest(Context context, int method, String url, Map<String, String> headers, Map<String, String> params, TypeToken typeToken, ResponseListener listener) {
        BaseRequest request = new BaseRequest(context, method, url, headers, params, typeToken, listener);
        BuzzingaApplication.get().addToRequestQueue(request);
    }

}
