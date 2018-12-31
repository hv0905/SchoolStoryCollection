package net.sakuratrak.schoolstorycollection;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public final class MainActivityQuizFragment extends Fragment {

    public static final String TAG = "MainWindow_Quiz";

    public ViewGroup _root;
    public Button _buttonTest;

    public MainActivityQuizFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _root = (ViewGroup) inflater.inflate(R.layout.fragment_main_activity_quiz, container, false);
        _buttonTest = _root.findViewById(R.id.buttonTest);


        _buttonTest.setOnClickListener(v -> AlarmReceiver.setupAlarm(getContext(),false));
        return _root;

    }

}
