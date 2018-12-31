package net.sakuratrak.schoolstorycollection;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import net.sakuratrak.schoolstorycollection.core.AppMaster;
import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.IListedDataProvidable;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;
import net.sakuratrak.schoolstorycollection.core.QuestionType;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public final class MainActivityWorkBookFragment extends Fragment {

    public static final String TAG = "workbookFragment";

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

    private static final int REQUEST_QUIZ = 1002;
    private static final int REQUEST_DETAIL = 1001;
    private static final int REQUEST_ADD_QUESTION = 1000;

    public Runnable getNotifyToUpdate() {
        return _notifyToUpdate;
    }

    public void setNotifyToUpdate(Runnable _notifyToUpdate) {
        this._notifyToUpdate = _notifyToUpdate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        _itemList.setLayoutManager(new LinearLayoutManager(getParent(), LinearLayoutManager.VERTICAL, false));
        getParent().addSubjectUpdateEvent(this::refreshList);
        setDisplayMode(false);
        refreshList();
        return _root;
    }

    private MainActivity getParent() {
        return (MainActivity) getActivity();
    }

    public void refreshList() {
        Log.d(TAG, "refreshList: go");

        QuestionInfo.QuestionInfoDaoManager mgr = new QuestionInfo.QuestionInfoDaoManager(DbManager.getDefaultHelper(getContext()).getQuestionInfos());
        try {
            _contexts = mgr.FindAllWithSubject(getParent().getCurrentSubject());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_ADD_QUESTION:
                if (resultCode == RESULT_OK)
                    getParent().requireRefresh();
                break;
            case REQUEST_DETAIL:
                switch (resultCode) {
                    case QuestionDetailActivity.RESULT_DELETED:
                        Snackbar.make(_root, "已删除", Snackbar.LENGTH_LONG).show();
                    case QuestionDetailActivity.RESULT_EDITED:
                        getParent().requireRefresh();
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void goDetail(QuestionInfo info, View sharedView) {
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

    private void showOptionMenu(QuestionInfo info){
        AlertDialog builder = new AlertDialog.Builder(getContext()).setItems(new String[]{getString(R.string.view), getString(R.string.test)}, (dialog, which) -> {
            switch (which){
                case 0:
                    goDetail(info,null);
                    break;
                case 1:
                    goQuiz(info);
                    break;
            }
        }).setNegativeButton(R.string.cancel,null).show();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: ");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        super.onDetach();
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
        } else {
            _mainAdapter = new QuestionItemAdapter.FullQuestionItemAdapter(_defaultList);
        }
        _itemList.setAdapter(_mainAdapter);
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

            QuestionInfo info = _contexts.get(index);
            return new QuestionItemAdapter.DataContext(info.getTitle(),
                    format.format(info.getAuthorTime()), info.getUnit() != null ? info.getUnit().getName() : getString(R.string.emptyUnit),
                    Uri.fromFile(AppMaster.getThumbFile(getContext(), info.getQuestionImage()[0])),
                    info.getDifficulty() / 2f,
                    info.isFavourite(),
                    v -> goDetail(info, v),
                    v -> goQuiz(info),
                    v -> {
                showOptionMenu(info);
                return true;
            });
        }
    }

}
