package sakuratrak.schoolstorycollection;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public final class MainActivityPagerAdapter extends FragmentStatePagerAdapter {

    private static final int PAGES_COUNT = 4;

    public MainActivityWorkBookFragment mainActivityWorkBookFragment;




    public MainActivityPagerAdapter(FragmentManager fm) {
        super(fm);
        mainActivityWorkBookFragment = new MainActivityWorkBookFragment();
    }

    @Override
    public int getCount() {
        return PAGES_COUNT;
    }

    @Override
    public Fragment getItem(int i) {
        return null;
    }
}
