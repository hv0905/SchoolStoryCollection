package sakuratrak.schoolstorycollection;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    private TextView _mTextMessage;
    private RecyclerView _itemList;
    private FloatingActionButton _addItemBtn;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //_mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    //_mTextMessage.setText(R.string.title_quiz);
                    return true;
                case R.id.navigation_notifications:
                    //_mTextMessage.setText(R.string.title_settings);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        _addItemBtn = findViewById(R.id.addItemBtn);
        _addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to add activity
                //Snackbar.make(v,"Add button clicked!!!",2000).show();
                AlertDialog ad = new AlertDialog.Builder(MainActivity.this).setTitle("选择题目类型")
                        .setItems(new String[]{"单选题", "多选题", "填空题","解答题"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
//                        .setPositiveButton("完成", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
                        .show();

            }

        });
        _itemList = findViewById(R.id.itemList);

        ArrayList<String> lstr = new ArrayList<>();
        for (int i = 0;i<100;i++){
            lstr.add(String.format("item %d",i));
        }

        QuestionItemAdapter qia = new QuestionItemAdapter(lstr);
        _itemList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        _itemList.setAdapter(qia);


    }

}
