package net.sakuratrak.schoolstorycollection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public final class MainActivityStatFragment extends Fragment {

    public ViewPager _pager;
    public TabLayout _tabLayout;
    public StatFragmentPagerAdapter _adapter;

    public MainActivityStatFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _pager = (ViewPager) inflater.inflate(R.layout.fragment_main_activity_stat, container, false);

        _tabLayout = _pager.findViewById(R.id.tab_layout);

        //set pages

        _adapter = new StatFragmentPagerAdapter(getChildFragmentManager());
        _pager.setOffscreenPageLimit(StatFragmentPagerAdapter.PAGE_COUNT);
        _pager.setAdapter(_adapter);

        _tabLayout.setupWithViewPager(_pager);

        return _pager;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isAdded()) return;
        _adapter.main.userVisibleChangeEventHandler(isVisibleToUser);
    }
}
