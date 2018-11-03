package sakuratrak.schoolstorycollection;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public final class MainActivityPagerAdapter extends FragmentStatePagerAdapter {

    public static final int PAGES_COUNT = 4;

    public MainActivityWorkBookFragment mainActivityWorkBookFragment;

    public MainActivityQuizFragment mainActivityQuizFragment;

    public MainActivityUnitFragment unit;

    public MainActivitySettingsFragment mainActivitySettingsFragment;


    public MainActivityPagerAdapter(FragmentManager fm) {
        super(fm);
        mainActivityWorkBookFragment = new MainActivityWorkBookFragment();
        mainActivityQuizFragment = new MainActivityQuizFragment();
        unit = new MainActivityUnitFragment();
        mainActivitySettingsFragment = new MainActivitySettingsFragment();
    }

    @Override
    public int getCount() {
        return PAGES_COUNT;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return mainActivityWorkBookFragment;
            case 1:
                return mainActivityQuizFragment;
            case 2:
                return unit;
            case 3:
                return mainActivitySettingsFragment;
            default:
                return null;
        }

    }
}
