package sakuratrak.schoolstorycollection;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import sakuratrak.schoolstorycollection.core.Answer;
import sakuratrak.schoolstorycollection.core.TextAnswer;

public final class TextAnswerCreateView extends AnswerUiCreatorView {

    TextInputEditText _answerText;

    public TextAnswerCreateView(Context context) {
        super(context);
        init();
    }

    public TextAnswerCreateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextAnswerCreateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TextAnswerCreateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();

    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.element_answer_define_fill,this);
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
    }

    @Override
    public Answer getAnswer() {
       if(hasAnswer()) return new TextAnswer(_answerText.getText().toString());
       return null;
    }

    @Override
    public void setAnswer(Answer value) {
        if(value instanceof TextAnswer){
            _answerText.setText(((TextAnswer) value).Answer);
        }else{
            throw new IllegalArgumentException("value");
        }
    }

    @Override
    public boolean hasAnswer() {
        return !_answerText.getText().toString().trim().isEmpty();
    }
}
