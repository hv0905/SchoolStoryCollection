package sakuratrak.schoolstorycollection;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import sakuratrak.schoolstorycollection.core.LearningSubject;
import sakuratrak.schoolstorycollection.core.QuestionType;

public final class CommonAlerts {
    public static AlertDialog AskQuestionType(Context context, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        return new AlertDialog.Builder(context).setTitle(R.string.ChoseQuestionType).setIcon(R.drawable.ic_layers_black_24dp)
                .setItems(R.array.question_type, okListener).setNegativeButton(R.string.Cancel, cancelListener).show();
    }

    public static AlertDialog AskSubjectType(Context context, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        return new AlertDialog.Builder(context).setTitle(R.string.ChoseSubject).setIcon(R.drawable.ic_book_black_24dp)
                .setItems(R.array.learning_subjects, okListener).setNegativeButton(R.string.Cancel, cancelListener).show();
    }


}
