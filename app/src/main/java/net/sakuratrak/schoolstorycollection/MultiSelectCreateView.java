package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.google.android.material.chip.Chip;

import net.sakuratrak.schoolstorycollection.core.Answer;
import net.sakuratrak.schoolstorycollection.core.SelectableAnswer;

import androidx.annotation.Nullable;

public final class MultiSelectCreateView extends AnswerUiCreatorView {

    //region elements
    Chip a;
    Chip b;
    Chip c;
    Chip d;
    //endregion


    public MultiSelectCreateView(Context context) {
        super(context);
        init();
    }

    public MultiSelectCreateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiSelectCreateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MultiSelectCreateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.element_answer_define_multiply_choice, this);
        a = findViewById(R.id.a);
        b = findViewById(R.id.b);
        c = findViewById(R.id.c);
        d = findViewById(R.id.d);

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
        int count = 0;
        if (a.isChecked()) count++;
        if (b.isChecked()) count++;
        if (c.isChecked()) count++;
        if (d.isChecked()) count++;
        return count >= 2;
    }
}
