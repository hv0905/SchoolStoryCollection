package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public final class QuizCheckView extends FrameLayout {

    public static final int NOTICE_NONE = 0;
    public static final int NOTICE_ALL_RIGHT = 1;
    public static final int NOTICE_HALF_RIGHT = 2;
    public static final int NOTICE_NONE_RIGHT = 3;


    private ViewGroup _rootView;
    private FrameLayout _noticeBar;
    private TextView _allRightContent;
    private TextView _halfRightContent;
    private TextView _noneRightContent;
    private MaterialButton _quitBtn;
    private MaterialButton _nextBtn;

    public QuizCheckView(@NonNull Context context) {
        super(context);
        init();

    }

    public QuizCheckView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public QuizCheckView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public QuizCheckView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.element_quiz_check, this);
        _rootView = findViewById(R.id.rootView);
        _noticeBar = findViewById(R.id.noticeBar);
        _allRightContent = findViewById(R.id.allRightContent);
        _halfRightContent = findViewById(R.id.halfRightContent);
        _noneRightContent = findViewById(R.id.noneRightContent);
        _quitBtn = findViewById(R.id.quitBtn);
        _nextBtn = findViewById(R.id.nextBtn);


    }

    public void setNoticeContain(int notice) {
        _allRightContent.setVisibility(GONE);
        _halfRightContent.setVisibility(GONE);
        _noneRightContent.setVisibility(GONE);

        switch (notice) {
            case NOTICE_ALL_RIGHT:
                _allRightContent.setVisibility(VISIBLE);
                break;
            case NOTICE_HALF_RIGHT:
                _halfRightContent.setVisibility(VISIBLE);
                break;
            case NOTICE_NONE_RIGHT:
                _noneRightContent.setVisibility(VISIBLE);
                break;
        }
    }

    public void setOnNextBtnClickListener(OnClickListener l) {
        _nextBtn.setOnClickListener(l);
    }

    public void setOnQuitBtnClickListener(OnClickListener l) {
        _quitBtn.setOnClickListener(l);
    }

    public void setIsDone(boolean isDone) {
        if (isDone) {
            _nextBtn.setText(R.string.completeQuiz);
            _nextBtn.setIconResource(R.drawable.ic_done_white_24dp);
            _quitBtn.setVisibility(GONE);
        } else {
            _nextBtn.setText(R.string.nextQuestion);
            _nextBtn.setIconResource(R.drawable.ic_navigate_next_white_24dp);
            _quitBtn.setVisibility(VISIBLE);
        }
    }

}
