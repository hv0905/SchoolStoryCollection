package net.sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.text.Spanned;
import android.util.Log;
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
import net.sakuratrak.schoolstorycollection.core.ExerciseLog;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class QuizActivity extends AppCompatActivity {

    public static final int MODE_SOLO = 1;
    public static final int MODE_LIST = 2;

    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_QUESTION_ID = "id";
    public static final String EXTRA_QUESTION_IDS = "ids";

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
    QuizCheckView _checkView;
    ConstraintLayout _mainContainer;

    boolean _autoNext;

    ArrayList<Integer> _idList;
    int _currentId = 0;
    int _mode;

    int _state;

    QuestionInfo _currentContext;
    private QuizMarkView _markView;
    private ArrayList<Integer> _historyIds;


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
        _mainContainer = findViewById(R.id.mainContainer);

        setSupportActionBar(_toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(EXTRA_MODE)) {
            _mode = getIntent().getIntExtra(EXTRA_MODE, 0);
        } else finish();

        switch (_mode) {
            case MODE_SOLO:
                try {
                    _currentContext = DbManager.getDefaultHelper(this)
                            .getQuestionInfos()
                            .queryForId(getIntent().getIntExtra(EXTRA_QUESTION_ID, 0));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case MODE_LIST:
                _idList = getIntent().getIntegerArrayListExtra(EXTRA_QUESTION_IDS);
                try {
                    _currentContext = DbManager.getDefaultHelper(this).getQuestionInfos().queryForId(_idList.get(_currentId));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            default:
                finish();
                return;
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
        getWindow().setStatusBarColor(uiColor);

        _questionName.setText(_currentContext.getTitle());
        _questionCounter.setText(String.valueOf(_currentId + 1));
        _textQuestionType.setText(_currentContext.getType().getTitleId());
        _questionText.post(() -> {
            Spanned sp = MarkDown.fromMarkdown(_currentContext.getQuestionDetail(), null, _questionText);
            _questionText.setText(sp);
        });
        _questionImgDisplay.setImages(Arrays.asList(_currentContext.getQuestionImage()));

        _quizAnswerContent = _currentContext.getType().getQuizAnswerView(this);
        _quizAnswerContent.setOnAnswerReport((sender, status) -> checkAnswer());
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
            postRecord((int) (score * 100 + 0.5));
            _state = STATE_POST_CHECKING;
            if (_autoNext && score >= 1f) {
                loadNext();
            } else {
                setupAnswer();
                _answerWorkZone.animate().alpha(0).setDuration(200).withEndAction(() -> {
                    _answerWorkZone.removeAllViews();
                    _checkView = new QuizCheckView(this);
                    _checkView.setNoticeContain(score == 0 ?
                            QuizCheckView.NOTICE_NONE_RIGHT
                            : (score == 1 ? QuizCheckView.NOTICE_ALL_RIGHT
                            : QuizCheckView.NOTICE_HALF_RIGHT));
                    _checkView.setIsDone(shouldExit());
                    _checkView.setOnNextBtnClickListener(v -> loadNext());
                    _checkView.setOnQuitBtnClickListener(v -> onBackPressed());
                    _answerWorkZone.addView(_checkView);
                    _answerWorkZone.animate().alpha(1).setDuration(200).start();
                }).start();
            }
        } else {
            setupAnswer();
            _state = STATE_CHECKING;
            _answerWorkZone.animate()
                    .alpha(0).setDuration(200)
                    .withEndAction(() -> {
                        _answerWorkZone.removeAllViews();
                        _markView = new QuizMarkView(this);
                        _markView.setOnConfirmListener(v -> {
                            postRecord(_markView.getScore());
                            _state = STATE_POST_CHECKING;
                            loadNext();
                        });
                        _answerWorkZone.addView(_markView);
                        _answerWorkZone.animate().alpha(1).setDuration(200).start();
                    }).start();


        }
    }

    //增加一条做题记录
    public void postRecord(int score) {
        new Thread(() -> {
            try {
                ExerciseLog log = new ExerciseLog(score, _currentContext);
                DbManager.getDefaultHelper(this)
                        .getExerciseLogs()
                        .create(log);
                if(_historyIds == null)
                    _historyIds = new ArrayList<>();
                _historyIds.add(log.getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void setupAnswer() {
        if(_answerViewZone.isExpanded()){
            _answerViewZone.setExpanded(false);
        }
        _analysisImgDisplay.setImages(Arrays.asList(_currentContext.getAnalysisImage()));
        _analysisText.post(() -> {
            Spanned sp = MarkDown.fromMarkdown(_currentContext.getAnalysisDetail(), null, _analysisText);
            _analysisText.setText(sp);
        });
        _answerContent = _currentContext.getType().getDisplayView(this);
        _answerContent.setAnswer(_currentContext.getAnswer());
        _answerContainer.removeAllViews();
        _answerContainer.addView(_answerContent);
        _answerViewZone.initLayout();


        _answerViewZone.postDelayed(() -> _answerViewZone.expand(), 150);
        _questionScroll.postDelayed(() ->
                        _questionScroll.smoothScrollTo(0, _questionHolder.getMeasuredHeight())
                , 650);
    }

    public void loadNext() {
        if (shouldExit()) {
            exit();
        } else {
            //todo 载入下一题
            _state = STATE_ANSWERING;
            _currentId++;
            try {
                _currentContext = DbManager.getDefaultHelper(this).getQuestionInfos().queryForId(_idList.get(_currentId));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            _mainContainer.animate().alpha(0).translationXBy(-100).setDuration(200).withEndAction(() -> {
                setupQuestion();
                _mainContainer.setTranslationX(100);
                _mainContainer.animate().alpha(1).translationXBy(-100).setDuration(200).start();
            }).start();
        }
    }

    @Override
    public void onBackPressed() {
        switch (_state) {
            case STATE_ANSWERING:
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setTitle(R.string.exitQuizDialogAskTitle)
                        .setMessage(R.string.exitQuizDialogAskState0)
                        .setNegativeButton(R.string.confirm, (dialog, which) -> exit())
                        .setPositiveButton(R.string.cancel, null)
                        .show();
                break;
            case STATE_CHECKING:
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setTitle(R.string.exitQuizDialogAskTitle)
                        .setMessage(R.string.exitQuizDialogAskState1)
                        .setNegativeButton(R.string.confirm, (dialog, which) -> exit())
                        .setPositiveButton(R.string.cancel, null)
                        .show();
                break;
            case STATE_POST_CHECKING:
                //可以直接退出
                exit();
                break;
        }
    }

    void exit() {
        //退出小测,显示报告
        if (_currentId == 0) {
            //一题都没做可以直接退了
            super.onBackPressed();
            finish();
        } else {
            //要显示报告
            Intent intent = new Intent(this,QuizResultActivity.class);
            intent.putIntegerArrayListExtra(QuizResultActivity.EXTRA_RESULT_IDS,_historyIds);
            startActivity(intent);
            finish();
        }
    }

    boolean shouldExit() {
        return _mode == MODE_SOLO || _currentId >= (_idList.size() - 1);
    }
}