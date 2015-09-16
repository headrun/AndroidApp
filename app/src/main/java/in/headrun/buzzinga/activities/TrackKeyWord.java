package in.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.doto.Utils;

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
        trak_progress.setVisibility(View.GONE);
        trackbtn.setOnClickListener(this);
        Trackkeyword.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

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
                trackkeyword();
                break;
        }
    }


    public void trackkeyword(){
        String[] track_word = {Trackkeyword.getText().toString().toString()};
        if (Config.TRACKKEYWORD)
            Log.i(TAG, "track key word is" + track_word[0]);
        if (track_word[0].length() > 0) {
            trak_progress.setVisibility(View.VISIBLE);
            new UserSession(TrackKeyWord.this).setTrackKey(track_word[0].toString());
            new Utils(this).clear_all_data();
            Constants.BTRACKKEY.add(track_word[0].toString());
            Utils.add_query_data();
            Constants.listdetails.clear();

            Intent intent = new Intent(getApplication(), HomeScreen.class);
            intent.putExtra(Constants.Intent_OPERATION, Constants.Intent_TRACK);
            startActivity(intent);
            trak_progress.setVisibility(View.GONE);

        } else
            Toast.makeText(this, "Enter your brand", Toast.LENGTH_LONG).show();


    }
}
