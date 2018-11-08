package sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;

import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.LearningSubject;
import sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import sakuratrak.schoolstorycollection.core.QuestionInfo;
import sakuratrak.schoolstorycollection.core.QuestionType;

public class QuestionEditActivity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT = "subject";
    public static final String EXTRA_QUESTION_TYPE_ID = "question_type_id";

    private static final int REQUEST_UNIT_CHOOSE = 200;
    private static final int REQUEST_IMAGE_CAMERA_QUESTION = 201;
    private static final int REQUEST_IMAGE_GET_QUESTION = 202;
    private static final int REQUEST_IMAGE_EDIT_QUESTION = 203;
    private static final int REQUEST_IMAGE_CAMERA_ANSWER = 204;
    private static final int REQUEST_IMAGE_GET_ANSWER = 205;
    private static final int REQUEST_IMAGE_EDIT_ANSWER = 206;
    private static final int REQUEST_IMAGE_CAMERA_ANALYSIS = 207;
    private static final int REQUEST_IMAGE_GET_ANALYSIS = 208;
    private static final int REQUEST_IMAGE_EDIT_ANALYSIS = 209;

    private LearningSubject _currentSubject;
    private LearningUnitInfo _unit;
    private QuestionType _questionType;
    private File _currentTempCameraPhoto;
    private File _currentTargetPhoto;
    private QuestionInfo _context;

    //region views
    private View _root;
    private View _unitSelectLayout;
    private TextView _unitText;
    private FrameLayout _answerContainer;
    private View _answerContent;
    private ImageSelectView _questionImgRecycle;
    private ImageSelectView _analysisImgRecycle;
    //endregion

    private ImageListEditAdapter _questionImgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = QuestionInfo.create();
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
        _analysisImgRecycle = findViewById(R.id.analysisImgRecycle);


        _questionImgRecycle.setNestedScrollingEnabled(false);
        _questionImgRecycle.setCodes(REQUEST_IMAGE_CAMERA_QUESTION, REQUEST_IMAGE_GET_QUESTION, REQUEST_IMAGE_EDIT_QUESTION);

        _analysisImgRecycle.setNestedScrollingEnabled(false);
        _analysisImgRecycle.setCodes(REQUEST_IMAGE_CAMERA_ANALYSIS, REQUEST_IMAGE_GET_ANALYSIS, REQUEST_IMAGE_EDIT_ANALYSIS);


        _unitSelectLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LearningUnitChoosingActivity.class);
            intent.putExtra(LearningUnitChoosingActivity.EXTRA_SUBJECT, _currentSubject);
            intent.putExtra(LearningUnitChoosingActivity.EXTRA_SHOW_NONE, true);
            startActivityForResult(intent, REQUEST_UNIT_CHOOSE);
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
                _answerContent = LayoutInflater.from(this).inflate(R.layout.element_answer_define_answer, _answerContainer);
                break;
            case ANSWER:
                _answerContent = LayoutInflater.from(this).inflate(R.layout.element_answer_define_answer, _answerContainer);
                break;
        }
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

                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        _questionImgRecycle.onActivityResult(requestCode, resultCode, data);
        _analysisImgRecycle.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_UNIT_CHOOSE:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    if (data.getBooleanExtra(LearningUnitChoosingActivity.RESULT_SELECTED, false)) {
                        try {
                            _unit = DbManager.getHelper(this).getLearningUnitInfos().queryForId(data.getIntExtra(LearningUnitChoosingActivity.RESULT_UNIT_ID, 0));
                            _unitText.setText(_unit.getName());
                        }catch (java.sql.SQLException e) {
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        _questionImgRecycle.onRequestPermissionsResult(requestCode, permissions, grantResults);
        _analysisImgRecycle.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
