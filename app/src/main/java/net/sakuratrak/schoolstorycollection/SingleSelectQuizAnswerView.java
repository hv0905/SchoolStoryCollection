package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.sakuratrak.schoolstorycollection.core.Answer;
import net.sakuratrak.schoolstorycollection.core.SelectableAnswer;

import androidx.annotation.Nullable;

public class SingleSelectQuizAnswerView extends CheckableQuizAnswerView {

    private ViewGroup _root;
    private Chip _a;
    private Chip _b;
    private Chip _c;
    private Chip _d;
    private FloatingActionButton _confirm;

    public SingleSelectQuizAnswerView(Context context) {
        super(context);
        init();
    }

    public SingleSelectQuizAnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SingleSelectQuizAnswerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SingleSelectQuizAnswerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.element_answer_quiz_single_choice, this);
        _root = findViewById(R.id.rootView);
        _a = findViewById(R.id.answerA);
        _b = findViewById(R.id.answerB);
        _c = findViewById(R.id.answerC);
        _d = findViewById(R.id.answerD);
        _confirm = findViewById(R.id.confirmBtn);


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
    public Answer.PlainTextAnswer getAnswer() {
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