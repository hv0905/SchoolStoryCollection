package sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.sql.SQLException;

import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.LearningSubject;
import sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import sakuratrak.schoolstorycollection.core.QuestionInfo;
import sakuratrak.schoolstorycollection.core.QuestionType;

public class QuestionEditActivity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT = "subject";
    public static final String EXTRA_QUESTION_TYPE_ID = "question_type_id";
    public static final String EXTRA_CONTEXT = "context";
    public static final String TAG = "QuestionEditActivity";

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
    private QuestionInfo _context;
    private boolean _isEdit = false;

    //region views
    private TextInputEditText _editTitle;
    private TextInputEditText _editSource;
    private TextInputEditText _editQuestionInfo;
    private TextInputEditText _editAnalysisInfo;

    private View _unitSelectLayout;
    private TextView _unitText;
    private FrameLayout _answerContainer;
    private AnswerUiCreatorView _answerContent;
    private ImageSelectView _questionImgRecycle;
    private ImageSelectView _analysisImgRecycle;
    private MenuItem _okButton;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_edit);
        _currentSubject = (LearningSubject) getIntent().getSerializableExtra(EXTRA_SUBJECT);
        _questionType = QuestionType.id2Obj(getIntent().getIntExtra(EXTRA_QUESTION_TYPE_ID, 0));
        _isEdit = getIntent().hasExtra(EXTRA_CONTEXT);
        if (_isEdit) {
            //edit
            _context = (QuestionInfo) getIntent().getSerializableExtra(EXTRA_CONTEXT);
        } else {
            //new
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }

        //set answer content
        switch (_questionType) {
            case SINGLE_CHOICE:
                _answerContent = new SingleSelectCreateView(this);
                break;
            case MULTIPLY_CHOICE:
                _answerContent = new MultiSelectCreateView(this);
                break;
            case TYPEABLE_BLANK:
                _answerContent = new TextAnswerCreateView(this);
                break;
            case BLANK:
                AnswerAnswerCreateView answerAnswerCreateView1 = new AnswerAnswerCreateView(this);
                answerAnswerCreateView1.setNoticeText(R.string.fillAnswer);
                answerAnswerCreateView1.getAnswerImage().setCodes(REQUEST_IMAGE_CAMERA_ANSWER, REQUEST_IMAGE_EDIT_ANSWER, REQUEST_IMAGE_GET_ANSWER);
                _answerContent = answerAnswerCreateView1;
                break;
            case ANSWER:
                AnswerAnswerCreateView answerAnswerCreateView2 = new AnswerAnswerCreateView(this);
                answerAnswerCreateView2.setNoticeText(R.string.answerQuestionAnswer);
                answerAnswerCreateView2.getAnswerImage().setCodes(REQUEST_IMAGE_CAMERA_ANSWER, REQUEST_IMAGE_EDIT_ANSWER, REQUEST_IMAGE_GET_ANSWER);
                _answerContent = answerAnswerCreateView2;
                break;
        }


        _editTitle = findViewById(R.id.editTitle);
        _editSource = findViewById(R.id.editSource);
        _editAnalysisInfo = findViewById(R.id.editAnalysisInfo);
        _editQuestionInfo = findViewById(R.id.editQuestionInfo);
        _unitSelectLayout = findViewById(R.id.unitSelectLayout);
        _unitText = findViewById(R.id.textUnit);
        _answerContainer = findViewById(R.id.answerContainer);
        _questionImgRecycle = findViewById(R.id.questionImgRecycle);
        _analysisImgRecycle = findViewById(R.id.analysisImgRecycle);

        _answerContainer.addView(_answerContent);

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


        if (_context != null) {
            //load
            _editTitle.setText(_context.getTitle());
            _editSource.setText(_context.getSource());
            _editQuestionInfo.setText(_context.getQuestionDetail());
            _editAnalysisInfo.setText(_context.getAnalysisDetail());
            _answerContent.setAnswer(_context.getAnswer());

        }

        _analysisImgRecycle.setOnItemToggleListener(v -> checkState());
        _questionImgRecycle.setOnItemToggleListener(v -> checkState());
        _answerContent.setOnUpdateEventHandler(sender -> checkState());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_activity_menu, menu);
        _okButton = menu.findItem(R.id.ok);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                if(_context == null) _context = new QuestionInfo(_currentSubject,_questionType);
                //todo save
                if (_editTitle.getText().toString().trim().isEmpty()) {
                    _context.setTitle("some question ~");
                } else {
                    _context.setTitle(_editTitle.getText().toString());
                }
                if (_editSource.getText().toString().trim().isEmpty()) {
                    _context.setSource("somewhere ~");
                } else {
                    _context.setSource(_editSource.getText().toString());
                }
                _context.setUnit(_unit);
                _context.setQuestionDetail(_editQuestionInfo.getText().toString());
                _context.setAnalysisDetail(_editAnalysisInfo.getText().toString());
                _context.setQuestionImage(_questionImgRecycle.getImages());
                _context.setAnalysisImage(_analysisImgRecycle.getImages());
                _context.setAnswer(_answerContent.getAnswer());

                //todo update the database
                try {
                    if (_isEdit) {
                        DbManager.getDefaultHelper(this).getQuestionInfos().update(_context);//更新
                    } else {
                        DbManager.getDefaultHelper(this).getQuestionInfos().create(_context);
                    }
                }catch (SQLException sql){
                    sql.printStackTrace();
                    Snackbar.make(_editTitle, R.string.sqlExp,Snackbar.LENGTH_LONG).show();
                    return true;
                }
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
        if (_answerContent instanceof AnswerAnswerCreateView) {
            ((AnswerAnswerCreateView) _answerContent).getAnswerImage().onActivityResult(requestCode, resultCode, data);
        }
        switch (requestCode) {
            case REQUEST_UNIT_CHOOSE:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    if (data.getBooleanExtra(LearningUnitChoosingActivity.RESULT_SELECTED, false)) {
                        try {
                            _unit = DbManager.getDefaultHelper(this).getLearningUnitInfos().queryForId(data.getIntExtra(LearningUnitChoosingActivity.RESULT_UNIT_ID, 0));
                            _unitText.setText(_unit.getName());
                        } catch (java.sql.SQLException e) {
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
        if (_answerContent instanceof AnswerAnswerCreateView) {
            ((AnswerAnswerCreateView) _answerContent).getAnswerImage().onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void checkState() {
        Log.d(TAG, "checkState: updated. need to check state");
        _okButton.setEnabled(_answerContent.hasAnswer() && _questionImgRecycle.getImages().size() != 0);
    }


}
