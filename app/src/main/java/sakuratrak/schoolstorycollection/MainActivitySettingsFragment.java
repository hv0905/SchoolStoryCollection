package sakuratrak.schoolstorycollection;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public final class MainActivitySettingsFragment extends Fragment {

    public View _root;
    public MaterialButton _about;
    public MaterialButton _settingBtn;

    public MainActivitySettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _root =  inflater.inflate(R.layout.fragment_main_activity_settings, container, false);
        _about = _root.findViewById(R.id.main_settings_about);
        _settingBtn = _root.findViewById(R.id.settingBtn);


        _settingBtn.setOnClickListener(v -> {
            getParent().startActivity(new Intent(getActivity(),SettingActivity.class));
        });
        _about.setOnClickListener(v -> getParent().startActivity(new Intent(getParent(),AboutActivity.class)));


        return _root;
    }


    public MainActivity getParent(){
        return (MainActivity)getActivity();
    }

}
