package net.sakuratrak.schoolstorycollection.core;

import java.util.ArrayList;
import java.util.List;

public class QuizHelper {

    public static List<QuestionInfo> prepareRandomQuiz(List<QuestionInfo> questions, int n) {
        List<QuestionInfo> qi = new ArrayList<>(questions);
        if (qi.size() == 0) {
            return null;
        }
        ArrayList<QuestionInfo> quizContext = new ArrayList<>();
        if (qi.size() < n) {
            //全测
            quizContext.addAll(qi);
        } else {
            int[] indexes = MathHelper.getMultiRandomItem(qi.size(), n);
            for (int index : indexes) {
                quizContext.add(qi.get(index));
            }
        }
        return quizContext;
    }

    public static List<QuestionInfo> prepareSmartQuiz(List<QuestionInfo> questionInfos, int n) {
        List<QuestionInfo> qi = new ArrayList<>(questionInfos);
        if (qi.size() == 0) {
            return null;
        }
        ArrayList<QuestionInfo> quizContext = new ArrayList<>();
        if (qi.size() < n) {
            //全测
            quizContext.addAll(qi);
        } else {
            for (int i = 0; i < qi.size(); i++) {
                QuestionInfo item = qi.get(i);
                if (item.isHidden() || (item.getUnit() != null && item.getUnit().isHidden())) {
                    qi.remove(i);
                    i--;
                }
            }
            int[] randomChance = new int[qi.size()];
            for (int i = 0; i < qi.size(); i++) {
                QuestionInfo item = qi.get(i);
                randomChance[i] = 110 - item.computeReviewValue();

            }
            int[] result = MathHelper.getMultiRandomItemWithProportion(randomChance, n);
            for (int item : result) {
                quizContext.add(qi.get(item));
            }
        }
        return quizContext;
    }
}
