package net.sakuratrak.schoolstorycollection.core;

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

    @Override
    public float checkAnswer(PlainTextAnswer userAnswer) {
        if(!(userAnswer instanceof BlankAnswer)){
            throw new IllegalArgumentException("userAnswer");
        }
        String[] answerList = getAnswerList();
        for (String item : answerList) {
            if(item.equals(((BlankAnswer) userAnswer).answer)) return 1;
        }
        return 0;
    }

    public String[] getAnswerList(){
        String tmp = answer.replace('；',';');
        String[] answers = tmp.split(";");
        for (int i = 0;i<answers.length;i++){
            answers[i] = answers[i].trim();
        }
        return answers;
    }
}
