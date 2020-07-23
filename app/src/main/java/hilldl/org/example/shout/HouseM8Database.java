package hilldl.org.example.shout;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HouseM8Database extends SQLiteOpenHelper {
    private static final String TAG = "HouseM8Database";

    public static final String DATABASE_NAME = "HouseM8.db";
    public static final int DATABASE_VERSION = 1;

    // Implement AppDatabase as a singleton
    private static HouseM8Database instance = null;

    private HouseM8Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Get an instance of the app's singleton database helper object.
     * <p>
     * Whenever another class needs an instance of the 'HouseM8Database' class, it calls the
     * static 'getInstance' method.  The first time this is called, it will create a new
     * instance of the HouseM8Database, and thereafter it will return the instance.
     * <p>
     * IMPORTANT NOTE - this is generally fine, but not thread safe.  If it hasn't been created yet
     * and is called on two different threads, you'll get two difference instances of the class.
     *
     * @param context the contentProvider's context
     * @return a SQLite database helper object
     */

    static HouseM8Database getInstance(Context context) {
        if (instance == null) {
            Log.d(TAG, "getInstance: creating new instance");
            instance = new HouseM8Database(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        String sSQL = "";
        
        if (db == null) {
            Log.d(TAG, "onCreate: db is null");
        } else {
            Log.d(TAG, "onCreate: db is not null");
        }

//        sSQL = "CREATE TABLE Logins(_id INTEGER PRIMARY KEY NOT NULL,
//        name TEXT NOT NULL, Email TEXT NOT NULL, PASSWORD TEXT NOT NULL);";

        sSQL = "CREATE TABLE IF NOT EXISTS " + LoginsContract.TABLE_NAME + "(" +
                LoginsContract.Columns.LOGINS_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                LoginsContract.Columns.LOGINS_NAME + " TEXT NOT NULL, " +
                LoginsContract.Columns.LOGINS_EMAIL + " TEXT NOT NULL, " +
                LoginsContract.Columns.LOGINS_PASSWORD + " TEXT NOT NULL);";
        Log.d(TAG, "onCreate: " + sSQL);
        db.execSQL(sSQL);
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int newVersion) {
        switch (newVersion) {
            case 1:
                // whatever edits are needed to upgrade database
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown version: " + newVersion);
        }
        Log.d(TAG, "onUpgrade: ends");
    }
}
