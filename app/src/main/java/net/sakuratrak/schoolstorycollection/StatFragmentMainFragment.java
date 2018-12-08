package net.sakuratrak.schoolstorycollection;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class StatFragmentMainFragment extends Fragment {

    public static final String TAG = "StatFragment_Main";

    boolean _created;
    ScrollView _root;
    PieChart _questionPie;
    PieChart _difficultyPie;

    public StatFragmentMainFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _created = true;

        _root = (ScrollView) inflater.inflate(R.layout.fragment_stat_fragment_main, container, false);
        _questionPie = _root.findViewById(R.id.questionPie);
        _difficultyPie = _root.findViewById(R.id.difficultyPie);

        _questionPie.setCenterText("错题的单元分布");
        _questionPie.setNoDataText("加入些错题再来看吧~");
        applyAppearanceForPie(_questionPie);



        _difficultyPie.setCenterText("错题的难度分布");
        _difficultyPie.setNoDataText("加入些错题再来看吧~");
        applyAppearanceForPie(_difficultyPie);

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

        getParent().addSubjectUpdateEvent(subject -> {
            refreshPies();
            if (isVisible()) {
                animateIn();
            }
        });

        refreshPies();
        return _root;
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
            applyAppearanceForDataSet(questionPieDataSet);
            PieData questionPieData = new PieData(questionPieDataSet);
            questionPieData.setValueFormatter(new PercentFormatter());

            _questionPie.setData(questionPieData);


            List<QuestionInfo> questions = new QuestionInfo.QuestionInfoDaoManager(DbManager.getDefaultHelper(getActivity()).
                    getQuestionInfos()).FindAllWithSubject(getParent().getCurrentSubject());


            int[] difficultyCounts = new int[QuestionInfo.DIFFICULTY_MAX];

            for (QuestionInfo item : questions) {
                difficultyCounts[item.getDifficulty() - 1]++;
            }

            ArrayList<PieEntry> difficyltyPieEntry = new ArrayList<>();

            for (int i = 0; i < difficultyCounts.length; i++) {
                if (difficultyCounts[i] == 0) continue;
                difficyltyPieEntry.add(new PieEntry(difficultyCounts[i], String.format("%.1f★", (i + 1) / 2f)));
            }

            PieDataSet difficultyPieDataSet = new PieDataSet(difficyltyPieEntry, getString(R.string.difficulty));
            applyAppearanceForDataSet(difficultyPieDataSet);

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

    void applyAppearanceForDataSet(PieDataSet dataSet) {
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setColors(
                getResources().getColor(R.color.flat1),
                getResources().getColor(R.color.flat2),
                getResources().getColor(R.color.flat3),
                getResources().getColor(R.color.flat4),
                getResources().getColor(R.color.flat5),
                getResources().getColor(R.color.flat6),
                getResources().getColor(R.color.flat7),
                getResources().getColor(R.color.flat8)
                );
        //dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setValueLinePart1OffsetPercentage(80f);//数据连接线距图形片内部边界的距离，为百分数
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setValueLineColor(Color.BLACK);//设置连接线的颜色
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setSliceSpace(3f);//设置饼块之间的间隔
        dataSet.setSelectionShift(5f);//设置饼块选中时偏离饼图中心的距离

    }

    void applyAppearanceForPie(PieChart pie){
        pie.setCenterTextSize(16);
        pie.setCenterTextColor(getResources().getColor(R.color.colorAccent));
        pie.getLegend().setEnabled(false);
        Description dsc = new Description();
        dsc.setText("");
        pie.setDescription(dsc);
    }
}