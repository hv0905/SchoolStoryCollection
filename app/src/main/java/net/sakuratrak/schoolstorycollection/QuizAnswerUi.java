package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import net.sakuratrak.schoolstorycollection.core.Answer;

public abstract class QuizAnswerUi extends FrameLayout {

    public static final int ANSWER_COMPLETED = 0;
    public static final int ANSWER_SKIPPED = 1;

    public AnswerReportListener onAnswerReport;

    public QuizAnswerUi(Context context) {
        super(context);
    }

    public QuizAnswerUi(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuizAnswerUi(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public QuizAnswerUi(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AnswerReportListener getOnAnswerReport() {
        return onAnswerReport;
    }

    public void setOnAnswerReport(AnswerReportListener onAnswerReport) {
        this.onAnswerReport = onAnswerReport;
    }

    public abstract void setAnswer(Answer answer);

    public abstract Answer getAnswer();

    public void onAnswerReport(int status){
        if(onAnswerReport != null){
            onAnswerReport.done(this,status);
        }
    }

    public interface AnswerReportListener {
        public void done(QuizAnswerUi sender,int status);
    }


}