package net.sakuratrak.schoolstorycollection;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.ExerciseLog;
import net.sakuratrak.schoolstorycollection.core.ListDataProvider;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public final class MainActivityQuizFragment extends Fragment {

    public static final String TAG = "MainWindow_Quiz";

    ViewGroup _root;
    private ConstraintLayout _operateButtons;
    private View _quickQuizButton;
    private View _unitQuizButton;
    private RecyclerView _listLog;
    private ExerciseLogAdapter _mainAdapter;
    private ArrayList<ExerciseLogAdapter.DataContext> _logContext;
    private final MainActivity.RequireRefreshEventHandler _update = this::update;

    public MainActivityQuizFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _root = (ViewGroup) inflater.inflate(R.layout.fragment_main_activity_quiz, container, false);
        _operateButtons = _root.findViewById(R.id.operateButtons);
        _quickQuizButton = _root.findViewById(R.id.quickQuizBtn);
        _unitQuizButton = _root.findViewById(R.id.unitQuizBtn);
        _listLog = _root.findViewById(R.id.listLog);
        //_buttonTest = _root.findViewById(R.id.buttonTest);

        _quickQuizButton.setOnClickListener(v -> {
            //get a lot of questions
            ArrayList<Integer> ids = new ArrayList<>();
            List<QuestionInfo> infos = null;
            try {
                infos = new QuestionInfo.QuestionInfoDaoManager(
                        DbManager.getDefaultHelper(getContext())
                                .getQuestionInfos())
                        .FindAllWithSubject(getParent().getCurrentSubject());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for (QuestionInfo info :
                    infos) {
                ids.add(info.getId());
            }
            Intent intent = new Intent(getActivity(), QuizActivity.class);
            intent.putIntegerArrayListExtra(QuizActivity.EXTRA_QUESTION_IDS, ids);
            intent.putExtra(QuizActivity.EXTRA_MODE, QuizActivity.MODE_LIST);
            startActivity(intent);

        });

        _unitQuizButton.setOnClickListener(v -> {

        });

        //_buttonTest.setOnClickListener(v -> AlarmReceiver.setupAlarm(getContext(),false));
        _logContext = new ArrayList<>();
        _mainAdapter = new ExerciseLogAdapter(new ListDataProvider<>(_logContext));
        _listLog.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        _listLog.setAdapter(new AlphaInAnimationAdapter(_mainAdapter));
        getParent().addRequireRefreshEvent(_update);
        update();
        return _root;

    }

    @Override
    public void onDestroyView() {
        getParent().removeRequireRefreshEvent(_update);
        super.onDestroyView();
    }

    private void update() {
        _operateButtons.setBackgroundColor(UiHelper.getFlatUiColor(getParent(), getParent().getCurrentSubject().getId()));

        //载入记录
        _logContext.clear();
        try {
            List<ExerciseLog> datas = DbManager.getDefaultHelper(getContext()).getExerciseLogs().queryForAll();
            int index = 1;
            UUID groupUuid = null;
            for (ExerciseLog data :
                    datas) {
                if (data.getGroupGuid().equals(groupUuid)) {
                    index++;
                } else {
                    index = 1;
                    groupUuid = data.getGroupGuid();
                }
                _logContext.add(new ExerciseLogAdapter.DataContext(index,
                        data.getQuestion().getTitle(),
                        data.getQuestion().getUnit() == null ? getString(R.string.emptyUnit) : data.getQuestion().getUnit().getName(),
                        data.getCorrectRatio(),
                        v -> {
                            Intent intent = new Intent(getContext(),QuestionDetailActivity.class);
                            intent.putExtra(QuestionDetailActivity.EXTRA_QUESTION_ID,data.getQuestion().getId());
                            startActivity(intent);
                        }));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public MainActivity getParent() {
        return (MainActivity) getActivity();
    }

}
