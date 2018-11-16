package sakuratrak.schoolstorycollection;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import sakuratrak.schoolstorycollection.core.Answer;
import sakuratrak.schoolstorycollection.core.ImageAnswer;

public final class AnswerAnswerCreateView extends AnswerUiCreatorView {

    //region elements
    TextView _noticeText;
    ImageSelectView _answerImage;
    //endregion


    public AnswerAnswerCreateView(Context context) {
        super(context);
        init();
    }

    public AnswerAnswerCreateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public AnswerAnswerCreateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public AnswerAnswerCreateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();

    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.element_answer_define_answer, this);
        _noticeText = findViewById(R.id.noticeText);
        _answerImage = findViewById(R.id.answerImage);
    }

    @Nullable
    @Override
    public Answer getAnswer() {
        if (hasAnswer())
            return new ImageAnswer(_answerImage.getImages());
        return null;
    }

    @Override
    public void setAnswer(Answer value) {
        if (value instanceof ImageAnswer) {
            _answerImage.setImages(((ImageAnswer) value).Image);
        } else {
            throw new IllegalArgumentException("value");
        }
    }

    @Override
    public boolean hasAnswer() {
        return _answerImage.getImages().size() == 0;
    }

    public void setNoticeText(CharSequence text){
        _noticeText.setText(text);
    }

    /**using this method to link native method for ImageSelectView.
     * NEVER IGNORE THIS!!!
     * */
    public ImageSelectView getAnswerImage(){
        return _answerImage;
    }
}
