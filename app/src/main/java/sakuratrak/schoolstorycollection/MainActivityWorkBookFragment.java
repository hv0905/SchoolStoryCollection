package sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivityWorkBookFragment extends Fragment {

    private ConstraintLayout _root;
    private RecyclerView _itemList;
    private FloatingActionButton _addItemBtn;
    private Runnable _notifyToUpdate;

    public Runnable getNotifyToUpdate() {
        return _notifyToUpdate;
    }

    public void setNotifyToUpdate(Runnable _notifyToUpdate) {
        this._notifyToUpdate = _notifyToUpdate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _root = (ConstraintLayout) inflater.inflate(R.layout.fragment_main_activity_workbook,container,false);
        _itemList = _root.findViewById(R.id.itemList);
        _addItemBtn = _root.findViewById(R.id.addItemBtn);

        _addItemBtn.setOnClickListener(v -> {
            //navigate to add activity
            CommonAlerts.AskQuestionType(getParent(), (dialogInterface, i) -> {
                Intent intent = new Intent(getParent(),NewQuestionActivity.class);
                intent.putExtra(NewQuestionActivity.EXTRA_QUESTION_TYPE_ID,i);
                intent.putExtra(NewQuestionActivity.EXTRA_SUBJECT,getParent().getCurrentSubject());
                startActivity(intent);
            }, null);
        });

        return _root;
    }

    private MainActivity getParent() {
        return (MainActivity) getActivity();
    }

    public void refreshList() {

    }
}
