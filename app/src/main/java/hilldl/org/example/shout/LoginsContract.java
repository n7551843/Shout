package hilldl.org.example.shout;

/*
This is going to be the contract class for the Logins Table of the HouseM8 database.
This class will contain all the static table detail references, as well as links to build
relevant uris with the content providers.
 */

import android.content.ContentUris;
import android.net.Uri;

public class LoginsContract {

    static final String TABLE_NAME = "Logins";

    public static class Columns {
        public static final String LOGINS_ID = "_id";
        public static final String LOGINS_NAME = "Name";
        public static final String LOGINS_EMAIL = "Email";
        public static final String LOGINS_PASSWORD = "Password";

        private Columns() {
        }
    }

    /**
     * This is the Uri to access the Tasks table.
     */

    static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);
//
    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME; //TODO Check these are still right
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AppProvider.CONTENT_AUTHORITY + "." + TABLE_NAME; //TODO Check these are still right
//
    static Uri buildTaskUri(long taskId) {
        return ContentUris.withAppendedId(CONTENT_URI, taskId);
    }

    static long getTaskId(Uri uri) {
        return ContentUris.parseId(uri);
    }

}
