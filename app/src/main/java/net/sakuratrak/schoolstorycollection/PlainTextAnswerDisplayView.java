package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import net.sakuratrak.schoolstorycollection.R.id;
import net.sakuratrak.schoolstorycollection.R.layout;
import net.sakuratrak.schoolstorycollection.core.Answer;
import net.sakuratrak.schoolstorycollection.core.Answer.PlainTextAnswer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class PlainTextAnswerDisplayView extends AnswerUiDisplayView {

    TextView _root;


    public PlainTextAnswerDisplayView(@NonNull Context context) {
        super(context);
        init();

    }

    public PlainTextAnswerDisplayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public PlainTextAnswerDisplayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public PlainTextAnswerDisplayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    void init() {
        LayoutInflater.from(getContext()).inflate(layout.element_answer_display_text, this);
        _root = findViewById(id.imgs);
    }

    @Override
    public void setAnswer(Answer value) {
        if (value instanceof PlainTextAnswer)
            _root.setText(value.toString());
        else throw new IllegalArgumentException("value");
    }
}
