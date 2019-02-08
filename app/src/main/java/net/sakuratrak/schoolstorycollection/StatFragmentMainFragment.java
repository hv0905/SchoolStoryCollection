package net.sakuratrak.schoolstorycollection;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.ExerciseLogGroup;
import net.sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;
import net.sakuratrak.schoolstorycollection.core.ReviewRatio;
import net.sakuratrak.schoolstorycollection.core.StatHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public final class StatFragmentMainFragment extends Fragment {

    private static final String TAG = "StatFragment_Main";

    //region views
    private ScrollView _root;
    private BarChart _dailyQuizChart;
    private PieChart _difficultyPie;
    private PieChart _questionPie;
    private PieChart _reviewRatioPie;
    //endregion
    private final MainActivity.RequireRefreshEventHandler _requireRefreshEvent = () -> {
        refreshPies();
        if (isVisible()) {
            animateIn();
        }
    };
    //region fields
    private boolean _created;
    //endregion

    //region methods
    public StatFragmentMainFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _created = true;

        _root = (ScrollView) inflater.inflate(R.layout.fragment_stat_fragment_main, container, false);

        _dailyQuizChart = _root.findViewById(R.id.dailyQuizChart);
        _questionPie = _root.findViewById(R.id.questionPie);
        _difficultyPie = _root.findViewById(R.id.difficultyPie);
        _reviewRatioPie = _root.findViewById(R.id.reviewRatioPie);

        UiHelper.applyAppearanceForBar(getContext(), _dailyQuizChart);
        _questionPie.setNoDataText(getString(R.string.quizDailyEmptyNotice));
        _dailyQuizChart.getXAxis().setValueFormatter(new DayOfWeekAxisValveFormatter());

        _questionPie.setCenterText(getString(R.string.StatUnitPie));
        _questionPie.setNoDataText(getString(R.string.StatAddQuestionNotify));
        UiHelper.applyAppearanceForPie(getActivity(), _questionPie);


        _difficultyPie.setCenterText(getString(R.string.StatDifficultyPie));
        _difficultyPie.setNoDataText(getString(R.string.StatAddQuestionNotify));
        UiHelper.applyAppearanceForPie(getActivity(), _difficultyPie);

        _reviewRatioPie.setCenterText(getString(R.string.StatReviewPie));
        _reviewRatioPie.setNoDataText(getString(R.string.StatAddQuestionNotify));
        UiHelper.applyAppearanceForPie(getActivity(),_reviewRatioPie);

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

    private void refreshPies() {
        try {
            //===============
            //Bar 1
            //===============

            int[] last7Days = StatHelper.getLastNDaysQuizCount(new ExerciseLogGroup.DbHelper(getContext()), 7, getParent().getCurrentSubject());
            ArrayList<BarEntry> dailyBarEntry = new ArrayList<>();
            Calendar currentTime = Calendar.getInstance();
            for (int i = 0; i < last7Days.length; i++) {
                BarEntry entry = new BarEntry(i + 1, last7Days[i]);
                dailyBarEntry.add(entry);
            }
            BarDataSet dailyQuizDataSet = new BarDataSet(dailyBarEntry, "");
            UiHelper.applyAppearanceForBarDataSet(getContext(), dailyQuizDataSet);
            BarData dqd = new BarData(dailyQuizDataSet);
            _dailyQuizChart.setData(dqd);

            //===============
            //Pie 2
            //===============

            List<LearningUnitInfo> units = new LearningUnitInfo.DbHelper(
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


            //==============
            //Pie 1+3
            //==============

            List<QuestionInfo> questions = new QuestionInfo.DbHelper(DbManager.getDefaultHelper(getActivity()).
                    getQuestionInfos()).findAllWithSubject(getParent().getCurrentSubject());

            int[] difficultyCounts = new int[QuestionInfo.DIFFICULTY_MAX + 1];
            int[] reviewRatioCounts = new int[4];

            for (QuestionInfo item : questions) {
                difficultyCounts[item.getDifficulty()]++;
                reviewRatioCounts[ReviewRatio.getByRatio(item.computeReviewValue()).getId()]++;
            }

            ArrayList<PieEntry> difficultyPieEntry = new ArrayList<>();

            for (int i = 0; i < difficultyCounts.length; i++) {
                if (difficultyCounts[i] == 0) continue;
                difficultyPieEntry.add(new PieEntry(difficultyCounts[i], String.format(Locale.ENGLISH, "%.1f★", (i) / 2f)));
            }
            ArrayList<PieEntry> reviewRatioPieEntry = new ArrayList<>();

            boolean hasData = false;
            for (int i = 0; i < reviewRatioCounts.length; i++) {
                if (reviewRatioCounts[i] != 0) hasData = true;
                reviewRatioPieEntry.add(new PieEntry(reviewRatioCounts[i], reviewRatioCounts[i] == 0 ? "" : getString(ReviewRatio.fromId(i).getStr())));
            }

            if (difficultyPieEntry.size() != 0) {
                PieDataSet difficultyPieDataSet = new PieDataSet(difficultyPieEntry, getString(R.string.difficulty));
                UiHelper.applyAppearanceForPieDataSet(getActivity(), difficultyPieDataSet, false);
                _difficultyPie.setData(new PieData(difficultyPieDataSet));
            } else {
                _difficultyPie.setData(null);
            }

            if (hasData) {
                PieDataSet reviewRatioPieDataSet = new PieDataSet(reviewRatioPieEntry, getString(R.string.reviewRatio));
                UiHelper.applyAppearanceForPieDataSet(getActivity(), reviewRatioPieDataSet, false);
                reviewRatioPieDataSet.setColors(
                        getResources().getColor(R.color.flat5),
                        getResources().getColor(R.color.flat7),
                        getResources().getColor(R.color.flat8),
                        getResources().getColor(R.color.black));
                _reviewRatioPie.setData(new PieData(reviewRatioPieDataSet));
            } else {
                _reviewRatioPie.setData(null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private MainActivity getParent() {
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

    private void animateIn() {
        if (!isAdded()) return;
        _dailyQuizChart.animateY(1000, Easing.EaseInOutQuad);
        _questionPie.animateY(1000, Easing.EaseInOutQuad);
        _difficultyPie.animateY(1000, Easing.EaseInOutQuad);
        _reviewRatioPie.animateY(1000,Easing.EaseInOutQuad);
    }
    //endregion


}