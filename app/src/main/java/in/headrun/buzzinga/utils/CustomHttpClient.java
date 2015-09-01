package in.headrun.buzzinga.utils;

/**
 * Created by Thedla on 20-01-2015.
 */

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

public class CustomHttpClient {
    //AlertDialogManager alert = new  AlertDialogManager();
    /** The time it takes for our client to timeout */
    public static final int HTTP_TIMEOUT = 30 * 1000; // milliseconds
    /** Single instance of our HttpClient */
    private static HttpClient mHttpClient;
    /**
     * Get our single instance of our HttpClient object.
     *
     * @return an HttpClient object with connection parameters set
     */

    private static HttpClient getHttpClient() {

        if (mHttpClient == null) {
            Log.i("log_tag", "mHttpClient ");

            mHttpClient = new DefaultHttpClient();

            final HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
            ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
        }

        return mHttpClient;
    }



    public static String executeHttpPost(String url,ArrayList<NameValuePair> postParameters) throws Exception {

        BufferedReader in = null;

        try {

            HttpClient client = getHttpClient();

            HttpPost request = new HttpPost(url);

            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);

            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);
            Log.i("log_tag", "httpresponse " + response);



            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");

            String line = "";

            String NL = System.getProperty("line.separator");

            while ((line = in.readLine()) != null) {

                sb.append(line + NL);

            }

            in.close();

            String result = sb.toString();

            return result;

        } finally {

            if (in != null) {

                try {

                    in.close();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

        }

    }

    /**

     * Performs an HTTP GET request to the specified url.

     *

     * @param url

     *            The web address to post the request to

     * @return The result of the request

     * @throws Exception

     */

    public static String executeHttpGet(String url) throws Exception {

        BufferedReader in = null;

        try {

            HttpClient client = getHttpClient();

            HttpGet request = new HttpGet();

            request.setURI(new URI(url));

            HttpResponse response = client.execute(request);

            in = new BufferedReader(new InputStreamReader(response.getEntity()

                    .getContent()));


            StringBuffer sb = new StringBuffer("");

            String line = "";

            String NL = System.getProperty("line.separator");

            while ((line = in.readLine()) != null) {

                sb.append(line + NL);

            }

            in.close();


            String result = sb.toString();

            return result;

        } finally {

            if (in != null) {

                try {

                    in.close();

                } catch (IOException e) {

                    Log.e("log_tag", "Error converting result " + e.toString());

                    e.printStackTrace();

                }

            }

        }

    }
//////////////////////////////////////////

    public static String executehttpget(String url,ArrayList<NameValuePair> postParameters) throws Exception
    {
        BufferedReader in = null;


        try {

            HttpClient httpClient = new DefaultHttpClient();
            String paramsString = URLEncodedUtils.format(postParameters, "UTF-8");
            Log.i("Log_tag", "paramsString" + paramsString);
            String path=url + "?" + paramsString;
            Log.i("Log_tag", "path is" + path);
            HttpGet httpGet = new HttpGet(path);
            Log.i("Log_tag", "httpGet :" + httpGet);
            HttpResponse response = httpClient.execute(httpGet);
            Log.i("Log_tag", "httpGet response" + response);
            in = new BufferedReader(new InputStreamReader(response.getEntity()

                    .getContent()));


            StringBuffer sb = new StringBuffer("");

            String line = "";

            String NL = System.getProperty("line.separator");

            while ((line = in.readLine()) != null) {

                sb.append(line + NL);

            }
            in.close();


            String result = sb.toString();

            return result;


        } finally {

            if (in != null) {

                try {

                    in.close();

                } catch (IOException e) {

                    Log.e("log_tag", "Error converting result " + e.toString());

                    e.printStackTrace();

                }

            }


        }


        }
      public static String executeHttpGet17(String url,ArrayList<NameValuePair> postParameters) throws Exception {

    BufferedReader in = null;

    try {

        HttpClient client = getHttpClient();

        HttpGet request = new HttpGet();

        request.setURI(new URI(url));

        HttpResponse response = client.execute(request);

        in = new BufferedReader(new InputStreamReader(response.getEntity()

                .getContent()));


        StringBuffer sb = new StringBuffer("");

        String line = "";

        String NL = System.getProperty("line.separator");

        while ((line = in.readLine()) != null) {

            sb.append(line + NL);

        }

        in.close();


        String result = sb.toString();

        return result;

    } finally {

        if (in != null) {

            try {

                in.close();

            } catch (IOException e) {

                Log.e("log_tag", "Error converting result " + e.toString());

                e.printStackTrace();

            }

        }

    }

}

}
