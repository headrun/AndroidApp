package in.headrun.buzzinga.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import in.headrun.buzzinga.R;
import in.headrun.buzzinga.activities.HomeScreen;
import in.headrun.buzzinga.activities.Pager;
import in.headrun.buzzinga.config.Constants;
import in.headrun.buzzinga.dashboard.ListViewMultiChartActivity;

public class PageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String Intent_type = "";

    public PageAdapter(FragmentManager fm, int NumOfTabs, String Intent_type) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.Intent_type = Intent_type;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        switch (position) {
            case 0:
                Bundle bundle = new Bundle();
                if (Intent_type == null)
                    Intent_type = "";
                bundle.putString(Constants.Intent_OPERATION, Intent_type);
                fragment = new HomeScreen();
                fragment.setArguments(bundle);
                break;

            case 1:
                fragment = new ListViewMultiChartActivity();
                break;

            case 2:
                fragment = new ListViewMultiChartActivity();
                break;

            default:
                return null;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}