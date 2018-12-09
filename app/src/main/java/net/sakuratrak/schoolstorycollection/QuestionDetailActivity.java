package net.sakuratrak.schoolstorycollection;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zzhoujay.markdown.MarkDown;

import net.sakuratrak.schoolstorycollection.core.AppMaster;
import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;
import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.ImageAnswer;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class QuestionDetailActivity extends AppCompatActivity {

    public static final String EXTRA_QUESTION_ID = "questionId";
    public static final String TAG = "QuestionDetail";

    public static final int RESULT_EDITED = 200;
    public static final int RESULT_DELETED = 201;

    private static final int REQUEST_EDIT = 100;

    private QuestionInfo _context;

    private boolean _edited = false;

    //region uiElements

    AppBarLayout _appBar;
    Toolbar _toolbar;
    CollapsingToolbarLayout _toolbarLayout;
    ImageView _imageTopContent;
    FrameLayout _imageTopBorder;
    TextView _questionText;
    TextView _analysisText;
    FrameLayout _answerContainer;
    AnswerUiDisplayView _answerContent;
    FloatingActionButton _showAnswerButton;
    LinearLayout _answerZone;
    ImageDisplayView _questionImgDisplay;
    ImageDisplayView _analysisImgDisplay;
    TextView _valCreateTime;
    RatingBar _valDifficulty;
    TextView _valUnit;

    MenuItem _showAnswerMenu;
    MenuItem _favouriteMenu;

    //endregion

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

        setSupportActionBar(_toolbar);

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

        _showAnswerButton.setOnClickListener(v -> toggleAnswer());

        //load context
        refresh();

    }

    void loadMarkdown(TextView textView, String text) {
        Spanned spanned = MarkDown.fromMarkdown(text, source -> null, textView);
        textView.setText(spanned);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        _toolbar.inflateMenu(R.menu.detail_top_options);
        _showAnswerMenu = _toolbar.getMenu().findItem(R.id.showAnswerMenu);
        _favouriteMenu = _toolbar.getMenu().findItem(R.id.favourite);
        if(_toolbar.getMenu() instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            //noinspection RestrictedApi
            m.setOptionalIconsVisible(true);
        }

        _favouriteMenu.setChecked(_context.isFavourite());
        _favouriteMenu.setIcon(_favouriteMenu.isChecked() ? R.drawable.ic_favorite_pink_24dp: R.drawable.ic_favorite_border_white_24dp);

        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id
                    .showAnswerMenu:
                toggleAnswer();
                return true;
            case R.id
                    .editMenu:
                gotoEdit();
                return true;

            case R.id
                    .shareMenu:
                //todo share
                return true;
            case R.id
                    .hide:
                return true;
            case R.id.favourite:
                Log.d(TAG, "onOptionsItemSelected: " + item.isChecked());
                item.setChecked(!item.isChecked());
                item.setIcon(item.isChecked() ? R.drawable.ic_favorite_pink_24dp: R.drawable.ic_favorite_border_white_24dp);
                _context.setFavourite(item.isChecked());
                try {
                    DbManager.getDefaultHelper(this).getQuestionInfos().update(_context);
                    _edited = true;
                } catch (SQLException e) {
                    Snackbar.make(_toolbarLayout,R.string.sqlExp,Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                Snackbar.make(_toolbarLayout,String.format("已%s喜欢",item.isChecked() ? "": "取消"),Snackbar.LENGTH_LONG).show();

//                if(item.isChecked()){
//
//                }else{
//
//                }
                return true;

            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setTitle("删除确认").setMessage(String.format("将永久删除错题%s(真的很久!)!", _context.getTitle()))
                        .setPositiveButton(R.string.confirm, (dialog, which) -> {
                            try {
                                //delete all images
                                for(String item_ : _context.getQuestionImage()){
                                    File raw = new File(AppSettingsMaster.getWorkBookImageDir(this),item_);
                                    File tmb = new File(AppMaster.getLocalThumbCacheDir(this),item_);
                                    if(raw.exists())raw.delete();
                                    if(tmb.exists()) tmb.delete();
                                }
                                for(String item_ : _context.getAnalysisImage()){
                                    File raw = new File(AppSettingsMaster.getWorkBookImageDir(this),item_);
                                    File tmb = new File(AppMaster.getLocalThumbCacheDir(this),item_);
                                    if(raw.exists())raw.delete();
                                    if(tmb.exists()) tmb.delete();
                                }
                                if(_context.getAnswer() instanceof ImageAnswer){
                                    for(String item_ : ((ImageAnswer) _context.getAnswer()).Image){
                                        File raw = new File(AppSettingsMaster.getWorkBookImageDir(this),item_);
                                        File tmb = new File(AppMaster.getLocalThumbCacheDir(this),item_);
                                        if(raw.exists())raw.delete();
                                        if(tmb.exists()) tmb.delete();
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

        }
        return false;
    }

    public void gotoEdit() {
        Intent intent = new Intent(this, QuestionEditActivity.class);
        intent.putExtra(QuestionEditActivity.EXTRA_CONTEXT_ID, _context.getId());
        startActivityForResult(intent, REQUEST_EDIT);
    }

    public void toggleAnswer() {
        if (_answerZone.getVisibility() == View.VISIBLE) {
            //hide
            _answerZone.animate().setDuration(200).alpha(0).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    _answerZone.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {
            //show
            _answerZone.setAlpha(0);
            _answerZone.setVisibility(View.VISIBLE);
            _answerZone.animate().setDuration(200).alpha(1).setListener(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_EDIT:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(_toolbarLayout, "更改已保存", Snackbar.LENGTH_LONG).show();
                    _edited = true;
                    refresh();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refresh() {

        if (getIntent().hasExtra(EXTRA_QUESTION_ID)) {
            try {
                _context = DbManager.getDefaultHelper(this).getQuestionInfos().queryForId(getIntent().getIntExtra(EXTRA_QUESTION_ID, 0));
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.sqlExp, Toast.LENGTH_LONG).show();
            }

        } else finish();

        getSupportActionBar().setTitle(_context.getTitle());
        _toolbar.setTitle(_context.getTitle());
        _toolbarLayout.setTitle(_context.getTitle());
        _valDifficulty.setRating(_context.getDifficulty() / 2f);
        _valCreateTime.setText(new SimpleDateFormat("yy.mm.dd hh:mm:ss", Locale.US).format(_context.getAuthorTime()));
        _valUnit.setText(_context.getUnit() == null ? getText(R.string.emptyUnit) : _context.getUnit().getName());

        _imageTopContent.setImageURI(Uri.fromFile(AppMaster.getThumbFile(this, _context.getQuestionImage().get(0))));

        _questionText.post(() -> loadMarkdown(_questionText, _context.getQuestionDetail()));
        _analysisText.post(() -> loadMarkdown(_analysisText, _context.getAnalysisDetail()));



        _answerContent = _context.getType().getDisplayView(this);
        _answerContent.setAnswer(_context.getAnswer());
        _answerContainer.removeAllViews();
        _answerContainer.addView(_answerContent);

        _questionImgDisplay.setImages(_context.getQuestionImage());
        _analysisImgDisplay.setImages(_context.getAnalysisImage());

        if(_favouriteMenu != null){
            _favouriteMenu.setChecked(_context.isFavourite());
            _favouriteMenu.setIcon(_favouriteMenu.isChecked() ? R.drawable.ic_favorite_pink_24dp: R.drawable.ic_favorite_border_white_24dp);
        }
    }

    @Override
    public void onBackPressed() {
        if (_edited)
            setResult(RESULT_EDITED);
        super.onBackPressed();
    }
}
