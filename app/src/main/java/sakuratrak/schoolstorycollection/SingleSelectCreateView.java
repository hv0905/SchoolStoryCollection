package sakuratrak.schoolstorycollection;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import sakuratrak.schoolstorycollection.core.Answer;
import sakuratrak.schoolstorycollection.core.SelectableAnswer;

public final class SingleSelectCreateView extends AnswerUiCreatorView {

    //region elements
    Chip a;
    Chip b;
    Chip c;
    Chip d;
    //endregion


    public SingleSelectCreateView(Context context) {
        super(context);
        init();
    }

    public SingleSelectCreateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SingleSelectCreateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SingleSelectCreateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.element_answer_define_single_choice, this);
        a = findViewById(R.id.a);
        b = findViewById(R.id.b);
        c = findViewById(R.id.c);
        d = findViewById(R.id.d);
    }

    @Override
    @Nullable
    public Answer getAnswer() {
        if(!hasAnswer()) return null;
        SelectableAnswer sa = new SelectableAnswer();
        sa.A = a.isChecked();
        sa.B = b.isChecked();
        sa.C = c.isChecked();
        sa.D = d.isChecked();
        return sa;
    }

    @Override
    public void setAnswer(Answer value) {
        if(value instanceof SelectableAnswer){
            SelectableAnswer sv = (SelectableAnswer) value;
            if(sv.A){
                a.setChecked(true);
            }else if(sv.B){
                b.setChecked(true);
            }else if(sv.C){
                c.setChecked(true);
            }else if(sv.D){
                d.setChecked(true);
            }
        }else{
            throw new IllegalArgumentException("Answer type is not correct.");
        }

    }

    @Override
    public boolean hasAnswer() {
        return a.isChecked() || b.isChecked() || c.isChecked() || d.isChecked();
    }
}
