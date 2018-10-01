package sakuratrak.schoolstorycollection;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public final class UnitDisplayAdapter extends RecyclerView.Adapter<UnitDisplayAdapter.Holder> {
    public ArrayList<UnitDisplayInfo> _mDataSet;

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Holder hd = new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_questionlistdisplay,parent,false));
        return hd;

    }

    @Override
    public void onBindViewHolder(@NonNull Holder viewHolder, int i) {
        UnitDisplayInfo udi = _mDataSet.get(i);
        viewHolder._valTitle.setText(udi.Title);
        viewHolder._valQuizCount.setText(String.valueOf(udi.QuizCount));
        viewHolder._valCurrectRatio.setText(String.valueOf(udi.QuizCurrectRatio) + "%");
        viewHolder._currectRatioBar.setProgress(udi.QuizCurrectRatio);
        viewHolder._rmBtn.setOnClickListener(udi.RmClicked);
        viewHolder._resetBtn.setOnClickListener(udi.ResetClicked);
    }

    @Override
    public int getItemCount() {
        return _mDataSet.size();
    }

    public UnitDisplayAdapter(ArrayList<UnitDisplayInfo> mDataSet){
        _mDataSet = mDataSet;
    }

    public static class Holder extends RecyclerView.ViewHolder
    {
        private View _root;
        public TextView _valTitle;
        public Button _resetBtn;
        public Button _rmBtn;
        public TextView _valCurrectRatio;
        public ProgressBar _currectRatioBar;
        public TextView _valQuizCount;


        public Holder(View rootView){
            super(rootView);
            _root = rootView;
            _valTitle = _root.findViewById(R.id.valTitle);
            _resetBtn = _root.findViewById(R.id.resetBtn);
            _rmBtn = _root.findViewById(R.id.rmBtn);
            _valCurrectRatio = _root.findViewById(R.id.valCurrectRatio);
            _currectRatioBar = _root.findViewById(R.id.currectRatioBar);
            _valQuizCount = _root.findViewById(R.id.valQuizCount);
        }

    }


    public static class UnitDisplayInfo{
        public View.OnClickListener ResetClicked;
        public View.OnClickListener RmClicked;
        public int QuizCount;
        public int QuizCurrectRatio;
        public String Title;

        public UnitDisplayInfo(int quizCount, int quizCurrectRatio, String title) {
            QuizCount = quizCount;
            QuizCurrectRatio = quizCurrectRatio;
            Title = title;
        }

        public UnitDisplayInfo(View.OnClickListener _resetClicked, View.OnClickListener _rmClicked, int quizCount, int quizCurrectRatio, String title) {
            this.ResetClicked = _resetClicked;
            this.RmClicked = _rmClicked;
            QuizCount = quizCount;
            QuizCurrectRatio = quizCurrectRatio;
            Title = title;
        }

        public UnitDisplayInfo(){

        }
    }
}
