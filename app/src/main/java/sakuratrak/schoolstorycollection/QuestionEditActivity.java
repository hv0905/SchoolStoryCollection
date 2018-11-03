package sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;

import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.LearningSubject;
import sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import sakuratrak.schoolstorycollection.core.QuestionType;

public class QuestionEditActivity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT = "subject";
    public static final String EXTRA_QUESTION_TYPE_ID = "question_type_id";

    private static final int INTENT_UNIT_CHOOSE = 1;

    private LearningSubject _currentSubject;
    private LearningUnitInfo _unit;
    private QuestionType _questionType;

    //region views
    private View _unitSelectLayout;
    private TextView _unitText;
    private FrameLayout _answerContainer;
    private View _answerContent;
    private RecyclerView _questionImgRecycle;


    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_edit);
        _currentSubject = (LearningSubject) getIntent().getSerializableExtra(EXTRA_SUBJECT);
        _questionType = QuestionType.id2Obj(getIntent().getIntExtra(EXTRA_QUESTION_TYPE_ID, 0));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }
        _unitSelectLayout = findViewById(R.id.unitSelectLayout);
        _unitText = findViewById(R.id.textUnit);
        _answerContainer = findViewById(R.id.answerContainer);
        _questionImgRecycle = findViewById(R.id.questionImgRecycle);

        _unitSelectLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LearningUnitChoosingActivity.class);
            intent.putExtra(LearningUnitChoosingActivity.EXTRA_SUBJECT, _currentSubject);
            intent.putExtra(LearningUnitChoosingActivity.EXTRA_SHOW_NONE, true);
            startActivityForResult(intent, INTENT_UNIT_CHOOSE);
        });

        switch (_questionType) {
            case SINGLE_CHOICE:
                _answerContent = LayoutInflater.from(this).inflate(R.layout.element_answer_define_single_choice, _answerContainer);
                break;
            case MULTIPLY_CHOICE:
                _answerContent = LayoutInflater.from(this).inflate(R.layout.element_answer_define_multiply_choice, _answerContainer);
                break;
            case TYPEABLE_BLANK:
                _answerContent = LayoutInflater.from(this).inflate(R.layout.element_answer_define_fill, _answerContainer);
                break;
            case BLANK:
                _answerContent = LayoutInflater.from(this).inflate(R.layout.element_answer_define_single_choice, _answerContainer);
                break;
            case ANSWER:
                _answerContent = LayoutInflater.from(this).inflate(R.layout.element_answer_define_single_choice, _answerContainer);
                break;
        }
        _questionImgRecycle.setLayoutManager(new LinearLayoutManager(getParent(), LinearLayoutManager.VERTICAL, false));
        ImageListEditAdapter ilea = new ImageListEditAdapter(this,new ArrayList<>(),true);
        _questionImgRecycle.setAdapter(ilea);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        switch (requestCode) {
            case INTENT_UNIT_CHOOSE:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    if (data.getBooleanExtra(LearningUnitChoosingActivity.RESULT_SELECTED, false)) {
                        try {
                            _unit = DbManager.getHelper(this).getLearningUnitInfos().queryForId(data.getIntExtra(LearningUnitChoosingActivity.RESULT_UNIT_ID, 0));
                            _unitText.setText(_unit.getName());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        _unit = null;
                        _unitText.setText(R.string.emptyUnit);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
