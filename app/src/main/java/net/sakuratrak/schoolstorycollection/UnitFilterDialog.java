package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.widget.Switch;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import net.sakuratrak.schoolstorycollection.core.LearningSubject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

public final class UnitFilterDialog {

    private static final int[] QUESTION_REVIEW_RATIO_LEVEL_CHIP_IDS = {
            R.id.chipWell,
            R.id.chipMid,
            R.id.chipBad,
            R.id.chipUnknown,
    };

    private LearningSubject _subject;
    private AlertDialog _dialog;

    private TextInputEditText _searchText;
    private Switch _hiddenSwitch;
    private ChipGroup _reviewRatioChips;


    private String _keyword;
    private boolean _isHiddenShown;
    private List<ReviewRatio> _selectedRatios = new ArrayList<>();

    private Runnable _onUpdate;

    public UnitFilterDialog(LearningSubject _subject) {
        this._subject = _subject;
    }

    public UnitFilterDialog() {
    }

    public boolean isFilterActive() {
        return (_keyword != null && !_keyword.trim().isEmpty()) ||
                _selectedRatios.size() != 0 ||
                _isHiddenShown;

    }

    public void showDialog(Context context) {
        _dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_unit_filter)
                .setIcon(R.drawable.ic_filter_list_black_24dp)
                .setPositiveButton(R.string.confirm, (dialog, which) -> dialogClosed())
                .setNegativeButton(R.string.reset, (dialog, which) -> {
                    resetDialog();
                    dialogClosed();
                })
                .setOnCancelListener(dialog -> dialogClosed())
                .setTitle(R.string.filterTitle)
                .show();
        _dialog.setCanceledOnTouchOutside(false);

        //init the dialog
        _searchText = _dialog.findViewById(R.id.searchText);
        _hiddenSwitch = _dialog.findViewById(R.id.hiddenSwitch);
        _reviewRatioChips = _dialog.findViewById(R.id.reviewRatioChips);

        for (int i = 0; i < QUESTION_REVIEW_RATIO_LEVEL_CHIP_IDS.length; i++) {
            final int finalI = i;
            Chip chip = _dialog.findViewById(QUESTION_REVIEW_RATIO_LEVEL_CHIP_IDS[i]);
            if (_selectedRatios.contains(ReviewRatio.fromId(i))) {
                //noinspection ConstantConditions
                chip.setChecked(true);
            }
            //noinspection ConstantConditions
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    _selectedRatios.add(ReviewRatio.fromId(finalI));
                } else {
                    _selectedRatios.remove(ReviewRatio.fromId(finalI));
                }
            });
        }

        _searchText.setText(_keyword);
        _hiddenSwitch.setChecked(_isHiddenShown);
    }

    public void resetDialog() {
        _searchText.setText("");
        _hiddenSwitch.setChecked(false);
        _selectedRatios.clear();
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

    public List<ReviewRatio> get_selectedRatios() {
        return _selectedRatios;
    }

    public void set_selectedRatios(List<ReviewRatio> _selectedRatios) {
        this._selectedRatios = _selectedRatios;
    }


}
