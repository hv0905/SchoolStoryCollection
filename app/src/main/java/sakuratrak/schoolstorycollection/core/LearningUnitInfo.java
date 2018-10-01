package sakuratrak.schoolstorycollection.core;

import java.io.Serializable;
import java.util.ArrayList;

public class LearningUnitInfo implements Serializable {
    public String Name;
    public LearningSubject subject;
    public ArrayList<ExerciseLog> ExerciseLogs;
}
