package sakuratrak.schoolstorycollection;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import sakuratrak.schoolstorycollection.core.LearningSubject;
import sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import sakuratrak.schoolstorycollection.core.LearningUnitStorageFile;

import static sakuratrak.schoolstorycollection.core.LearningUnitStorageFile.getDefault;

public class LearningUnitChoosingActivity extends AppCompatActivity {

    public LearningSubject _currentSubject = LearningSubject.OTHER;

    //region views
    ListView _listMain;
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _currentSubject = (LearningSubject) getIntent().getSerializableExtra("subject");
        setContentView(R.layout.activity_learning_unit_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _listMain = findViewById(R.id.listMain);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            final EditText et = new EditText(this);
            et.setSingleLine();
            AlertDialog.Builder ab = new AlertDialog.Builder(this).setIcon(R.drawable.ic_book_black_24dp).setTitle("创建单元")
                    .setMessage("单元名称:")
                    .setView(et).setNegativeButton("完成", (dialog, which) -> {
                        if (et.getText().toString().trim().isEmpty()) {
                            new AlertDialog.Builder(this).setMessage("请输入单元名称").setTitle("错误").setNegativeButton("确定", null).setIcon(R.drawable.ic_warning_black_24dp).show();
                            return;
                        }
                        ArrayList<LearningUnitInfo> list = getDefault().getUnitsOrNew(_currentSubject);
                        list.add(new LearningUnitInfo(et.getText().toString().trim()));
                        getDefault().setUnits(_currentSubject, list);
                        refreshUnit();
                        try {
                            getDefault().saveToInternalStorage(this);
                        } catch (IOException io) {
                            Snackbar.make(view, R.string.failSaveUnitError, Snackbar.LENGTH_LONG).show();
                        }

                    })
                    .setPositiveButton("取消",null);

            ab.show();
        });

        refreshUnit();
    }

    private void refreshUnit() {
        ArrayList<LearningUnitInfo> info =  LearningUnitStorageFile.getDefault().getUnitsOrNew(_currentSubject);
        ArrayList<String> display = new ArrayList<>();
        for(LearningUnitInfo item : info){
            display.add(item.Name);
        }
        _listMain.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, display));


    }

}
