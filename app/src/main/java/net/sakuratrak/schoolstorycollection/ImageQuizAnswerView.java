package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;

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
        LayoutInflater.from(getContext()).inflate(R.layout.element_answer_quiz_image,this);
        _confirmBtn = findViewById(R.id.confirmBtn);
        _confirmBtn.setOnClickListener(v -> onAnswerReport(ANSWER_COMPLETED));
    }
}
