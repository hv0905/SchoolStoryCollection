package net.sakuratrak.schoolstorycollection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.sakuratrak.schoolstorycollection.core.IListedDataProvidable;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public final class UnitDisplayAdapter extends RecyclerView.Adapter<UnitDisplayAdapter.Holder> {
    private IListedDataProvidable<DataContext> _dataContext;

    public UnitDisplayAdapter(IListedDataProvidable<DataContext> mDataSet) {
        _dataContext = mDataSet;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_unit, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull Holder viewHolder, int i) {
        DataContext udi = _dataContext.get(i);
        viewHolder._valTitle.setText(udi.Title);
        viewHolder._valQuizCount.setText(String.valueOf(udi.QuizCount));
        viewHolder._valCorrectRatio.setText(String.format(Locale.ENGLISH, "%d%%", udi.QuizCorrectRatio));
        viewHolder._correctRatioBar.setProgress(udi.QuizCorrectRatio);
        viewHolder._frame.setOnClickListener(udi.DetailClicked);
        viewHolder._warningTxt.setVisibility(udi.requireMoreRecord ? View.VISIBLE : View.INVISIBLE);
        viewHolder._valQuestionCount.setText(String.valueOf(udi.QuestionCount));
        viewHolder._valQuestionRatio.setText(String.format(Locale.ENGLISH, "%d%%", udi.QuestionRatio));
        viewHolder._questionRatioBar.setProgress(udi.QuestionRatio);


        if (udi.requireMoreRecord) {
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

        private final TextView _valTitle;
        private final TextView _valCorrectRatio;
        private final ProgressBar _correctRatioBar;
        private final TextView _valQuizCount;
        private final TextView _warningTxt;
        private final TextView _valQuestionCount;
        private final TextView _valQuestionRatio;
        private final ProgressBar _questionRatioBar;
        private final FrameLayout _frame;


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
    }
}
