package sakuratrak.schoolstorycollection.core;

import android.app.Activity;

import sakuratrak.schoolstorycollection.AnswerUiCreatorView;

public class SelectableAnswer extends Answer.PlainTextAnswer {

    public boolean A;
    public boolean B;
    public boolean C;
    public boolean D;

    public SelectableAnswer(boolean a, boolean b, boolean c, boolean d) {
        A = a;
        B = b;
        C = c;
        D = d;
    }

    public SelectableAnswer() {
    }

    @Override
    public String toString() {
        return (A ? "A" : "") +(B ? "B" : "") +(C ? "C" : "") +(D ? "D" : "");
    }



}
