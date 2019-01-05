package net.sakuratrak.schoolstorycollection.core;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "ExerciseLog")
public class ExerciseLog implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int correctRatio;

    @DatabaseField
    private Date happenedTime;

    @DatabaseField(foreign = true ,foreignAutoRefresh = true)
    private QuestionInfo question;

    //region simple getter and setter


    public ExerciseLog() {
        this.happenedTime = new Date();
    }

    public ExerciseLog(int correctRatio, Date happenedTime, QuestionInfo question) {
        this.correctRatio = correctRatio;
        this.happenedTime = happenedTime;
        this.question = question;
    }

    public ExerciseLog(int correctRatio, QuestionInfo question) {
        this.correctRatio = correctRatio;
        this.question = question;
        this.happenedTime = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCorrectRatio() {
        return correctRatio;
    }

    public void setCorrectRatio(int correctRatio) {
        this.correctRatio = correctRatio;
    }

    public Date getHappenedTime() {
        return happenedTime;
    }

    public void setHappenedTime(Date happenedTime) {
        this.happenedTime = happenedTime;
    }

    public QuestionInfo getQuestion() {
        return question;
    }

    public void setQuestion(QuestionInfo question) {
        this.question = question;
    }

    //endregion
}
