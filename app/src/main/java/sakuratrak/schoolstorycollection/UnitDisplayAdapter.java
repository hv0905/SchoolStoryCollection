package sakuratrak.schoolstorycollection;

import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public final class UnitDisplayAdapter extends RecyclerView.Adapter<UnitDisplayAdapter.Holder> {
    private ArrayList<UnitDisplayInfo> _mDataSet;

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_unitlistdisplay,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull Holder viewHolder, int i) {
        UnitDisplayInfo udi = _mDataSet.get(i);
        viewHolder._valTitle.setText(udi.Title);
        viewHolder._valQuizCount.setText(String.valueOf(udi.QuizCount));
        viewHolder._valCurrectRatio.setText(String.format(Locale.ENGLISH,"%d%%",udi.QuizCorrectRatio));
        viewHolder._currectRatioBar.setProgress(udi.QuizCorrectRatio);
        viewHolder._rmBtn.setOnClickListener(udi.RmClicked);
        viewHolder._resetBtn.setOnClickListener(udi.ResetClicked);
        viewHolder._warningTxt.setVisibility(udi.requireMoreRecord ? View.VISIBLE : View.INVISIBLE);
        viewHolder._valQuestionCount.setText(String.valueOf(udi.QuestionCount));
        viewHolder._valQuestionRatio.setText(String.format(Locale.ENGLISH,"%d%%",udi.QuestionRatio));
        viewHolder._questionRatioBar.setProgress(udi.QuestionRatio);


        if(udi.requireMoreRecord){
            viewHolder._warningTxt.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }else{
            viewHolder._warningTxt.setHeight(0);
        }
    }

    @Override
    public int getItemCount() {
        return _mDataSet.size();
    }

    public UnitDisplayAdapter(ArrayList<UnitDisplayInfo> mDataSet){
        _mDataSet = mDataSet;
    }

    public static final class Holder extends RecyclerView.ViewHolder
    {
        private View _root;
        private TextView _valTitle;
        private MaterialButton _resetBtn;
        private MaterialButton _rmBtn;
        private TextView _valCurrectRatio;
        private ProgressBar _currectRatioBar;
        private TextView _valQuizCount;
        private TextView _warningTxt;
        private TextView _valQuestionCount;
        private TextView _valQuestionRatio;
        private ProgressBar _questionRatioBar;


        private Holder(View rootView){
            super(rootView);
            _root = rootView;
            _valTitle = _root.findViewById(R.id.valTitle);
            _resetBtn = _root.findViewById(R.id.resetBtn);
            _rmBtn = _root.findViewById(R.id.rmBtn);
            _valCurrectRatio = _root.findViewById(R.id.valCorrectRatio);
            _currectRatioBar = _root.findViewById(R.id.correctRatioBar);
            _valQuizCount = _root.findViewById(R.id.valQuizCount);
            _warningTxt = _root.findViewById(R.id.warningTxt);
            _valQuestionCount = _root.findViewById(R.id.valQuestionCount);
            _valQuestionRatio = _root.findViewById(R.id.valQuestionRatio);
            _questionRatioBar = _root.findViewById(R.id.questionRatioBar);
        }

    }


    public static class UnitDisplayInfo{
        public View.OnClickListener ResetClicked;
        public View.OnClickListener RmClicked;
        public int QuizCount;
        public int QuizCorrectRatio;
        public int QuestionCount;
        public int QuestionRatio;
        public boolean requireMoreRecord = false;
        public String Title;

        public UnitDisplayInfo(String title, int quizCount, int quizCorrectRatio, int questionCount, int questionRatio, boolean requireMoreRecord) {
            QuizCount = quizCount;
            QuizCorrectRatio = quizCorrectRatio;
            QuestionCount = questionCount;
            QuestionRatio = questionRatio;
            this.requireMoreRecord = requireMoreRecord;
            Title = title;
        }

        public UnitDisplayInfo(View.OnClickListener resetClicked, View.OnClickListener rmClicked, int quizCount, int quizCorrectRatio, int questionCount, int questionRatio, boolean requireMoreRecord, String title) {
            ResetClicked = resetClicked;
            RmClicked = rmClicked;
            QuizCount = quizCount;
            QuizCorrectRatio = quizCorrectRatio;
            QuestionCount = questionCount;
            QuestionRatio = questionRatio;
            this.requireMoreRecord = requireMoreRecord;
            Title = title;
        }

        public UnitDisplayInfo(){

        }
    }
}
