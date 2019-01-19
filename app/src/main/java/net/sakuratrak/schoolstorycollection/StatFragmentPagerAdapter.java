package net.sakuratrak.schoolstorycollection;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public final class StatFragmentPagerAdapter extends FragmentStatePagerAdapter {

    public static final int PAGE_COUNT = 2;

    public final StatFragmentMainFragment main;
    public final StatFragmentUnitFragment unit;

    public StatFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

        main = new StatFragmentMainFragment();
        unit = new StatFragmentUnitFragment();
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return main;
            case 1:
                return unit;
        }
        throw new IllegalArgumentException("i");
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "统计信息";
            case 1:
                return "单元统计";
        }
        throw new IllegalArgumentException();
    }
}
