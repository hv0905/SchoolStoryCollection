package sakuratrak.schoolstorycollection;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzhoujay.markdown.MarkDown;

import java.sql.SQLException;

import sakuratrak.schoolstorycollection.core.AppMaster;
import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.QuestionInfo;

public class QuestionDetailActivity extends AppCompatActivity {

    public static final String EXTRA_QUESTION_ID = "questionId";
    public static final String TAG = "QuestionDetail";

    private QuestionInfo _context;

    //region uiElements

    AppBarLayout _appBar;
    Toolbar _toolbar;
    CollapsingToolbarLayout _toolbarLayout;
    ImageView _imageTop;
    TextView _questionText;
    TextView _analysisText;
    FrameLayout _answerContainer;
    AnswerUiDisplayView _answerContent;
    FloatingActionButton _showAnswerButton;
    LinearLayout _answerZone;

    MenuItem _showAnswerMenu;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);


        if (getIntent().hasExtra(EXTRA_QUESTION_ID)) {
            try {
                _context = DbManager.getDefaultHelper(this).getQuestionInfos().queryForId(getIntent().getIntExtra(EXTRA_QUESTION_ID, 0));
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.sqlExp, Toast.LENGTH_LONG).show();
            }

        } else finish();


        _appBar = findViewById(R.id.app_bar);
        _toolbar = findViewById(R.id.toolbar);
        _toolbarLayout = findViewById(R.id.toolbarLayout);
        _imageTop = findViewById(R.id.imageTop);
        _questionText = findViewById(R.id.questionText);
        _analysisText = findViewById(R.id.analysisText);
        _answerContainer = findViewById(R.id.answerContainer);
        _showAnswerButton = findViewById(R.id.showAnswerButton);
        _answerZone = findViewById(R.id.answerZone);


        setSupportActionBar(_toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        _appBar.addOnOffsetChangedListener((appBarLayout, i) -> {
            if (Math.abs(i)-appBarLayout.getTotalScrollRange() == 0)
            {
                if(_showAnswerMenu != null){
                    _showAnswerMenu.setVisible(true);
                }
            }
            else
            {
                if(_showAnswerMenu != null){
                    _showAnswerMenu.setVisible(false);
                }
            }
        });

        _showAnswerButton.setOnClickListener(v -> toggleAnswer());

        //load context
        getSupportActionBar().setTitle(_context.getTitle());
        _imageTop.setImageURI(Uri.fromFile(AppMaster.getThumbFile(this, _context.getQuestionImage().get(0))));

        _questionText.post(() -> loadMarkdown(_questionText, _context.getQuestionDetail()));
        _analysisText.post(() -> loadMarkdown(_analysisText, _context.getAnalysisDetail()));

        _answerContent = _context.getType().getDisplayView(this);
        _answerContent.setAnswer(_context.getAnswer());
        _answerContainer.addView(_answerContent);

    }

    void loadMarkdown(TextView textView, String text) {
        Spanned spanned = MarkDown.fromMarkdown(text, source -> null, textView);
        textView.setText(spanned);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        _toolbar.inflateMenu(R.menu.detail_top_options);
        _showAnswerMenu = _toolbar.getMenu().findItem(R.id.showAnswerMenu);
        return true;
    }

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
                return true;

        }
        return false;
    }

    public void gotoEdit(){
        Intent intent = new Intent(this,QuestionEditActivity.class);
        intent.putExtra(QuestionEditActivity.EXTRA_CONTEXT_ID,_context.getId());
        startActivity(intent);
    }

    public void toggleAnswer(){
        if(_answerZone.getVisibility() == View.VISIBLE){
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
        }else{
            //show
            _answerZone.setAlpha(0);
            _answerZone.setVisibility(View.VISIBLE);
            _answerZone.animate().setDuration(200).alpha(1).setListener(null);
        }
    }


}
