package sakuratrak.schoolstorycollection.core;


import java.io.Serializable;
import java.util.UUID;

public final class QuestionInfo implements Serializable {

    private String Title;
    private LearningSubject Subject;
    private QuestionType Type;
    public UUID MainImageId;
    public UUID AnswerImageId;
    public UUID AnalysisImageId;


    public QuestionInfo(){

    }

    public QuestionInfo(String title,LearningSubject subject,QuestionType type){
        Title = title;
        Subject = subject;
        Type = type;
    }


}
