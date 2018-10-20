package sakuratrak.schoolstorycollection.core;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@DatabaseTable(tableName = "ExerciseLog")
public class ExerciseLog implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int questionId;

    @DatabaseField
    private int correctRatio;

    @DatabaseField
    private Date happenedTime;

    @DatabaseField(foreign = true)
    private QuestionInfo question;

    //region simple getter and setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
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
