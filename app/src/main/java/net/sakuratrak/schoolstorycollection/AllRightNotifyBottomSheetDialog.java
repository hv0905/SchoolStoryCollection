package net.sakuratrak.schoolstorycollection;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public final class AllRightNotifyBottomSheetDialog extends BottomSheetDialogFragment {

    public ViewGroup _root;
    public Button _buttonNext;
    public Button _buttonShowAnswer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _root = (ViewGroup) inflater.inflate(R.layout.layout_allright,container,false);
        _buttonNext = _root.findViewById(R.id.buttonNext);
        _buttonShowAnswer = _root.findViewById(R.id.buttonShowAnswer);
        return _root;
    }
}
