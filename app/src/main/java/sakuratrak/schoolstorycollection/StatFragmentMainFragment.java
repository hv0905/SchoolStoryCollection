package sakuratrak.schoolstorycollection;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import sakuratrak.schoolstorycollection.core.QuestionInfo;

public final class StatFragmentMainFragment extends Fragment {

    public static final String TAG = "StatFragment_Main";

    boolean _created;
    LinearLayout _root;
    PieChart _questionPie;

    public StatFragmentMainFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _created = true;

        _root = (LinearLayout) inflater.inflate(R.layout.fragment_stat_fragment_main,container,false);
        _questionPie = _root.findViewById(R.id.questionPie);

        _questionPie.setCenterText("错题的单元分布情况");

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


        refreshPies();
        return _root;
    }


    public void refreshPies(){
        try {
            List<LearningUnitInfo> questions = new LearningUnitInfo.LearningUnitInfoDaoHelper(
                    DbManager.getDefaultHelper(getActivity()).getLearningUnitInfos())
                    .findBySubject(getParent().getCurrentSubject());

            ArrayList<PieEntry> questionEntry = new ArrayList<>();
            for (LearningUnitInfo item : questions){
                int size = item.getQuestions().size();
                if(size <= 0) continue;
                questionEntry.add(new PieEntry(size,item.getName()));
            }
            PieDataSet dataSet = new PieDataSet(questionEntry,"单元");
            dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            _questionPie.setData(new PieData(dataSet));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public MainActivity getParent(){
        return (MainActivity)getActivity();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, "setUserVisibleHint: " + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
        userVisibleChangeEventHandler(isVisibleToUser);
    }

    public void userVisibleChangeEventHandler(boolean isVisible){
        if(isVisible){
            //just show
            animateIn();
        }
    }

    public void animateIn(){
        if(!isAdded()) return;
            _questionPie.animateY(500);
    }
}