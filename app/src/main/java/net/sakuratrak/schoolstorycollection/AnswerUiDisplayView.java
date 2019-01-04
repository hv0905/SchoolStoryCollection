package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import net.sakuratrak.schoolstorycollection.core.Answer;

public abstract class AnswerUiDisplayView extends FrameLayout {
    public AnswerUiDisplayView(@NonNull Context context) {
        super(context);
    }

    public AnswerUiDisplayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnswerUiDisplayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AnswerUiDisplayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public abstract void setAnswer(Answer answer);
}
