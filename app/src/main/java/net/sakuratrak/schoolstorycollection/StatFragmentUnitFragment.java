package net.sakuratrak.schoolstorycollection;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import net.sakuratrak.schoolstorycollection.core.ListDataProvider;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public final class StatFragmentUnitFragment extends Fragment {

    public static final String TAG = "Stat_UnitFragment";
    private static final int REQUEST_DETAIL = 100;

    //region views
    public ConstraintLayout _root;
    private View _unitEmptyNotice;
    private RecyclerView _unitList;
    private FloatingActionButton _addUnitBtn;
    //endregion

    private Runnable _notifyUnitRefresh;
    private UnitDisplayAdapter _mainAdapter;
    private List<LearningUnitInfo> _context;
    private List<UnitDisplayAdapter.FullUnitDisplayAdapter.DataContext> _displayContext;

    private final MainActivity.RequireRefreshEventHandler _requireRefresh = this::refresh;
    private final MainActivity.ChangeDisplayModeEventHandler _changeMode = this::changeDisplayMode;
    private RecycleViewDivider _mainDivider;


    public StatFragmentUnitFragment() {
    }

    public Runnable getNotifyUnitRefresh() {
        return _notifyUnitRefresh;
    }

    public void setNotifyUnitRefresh(Runnable _notifyUnitRefresh) {
        this._notifyUnitRefresh = _notifyUnitRefresh;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: stat fragment");
        _root = (ConstraintLayout) inflater.inflate(R.layout.fragment_stat_fragment_unit, container, false);
        _unitList = _root.findViewById(R.id.unitList);
        _unitEmptyNotice = _root.findViewById(R.id.unitEmptyNotice);
        _addUnitBtn = _root.findViewById(R.id.addUnitBtn);

        _addUnitBtn.setOnClickListener(v -> {
            final EditText et = new EditText(getParent());
            et.setSingleLine();
            AlertDialog.Builder ab = new AlertDialog.Builder(getParent()).setIcon(R.drawable.ic_book_black_24dp).setTitle("创建单元")
                    .setMessage("单元名称:")
                    .setView(et).setPositiveButton("完成", (dialog, which) -> {
                        if (et.getText().toString().trim().isEmpty()) {
                            new AlertDialog.Builder(getParent()).setMessage("请输入单元名称").setTitle("错误").setNegativeButton("确定", null).setIcon(R.drawable.ic_warning_black_24dp).show();
                            return;
                        }
                        try {
                            DbManager.getDefaultHelper(getParent()).getLearningUnitInfos().create(new LearningUnitInfo(et.getText().toString().trim(), getParent().getCurrentSubject()));
                        } catch (SQLException e) {
                            Snackbar.make(_root, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
                            return;
                        }

                        destroyTel();
                        getParent().requireRefresh();
                        regTel();

                        refreshContext();
                        _mainAdapter.notifyItemInserted(0);
                        _unitList.scrollToPosition(0);
                    })
                    .setNegativeButton("取消", null);

            ab.show();
        });

        _unitList.setLayoutManager(new LinearLayoutManager(getParent(), RecyclerView.VERTICAL, false));
        _unitList.setItemAnimator(new LandingAnimator());

        _displayContext = new ArrayList<>();
        changeDisplayMode(getParent().is_isSecondDisplayMode());

        regTel();
        refresh();

        return _root;
    }

    public void regTel(){
        getParent().addRequireRefreshEvent(_requireRefresh);
        getParent().addChangeDisplayModeEvent(_changeMode);
    }

    public void destroyTel(){
        getParent().removeRequireRefreshEvent(_requireRefresh);
        getParent().removeChangeDisplayModeEvent(_changeMode);
    }

    @Override
    public void onDestroyView() {
        destroyTel();
        super.onDestroyView();

    }

    private void refreshContext(){
        try {
            _context= (DbManager.getDefaultHelper(getParent())).getLearningUnitInfos().queryForEq("subjectId", getParent().getCurrentSubject().getId());
        } catch (SQLException e) {
            e.printStackTrace();
            Snackbar.make(_root, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (_context.size() == 0) {
            _unitEmptyNotice.setVisibility(View.VISIBLE);
        } else {
            _unitEmptyNotice.setVisibility(View.INVISIBLE);
        }

        int questionSum;
        try {
            questionSum = new QuestionInfo.QuestionInfoDaoManager(DbManager.getDefaultHelper(getContext())).FindAllWithSubject(getParent().getCurrentSubject()).size();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        _displayContext.clear();

        for (int i = _context.size() - 1; i >= 0; i--) {
            LearningUnitInfo item = _context.get(i);
            UnitDisplayAdapter.FullUnitDisplayAdapter.DataContext udiItem = UnitDisplayAdapter.DataContext.fromDb(item, questionSum);
            udiItem.DetailClicked = v -> detailClicked(v, udiItem, item);
            _displayContext.add(udiItem);
        }


        if (_notifyUnitRefresh != null) {
            _notifyUnitRefresh.run();
        }
    }

    private void refresh() {
        refreshContext();
        _mainAdapter.notifyDataSetChanged();
    }

    private void detailClicked(View v, UnitDisplayAdapter.DataContext udiItem, LearningUnitInfo item) {
        Intent intent = new Intent(getActivity(), UnitDetailActivity.class);
        intent.putExtra(UnitDetailActivity.EXTRA_CONTEXT_ID, item.getId());
        ActivityOptions ao;
        if(getParent().is_isSecondDisplayMode()){
            ao = ActivityOptions.makeSceneTransitionAnimation(getActivity());
        }else {
            ao = ActivityOptions.makeSceneTransitionAnimation(
                    getActivity(),
                    udiItem.unitMainInfo,
                    "unitMainInfo");
        }
        startActivityForResult(intent, REQUEST_DETAIL, ao.toBundle());

//        UnitDetailDialog udi = new UnitDetailDialog(item);
//        udi.show(getChildFragmentManager(),"UnitDetailDialog");
    }

    private MainActivity getParent() {
        return (MainActivity) getActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DETAIL:
                switch (resultCode) {
                    case UnitDetailActivity.RESULT_DELETED:
                        Snackbar.make(_root, R.string.deleted, Snackbar.LENGTH_LONG).show();
                    case UnitDetailActivity.RESULT_CHANGED:
                        getParent().requireRefresh();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changeDisplayMode(boolean isSecondMode) {
        if (isSecondMode) {
            _mainAdapter = new UnitDisplayAdapter.SimpleUnitDisplayAdapter(new ListDataProvider<>(_displayContext));
            if (_mainDivider == null)
                _mainDivider = new RecycleViewDivider(RecyclerView.VERTICAL, getContext());
            _unitList.addItemDecoration(_mainDivider);
        } else {
            _mainAdapter = new UnitDisplayAdapter.FullUnitDisplayAdapter(new ListDataProvider<>(_displayContext));
            if (_mainDivider != null)
                _unitList.removeItemDecoration(_mainDivider);
        }
        _unitList.setAdapter(new AlphaInAnimationAdapter(_mainAdapter));
    }
}

