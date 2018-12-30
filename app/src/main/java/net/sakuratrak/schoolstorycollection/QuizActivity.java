package net.sakuratrak.schoolstorycollection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.zzhoujay.markdown.MarkDown;

import net.sakuratrak.schoolstorycollection.core.Answer;
import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;
import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;

import java.sql.SQLException;

public class QuizActivity extends AppCompatActivity {

    public static final int MODE_SOLO = 1;
    public static final int MODE_LIST = 2;

    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_QUESTION_ID = "id";

    public static final String TAG = "QuizActivity";

    public static final int STATE_ANSWERING = 0;
    public static final int STATE_CHECKING = 1;
    public static final int STATE_POST_CHECKING = 2;


    Toolbar _toolbar;
    TextView _questionCounter;
    TextView _questionName;
    TextView _textQuestionType;
    TextView _questionText;
    ImageDisplayView _questionImgDisplay;
    FrameLayout _answerWorkZone;
    QuizAnswerView _quizAnswerContent;
    ExpandableLinearLayout _answerViewZone;
    LinearLayout _questionHolder;
    ScrollView _questionScroll;
    AnswerUiDisplayView _answerContent;
    TextView _analysisText;
    FrameLayout _answerContainer;
    ImageDisplayView _analysisImgDisplay;

    boolean _autoNext;


    int _counter = 0;
    int _mode;

    int _state;

    QuestionInfo _currentContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        _autoNext = AppSettingsMaster.getIfQuizAutoNext(this);

        _toolbar = findViewById(R.id.toolbar);
        _questionCounter = findViewById(R.id.questionCounter);
        _questionName = findViewById(R.id.questionName);
        _textQuestionType = findViewById(R.id.textQuestionType);
        _questionText = findViewById(R.id.questionText);
        _questionImgDisplay = findViewById(R.id.questionImgDisplay);
        _answerWorkZone = findViewById(R.id.answerWorkZone);
        _answerViewZone = findViewById(R.id.answerViewZone);
        _questionHolder = findViewById(R.id.questionHolder);
        _questionScroll = findViewById(R.id.questionScroll);
        _analysisText = findViewById(R.id.analysisText);
        _answerContainer = findViewById(R.id.answerContainer);
        _analysisImgDisplay = findViewById(R.id.analysisImgDisplay);

        setSupportActionBar(_toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(EXTRA_MODE)) {
            _mode = getIntent().getIntExtra(EXTRA_MODE, 0);
        } else finish();

        if (_mode == MODE_SOLO) {
            try {
                _currentContext = DbManager.getDefaultHelper(this).getQuestionInfos().queryForId(getIntent().getIntExtra(EXTRA_QUESTION_ID, 0));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        setupQuestion();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        getMenuInflater().inflate(R.menu.quiz_options, menu);
        return true;
    }

    public void setupQuestion() {
        int uiColor = UiHelper.getFlatUiColor(this, _currentContext.getSubject().getId());
        _toolbar.setBackgroundColor(uiColor);
        _answerWorkZone.setBackgroundColor(uiColor);

        _questionName.setText(_currentContext.getTitle());
        _questionCounter.setText(String.valueOf(++_counter));
        _textQuestionType.setText(_currentContext.getType().getTitleId());
        _questionText.post(() -> {
            Spanned sp = MarkDown.fromMarkdown(_currentContext.getQuestionDetail(), null, _questionText);
            _questionText.setText(sp);
        });
        _questionImgDisplay.setImages(_currentContext.getQuestionImage());

        _quizAnswerContent = _currentContext.getType().getQuizAnswerView(this);
        _quizAnswerContent.setOnAnswerReport((sender, status) -> {
            checkAnswer();
        });
        FrameLayout.LayoutParams ctLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dp = (int) getResources().getDimension(R.dimen.ui_margin_mid);
        ctLp.setMargins(dp, dp, dp, dp);
        _quizAnswerContent.setLayoutParams(ctLp);
        _answerWorkZone.removeAllViews();
        _answerWorkZone.addView(_quizAnswerContent);
        _state = STATE_ANSWERING;
    }

    public void checkAnswer() {
        Log.d(TAG, "checkAnswer: submit");
        if (_currentContext.getAnswer() instanceof Answer.PlainTextAnswer) {
            //收集用户答案并检查
            Answer.PlainTextAnswer userAnswer = ((CheckableQuizAnswerView) _quizAnswerContent).getAnswer();
            float score = ((Answer.PlainTextAnswer) _currentContext.getAnswer()).checkAnswer(userAnswer);
            //提交记录
            Log.d(TAG, "checkAnswer: score:" + score);
            postRecord(score);
            _state = STATE_POST_CHECKING;
            if (_autoNext && score >= 1f) {
                loadNext();
            } else {
                setupAnswer();
                //todo 更新ui
            }
        } else {
            setupAnswer();
            _state = STATE_CHECKING;
            _answerWorkZone.removeAllViews();
            LayoutInflater.from(this).inflate(R.layout.element_quiz_mark, _answerWorkZone);
            //todo 要显示答案,然后让用户评分
        }
    }

    //增加一条做题记录
    public void postRecord(float score) {
        //todo
    }

    public void setupAnswer() {
        _analysisImgDisplay.setImages(_currentContext.getAnalysisImage());
        _analysisText.post(() -> {
            Spanned sp = MarkDown.fromMarkdown(_currentContext.getAnalysisDetail(), null, _analysisText);
            _analysisText.setText(sp);
        });
        _answerContent = _currentContext.getType().getDisplayView(this);
        _answerContent.setAnswer(_currentContext.getAnswer());
        _answerContainer.removeAllViews();
        _answerContainer.addView(_answerContent);
        _answerViewZone.initLayout();


        _answerViewZone.postDelayed(() -> _answerViewZone.expand(), 100);
        _questionScroll.postDelayed(() -> _questionScroll.smoothScrollTo(0, _questionHolder.getMeasuredHeight()), 600);
    }

    public void loadNext() {
        //todo 直接下一题
    }
}