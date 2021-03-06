package net.sakuratrak.schoolstorycollection.core;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.sql.SQLException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class DbManager extends OrmLiteSqliteOpenHelper {

    public static final String DATABASE_NAME = "questionBook.db";

    public static final int DATABASE_VER = 8;

    private static OrmLiteSqliteOpenHelper currentHelper;

    private Dao<QuestionInfo, Integer> questionInfos;

    private Dao<LearningUnitInfo, Integer> learningUnitInfos;

    private Dao<ExerciseLog, Integer> exerciseLogs;

    private Dao<ExerciseLogGroup, Integer> exerciseLogGroups;

    public DbManager(Context context) {
        super(context, context.getDatabasePath(AppSettingsMaster.getWorkbookDb(context).getAbsolutePath()).getAbsolutePath(), null, DATABASE_VER);
    }

    public DbManager(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VER);
    }

    public DbManager(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    public static DbManager getDefaultHelper(Context context) {
        if (currentHelper == null) {
            //File db = AppSettingsMaster.getWorkbookDb(context);
            currentHelper = OpenHelperManager.getHelper(context, DbManager.class);
            //currentHelper = new DbManager(context,db.getAbsolutePath());
        }
        return (DbManager) currentHelper;
    }

    public static DbManager getHelperWithPath(Context context, File path) {
        return new DbManager(context, path.getAbsolutePath());
    }

    public static void releaseCurrentHelper() {
        if (currentHelper != null) {
            //currentHelper.close();
            OpenHelperManager.releaseHelper();
            currentHelper = null;
        }
    }

    public Dao<QuestionInfo, Integer> getQuestionInfos() {
        if (questionInfos == null) {
            try {
                questionInfos = getDao(QuestionInfo.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return questionInfos;
    }

    public Dao<LearningUnitInfo, Integer> getLearningUnitInfos() {
        if (learningUnitInfos == null) {
            try {
                learningUnitInfos = getDao(LearningUnitInfo.class);
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
        return learningUnitInfos;
    }

    public Dao<ExerciseLog, Integer> getExerciseLogs() {
        if (exerciseLogs == null) {
            try {
                exerciseLogs = getDao(ExerciseLog.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exerciseLogs;
    }

    public Dao<ExerciseLogGroup, Integer> getExerciseLogGroups() {
        if (exerciseLogGroups == null) {
            try {
                exerciseLogGroups = getDao(ExerciseLogGroup.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exerciseLogGroups;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, QuestionInfo.class);
            TableUtils.createTable(connectionSource, LearningUnitInfo.class);
            TableUtils.createTable(connectionSource, ExerciseLog.class);
            TableUtils.createTable(connectionSource, ExerciseLogGroup.class);

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
            TableUtils.dropTable(connectionSource, ExerciseLogGroup.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class CustomDatabaseContextWrapper extends ContextWrapper {

        public File _path;
        public String _name;
        public File _fullPath;


        public CustomDatabaseContextWrapper(Context base) {
            super(base);
        }

        public CustomDatabaseContextWrapper(@NonNull Context base, @Nullable File path, @Nullable String name, @Nullable File fullPath) {
            super(base);
            _path = path;
            _name = name;
            _fullPath = fullPath;
        }

        @Override
        public File getDatabasePath(String name) {
            if (_fullPath != null) return _fullPath;
            if (_path == null) {
                return super.getDatabasePath(_name == null ? name : _name);
            } else {
                return new File(_path, _name == null ? name : _name);
            }
        }
    }

}
