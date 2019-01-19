package net.sakuratrak.schoolstorycollection;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.ExerciseLog;
import net.sakuratrak.schoolstorycollection.core.ExerciseLogGroup;
import net.sakuratrak.schoolstorycollection.core.ListDataProvider;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public final class MainActivityQuizFragment extends Fragment {

    public static final String TAG = "MainWindow_Quiz";

    public static final int REQUEST_QUIZ = 100;


    ViewGroup _root;
    private ConstraintLayout _operateButtons;
    private View _quickQuizButton;
    private View _unitQuizButton;
    private RecyclerView _listLog;
    private ExerciseLogGroupAdapter _mainAdapter;
    private ArrayList<ExerciseLogGroupAdapter.DataContext> _logContext;
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
            intent.putExtra(QuizActivity.EXTRA_QUIZ_DESCRIPTION, "快速小测");
            startActivityForResult(intent,REQUEST_QUIZ);

        });

        _unitQuizButton.setOnClickListener(v -> {

        });

        //_buttonTest.setOnClickListener(v -> AlarmReceiver.setupAlarm(getContext(),false));
        _logContext = new ArrayList<>();
        _mainAdapter = new ExerciseLogGroupAdapter(new ListDataProvider<>(_logContext));
        _listLog.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        _listLog.addItemDecoration(new RecycleViewDivider(RecyclerView.VERTICAL, getContext()));
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
//            List<ExerciseLog> datas = DbManager.getDefaultHelper(getContext()).getExerciseLogs().queryForAll();
//            int index = 1;
//            UUID groupUuid = null;
//            for (ExerciseLog data :
//                    datas) {
//                if (data.getGroupGuid().equals(groupUuid)) {
//                    index++;
//                } else {
//                    index = 1;
//                    groupUuid = data.getGroupGuid();
//                }
//                _logContext.add(new ExerciseLogAdapter.DataContext(index,
//                        data.getQuestion().getTitle(),
//                        data.getQuestion().getUnit() == null ? getString(R.string.emptyUnit) : data.getQuestion().getUnit().getName(),
//                        data.getCorrectRatio(),
//                        v -> {
//                            Intent intent = new Intent(getContext(),QuestionDetailActivity.class);
//                            intent.putExtra(QuestionDetailActivity.EXTRA_QUESTION_ID,data.getQuestion().getId());
//                            startActivity(intent);
//                        }));
//            }

            List<ExerciseLogGroup> datas = DbManager.getDefaultHelper(getContext()).getExerciseLogGroups().queryForAll();

            for (ExerciseLogGroup data : datas) {
                //        public DataContext(String title, String happenTime, int questionCount, int score, View.OnClickListener onClick) {
                _logContext.add(new ExerciseLogGroupAdapter.DataContext(
                        data.getDescription(),
                        data.getHappendTime().toString(),
                        data.getLogs().size(),
                        data.getAvgScore(),
                        v -> {
                            new AlertDialog.Builder(getContext()).setItems(R.array.quiz_log_operate, (dialog, which) -> {
                                switch (which) {
                                    case 0://view
                                        Intent intent = new Intent(getContext(), QuizResultActivity.class);
                                        intent.putExtra(QuizResultActivity.EXTRA_GROUP_ID, data.getId());
                                        startActivity(intent);
                                        break;
                                    case 1://retry
                                        Intent intent1 = new Intent(getContext(), QuizActivity.class);
                                        intent1.putExtra(QuizActivity.EXTRA_MODE, QuizActivity.MODE_LIST);
                                        intent1.putExtra(QuizActivity.EXTRA_QUIZ_DESCRIPTION, "重测：" + data.getDescription());
                                        ArrayList<Integer> arrayList = new ArrayList<>();
                                        for (ExerciseLog log :
                                                data.getLogs()) {
                                            if (log.getQuestion() != null)
                                                arrayList.add(log.getQuestion().getId());
                                        }
                                        if (arrayList.size() == 0) {
                                            Toast.makeText(getContext(), "无法开始，小测中的所有题目已被删除", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        intent1.putIntegerArrayListExtra(QuizActivity.EXTRA_QUESTION_IDS, arrayList);
                                        startActivityForResult(intent1,REQUEST_QUIZ);
                                        break;
                                }
                            }).setNegativeButton(R.string.cancel, null).show();

                        }));
            }

            _mainAdapter.notifyDataSetChanged();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public MainActivity getParent() {
        return (MainActivity) getActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_QUIZ:
                if(resultCode != QuizActivity.RESULT_NONE_DONE){
                    //要更新记录
                    getParent().requireRefresh();
                }
        }
    }
}
