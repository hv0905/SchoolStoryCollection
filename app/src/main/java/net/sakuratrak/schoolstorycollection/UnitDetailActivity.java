package net.sakuratrak.schoolstorycollection;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;

import java.sql.SQLException;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

public class UnitDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CONTEXT_ID = "subject_id";

    public static final int RESULT_DELETED = 100;

    public static final int RESULT_CHANGED = 101;

    LearningUnitInfo _context;

    Toolbar _toolbar;
    MaterialButton _resetBtn;
    MaterialButton _rmBtn;
    ScrollView _scrollMain;
    PieChart _difficultyPie;
    TextView _valQuizCount;
    TextView _valCorrectRatio;
    ProgressBar _correctRatioBar;
    TextView _warningTxt;
    TextView _valQuestionCount;
    TextView _valQuestionRatio;
    ProgressBar _questionRatioBar;
    ConstraintLayout _unitMainInfo;


    boolean _changed = false;

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

        _rmBtn = findViewById(R.id.rmBtn);
        _resetBtn = findViewById(R.id.resetBtn);
        _scrollMain = findViewById(R.id.scrollMain);
        _difficultyPie = findViewById(R.id.difficultyPie);
        _toolbar = findViewById(R.id.toolbar);
        _valQuizCount = findViewById(R.id.valQuizCount);
        _valCorrectRatio = findViewById(R.id.valCorrectRatio);
        _correctRatioBar = findViewById(R.id.correctRatioBar);
        _warningTxt = findViewById(R.id.warningTxt);
        _valQuestionCount = findViewById(R.id.valQuestionCount);
        _valQuestionRatio = findViewById(R.id.valQuestionRatio);
        _questionRatioBar = findViewById(R.id.questionRatioBar);
        _unitMainInfo = findViewById(R.id.unitMainInfo);

        UiHelper.applyAppearanceForPie(this, _difficultyPie);

        int uiColor = UiHelper.getFlatUiColor(this, _context.getSubject().getId());
        _toolbar.setBackgroundColor(uiColor);
        getWindow().setStatusBarColor(uiColor);

        setSupportActionBar(_toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        _resetBtn.setOnClickListener(v -> {
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle(R.string.confirmLog_title).setIcon(R.drawable.ic_warning_black_24dp).setMessage(String.format(getString(R.string.confirmLog_msg), _context.getName()));
            ad.setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.confirm, (dialog, which) -> {
                _changed = true;
                Snackbar.make(_resetBtn, "已清除统计信息", Snackbar.LENGTH_LONG).show();
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
                _changed = false;
                setResult(RESULT_DELETED);
                finish();
            });
            ad.show();
        });

        refresh();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (_changed) setResult(RESULT_CHANGED);
    }

    void refresh() {
        getSupportActionBar().setTitle(_context.getName());
        int questionSum;
        try {
            questionSum = new QuestionInfo.QuestionInfoDaoManager(DbManager.getDefaultHelper(this)).FindAllWithSubject(_context.getSubject()).size();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        UnitDisplayAdapter.DataContext mainInfo = UnitDisplayAdapter.DataContext.fromDb(_context,questionSum);
        _valQuizCount.setText(String.valueOf(mainInfo.QuizCount));
        _valCorrectRatio.setText(String.format(Locale.ENGLISH, "%d%%", mainInfo.QuizCorrectRatio));
        _correctRatioBar.setProgress(mainInfo.QuizCorrectRatio);
        _warningTxt.setVisibility(mainInfo.requireMoreRecord ? View.VISIBLE : View.INVISIBLE);
        _valQuestionCount.setText(String.valueOf(mainInfo.QuestionCount));
        _valQuestionRatio.setText(String.format(Locale.ENGLISH, "%d%%", mainInfo.QuestionRatio));
        _questionRatioBar.setProgress(mainInfo.QuestionRatio);
        // load graph
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
