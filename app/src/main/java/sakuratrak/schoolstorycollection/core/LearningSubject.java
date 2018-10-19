package sakuratrak.schoolstorycollection.core;

import java.io.Serializable;

public enum LearningSubject implements Serializable {
    CHINESE(0),
    MATH(1),
    ENGLISH(2),
    PHYSICS(3),
    CHEMISTRY(4),
    BIOLOGIC(5),
    POLITICS(6),
    HISTORY(7),
    GEO(8),
    OTHER(9);

    private int _id;

    LearningSubject(int id){
        _id = id;
    }

    public static LearningSubject id2Obj(int id){
        switch (id) {
            case 0:
                return LearningSubject.CHINESE;
            case 1:
                return LearningSubject.MATH;
            case 2:
                return LearningSubject.ENGLISH;
            case 3:
                return LearningSubject.PHYSICS;
            case 4:
                return LearningSubject.CHEMISTRY;
            case 5:
                return LearningSubject.BIOLOGIC;
            case 6:
                return LearningSubject.POLITICS;
            case 7:
                return LearningSubject.HISTORY;
            case 8:
                return LearningSubject.GEO;
            case 9:
                return LearningSubject.OTHER;
        }
        throw new IllegalArgumentException();
    }

    public int getId(){
        return _id;
    }
}
