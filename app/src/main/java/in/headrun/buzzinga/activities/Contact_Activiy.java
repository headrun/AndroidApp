package in.headrun.buzzinga.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import butterknife.BindView;
import butterknife.ButterKnife;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.utils.Utils;

public class Contact_Activiy extends AppCompatActivity {

    @BindView(R.id.mobile_lay)
    RelativeLayout mobile_lay;
    @BindView(R.id.mobile_value)
    TextView mobile_value;
    @BindView(R.id.email_value)
    TextView email_value;
    @BindView(R.id.email_lay)
    RelativeLayout email_lay;
    @BindView(R.id.collapse_toolbar_layout)
    CollapsingToolbarLayout collapse_toolbar_layout;


    private static final int CALL_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle("Contact Us");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        collapse_toolbar_layout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapse_toolbar_layout.setExpandedTitleTextAppearance(R.style.expandedappbar);

        askPermission();
        mobile_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.callToMobile(mobile_value.getText().toString().trim(), Contact_Activiy.this);
            }
        });

        email_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.composeEmail(new String[]{email_value.getText().toString().trim()}, Contact_Activiy.this);
            }
        });

    }

    public void askPermission() {

        boolean hasPermission = (ContextCompat.checkSelfPermission(Contact_Activiy.this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(Contact_Activiy.this,
                    new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALL_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                    Toast.makeText(Contact_Activiy.this, getString(R.string.call_permission_error), Toast.LENGTH_LONG).show();
                }
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class).
                putExtra(Constants.Intent_OPERATION, Constants.Intent_NOTHING).
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK));
        overridePendingTransition(R.anim.move_right_out_activity, R.anim.move_left_in_activity);
    }
}
