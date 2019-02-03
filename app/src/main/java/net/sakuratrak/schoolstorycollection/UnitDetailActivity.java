package net.sakuratrak.schoolstorycollection;

import android.R.id;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import net.sakuratrak.schoolstorycollection.R.drawable;
import net.sakuratrak.schoolstorycollection.R.layout;
import net.sakuratrak.schoolstorycollection.R.string;
import net.sakuratrak.schoolstorycollection.UnitDisplayAdapter.DataContext;
import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;
import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo.DbHelper;

import java.sql.SQLException;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

public class UnitDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CONTEXT_ID = "subject_id";

    public static final int RESULT_DELETED = 100;
    public static final int RESULT_CHANGED = 101;
    public static final int RESULT_HIDDEN = 102;

    private LearningUnitInfo _context;
    private boolean _edited = false;
    private boolean _hidden = false;

    //region views
    private Toolbar _toolbar;
    private MaterialButton _hideBtn;
    private MaterialButton _resetBtn;
    private MaterialButton _rmBtn;
    private ScrollView _scrollMain;
    private PieChart _difficultyPie;
    private TextView _valQuizCount;
    private TextView _valCorrectRatio;
    private ProgressBar _correctRatioBar;
    private TextView _warningTxt;
    private TextView _valQuestionCount;
    private TextView _valQuestionRatio;
    private ProgressBar _questionRatioBar;
    private ConstraintLayout _unitMainInfo;
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_unit_detail);

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

        getSupportActionBar().setHomeAsUpIndicator(drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _hideBtn.setOnClickListener(v -> {
            if(_context.isHidden()){
                //要恢复显示
                // TODO: 2019/1/29 undo hide unit
                _context.setHidden(false);
                try {
                    DbManager.getDefaultHelper(this).getLearningUnitInfos().update(_context);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }
                Snackbar.make(_toolbar, string.hiddenUndoed,Snackbar.LENGTH_LONG).show();
                refresh();
                _hidden = false;
                _edited = true;

            }else {
                //要隐藏
                if (AppSettingsMaster.getBooleanVal(this, AppSettingsMaster.SETTINGS_DIALOG_UNIT_HIDE_CONFIRM, false)) {
                    hideUnit();
                } else {
                    CheckBox cb = new CheckBox(this);
                    cb.setText(getString(string.neverShowAgain));
                    cb.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    new Builder(this)
                            .setIcon(drawable.ic_warning_black_24dp)
                            .setTitle(string.dialogHideUnitConfirm)
                            .setMessage(string.dialogHideUnitConfirmMsg)
                            .setView(cb)
                            .setPositiveButton(string.confirm, (dialog, which) -> {
                                if (cb.isChecked()) {
                                    AppSettingsMaster.setBooleanVal(this, AppSettingsMaster.SETTINGS_DIALOG_UNIT_HIDE_CONFIRM, true);
                                }
                                hideUnit();
                            })
                            .setNegativeButton(string.cancel,null)
                            .show();
                }
            }
        });


        _resetBtn.setOnClickListener(v -> {
            Builder ad = new Builder(this);
            ad.setTitle(string.confirmLog_title).setIcon(drawable.ic_warning_black_24dp).setMessage(String.format(getString(string.confirmLog_msg), _context.getName()));
            ad.setNegativeButton(string.cancel, null).setPositiveButton(string.confirm, (dialog, which) -> {
                _edited = true;
                Snackbar.make(_resetBtn, "已清除统计信息", Snackbar.LENGTH_LONG).show();
            });
            ad.show();
        });

        _rmBtn.setOnClickListener(v -> {
            Builder ad = new Builder(this);
            ad.setTitle(string.confirmRm_title).setIcon(drawable.ic_warning_black_24dp).setMessage(String.format(getString(string.confirmRm_msg), _context.getName()));
            ad.setNegativeButton(string.cancel, null).setPositiveButton(string.confirm, (dialog, which) -> {
                try {
                    DbManager.getDefaultHelper(this).getLearningUnitInfos().delete(_context);
                } catch (SQLException e) {
                    Snackbar.make(_rmBtn, string.sqlExp, Snackbar.LENGTH_LONG).show();
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
        if(_hidden) setResult(RESULT_HIDDEN);
        else if (_edited) setResult(RESULT_CHANGED);

        super.onBackPressed();
    }

    private void refresh() {
        getSupportActionBar().setTitle(_context.getName());
        int questionSum;
        try {
            questionSum = new DbHelper(DbManager.getDefaultHelper(this)).FindAllWithSubject(_context.getSubject()).size();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        DataContext mainInfo = DataContext.fromDb(_context, questionSum);
        _valQuizCount.setText(String.valueOf(mainInfo.QuizCount));
        _valCorrectRatio.setText(String.format(Locale.ENGLISH, "%d%%", mainInfo.QuizCorrectRatio));
        _correctRatioBar.setProgress(mainInfo.QuizCorrectRatio);
        _warningTxt.setVisibility(mainInfo.requireMoreRecord ? View.VISIBLE : View.INVISIBLE);
        _valQuestionCount.setText(String.valueOf(mainInfo.QuestionCount));
        _valQuestionRatio.setText(String.format(Locale.ENGLISH, "%d%%", mainInfo.QuestionRatio));
        _questionRatioBar.setProgress(mainInfo.QuestionRatio);
        _hideBtn.setText(_context.isHidden() ? string.undoHideUnit : string.hideUnit);
        // load graph
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideUnit(){
        // TODO: 2019/1/29
        _context.setHidden(true);
        try {
            DbManager.getDefaultHelper(this).getLearningUnitInfos().update(_context);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        Snackbar.make(_toolbar, string.hiddenDone,Snackbar.LENGTH_LONG).show();
        refresh();
        _hidden = true;
    }
}
