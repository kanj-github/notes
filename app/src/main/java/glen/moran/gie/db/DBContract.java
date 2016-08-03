package glen.moran.gie.db;

/**
 * Created by voldemort on 2/8/16.
 */
public final class DBContract {
    public static final String DATABASE_NAME = "glen.moran.gie.db";

    public static final String TABLE_NAME = "notes";
    public static final String COL_NAME_TITLE = "title";
    public static final String COL_NAME_SHORT_DESC = "short_desc";
    public static final String COL_NAME_ID = "rowid";

    public static final String SQL_CREATE_TABLE =  "CREATE TABLE " + TABLE_NAME + " (" +
            COL_NAME_TITLE + " TEXT, " +
            COL_NAME_SHORT_DESC + " TEXT);";

    public static final String GET_ALL_NOTES_COLUMNS[] = {
            COL_NAME_ID,
            COL_NAME_TITLE,
            COL_NAME_SHORT_DESC
    };
}
