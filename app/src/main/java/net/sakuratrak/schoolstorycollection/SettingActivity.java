package net.sakuratrak.schoolstorycollection;

import android.R.id;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import net.sakuratrak.schoolstorycollection.R.drawable;
import net.sakuratrak.schoolstorycollection.R.string;
import net.sakuratrak.schoolstorycollection.R.xml;
import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;

import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingActivity extends AppCompatActivity {

    private static final String[] DIALOG_SETTINGS = {
            AppSettingsMaster.SETTINGS_DIALOG_HIDE_CONFIRM,
            AppSettingsMaster.SETTINGS_DIALOG_UNIT_HIDE_CONFIRM
    };

    public static final String TAG = "SettingActivity";
    private MainSettingFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_setting);
        fragment = new MainSettingFragment();
        getSupportFragmentManager().beginTransaction().replace(id.content, fragment).commit();
    }

    @Override
    protected void onStop() {
        //使用最新的设定更新定时提醒订阅
        AlarmReceiver.setupAlarm(this, false);
        super.onStop();
    }

    public static class MainSettingFragment extends PreferenceFragmentCompat {
        protected int easterEggClickTime = 0;

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            if (getArguments() != null) {
                String key = getArguments().getString("rootKey");
                setPreferencesFromResource(xml.prefenerce_main, key);
            } else {
                setPreferencesFromResource(xml.prefenerce_main, s);
            }
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            Log.d("setting", "onPreferenceTreeClick: " + preference.getKey() + preference.getTitle());
            if (preference.getKey() != null) {
                switch (preference.getKey()) {
                    case "easterEgg":
                        easterEggClickTime++;
                        if (easterEggClickTime >= 6) {
                            Toast.makeText(getContext(), "无论前方如何,请不要后悔与我的相遇\n      -- 古河渚", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case "feedback_vote":
                        Intent intVote = new Intent(Intent.ACTION_VIEW,Uri.parse("https://forms.office.com/Pages/ResponsePage.aspx?id=DQSIkWdsW0yxEjajBLZtrQAAAAAAAAAAAAN__tALAnlURTUzSTVLWjBHVVA5R1E3Wk44N0ZKNVFLTy4u"));
                        if(intVote.resolveActivity(getContext().getPackageManager()) != null){
                            startActivity(intVote);
                        }

                        break;
                    case "feedback_github":
                        Intent intGh = new Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/EdgeNeko/SchoolStoryCollection_FeedbackOnly"));
                        if(intGh.resolveActivity(getContext().getPackageManager()) != null){
                            startActivity(intGh);
                        }
                        break;
                    case "feedback_mail":
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:mchxfeedback@126.com"));
                        intent.putExtra(Intent.EXTRA_EMAIL, "mchxfeedback@126.com");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "School Story Collection反馈");
                        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            new Builder(getContext())
                                    .setMessage("未检测到邮件应用\n请手动发送邮件到mchxfeedback@126.com")
                                    .setTitle(string.feedback)
                                    .setIcon(drawable.ic_info_black_24dp)
                                    .show();
                        }
                        break;
                    case "resetDialog":
                        for (String item :
                                DIALOG_SETTINGS) {
                            AppSettingsMaster.setBooleanVal(getContext(), item, false);
                        }

                        Toast.makeText(getContext(),"已全部重置",Toast.LENGTH_LONG).show();
                        break;
                }
            }
            return super.onPreferenceTreeClick(preference);
        }

        @Override
        public void onDisplayPreferenceDialog(Preference preference) {
            Log.d("settings", "onDisplayPreferenceDialog: " + preference.getKey() + preference.getTitle());
            DialogFragment dialogFragment = null;
            if (preference instanceof TimePreference) {
                dialogFragment = new TimePreferenceDialogFragmentCompat();
                Bundle bundle = new Bundle(1);
                bundle.putString("key", preference.getKey());
                dialogFragment.setArguments(bundle);
            }

            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0);
                if (this.getFragmentManager() != null) {
                    dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
                }
            } else {
                super.onDisplayPreferenceDialog(preference);
            }
        }

        @Override
        public void onNavigateToScreen(PreferenceScreen preferenceScreen) {
            MainSettingFragment applicationPreferencesFragment = new MainSettingFragment();
            Bundle args = new Bundle();
            args.putString("rootKey", preferenceScreen.getKey());
            applicationPreferencesFragment.setArguments(args);
            //noinspection ConstantConditions
            getFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(getId(), applicationPreferencesFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }
}
