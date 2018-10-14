package sakuratrak.schoolstorycollection.core;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;

@DatabaseTable(tableName = "LearningUnitInfo")
public class LearningUnitInfo implements Serializable {
    public static final int NEED_MORE_MAX = 5;

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public String Name;

    @DatabaseField
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
        return ExerciseLogs.size() < NEED_MORE_MAX;
    }
}
