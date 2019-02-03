package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;

import net.sakuratrak.schoolstorycollection.R.array;
import net.sakuratrak.schoolstorycollection.R.drawable;
import net.sakuratrak.schoolstorycollection.R.string;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;

public final class CommonAlerts {
    @SuppressWarnings("UnusedReturnValue")
    public static AlertDialog AskQuestionType(Context context, OnClickListener okListener, OnClickListener cancelListener) {
        return new Builder(context).setTitle(string.ChoseQuestionType).setIcon(drawable.ic_layers_black_24dp)
                .setItems(array.question_type, okListener).setNegativeButton(string.Cancel, cancelListener).show();
    }

    public static AlertDialog AskSubjectType(Context context, OnClickListener okListener, OnClickListener cancelListener) {
        return new Builder(context).setTitle(string.ChoseSubject).setIcon(drawable.ic_book_black_24dp)
                .setItems(array.learning_subjects, okListener).setNegativeButton(string.Cancel, cancelListener).show();
    }

    public static AlertDialog AskPhoto(Context context, OnClickListener okListener, OnClickListener cancelListener) {
        return new Builder(context).setItems(array.photoProvidePath, okListener).setNegativeButton(string.cancel, cancelListener).show();
    }


}
