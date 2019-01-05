package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public abstract class QuizAnswerView extends FrameLayout {

    public static final int ANSWER_COMPLETED = 0;
    public static final int ANSWER_SKIPPED = 1;

    public AnswerReportListener onAnswerReport;

    public QuizAnswerView(Context context) {
        super(context);
    }

    public QuizAnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuizAnswerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public QuizAnswerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AnswerReportListener getOnAnswerReport() {
        return onAnswerReport;
    }

    public void setOnAnswerReport(AnswerReportListener onAnswerReport) {
        this.onAnswerReport = onAnswerReport;
    }


    public void onAnswerReport(int status) {
        if (onAnswerReport != null) {
            onAnswerReport.done(this, status);
        }
    }

    public void updateEmptyStatus() {

    }

    public interface AnswerReportListener {
        void done(QuizAnswerView sender, int status);
    }


}