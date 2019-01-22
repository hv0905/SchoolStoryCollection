package net.sakuratrak.schoolstorycollection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.sakuratrak.schoolstorycollection.core.IListedDataProvidable;
import net.sakuratrak.schoolstorycollection.core.LearningUnitInfo;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public final class UnitDisplayAdapter extends RecyclerView.Adapter<UnitDisplayAdapter.Holder> {
    private IListedDataProvidable<DataContext> _dataContext;

    public UnitDisplayAdapter(IListedDataProvidable<DataContext> dataContext) {
        _dataContext = dataContext;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_unit, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull Holder viewHolder, int i) {
        DataContext current = _dataContext.get(i);
        viewHolder._valTitle.setText(current.Title);
        viewHolder._valQuizCount.setText(String.valueOf(current.QuizCount));
        viewHolder._valCorrectRatio.setText(String.format(Locale.ENGLISH, "%d%%", current.QuizCorrectRatio));
        viewHolder._correctRatioBar.setProgress(current.QuizCorrectRatio);
        viewHolder._frame.setOnClickListener(current.DetailClicked);
        viewHolder._warningTxt.setVisibility(current.requireMoreRecord ? View.VISIBLE : View.INVISIBLE);
        viewHolder._valQuestionCount.setText(String.valueOf(current.QuestionCount));
        viewHolder._valQuestionRatio.setText(String.format(Locale.ENGLISH, "%d%%", current.QuestionRatio));
        viewHolder._questionRatioBar.setProgress(current.QuestionRatio);
        current.unitMainInfo = viewHolder._unitMainInfo;


        if (current.requireMoreRecord) {
            viewHolder._warningTxt.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            viewHolder._warningTxt.setHeight(0);
        }
    }

    @Override
    public int getItemCount() {
        return _dataContext.count();
    }

    public IListedDataProvidable<DataContext> getDataContext() {
        return _dataContext;
    }

    public void setDataContext(IListedDataProvidable<DataContext> _dataContext) {
        this._dataContext = _dataContext;
        notifyDataSetChanged();
    }

    public static final class Holder extends RecyclerView.ViewHolder {
        private final View _root;
        private final ConstraintLayout _unitMainInfo;
        private final TextView _valTitle;
        private final TextView _valCorrectRatio;
        private final ProgressBar _correctRatioBar;
        private final TextView _valQuizCount;
        private final TextView _warningTxt;
        private final TextView _valQuestionCount;
        private final TextView _valQuestionRatio;
        private final ProgressBar _questionRatioBar;
        private final LinearLayout _frame;


        private Holder(View rootView) {
            super(rootView);
            _root = rootView;
            _valTitle = _root.findViewById(R.id.valTitle);
            _valCorrectRatio = _root.findViewById(R.id.valCorrectRatio);
            _correctRatioBar = _root.findViewById(R.id.correctRatioBar);
            _valQuizCount = _root.findViewById(R.id.valQuizCount);
            _warningTxt = _root.findViewById(R.id.warningTxt);
            _valQuestionCount = _root.findViewById(R.id.valQuestionCount);
            _valQuestionRatio = _root.findViewById(R.id.valQuestionRatio);
            _questionRatioBar = _root.findViewById(R.id.questionRatioBar);
            _frame = _root.findViewById(R.id.frame);
            _unitMainInfo = _root.findViewById(R.id.unitMainInfo);
        }

    }


    public static class DataContext {
        public View.OnClickListener DetailClicked;
        public String Title;
        //public View.OnClickListener RmClicked;
        protected int QuizCount;
        protected int QuizCorrectRatio;
        protected int QuestionCount;
        protected int QuestionRatio;
        protected boolean requireMoreRecord = false;

        public ConstraintLayout unitMainInfo;

        public DataContext(String title, int quizCount, int quizCorrectRatio, int questionCount, int questionRatio, boolean requireMoreRecord) {
            QuizCount = quizCount;
            QuizCorrectRatio = quizCorrectRatio;
            QuestionCount = questionCount;
            QuestionRatio = questionRatio;
            this.requireMoreRecord = requireMoreRecord;
            Title = title;
        }

        public DataContext(View.OnClickListener detailClicked, int quizCount, int quizCorrectRatio, int questionCount, int questionRatio, boolean requireMoreRecord, String title) {
            DetailClicked = detailClicked;
            QuizCount = quizCount;
            QuizCorrectRatio = quizCorrectRatio;
            QuestionCount = questionCount;
            QuestionRatio = questionRatio;
            this.requireMoreRecord = requireMoreRecord;
            Title = title;
        }

        public DataContext() {

        }

        public static DataContext fromDb(LearningUnitInfo dbInfo,int questionSum){
            int questionsc = dbInfo.getQuestions().size();
            return new UnitDisplayAdapter.DataContext(dbInfo.getName(),
                    dbInfo.getExerciseLogCount(),
                    dbInfo.computeCorrectRatio(),
                    questionsc,
                    (int) (((double)questionsc / questionSum) * 100),
                    dbInfo.getIfNeedMoreQuiz());
        }
    }
}
