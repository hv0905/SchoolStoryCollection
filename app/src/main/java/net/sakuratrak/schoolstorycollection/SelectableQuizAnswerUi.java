package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;

import net.sakuratrak.schoolstorycollection.core.Answer;

public class SelectableQuizAnswerUi extends QuizAnswerUi {

    public SelectableQuizAnswerUi(Context context) {
        super(context);
        init();
    }

    public SelectableQuizAnswerUi(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectableQuizAnswerUi(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SelectableQuizAnswerUi(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public void setAnswer(Answer answer) {

    }

    @Override
    public Answer getAnswer() {
        return null;
    }

    public void init(){
    }

}