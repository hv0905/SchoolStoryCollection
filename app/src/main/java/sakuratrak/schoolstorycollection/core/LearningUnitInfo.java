package sakuratrak.schoolstorycollection.core;

import java.io.Serializable;
import java.util.ArrayList;

public class LearningUnitInfo implements Serializable {
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
}
