package in.headrun.buzzinga.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by headrun on 20/7/15.
 */
public class ConnectionSettings {

    public static boolean isConnected(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        Log.i("Log_Tag","conneced network is"+info[i]+"sate"+info[i].getState());
                        return true;
                    }

        }
        return false;
    }
}
