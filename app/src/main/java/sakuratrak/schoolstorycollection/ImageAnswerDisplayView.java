package sakuratrak.schoolstorycollection;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import sakuratrak.schoolstorycollection.core.Answer;

public final class ImageAnswerDisplayView extends AnswerUiDisplayView {
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
        LayoutInflater.from(getContext()).inflate(R.layout.element_answer_display_image,this);
    }

    @Override
    public void setAnswer(Answer answer) {

    }
}
