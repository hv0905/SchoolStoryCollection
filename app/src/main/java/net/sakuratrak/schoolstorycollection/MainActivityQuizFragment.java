package net.sakuratrak.schoolstorycollection;


import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.sakuratrak.schoolstorycollection.core.AppMaster;
import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class MainActivityQuizFragment extends Fragment {

    public static final String TAG = "MainWindow_Quiz";

    ViewGroup _root;
    ConstraintLayout _operateButtons;
    View _quickQuizButton;
    View _unitQuizButton;

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
        //_buttonTest = _root.findViewById(R.id.buttonTest);

        _quickQuizButton.setOnClickListener(v -> {
            //get a lot of questions
            ArrayList<Integer> ids = new ArrayList<>();
            List<QuestionInfo> infos = null;
            try {
                infos = DbManager.getDefaultHelper(getContext()).getQuestionInfos().queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for (QuestionInfo info :
                    infos) {
                ids.add(info.getId());
            }
            Intent intent = new Intent(getActivity(),QuizActivity.class);
            intent.putIntegerArrayListExtra(QuizActivity.EXTRA_QUESTION_IDS,ids);
            intent.putExtra(QuizActivity.EXTRA_MODE,QuizActivity.MODE_LIST);
            startActivity(intent);

        });

        _unitQuizButton.setOnClickListener(v -> {

        });

        //_buttonTest.setOnClickListener(v -> AlarmReceiver.setupAlarm(getContext(),false));

        getParent().addSubjectUpdateEvent(this::update);
        update();
        return _root;

    }

    public void update(){
        _operateButtons.setBackgroundColor(UiHelper.getFlatUiColor(getParent(),getParent().getCurrentSubject().getId()));
    }


    public MainActivity getParent(){
        return (MainActivity) getActivity();
    }

}
