package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Switch;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningSubject;
import net.sakuratrak.schoolstorycollection.core.LearningUnitInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

public final class FilterDialog {

    public static final String TAG = "filter_dialog";

    private AlertDialog _dialog;

    private TextInputEditText _searchText;
    private Switch _hiddenSwitch;
    private ChipGroup _unitChips;

    private String _searchTxt;
    private boolean _isHiddenShown;
    private LearningSubject _subject;
    private List<LearningUnitInfo> _selectedUnits;
    private Runnable _onUpdate;


    public FilterDialog() {

    }

    public FilterDialog(LearningSubject _subject) {
        this._subject = _subject;
        _selectedUnits = new ArrayList<>();
    }

    public void showDialog(Context context) {
        _dialog = new AlertDialog.Builder(context)
                .setView(R.layout.layout_filter_dialog)
                .setIcon(R.drawable.ic_filter_list_black_24dp)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    dialogClosed();
                })
                .setNegativeButton(R.string.reset, (dialog, which) -> {
                    resetDialog();
                    dialogClosed();
                })
                .setOnCancelListener(dialog -> {
                    dialogClosed();
                })
                .setTitle(R.string.filterTitle)
                .show();
        _dialog.setCanceledOnTouchOutside(false);
        //开始初始化控件


        _searchText = _dialog.findViewById(R.id.searchText);
        _hiddenSwitch = _dialog.findViewById(R.id.hiddenSwitch);
        _unitChips = _dialog.findViewById(R.id.unitChips);


        _unitChips.removeAllViews();

        try {
            List<LearningUnitInfo> units = new LearningUnitInfo.LearningUnitInfoDaoHelper(DbManager.getDefaultHelper(context))
                    .findBySubject(_subject);
            LayoutInflater lf = _dialog.getLayoutInflater();
            for (int i = units.size() - 1; i >= 0; i--) {
                final LearningUnitInfo unit = units.get(i);
                Chip ch = (Chip) lf.inflate(R.layout.elements_unit_choose_chip, _unitChips, false);
                ch.setText(unit.getName());
                ch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (_selectedUnits.contains(unit)) {
                    ch.setChecked(true);
                } else {
                    ch.setChecked(false);
                }
                _unitChips.addView(ch, 0, new ChipGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        _selectedUnits.add(unit);
                    } else {
                        _selectedUnits.remove(unit);
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void dialogClosed() {
        //apply dialog info into fields
        _searchTxt = _searchText.getText().toString();
        _isHiddenShown = _hiddenSwitch.isChecked();
        if (_onUpdate != null)
            _onUpdate.run();
    }

    public void resetDialog() {
        _searchText.setText("");
        _selectedUnits.clear();
        _hiddenSwitch.setChecked(false);
    }

    public String get_searchTxt() {
        return _searchTxt;
    }

    public void set_searchTxt(String _searchTxt) {
        this._searchTxt = _searchTxt;
    }

    public boolean is_isHiddenShown() {
        return _isHiddenShown;
    }

    public void set_isHiddenShown(boolean _isHiddenShown) {
        this._isHiddenShown = _isHiddenShown;
    }

    public LearningSubject get_subject() {
        return _subject;
    }

    public void set_subject(LearningSubject _subject) {
        this._subject = _subject;
    }

    public List<LearningUnitInfo> get_selectedUnits() {
        return _selectedUnits;
    }

    public void set_selectedUnits(List<LearningUnitInfo> _selectedUnits) {
        this._selectedUnits = _selectedUnits;
    }

    public Runnable get_onUpdate() {
        return _onUpdate;
    }

    public void set_onUpdate(Runnable _onUpdate) {
        this._onUpdate = _onUpdate;
    }

}
