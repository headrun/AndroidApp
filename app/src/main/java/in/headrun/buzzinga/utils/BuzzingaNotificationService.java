package in.headrun.buzzinga.utils;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

/**
 * Created by sujith on 22/8/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BuzzingaNotificationService extends JobService {


    @Override
    public boolean onStartJob(JobParameters params) {
        Utils.serverCallnotificationCount(this);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
