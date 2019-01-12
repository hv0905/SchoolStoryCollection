package net.sakuratrak.schoolstorycollection;

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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public final class StatFragmentUnitFragment extends Fragment {

    public static final String TAG = "Stat_UnitFragment";
    public static final int REQUEST_DETAIL = 100;
    //region UI controls
    public ConstraintLayout _root;
    public View _unitEmptyNotice;
    public RecyclerView _unitList;
    public FloatingActionButton _addUnitBtn;
    //endregion
    private Runnable _notifyUnitRefresh;

    private final MainActivity.RequireRefreshEventHandler _requireRefresh = this::refreshUnit;


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
                        getParent().requireRefresh();
                    })
                    .setNegativeButton("取消", null);

            ab.show();
        });

        _unitList.setLayoutManager(new LinearLayoutManager(getParent(), RecyclerView.VERTICAL, false));


        getParent().addRequireRefreshEvent(_requireRefresh);
        refreshUnit();

        return _root;
    }

    @Override
    public void onDestroyView() {
        getParent().removeRequireRefreshEvent(_requireRefresh);
        super.onDestroyView();

    }

    public void refreshUnit() {
        if (!this.isAdded()) return;
        ArrayList<UnitDisplayAdapter.DataContext> udi = new ArrayList<>();
        List<LearningUnitInfo> luis;
        try {
            luis = (DbManager.getDefaultHelper(getParent())).getLearningUnitInfos().queryForEq("subjectId", getParent().getCurrentSubject().getId());
        } catch (SQLException e) {
            e.printStackTrace();
            Snackbar.make(_root, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (luis.size() == 0) {
            _unitEmptyNotice.setVisibility(View.VISIBLE);
        } else {
            _unitEmptyNotice.setVisibility(View.INVISIBLE);
        }
        for (LearningUnitInfo item : luis) {
            UnitDisplayAdapter.DataContext udiItem = new UnitDisplayAdapter.DataContext(item.getName(), item.getExerciseLogCount(), item.computeCorrectRatio(), item.getExerciseLogCount(), 50, item.getIfNeedMoreQuiz());
            udiItem.QuestionCount = item.getQuestions().size();
            udiItem.DetailClicked = v -> detailClicked(v, udiItem, item);
            udi.add(udiItem);
        }
        if (_unitList.getAdapter() == null) {
            UnitDisplayAdapter uda = new UnitDisplayAdapter(new ListDataProvider<>(udi));
            _unitList.setAdapter(uda);
        } else {
            ((UnitDisplayAdapter) _unitList.getAdapter()).setDataContext(new ListDataProvider<>(udi));
        }

        if (_notifyUnitRefresh != null) {
            _notifyUnitRefresh.run();
        }
    }

    private void detailClicked(View v, UnitDisplayAdapter.DataContext udiItem, LearningUnitInfo item) {
        Intent intent = new Intent(getActivity(), UnitDetailActivity.class);
        intent.putExtra(UnitDetailActivity.EXTRA_CONTEXT_ID, item.getId());
        startActivityForResult(intent, REQUEST_DETAIL);

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
}

