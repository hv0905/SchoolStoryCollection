package sakuratrak.schoolstorycollection.core;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;

@DatabaseTable(tableName = "QuestionInfo")
public final class QuestionInfo implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false)
    private String Title;

    @DatabaseField(canBeNull = false)
    private LearningSubject Subject;

    @DatabaseField(canBeNull = false)
    private QuestionType Type;

    @DatabaseField()
    public ArrayList<String> MainImage;

    @DatabaseField()
    public ArrayList<String> AnalysisImage;

    @DatabaseField()
    public Answer Answer;

    @DatabaseField()
    public ArrayList<Integer> ExerciseLogIds;

    @DatabaseField
    public int UnitId;


    public QuestionInfo(){

    }

    public QuestionInfo(String title,LearningSubject subject,QuestionType type){
        Title = title;
        Subject = subject;
        Type = type;
    }


}
