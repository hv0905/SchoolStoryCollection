package sakuratrak.schoolstorycollection;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.TimePicker;

public class TimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat implements DialogPreference.TargetFragment
{
    TimePicker timePicker = null;

    @Override
    protected View onCreateDialogView(Context context)
    {
        timePicker = new TimePicker(context);
        return (timePicker);
    }

    @Override
    protected void onBindDialogView(View v)
    {
        super.onBindDialogView(v);
        timePicker.setIs24HourView(true);
        TimePreference pref = (TimePreference) getPreference();
        timePicker.setCurrentHour(pref.hour);
        timePicker.setCurrentMinute(pref.minute);
    }

    @Override
    public void onDialogClosed(boolean positiveResult)
    {
        if (positiveResult)
        {
            TimePreference pref = (TimePreference) getPreference();
            pref.hour = timePicker.getCurrentHour();
            pref.minute = timePicker.getCurrentMinute();

            String value = TimePreference.timeToString(pref.hour, pref.minute);
            if (pref.callChangeListener(value)) pref.persistStringValue(value);
        }
    }

    @Override
    public Preference findPreference(CharSequence charSequence)
    {
        return getPreference();
    }
}