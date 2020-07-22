package me.nathan.smsabuse_sf.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import me.nathan.smsabuse_sf.R;

/**
 * Class responsible for handling the switching of tabs in the app, returns the appropriate fragment
 * for each page
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    // The tab titles and context for the application
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    /**
     * @param context The applications context
     * @param fm The applications fragment manager
     */
    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    /**
     * @param position The current tab one is on
     * @return The fragment that corresponds to that tab
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Numbers();
            case 1:
                return new Message();
            default:
                return new Wizdary();
        }
    }

    /**
     * @param position The current tab that one is on
     * @return the name of the tab that one is on
     */

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    /**
     * @return The number of tabs in the app, 2 in this case
     */

    @NonNull
    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}