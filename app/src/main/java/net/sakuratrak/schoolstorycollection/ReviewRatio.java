package net.sakuratrak.schoolstorycollection;

public enum ReviewRatio {
    NICE,
    MID,
    BAD,
    UNKNOWN;

    public static ReviewRatio getByRatio(int ratio) {
        if (ratio > 80) {
            return NICE;
        } else if (ratio > 40) {
            return MID;
        } else if (ratio == -1) {
            return UNKNOWN;
        } else {
            return BAD;
        }
    }

    public static ReviewRatio fromId(int id) {
        return values()[id];
    }

    public int getId() {
        return ordinal();
    }
}
