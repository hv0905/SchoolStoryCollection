package net.sakuratrak.schoolstorycollection.core;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable
public final class ExerciseLogGroup {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String description;

    @DatabaseField
    private Date happendTime;

    @DatabaseField
    private int subjectId;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<ExerciseLog> logs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ForeignCollection<ExerciseLog> getLogs() {
        return logs;
    }

    public void setLogs(ForeignCollection<ExerciseLog> logs) {
        this.logs = logs;
    }

    public Date getHappendTime() {
        //emergency fix patch
        if(happendTime == null) happendTime = new Date();
        return happendTime;
    }

    public void setHappendTime(Date happendTime) {
        this.happendTime = happendTime;
    }

    public ExerciseLogGroup(String description) {
        happendTime = new Date();
        this.description = description;
    }

    public ExerciseLogGroup(String description, LearningSubject subject) {
        happendTime = new Date();
        this.description = description;
        setSubject(subject);
    }

    public ExerciseLogGroup() {
        happendTime = new Date();
    }

    public LearningSubject getSubject() {
        return LearningSubject.id2Obj(subjectId);
    }

    public void setSubject(LearningSubject value) {
        subjectId = value.getId();
    }

    public int getAvgScore() {
        int sum = 0;
        for (ExerciseLog log :
                getLogs()) {
            sum+=log.getCorrectRatio();
        }
        return (int) (( (double)sum / logs.size() ) + 0.5);
    }

    public static class DaoHelper {
        Dao<ExerciseLogGroup,Integer> _base;

        public DaoHelper(Dao<ExerciseLogGroup, Integer> _base) {
            this._base = _base;
        }

    }
}
