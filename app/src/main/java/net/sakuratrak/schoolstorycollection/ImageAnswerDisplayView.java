package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import net.sakuratrak.schoolstorycollection.core.Answer;
import net.sakuratrak.schoolstorycollection.core.ImageAnswer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class ImageAnswerDisplayView extends AnswerUiDisplayView {

    ImageDisplayView _root;

    public ImageAnswerDisplayView(@NonNull Context context) {
        super(context);
        init();

    }

    public ImageAnswerDisplayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public ImageAnswerDisplayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public ImageAnswerDisplayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.element_answer_display_image, this);
        _root = findViewById(R.id.imgs);
    }

    @Override
    public void setAnswer(Answer answer) {
        if (answer instanceof ImageAnswer) {
            _root.setImages(((ImageAnswer) answer).Image);
        } else throw new IllegalArgumentException("value");
    }
}
