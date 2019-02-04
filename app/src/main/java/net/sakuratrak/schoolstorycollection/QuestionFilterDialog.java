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
import net.sakuratrak.schoolstorycollection.core.QuestionType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

public final class QuestionFilterDialog {

    public static final String TAG = "filter_dialog";

    private static final int[] QUESTION_TYPE_CHIP_IDS = new int[]{
            R.id.chipSingleChoice,
            R.id.chipMultiChoice,
            R.id.chipEditableFill,
            R.id.chipFill,
            R.id.chipAnswer,
    };

    private AlertDialog _dialog;

    private TextInputEditText _searchText;
    private Switch _hiddenSwitch;
    private ChipGroup _unitChips;
    private ChipGroup _questionTypeGroup;

    private String _searchTxt;
    private boolean _isHiddenShown;
    private LearningSubject _subject;
    private List<Integer> _selectedUnitIds = new ArrayList<>();
    private List<QuestionType> _selectedType = new ArrayList<>();
    private Runnable _onUpdate;


    public QuestionFilterDialog() {

    }

    public QuestionFilterDialog(LearningSubject _subject) {
        this._subject = _subject;

    }

    public void showDialog(Context context) {
        _dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_question_filter)
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
        //开始初始化控件


        _searchText = _dialog.findViewById(R.id.searchText);
        _hiddenSwitch = _dialog.findViewById(R.id.hiddenSwitch);
        _unitChips = _dialog.findViewById(R.id.unitChips);
        _questionTypeGroup = _dialog.findViewById(R.id.questionTypeGroup);

        for (int i = 0; i < QUESTION_TYPE_CHIP_IDS.length; i++) {
            final int finalI = i;
            Chip chip = _dialog.findViewById(QUESTION_TYPE_CHIP_IDS[i]);
            if (_selectedType.contains(QuestionType.id2Obj(i))) {
                //noinspection ConstantConditions
                chip.setChecked(true);
            }
            //noinspection ConstantConditions
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    _selectedType.add(QuestionType.id2Obj(finalI));
                } else {
                    _selectedType.remove(QuestionType.id2Obj(finalI));
                }
            });
        }


        _searchText.setText(_searchTxt);
        _hiddenSwitch.setChecked(_isHiddenShown);

        _unitChips.removeAllViews();

        try {
            List<LearningUnitInfo> units = new LearningUnitInfo.DbHelper(DbManager.getDefaultHelper(context))
                    .findBySubject(_subject);
            LayoutInflater lf = _dialog.getLayoutInflater();
            for (int i = units.size() - 1; i >= 0; i--) {
                final LearningUnitInfo unit = units.get(i);
                Chip ch = (Chip) lf.inflate(R.layout.elements_unit_choose_chip, _unitChips, false);
                ch.setText(unit.getName());
                ch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (_selectedUnitIds.contains(unit.getId())) {
                    ch.setChecked(true);
                } else {
                    ch.setChecked(false);
                }
                _unitChips.addView(ch, 0, new ChipGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        _selectedUnitIds.add(unit.getId());
                    } else {
                        _selectedUnitIds.remove((Integer) unit.getId());

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
        _selectedUnitIds.clear();
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

    public List<Integer> get_selectedUnitIds() {
        return _selectedUnitIds;
    }

    public void set_selectedUnitIds(List<Integer> _selectedUnitIds) {
        this._selectedUnitIds = _selectedUnitIds;
    }

    public Runnable get_onUpdate() {
        return _onUpdate;
    }

    public void set_onUpdate(Runnable _onUpdate) {
        this._onUpdate = _onUpdate;
    }

    public boolean isFilterActive() {
        return (_searchTxt != null && !_searchTxt.isEmpty()) || (_selectedUnitIds.size() != 0) || (_selectedType.size() != 0) || _isHiddenShown;
    }

    public List<QuestionType> get_selectedType() {
        return _selectedType;
    }

    public void set_selectedType(List<QuestionType> _selectedType) {
        this._selectedType = _selectedType;
    }
}
