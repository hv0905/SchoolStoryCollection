package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;

import net.sakuratrak.schoolstorycollection.R.id;
import net.sakuratrak.schoolstorycollection.R.layout;

public final class ImageQuizAnswerView extends QuizAnswerView {

    Button _confirmBtn;

    public ImageQuizAnswerView(Context context) {
        super(context);
        init();

    }

    public ImageQuizAnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public ImageQuizAnswerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public ImageQuizAnswerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    void init() {
        LayoutInflater.from(getContext()).inflate(layout.element_answer_quiz_image, this);
        _confirmBtn = findViewById(id.confirmBtn);
        _confirmBtn.setOnClickListener(v -> onAnswerReport(ANSWER_COMPLETED));
    }
}
