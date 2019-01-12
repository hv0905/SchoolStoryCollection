package net.sakuratrak.schoolstorycollection;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public final class StatFragmentMainFragment extends Fragment {

    public static final String TAG = "StatFragment_Main";

    boolean _created;
    ScrollView _root;
    PieChart _questionPie;
    PieChart _difficultyPie;
    final MainActivity.RequireRefreshEventHandler _requireRefreshEvent = () -> {
        refreshPies();
        if (isVisible()) {
            animateIn();
        }
    };

    public StatFragmentMainFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _created = true;

        _root = (ScrollView) inflater.inflate(R.layout.fragment_stat_fragment_main, container, false);
        _questionPie = _root.findViewById(R.id.questionPie);
        _difficultyPie = _root.findViewById(R.id.difficultyPie);

        _questionPie.setCenterText(getString(R.string.StatUnitPie));
        _questionPie.setNoDataText(getString(R.string.StatAddQuestionNotify));
        UiHelper.applyAppearanceForPie(getActivity(), _questionPie);


        _difficultyPie.setCenterText(getString(R.string.StatDifficultyPie));
        _difficultyPie.setNoDataText(getString(R.string.StatAddQuestionNotify));
        UiHelper.applyAppearanceForPie(getActivity(), _difficultyPie);

        _questionPie.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: " + e.toString());
                Log.d(TAG, "onValueSelected: " + h.toString());
            }

            @Override
            public void onNothingSelected() {

            }
        });


        getParent().addRequireRefreshEvent(_requireRefreshEvent);
        refreshPies();
        return _root;
    }

    @Override
    public void onDestroyView() {
        getParent().removeRequireRefreshEvent(_requireRefreshEvent);
        super.onDestroyView();
    }

    public void refreshPies() {
        try {
            List<LearningUnitInfo> units = new LearningUnitInfo.LearningUnitInfoDaoHelper(
                    DbManager.getDefaultHelper(getActivity()).getLearningUnitInfos())
                    .findBySubject(getParent().getCurrentSubject());

            ArrayList<PieEntry> questionPieEntry = new ArrayList<>();
            for (LearningUnitInfo item : units) {
                int size = item.getQuestions().size();
                if (size <= 0) continue;
                questionPieEntry.add(new PieEntry(size, item.getName()));
            }
            PieDataSet questionPieDataSet = new PieDataSet(questionPieEntry, getString(R.string.unit));
            UiHelper.applyAppearanceForPieDataSet(getParent(), questionPieDataSet);
            PieData questionPieData = new PieData(questionPieDataSet);
            _questionPie.setData(questionPieData);


            List<QuestionInfo> questions = new QuestionInfo.QuestionInfoDaoManager(DbManager.getDefaultHelper(getActivity()).
                    getQuestionInfos()).FindAllWithSubject(getParent().getCurrentSubject());


            int[] difficultyCounts = new int[QuestionInfo.DIFFICULTY_MAX];

            for (QuestionInfo item : questions) {
                difficultyCounts[item.getDifficulty() - 1]++;
            }

            ArrayList<PieEntry> difficultyPieEntry = new ArrayList<>();

            for (int i = 0; i < difficultyCounts.length; i++) {
                if (difficultyCounts[i] == 0) continue;
                difficultyPieEntry.add(new PieEntry(difficultyCounts[i], String.format(Locale.ENGLISH, "%.1f★", (i + 1) / 2f)));
            }

            PieDataSet difficultyPieDataSet = new PieDataSet(difficultyPieEntry, getString(R.string.difficulty));
            UiHelper.applyAppearanceForPieDataSet(getParent(), difficultyPieDataSet);

            _difficultyPie.setData(new PieData(difficultyPieDataSet));


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public MainActivity getParent() {
        return (MainActivity) getActivity();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, "setUserVisibleHint: " + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
        userVisibleChangeEventHandler(isVisibleToUser);
    }

    public void userVisibleChangeEventHandler(boolean isVisible) {
        if (isVisible) {
            //just show
            animateIn();
        }
    }

    public void animateIn() {
        if (!isAdded()) return;
        _questionPie.animateY(1000, Easing.EaseInOutQuad);
        _difficultyPie.animateY(1000, Easing.EaseInOutQuad);
    }


}