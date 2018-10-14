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

}
