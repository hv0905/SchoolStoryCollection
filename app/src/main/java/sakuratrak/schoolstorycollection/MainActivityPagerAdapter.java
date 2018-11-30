package sakuratrak.schoolstorycollection;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public final class MainActivityPagerAdapter extends FragmentStatePagerAdapter {

    public static final int PAGES_COUNT = 3;

    public MainActivityWorkBookFragment workBook;

    public MainActivityQuizFragment mainActivityQuizFragment;

    public MainActivityUnitFragment unit;



    public MainActivityPagerAdapter(FragmentManager fm) {
        super(fm);
        workBook = new MainActivityWorkBookFragment();
        mainActivityQuizFragment = new MainActivityQuizFragment();
        unit = new MainActivityUnitFragment();
    }

    @Override
    public int getCount() {
        return PAGES_COUNT;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return workBook;
            case 1:
                return mainActivityQuizFragment;
            case 2:
                return unit;
            //case 3:
                //return mainActivitySettingsFragment;
            default:
                return null;
        }

    }
}
