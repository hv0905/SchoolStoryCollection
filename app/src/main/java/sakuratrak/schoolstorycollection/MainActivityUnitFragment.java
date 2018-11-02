package sakuratrak.schoolstorycollection;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.LearningUnitInfo;


public class MainActivityUnitFragment extends Fragment {

    private Runnable _notifyUnitRefresh;

    //region UI controls
    public ConstraintLayout _root;
    public View _unitEmptyNotice;
    public RecyclerView _unitList;
    public FloatingActionButton _addUnitBtn;
    public MainActivity _backupParent;
    //endregion


    public Runnable getNotifyUnitRefresh() {
        return _notifyUnitRefresh;
    }

    public void setNotifyUnitRefresh(Runnable _notifyUnitRefresh) {
        this._notifyUnitRefresh = _notifyUnitRefresh;
    }

    public MainActivityUnitFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("Creating Fragment. . . . . .");
        _root =  (ConstraintLayout) inflater.inflate(R.layout.fragment_main_activity_unit, container, false);
        _unitList = _root.findViewById(R.id.unitList);
        _unitEmptyNotice = _root.findViewById(R.id.unitEmptyNotice);
        _addUnitBtn = _root.findViewById(R.id.addUnitBtn);
        
        _addUnitBtn.setOnClickListener(v -> {
            final EditText et = new EditText(getParent());
            et.setSingleLine();
            AlertDialog.Builder ab = new AlertDialog.Builder(getParent()).setIcon(R.drawable.ic_book_black_24dp).setTitle("创建单元")
                    .setMessage("单元名称:")
                    .setView(et).setNegativeButton("完成", (dialog, which) -> {
                        if (et.getText().toString().trim().isEmpty()) {
                            new AlertDialog.Builder(getParent()).setMessage("请输入单元名称").setTitle("错误").setNegativeButton("确定", null).setIcon(R.drawable.ic_warning_black_24dp).show();
                            return;
                        }
                        try {
                            DbManager.getHelper(getParent()).getLearningUnitInfos().create(new LearningUnitInfo(et.getText().toString().trim(), getParent().getCurrentSubject()));
                            DbManager.releaseHelper();
                        } catch (SQLException e) {
                            Snackbar.make(_root, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        refreshUnit();
                    })
                    .setPositiveButton("取消", null);

            ab.show();          
        });

        _unitList.setLayoutManager(new LinearLayoutManager(getParent(), LinearLayoutManager.VERTICAL, false));

        refreshUnit();

        return _root;
    }

    public void refreshUnit(){
        if(!this.isAdded()) return;
        ArrayList<UnitDisplayAdapter.UnitDisplayDataContext> udi = new ArrayList<>();
        List<LearningUnitInfo> luis;
        try {
            luis = (DbManager.getHelper(getParent())).getLearningUnitInfos().queryForEq("subjectId", getParent().getCurrentSubject().getId());
            DbManager.releaseHelper();
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
            UnitDisplayAdapter.UnitDisplayDataContext udiItem = new UnitDisplayAdapter.UnitDisplayDataContext(item.getName(), item.getExerciseLogCount(), item.computeCorrectRatio(), item.getExerciseLogCount(), 50, item.getIfNeedMoreQuiz());
            udiItem.RmClicked = v -> notifyRmUnit(v, udiItem, item);
            udiItem.ResetClicked = v -> notifyResetUnit(v, udiItem, item);
            udi.add(udiItem);
        }
        UnitDisplayAdapter uda = new UnitDisplayAdapter(udi);
        _unitList.setAdapter(uda);


    }

    private void notifyResetUnit(View v, UnitDisplayAdapter.UnitDisplayDataContext udiItem, LearningUnitInfo item) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getParent());
        ad.setTitle(R.string.confirmLog_title).setIcon(R.drawable.ic_warning_black_24dp).setMessage(String.format(getString(R.string.confirmLog_msg), udiItem.Title));
        ad.setPositiveButton(R.string.cancel, null).setNegativeButton(R.string.confirm, (dialog, which) -> {

        });
        ad.show();
    }

    private void notifyRmUnit(View v, UnitDisplayAdapter.UnitDisplayDataContext udiItem, LearningUnitInfo item) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getParent());
        ad.setTitle(R.string.confirmRm_title).setIcon(R.drawable.ic_warning_black_24dp).setMessage(String.format(getString(R.string.confirmRm_msg), udiItem.Title));
        ad.setPositiveButton(R.string.cancel, null).setNegativeButton(R.string.confirm, (dialog, which) -> {
            try {
                DbManager.getHelper(getParent()).getLearningUnitInfos().delete(item);
                DbManager.releaseHelper();
            } catch (SQLException e) {
                Snackbar.make(_root, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
            }
            refreshUnit();
        });
        ad.show();
    }

    private MainActivity getParent(){
        if(getActivity() == null){
            return _backupParent;
        }return (MainActivity) getActivity();
    }
}

