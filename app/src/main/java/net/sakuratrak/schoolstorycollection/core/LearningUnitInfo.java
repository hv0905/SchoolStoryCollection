package net.sakuratrak.schoolstorycollection.core;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;


@DatabaseTable(tableName = "LearningUnitInfo")
public class LearningUnitInfo implements Serializable, Comparable<LearningUnitInfo> {
    private static final int NEED_MORE_MAX = 5;


    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String name;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<QuestionInfo> questions;

    @DatabaseField(canBeNull = false)
    private int subjectId;

    @DatabaseField
    private Date createTime;

    public LearningUnitInfo() {
        createTime = new Date();
    }

    public LearningUnitInfo(String name, LearningSubject subject) {
        createTime = new Date();
        this.name = name;
        setSubject(subject);
    }

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

    public LearningSubject getSubject() {
        return LearningSubject.id2Obj(subjectId);
    }

    private void setSubject(LearningSubject value) {
        subjectId = value.getId();
    }

    public int computeCorrectRatio() {
        if (questions == null || questions.size() == 0) {
            return 100;
        }
        int sum = 0;
        int count = 0;
        for (QuestionInfo item : questions) {
            Collection<ExerciseLog> logs = item.getExerciseLogs();
            if (logs == null) continue;
            for (ExerciseLog log : logs) {
                sum += log.getCorrectRatio();
            }
            count += logs.size();
        }
        if (count == 0) return 100;
        return sum / count;
    }

    public boolean getIfNeedMoreQuiz() {
        return getExerciseLogCount() < NEED_MORE_MAX;
    }

    public int getExerciseLogCount() {
        if (questions == null || questions.size() == 0) return 0;
        int count = 0;
        for (QuestionInfo item : questions) {
            Collection<ExerciseLog> logs = item.getExerciseLogs();
            if (logs == null) continue;
            count += logs.size();
        }
        return count;
    }

    public ForeignCollection<QuestionInfo> getQuestions() {
        return questions;
    }

    public void setQuestions(ForeignCollection<QuestionInfo> questions) {
        this.questions = questions;
    }

    @Override
    public int compareTo(LearningUnitInfo o) {
        return createTime.compareTo(o.createTime);
    }

    public static class LearningUnitInfoDaoHelper {

        private final Dao<LearningUnitInfo, Integer> _base;

        public LearningUnitInfoDaoHelper(Dao<LearningUnitInfo, Integer> base) {
            this._base = base;
        }

        public LearningUnitInfoDaoHelper(DbManager mgr) {
            _base = mgr.getLearningUnitInfos();
        }

        public List<LearningUnitInfo> findBySubject(LearningSubject subject) throws SQLException {
            return _base.queryForEq("subjectId", subject.getId());
        }


    }
}
