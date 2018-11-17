package sakuratrak.schoolstorycollection;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import java.sql.SQLException;

import sakuratrak.schoolstorycollection.core.AppMaster;
import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.QuestionInfo;

public class QuestionDetailActivity extends AppCompatActivity {

    public static final String EXTRA_QUESTION_ID = "questionId";

    private QuestionInfo _context;

    //region uiElements

    Toolbar _toolbar;
    CollapsingToolbarLayout _toolbarLayout;
    ImageView _imageTop;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);


        if(getIntent().hasExtra(EXTRA_QUESTION_ID)){
            try {
                _context = DbManager.getDefaultHelper(this).getQuestionInfos().queryForId(getIntent().getIntExtra(EXTRA_QUESTION_ID,0));
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        _toolbar = findViewById(R.id.toolbar);
        _toolbarLayout = findViewById(R.id.toolbarLayout);
        _imageTop = findViewById(R.id.imageTop);



        setSupportActionBar(_toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //load context
        _toolbar.setTitle(_context.getTitle());
        _imageTop.setImageURI(Uri.fromFile(AppMaster.getThumbFile(this,_context.getQuestionImage().get(0))));

    }
}
