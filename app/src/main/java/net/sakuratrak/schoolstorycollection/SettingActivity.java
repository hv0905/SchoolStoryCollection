package net.sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    public MainSettingFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_setting);
        fragment = new MainSettingFragment();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,fragment).commit();
    }

    public static class MainSettingFragment extends PreferenceFragmentCompat
    {
        public int easterEggClickTime = 0;

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            if(getArguments() != null){
                String key = getArguments().getString("rootKey");
                setPreferencesFromResource(R.xml.prefenerce_main, key);
            }else{
                setPreferencesFromResource(R.xml.prefenerce_main,s);
            }
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            Log.d("setting", "onPreferenceTreeClick: " + preference.getKey() + preference.getTitle());
            if(preference.getKey() != null) {
                switch (preference.getKey()) {
                    case "easterEgg":
                        easterEggClickTime++;
                        if (easterEggClickTime >= 6) {
                            Toast.makeText(getContext(), "无论前方如何,请不要后悔与我的相遇\n      -- 古河渚", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case "feedback":
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:mchxfeedback@126.com"));
                        intent.putExtra(Intent.EXTRA_EMAIL,"mchxfeedback@126.com");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "School Story Collection反馈");
                        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivity(intent);
                        }else{
                            new AlertDialog.Builder(getContext())
                                    .setMessage("未检测到邮件应用\n请手动发送邮件到mchxfeedback@126.com")
                                    .setTitle(R.string.feedback)
                                    .setIcon(R.drawable.ic_info_black_24dp)
                                    .show();
                        }
                }
            }
            return super.onPreferenceTreeClick(preference);
        }

        @Override
        public void onDisplayPreferenceDialog(Preference preference)
        {
            Log.d("settings", "onDisplayPreferenceDialog: " + preference.getKey() + preference.getTitle());
            DialogFragment dialogFragment = null;
            if (preference instanceof TimePreference)
            {
                dialogFragment = new TimePreferenceDialogFragmentCompat();
                Bundle bundle = new Bundle(1);
                bundle.putString("key", preference.getKey());
                dialogFragment.setArguments(bundle);
            }

            if (dialogFragment != null)
            {
                dialogFragment.setTargetFragment(this, 0);
                if (this.getFragmentManager() != null) {
                    dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
                }
            }
            else
            {
                super.onDisplayPreferenceDialog(preference);
            }
        }

        @Override
        public void onNavigateToScreen(PreferenceScreen preferenceScreen){
            MainSettingFragment applicationPreferencesFragment = new MainSettingFragment();
            Bundle args = new Bundle();
            args.putString("rootKey", preferenceScreen.getKey());
            applicationPreferencesFragment.setArguments(args);
            //noinspection ConstantConditions
            getFragmentManager()
                    .beginTransaction()
                    .replace(getId(), applicationPreferencesFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }
}
