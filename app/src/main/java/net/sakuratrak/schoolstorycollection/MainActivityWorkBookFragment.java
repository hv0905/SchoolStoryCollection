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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.sakuratrak.schoolstorycollection.core.AppMaster;
import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningSubject;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;
import net.sakuratrak.schoolstorycollection.core.QuestionType;

import static android.app.Activity.RESULT_OK;

public final class MainActivityWorkBookFragment extends Fragment {

    public static final String TAG = "workbookFragment";

    private ConstraintLayout _root;
    private RecyclerView _itemList;
    private FloatingActionMenu _addItemBtn;
    private Runnable _notifyToUpdate;
    private QuestionItemAdapter _mainAdapter;
    private View _workbookEmptyNotice;

    private FloatingActionButton _addItem_singleChoice;
    private FloatingActionButton _addItem_multiChoice;
    private FloatingActionButton _addItem_editableFill;
    private FloatingActionButton _addItem_fill;
    private FloatingActionButton _addItem_answer;

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
        _mainAdapter = new QuestionItemAdapter(new ArrayList<>());
        _itemList.setAdapter(_mainAdapter);
        getParent().addSubjectUpdateEvent(this::refreshList);
        refreshList();
        return _root;
    }

    private MainActivity getParent() {
        return (MainActivity) getActivity();
    }

    public void refreshList() {
        Log.d(TAG, "refreshList: go");
        ArrayList<QuestionItemAdapter.DataContext> context = _mainAdapter.get_dataContext();
        context.clear();
        QuestionInfo.QuestionInfoDaoManager mgr = new QuestionInfo.QuestionInfoDaoManager(DbManager.getDefaultHelper(getContext()).getQuestionInfos());
        List<QuestionInfo> infos;
        try {
            infos = mgr.FindAllWithSubject(getParent().getCurrentSubject());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        for (QuestionInfo info : infos) {
            SimpleDateFormat format = new SimpleDateFormat("yy.mm.dd", Locale.US);

            QuestionItemAdapter.DataContext item = new QuestionItemAdapter.DataContext(info.getTitle(),
                    format.format(info.getAuthorTime()),info.getUnit() != null ? info.getUnit().getName() : getString(R.string.emptyUnit),
                    Uri.fromFile(AppMaster.getThumbFile(getContext(),info.getQuestionImage().get(0))),
                    info.getDifficulty() / 2f,
                    info.isFavourite(),
                    v -> goDetail(info.getId(),v),
                    null);

            context.add(item);
        }
        _mainAdapter.set_dataContext(context);
        if (context.size() == 0) {
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

    private void goDetail(int id, View sharedView) {
        Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
        intent.putExtra(QuestionDetailActivity.EXTRA_QUESTION_ID, id);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), sharedView, "topImage");
        startActivityForResult(intent, REQUEST_DETAIL, options.toBundle());
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
        _addItemBtn.close(false);
    }
}
