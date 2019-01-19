package net.sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.ExerciseLog;
import net.sakuratrak.schoolstorycollection.core.ExerciseLogGroup;
import net.sakuratrak.schoolstorycollection.core.ListDataProvider;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class QuizResultActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "groupId";
    private Toolbar _toolbar;
    private AppBarLayout _appBar;
    private TextView _textStory;
    private RecyclerView _recycleQuestions;
    private ExerciseLogAdapter _adapter;
    private CollapsingToolbarLayout _toolbarLayout;
    private ArrayList<ExerciseLogAdapter.DataContext> _contextList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        _toolbar = findViewById(R.id.toolbar);
        _appBar = findViewById(R.id.app_bar);
        _textStory = findViewById(R.id.textStory);
        _recycleQuestions = findViewById(R.id.recycleQuestions);
        _toolbarLayout = findViewById(R.id.toolbar_layout);

        setSupportActionBar(_toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        _recycleQuestions.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        _recycleQuestions.addItemDecoration(new RecycleViewDivider(RecyclerView.VERTICAL, this));

        //refresh
        if (!getIntent().hasExtra(EXTRA_GROUP_ID)) {
            finish();
            return;
        }
        //ArrayList<Integer> ids = getIntent().getIntegerArrayListExtra(EXTRA_RESULT_IDS);
        //Dao<ExerciseLog, Integer> questionInfos = DbManager.getDefaultHelper(this).getExerciseLogs();
        ExerciseLogGroup group;
        try {
            group = DbManager.getDefaultHelper(this).getExerciseLogGroups().queryForId(getIntent().getIntExtra(EXTRA_GROUP_ID, 0));
        } catch (SQLException e) {
            e.printStackTrace();
            finish();
            return;
        }
        int scoreSum = 0;
        _contextList = new ArrayList<>();
        int i = 0;
        for (ExerciseLog log : group.getLogs()) {
            _contextList.add(new ExerciseLogAdapter.DataContext(
                    ++i,
                    log.getQuestion().getTitle(),
                    log.getQuestion().getUnit() == null ? getString(R.string.emptyUnit) : log.getQuestion().getUnit().getName(),
                    log.getCorrectRatio(),
                    v -> {
                        Intent intent = new Intent(this, QuestionDetailActivity.class);
                        intent.putExtra(QuestionDetailActivity.EXTRA_QUESTION_ID, log.getQuestion().getId());
                        startActivity(intent);
                    }
            ));
        }

            int scoreAvg = group.getAvgScore();
            int uiColor = UiHelper.getWarnColorByScore(getResources(),scoreAvg);
            int scoreLevel = scoreAvg / 25;
        switch (scoreLevel) {
            case 0://0-25
                _textStory.setText(R.string.quizResultStory0);
                break;
            case 1://25-50
                _textStory.setText(R.string.quizResultStory1);
                break;
            case 2://50-75
                _textStory.setText(R.string.quizResultStory2);
                break;
            case 3://75-100
                _textStory.setText(R.string.quizResultStory3);
                break;
            default://100+
                _textStory.setText(R.string.quizResultStory4);
                break;
        }
        _toolbar.setBackgroundColor(uiColor);
        _appBar.setBackgroundColor(uiColor);
        _toolbarLayout.setBackgroundColor(uiColor);
        _toolbarLayout.setContentScrimColor(uiColor);
        getWindow().setStatusBarColor(uiColor);

        _toolbar.setBackgroundColor(uiColor);
            _appBar.setBackgroundColor(uiColor);
            _toolbarLayout.setBackgroundColor(uiColor);
            _toolbarLayout.setContentScrimColor(uiColor);
            getWindow().setStatusBarColor(uiColor);

            String title = String.format(Locale.ENGLISH, "%d%%", scoreAvg);
            _toolbar.setTitle(title);
            getSupportActionBar().setTitle(title);
            _toolbarLayout.setTitle(title);
            setTitle(title);

            _adapter = new ExerciseLogAdapter(new ListDataProvider<>(_contextList));

            _recycleQuestions.setAdapter(new AlphaInAnimationAdapter(_adapter));

        }


        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            if (item.getItemId() == android.R.id.home) {
                onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
