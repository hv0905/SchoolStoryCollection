package sakuratrak.schoolstorycollection.core;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;


@DatabaseTable(tableName = "LearningUnitInfo")
public class LearningUnitInfo implements Serializable {
    public static final int NEED_MORE_MAX = 5;


    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<ExerciseLog> exerciseLogs;

    @DatabaseField(canBeNull = false)
    private int subjectId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ExerciseLog> getExerciseLogs() {
        return exerciseLogs;
    }

    public void setExerciseLogs(ArrayList<ExerciseLog> exerciseLogs) {
        this.exerciseLogs = exerciseLogs;
    }

    public LearningSubject getSubject(){
        return LearningSubject.id2Obj(subjectId);
    }

    public void setSubject(LearningSubject value){
        subjectId = value.getId();
    }

    public LearningUnitInfo() {

    }

    public LearningUnitInfo(String name, ArrayList<ExerciseLog> exerciseLogs,LearningSubject subject) {
        this.name = name;
        this.exerciseLogs = exerciseLogs;
        setSubject(subject);
    }

    public LearningUnitInfo(String name,LearningSubject subject) {
        this.name = name;
        setSubject(subject);
        exerciseLogs = new ArrayList<>();
    }

    public int computeCorrectRatio() {
        if (exerciseLogs.size() == 0) {
            return 100;
        }
        int sum = 0;
        for (ExerciseLog item : exerciseLogs) {
            sum += item.correctRatio;
        }
        return sum / exerciseLogs.size();
    }

    public boolean getIfNeedMoreQuiz() {
        return exerciseLogs.size() < NEED_MORE_MAX;
    }
}
