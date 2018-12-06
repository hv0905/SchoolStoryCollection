package net.sakuratrak.schoolstorycollection.core;

import java.io.Serializable;

public enum LearningSubject implements Serializable {
    CHINESE,
    MATH,
    ENGLISH,
    PHYSICS,
    CHEMISTRY,
    BIOLOGIC,
    POLITICS,
    HISTORY,
    GEO,
    OTHER;

    public static LearningSubject id2Obj(int id) {
        return LearningSubject.values()[id];
    }

    public int getId() {
        return ordinal();
    }
}
