package glen.moran.gie.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by voldemort on 2/8/16.
 */
public class DbManager extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static DbManager mInstance = null;

    private  DbManager(Context context) {
        super(context, DBContract.DATABASE_NAME, null, DB_VERSION);
    }

    public static synchronized DbManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DbManager(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DBContract.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO: migrate data based upon version diff
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public Cursor getAllNotes() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = DBContract.GET_ALL_NOTES_COLUMNS;
        return db.query(
                DBContract.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );
    }
}
