package net.sakuratrak.schoolstorycollection;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.zzhoujay.markdown.MarkDown;

import net.sakuratrak.schoolstorycollection.core.AppMaster;
import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;
import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.ImageAnswer;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;
import net.sakuratrak.schoolstorycollection.core.ReviewRatio;
import net.sakuratrak.schoolstorycollection.core.ShareHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

public class QuestionDetailActivity extends AppCompatActivity {

    public static final String EXTRA_QUESTION_ID = "questionId";
    public static final int RESULT_EDITED = 200;
    public static final int RESULT_DELETED = 201;
    public static final int RESULT_HIDDEN = 202;
    private static final String TAG = "QuestionDetail";
    private static final int REQUEST_EDIT = 100;
    private AppBarLayout _appBar;
    private Toolbar _toolbar;

    //region uiElements
    private CollapsingToolbarLayout _toolbarLayout;
    private ImageView _imageTopContent;
    private FrameLayout _imageTopBorder;
    private TextView _questionText;
    private TextView _analysisText;
    private FrameLayout _answerContainer;
    private AnswerUiDisplayView _answerContent;
    private FloatingActionButton _showAnswerButton;
    private ExpandableLinearLayout _answerZone;
    private ImageDisplayView _questionImgDisplay;
    private ImageDisplayView _analysisImgDisplay;
    private TextView _valCreateTime;
    private RatingBar _valDifficulty;
    private TextView _valUnit;
    private MenuItem _showAnswerMenu;
    private MenuItem _favouriteMenu;
    private MenuItem _hideMenu;
    private QuestionInfo _context;
    private TextView _valReviewRatio;
    private LineChart _chartQuizLog;
    private TextView _reviewHigh;
    private TextView _reviewMid;
    private TextView _reviewLow;
    private TextView _reviewUnknown;
    private ConstraintLayout _unitBtn;
    private TextView _valSource;
    private TextView _valCorrectRatio;
    //endregion

    private boolean _edited = false;
    private boolean _hidden = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        _appBar = findViewById(R.id.app_bar);
        _toolbar = findViewById(R.id.toolbar);
        _toolbarLayout = findViewById(R.id.toolbarLayout);
        _imageTopBorder = findViewById(R.id.imageTopBorder);
        _imageTopContent = findViewById(R.id.imageTopContent);
        _questionText = findViewById(R.id.questionText);
        _analysisText = findViewById(R.id.analysisText);
        _answerContainer = findViewById(R.id.answerContainer);
        _showAnswerButton = findViewById(R.id.showAnswerButton);
        _answerZone = findViewById(R.id.answerZone);
        _questionImgDisplay = findViewById(R.id.questionImgDisplay);
        _analysisImgDisplay = findViewById(R.id.analysisImgDisplay);
        _valCreateTime = findViewById(R.id.valCreateTime);
        _valDifficulty = findViewById(R.id.valDifficulty);
        _valUnit = findViewById(R.id.valUnit);
        _valReviewRatio = findViewById(R.id.valReviewRatio);
        _chartQuizLog = findViewById(R.id.chartQuizLog);
        _reviewHigh = findViewById(R.id.reviewHigh);
        _reviewMid = findViewById(R.id.reviewMid);
        _reviewLow = findViewById(R.id.reviewLow);
        _reviewUnknown = findViewById(R.id.reviewUnknown);
        _unitBtn = findViewById(R.id.unitBtn);
        _valSource = findViewById(R.id.valSource);
        _valCorrectRatio = findViewById(R.id.valCorrectRatio);

        setSupportActionBar(_toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        _appBar.addOnOffsetChangedListener((appBarLayout, i) -> {
            if (Math.abs(i) - appBarLayout.getTotalScrollRange() == 0) {
                if (_showAnswerMenu != null) {
                    _showAnswerMenu.setVisible(true);
                }
            } else {
                if (_showAnswerMenu != null) {
                    _showAnswerMenu.setVisible(false);
                }
            }
        });

        _unitBtn.setOnClickListener(v -> {
            if (_context.getUnit() == null) return;
            Intent intent = new Intent(this, UnitDetailActivity.class);
            intent.putExtra(UnitDetailActivity.EXTRA_CONTEXT_ID, _context.getUnit().getId());
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        });

        _showAnswerButton.setOnClickListener(v -> toggleAnswer());
        UiHelper.applyAppearanceForLine(this, _chartQuizLog);

        //load context
        refresh();

    }

    private void loadMarkdown(TextView textView, String text) {
        Spanned spanned = MarkDown.fromMarkdown(text, source -> null, textView);
        textView.setText(spanned);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        _toolbar.inflateMenu(R.menu.detail_top_options);
        _showAnswerMenu = _toolbar.getMenu().findItem(R.id.showAnswerMenu);
        _favouriteMenu = _toolbar.getMenu().findItem(R.id.favourite);
        _hideMenu = _toolbar.getMenu().findItem(R.id.hide);

        if (_toolbar.getMenu() instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            //noinspection RestrictedApi
            m.setOptionalIconsVisible(true);
        }

        _hideMenu.setTitle(_context.isHidden() ? R.string.undoHide : R.string.hide);
        _favouriteMenu.setChecked(_context.isFavourite());
        _favouriteMenu.setIcon(_favouriteMenu.isChecked() ? R.drawable.ic_favorite_pink_24dp : R.drawable.ic_favorite_border_white_24dp);

        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.showAnswerMenu:
                toggleAnswer();
                return true;
            case R.id.editMenu:
                gotoEdit();
                return true;

            case R.id.shareMenu:
                Bitmap bm = ShareHelper.generateShareBitmap(this,_context);
                try {
                    File f = new File(AppSettingsMaster.getWorkbookRootDir(this),"a.jpg");
                    f.createNewFile();
                    FileOutputStream fos = new FileOutputStream(f);
                    bm.compress(Bitmap.CompressFormat.JPEG,90,fos);
                    fos.close();
                    bm.recycle();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //todo share
                return true;
            case R.id.hide:
                if (_context.isHidden()) {
                    //要取消
                    _context.setHidden(false);
                    try {
                        DbManager.getDefaultHelper(this).getQuestionInfos().update(_context);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return true;
                    }
                    Snackbar.make(_appBar, getString(R.string.hiddenUndoed), Snackbar.LENGTH_LONG).show();
                    refresh();
                    _edited = true;
                    _hidden = false;
                } else {
                    //要隐藏
                    if (!AppSettingsMaster.getBooleanVal(this, AppSettingsMaster.SETTINGS_DIALOG_HIDE_CONFIRM, false)) {
                        CheckBox cb = new CheckBox(this);
                        cb.setText(getString(R.string.neverShowAgain));
                        cb.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                        new Builder(this)
                                .setTitle(R.string.hiddenDialogTitle)
                                .setIcon(R.drawable.ic_warning_black_24dp)
                                .setMessage(R.string.hiddenDialogMsg)
                                .setView(cb)
                                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                                    if (cb.isChecked()) {
                                        AppSettingsMaster.setBooleanVal(this, AppSettingsMaster.SETTINGS_DIALOG_HIDE_CONFIRM, true);
                                    }
                                    hideQuestion();
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .show();
                    } else {
                        hideQuestion();
                    }
                }
                return true;
            case R.id.favourite:
                Log.d(TAG, "onOptionsItemSelected: " + item.isChecked());
                item.setChecked(!item.isChecked());
                item.setIcon(item.isChecked() ? R.drawable.ic_favorite_pink_24dp : R.drawable.ic_favorite_border_white_24dp);
                _context.setFavourite(item.isChecked());
                try {
                    DbManager.getDefaultHelper(this).getQuestionInfos().update(_context);
                    _edited = true;
                } catch (SQLException e) {
                    Snackbar.make(_toolbarLayout, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                Snackbar.make(_toolbarLayout, String.format("已%s喜欢", item.isChecked() ? "" : "取消"), Snackbar.LENGTH_LONG).show();

//                if(item.isChecked()){
//
//                }else{
//
//                }
                return true;

            case R.id.delete:
                new Builder(this)
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setTitle(getString(R.string.confirmDelete)).setMessage(String.format("将永久删除错题%s(真的很久!)!", _context.getTitle()))
                        .setPositiveButton(R.string.confirm, (dialog, which) -> {
                            try {
                                //delete all images
                                for (String item_ : _context.getQuestionImage()) {
                                    AppMaster.removeImgFile(this, item_);
                                    AppMaster.removeThumbFile(this, item_);
                                }
                                for (String item_ : _context.getAnalysisImage()) {
                                    AppMaster.removeImgFile(this, item_);
                                    AppMaster.removeThumbFile(this, item_);
                                }
                                if (_context.getAnswer() instanceof ImageAnswer) {
                                    for (String item_ : ((ImageAnswer) _context.getAnswer()).Image) {
                                        AppMaster.removeImgFile(this, item_);
                                        AppMaster.removeThumbFile(this, item_);
                                    }
                                }
                                DbManager.getDefaultHelper(this).getQuestionInfos().delete(_context);
                            } catch (SQLException e) {
                                Toast.makeText(this, R.string.sqlExp, Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                return;
                            }
                            setResult(RESULT_DELETED);
                            finish();
                        })
                        .setNegativeButton(R.string.cancel, null).show();
                return true;
            case R.id.resetMenu:
                new Builder(this)
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setTitle(getString(R.string.confirmDelete)).setMessage(String.format(getString(R.string.confirmQuestionResetStat_msg), _context.getTitle()))
                        .setPositiveButton(R.string.confirm, (dialog, which) -> {
                            _context.resetStat(this);
                            try {
                                DbManager.getDefaultHelper(this).getQuestionInfos().update(_context);
                                _edited = true;
                                refresh();
                                Snackbar.make(_appBar, R.string.resetStatSuccessful, Snackbar.LENGTH_LONG).show();
                            } catch (SQLException e) {
                                Snackbar.make(_toolbarLayout, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null).show();
                return true;

        }
        return false;
    }

    private void hideQuestion() {
        _context.setHidden(true);
        try {
            DbManager.getDefaultHelper(this).getQuestionInfos().update(_context);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Snackbar.make(_appBar, getString(R.string.hiddenDone), Snackbar.LENGTH_LONG).show();
        refresh();
        _hidden = true;
    }

    private void gotoEdit() {
        Intent intent = new Intent(this, QuestionEditActivity.class);
        intent.putExtra(QuestionEditActivity.EXTRA_CONTEXT_ID, _context.getId());
        startActivityForResult(intent, REQUEST_EDIT);
    }

    private void toggleAnswer() {
        _answerZone.toggle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_EDIT:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(_toolbarLayout, getString(R.string.changeSaved), Snackbar.LENGTH_LONG).show();
                    _edited = true;
                    refresh();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refresh() {

        if (!getIntent().hasExtra(EXTRA_QUESTION_ID)) {
            finish();
            return;
        }

        try {
            _context = DbManager.getDefaultHelper(this).getQuestionInfos().queryForId(getIntent().getIntExtra(EXTRA_QUESTION_ID, 0));
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.sqlExp, Toast.LENGTH_LONG).show();
            return;
        }

        //noinspection ConstantConditions
        getSupportActionBar().setTitle(_context.getTitle());
        _toolbar.setTitle(_context.getTitle());
        int uiColor = UiHelper.getFlatUiColor(this, _context.getSubject().getId());
        _toolbarLayout.setContentScrimColor(uiColor);
        _toolbarLayout.setStatusBarScrimColor(uiColor);
        _toolbarLayout.setTitle(_context.getTitle());
        _valDifficulty.setRating(_context.getDifficulty() / 2f);
        _valCreateTime.setText(UiHelper.defaultFormatWithTime.format(_context.getAuthorTime()));
        _valSource.setText(_context.getSource());
        _valCorrectRatio.setText(String.format(Locale.US, "%d%%", (int) (_context.computeAvgCorrectRatio() + 0.5)));
        _valUnit.setText(_context.getUnit() == null ? getText(R.string.emptyUnit) : _context.getUnit().getName());

        _imageTopContent.setImageURI(Uri.fromFile(AppMaster.getThumbFile(this, _context.getQuestionImage()[0])));

        _questionText.post(() -> loadMarkdown(_questionText, _context.getQuestionDetail()));
        _analysisText.post(() -> {
            loadMarkdown(_analysisText, _context.getAnalysisDetail());
            _answerZone.initLayout();
        });


        _answerContent = _context.getType().getDisplayView(this);
        _answerContent.setAnswer(_context.getAnswer());
        _answerContainer.removeAllViews();
        _answerContainer.addView(_answerContent);

        _questionImgDisplay.setImages(Arrays.asList(_context.getQuestionImage()));
        _analysisImgDisplay.setImages(Arrays.asList(_context.getAnalysisImage()));

        //stat

        int stat = _context.computeReviewValue();
        _valReviewRatio.setText(stat == -1 ? "小测不足5次" : String.format(Locale.US, "%d%%", stat));
        _reviewHigh.setVisibility(View.INVISIBLE);
        _reviewMid.setVisibility(View.INVISIBLE);
        _reviewLow.setVisibility(View.INVISIBLE);
        _reviewUnknown.setVisibility(View.INVISIBLE);

        ReviewRatio ratio = ReviewRatio.getByRatio(stat);
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

        ArrayList<Entry> entries = new ArrayList<>();
        int[] last5 = _context.getLastNScoreUnsafe(5);
        if (last5.length != 0) {
            //entries.add(new Entry(0, last5[0]));
            for (int i = 0; i < last5.length; i++) {
                entries.add(new Entry(i + 1, last5[i]));
            }
        }

        LineDataSet lds = new LineDataSet(entries, "");
        UiHelper.applyAppearanceForLineDataSet(this, lds);
        LineData ld = new LineData(lds);
        ld.setDrawValues(true);
        ld.setValueTextColor(R.color.colorAccent);
        ld.setValueTextSize(18f);
        _chartQuizLog.setData(ld);
        _chartQuizLog.getXAxis().setLabelCount(last5.length);


        if (_favouriteMenu != null) {
            _favouriteMenu.setChecked(_context.isFavourite());
            _favouriteMenu.setIcon(_favouriteMenu.isChecked() ? R.drawable.ic_favorite_pink_24dp : R.drawable.ic_favorite_border_white_24dp);
        }

        if (_hideMenu != null) {
            _hideMenu.setTitle(_context.isHidden() ? R.string.undoHide : R.string.hide);
        }
    }

    @Override
    public void onBackPressed() {
        if (_hidden) setResult(RESULT_HIDDEN);
        else if (_edited)
            setResult(RESULT_EDITED);
        super.onBackPressed();
    }
}
