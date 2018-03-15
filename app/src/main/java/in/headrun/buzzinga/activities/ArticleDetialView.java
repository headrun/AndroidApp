package in.headrun.buzzinga.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.config.ServerConfig;
import in.headrun.buzzinga.doto.SearchArticles;

/**
 * Created by hedarun on 14/3/18.
 */

public class ArticleDetialView extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.article_img)
    ImageView article_img;
    @BindView(R.id.article_source_img)
    ImageView article_source_img;
    @BindView(R.id.fab_share)
    FloatingActionButton fab_share;
    @BindView(R.id.article_source_txt)
    TextView article_source_txt;
    @BindView(R.id.article_date)
    TextView article_date;

    @BindView(R.id.article_text)
    TextView article_text;
    @BindView(R.id.article_desc)
    TextView article_desc;

    int pos;
    SearchArticles article_details;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detailview);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();
        pos = data.getInt("pos");

        article_desc.setTypeface(Typeface.createFromAsset(getAssets(),
                "fonts/MuseoSans-500.otf"));
        article_text.setTypeface(Typeface.createFromAsset(getAssets(),
                "fonts/MuseoSans-700.otf"));
        article_source_txt.setTypeface(Typeface.createFromAsset(getAssets(),
                "fonts/MuseoSans-300.otf"));
        article_date.setTypeface(Typeface.createFromAsset(getAssets(),
                "fonts/MuseoSans-300.otf"));

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.close_grey);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(7);

        if (Constants.SEARCHARTICLES.size() >= pos)
            article_details = Constants.SEARCHARTICLES.get(pos);
        if(article_details!=null && article_details.source!=null){
            //article img
            if (article_details.source.IMAGE_LINK != null && !article_details.source.IMAGE_LINK.isEmpty()){
            Glide.with(this).
                    load(article_details.source.IMAGE_LINK).into(article_img);
            }else {
                article_img.setVisibility(View.GONE);
            }

            //article source img
            String text_url = article_details.source.URL;
            URL aURL = null;
            try {
                aURL = new URL(text_url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String host_name = aURL.getHost();

            if (host_name != null && !host_name.isEmpty()){
                //article domain
                article_source_txt.setText(host_name);
                Glide.with(this).
                        load(ServerConfig.FETCH_ICON + host_name).
                        into(article_source_img);

            }else {
                article_source_txt.setVisibility(View.GONE);
                article_source_img.setVisibility(View.GONE);
            }

            //article date
            String epoc_time=article_details.source.DATE_ADDED;
            if(!epoc_time.isEmpty()){
                long millis = Long.valueOf(epoc_time) * 1000;
                Date date = new Date(millis);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                 article_date.setText(sdf.format(date));
            }
            //article title
            article_text.setText(article_details.source.TITLE);
            //article desc
            article_desc.setText(article_details.source.TEXT);

            fab_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(article_details!=null && article_details.source.URL!=null){
                        ShareCompat.IntentBuilder
                                .from(ArticleDetialView.this)
                                .setText(article_details.source.URL)
                                .setChooserTitle(article_details.source.TITLE)
                                .setType("text/plain") // most general text sharing MIME type
                                .setChooserTitle("Buzzinga Analytics")
                                .startChooser();
                    }
                }
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.artical_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.invite) {
            if(article_details!=null && article_details.source.URL!=null){

                ShareCompat.IntentBuilder
                        .from(this) // getActivity() or activity field if within Fragment
                        .setText(article_details.source.URL)
                        .setChooserTitle(article_details.source.TITLE)
                        .setType("text/plain") // most general text sharing MIME type
                        .setChooserTitle("Buzzinga Analytics")
                        .startChooser();
            }

        }

        return super.onOptionsItemSelected(item);
    }

}
