package sakuratrak.schoolstorycollection;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public final class MainActivityStatFragment extends Fragment {

    public ViewPager _pager;
    public TabLayout _tabLayout;
    public StatFragmentPagerAdapter _adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _pager = (ViewPager) inflater.inflate(R.layout.fragment_main_activity_stat,container,false);

        _tabLayout = _pager.findViewById(R.id.tab_layout);

        //set pages

        _adapter = new StatFragmentPagerAdapter(getChildFragmentManager());
        _pager.setOffscreenPageLimit(StatFragmentPagerAdapter.PAGE_COUNT);
        _pager.setAdapter(_adapter);

        _tabLayout.setupWithViewPager(_pager);

        return _pager;
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    public MainActivityStatFragment(){

    }


}
