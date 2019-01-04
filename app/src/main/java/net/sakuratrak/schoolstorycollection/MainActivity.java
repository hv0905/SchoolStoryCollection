package net.sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;
import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningSubject;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MainActivityPagerAdapter _pageContext;
    private LearningSubject _currentSubject = LearningSubject.CHINESE;

    private static final int[] SUBJECT_MENU_IDS = new int[]{
            R.id.nav_menu_chinese,
            R.id.nav_menu_math,
            R.id.nav_menu_english,
            R.id.nav_menu_physics,
            R.id.nav_menu_chemistry,
            R.id.nav_menu_biologic,
            R.id.nav_menu_politics,
            R.id.nav_menu_history,
            R.id.nav_menu_geo
    };

    //region ui_control
    private DrawerLayout _drawer;
    private BottomNavigationView _navigation;
    private Toolbar _toolbar;
    private ViewPager _pager;
    private MenuItem _filterMenu;
    private MenuItem _displayModeToggle;
    private NavigationView _navigationView;
    //endregion

    //region fields
    private boolean _isSecondDisplayMode = false;
    private File _cameraCurrentFile;
    private final String TAG = "MainActivity";
    private ActionBarDrawerToggle _drawerToggle;
    //endregion

    private ArrayList<RequireRefreshEventHandler> _requireRefreshEvent;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //region loadUI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //endregion

        //region get UI elements
        _pager = findViewById(R.id.pager);
        _navigation = findViewById(R.id.bottomNav);
        _toolbar = findViewById(R.id.toolbar);
        _drawer = findViewById(R.id.drawer);
        _navigationView = findViewById(R.id.navigationView);
        //endregion

        //设置工具栏
        setSupportActionBar(_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        int startId = AppSettingsMaster.getStartupSubjectId(this);
        refreshSubject(LearningSubject.id2Obj(startId));
        _navigationView.setCheckedItem(SUBJECT_MENU_IDS[startId]);

        //设置Drawer
        _drawerToggle = new ActionBarDrawerToggle(this, _drawer, _toolbar, R.string.subject, R.string.subject) {

        };

        _drawer.addDrawerListener(_drawerToggle);

        _navigationView.setNavigationItemSelectedListener(menuItem -> {
            _drawer.closeDrawer(Gravity.START);
            if (menuItem.getItemId() == R.id.nav_menu_settings) {
                //settings...
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            } else {
                menuItem.setChecked(true);
                for (int subject = 0; subject < 9; subject++) {
                    if (menuItem.getItemId() == SUBJECT_MENU_IDS[subject]) {
                        refreshSubject(LearningSubject.id2Obj(subject));
                        break;
                    }
                }
            }
            return false;
        });


        //open database
        DbManager.getDefaultHelper(this);

        //region Events

        _pager.setOffscreenPageLimit(MainActivityPagerAdapter.PAGES_COUNT);//全部加载
        _pageContext = new MainActivityPagerAdapter(getSupportFragmentManager());
        _pager.setAdapter(_pageContext);

        _pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        _navigation.setSelectedItemId(R.id.navigation_home);
                        break;
                    case 1:
                        _navigation.setSelectedItemId(R.id.navigation_dashboard);
                        break;
                    case 2:
                        _navigation.setSelectedItemId(R.id.navigation_unit);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        _navigation.setOnNavigationItemSelectedListener(item -> {
            if (_filterMenu != null)
                _filterMenu.setVisible(false);
            if(_displayModeToggle != null)
                _displayModeToggle.setVisible(false);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    _pager.setCurrentItem(0);
                    _filterMenu.setVisible(true);
                    _displayModeToggle.setVisible(true);
                    return true;
                case R.id.navigation_dashboard:
                    _pager.setCurrentItem(1);
                    return true;
                case R.id.navigation_unit:
                    _pager.setCurrentItem(2);
                    return true;
            }
            return false;
        });

        //endregion


        //region init recycler
        //endregion

    }

    //从顶部组合框中获取目前选中的科目
    @NonNull
    public LearningSubject getCurrentSubject() {
        return _currentSubject;
    }


    private void notifyUnitSaveError(View v) {
        Snackbar.make(v, R.string.failSaveUnitError, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_options, menu);
        _filterMenu = menu.findItem(R.id.filter);
        _displayModeToggle = menu.findItem(R.id.displayModeToggle);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (_drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.filter:
                FilterDialog fd = new FilterDialog();
                fd.show(getSupportFragmentManager(), "filter");
                return true;
            case R.id.displayModeToggle:
                _isSecondDisplayMode = !_isSecondDisplayMode;
                _displayModeToggle.setIcon(_isSecondDisplayMode ? R.drawable.ic_view_list_white_24dp : R.drawable.ic_dashboard_white_24dp);
                _pageContext.workBook.setDisplayMode(_isSecondDisplayMode);
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        System.out.println("MainActivity Destroyed!!!");
        AppSettingsMaster.setStartupSubjectId(this, _currentSubject.getId());
        DbManager.releaseCurrentHelper();
        super.onDestroy();
    }

    public void refreshSubject(LearningSubject subject) {
        _currentSubject = subject;
        //set toolbar color
        int uiColor = UiHelper.getFlatUiColor(this, _currentSubject.getId());
        getWindow().setStatusBarColor(uiColor);
        _toolbar.setBackgroundColor(uiColor);
        getSupportActionBar().setTitle(getResources().getStringArray(R.array.learning_subjects)[subject.getId()]);
        requireRefresh();
    }

    public void addSubjectUpdateEvent(RequireRefreshEventHandler handler) {
        if (_requireRefreshEvent == null) _requireRefreshEvent = new ArrayList<>();
        _requireRefreshEvent.add(handler);
    }

    public void removeSubjectUpdateEvent(RequireRefreshEventHandler handler) {
        if (_requireRefreshEvent == null) _requireRefreshEvent = new ArrayList<>();
        _requireRefreshEvent.remove(handler);
    }

    public void requireRefresh() {
        if (_requireRefreshEvent == null) return;
        for (RequireRefreshEventHandler item :
                _requireRefreshEvent) {
            item.update();
        }
    }


    @FunctionalInterface
    public interface RequireRefreshEventHandler {
        void update();
    }

}
