package net.sakuratrak.schoolstorycollection.core;

import android.content.Context;

import net.sakuratrak.schoolstorycollection.AnswerUiCreatorView;
import net.sakuratrak.schoolstorycollection.AnswerUiDisplayView;
import net.sakuratrak.schoolstorycollection.BlankAnswerCreateView;
import net.sakuratrak.schoolstorycollection.BlankQuizAnswerView;
import net.sakuratrak.schoolstorycollection.ImageAnswerCreateView;
import net.sakuratrak.schoolstorycollection.ImageAnswerDisplayView;
import net.sakuratrak.schoolstorycollection.ImageQuizAnswerView;
import net.sakuratrak.schoolstorycollection.MultiSelectCreateView;
import net.sakuratrak.schoolstorycollection.MultiplySelectQuizAnswerView;
import net.sakuratrak.schoolstorycollection.PlainTextAnswerDisplayView;
import net.sakuratrak.schoolstorycollection.QuizAnswerView;
import net.sakuratrak.schoolstorycollection.R;
import net.sakuratrak.schoolstorycollection.SingleSelectCreateView;
import net.sakuratrak.schoolstorycollection.SingleSelectQuizAnswerView;

import java.io.Serializable;

public enum QuestionType implements Serializable {
    SINGLE_CHOICE(R.string.singleChoiceQuestion),
    MULTIPLY_CHOICE(R.string.multiChoiceQuestion),
    TYPEABLE_BLANK(R.string.fillQuestion),
    BLANK(R.string.fillQuestion),
    ANSWER(R.string.answerQuestion);

    final int _resId;

    QuestionType(int resId) {
        _resId = resId;
    }

    public static QuestionType id2Obj(int id) {
        return values()[id];
    }

    public int getTitleId() {
        return _resId;
    }

    public int getId() {
        return ordinal();
    }

    public AnswerUiCreatorView getCreatorView(Context context) {
        switch (this) {

            case SINGLE_CHOICE:
                return new SingleSelectCreateView(context);
            case MULTIPLY_CHOICE:
                return new MultiSelectCreateView(context);
            case TYPEABLE_BLANK:
                return new BlankAnswerCreateView(context);
            case BLANK:
                ImageAnswerCreateView view = new ImageAnswerCreateView(context);
                view.setNoticeText(R.string.fillAnswer);
                return view;
            case ANSWER:
                ImageAnswerCreateView view1 = new ImageAnswerCreateView(context);
                view1.setNoticeText(R.string.answerQuestionAnswer);
                return view1;
        }

        throw new IllegalArgumentException();
    }

    public AnswerUiDisplayView getDisplayView(Context context) {
        switch (this) {

            case SINGLE_CHOICE:
            case MULTIPLY_CHOICE:
            case TYPEABLE_BLANK:
                return new PlainTextAnswerDisplayView(context);
            case BLANK:
            case ANSWER:
                return new ImageAnswerDisplayView(context);
        }

        throw new IllegalArgumentException();
    }

    public QuizAnswerView getQuizAnswerView(Context context) {
        switch (this) {
            case SINGLE_CHOICE:
                return new SingleSelectQuizAnswerView(context);
            case MULTIPLY_CHOICE:
                return new MultiplySelectQuizAnswerView(context);
            case TYPEABLE_BLANK:
                return new BlankQuizAnswerView(context);
            case BLANK:
            case ANSWER:
                return new ImageQuizAnswerView(context);
        }
        throw new IllegalArgumentException("value");
    }
}
