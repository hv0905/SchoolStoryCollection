package sakuratrak.schoolstorycollection;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView _mTextMessage;
    private RecyclerView _itemList;
    private FloatingActionButton _addItemBtn;

    private ConstraintLayout _workbookLayout;
    private ConstraintLayout _quizLayout;
    private ConstraintLayout _settingLayout;
    private BottomNavigationView _navigation;
    private Button _tempbtn;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    _workbookLayout.setVisibility(View.VISIBLE);
                    _quizLayout.setVisibility(View.INVISIBLE);
                    _settingLayout.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    _workbookLayout.setVisibility(View.INVISIBLE);
                    _quizLayout.setVisibility(View.VISIBLE);
                    _settingLayout.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_notifications:
                    _workbookLayout.setVisibility(View.INVISIBLE);
                    _quizLayout.setVisibility(View.INVISIBLE);
                    _settingLayout.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        _mTextMessage = findViewById(R.id.message);
        _navigation = findViewById(R.id.bottomNav);
        _addItemBtn = findViewById(R.id.addItemBtn);
        _itemList = findViewById(R.id.itemList);
        _workbookLayout = findViewById(R.id.workbookLayout);
        _quizLayout = findViewById(R.id.quizLayout);
        _settingLayout = findViewById(R.id.settingLayout);
        _tempbtn = findViewById(R.id.tempbtn);

        _navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        _addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to add activity
                //Snackbar.make(v,"Add button clicked!!!",2000).show();
                CommonAlerts.AskQuestionType(MainActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        CommonAlerts.Dialog2QuestionType(i);
                    }
                });

                CommonAlerts.AskSubjectType(MainActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
            }

        });

        _tempbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,QuestionDetailActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        ArrayList<String> lstr = new ArrayList<>();
        for (int i = 0;i<100;i++){
            lstr.add(String.format("item %d",i));
        }

        QuestionItemAdapter qia = new QuestionItemAdapter(lstr);
        _itemList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        _itemList.setAdapter(qia);


    }

}
