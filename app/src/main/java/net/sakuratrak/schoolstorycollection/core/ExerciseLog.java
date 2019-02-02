package net.sakuratrak.schoolstorycollection.core;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@DatabaseTable(tableName = "ExerciseLog")
public class ExerciseLog implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int correctRatio;

    @DatabaseField
    private Date happenedTime;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private QuestionInfo question;

//    @DatabaseField(dataType = DataType.UUID)
//    private UUID groupGuid;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private ExerciseLogGroup group;

    @DatabaseField
    private int subjectId;

    //region simple getter and setter


    public ExerciseLog() {
        this.happenedTime = new Date();
    }

    public ExerciseLog(int correctRatio, Date happenedTime, QuestionInfo question) {
        this.correctRatio = correctRatio;
        this.happenedTime = happenedTime;
        this.question = question;
        this.setSubject(question.getSubject());

    }

    public ExerciseLog(int correctRatio, QuestionInfo question) {
        this.correctRatio = correctRatio;
        this.question = question;
        this.happenedTime = new Date();
        this.setSubject(question.getSubject());

    }

    public ExerciseLog(int correctRatio, Date happenedTime, QuestionInfo question, ExerciseLogGroup group) {
        this.correctRatio = correctRatio;
        this.happenedTime = happenedTime;
        this.question = question;
        this.group = group;
        this.setSubject(question.getSubject());


    }

    public ExerciseLog(int correctRatio, QuestionInfo question, ExerciseLogGroup group) {
        this.correctRatio = correctRatio;
        this.question = question;
        this.group = group;
        this.setSubject(question.getSubject());
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

    public ExerciseLogGroup getGroup() {
        return group;
    }

    public void setGroup(ExerciseLogGroup group) {
        this.group = group;
    }

    public LearningSubject getSubject() {
        return LearningSubject.id2Obj(subjectId);
    }

    public void setSubject(LearningSubject value) {
        subjectId = value.getId();
    }


    //endregion

    public static class DbHelper {
        final Dao<ExerciseLog, Integer> _base;

        public DbHelper(Dao<ExerciseLog, Integer> _base) {
            this._base = _base;
        }

        public DbHelper(DbManager mgr) {
            _base = mgr.getExerciseLogs();
        }

        public DbHelper(Context context){
            _base = DbManager.getDefaultHelper(context).getExerciseLogs();
        }

        public List<ExerciseLog> FindAllWithSubject(LearningSubject subject) throws SQLException {
            return _base.queryForEq("subjectId", subject.getId());
        }

    }
}
