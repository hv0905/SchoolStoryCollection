package net.sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
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
import net.sakuratrak.schoolstorycollection.core.ExerciseLogGroup;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

public class QuizActivity extends AppCompatActivity {

    public static final int MODE_SOLO = 1;
    public static final int MODE_LIST = 2;

    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_QUESTION_ID = "id";
    public static final String EXTRA_QUESTION_IDS = "ids";
    public static final String EXTRA_QUIZ_DESCRIPTION = "quiz_description";
    public static final int RESULT_NONE_DONE = 1003;
    private static final String TAG = "QuizActivity";
    private static final int STATE_ANSWERING = 0;
    private static final int STATE_CHECKING = 1;
    private static final int STATE_POST_CHECKING = 2;
    private static final int RESULT_ALL_DONE = 1001;
    private static final int RESULT_PART_DONE = 1002;
    //region Elements
    private Toolbar _toolbar;
    private TextView _questionCounter;
    private TextView _questionName;
    private TextView _textQuestionType;
    private TextView _questionText;
    private ImageDisplayView _questionImgDisplay;
    private FrameLayout _answerWorkZone;
    private QuizAnswerView _quizAnswerContent;
    private ExpandableLinearLayout _answerViewZone;
    private LinearLayout _questionHolder;
    private ScrollView _questionScroll;
    private AnswerUiDisplayView _answerContent;
    private TextView _analysisText;
    private FrameLayout _answerContainer;
    private ImageDisplayView _analysisImgDisplay;
    private QuizCheckView _checkView;
    private ConstraintLayout _mainContainer;
    private TextView _txtTime;
    //endregion

    //region fields
    private boolean _autoNext;
    private ArrayList<Integer> _idList;
    private int _currentId = 0;
    private int _doneCount = 0;
    private int _mode;
    private int _state;
    private QuestionInfo _currentContext;
    private QuizMarkView _markView;
    private ExerciseLogGroup _currentGroup;
    private Timer _timer;
    private int currentTimeSec;
    //endregion

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
        _txtTime = findViewById(R.id.txtTime);

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
                if (_idList.size() == 0) finish();
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
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.confirmSkip))
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setMessage(getString(R.string.confirmSkipMsg))
                        .setNegativeButton(R.string.confirm, (dialog, which) -> loadNext())
                        .setPositiveButton(R.string.cancel, null).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz_options, menu);
        return true;
    }

    private void setupQuestion() {
        if (_answerViewZone.isExpanded()) {
            _answerViewZone.collapse(0, new LinearInterpolator());
        }
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
        currentTimeSec = 0;
        _timer = new Timer();
        _txtTime.setText(String.format(Locale.US, "%02d:%02d", currentTimeSec / 60, currentTimeSec % 60));
        _timer.schedule(new TimerTask() {
            @Override
            public void run() {
                currentTimeSec++;
                _txtTime.post(() -> _txtTime.setText(String.format(Locale.US, "%02d:%02d", currentTimeSec / 60, currentTimeSec % 60)));
            }
        }, 1000, 1000);
    }

    private void checkAnswer() {
        Log.d(TAG, "checkAnswer: submit");
        _timer.cancel();
        if (_currentContext.getAnswer() instanceof Answer.PlainTextAnswer) {
            //收集用户答案并检查
            Answer.PlainTextAnswer userAnswer = ((CheckableQuizAnswerView) _quizAnswerContent).getAnswer();
            float score = ((Answer.PlainTextAnswer) _currentContext.getAnswer()).checkAnswer(userAnswer);
            //提交记录
            Log.d(TAG, "checkAnswer: score:" + score);
            postRecord((int) (score * 100 + 0.5));
            _state = STATE_POST_CHECKING;
            if (_autoNext && score >= 1f && !shouldExit()) {
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
    private void postRecord(int score) {
        try {
            if (_currentGroup == null) {
                _currentGroup = new ExerciseLogGroup(getIntent().hasExtra(EXTRA_QUIZ_DESCRIPTION) ? getIntent().getStringExtra(EXTRA_QUIZ_DESCRIPTION) : "小测", _currentContext.getSubject());
                DbManager.getDefaultHelper(this).getExerciseLogGroups().create(_currentGroup);
            }
            ExerciseLog log = new ExerciseLog(score, _currentContext, _currentGroup);
            DbManager.getDefaultHelper(this)
                    .getExerciseLogs()
                    .create(log);
            _doneCount++;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupAnswer() {
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

    private void loadNext() {
        if (shouldExit()) {
            exit();
        } else {
            _state = STATE_ANSWERING;
            try {
                _currentContext = DbManager.getDefaultHelper(this).getQuestionInfos().queryForId(_idList.get(++_currentId));
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
                _currentId++;
                exit();
                break;
        }
    }

    private void exit() {
        //退出小测,显示报告
        if (_doneCount == 0) {
            //一题都没做可以直接退了
            setResult(RESULT_NONE_DONE);
            super.onBackPressed();
            finish();
        } else {
            //要显示报告
            if (shouldExit())
                setResult(RESULT_ALL_DONE);
            else
                setResult(RESULT_PART_DONE);
            Intent intent = new Intent(this, QuizResultActivity.class);
            intent.putExtra(QuizResultActivity.EXTRA_GROUP_ID, _currentGroup.getId());
            startActivity(intent);
            finish();
        }
    }

    private boolean shouldExit() {
        return _mode == MODE_SOLO || _currentId >= (_idList.size() - 1);
    }
}