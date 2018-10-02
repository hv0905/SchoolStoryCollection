package sakuratrak.schoolstorycollection.core;

import java.io.Serializable;

public enum QuestionType implements Serializable {
    SINGLE_CHOICE(0),
    MULTIPLY_CHOICE(1),
    TYPEABLE_BLANK(2),
    BLANK(3),
    ANSWER(4);

    int _id;

    QuestionType(int id){
        _id = id;
    }

    public static QuestionType id2Obj(int id){
        switch (id) {
            case 0:
                return QuestionType.SINGLE_CHOICE;
            case 1:
                return QuestionType.MULTIPLY_CHOICE;
            case 2:
                return QuestionType.TYPEABLE_BLANK;
            case 3:
                return QuestionType.BLANK;
            case 4:
                return QuestionType.ANSWER;
        }
        throw new IllegalArgumentException();
    }

    public int getId(){
        return _id;
    }
}
