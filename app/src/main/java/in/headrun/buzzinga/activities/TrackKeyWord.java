package in.headrun.buzzinga.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.headrun.buzzinga.BuzzingaApplication;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.UserSession;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.doto.QueryData;
import in.headrun.buzzinga.utils.ConnectionSettings;

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
    UserSession usersession;
    BuzzingaApplication buzzapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trackkeyword);
        ButterKnife.bind(this);
        trak_progress.setVisibility(View.GONE);
        trackbtn.setOnClickListener(this);
        Trackkeyword.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        usersession = new UserSession(getApplication());
        buzzapp = new BuzzingaApplication();

        if (usersession.getTrackKey().length()<0) {
            Trackkeyword.setText(usersession.getTrackKey());
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

        if(ConnectionSettings.isConnected(TrackKeyWord.this)) {

            String track = Trackkeyword.getText().toString().trim().toString();
            ArrayList<String> track_word = new ArrayList<>();

            if (Config.TRACKKEYWORD)
                Log.i(TAG, "track key word is" + track);
            if (track.length() > 0) {
                track_word.add(track);
                trak_progress.setVisibility(View.VISIBLE);
                usersession.setTrackKey(track_word.get(0));
                //usersession.setBTRACKKEY("BTACK_KEY", track_word.get(0));

                clear_all_data();

                Log.i(TAG, "track key is" + usersession.getTrackKey());
                new BuzzingaApplication().BTRACKKEY.add(usersession.getTrackKey());
                add_query_data();
                Constants.listdetails.clear();

                Intent intent = new Intent(getApplication(), HomeScreen.class);
                intent.putExtra(Constants.Intent_OPERATION, Constants.Intent_TRACK);
                startActivity(intent);
                trak_progress.setVisibility(View.GONE);

            } else
                Toast.makeText(this, "Enter your brand", Toast.LENGTH_LONG).show();
        }else{
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

    public void clear_all_data() {
        Constants.SEARCHSTRING = "";
        buzzapp.BTRACKKEY.clear();
        buzzapp.BSEARCHKEY.clear();
        buzzapp.BTODATE.clear();
        buzzapp.BFROMDATE.clear();
        buzzapp.BLOCATION.clear();
        buzzapp.BLANGUAGE.clear();
        buzzapp.BSOURCES.clear();
        buzzapp.BGENDER.clear();
        buzzapp.BSENTIMENT.clear();
    }

    public void add_query_data() {
        buzzapp.QueryString.clear();
        buzzapp.QueryString.add(new QueryData(Constants.TRACKKEY, buzzapp.BTRACKKEY));
        buzzapp.QueryString.add(new QueryData(Constants.FROMDATE, buzzapp.BFROMDATE));
        buzzapp.QueryString.add(new QueryData(Constants.TODATE, buzzapp.BTODATE));
        buzzapp.QueryString.add(new QueryData(Constants.LOCATION, buzzapp.BLOCATION));
        buzzapp.QueryString.add(new QueryData(Constants.LANGUAGE, buzzapp.BLANGUAGE));
        buzzapp.QueryString.add(new QueryData(Constants.SEARCHKEY, buzzapp.BSEARCHKEY));
        buzzapp.QueryString.add(new QueryData(Constants.SOURCES, buzzapp.BSOURCES));
        buzzapp.QueryString.add(new QueryData(Constants.GENDER, buzzapp.BGENDER));
        buzzapp.QueryString.add(new QueryData(Constants.SENTIMENT, buzzapp.BSENTIMENT));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        add_query_data();
    }
}
