package sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.sql.SQLException;

import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.LearningSubject;
import sakuratrak.schoolstorycollection.core.LearningUnitInfo;

public class NewQuestionActivity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT = "subject";

    private static final int INTENT_UNIT_CHOOSE = 1;

    private LearningSubject _currentSubject;
    private LearningUnitInfo _unit;

    //region views
    private View _unitSelectLayout;
    private TextView _unitText;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);
        _currentSubject = (LearningSubject)getIntent().getSerializableExtra(EXTRA_SUBJECT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        _unitSelectLayout = findViewById(R.id.unitSelectLayout);
        _unitText = findViewById(R.id.textUnit);

        _unitSelectLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this,LearningUnitChoosingActivity.class);
            intent.putExtra(LearningUnitChoosingActivity.EXTRA_SUBJECT,_currentSubject);
            intent.putExtra(LearningUnitChoosingActivity.EXTRA_SHOW_NONE,true);
            startActivityForResult(intent,INTENT_UNIT_CHOOSE);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ok:
                //todo ok...
                finish();
                return true;
            case android.R.id.home:
                finish();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case INTENT_UNIT_CHOOSE:
                if(resultCode == RESULT_OK){
                    if(data.getBooleanExtra(LearningUnitChoosingActivity.RESULT_SELECTED,false)) {
                        try {
                            _unit = DbManager.getHelper(this).getLearningUnitInfos().queryForId(data.getIntExtra(LearningUnitChoosingActivity.RESULT_UNIT_ID,0));
                            _unitText.setText(_unit.getName());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }else{
                        _unit = null;
                        _unitText.setText(R.string.emptyUnit);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
