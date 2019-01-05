package net.sakuratrak.schoolstorycollection;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private WebView _mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


    }

    @Override
    protected void onStart() {
        super.onStart();
        _mainView = findViewById(R.id.mainView);
//        _mainView.loadMarkdownFile("file:///android_asset/about.md");
//        _mainView.addStyleSheet(new Github());
//        _mainView.loadMarkdownFromAsset("about.md");

//        _mainView.post(() -> {
//            try {
//                Spanned spanned = MarkDown.fromMarkdown(getAssets().open("about.md"), (Html.ImageGetter) source -> null, _mainView);
//                _mainView.setText(spanned);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        });

        _mainView.loadUrl("file:///android_asset/aboutwww/about.html");

    }
}
