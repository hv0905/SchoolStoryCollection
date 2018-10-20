package sakuratrak.schoolstorycollection;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;

public class AboutActivity extends AppCompatActivity {

    private MarkdownView _mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


    }

    @Override
    protected void onStart() {
        super.onStart();
        _mainView = findViewById(R.id.mainView);
        _mainView.addStyleSheet(new Github());
        _mainView.loadMarkdownFromAsset("about.md");

    }
}
