package sakuratrak.schoolstorycollection.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DbManager extends OrmLiteSqliteOpenHelper {

    public static final String DATABASE_NAME="questionBook.db";

    private Dao<QuestionInfo,Integer> questionInfos;

    private Dao<LearningUnitInfo,Integer> learningUnitInfos;

    private Dao<ExerciseLog,Integer> exerciseLogs;

    public DbManager(Context context){
        super(context,DATABASE_NAME,null,2);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try
        {
            TableUtils.createTable(connectionSource, QuestionInfo.class);
            TableUtils.createTable(connectionSource, LearningUnitInfo.class);
            TableUtils.createTable(connectionSource, ExerciseLog.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, QuestionInfo.class, true);
            TableUtils.dropTable(connectionSource, LearningUnitInfo.class, true);
            TableUtils.dropTable(connectionSource, ExerciseLog.class, true);
            onCreate(database,connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
