package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.widget.Switch;

import com.google.android.material.textfield.TextInputEditText;

import net.sakuratrak.schoolstorycollection.R.drawable;
import net.sakuratrak.schoolstorycollection.R.id;
import net.sakuratrak.schoolstorycollection.R.layout;
import net.sakuratrak.schoolstorycollection.R.string;
import net.sakuratrak.schoolstorycollection.core.LearningSubject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;

public final class UnitFilterDialog {

    private LearningSubject _subject;
    private AlertDialog _dialog;
    private Runnable _onUpdate;

    private TextInputEditText _searchText;
    private Switch _hiddenSwitch;

    private String _keyword;
    private boolean _isHiddenShown;

    public UnitFilterDialog(LearningSubject _subject) {
        this._subject = _subject;
    }

    public UnitFilterDialog() {
    }

    public void showDialog(Context context) {
        _dialog = new Builder(context)
                .setView(layout.dialog_unit_filter)
                .setIcon(drawable.ic_filter_list_black_24dp)
                .setPositiveButton(string.confirm, (dialog, which) -> dialogClosed())
                .setNegativeButton(string.reset, (dialog, which) -> {
                    resetDialog();
                    dialogClosed();
                })
                .setOnCancelListener(dialog -> dialogClosed())
                .setTitle(string.filterTitle)
                .show();
        _dialog.setCanceledOnTouchOutside(false);

        //init the dialog
        _searchText = _dialog.findViewById(id.searchText);
        _hiddenSwitch = _dialog.findViewById(id.hiddenSwitch);

        _searchText.setText(_keyword);
        _hiddenSwitch.setChecked(_isHiddenShown);
    }

    public void resetDialog() {
        _searchText.setText("");
        _hiddenSwitch.setChecked(false);
    }

    private void dialogClosed() {
        _keyword = _searchText.getText().toString();
        _isHiddenShown = _hiddenSwitch.isChecked();
        if (_onUpdate != null)
            _onUpdate.run();
    }

    public LearningSubject get_subject() {
        return _subject;
    }

    public void set_subject(LearningSubject _subject) {
        if (_subject == this._subject) return;
        this._subject = _subject;
    }

    public Runnable get_onUpdate() {
        return _onUpdate;
    }

    public void set_onUpdate(Runnable _onUpdate) {
        this._onUpdate = _onUpdate;
    }

    public String get_keyword() {
        return _keyword;
    }

    public void set_keyword(String _keyword) {
        this._keyword = _keyword;
    }

    public boolean is_isHiddenShown() {
        return _isHiddenShown;
    }

    public void set_isHiddenShown(boolean _isHiddenShown) {
        this._isHiddenShown = _isHiddenShown;
    }
}
