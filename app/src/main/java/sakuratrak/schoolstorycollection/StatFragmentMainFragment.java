package sakuratrak.schoolstorycollection;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public final class StatFragmentMainFragment extends Fragment {

    LinearLayout _root;

    public StatFragmentMainFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _root = (LinearLayout) inflater.inflate(R.layout.fragment_stat_fragment_main,container,false);
        return _root;
    }
}
