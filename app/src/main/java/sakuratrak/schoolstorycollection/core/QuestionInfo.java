package sakuratrak.schoolstorycollection.core;


import android.support.annotation.Nullable;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

@DatabaseTable(tableName = "QuestionInfo")
public final class QuestionInfo implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField()
    private String title;

    @DatabaseField
    private String detail;

    @DatabaseField(canBeNull = false)
    private int subjectId;

//    @DatabaseField(dataType = DataType.SERIALIZABLE)
//    private ArrayList<Integer> exerciseLogIds;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<ExerciseLog> exerciseLogs;

    @DatabaseField(canBeNull = false)
    private int typeId;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> mainImage;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> analysisImage;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private Answer answer;


//    @DatabaseField
//    private int unitId;
    @DatabaseField(foreign = true)
    private LearningUnitInfo unit;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getMainImage() {
        return mainImage;
    }

    public void setMainImage(ArrayList<String> mainImage) {
        this.mainImage = mainImage;
    }

    public ArrayList<String> getAnalysisImage() {
        return analysisImage;
    }

    public void setAnalysisImage(ArrayList<String> analysisImage) {
        this.analysisImage = analysisImage;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public LearningSubject getSubject() {
        return LearningSubject.id2Obj(subjectId);
    }

    public void setSubject(LearningSubject value) {
        subjectId = value.getId();
    }

    public QuestionType getType() {
        return QuestionType.id2Obj(typeId);
    }

    public void setType(QuestionType value){
        typeId = value.getId();

    }

    @Nullable
    public LearningUnitInfo getUnit() {
        return unit;
    }

    public Collection<ExerciseLog> getExerciseLogs() {
        return exerciseLogs;
    }

    public QuestionInfo(){

    }

    public QuestionInfo(String title,LearningSubject subject,QuestionType type){
        this.title = title;
        subjectId = subject.getId();
        setType(type);
    }

    

}
