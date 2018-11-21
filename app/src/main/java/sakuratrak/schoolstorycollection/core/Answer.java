package sakuratrak.schoolstorycollection.core;

import java.io.Serializable;

public abstract class Answer implements Serializable {

    public static abstract class PlainTextAnswer extends Answer {

        @Override
        public abstract String toString();

        public abstract float checkAnswer(PlainTextAnswer userAnswer);
    }

}
