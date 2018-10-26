package sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.LearningSubject;
import sakuratrak.schoolstorycollection.core.LearningUnitInfo;

public class LearningUnitChoosingActivity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT = "subject";
    public static final String EXTRA_SHOW_NONE = "showNone";

    public static final String RESULT_SELECTED = "selected";
    public static final String RESULT_UNIT_ID = "unitId";

    public LearningSubject _currentSubject = LearningSubject.OTHER;
    public boolean _showNone;

    //region views
    ListView _listMain;
    LinearLayout _unitEmptyNotice;
    List<LearningUnitInfo> _info;
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _currentSubject = (LearningSubject) getIntent().getSerializableExtra(EXTRA_SUBJECT);
        _showNone = getIntent().getBooleanExtra(EXTRA_SHOW_NONE, false);
        setContentView(R.layout.activity_learning_unit_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        _listMain = findViewById(R.id.listMain);
        _unitEmptyNotice = findViewById(R.id.unitEmptyNotice);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            final EditText et = new EditText(this);
            et.setSingleLine();
            AlertDialog.Builder ab = new AlertDialog.Builder(this).setIcon(R.drawable.ic_book_black_24dp).setTitle(R.string.createUnit)
                    .setMessage(R.string.unitName)
                    .setView(et).setNegativeButton(R.string.finish, (dialog, which) -> {
                        if (et.getText().toString().trim().isEmpty()) {
                            new AlertDialog.Builder(this).setMessage(R.string.plsInputUnitName).setTitle(R.string.error).setNegativeButton(R.string.confirm, null).setIcon(R.drawable.ic_warning_black_24dp).show();
                            return;
                        }
                        try {
                            DbManager.getHelper(this).getLearningUnitInfos().create(new LearningUnitInfo(et.getText().toString().trim(), _currentSubject));
                            DbManager.releaseHelper();
                        } catch (SQLException e) {
                            Snackbar.make(_listMain, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        refreshUnit();
                    })
                    .setPositiveButton(R.string.cancel, null);

            ab.show();
        });

        _listMain.setOnItemClickListener((parent, view, position, id) -> {
            if (_showNone) {
                if (position == 0) {
                    getIntent().putExtra(RESULT_SELECTED, false);
                } else {
                    getIntent().putExtra(RESULT_SELECTED, true);
                    getIntent().putExtra(RESULT_UNIT_ID, _info.get(position - 1).getId());
                }
            }else{
                getIntent().putExtra(RESULT_SELECTED, true);
                getIntent().putExtra(RESULT_UNIT_ID,_info.get(position).getId());
            }
            setResult(RESULT_OK,getIntent());
            finish();

        });


        refreshUnit();
    }

    private void refreshUnit() {

        try {
            _info = DbManager.getHelper(this).getLearningUnitInfos().queryForEq("subjectId", _currentSubject.getId());
            DbManager.releaseHelper();
        } catch (SQLException e) {
            Snackbar.make(_listMain, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (_info.size() == 0) {
            //empty
            _unitEmptyNotice.setVisibility(View.VISIBLE);
            return;
        } else {
            _unitEmptyNotice.setVisibility(View.INVISIBLE);
        }
        ArrayList<String> display = new ArrayList<>();
        if (_showNone) {
            display.add(getString(R.string.emptyUnit));
        }
        for (LearningUnitInfo item : _info) {
            display.add(item.getName());
        }
        _listMain.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, display));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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
