package sakuratrak.schoolstorycollection.core;

import android.support.annotation.Nullable;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@DatabaseTable(tableName = "QuestionInfo")
public final class QuestionInfo implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField()
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

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> questionImage;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> analysisImage;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private Answer answer;

    @DatabaseField(foreign = true,foreignAutoRefresh = true)
    private LearningUnitInfo unit;

    @DatabaseField
    private Date authorTime;

    //region getter and setter

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(ArrayList<String> questionImage) {
        this.questionImage = questionImage;
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

    private void setType(QuestionType value){
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

    public Collection<ExerciseLog> getExerciseLogs() {
        return exerciseLogs;
    }

    public Date getAuthorTime() {
        return authorTime;
    }

    public void setAuthorTime(Date authorTime) {
        this.authorTime = authorTime;
    }

    //endregion

    public QuestionInfo(){

    }

    public static QuestionInfo createEmpty(){
        QuestionInfo info = new QuestionInfo();
        info.setAnalysisImage(new ArrayList<>());
        info.setQuestionImage(new ArrayList<>());
        return info;
    }

    public QuestionInfo(LearningSubject subject,QuestionType type){
        subjectId = subject.getId();
        setType(type);
    }

    public void setUnit(LearningUnitInfo unit) {
        this.unit = unit;
    }

    /** Common usage of QuestionInfo Table*/
    public static class QuestionInfoDaoManager{
        private final Dao<QuestionInfo,Integer> _base;

        public QuestionInfoDaoManager(Dao<QuestionInfo, Integer> base) {
            this._base = base;
        }

        public QuestionInfoDaoManager(DbManager mgr){
            _base= mgr.getQuestionInfos();
        }


        public List<QuestionInfo> FindAllWithSubject(LearningSubject subject) throws SQLException {
            return _base.queryForEq("subjectId",subject.getId());
        }

        public List<QuestionInfo> FindAllWithUnit(LearningUnitInfo... units) throws SQLException {
            ArrayList<QuestionInfo> infos = new ArrayList<>();

            for (LearningUnitInfo item: units) {
                infos.addAll(_base.queryForEq("unit",item));
            }
            return infos;
        }

    }

}
