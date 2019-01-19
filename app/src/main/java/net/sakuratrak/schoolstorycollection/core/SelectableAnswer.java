package net.sakuratrak.schoolstorycollection.core;

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

    public static SelectableAnswer fromMetaData(String metaData) {
        char[] c = metaData.toCharArray();
        SelectableAnswer sa = new SelectableAnswer();
        sa.A = c[0] == '1';
        sa.B = c[1] == '1';
        sa.C = c[2] == '1';
        sa.D = c[3] == '1';
        return sa;
    }

    @Override
    public String toString() {
        return (A ? "A" : "") + (B ? "B" : "") + (C ? "C" : "") + (D ? "D" : "");
    }

    @Override
    public float checkAnswer(PlainTextAnswer userAnswer) {
        if (!(userAnswer instanceof SelectableAnswer)) {
            throw new IllegalArgumentException("userAnswer");
        }

        int correctCount = 0;
        int itemCount = getItemCount();
        boolean[] correctAnswerArray = getAnswerArray();
        boolean[] userAnswerArray = ((SelectableAnswer) userAnswer).getAnswerArray();

        for (int i = 0; i < correctAnswerArray.length; i++) {
            if (correctAnswerArray[i]) {
                if (userAnswerArray[i]) correctCount++;
            } else {
                if (userAnswerArray[i]) return 0;
            }
        }

        if (correctCount <= 0) return 0;
        else if (correctCount < itemCount) {
            return 0.5f;
        } else return 1;

    }

    public int getItemCount() {
        int count = 0;
        for (boolean item : getAnswerArray()) {
            if (item) count++;
        }
        return count;
    }

    public boolean[] getAnswerArray() {
        return new boolean[]{A, B, C, D};
    }

    @Override
    public String toMetaData() {
        StringBuilder builder = new StringBuilder();
        boolean[] array = getAnswerArray();
        for (boolean item :
                array) {
            builder.append(item ? '1' : '0');
        }
        return builder.toString();
    }
}
