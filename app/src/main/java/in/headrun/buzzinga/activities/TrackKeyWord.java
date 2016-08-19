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

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.R;
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


    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trackkeyword);
        ButterKnife.bind(this);
        utils = new Utils(this);

        trackbtn.setOnClickListener(this);
        Trackkeyword.setImeOptions(EditorInfo.IME_ACTION_SEARCH);


        if (utils.userSession.getTrackKey().length() < 0) {
            Trackkeyword.setText(utils.userSession.getTrackKey());
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
                trackkeyword();
                break;
        }
    }

    public void trackkeyword() {

        hideKeyboard();

        if (utils.isNetwrokConnection()) {

            String track = Trackkeyword.getText().toString().trim();

            if (track.length() > 0) {

                trak_progress.setVisibility(View.VISIBLE);

                utils.userSession.setTrackKey(track);
                utils.add_query_data();

                startActivity(new Intent(getApplication(), HomeScreen.class)
                        .putExtra(Constants.Intent_OPERATION, Constants.Intent_TRACK));

                trak_progress.setVisibility(View.GONE);

            } else
                Toast.makeText(this, "Enter your brand", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplication(), "Network error", Toast.LENGTH_LONG).show();
        }
    }

    private void hideKeyboard() {

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.add_query_data();
    }
}
