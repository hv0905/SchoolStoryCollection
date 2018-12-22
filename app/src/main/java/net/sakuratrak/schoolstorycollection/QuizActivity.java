package net.sakuratrak.schoolstorycollection;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zzhoujay.markdown.MarkDown;

import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;

import java.sql.SQLException;

public class QuizActivity extends AppCompatActivity {

    public static final int MODE_SOLO = 1;
    public static final int MODE_LIST = 2;

    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_QUESTION_ID = "id";

    public static final String TAG = "QuizActivity";

    Toolbar _toolbar;
    TextView _questionCounter;
    TextView _questionName;
    TextView _textQuestionType;
    TextView _textQuestionInfo;
    ImageDisplayView _questionImg;
    FrameLayout _answerContainer;
    QuizAnswerView _answerContent;
    BottomSheetBehavior<FrameLayout> _behavior;


    int _counter = 0;
    int _mode;

    QuestionInfo _currentContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        _toolbar = findViewById(R.id.toolbar);
        _questionCounter = findViewById(R.id.questionCounter);
        _questionName = findViewById(R.id.questionName);
        _textQuestionType = findViewById(R.id.textQuestionType);
        _textQuestionInfo = findViewById(R.id.textQuestionInfo);
        _questionImg = findViewById(R.id.questionImg);
        _answerContainer = findViewById(R.id.answerContainer);

        setSupportActionBar(_toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra(EXTRA_MODE)){
            _mode = getIntent().getIntExtra(EXTRA_MODE,0);
        }else finish();

        if(_mode == MODE_SOLO){
            try {
                _currentContext = DbManager.getDefaultHelper(this).getQuestionInfos().queryForId(getIntent().getIntExtra(EXTRA_QUESTION_ID,0));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        setupQuestion();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.skip:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz_options,menu);
        return true;
    }

    public void setupQuestion(){
        int uiColor = UiHelper.getFlatUiColor(this,_currentContext.getSubject().getId());
        _toolbar.setBackgroundColor(uiColor);
        _answerContainer.setBackgroundColor(uiColor);

        _questionName.setText(_currentContext.getTitle());
        _questionCounter.setText(String.valueOf(++_counter));
        _textQuestionType.setText(_currentContext.getType().getTitleId());
        _textQuestionInfo.post(() -> {
            Spanned sp = MarkDown.fromMarkdown(_currentContext.getQuestionDetail(),null,_textQuestionInfo);
            _textQuestionInfo.setText(sp);
        });
        _questionImg.setImages(_currentContext.getQuestionImage());

        _answerContent = _currentContext.getType().getQuizAnswerView(this);
        _answerContent.setOnAnswerReport((sender, status) -> {
            Log.d(TAG, "setupQuestion: ok!!!");
        });
        FrameLayout.LayoutParams ctLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        int dp = (int) getResources().getDimension(R.dimen.ui_margin_mid);
        ctLp.setMargins(dp,dp,dp,dp);
        _answerContent.setLayoutParams(ctLp);
        _answerContainer.removeAllViews();
        _answerContainer.addView(_answerContent);

    }
}