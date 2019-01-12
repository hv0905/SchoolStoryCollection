package net.sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningSubject;
import net.sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;
import net.sakuratrak.schoolstorycollection.core.QuestionType;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class QuestionEditActivity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT = "subject";
    public static final String EXTRA_QUESTION_TYPE_ID = "question_type_id";
    public static final String EXTRA_CONTEXT_ID = "context";
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
    private RatingBar _difficultyEdit;
    private Toolbar _toolbar;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_edit);

        _isEdit = getIntent().hasExtra(EXTRA_CONTEXT_ID);
        if (_isEdit) {
            //edit
            try {
                _context = DbManager.getDefaultHelper(this).getQuestionInfos().queryForId(getIntent().getIntExtra(EXTRA_CONTEXT_ID, 0));
                _questionType = _context.getType();
                _currentSubject = _context.getSubject();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.sqlExp, Toast.LENGTH_LONG).show();
            }
        } else {
            _questionType = QuestionType.id2Obj(getIntent().getIntExtra(EXTRA_QUESTION_TYPE_ID, 0));
            _currentSubject = (LearningSubject) getIntent().getSerializableExtra(EXTRA_SUBJECT);
            //new
        }


        //set answer content
        _answerContent = _questionType.getCreatorView(this);

        if (_answerContent instanceof ImageAnswerCreateView) {
            ((ImageAnswerCreateView) _answerContent).getAnswerImage().setCodes(REQUEST_IMAGE_CAMERA_ANSWER, REQUEST_IMAGE_EDIT_ANSWER, REQUEST_IMAGE_GET_ANSWER);
        }

        //region get views
        _editTitle = findViewById(R.id.editTitle);
        _editSource = findViewById(R.id.editSource);
        _editAnalysisInfo = findViewById(R.id.editAnalysisInfo);
        _editQuestionInfo = findViewById(R.id.editQuestionInfo);
        _unitSelectLayout = findViewById(R.id.unitSelectLayout);
        _unitText = findViewById(R.id.textUnit);
        _answerContainer = findViewById(R.id.answerContainer);
        _questionImgRecycle = findViewById(R.id.questionImgRecycle);
        _analysisImgRecycle = findViewById(R.id.analysisImgRecycle);
        _difficultyEdit = findViewById(R.id.difficultyEdit);
        _toolbar = findViewById(R.id.toolbar);
        //endregion

        //region toolbar configure
        int uiColor = UiHelper.getFlatUiColor(this, _currentSubject.getId());
        getWindow().setStatusBarColor(uiColor);
        _toolbar.setBackgroundColor(uiColor);
        setSupportActionBar(_toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }
        //endregion


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
            _questionImgRecycle.setImages(Arrays.asList(_context.getQuestionImage()));
            _analysisImgRecycle.setImages(Arrays.asList(_context.getAnalysisImage()));
            _unit = _context.getUnit();
            if (_unit != null)
                _unitText.setText(_unit.getName());
            _difficultyEdit.setRating(_context.getDifficulty() / 2f);

        } else {
            _difficultyEdit.setRating(2.5f);
        }

        _analysisImgRecycle.setOnItemToggleListener(v -> checkState());
        _questionImgRecycle.setOnItemToggleListener(v -> checkState());
        _answerContent.setOnUpdateEventHandler(sender -> checkState());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_activity_menu, menu);
        _okButton = menu.findItem(R.id.ok);
        checkState();
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                if (_context == null) _context = new QuestionInfo(_currentSubject, _questionType);
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
                _context.setDifficulty((int) (_difficultyEdit.getRating() * 2));

                //todo update the database
                try {
                    if (_isEdit) {
                        DbManager.getDefaultHelper(this).getQuestionInfos().update(_context);//更新
                    } else {
                        _context.setAuthorTime(new Date());
                        DbManager.getDefaultHelper(this).getQuestionInfos().create(_context);
                    }
                } catch (SQLException sql) {
                    sql.printStackTrace();
                    Snackbar.make(_editTitle, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
                    return true;
                }
                setResult(RESULT_OK);
                finish();
                return true;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        _questionImgRecycle.onActivityResult(requestCode, resultCode, data);
        _analysisImgRecycle.onActivityResult(requestCode, resultCode, data);
        if (_answerContent instanceof ImageAnswerCreateView) {
            ((ImageAnswerCreateView) _answerContent).getAnswerImage().onActivityResult(requestCode, resultCode, data);
        }
        switch (requestCode) {
            case REQUEST_UNIT_CHOOSE:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    if (data.getBooleanExtra(LearningUnitChoosingActivity.RESULT_SELECTED, false)) {
                        try {
                            _unit = DbManager.getDefaultHelper(this)
                                    .getLearningUnitInfos()
                                    .queryForId(data.getIntExtra(LearningUnitChoosingActivity.RESULT_UNIT_ID, 0));
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
        if (_answerContent instanceof ImageAnswerCreateView) {
            ((ImageAnswerCreateView) _answerContent)
                    .getAnswerImage()
                    .onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void checkState() {
        Log.d(TAG, "checkState: updated. need to check state");
        _okButton.setEnabled(_answerContent.hasAnswer() && _questionImgRecycle.getImages().size() != 0);
    }


}
