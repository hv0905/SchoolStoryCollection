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

import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;
import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import net.sakuratrak.schoolstorycollection.core.ListDataProvider;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;
import net.sakuratrak.schoolstorycollection.core.QuizHelper;
import net.sakuratrak.schoolstorycollection.core.ReviewRatio;

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
    private static final int REQUEST_QUIZ = 101;

    //region views
    private ConstraintLayout _root;
    private View _unitEmptyNotice;
    private RecyclerView _unitList;
    private FloatingActionButton _addUnitBtn;
    private RecycleViewDivider _mainDivider;
    private FrameLayout _multiQuizBtn;
    private TextView _multiQuizBtnText;
    private ImageButton _multiMoreBtn;
    private MaterialCardView _multiActionBar;
    private TextView _txtUnitEmptyNotice;
    //endregion

    //region fields
    private UnitDisplayAdapter _mainAdapter;
    private List<LearningUnitInfo> _context;
    private List<UnitDisplayAdapter.FullUnitDisplayAdapter.DataContext> _displayContext;
    private int _multiCount;
    private boolean _multiShowed;
    private final MainActivity.ChangeDisplayModeEventHandler _changeMode = this::changeDisplayMode;
    private final MainActivity.RequireRefreshEventHandler _requireRefresh = this::refresh;
    private final MainActivity.RequireRefreshEventHandler _dialogUpdate = this::refresh;
    //endregion

    //region methods
    public StatFragmentUnitFragment() {
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
        _txtUnitEmptyNotice = _root.findViewById(R.id.txtUnitEmptyNotice);

        _multiQuizBtn.setOnClickListener(v -> {
            ArrayList<LearningUnitInfo> selectedUnits = new ArrayList<>();
            for (int i = 0; i < _displayContext.size(); i++) {
                UnitDisplayAdapter.DataContext dc = _displayContext.get(i);
                if (dc.Checked) {
                    selectedUnits.add(dc.db);
                }
            }
            requestUnitQuiz(selectedUnits, selectedUnits.size() + "个单元");
        });

        //noinspection ConstantConditions
        _multiMoreBtn.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle(String.format(Locale.US, "已选择%d个单元", _multiCount))
                .setItems(R.array.unit_multi_options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            new AlertDialog.Builder(getContext())
                                    .setMessage(String.format(Locale.US, "真的归档/取消归档%d个单元吗？", _multiCount))
                                    .setTitle(R.string.confirmMultiHideTitle)
                                    .setIcon(R.drawable.ic_warning_black_24dp)
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
                            new AlertDialog.Builder(getContext())
                                    .setMessage(String.format(Locale.US, "确定要重置%d个单元的统计信息吗？", _multiCount))
                                    .setTitle(R.string.confirmMultiResetStat)
                                    .setIcon(R.drawable.ic_warning_black_24dp)
                                    .setPositiveButton(R.string.confirm, (dialog1, which1) -> {
                                        for (int i = 0; i < _displayContext.size(); i++) {
                                            UnitDisplayAdapter.DataContext dc = _displayContext.get(i);
                                            if (dc.Checked) {
                                                dc.db.resetStat(getContext());
                                            }
                                        }
                                        getParent().requireRefresh();
                                    })
                                    .setNegativeButton(R.string.cancel, null)
                                    .show();
                            break;
                        case 2:
                            new AlertDialog.Builder(getContext())
                                    .setMessage(String.format(Locale.US, "真的删除%d个单元吗？", _multiCount))
                                    .setTitle(R.string.confirmMultiRmTitle)
                                    .setIcon(R.drawable.ic_warning_black_24dp)
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
                    //noinspection ConstantConditions
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
        getParent().set_unitDialogUpdate(_dialogUpdate);
    }

    private void destroyTel() {
        getParent().removeRequireRefreshEvent(_requireRefresh);
        getParent().removeChangeDisplayModeEvent(_changeMode);
        getParent().set_unitDialogUpdate(null);
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


        int questionSum = 0;
        try {
            List<QuestionInfo> questions = new QuestionInfo.DbHelper(DbManager.getDefaultHelper(getContext())).findAllWithSubject(getParent().getCurrentSubject());
            for (QuestionInfo qi :
                    questions) {
                if (!qi.isHidden()) questionSum++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        _displayContext.clear();

        String[] keyword = null;
        boolean isHiddenShown = false;
        List<ReviewRatio> reviewRatios = null;

        if (getParent()._unitFilterDialog != null) {
            keyword = getParent()._unitFilterDialog.get_keyword() == null ? null : getParent()._unitFilterDialog.get_keyword().split(" ");
            isHiddenShown = getParent()._unitFilterDialog.is_isHiddenShown();
            reviewRatios = getParent()._unitFilterDialog.get_selectedRatios();
            if (keyword != null && keyword.length == 0) keyword = null;
            if (reviewRatios != null && reviewRatios.size() == 0) reviewRatios = null;
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
            int reviewValue = item.computeAvgReviewRatio();
            if (reviewRatios != null) {
                if (!reviewRatios.contains(ReviewRatio.getByRatio(reviewValue))) continue;
            }

            //ok,加入列表
            UnitDisplayAdapter.FullUnitDisplayAdapter.DataContext udiItem = UnitDisplayAdapter.DataContext.fromDb(item, questionSum, reviewValue);
            udiItem.DetailClicked = v -> goDetail(v, udiItem, item);
            udiItem.OnChecked = (buttonView, isChecked) -> {
                if (isChecked) _multiCount++;
                else {
                    if (_multiCount > 0)
                        _multiCount--;
                }
                updateMulti(false);
            };
            udiItem.QuizClicked = v -> goSingleQuiz(udiItem, item);
            udiItem.MenuClicked = v -> {
                //noinspection ConstantConditions
                new AlertDialog.Builder(getContext())
                        .setItems(R.array.list_options, (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    goDetail(v, udiItem, item);
                                    break;
                                case 1:
                                    goSingleQuiz(udiItem, item);
                                    break;
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;
            };
            _displayContext.add(udiItem);
        }

        if (_displayContext.size() == 0) {
            _unitEmptyNotice.setVisibility(View.VISIBLE);
            _txtUnitEmptyNotice.setText(getParent()._unitFilterDialog != null && getParent()._unitFilterDialog.isFilterActive() ? R.string.filterEmptyUnitUi : R.string.unitEmptyUi);
        } else {
            _unitEmptyNotice.setVisibility(View.INVISIBLE);
        }
    }

    private void goSingleQuiz(UnitDisplayAdapter.DataContext udiItem, LearningUnitInfo item) {
        ArrayList<LearningUnitInfo> unit = new ArrayList<>();
        unit.add(item);
        requestUnitQuiz(unit, item.getName());
    }

    private void refresh() {
        refreshContext();
        _mainAdapter.notifyDataSetChanged();
    }

    private void goDetail(View v, UnitDisplayAdapter.DataContext udiItem, LearningUnitInfo item) {
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
            case REQUEST_QUIZ:
                if (resultCode != QuizActivity.RESULT_NONE_DONE) {
                    getParent().requireRefresh();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changeDisplayMode(boolean isSecondMode) {
        if (isSecondMode) {
            _mainAdapter = new UnitDisplayAdapter.SimpleUnitDisplayAdapter(new ListDataProvider<>(_displayContext));
            if (_mainDivider == null)
                //noinspection ConstantConditions
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

    private void requestUnitQuiz(List<LearningUnitInfo> units, String dist) {
        //noinspection ConstantConditions
        new AlertDialog.Builder(getContext()).setItems(R.array.list_quiz, (dialog, which) -> {
            List<QuestionInfo> in = new ArrayList<>();
            for (LearningUnitInfo unit : units) {
                for (QuestionInfo question :
                        unit.getQuestions()) {
                    if (!question.isHidden()) in.add(question);
                }
            }
            List<QuestionInfo> quizContext = null;
            int n = AppSettingsMaster.getQuizSize(getContext());
            switch (which) {
                case 0:
                    //sm
                    quizContext = QuizHelper.prepareSmartQuiz(in, n);
                    break;
                case 1:
                    //rd
                    quizContext = QuizHelper.prepareRandomQuiz(in, n);
                    break;
            }
            if (quizContext == null) {
                Snackbar.make(_root, R.string.quizWarnEmptyQuestion, Snackbar.LENGTH_LONG).show();
                return;
            }
            ArrayList<Integer> quizIds = new ArrayList<>(quizContext.size());
            for (QuestionInfo qc :
                    quizContext) {
                quizIds.add(qc.getId());
            }

            Intent intent = new Intent(getContext(), QuizActivity.class);
            intent.putIntegerArrayListExtra(QuizActivity.EXTRA_QUESTION_IDS, quizIds);
            intent.putExtra(QuizActivity.EXTRA_MODE, QuizActivity.MODE_LIST);
            intent.putExtra(QuizActivity.EXTRA_QUIZ_DESCRIPTION, String.format("单元%s小测:%s", which == 0 ? "智能" : "随机", dist));
            startActivityForResult(intent, REQUEST_QUIZ);
        })
                .setPositiveButton(R.string.cancel, null)
                .show();
    }
    //endregion
}

