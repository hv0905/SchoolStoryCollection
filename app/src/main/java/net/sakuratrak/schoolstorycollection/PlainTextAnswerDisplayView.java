package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import net.sakuratrak.schoolstorycollection.core.Answer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class PlainTextAnswerDisplayView extends AnswerUiDisplayView {

    private TextView _root;


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

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.element_answer_display_text, this);
        _root = findViewById(R.id.imgs);
    }

    @Override
    public void setAnswer(Answer value) {
        if (value instanceof Answer.PlainTextAnswer)
            _root.setText(value.toString());
        else throw new IllegalArgumentException("value");
    }
}
