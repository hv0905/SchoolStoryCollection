package sakuratrak.schoolstorycollection.core;

import android.content.Context;

import java.io.Serializable;

import sakuratrak.schoolstorycollection.ImageAnswerCreateView;
import sakuratrak.schoolstorycollection.AnswerUiCreatorView;
import sakuratrak.schoolstorycollection.AnswerUiDisplayView;
import sakuratrak.schoolstorycollection.BlankAnswerCreateView;
import sakuratrak.schoolstorycollection.ImageAnswerDisplayView;
import sakuratrak.schoolstorycollection.MultiSelectCreateView;
import sakuratrak.schoolstorycollection.PlainTextAnswerDisplayView;
import sakuratrak.schoolstorycollection.R;
import sakuratrak.schoolstorycollection.SingleSelectCreateView;

public enum QuestionType implements Serializable {
    SINGLE_CHOICE,
    MULTIPLY_CHOICE,
    TYPEABLE_BLANK,
    BLANK,
    ANSWER;

    public static QuestionType id2Obj(int id) {
        return values()[id];
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

    public AnswerUiDisplayView getDisplayView(Context context){
        switch (this){

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
}
