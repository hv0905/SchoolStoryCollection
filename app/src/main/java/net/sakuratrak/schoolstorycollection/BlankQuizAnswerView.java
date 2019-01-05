package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import net.sakuratrak.schoolstorycollection.core.Answer;
import net.sakuratrak.schoolstorycollection.core.BlankAnswer;

public final class BlankQuizAnswerView extends CheckableQuizAnswerView {

    ViewGroup _root;
    TextInputEditText _editAnswer;
    FloatingActionButton _confirmBtn;


    public BlankQuizAnswerView(Context context) {
        super(context);
        init();
    }

    public BlankQuizAnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BlankQuizAnswerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BlankQuizAnswerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.element_answer_quiz_blank, this);
        _root = findViewById(R.id.rootView);
        _confirmBtn = findViewById(R.id.confirmBtn);
        _editAnswer = findViewById(R.id.editAnswer);

        _editAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateEmptyStatus();
            }
        });

        _confirmBtn.setOnClickListener(v -> onAnswerReport(ANSWER_COMPLETED));
        updateEmptyStatus();
    }

    @Override
    public Answer.PlainTextAnswer getAnswer() {
        if (hasAnswer()) return new BlankAnswer(_editAnswer.getText().toString());
        return null;
    }

    @Override
    public void setAnswer(Answer value) {
        if (value instanceof BlankAnswer) {
            _editAnswer.setText(((BlankAnswer) value).answer);
        } else {
            throw new IllegalArgumentException("value");
        }
    }

    @Override
    public boolean hasAnswer() {
        return !_editAnswer.getText().toString().trim().isEmpty();
    }

    @Override
    public void updateEmptyStatus() {
        _confirmBtn.setEnabled(hasAnswer());
    }
}
