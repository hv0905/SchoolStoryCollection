package sakuratrak.schoolstorycollection.core;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@DatabaseTable(tableName = "ExerciseLog")
public class ExerciseLog implements Serializable {
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public int questionId;

    @DatabaseField
    public int correctRatio;

    @DatabaseField
    public Date happenedTime;

}
