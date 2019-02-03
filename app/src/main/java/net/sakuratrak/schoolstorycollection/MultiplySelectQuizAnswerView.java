package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.sakuratrak.schoolstorycollection.R.id;
import net.sakuratrak.schoolstorycollection.R.layout;
import net.sakuratrak.schoolstorycollection.core.Answer;
import net.sakuratrak.schoolstorycollection.core.Answer.PlainTextAnswer;
import net.sakuratrak.schoolstorycollection.core.SelectableAnswer;

import androidx.annotation.Nullable;

public class MultiplySelectQuizAnswerView extends CheckableQuizAnswerView {

    ViewGroup _root;
    Chip _a;
    Chip _b;
    Chip _c;
    Chip _d;
    FloatingActionButton _confirm;

    public MultiplySelectQuizAnswerView(Context context) {
        super(context);
        init();
    }

    public MultiplySelectQuizAnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiplySelectQuizAnswerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MultiplySelectQuizAnswerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(layout.element_answer_quiz_multiply_choice, this);
        _root = findViewById(id.rootView);
        _a = findViewById(id.answerA);
        _b = findViewById(id.answerB);
        _c = findViewById(id.answerC);
        _d = findViewById(id.answerD);
        _confirm = findViewById(id.confirmBtn);


        OnClickListener chipClicked = v -> updateEmptyStatus();
        _a.setOnClickListener(chipClicked);
        _b.setOnClickListener(chipClicked);
        _c.setOnClickListener(chipClicked);
        _d.setOnClickListener(chipClicked);

        _confirm.setOnClickListener(v -> onAnswerReport(ANSWER_COMPLETED));

        updateEmptyStatus();

    }

    @Override
    public void updateEmptyStatus() {
        _confirm.setEnabled(hasAnswer());
    }

    @Override
    public boolean hasAnswer() {
        return _a.isChecked() || _b.isChecked() || _c.isChecked() || _d.isChecked();
    }

    @Override
    @Nullable
    public PlainTextAnswer getAnswer() {
        if (!hasAnswer()) return null;
        SelectableAnswer sa = new SelectableAnswer();
        sa.A = _a.isChecked();
        sa.B = _b.isChecked();
        sa.C = _c.isChecked();
        sa.D = _d.isChecked();
        return sa;
    }

    @Override
    public void setAnswer(Answer value) {
        if (value instanceof SelectableAnswer) {
            SelectableAnswer sv = (SelectableAnswer) value;
            _a.setChecked(sv.A);
            _b.setChecked(sv.B);
            _c.setChecked(sv.C);
            _d.setChecked(sv.D);
        } else {
            throw new IllegalArgumentException("value");
        }
    }

}