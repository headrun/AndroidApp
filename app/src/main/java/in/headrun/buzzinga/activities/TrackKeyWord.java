package in.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.BuzzingaApplication;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.utils.Utils;


/**
 * Created by headrun on 4/9/15.
 */
public class TrackKeyWord extends Activity implements View.OnClickListener {

    public String TAG = TrackKeyWord.this.getClass().getSimpleName();

    @Bind(R.id.keyword)
    EditText Trackkeyword;
    @Bind(R.id.trackbtn)
    Button trackbtn;
    @Bind(R.id.track_progress)
    ProgressBar trak_progress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trackkeyword);
        ButterKnife.bind(this);

        trackbtn.setOnClickListener(this);
        Trackkeyword.setImeOptions(EditorInfo.IME_ACTION_SEARCH);


        if (BuzzingaApplication.getUserSession().getTrackKey().length() > 0) {
            Trackkeyword.setText(BuzzingaApplication.getUserSession().getTrackKey());
            Trackkeyword.setSelection(BuzzingaApplication.getUserSession().getTrackKey().length());
        }

        Trackkeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    trackkeyword();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.trackbtn:

                Utils.hideKeyboard(this.getCurrentFocus(),this);
                trackkeyword();
                break;
        }
    }

    public void trackkeyword() {


        if (Utils.isNetwrokConnection(this)) {

            String track = Trackkeyword.getText().toString().trim();

            if (track.length() > 0) {

                // trak_progress.setVisibility(View.VISIBLE);

                BuzzingaApplication.getUserSession().setTrackKey(track);
                BuzzingaApplication.getUserSession().clearsession(BuzzingaApplication.getUserSession().TACK_SEARCH_KEY);
                Utils.add_query_data();

                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, track);
                BuzzingaApplication.getmFirebaseAnalytics().logEvent("Track", params);
                BuzzingaApplication.getmFirebaseAnalytics().setAnalyticsCollectionEnabled(true);

                startActivity(new Intent(getApplication(), MainActivity.class)
                        .putExtra(Constants.Intent_OPERATION, Constants.Intent_TRACK));

                this.overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);

                // trak_progress.setVisibility(View.GONE);

            } else
                Toast.makeText(this, "Enter your brand", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplication(), "Network error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.add_query_data();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
