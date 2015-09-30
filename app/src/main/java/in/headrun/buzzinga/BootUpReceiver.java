package in.headrun.buzzinga;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by headrun on 8/9/15.
 */
public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, BuzzingaNotification.class);
        context.startService(myIntent);
    }
}
