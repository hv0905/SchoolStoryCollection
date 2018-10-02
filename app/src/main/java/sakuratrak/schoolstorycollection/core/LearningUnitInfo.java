package sakuratrak.schoolstorycollection.core;

import java.io.Serializable;
import java.util.ArrayList;

public class LearningUnitInfo implements Serializable {
    public static final int NEEDMORE_MAX = 5;

    public String Name;
    public ArrayList<ExerciseLog> ExerciseLogs;

    public LearningUnitInfo(){

    }

    public LearningUnitInfo(String name, ArrayList<ExerciseLog> exerciseLogs) {
        Name = name;
        ExerciseLogs = exerciseLogs;
    }

    public LearningUnitInfo(String name) {
        Name = name;
        ExerciseLogs = new ArrayList<>();
    }

    public int computeCorrectRatio(){
        if(ExerciseLogs.size() == 0){
            return 100;
        }
        int sum = 0;
        for(ExerciseLog item : ExerciseLogs){
            sum +=item.CorrectRatio;
        }
        return sum / ExerciseLogs.size();
    }

    public boolean getIfNeedMoreQuiz(){
        return ExerciseLogs.size() < NEEDMORE_MAX;
    }
}
