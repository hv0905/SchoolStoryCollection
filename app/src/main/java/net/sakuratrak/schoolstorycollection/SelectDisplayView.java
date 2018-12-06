package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import net.sakuratrak.schoolstorycollection.core.Answer;

public final class SelectDisplayView extends AnswerUiDisplayView {
    public SelectDisplayView(@NonNull Context context) {
        super(context);
        init();

    }

    public SelectDisplayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public SelectDisplayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public SelectDisplayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public void setAnswer(Answer answer) {

    }

    private void init() {

    }
}
