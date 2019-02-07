package net.sakuratrak.schoolstorycollection;

import androidx.annotation.StringRes;

public enum ReviewRatio {
    NICE(R.string.reviewNice),
    MID(R.string.reviewMid),
    BAD(R.string.reviewBad),
    UNKNOWN(R.string.reviewUnknown);

    int resId;


    ReviewRatio(@StringRes int resId){
        this.resId = resId;
    }

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

    public int getStr(){
        return resId;
    }
}
