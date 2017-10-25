package in.headrun.buzzinga.core;


import android.content.Context;

import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import in.headrun.buzzinga.UserSession;

public class BuzzingaNetowrkServices extends BaseService {

    private String TAG = BuzzingaNetowrkServices.class.getSimpleName();

    String PLATFORM = "platform";
    String ANDROID = "android";
    String MOBILE = "mobile";
    public static boolean CONTENT_TYPE = true;


    public static Map<String, String> setHeaders(Context mContext) {
        Map<String, String> headers = new HashMap<String, String>();
        String sessionid = new UserSession(mContext).getTSESSION();
        if (sessionid.length() > 0) {
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


    public void getwebLinkData(Context mContext, String url, ResponseListener<String> listener) {
        executeGetRequest(mContext, url, null, null, new TypeToken<String>() {
        }, listener);
    }

}