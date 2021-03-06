package net.sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;
import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;
import net.sakuratrak.schoolstorycollection.core.QuizHelper;
import net.sakuratrak.schoolstorycollection.core.ReviewRatio;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

public class UnitDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CONTEXT_ID = "subject_id";

    public static final int RESULT_DELETED = 100;
    public static final int RESULT_CHANGED = 101;
    public static final int RESULT_HIDDEN = 102;

    private static final int REQUEST_QUIZ = 201;

    private LearningUnitInfo _context;
    private boolean _edited = false;
    private boolean _hidden = false;

    //region views
    private Toolbar _toolbar;
    private MaterialButton _hideBtn;
    private MaterialButton _resetBtn;
    private MaterialButton _rmBtn;
    private ScrollView _scrollMain;
    private TextView _valQuizCount;
    private TextView _valCorrectRatio;
    private ProgressBar _correctRatioBar;
    private TextView _warningTxt;
    private TextView _valQuestionCount;
    private TextView _valQuestionRatio;
    private ProgressBar _questionRatioBar;
    private ConstraintLayout _unitMainInfo;
    private TextView _reviewHigh;
    private TextView _reviewMid;
    private TextView _reviewLow;
    private TextView _reviewUnknown;
    private TextView _valQuizAvg;
    private PieChart _reviewRatioPie;
    private PieChart _difficultyPie;
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_detail);

        if (getIntent().hasExtra(EXTRA_CONTEXT_ID)) {
            try {
                _context = DbManager.getDefaultHelper(this).getLearningUnitInfos().queryForId(getIntent().getIntExtra(EXTRA_CONTEXT_ID, 0));
            } catch (SQLException e) {
                e.printStackTrace();
                finish();
            }
        } else finish();

        _hideBtn = findViewById(R.id.hideBtn);
        _rmBtn = findViewById(R.id.rmBtn);
        _resetBtn = findViewById(R.id.resetBtn);
        _scrollMain = findViewById(R.id.scrollMain);
        _toolbar = findViewById(R.id.toolbar);
        _valQuizCount = findViewById(R.id.valQuizCount);
        _valCorrectRatio = findViewById(R.id.valCorrectRatio);
        _correctRatioBar = findViewById(R.id.correctRatioBar);
        _warningTxt = findViewById(R.id.warningTxt);
        _valQuestionCount = findViewById(R.id.valQuestionCount);
        _valQuestionRatio = findViewById(R.id.valQuestionRatio);
        _questionRatioBar = findViewById(R.id.questionRatioBar);
        _unitMainInfo = findViewById(R.id.unitMainInfo);
        _reviewHigh = findViewById(R.id.reviewHigh);
        _reviewMid = findViewById(R.id.reviewMid);
        _reviewLow = findViewById(R.id.reviewLow);
        _reviewUnknown = findViewById(R.id.reviewUnknown);
        _valQuizAvg = findViewById(R.id.valQuizAvg);
        _reviewRatioPie = findViewById(R.id.reviewRatioPie);
        _difficultyPie = findViewById(R.id.difficultyPie);

        UiHelper.applyAppearanceForPie(this, _difficultyPie);
        UiHelper.applyAppearanceForPie(this, _reviewRatioPie);

        int uiColor = UiHelper.getFlatUiColor(this, _context.getSubject().getId());
        _toolbar.setBackgroundColor(uiColor);
        getWindow().setStatusBarColor(uiColor);

        setSupportActionBar(_toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        _difficultyPie.setCenterText(getString(R.string.StatDifficultyPie));
        _difficultyPie.setNoDataText(getString(R.string.StatAddQuestionNotify));

        _reviewRatioPie.setCenterText(getString(R.string.StatReviewPie));
        _reviewRatioPie.setNoDataText(getString(R.string.StatAddQuestionNotify));

        _hideBtn.setOnClickListener(v -> {
            if (_context.isHidden()) {
                //要恢复显示
                _context.setHidden(false);
                try {
                    DbManager.getDefaultHelper(this).getLearningUnitInfos().update(_context);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }
                Snackbar.make(_toolbar, R.string.hiddenUndoed, Snackbar.LENGTH_LONG).show();
                refresh();
                _hidden = false;
                _edited = true;

            } else {
                //要隐藏
                if (AppSettingsMaster.getBooleanVal(this, AppSettingsMaster.SETTINGS_DIALOG_UNIT_HIDE_CONFIRM, false)) {
                    hideUnit();
                } else {
                    CheckBox cb = new CheckBox(this);
                    cb.setText(getString(R.string.neverShowAgain));
                    cb.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    new AlertDialog.Builder(this)
                            .setIcon(R.drawable.ic_warning_black_24dp)
                            .setTitle(R.string.dialogHideUnitConfirm)
                            .setMessage(R.string.dialogHideUnitConfirmMsg)
                            .setView(cb)
                            .setPositiveButton(R.string.confirm, (dialog, which) -> {
                                if (cb.isChecked()) {
                                    AppSettingsMaster.setBooleanVal(this, AppSettingsMaster.SETTINGS_DIALOG_UNIT_HIDE_CONFIRM, true);
                                }
                                hideUnit();
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                }
            }
        });


        _resetBtn.setOnClickListener(v -> {
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle(R.string.confirmLog_title).setIcon(R.drawable.ic_warning_black_24dp).setMessage(String.format(getString(R.string.confirmUnitResetStat_msg), _context.getName()));
            ad.setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.confirm, (dialog, which) -> {
                _context.resetStat(this);
                _edited = true;
                refresh();
                Snackbar.make(_resetBtn, R.string.resetStatSuccessful, Snackbar.LENGTH_LONG).show();
            });
            ad.show();
        });

        _rmBtn.setOnClickListener(v -> {
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle(R.string.confirmRm_title).setIcon(R.drawable.ic_warning_black_24dp).setMessage(String.format(getString(R.string.confirmRm_msg), _context.getName()));
            ad.setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.confirm, (dialog, which) -> {
                try {
                    DbManager.getDefaultHelper(this).getLearningUnitInfos().delete(_context);
                } catch (SQLException e) {
                    Snackbar.make(_rmBtn, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
                    return;
                }
                _edited = false;
                setResult(RESULT_DELETED);
                finish();
            });
            ad.show();
        });

        refresh();
    }

    @Override
    public void onBackPressed() {
        if (_hidden) setResult(RESULT_HIDDEN);
        else if (_edited) setResult(RESULT_CHANGED);

        super.onBackPressed();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        _reviewRatioPie.animateY(1000, Easing.EaseInOutQuad);
        _difficultyPie.animateY(1000, Easing.EaseInOutQuad);

    }

    private void refresh() {
        getSupportActionBar().setTitle(_context.getName());
        int questionSum = 0;
        try {
            List<QuestionInfo> questions = new QuestionInfo.DbHelper(DbManager.getDefaultHelper(this)).findAllWithSubject(_context.getSubject());
            for (QuestionInfo qi :
                    questions) {
                if(!qi.isHidden()) questionSum++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        UnitDisplayAdapter.DataContext mainInfo = UnitDisplayAdapter.DataContext.fromDb(_context, questionSum);
        _valQuizCount.setText(String.valueOf(mainInfo.QuizCount));
        _valCorrectRatio.setText(mainInfo.ReviewRatio == -1 ? "-" : String.format(Locale.ENGLISH, "%d%%", mainInfo.ReviewRatio));
        _correctRatioBar.setProgress(mainInfo.ReviewRatio);
        _warningTxt.setVisibility(mainInfo.requireMoreRecord ? View.VISIBLE : View.INVISIBLE);
        _valQuestionCount.setText(String.valueOf(mainInfo.QuestionCount));
        _valQuestionRatio.setText(String.format(Locale.ENGLISH, "%d%%", mainInfo.QuestionRatio));
        _questionRatioBar.setProgress(mainInfo.QuestionRatio);
        _hideBtn.setText(_context.isHidden() ? R.string.undoHideUnit : R.string.hideUnit);
        _valQuizAvg.setText(String.format(Locale.US, "%d%%", _context.computeCorrectRatio()));

        _reviewHigh.setVisibility(View.INVISIBLE);
        _reviewMid.setVisibility(View.INVISIBLE);
        _reviewLow.setVisibility(View.INVISIBLE);
        _reviewUnknown.setVisibility(View.INVISIBLE);

        ReviewRatio ratio = ReviewRatio.getByRatio(mainInfo.ReviewRatio);
        switch (ratio) {
            case NICE:
                _reviewHigh.setVisibility(View.VISIBLE);
                break;
            case MID:
                _reviewMid.setVisibility(View.VISIBLE);
                break;
            case BAD:
                _reviewLow.setVisibility(View.VISIBLE);
                break;
            case UNKNOWN:
                _reviewUnknown.setVisibility(View.VISIBLE);
                break;
        }

        // load graph

        int[] difficultyCounts = new int[QuestionInfo.DIFFICULTY_MAX + 1];
        int[] reviewRatioCounts = new int[4];

        for (QuestionInfo item : getActiveQuestions()) {
            difficultyCounts[item.getDifficulty()]++;
            reviewRatioCounts[ReviewRatio.getByRatio(item.computeReviewValue()).getId()]++;
        }

        ArrayList<PieEntry> difficultyPieEntry = new ArrayList<>();

        for (int i = 0; i < difficultyCounts.length; i++) {
            if (difficultyCounts[i] == 0) continue;
            difficultyPieEntry.add(new PieEntry(difficultyCounts[i], String.format(Locale.US, "%.1f★", (i) / 2f)));
        }

        ArrayList<PieEntry> reviewRatioPieEntry = new ArrayList<>();

        boolean hasData = false;
        for (int i = 0; i < reviewRatioCounts.length; i++) {
            if (reviewRatioCounts[i] != 0) hasData = true;
            reviewRatioPieEntry.add(new PieEntry(reviewRatioCounts[i], reviewRatioCounts[i] == 0 ? "" : getString(ReviewRatio.fromId(i).getStr())));
        }

        if (difficultyPieEntry.size() != 0) {
            PieDataSet difficultyPieDataSet = new PieDataSet(difficultyPieEntry, getString(R.string.difficulty));
            UiHelper.applyAppearanceForPieDataSet(this, difficultyPieDataSet, false);
            _difficultyPie.setData(new PieData(difficultyPieDataSet));
        } else {
            _difficultyPie.setData(null);
        }

        if (hasData) {
            PieDataSet reviewRatioPieDataSet = new PieDataSet(reviewRatioPieEntry, getString(R.string.reviewRatio));
            UiHelper.applyAppearanceForPieDataSet(this, reviewRatioPieDataSet, false);
            reviewRatioPieDataSet.setColors(
                    getResources().getColor(R.color.flat5),
                    getResources().getColor(R.color.flat7),
                    getResources().getColor(R.color.flat8),
                    getResources().getColor(R.color.black));
            _reviewRatioPie.setData(new PieData(reviewRatioPieDataSet));
        } else {
            _reviewRatioPie.setData(null);
        }
    }

    public List<QuestionInfo> getActiveQuestions(){
        List<QuestionInfo> result = new ArrayList<>();
        for (QuestionInfo question :
                _context.getQuestions()) {
            if (!question.isHidden())
                result.add(question);
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.quiz:
                new AlertDialog.Builder(this).setItems(R.array.list_quiz, (dialog, which) -> {
                    List<QuestionInfo> in = getActiveQuestions();
                    List<QuestionInfo> quizContext = null;
                    int n = AppSettingsMaster.getQuizSize(this);
                    switch (which) {
                        case 0:
                            //sm
                            quizContext = QuizHelper.prepareSmartQuiz(in, n);
                            break;
                        case 1:
                            //rd
                            quizContext = QuizHelper.prepareRandomQuiz(in, n);
                            break;
                    }
                    if (quizContext == null) {
                        Snackbar.make(_toolbar, R.string.quizWarnEmptyQuestion, Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    ArrayList<Integer> quizIds = new ArrayList<>(quizContext.size());
                    for (QuestionInfo qc :
                            quizContext) {
                        quizIds.add(qc.getId());
                    }

                    Intent intent = new Intent(this, QuizActivity.class);
                    intent.putIntegerArrayListExtra(QuizActivity.EXTRA_QUESTION_IDS, quizIds);
                    intent.putExtra(QuizActivity.EXTRA_MODE, QuizActivity.MODE_LIST);
                    intent.putExtra(QuizActivity.EXTRA_QUIZ_DESCRIPTION, String.format("单元%s小测:%s", which == 0 ? "智能" : "随机", _context.getName()));
                    startActivityForResult(intent, REQUEST_QUIZ);
                })
                        .setPositiveButton(R.string.cancel, null)
                        .show();
                return true;
            case R.id.rename:
                AlertDialog ad = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_edit_black_24dp)
                        .setTitle(R.string.renameUnitTitle)
                        .setView(R.layout.dialog_add_unit)
                        .setPositiveButton(R.string.done, (dialog, which) -> {
                            AlertDialog dg = (AlertDialog) dialog;
                            TextInputEditText editText = dg.findViewById(R.id.txtUnitName);
                            //noinspection ConstantConditions
                            if (editText.getText() == null || editText.getText().toString().trim().isEmpty()) {
                                return;
                            }
                            _context.setName(editText.getText().toString().trim());
                            try {
                                DbManager.getDefaultHelper(this).getLearningUnitInfos().update(_context);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            _edited = true;
                            refresh();
                        })
                        .setNegativeButton("取消", null)
                        .show();
                TextInputEditText edit = ad.findViewById(R.id.txtUnitName);
                edit.setText(_context.getName());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideUnit() {
        // TODO: 2019/1/29
        _context.setHidden(true);
        try {
            DbManager.getDefaultHelper(this).getLearningUnitInfos().update(_context);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        Snackbar.make(_toolbar, R.string.hiddenDone, Snackbar.LENGTH_LONG).show();
        refresh();
        _hidden = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.unit_detail_options, menu);
        return true;
    }
}
