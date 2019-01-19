package net.sakuratrak.schoolstorycollection.core;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;

@DatabaseTable(tableName = "QuestionInfo")
public final class QuestionInfo implements Serializable, Comparable<QuestionInfo> {

    public static final int DIFFICULTY_MAX = 10;

    //region fields

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String title;

    @DatabaseField
    private String source;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String analysisDetail;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String questionDetail;

    @DatabaseField(canBeNull = false)
    private int subjectId;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<ExerciseLog> exerciseLogs;

    @DatabaseField(canBeNull = false)
    private int typeId;

    @DatabaseField()
    private String questionImage;

    @DatabaseField()
    private String analysisImage;

    private Answer answer;

    @DatabaseField(dataType = DataType.STRING, columnName = "answer")
    private String answerStr;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private LearningUnitInfo unit;

    @DatabaseField
    private Date authorTime;

    @DatabaseField
    private int difficulty;

    @DatabaseField
    private boolean favourite;

    //endregion

    //region getter and setter

    public QuestionInfo() {

    }

    public QuestionInfo(LearningSubject subject, QuestionType type) {
        subjectId = subject.getId();
        setType(type);
    }

    public static QuestionInfo createEmpty() {
        QuestionInfo info = new QuestionInfo();
        info.setAnalysisImage(new ArrayList<>());
        info.setQuestionImage(new ArrayList<>());
        return info;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getQuestionImage() {
        return AppHelper.string2StringArray(questionImage);
    }

    public void setQuestionImage(List<String> questionImage) {
        this.questionImage = AppHelper.stringList2String(questionImage);
    }

    public String[] getAnalysisImage() {
        if (analysisImage == null || analysisImage.isEmpty()) return new String[0];
        return AppHelper.string2StringArray(analysisImage);
    }

    public void setAnalysisImage(List<String> analysisImage) {
        this.analysisImage = AppHelper.stringList2String(analysisImage);
    }

    public Answer getAnswer() {
        if (answer == null) {
            switch (getType()) {

                case SINGLE_CHOICE:
                case MULTIPLY_CHOICE:
                    answer = SelectableAnswer.fromMetaData(answerStr);
                    break;
                case TYPEABLE_BLANK:
                    answer = BlankAnswer.fromMetaData(answerStr);
                    break;
                case BLANK:
                case ANSWER:
                    answer = ImageAnswer.fromMetaData(answerStr);
                    break;
            }
        }
        return answer;
    }

    public void setAnswer(Answer answer) {
        if (answer == this.answer) return;
        this.answer = answer;
        answerStr = answer.toMetaData();
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

    private void setType(QuestionType value) {
        typeId = value.getId();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAnalysisDetail() {
        return analysisDetail;
    }

    public void setAnalysisDetail(String analysisDetail) {
        this.analysisDetail = analysisDetail;
    }

    public String getQuestionDetail() {
        return questionDetail;
    }

    public void setQuestionDetail(String questionDetail) {
        this.questionDetail = questionDetail;
    }

    @Nullable
    public LearningUnitInfo getUnit() {
        return unit;
    }

    public void setUnit(LearningUnitInfo unit) {
        this.unit = unit;
    }

    public Collection<ExerciseLog> getExerciseLogs() {
        return exerciseLogs;
    }

    public Date getAuthorTime() {
        return authorTime;
    }

    public void setAuthorTime(Date authorTime) {
        this.authorTime = authorTime;
    }

    public int getId() {
        return id;
    }

    public int getDifficulty() {
        return difficulty;
    }

    //endregion

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    @Override
    public int compareTo(QuestionInfo o) {
        return -getAuthorTime().compareTo(o.getAuthorTime());
    }

    /**
     * Common usage of QuestionInfo Table
     */
    public static class QuestionInfoDaoManager {
        private final Dao<QuestionInfo, Integer> _base;

        public QuestionInfoDaoManager(Dao<QuestionInfo, Integer> base) {
            this._base = base;
        }

        public QuestionInfoDaoManager(DbManager mgr) {
            _base = mgr.getQuestionInfos();
        }


        public List<QuestionInfo> FindAllWithSubject(LearningSubject subject) throws SQLException {
            return _base.queryForEq("subjectId", subject.getId());
        }

        public List<QuestionInfo> FindAllWithUnit(LearningUnitInfo... units) throws SQLException {
            ArrayList<QuestionInfo> infos = new ArrayList<>();

            for (LearningUnitInfo item : units) {
                infos.addAll(_base.queryForEq("unit", item));
            }
            return infos;
        }

    }

}
