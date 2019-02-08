package net.sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import net.sakuratrak.schoolstorycollection.core.AppHelper;
import net.sakuratrak.schoolstorycollection.core.AppMaster;
import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;
import net.sakuratrak.schoolstorycollection.core.DbManager;

import java.io.File;
import java.sql.SQLException;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingActivity extends AppCompatActivity {

    public static final String TAG = "SettingActivity";
    private static final String[] DIALOG_SETTINGS = {
            AppSettingsMaster.SETTINGS_DIALOG_HIDE_CONFIRM,
            AppSettingsMaster.SETTINGS_DIALOG_UNIT_HIDE_CONFIRM,
            AppSettingsMaster.SETTINGS_DIALOG_HELP_ADD_QUESTION,
            AppSettingsMaster.SETTINGS_DIALOG_HELP_NO_UNIT,
    };
    private MainSettingFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_setting);
        fragment = new MainSettingFragment();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
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
                setPreferencesFromResource(R.xml.prefenerce_main, key);
            } else {
                setPreferencesFromResource(R.xml.prefenerce_main, s);
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
                        Intent intVote = new Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.office.com/Pages/ResponsePage.aspx?id=DQSIkWdsW0yxEjajBLZtrQAAAAAAAAAAAAN__tALAnlURTUzSTVLWjBHVVA5R1E3Wk44N0ZKNVFLTy4u"));
                        if (intVote.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivity(intVote);
                        }

                        break;
                    case "feedback_github":
                        Intent intGh = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/EdgeNeko/SchoolStoryCollection_FeedbackOnly"));
                        if (intGh.resolveActivity(getContext().getPackageManager()) != null) {
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
                            new AlertDialog.Builder(getContext())
                                    .setMessage("未检测到邮件应用\n请手动发送邮件到mchxfeedback@126.com")
                                    .setTitle(R.string.feedback)
                                    .setIcon(R.drawable.ic_info_black_24dp)
                                    .show();
                        }
                        break;
                    case "resetDialog":
                        for (String item :
                                DIALOG_SETTINGS) {
                            AppSettingsMaster.setBooleanVal(getContext(), item, false);
                        }

                        Toast.makeText(getContext(), "已全部重置", Toast.LENGTH_LONG).show();
                        break;
                    case "clearStatInfo":
                        CheckBox cb = new CheckBox(getContext());
                        cb.setText("我没有手滑");
                        new AlertDialog.Builder(getContext())
                                .setIcon(R.drawable.ic_warning_black_24dp)
                                .setTitle(R.string.warning)
                                .setMessage("本操作不可逆!!!\n确定要重置所有题目(包括已归档的题目)的统计信息吗?\n确定要重置所有题目(包括已归档的题目)的统计信息吗?\n确定要重置所有题目(包括已归档的题目)的统计信息吗?")
                                .setView(cb)
                                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                                    if (cb.isChecked()) {
                                        try {
                                            DbManager dbm = DbManager.getDefaultHelper(getContext());
                                            dbm.getExerciseLogs().deleteBuilder().delete();
                                            dbm.getExerciseLogGroups().deleteBuilder().delete();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(getContext(), "清除成功", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getContext(), "已取消", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .show();
                        break;
                    case "clearAll":
                        CheckBox cb1 = new CheckBox(getContext());
                        cb1.setText("我没有手滑");
                        new AlertDialog.Builder(getContext())
                                .setIcon(R.drawable.ic_warning_black_24dp)
                                .setTitle(R.string.warning)
                                .setMessage("本操作不可逆!!!\n确定要清空整个错题本吗?\n确定要清空整个错题本吗?\n确定要清空整个错题本吗?")
                                .setView(cb1)
                                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                                    if (cb1.isChecked()) {
                                        DbManager.releaseCurrentHelper();
                                        File toDelete = AppSettingsMaster.getWorkbookRootDir(getContext());
                                        AppHelper.deleteDir(toDelete);
                                        Toast.makeText(getContext(), "清除成功", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getContext(), "已取消", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .show();
                        break;
                    case "clearThumbCache":
                        new AlertDialog.Builder(getContext())
                                .setIcon(R.drawable.ic_warning_black_24dp)
                                .setTitle("清除缩略图缓存?")
                                .setMessage("将清除缩略图缓存?")
                                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                                    File toDelete = AppMaster.getLocalThumbCacheDir(getContext());
                                    AppHelper.clearDir(toDelete);
                                    Toast.makeText(getContext(), "清除成功", Toast.LENGTH_LONG).show();
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .show();
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
