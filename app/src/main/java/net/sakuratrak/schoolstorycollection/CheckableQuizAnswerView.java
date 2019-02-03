package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;

import net.sakuratrak.schoolstorycollection.core.Answer;
import net.sakuratrak.schoolstorycollection.core.Answer.PlainTextAnswer;

public abstract class CheckableQuizAnswerView extends QuizAnswerView {

    public CheckableQuizAnswerView(Context context) {
        super(context);
    }

    public CheckableQuizAnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableQuizAnswerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckableQuizAnswerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public abstract PlainTextAnswer getAnswer();

    public abstract void setAnswer(Answer answer);

    public abstract boolean hasAnswer();

}
