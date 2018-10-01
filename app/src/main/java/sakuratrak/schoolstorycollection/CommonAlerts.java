package sakuratrak.schoolstorycollection;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import sakuratrak.schoolstorycollection.sakuratrak.schoolstorycollection.core.LearningSubject;
import sakuratrak.schoolstorycollection.sakuratrak.schoolstorycollection.core.QuestionType;

public final class CommonAlerts {
    public static AlertDialog AskQuestionType(Context context,DialogInterface.OnClickListener listener){
        return new AlertDialog.Builder(context).setTitle(R.string.ChoseQuestionType).setIcon(R.drawable.ic_layers_black_24dp)
                .setItems(new String[]{context.getString(R.string.SingleChoice), context.getString(R.string.MultiChoice), context.getString(R.string.BlankFill),context.getString(R.string.answerable)}, listener).show();
    }

    public static QuestionType Dialog2QuestionType(int id) throws IllegalArgumentException{
        switch (id){
            case 0:
                return QuestionType.SINGLE_CHOICE;
            case 1:
                return QuestionType.MULTIPLY_CHOICE;
            case 2:
                return QuestionType.BLANK;
            case 3:
                return  QuestionType.ANSWER;
        }
        throw new IllegalArgumentException();
    }


    public static AlertDialog AskSubjectType(Context context,DialogInterface.OnClickListener listener){
        return new AlertDialog.Builder(context).setTitle(R.string.ChoseSubject).setIcon(R.drawable.ic_book_black_24dp)
                .setItems(new String[]{context.getString(R.string.SubjectChinese),context.getString(R.string.SubjectMath),context.getString(R.string.SubjectEnglish),context.getString(R.string.SubjectPhysics),context.getString(R.string.SubjectChemistry),context.getString(R.string.SubjectBiology),context.getString(R.string.SubjectPolitics),context.getString(R.string.SubjectHistory),context.getString(R.string.SubjectGeo)},listener).show();
    }


    public static LearningSubject Dialog2Subject(int id) throws IllegalArgumentException{
        switch (id){
            case 0:
                return LearningSubject.CHINESE;
            case 1:
                return LearningSubject.MATH;
            case 2:
                return  LearningSubject.ENGLISH;
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
        }
        throw new IllegalArgumentException();
    }
}
