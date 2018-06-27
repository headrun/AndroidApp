package in.headrun.buzzinga.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import in.headrun.buzzinga.utils.Utils;

/**
 * Created by sujith on 3/1/18.
 */

public class TweetLoadService extends IntentService {

    public TweetLoadService() {
        super(TweetLoadService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Utils.loadTweetDefault();
    }

}
