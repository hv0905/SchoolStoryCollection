package sakuratrak.schoolstorycollection;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import sakuratrak.schoolstorycollection.core.LearningSubject;
import sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import sakuratrak.schoolstorycollection.core.LearningUnitStorageFile;
import sakuratrak.schoolstorycollection.core.QuestionType;

public class MainActivity extends AppCompatActivity {

    //region ui_control
    private TextView _mTextMessage;
    private RecyclerView _itemList;
    private RecyclerView _unitList;
    private FloatingActionButton _addItemBtn;
    private FloatingActionButton _addUnitBtn;
    private AppCompatSpinner _subjectSpinner;
    private ConstraintLayout _workbookLayout;
    private ConstraintLayout _quizLayout;
    private ConstraintLayout _unitLayout;
    private ConstraintLayout _settingLayout;
    private BottomNavigationView _navigation;
    private Button _unitManageBtn;
    private Button _tempbtn;
    private Toolbar _toolbar;
    //endregion

    //region events
    View.OnClickListener unitClearLogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
            ad.setTitle("清空记录确认").setIcon(R.drawable.ic_warning_black_24dp).setMessage(String.format("%s的所有做题记录和统计信息将清空!\n确定要从零开始吗?", "NAME"));
            ad.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //clean log
                }
            });
        }
    };

    View.OnClickListener unitRmListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
            ad.setTitle("删除确认").setIcon(R.drawable.ic_warning_black_24dp).setMessage(String.format("%s将永久失去(真的很久!)!", "NAME"));
            ad.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            ad.show();
        }
    };

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //region loadUI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //endregion


        //region get UI elements
        _mTextMessage = findViewById(R.id.message);
        _navigation = findViewById(R.id.bottomNav);
        _addItemBtn = findViewById(R.id.addItemBtn);
        _itemList = findViewById(R.id.itemList);
        _unitList = findViewById(R.id.unitList);
        _addUnitBtn = findViewById(R.id.addUnitBtn);
        _workbookLayout = findViewById(R.id.workbookLayout);
        _quizLayout = findViewById(R.id.quizLayout);
        _unitLayout = findViewById(R.id.unitLayout);
        _settingLayout = findViewById(R.id.settingLayout);
        _tempbtn = findViewById(R.id.tempbtn);
        _unitManageBtn = findViewById(R.id.main_settings_unitManageBtn);
        _subjectSpinner = findViewById(R.id.subjectSpinner);
        _toolbar = findViewById(R.id.toolbar);
        //endregion

        //设置工具栏
        setSupportActionBar(_toolbar);

        //region Events

        _navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                _workbookLayout.setVisibility(View.INVISIBLE);
                _quizLayout.setVisibility(View.INVISIBLE);
                _unitLayout.setVisibility(View.INVISIBLE);
                _settingLayout.setVisibility(View.INVISIBLE);
                _subjectSpinner.setVisibility(View.VISIBLE);
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        _workbookLayout.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.navigation_dashboard:
                        _quizLayout.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.navigation_settings:
                        _settingLayout.setVisibility(View.VISIBLE);
                        _subjectSpinner.setVisibility(View.INVISIBLE);
                        return true;
                    case R.id.navigation_unit:
                        _unitLayout.setVisibility(View.VISIBLE);
                        return true;
                }
                return false;
            }
        });


        _addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to add activity
                //Snackbar.make(v,"Add button clicked!!!",2000).show();
                CommonAlerts.AskQuestionType(MainActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        final QuestionType type = QuestionType.id2Obj(i);
                        //System.out.println(type.toString());
                        CommonAlerts.AskSubjectType(MainActivity.this, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                final LearningSubject sub = LearningSubject.id2Obj(i);
                                //System.out.println(sub.toString());
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });

        _tempbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, QuestionDetailActivity.class);
//                MainActivity.this.startActivity(intent);
                getCurrectSubject();
            }
        });


        _unitManageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择科目
                CommonAlerts.AskSubjectType(MainActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //继续
                        LearningSubject sub = LearningSubject.id2Obj(which);
                        Intent in = new Intent(MainActivity.this, LearningUnitManageActivity.class);
                        in.putExtra("subject", sub);
                        MainActivity.this.startActivity(in);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });


        _addUnitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final EditText et = new EditText(MainActivity.this);
                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this).setIcon(R.drawable.ic_book_black_24dp).setTitle("创建单元")
                        .setMessage("单元名称:")
                        .setView(et).setNegativeButton("完成", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                System.out.println(et.getText());
                                ArrayList<LearningUnitInfo> lisf =  LearningUnitStorageFile.getDefault().getUnits(getCurrectSubject());
                                if(lisf == null) lisf = new ArrayList<>();
                                lisf.add(new LearningUnitInfo(et.getText().toString()));
                                LearningUnitStorageFile.getDefault().setUnits(getCurrectSubject(),lisf);
                                refreshUnit();
                                try {
                                    LearningUnitStorageFile.getDefault().saveToInternalStorage(MainActivity.this);
                                }catch (IOException io){
                                    Snackbar.make(v,"错误:无法保存单元文件",Snackbar.LENGTH_LONG).show();
                                }

                            }
                        })
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                ab.show();
            }
        });

        _subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //load unit
                refreshUnit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //ignored
            }
        });

        //endregion


        //region init recycler
        _itemList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        _unitList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ArrayAdapter<CharSequence> subjectDropdown = ArrayAdapter.createFromResource(this, R.array.learning_subjects, R.layout.layout_spinner_item);
        subjectDropdown.setDropDownViewResource(R.layout.layout_spinner_dropdown);

        ArrayList<String> lstr = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            lstr.add(String.format("item %d", i));
        }

        _subjectSpinner.setAdapter(subjectDropdown);

        QuestionItemAdapter qia = new QuestionItemAdapter(lstr);
        _itemList.setAdapter(qia);
        refreshUnit();



        //endregion



//        _unitList.setAdapter(uda);

    }

    //从顶部组合框中获取目前选中的科目
    public LearningSubject getCurrectSubject() {
        return LearningSubject.id2Obj(_subjectSpinner.getSelectedItemPosition());
    }

    public void refreshUnit(){
        if(!LearningUnitStorageFile.defaultLoaded())
        {
            LearningUnitStorageFile defaults = LearningUnitStorageFile.readFromInternalStorage(this);
            if(defaults == null){
                defaults = new LearningUnitStorageFile();
            }
            LearningUnitStorageFile.setDefault(defaults);
        }
        ArrayList<LearningUnitInfo> luis = LearningUnitStorageFile.getDefault().getUnits(getCurrectSubject());
        if(luis == null)
            luis = new ArrayList<>();
        ArrayList<UnitDisplayAdapter.UnitDisplayInfo> udi = new ArrayList<>();
        for (LearningUnitInfo item : luis){
            UnitDisplayAdapter.UnitDisplayInfo udiItem = new UnitDisplayAdapter.UnitDisplayInfo(item.ExerciseLogs.size(),50,item.Name);
            udi.add(udiItem);
        }
        UnitDisplayAdapter uda = new UnitDisplayAdapter(udi);
        _unitList.setAdapter(uda);
    }

}
