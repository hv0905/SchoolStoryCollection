package net.sakuratrak.schoolstorycollection.core;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

public final class StatHelper {


    /**
     * @param n 举个例子,如果是7,那么是获取今天~前6天的
     */
    public static int[] getLastNDaysQuizCount(ExerciseLogGroup.DbHelper helper, int n, LearningSubject subj) {
        try {
            Calendar today = Calendar.getInstance();
            List<ExerciseLogGroup> groups = helper.findAllWithSubject(subj);
            int[] result = new int[n];
            for (int i = groups.size() - 1; i >= 0; i--) {
                ExerciseLogGroup item = groups.get(i);
                Calendar current = Calendar.getInstance();
                current.setTime(item.getHappendTime());
                int delta = AppHelper.getDeltaDay(current, today);
                if (delta < n) {
                    result[delta] += item.getLogs().size();
                }
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



}