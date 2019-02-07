package net.sakuratrak.schoolstorycollection;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Calendar;


public class DayOfWeekAxisValveFormatter implements IAxisValueFormatter {

    private static final String[] WEEK_CHINESE = {"日", "一", "二", "三", "四", "五", "六"};

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar currentDay = Calendar.getInstance();
        currentDay.add(Calendar.DAY_OF_YEAR, (int) (1 - value));
        return "星期" + WEEK_CHINESE[currentDay.get(Calendar.DAY_OF_WEEK) - 1];
    }
}
