package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.google.android.material.chip.Chip;

import net.sakuratrak.schoolstorycollection.R.id;
import net.sakuratrak.schoolstorycollection.R.layout;
import net.sakuratrak.schoolstorycollection.core.Answer;
import net.sakuratrak.schoolstorycollection.core.SelectableAnswer;

import androidx.annotation.Nullable;

public final class SingleSelectCreateView extends AnswerUiCreatorView {

    //region elements
    private Chip a;
    private Chip b;
    private Chip c;
    private Chip d;
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
        LayoutInflater.from(getContext()).inflate(layout.element_answer_define_single_choice, this);
        a = findViewById(id.a);
        b = findViewById(id.b);
        c = findViewById(id.c);
        d = findViewById(id.d);

        OnClickListener listener = v -> toggleOnUpdate();

        a.setOnClickListener(listener);
        b.setOnClickListener(listener);
        c.setOnClickListener(listener);
        d.setOnClickListener(listener);

    }

    @Override
    @Nullable
    public Answer getAnswer() {
        if (!hasAnswer()) return null;
        SelectableAnswer sa = new SelectableAnswer();
        sa.A = a.isChecked();
        sa.B = b.isChecked();
        sa.C = c.isChecked();
        sa.D = d.isChecked();
        return sa;
    }

    @Override
    public void setAnswer(Answer value) {
        if (value instanceof SelectableAnswer) {
            SelectableAnswer sv = (SelectableAnswer) value;
            a.setChecked(sv.A);
            b.setChecked(sv.B);
            c.setChecked(sv.C);
            d.setChecked(sv.D);
        } else {
            throw new IllegalArgumentException("value");
        }
    }

    @Override
    public boolean hasAnswer() {
        return a.isChecked() || b.isChecked() || c.isChecked() || d.isChecked();
    }
}
