package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.sakuratrak.schoolstorycollection.R.id;
import net.sakuratrak.schoolstorycollection.R.layout;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public final class QuizMarkView extends FrameLayout {

    private Chip _markA;
    private Chip _markB;
    private Chip _markC;
    private Chip _markD;
    private FloatingActionButton _doneButton;
    private SeekBar _markBar;
    private TextView _markBarVal;
    private ViewGroup _rootView;


    public QuizMarkView(@NonNull Context context) {
        super(context);
        init();

    }

    public QuizMarkView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public QuizMarkView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public QuizMarkView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        _rootView = (ViewGroup) LayoutInflater.from(getContext()).inflate(layout.element_quiz_mark, this);

        _markA = _rootView.findViewById(id.markA);
        _markB = _rootView.findViewById(id.markB);
        _markC = _rootView.findViewById(id.markC);
        _markD = _rootView.findViewById(id.markD);
        _doneButton = _rootView.findViewById(id.doneButton);
        _markBar = _rootView.findViewById(id.markBar);
        _markBarVal = _rootView.findViewById(id.markBarVal);


        _markA.setOnClickListener(v -> setScore(100));

        _markB.setOnClickListener(v -> setScore(80));

        _markC.setOnClickListener(v -> setScore(20));

        _markD.setOnClickListener(v -> setScore(0));

        _markBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        updateText();

    }

    public int getScore() {
        return _markBar.getProgress();
    }

    private void setScore(int score) {
        _markBar.setProgress(score);
        updateText();
    }

    private void updateText() {
        _markBarVal.setText(String.format(Locale.ENGLISH, "%d%%", _markBar.getProgress()));
    }

    void setOnConfirmListener(OnClickListener l) {
        _doneButton.setOnClickListener(l);
    }


}
