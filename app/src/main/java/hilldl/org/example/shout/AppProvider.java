package hilldl.org.example.shout;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppProvider extends ContentProvider {

    private static final String TAG = "AppProvider";
    private static HouseM8Database mOpenHelper = null;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int LOGINS = 100;
    private static final int LOGINS_ID = 101;
    private static final int LOGINS_NAME = 102;
    private static final int LOGINS_EMAIL = 103;
    private static final int LOGINS_PASSWORD = 104;

    static final String CONTENT_AUTHORITY = "hilldl.org.example.shout.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

//    private static final int TIMINGS = 200;
//    private static final int TIMINGS_ID = 201;

//    private static final int TASKS_TIMINGS = 300;
//    private static final int TASKS_TIMINGS_ID = 301;

//    private static final int TASKS_DURATIONS = 400;
//    private static final int TASKS_DURATIONS_ID = 401;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // e.g. content://hilldl.org.example.shout.provider/Logins
        matcher.addURI(CONTENT_AUTHORITY, LoginsContract.TABLE_NAME, LOGINS);
        // e.g. content://hilldl.org.example.shout.provider/Logins/8
        // Note: the '#' here signifies 'replace with number"
        matcher.addURI(CONTENT_AUTHORITY, LoginsContract.TABLE_NAME + "/#", LOGINS_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {

        Log.d(TAG, "onCreate: starts");
        mOpenHelper = HouseM8Database.getInstance(getContext());
        if (mOpenHelper == null) {
            Log.d(TAG, "onCreate: mOpenHelper is null");
        } else {
            Log.d(TAG, "onCreate: mOpenHelper is not null");
        }
        Log.d(TAG, "onCreate: ends");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Log.d(TAG, "query: called with URI = " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match) {
            case LOGINS:
                queryBuilder.setTables(LoginsContract.TABLE_NAME);
                // select Table and return all records. No narrowing arguments
                // e.g. SELECT * FROM <Table Name>
                break;
            case LOGINS_ID:
                queryBuilder.setTables(LoginsContract.TABLE_NAME);
                long taskId = LoginsContract.getTaskId(uri);
                queryBuilder.appendWhere(LoginsContract.Columns.LOGINS_ID + " = " + taskId);
                break;
            default:
                throw new IllegalArgumentException(TAG + " query: matcher couldn't find match.");
        }


        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        Log.d(TAG, "query: rows in returned cursor = " + cursor.getCount()); //TODO remove this line
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "insert: entering insert, called with " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "insert: match is " + match);

        final SQLiteDatabase db;

        Uri returnUri;
        long recordId;
        switch (match) {
            case LOGINS:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(LoginsContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = LoginsContract.buildTaskUri(recordId);
                } else {
                    throw new android.database.SQLException("failed to insert into " + uri.toString());
                }
                break;

            // NOTE - if TimingsContract class ends up having a static inner class called 'Timings
            // Then the faulty code below should be TimingsContract.Timings.TABLE_NAME and
            // TimingsContract.Timings.buildTimingID() instead;
            case LOGINS_ID:
//                db = mOpenHelper.getWritableDatabase();
//                recordId = db.insert(TimingsContract.TABLE_NAME, null, values);
//                if (recordId >= 0) {
//                    returnUri = TimingsContract.buildTimingId(recordId);
//                } else {
//                    throw new android.database.SQLException("failed to insert into " + uri.toString());
//                }
//                break;

            default:
                throw new IllegalArgumentException("Unknown uri : " + uri);
        }

        if (recordId >= 0) {
            Log.d(TAG, "insert: something was inserted, setting notify change with " + uri);
            Context context = getContext();
            if (context != null) {
                context.getContentResolver().notifyChange(uri, null);
                // send null back to observer. This is because you don't want to bind your
                // Content Provider to be bound to objects running queries. So send back null
            } else {
                Log.d(TAG, "insert: couldnt get context");
            }

        } else {
            Log.d(TAG, "insert: nothing was inserted");
        }

        Log.d(TAG, "insert: exiting insert, returning " + returnUri);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
