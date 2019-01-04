package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;


public final class QuizMarkView extends FrameLayout {

    Chip _markA;
    Chip _markB;
    Chip _markC;
    Chip _markD;
    FloatingActionButton _doneButton;
    SeekBar _markBar;
    TextView _markBarVal;
    ViewGroup _rootView;




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

    void init(){
        _rootView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.element_quiz_mark,this);

        _markA = _rootView.findViewById(R.id.markA);
        _markB = _rootView.findViewById(R.id.markB);
        _markC = _rootView.findViewById(R.id.markC);
        _markD = _rootView.findViewById(R.id.markD);
        _doneButton = _rootView.findViewById(R.id.doneButton);
        _markBar = _rootView.findViewById(R.id.markBar);
        _markBarVal = _rootView.findViewById(R.id.markBarVal);


        _markA.setOnClickListener(v -> setScore(100));

        _markB.setOnClickListener(v -> setScore(80));

        _markC.setOnClickListener(v -> setScore(20));

        _markD.setOnClickListener(v -> setScore(0));

        _markBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    public void setScore(int score){
        _markBar.setProgress(score);
        updateText();
    }

    public int getScore(){
        return _markBar.getProgress();
    }

    void updateText(){
        _markBarVal.setText(String.format(Locale.ENGLISH,"%d%%",_markBar.getProgress()));
    }

    void setOnConfirmListener(OnClickListener l){
        _doneButton.setOnClickListener(l);
    }


}
