package me.kaelaela.opengraphview.network.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import me.kaelaela.opengraphview.OGParser;
import me.kaelaela.opengraphview.Parser;
import me.kaelaela.opengraphview.network.model.OGData;

public class LoadOGDataTask extends AsyncTask<String, Void, OGData> {

    public abstract static class OnLoadListener {
        public void onLoadStart() {
        }

        public void onLoadSuccess(OGData ogData) {
        }

        public void onLoadError() {
        }
    }

    private final Parser mParser;

    private OnLoadListener mListener;
    private OGData mData = new OGData();

    public LoadOGDataTask(OnLoadListener listener, Parser parser) {
        mListener = listener;
        mParser = (parser == null) ? new OGParser() : parser;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        java.net.URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }

    @Override
    protected OGData doInBackground(String... urls) {
        mListener.onLoadStart();
        InputStream inputStream = null;
        try {
            inputStream = downloadUrl(urls[0]);
            mData = mParser.parse(inputStream);
        } catch (IOException e) {
            mListener.onLoadError();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    mListener.onLoadError();
                    e.printStackTrace();
                }
            }
        }
        return mData;
    }

    @Override
    protected void onPostExecute(OGData og) {
        super.onPostExecute(og);
        try {
            if (og == null) {
                mListener.onLoadError();
                return;
            }
            mListener.onLoadSuccess(og);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
