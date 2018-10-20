package sakuratrak.schoolstorycollection.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public final class DbManager extends OrmLiteSqliteOpenHelper {

    public static final String DATABASE_NAME="questionBook.db";

    private static OrmLiteSqliteOpenHelper currentHelper;

    private Dao<QuestionInfo,Integer> questionInfos;

    private Dao<LearningUnitInfo,Integer> learningUnitInfos;

    private Dao<ExerciseLog,Integer> exerciseLogs;

    public Dao<QuestionInfo, Integer> getQuestionInfos() {
        if(questionInfos == null){
            try {
                questionInfos = getDao(QuestionInfo.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return questionInfos;
    }

    public Dao<LearningUnitInfo, Integer> getLearningUnitInfos() {
        if(learningUnitInfos == null){
            try {
                learningUnitInfos = getDao(LearningUnitInfo.class);
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
        return learningUnitInfos;
    }

    public Dao<ExerciseLog, Integer> getExerciseLogs() {
        if(exerciseLogs == null){
            try {
                exerciseLogs = getDao(ExerciseLog.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exerciseLogs;
    }

    public DbManager(Context context){
        super(context,DATABASE_NAME,null,2);
    }

    public DbManager(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
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


    public static DbManager getHelper(Context context){
        if(currentHelper == null){
            currentHelper = OpenHelperManager.getHelper(context,DbManager.class);
        }
        return (DbManager) currentHelper;
    }

    public static void releaseHelper(){
        if(currentHelper != null){
            OpenHelperManager.releaseHelper();
                currentHelper = null;
        }
    }

}
