package com.example.amit.projectapp2.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class ServiceMgmtContract {
    private ServiceMgmtContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.amit.projectapp2";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.projectapp2/user/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_USER = "user";
    public static final String PATH_PROVIDER = "provider";
    public static final String PATH_APPOINTMENT = "appointment";
    public static final String PATH_SERVICE = "service";
    public static final String PATH_SUGGESTION = "suggestion";
    public static final String PATH_CURRENT_ID = "current_id";

    /**
     * Inner class that defines constant values for each database table.
     */

    /**
     * Inner class for user database table
     */
    public static final class UserEntry implements BaseColumns {

        // uri
        public static final Uri USER_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USER);

        //table name
        public final static String TABLE_NAME = "user";

        //column constants
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_USERNAME = "username";
        public final static String COLUMN_PASSWORD = "password";
        public final static String COLUMN_FULL_NAME = "full_name";
        public final static String COLUMN_CONTACT_NUMBER = "contact_number";
        public final static String COLUMN_PIN_CODE = "pin_code";
        public final static String COLUMN_HOUSE_NUMBER = "house_number";
        public final static String COLUMN_LOCALITY = "locality";
        public final static String COLUMN_lANDMARK = "landmark";
        public final static String COLUMN_CITY = "city";
        public final static String COLUMN_STATE = "state";
        public final static String COLUMN_COUNTRY = "country";
        public final static String COLUMN_IS_PROVIDER = "is_provider";

        // value to be stored in COLUMN_IS_PROVIDER if user is not provider
        public static final int NOT_PROVIDER = -1;

        //method to find out whether the user is provider or not
        public static boolean isProvider(int arg) {
            if (arg == NOT_PROVIDER)
                return false;
            else
                return true;
        }
    }

    /**
     * Inner class for provider database table
     */
    public static final class ProviderEntry implements BaseColumns {

        // uri
        public static final Uri PROVIDER_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PROVIDER);

        //table name
        public final static String TABLE_NAME = "provider";

        //column constants
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_TYPE = "type";
        public final static String COLUMN_PIN_CODE = "pin_code";
        public final static String COLUMN_LOCALITY = "locality";
        public final static String COLUMN_CITY = "city";
        public final static String COLUMN_STATE = "state";
        public final static String COLUMN_COUNTRY = "country";
        public final static String COLUMN_COMPANY_NAME = "company_name";

        //type constants to be stored in COLUMN_TYPE
        public final static String TYPE_MECHANIC = "Mechanic";
        public final static String TYPE_ELECTRICIAN = "Electrician";
        public final static String TYPE_PLUMBER = "Plumber";
        public final static String TYPE_COOK = "Cook";
        public final static String TYPE_MAID = "Maid";
        public final static String TYPE_PAINTER = "Painter";

        //method to validate the type values
        public static boolean isValidType(String arg) {
            if (arg.equals(TYPE_MECHANIC) || arg.equals(TYPE_ELECTRICIAN) || arg.equals(TYPE_PLUMBER)
                    || arg.equals(TYPE_COOK) || arg.equals(TYPE_MAID) || arg.equals(TYPE_PAINTER))
                return true;
            else
                return false;
        }
    }

    /**
     * Inner class for appointment database table
     */
    public static final class AppointmentEntry implements BaseColumns {
        // uri
        public static final Uri APPOINTMENT_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_APPOINTMENT);

        //table name
        public final static String TABLE_NAME = "appointment";

        //column constants
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_USER_ID = "user_id";
        public final static String COLUMN_PROVIDER_ID = "provider_id";
        public final static String COLUMN_SERVICE_ID = "service_id";
        public final static String COLUMN_STATUS = "status";

        //constants to be stored in COLUMN_STATUS
        //public final static int STATUS_NO_PROVIDER_FOUND = 1;
        public final static int STATUS_PENDING = 1;
        public final static int STATUS_USER_CANCELED = 2;
        public final static int STATUS_COMPLETED = 3;

        //method to validate the status values
        public static boolean isValidStatus(int arg) {
            if (arg >= 1 && arg <= 3)
                return true;
            else
                return false;
        }
    }

    /**
     * Inner class for service database table
     */
    public static final class ServiceEntry implements BaseColumns {
        // uri
        public static final Uri SERVICE_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SERVICE);

        //table name
        public final static String TABLE_NAME = "service";

        //column constants
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_TYPE = "type";
        public final static String COLUMN_SHORT_DESCRIPTION = "short_description";
        public final static String COLUMN_DESCRIPTION = "description";
    }

    /**
     * Inner class for suggestion database table
     */
    public static final class SuggestionEntry implements BaseColumns {
        // uri
        public static final Uri SUGGESTION_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUGGESTION);

        //table name
        public final static String TABLE_NAME = "suggestion";

        //column constants
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_USER_ID = "user_id";
        public final static String COLUMN_SUGGESTION_TYPE = "suggestion_type";
        public final static String COLUMN_DESCRIPTION = "description";

        //constants to be stored in COLUMN_SUGGESTION_TYPE
        public final static int SUGGESTION_TYPE_LIKE = 1;
        public final static int SUGGESTION_TYPE_DISLIKE = 2;
        public final static int SUGGESTION_TYPE_IDEA = 3;

        //method to validate suggestion type
        public static boolean isValidSuggestionType(int arg) {
            if (arg == 1 || arg == 2 || arg == 3)
                return true;
            else
                return false;
        }
    }

    /**
     * Inner class for current_id database table
     */
    public static final class CurrentIdEntry implements BaseColumns {
        // uri
        public static final Uri CURRENT_ID_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CURRENT_ID);

        //table name
        public final static String TABLE_NAME = "current_id";

        //column constants
        public final static String COLUMN_USER_ID = "user_id";
    }

    public static boolean isValidId(int id) {
        if (id >= 0)
            return true;
        else
            return false;
    }
}
