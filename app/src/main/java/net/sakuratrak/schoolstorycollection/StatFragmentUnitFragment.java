package net.sakuratrak.schoolstorycollection;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import net.sakuratrak.schoolstorycollection.core.ListDataProvider;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public final class StatFragmentUnitFragment extends Fragment {

    private static final String TAG = "Stat_UnitFragment";
    private static final int REQUEST_DETAIL = 100;

    //region views
    private ConstraintLayout _root;
    private View _unitEmptyNotice;
    private RecyclerView _unitList;
    private FloatingActionButton _addUnitBtn;
    //endregion

    private Runnable _notifyUnitRefresh;
    private UnitDisplayAdapter _mainAdapter;
    private List<LearningUnitInfo> _context;
    private List<UnitDisplayAdapter.FullUnitDisplayAdapter.DataContext> _displayContext;
    private RecycleViewDivider _mainDivider;
    private int _multiCount;
    private boolean _multiShowed;
    private FrameLayout _multiQuizBtn;
    private TextView _multiQuizBtnText;
    private ImageButton _multiMoreBtn;
    private MaterialCardView _multiActionBar;
    private final MainActivity.RequireRefreshEventHandler _requireRefresh = this::refresh;
    private final MainActivity.ChangeDisplayModeEventHandler _changeMode = this::changeDisplayMode;


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
        _multiQuizBtn = (FrameLayout) _root.findViewById(R.id.multiQuizBtn);
        _multiQuizBtnText = (TextView) _root.findViewById(R.id.multiQuizBtnText);
        _multiMoreBtn = (ImageButton) _root.findViewById(R.id.multiMoreBtn);
        _multiActionBar = (MaterialCardView) _root.findViewById(R.id.multiActionBar);

        _multiQuizBtn.setOnClickListener(v -> {
            // TODO: 2019/2/1 调用单元小测
        });

        _multiMoreBtn.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle(String.format(Locale.US, "已选择%d个单元", _multiCount))
                .setItems(R.array.unit_multi_options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // TODO: 2019/2/1 box
                            new AlertDialog.Builder(getContext())
                                    .setMessage(String.format(Locale.US, "真的归档/取消归档%d个单元吗？", _multiCount))
                                    .setTitle(R.string.confirmMultiHideTitle)
                                    .setPositiveButton(R.string.confirm, (dialog1, which1) -> {
                                        for (int i = 0; i < _displayContext.size(); i++) {
                                            UnitDisplayAdapter.DataContext dc = _displayContext.get(i);
                                            if (dc.Checked) {
                                                dc.db.setHidden(!dc.db.isHidden());
                                                try {
                                                    DbManager.getDefaultHelper(getContext()).getLearningUnitInfos().update(dc.db);
                                                } catch (SQLException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        getParent().requireRefresh();
                                    })
                                    .setNegativeButton(R.string.cancel, null)
                                    .show();
                            break;
                        case 1:
                            // TODO: 2019/2/1 clear
                            break;

                        case 2:
                            // TODO: 2019/2/1 rm
                            new AlertDialog.Builder(getContext())
                                    .setMessage(String.format(Locale.US, "真的删除%d个单元吗？", _multiCount))
                                    .setTitle(R.string.confirmMultiRmTitle)
                                    .setPositiveButton(R.string.confirm, (dialog1, which1) -> {
                                        for (int i = 0; i < _displayContext.size(); i++) {
                                            UnitDisplayAdapter.DataContext dc = _displayContext.get(i);
                                            if (dc.Checked) {
                                                try {
                                                    DbManager.getDefaultHelper(getContext()).getLearningUnitInfos().delete(dc.db);
                                                } catch (SQLException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        getParent().requireRefresh();
                                    })
                                    .setNegativeButton(R.string.cancel, null)
                                    .show();
                            break;
                    }
                })
                .setIcon(R.drawable.ic_done_all_black_24dp)
                .setPositiveButton(R.string.cancel, null)
                .setNegativeButton(R.string.unselectAll, (dialog, which) -> {
                    for (UnitDisplayAdapter.DataContext dc :
                            _displayContext) {
                        dc.Checked = false;
                    }
                    _mainAdapter.notifyDataSetChanged();
                    _multiCount = 0;
                    updateMulti(false);
                })
                .show());

        _addUnitBtn.setOnClickListener(v -> new AlertDialog.Builder(getParent())
                .setIcon(R.drawable.ic_book_black_24dp)
                .setTitle(R.string.newUnitTitle)
                .setView(R.layout.dialog_add_unit)
                .setPositiveButton(R.string.done, (dialog, which) -> {
                    AlertDialog dg = (AlertDialog) dialog;
                    TextInputEditText tiet = dg.findViewById(R.id.txtUnitName);
                    if (tiet.getText() == null || tiet.getText().toString().trim().isEmpty()) {
                        new AlertDialog.Builder(getParent())
                                .setMessage("请输入单元名称")
                                .setTitle(R.string.error)
                                .setNegativeButton(R.string.confirm, null)
                                .setIcon(R.drawable.ic_warning_black_24dp)
                                .show();
                        return;
                    }
                    try {
                        DbManager.getDefaultHelper(getParent()).getLearningUnitInfos().create(new LearningUnitInfo(tiet.getText().toString().trim(), getParent().getCurrentSubject()));
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
                .setNegativeButton("取消", null)
                .show());

        _unitList.setLayoutManager(new LinearLayoutManager(getParent(), RecyclerView.VERTICAL, false));
        _unitList.setItemAnimator(new LandingAnimator());

        _displayContext = new ArrayList<>();
        changeDisplayMode(getParent().is_isSecondDisplayMode());

        regTel();
        refresh();

        return _root;
    }

    private void regTel() {
        getParent().addRequireRefreshEvent(_requireRefresh);
        getParent().addChangeDisplayModeEvent(_changeMode);
    }

    private void destroyTel() {
        getParent().removeRequireRefreshEvent(_requireRefresh);
        getParent().removeChangeDisplayModeEvent(_changeMode);
    }

    @Override
    public void onDestroyView() {
        destroyTel();
        super.onDestroyView();

    }

    private void refreshContext() {
        try {
            _context = (DbManager.getDefaultHelper(getParent())).getLearningUnitInfos().queryForEq("subjectId", getParent().getCurrentSubject().getId());
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

        String[] keyword = null;
        boolean isHiddenShown = false;

        if (getParent()._unitFilterDialog != null) {
            keyword = getParent()._unitFilterDialog.get_keyword() == null ? null : getParent()._unitFilterDialog.get_keyword().split(" ");
            isHiddenShown = getParent()._unitFilterDialog.is_isHiddenShown();
            if (keyword != null && keyword.length == 0) keyword = null;
        }


        for (int i = _context.size() - 1; i >= 0; i--) {
            LearningUnitInfo item = _context.get(i);
            //筛选
            if (!isHiddenShown && item.isHidden()) continue;
            if (keyword != null) {
                boolean mainGoFlag = false;
                for (String word :
                        keyword) {
                    if (item.getName().contains(word)) {
                        mainGoFlag = true;
                        break;
                    }
                }
                if (!mainGoFlag) continue;
            }

            //ok,加入列表
            UnitDisplayAdapter.FullUnitDisplayAdapter.DataContext udiItem = UnitDisplayAdapter.DataContext.fromDb(item, questionSum);
            udiItem.DetailClicked = v -> detailClicked(v, udiItem, item);
            udiItem.OnChecked = (buttonView, isChecked) -> {
                if (isChecked) _multiCount++;
                else {
                    if (_multiCount > 0)
                        _multiCount--;
                }
                updateMulti(false);
            };
            _displayContext.add(udiItem);
        }


        if (_notifyUnitRefresh != null) {
            _notifyUnitRefresh.run();
        }
    }

    public void refresh() {
        refreshContext();
        _mainAdapter.notifyDataSetChanged();
    }

    private void detailClicked(View v, UnitDisplayAdapter.DataContext udiItem, LearningUnitInfo item) {
        Intent intent = new Intent(getActivity(), UnitDetailActivity.class);
        intent.putExtra(UnitDetailActivity.EXTRA_CONTEXT_ID, item.getId());
        ActivityOptions ao;
        if (getParent().is_isSecondDisplayMode()) {
            ao = ActivityOptions.makeSceneTransitionAnimation(getActivity());
        } else {
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
        return (MainActivity) getContext();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DETAIL:
                switch (resultCode) {
                    case UnitDetailActivity.RESULT_DELETED:
                        Snackbar.make(_root, R.string.deleted, Snackbar.LENGTH_LONG).show();
                        getParent().requireRefresh();
                        break;
                    case UnitDetailActivity.RESULT_CHANGED:
                        getParent().requireRefresh();
                        break;
                    case UnitDetailActivity.RESULT_HIDDEN:
                        getParent().requireRefresh();
                        break;
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
            updateMulti(false);
        } else {
            _mainAdapter = new UnitDisplayAdapter.FullUnitDisplayAdapter(new ListDataProvider<>(_displayContext));
            if (_mainDivider != null)
                _unitList.removeItemDecoration(_mainDivider);
            updateMulti(true);

        }
        _unitList.setAdapter(new AlphaInAnimationAdapter(_mainAdapter));
    }

    private void updateMulti(boolean shouldHide) {
        _multiQuizBtnText.setText(String.format(Locale.US, "小测%d个单元", _multiCount));
        boolean isMulti = _multiCount != 0;

        if (_multiShowed && (!isMulti || shouldHide)) {
            //hide
            _multiShowed = false;
            _multiActionBar.setVisibility(View.VISIBLE);
            _multiActionBar.setTranslationY(0);
            _multiActionBar.setAlpha(1);
            _multiActionBar.animate()
                    .translationYBy(100)
                    .alpha(0)
                    .setDuration(200)
                    .withEndAction(() -> _multiActionBar.setVisibility(View.INVISIBLE))
                    .start();
        }

        if (!_multiShowed && isMulti && !shouldHide) {
            //show
            _multiShowed = true;
            _multiActionBar.setVisibility(View.VISIBLE);
            _multiActionBar.setTranslationY(100);
            _multiActionBar.setAlpha(0);
            _multiActionBar.animate()
                    .translationYBy(-100)
                    .alpha(1)
                    .setDuration(200)
                    .start();
        }
    }
}

