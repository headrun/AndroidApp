package in.headrun.buzzinga.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import in.headrun.buzzinga.R;
import in.headrun.buzzinga.config.Config;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.dashboard.ListViewMultiChartActivity;
import in.headrun.buzzinga.utils.Utils;


public class Pager extends android.support.v4.app.Fragment {


    public String TAG = Pager.class.getSimpleName();

    public ViewPager pager;

    public String Intent_opt = "";

    public Pager() {
        // Required empty public constructor
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            Intent_opt = bundle.getString(Constants.Intent_OPERATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pager, container, false);

        pager = (ViewPager) v.findViewById(R.id.pager);

        readBundle(getArguments());
        pager.setAdapter(new PageAdapter(getFragmentManager()));
        pager.setCurrentItem(0);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                new Utils(getActivity()).showLog(TAG,
                        "count is" + pager.getAdapter().getCount() + " pos is " + position,
                        Config.Pager);

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        pager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {

                final float normalizedposition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedposition / 2 + 0.5f);
                page.setScaleY(normalizedposition / 2 + 0.5f);
            }
        });


        return v;

    }

    public class PageAdapter extends FragmentStatePagerAdapter {


        public PageAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    Bundle bundle = new Bundle();
                    if (Intent_opt == null)
                        Intent_opt = "";
                    bundle.putString(Constants.Intent_OPERATION, Intent_opt);
                    fragment = new HomeScreen();
                    fragment.setArguments(bundle);
                    break;
                case 1:
                    fragment = new ListViewMultiChartActivity();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
