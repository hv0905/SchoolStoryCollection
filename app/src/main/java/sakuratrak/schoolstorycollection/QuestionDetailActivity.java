package sakuratrak.schoolstorycollection;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
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

    Toolbar _toolbar;
    CollapsingToolbarLayout _toolbarLayout;
    ImageView _imageTop;
    TextView _questionText;
    TextView _analysisText;

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


        _toolbar = findViewById(R.id.toolbar);
        _toolbarLayout = findViewById(R.id.toolbarLayout);
        _imageTop = findViewById(R.id.imageTop);
        _questionText = findViewById(R.id.questionText);
        _analysisText = findViewById(R.id.analysisText);


        setSupportActionBar(_toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        //load context
        getSupportActionBar().setTitle(_context.getTitle());
        _imageTop.setImageURI(Uri.fromFile(AppMaster.getThumbFile(this, _context.getQuestionImage().get(0))));

        _questionText.post(() -> loadMarkdown(_questionText, _context.getQuestionDetail()));
        _analysisText.post(() -> loadMarkdown(_analysisText, _context.getAnalysisDetail()));
    }

    void loadMarkdown(TextView textView, String text) {
        Spanned spanned = MarkDown.fromMarkdown(text, source -> null, textView);
        textView.setText(spanned);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.detail_top_options, menu);
        //todo add menu operate
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
                return true;
            case R.id
                    .editMenu:
                return true;

            case R.id
                    .shareMenu:
                return true;

        }
        return false;
    }


}
