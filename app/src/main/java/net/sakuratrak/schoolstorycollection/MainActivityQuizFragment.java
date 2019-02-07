package net.sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;
import net.sakuratrak.schoolstorycollection.core.ExerciseLog;
import net.sakuratrak.schoolstorycollection.core.ExerciseLogGroup;
import net.sakuratrak.schoolstorycollection.core.ListDataProvider;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;
import net.sakuratrak.schoolstorycollection.core.QuizHelper;

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

    private static final int REQUEST_QUIZ = 100;
    private static final int REQUEST_DETAIL = 101;


    //region views
    private ViewGroup _root;
    private ConstraintLayout _operateButtons;
    private View _quickQuizButton;
    private View _randomQuizButton;
    private RecyclerView _listLog;
    //endregion

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
        _randomQuizButton = _root.findViewById(R.id.randomQuizBtn);
        _listLog = _root.findViewById(R.id.listLog);

        _quickQuizButton.setOnClickListener(v -> {
            try {
                List<QuestionInfo> quizContext = QuizHelper.prepareSmartQuiz(
                        new QuestionInfo.DbHelper(getContext()).findAllWithSubject(getParent().getCurrentSubject()),
                        AppSettingsMaster.getQuizSize(getContext())
                );
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
                intent.putExtra(QuizActivity.EXTRA_QUIZ_DESCRIPTION, "快速小测");
                startActivityForResult(intent, REQUEST_QUIZ);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        });

        _randomQuizButton.setOnClickListener(v -> {
            try {
                List<QuestionInfo> quizContext = QuizHelper.prepareRandomQuiz(
                        new QuestionInfo.DbHelper(getContext()).findAllWithSubject(getParent().getCurrentSubject()),
                        AppSettingsMaster.getQuizSize(getContext())
                );
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
                intent.putExtra(QuizActivity.EXTRA_QUIZ_DESCRIPTION, "快速小测");
                startActivityForResult(intent, REQUEST_QUIZ);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        //_buttonTest.setOnClickListener(v -> AlarmReceiver.setupAlarm(getContext(),false));
        _logContext = new ArrayList<>();
        _mainAdapter = new ExerciseLogGroupAdapter(new ListDataProvider<>(_logContext));
        _listLog.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        //noinspection ConstantConditions
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

            List<ExerciseLogGroup> datas = new ExerciseLogGroup.DbHelper(getContext()).findAllWithSubject(getParent().getCurrentSubject());

            for (int i = datas.size() - 1; i >= 0; i--) {
                ExerciseLogGroup data = datas.get(i);
                //        public DataContext(String title, String happenTime, int questionCount, int score, View.OnClickListener onClick) {
                //noinspection ConstantConditions
                _logContext.add(new ExerciseLogGroupAdapter.DataContext(
                        data.getDescription(),
                        UiHelper.defaultFormatWithTime.format(data.getHappendTime()),
                        data.getLogs().size(),
                        data.getAvgScore(),
                        v -> viewLog(data),
                        v -> {
                            new AlertDialog.Builder(getContext()).setItems(R.array.quiz_log_operate, (dialog, which) -> {
                                switch (which) {
                                    case 0://view
                                        viewLog(data);
                                        break;
                                    case 1://retry
                                        viewRetry(data);
                                        break;
                                }
                            }).setNegativeButton(R.string.cancel, null).show();
                            return true;
                        }));
            }

            _mainAdapter.notifyDataSetChanged();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private MainActivity getParent() {
        return (MainActivity) getActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_QUIZ:
                if (resultCode != QuizActivity.RESULT_NONE_DONE) {
                    //要更新记录
                    getParent().requireRefresh();
                }
            case REQUEST_DETAIL:
                if (resultCode == QuizResultActivity.RESULT_REQUIZ) {
                    getParent().requireRefresh();
                }
        }
    }

    private void viewLog(ExerciseLogGroup data) {
        Intent intent = new Intent(getContext(), QuizResultActivity.class);
        intent.putExtra(QuizResultActivity.EXTRA_GROUP_ID, data.getId());
        startActivityForResult(intent, REQUEST_DETAIL);
    }

    private void viewRetry(ExerciseLogGroup data) {
        Intent intent = new Intent(getContext(), QuizActivity.class);
        intent.putExtra(QuizActivity.EXTRA_MODE, QuizActivity.MODE_LIST);
        intent.putExtra(QuizActivity.EXTRA_QUIZ_DESCRIPTION, "重测：" + data.getDescription());
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
        intent.putIntegerArrayListExtra(QuizActivity.EXTRA_QUESTION_IDS, arrayList);
        startActivityForResult(intent, REQUEST_QUIZ);
    }
}
