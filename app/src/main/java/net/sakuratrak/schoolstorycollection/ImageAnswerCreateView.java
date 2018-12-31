package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import net.sakuratrak.schoolstorycollection.core.Answer;
import net.sakuratrak.schoolstorycollection.core.ImageAnswer;

public final class ImageAnswerCreateView extends AnswerUiCreatorView {

    //region elements
    TextView _noticeText;
    ImageSelectView _answerImage;
    //endregion


    public ImageAnswerCreateView(Context context) {
        super(context);
        init();
    }

    public ImageAnswerCreateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public ImageAnswerCreateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public ImageAnswerCreateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();

    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.element_answer_define_answer, this);
        _noticeText = findViewById(R.id.noticeText);
        _answerImage = findViewById(R.id.answerImage);

        _answerImage.setOnItemToggleListener(v -> toggleOnUpdate());
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
        return _answerImage.getImages().size() != 0;
    }

    public void setNoticeText(CharSequence text){
        _noticeText.setText(text);
    }

    public void setNoticeText(int resId){
        _noticeText.setText(resId);
    }

    /**using this method to link native method for ImageSelectView.
     * NEVER IGNORE THIS!!!
     * */
    public ImageSelectView getAnswerImage(){
        return _answerImage;
    }
}
