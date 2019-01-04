package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import com.google.android.material.textfield.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import net.sakuratrak.schoolstorycollection.core.Answer;
import net.sakuratrak.schoolstorycollection.core.BlankAnswer;

public final class BlankAnswerCreateView extends AnswerUiCreatorView {

    TextInputEditText _answerText;

    public BlankAnswerCreateView(Context context) {
        super(context);
        init();
    }

    public BlankAnswerCreateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BlankAnswerCreateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BlankAnswerCreateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();

    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.element_answer_define_blank,this);
        _answerText = findViewById(R.id.answerText);
        _answerText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                toggleOnUpdate();
            }
        });

        toggleOnUpdate();
    }

    @Override
    public Answer getAnswer() {
       if(hasAnswer()) return new BlankAnswer(_answerText.getText().toString());
       return null;
    }

    @Override
    public void setAnswer(Answer value) {
        if(value instanceof BlankAnswer){
            _answerText.setText(((BlankAnswer) value).answer);
        }else{
            throw new IllegalArgumentException("value");
        }
    }

    @Override
    public boolean hasAnswer() {
        return !_answerText.getText().toString().trim().isEmpty();
    }
}
