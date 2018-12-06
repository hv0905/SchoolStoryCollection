package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public final class CommonAlerts {
    @SuppressWarnings("UnusedReturnValue")
    public static AlertDialog AskQuestionType(Context context, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        return new AlertDialog.Builder(context).setTitle(R.string.ChoseQuestionType).setIcon(R.drawable.ic_layers_black_24dp)
                .setItems(R.array.question_type, okListener).setNegativeButton(R.string.Cancel, cancelListener).show();
    }

    public static AlertDialog AskSubjectType(Context context, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        return new AlertDialog.Builder(context).setTitle(R.string.ChoseSubject).setIcon(R.drawable.ic_book_black_24dp)
                .setItems(R.array.learning_subjects, okListener).setNegativeButton(R.string.Cancel, cancelListener).show();
    }

    public static AlertDialog AskPhoto(Context context, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener){
        return new AlertDialog.Builder(context).setItems(R.array.photoProvidePath,okListener).setNegativeButton(R.string.cancel,cancelListener).show();
    }


}
