package net.sakuratrak.schoolstorycollection;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningSubject;
import net.sakuratrak.schoolstorycollection.core.LearningUnitInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LearningUnitChoosingActivity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT = "subject";
    public static final String EXTRA_SHOW_NONE = "showNone";

    public static final String RESULT_SELECTED = "selected";
    public static final String RESULT_UNIT_ID = "unitId";

    private LearningSubject _currentSubject;
    private boolean _showNone;

    //region views
    private ListView _listMain;
    private LinearLayout _unitEmptyNotice;
    private List<LearningUnitInfo> _info;
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _currentSubject = (LearningSubject) getIntent().getSerializableExtra(EXTRA_SUBJECT);
        _showNone = getIntent().getBooleanExtra(EXTRA_SHOW_NONE, false);
        setContentView(R.layout.activity_learning_unit_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        int uiColor = UiHelper.getFlatUiColor(this, _currentSubject.getId());
        getWindow().setStatusBarColor(uiColor);
        toolbar.setBackgroundColor(uiColor);

        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        _listMain = findViewById(R.id.listMain);
        _unitEmptyNotice = findViewById(R.id.unitEmptyNotice);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {

            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_book_black_24dp)
                    .setTitle(R.string.newUnitTitle)
                    .setView(R.layout.dialog_add_unit)
                    .setPositiveButton(R.string.done, (dialog, which) -> {
                        AlertDialog dg = (AlertDialog) dialog;
                        TextInputEditText tiet = dg.findViewById(R.id.txtUnitName);
                        if (tiet.getText() == null || tiet.getText().toString().trim().isEmpty()) {
                            new AlertDialog.Builder(getParent())
                                    .setMessage("请输入单元名称")
                                    .setTitle(R.string.error)
                                    .setNegativeButton(R.string.confirm, null)
                                    .setIcon(R.drawable.ic_warning_black_24dp)
                                    .show();
                            return;
                        }
                        try {
                            DbManager.getDefaultHelper(getParent()).getLearningUnitInfos().create(new LearningUnitInfo(tiet.getText().toString().trim(), _currentSubject));
                        } catch (SQLException e) {
                            Snackbar.make(_listMain, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        refreshUnit();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        _listMain.setOnItemClickListener((parent, view, position, id) -> {
            if (_showNone) {
                if (position == 0) {
                    getIntent().putExtra(RESULT_SELECTED, false);
                } else {
                    getIntent().putExtra(RESULT_SELECTED, true);
                    getIntent().putExtra(RESULT_UNIT_ID, _info.get(position - 1).getId());
                }
            } else {
                getIntent().putExtra(RESULT_SELECTED, true);
                getIntent().putExtra(RESULT_UNIT_ID, _info.get(position).getId());
            }
            setResult(RESULT_OK, getIntent());
            finish();

        });


        refreshUnit();
    }

    private void refreshUnit() {

        try {
            _info = DbManager.getDefaultHelper(this).getLearningUnitInfos().queryForEq("subjectId", _currentSubject.getId());
        } catch (SQLException e) {
            Snackbar.make(_listMain, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
            return;
        }
        ArrayList<String> display = new ArrayList<>();
        if (_showNone) {
            display.add(getString(R.string.emptyUnit));
        }
        for (LearningUnitInfo item : _info) {
            display.add(item.getName());
        }
        _listMain.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, display));
        if (_info.size() == 0) {
            //empty
            _unitEmptyNotice.setVisibility(View.VISIBLE);
        } else {
            _unitEmptyNotice.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
