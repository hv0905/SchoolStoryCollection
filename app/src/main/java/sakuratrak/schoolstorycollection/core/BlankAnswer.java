package sakuratrak.schoolstorycollection.core;

public final class BlankAnswer extends Answer.PlainTextAnswer {

    public BlankAnswer(){}

    public BlankAnswer(String answer) {
        this.answer = answer;
    }

    public String answer;

    @Override
    public String toString() {
        return answer;
    }
}
