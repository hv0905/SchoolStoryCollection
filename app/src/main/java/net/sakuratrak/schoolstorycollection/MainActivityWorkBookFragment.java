package net.sakuratrak.schoolstorycollection;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;

import net.sakuratrak.schoolstorycollection.core.AppMaster;
import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.IListedDataProvidable;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;
import net.sakuratrak.schoolstorycollection.core.QuestionType;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public final class MainActivityWorkBookFragment extends Fragment {

    public static final String TAG = "workbookFragment";
    private static final int REQUEST_QUIZ = 1002;
    private static final int REQUEST_DETAIL = 1001;
    private static final int REQUEST_ADD_QUESTION = 1000;

    //region views
    private ConstraintLayout _root;
    private RecyclerView _itemList;
    private FloatingActionMenu _addItemBtn;
    private Runnable _notifyToUpdate;
    private QuestionItemAdapter _mainAdapter;
    private int _questionCount;
    private List<QuestionInfo> _contexts;
    private View _workbookEmptyNotice;
    private DataContextList _defaultList = this.new DataContextList();
    private FloatingActionButton _addItem_singleChoice;
    private FloatingActionButton _addItem_multiChoice;
    private FloatingActionButton _addItem_editableFill;
    private FloatingActionButton _addItem_fill;
    private FloatingActionButton _addItem_answer;
    //endregion

    //region fields
    private int _currentDetailIndex = -1;
    //endregion

    private RecycleViewDivider _mainDivider;

    private MainActivity.RequireRefreshEventHandler _refreshEvent = this::refreshList;

    public Runnable getNotifyToUpdate() {
        return _notifyToUpdate;
    }

    public void setNotifyToUpdate(Runnable _notifyToUpdate) {
        this._notifyToUpdate = _notifyToUpdate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        _root = (ConstraintLayout) inflater.inflate(R.layout.fragment_main_activity_workbook, container, false);

        _itemList = _root.findViewById(R.id.itemList);
        _addItemBtn = _root.findViewById(R.id.addItemBtn);

        _addItem_singleChoice = _root.findViewById(R.id.addItem_singleChoice);
        _addItem_multiChoice = _root.findViewById(R.id.addItem_multiChoice);
        _addItem_editableFill = _root.findViewById(R.id.addItem_editableFill);
        _addItem_fill = _root.findViewById(R.id.addItem_fill);
        _addItem_answer = _root.findViewById(R.id.addItem_answer);

        _workbookEmptyNotice = _root.findViewById(R.id.workbookEmptyNotice);

        _addItem_singleChoice.setOnClickListener(v -> onAddItem(QuestionType.SINGLE_CHOICE));

        _addItem_multiChoice.setOnClickListener(v -> onAddItem(QuestionType.MULTIPLY_CHOICE));

        _addItem_editableFill.setOnClickListener(v -> onAddItem(QuestionType.TYPEABLE_BLANK));

        _addItem_fill.setOnClickListener(v -> onAddItem(QuestionType.BLANK));

        _addItem_answer.setOnClickListener(v -> onAddItem(QuestionType.ANSWER));

        _itemList.setLayoutManager(new LinearLayoutManager(getParent(), RecyclerView.VERTICAL, false));
        _itemList.setItemAnimator(new LandingAnimator());

        setRefreshEventStatus(true);
        setDisplayMode(getParent().is_isSecondDisplayMode());
        refreshList();
        return _root;
    }

    @Override
    public void onDestroyView() {
        setRefreshEventStatus(false);
        super.onDestroyView();
    }

    private void setRefreshEventStatus(boolean enabled) {
        if (enabled) {
            getParent().addRequireRefreshEvent(_refreshEvent);
        } else {
            getParent().removeRequireRefreshEvent(_refreshEvent);
        }
    }

    private MainActivity getParent() {
        return (MainActivity) getActivity();
    }

    public void refreshList() {
        Log.d(TAG, "refreshList: go");

        try {
            refreshContext();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        _mainAdapter.notifyDataSetChanged();
        if (_contexts.size() == 0) {
            _workbookEmptyNotice.setVisibility(View.VISIBLE);
        } else {
            _workbookEmptyNotice.setVisibility(View.INVISIBLE);
        }

    }

    public void refreshContext() throws SQLException {
        QuestionInfo.QuestionInfoDaoManager mgr = new QuestionInfo.QuestionInfoDaoManager(DbManager.getDefaultHelper(getContext()).getQuestionInfos());
        _contexts = mgr.FindAllWithSubject(getParent().getCurrentSubject());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_ADD_QUESTION:
                if (resultCode == Activity.RESULT_OK) {
                    setRefreshEventStatus(false);
                    getParent().requireRefresh();
                    setRefreshEventStatus(true);
                    try {
                        refreshContext();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return;
                    }
                    _mainAdapter.notifyItemInserted(0);
                    _itemList.scrollToPosition(0);
                }
                break;
            case REQUEST_DETAIL:
                switch (resultCode) {
                    case QuestionDetailActivity.RESULT_DELETED:
                        try {
                            refreshContext();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            return;
                        }

                        Snackbar.make(_root, "已删除", Snackbar.LENGTH_LONG).show();
                        if (_currentDetailIndex != -1)
                            _mainAdapter.notifyItemRemoved(_currentDetailIndex);
                        else _mainAdapter.notifyDataSetChanged();

                        setRefreshEventStatus(false);
                        getParent().requireRefresh();
                        setRefreshEventStatus(true);
                        break;
                    case QuestionDetailActivity.RESULT_EDITED:
                        try {
                            refreshContext();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            return;
                        }

                        if (_currentDetailIndex != -1) {
                            _mainAdapter.notifyItemChanged(_currentDetailIndex);
                        } else _mainAdapter.notifyDataSetChanged();

                        setRefreshEventStatus(false);
                        getParent().requireRefresh();
                        setRefreshEventStatus(true);
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void goDetail(QuestionInfo info, View sharedView, int index) {
        _currentDetailIndex = index;
        Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
        intent.putExtra(QuestionDetailActivity.EXTRA_QUESTION_ID, info.getId());
        //ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), sharedView, "topImage");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
        startActivityForResult(intent, REQUEST_DETAIL, options.toBundle());
    }

    private void goQuiz(QuestionInfo info) {
        Intent intent = new Intent(getActivity(), QuizActivity.class);
        intent.putExtra(QuizActivity.EXTRA_MODE, QuizActivity.MODE_SOLO);
        intent.putExtra(QuizActivity.EXTRA_QUESTION_ID, info.getId());
        startActivityForResult(intent, REQUEST_QUIZ);
    }

    private void showOptionMenu(QuestionInfo info, int index) {
        AlertDialog builder = new AlertDialog.Builder(getContext()).
                setItems(new String[]{getString(R.string.view), getString(R.string.test)}, (dialog, which) -> {
            switch (which) {
                case 0:
                    goDetail(info, null, index);
                    break;
                case 1:
                    goQuiz(info);
                    break;
            }
        }).setNegativeButton(R.string.cancel, null).show();
    }

    void onAddItem(QuestionType type) {
        Intent intent = new Intent(getParent(), QuestionEditActivity.class);
        intent.putExtra(QuestionEditActivity.EXTRA_QUESTION_TYPE_ID, type.getId());
        intent.putExtra(QuestionEditActivity.EXTRA_SUBJECT, getParent().getCurrentSubject());
        startActivityForResult(intent, REQUEST_ADD_QUESTION);
        _addItemBtn.close(true);
    }

    void setDisplayMode(boolean second) {
        if (second) {
            _mainAdapter = new QuestionItemAdapter.SimpleQuestionItemAdapter(_defaultList);
            if (_mainDivider == null)
                _mainDivider = new RecycleViewDivider(RecyclerView.VERTICAL, getParent());
            _itemList.addItemDecoration(_mainDivider);

        } else {
            _mainAdapter = new QuestionItemAdapter.FullQuestionItemAdapter(_defaultList);
            if (_mainDivider != null)
                _itemList.removeItemDecoration(_mainDivider);
        }
        _itemList.setAdapter(new AlphaInAnimationAdapter(_mainAdapter));
    }

    public class DataContextList implements IListedDataProvidable<QuestionItemAdapter.DataContext> {

        @Override
        public int count() {
            return _contexts.size();
        }

        @Override
        public QuestionItemAdapter.DataContext get(int index) {
            Log.d(TAG, "get: query:" + index);
            SimpleDateFormat format = new SimpleDateFormat("yy.mm.dd", Locale.US);

            QuestionInfo info = _contexts.get(count() - 1 - index);
            return new QuestionItemAdapter.DataContext(info.getTitle(),
                    format.format(info.getAuthorTime()), info.getUnit() != null ? info.getUnit().getName() : getString(R.string.emptyUnit),
                    Uri.fromFile(AppMaster.getThumbFile(getContext(), info.getQuestionImage()[0])),
                    info.getDifficulty() / 2f,
                    info.isFavourite(),
                    v -> {
                        goDetail(info, v, index);
                    },
                    v -> goQuiz(info),
                    v -> {
                        showOptionMenu(info, index);
                        return true;
                    });
        }
    }
}
