package net.sakuratrak.schoolstorycollection;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningUnitInfo;

import java.sql.SQLException;

public final class UnitDetailDialog extends BottomSheetDialogFragment {

    LearningUnitInfo _context;

    ViewGroup _root;
    TextView _title;
    MaterialButton _closeBtn;
    MaterialButton _resetBtn;
    MaterialButton _rmBtn;
    ScrollView _scrollMain;

    //public boolean _edited = false;

    public UnitDetailDialog() {
        super();
    }

    @SuppressLint("ValidFragment")
    public UnitDetailDialog(LearningUnitInfo context) {
        super();
        _context = context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _root = (ViewGroup) inflater.inflate(R.layout.fragnment_unit_detail, container, false);
        _title = _root.findViewById(R.id.title);
        _closeBtn = _root.findViewById(R.id.closeBtn);
        _resetBtn = _root.findViewById(R.id.resetBtn);
        _rmBtn = _root.findViewById(R.id.rmBtn);
        _scrollMain = _root.findViewById(R.id.scrollMain);

        _closeBtn.setOnClickListener(v -> dismiss());

        _resetBtn.setOnClickListener(v -> {
        AlertDialog.Builder ad = new AlertDialog.Builder(getParent());
        ad.setTitle(R.string.confirmLog_title).setIcon(R.drawable.ic_warning_black_24dp).setMessage(String.format(getString(R.string.confirmLog_msg), _context.getName()));
        ad.setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.confirm, (dialog, which) -> {

        });
        ad.show();
        });

        _rmBtn.setOnClickListener(v -> {
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setTitle(R.string.confirmRm_title).setIcon(R.drawable.ic_warning_black_24dp).setMessage(String.format(getString(R.string.confirmRm_msg), _context.getName()));
            ad.setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.confirm, (dialog, which) -> {
                try {
                    DbManager.getDefaultHelper(getContext()).getLearningUnitInfos().delete(_context);
                } catch (SQLException e) {
                    Snackbar.make(_root, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
                    return;
                }
                getParent().requireRefresh();
                dismiss();
            });
            ad.show();
        });

        refresh();
        return _root;
    }

    void refresh() {
        _title.setText(_context.getName());
    }

    MainActivity getParent(){
        return (MainActivity)getActivity();
    }
}
