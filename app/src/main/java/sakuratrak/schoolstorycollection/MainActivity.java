package sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;

import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.LearningSubject;

public class MainActivity extends AppCompatActivity {

    private MainActivityPagerAdapter _pageContext;
    private LearningSubject _currentSubject = LearningSubject.CHINESE;

    private static final int[] SUBJECT_MENU_IDS = new int[] {
            R.id.nav_menu_chinese,
            R.id.nav_menu_math,
            R.id.nav_menu_english,
            R.id.nav_menu_phystic,
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
    private NavigationView _navigationView;
    //endregion

    //region fields
    private File _cameraCurrentFile;
    private final String TAG = "MainActivity";
    private ActionBarDrawerToggle _drawerToggle;
    //endregion

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
        getSupportActionBar().setTitle(R.string.chinese);

        //设置Drawer
        _drawerToggle = new ActionBarDrawerToggle(this,_drawer,_toolbar,R.string.subject,R.string.subject){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        _drawer.addDrawerListener(_drawerToggle);

        _navigationView.setNavigationItemSelectedListener(menuItem -> {
            _drawer.closeDrawer(Gravity.START);
            if(menuItem.getItemId() == R.id.nav_menu_settings){
                //settings...
                Intent intent = new Intent(this,SettingActivity.class);
                startActivity(intent);
                return true;
            }else{
                getSupportActionBar().setTitle(menuItem.getTitle());
                menuItem.setChecked(true);
//                switch (menuItem.getItemId()){
//                    case R.id.nav_menu_chinese:
//                        refreshSubject(LearningSubject.CHINESE);
//                        break;
//                    case R.id.nav_menu_math:
//                        refreshSubject(LearningSubject.MATH);
//                        break;
//                    case R.id.nav_menu_english:
//                        refreshSubject(LearningSubject.ENGLISH);
//                        break;
//                    case R.id.nav_menu_physics:
//                        refreshSubject(LearningSubject.PHYSICS);
//                        break;
//                    case R.id.nav_menu_chemistry:
//                        refreshSubject(LearningSubject.HISTORY);
//                        break;
//                    case R.id.nav_menu_biologic:
//                        refreshSubject(LearningSubject.GEO);
//                        break;
//                    case R.id.nav_menu_politics:
//                        refreshSubject(LearningSubject.POLITICS);
//                        break;
//                    case R.id.nav_menu_history:
//                        refreshSubject(LearningSubject.CHEMISTRY);
//                        break;
//                    case R.id.nav_menu_geo:
//                        refreshSubject(LearningSubject.GEO);
//                        break;
//                }

                for(int subject = 0; subject < 9 ; subject++){
                    if(menuItem.getItemId() == SUBJECT_MENU_IDS[i]){
                        refreshSubject(LearningSubject.id2Obj(i));
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
        _pageContext.unit._backupParent = this;
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
                _filterMenu.setVisible(false);
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                            _pager.setCurrentItem(0);
                        _filterMenu.setVisible(true);
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

    //Intent接收事件
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode + " " + resultCode);

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
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        System.out.println("MainActivity Destroyed!!!");
        DbManager.releaseCurrentHelper();
        super.onDestroy();
    }

    public void refreshSubject(LearningSubject subject){
        if(_currentSubject == subject) return;
        _currentSubject = subject;
        _pageContext.unit.refreshUnit();
    }

}
