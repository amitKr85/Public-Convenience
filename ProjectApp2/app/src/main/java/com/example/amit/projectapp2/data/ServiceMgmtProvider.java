package com.example.amit.projectapp2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.amit.projectapp2.data.ServiceMgmtContract.*;

public class ServiceMgmtProvider extends ContentProvider {

    /**
     * Rri matcher codes for different Uri's
     */
    public final static int USER = 10;
    public final static int USER_ID = 11;
    public final static int PROVIDER = 20;
    public final static int PROVIDER_ID = 21;
    public final static int APPOINTMENT = 30;
    public final static int APPOINTMENT_ID = 31;
    public final static int SERVICE = 40;
    public final static int SERVICE_ID = 41;
    public final static int SUGGESTION = 50;
    public final static int SUGGESTION_ID = 51;
    public final static int CURRENT = 60;

    /**
     * UriMatcher
     */
    public final static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /** all available uri's handled by provider is adding to the uriMatcher
     * and attaching corresponding constant integer values
     */
    static {
        sUriMatcher.addURI(ServiceMgmtContract.CONTENT_AUTHORITY, ServiceMgmtContract.PATH_USER, USER);
        sUriMatcher.addURI(ServiceMgmtContract.CONTENT_AUTHORITY, ServiceMgmtContract.PATH_USER + "/#", USER_ID);

        sUriMatcher.addURI(ServiceMgmtContract.CONTENT_AUTHORITY, ServiceMgmtContract.PATH_PROVIDER, PROVIDER);
        sUriMatcher.addURI(ServiceMgmtContract.CONTENT_AUTHORITY, ServiceMgmtContract.PATH_PROVIDER + "/#", PROVIDER_ID);

        sUriMatcher.addURI(ServiceMgmtContract.CONTENT_AUTHORITY, ServiceMgmtContract.PATH_APPOINTMENT, APPOINTMENT);
        sUriMatcher.addURI(ServiceMgmtContract.CONTENT_AUTHORITY, ServiceMgmtContract.PATH_APPOINTMENT + "/#", APPOINTMENT_ID);

        sUriMatcher.addURI(ServiceMgmtContract.CONTENT_AUTHORITY, ServiceMgmtContract.PATH_SERVICE, SERVICE);
        sUriMatcher.addURI(ServiceMgmtContract.CONTENT_AUTHORITY, ServiceMgmtContract.PATH_SERVICE + "/#", SERVICE_ID);

        sUriMatcher.addURI(ServiceMgmtContract.CONTENT_AUTHORITY, ServiceMgmtContract.PATH_SUGGESTION, SUGGESTION);
        sUriMatcher.addURI(ServiceMgmtContract.CONTENT_AUTHORITY, ServiceMgmtContract.PATH_SUGGESTION + "/#", SUGGESTION_ID);

        sUriMatcher.addURI(ServiceMgmtContract.CONTENT_AUTHORITY, ServiceMgmtContract.PATH_CURRENT_ID, CURRENT);
    }

    // DbHelper
    ServiceMgmtDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ServiceMgmtDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);

        switch (match) {
            //for user_id
            case USER_ID:
                selection = UserEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // for user
            case USER:
                cursor = database.query(UserEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            ///////////////////////////////////////////////////////////
            case PROVIDER_ID:
                selection = ProviderEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case PROVIDER:
                cursor = database.query(ProviderEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            /////////////////////////////////////////////////////////////
            case APPOINTMENT_ID:
                selection = AppointmentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case APPOINTMENT:
                cursor = database.query(AppointmentEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                //setting notification on th uri
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            /////////////////////////////////////////////////////////////
            case SERVICE_ID:
                selection = ServiceEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case SERVICE:
                cursor = database.query(ServiceEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            /////////////////////////////////////////////////////////////
            case SUGGESTION_ID:
                selection = SuggestionEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case SUGGESTION:
                cursor = database.query(SuggestionEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            /////////////////////////////////////////////////////////////
            case CURRENT:
                cursor = database.query(CurrentIdEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        /////setting notification on th uri
        //cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        if(values==null || values.size()==0)
            throw new IllegalArgumentException("empty ContentValues object using in insert oper.");

        int match = sUriMatcher.match(uri);
        switch (match) {
            case USER:
                return insertUserData(uri, values);
            case PROVIDER:
                return insertProviderData(uri, values);
            case APPOINTMENT:
                return insertAppointmentData(uri, values);
            case SERVICE:
                return insertServiceData(uri, values);
            case SUGGESTION:
                return insertSuggestionData(uri, values);
            case CURRENT:
                return insertCurrentData(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertUserData(Uri uri, ContentValues values) {
        String userName = values.getAsString(UserEntry.COLUMN_USERNAME);
        String password = values.getAsString(UserEntry.COLUMN_PASSWORD);
        String fullName = values.getAsString(UserEntry.COLUMN_FULL_NAME);
        String contactNumber = values.getAsString(UserEntry.COLUMN_CONTACT_NUMBER);
        int pinCode = values.getAsInteger(UserEntry.COLUMN_PIN_CODE);
        String houseNo = values.getAsString(UserEntry.COLUMN_HOUSE_NUMBER);
        String locality = values.getAsString(UserEntry.COLUMN_LOCALITY);
        String landmark = values.getAsString(UserEntry.COLUMN_lANDMARK);
        String city = values.getAsString(UserEntry.COLUMN_CITY);
        String state = values.getAsString(UserEntry.COLUMN_STATE);
        String country = values.getAsString(UserEntry.COLUMN_COUNTRY);
        int isProvider = values.getAsInteger(UserEntry.COLUMN_IS_PROVIDER);

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(contactNumber)
                || TextUtils.isEmpty(houseNo) || TextUtils.isEmpty(locality)
                || TextUtils.isEmpty(landmark) || TextUtils.isEmpty(city)
                || TextUtils.isEmpty(state) || TextUtils.isEmpty(country))
            throw new IllegalArgumentException("empty input in User table");
        if (!(pinCode > 0))
            throw new IllegalArgumentException("invalid pincode input in user table");
        if (!(isProvider >= -1))
            throw new IllegalArgumentException("invalid isProvider input in user table");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(UserEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e("ServiceMgmtProvider", "Failed to insert row for " + uri);
            return null;
        }

        ///notifying all listeners that the content has changed at this uri
        //getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertProviderData(Uri uri, ContentValues values) {
        String type = values.getAsString(ProviderEntry.COLUMN_TYPE);
        int pinCode = values.getAsInteger(ProviderEntry.COLUMN_PIN_CODE);
        String locality = values.getAsString(ProviderEntry.COLUMN_LOCALITY);
        String city = values.getAsString(ProviderEntry.COLUMN_CITY);
        String state = values.getAsString(ProviderEntry.COLUMN_STATE);
        String country = values.getAsString(ProviderEntry.COLUMN_COUNTRY);
        String companyName = values.getAsString(ProviderEntry.COLUMN_COMPANY_NAME);

        if (TextUtils.isEmpty(type) || TextUtils.isEmpty(locality) || TextUtils.isEmpty(city)
                || TextUtils.isEmpty(state) || TextUtils.isEmpty(country) || TextUtils.isEmpty(companyName))
            throw new IllegalArgumentException("empty input in provider table");
        if (!(pinCode > 0))
            throw new IllegalArgumentException("invalid pincode input in provider table");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(ProviderEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e("ServiceMgmtProvider", "Failed to insert row for " + uri);
            return null;
        }

        ///notifying all listeners that the content has changed at this uri
        //getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertAppointmentData(Uri uri, ContentValues values) {
        int userId = values.getAsInteger(AppointmentEntry.COLUMN_USER_ID);
        int providerId = values.getAsInteger(AppointmentEntry.COLUMN_PROVIDER_ID);
        int serviceId = values.getAsInteger(AppointmentEntry.COLUMN_SERVICE_ID);
        int status = values.getAsInteger(AppointmentEntry.COLUMN_STATUS);

        if (!ServiceMgmtContract.isValidId(userId))
            throw new IllegalArgumentException("invalid userid input in app. table");
        if (!ServiceMgmtContract.isValidId(providerId))
            throw new IllegalArgumentException("invalid providerid input in app. table");
        if (!ServiceMgmtContract.isValidId(serviceId))
            throw new IllegalArgumentException("invalid serviceid input in app. table");
        if (!AppointmentEntry.isValidStatus(status))
            throw new IllegalArgumentException("invalid status input in app. table");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(AppointmentEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e("ServiceMgmtProvider", "Failed to insert row for " + uri);
            return null;
        }

        ///notifying all listeners that the content has changed at this uri
        //getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertServiceData(Uri uri, ContentValues values) {
        String type = values.getAsString(ServiceEntry.COLUMN_TYPE);
        String shortDescription = values.getAsString(ServiceEntry.COLUMN_SHORT_DESCRIPTION);
        String description = values.getAsString(ServiceEntry.COLUMN_DESCRIPTION);

        if (TextUtils.isEmpty(type) || TextUtils.isEmpty(shortDescription) || TextUtils.isEmpty(description))
            throw new IllegalArgumentException("empty input in service table");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(ServiceEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e("ServiceMgmtProvider", "Failed to insert row for " + uri);
            return null;
        }

        ///notifying all listeners that the content has changed at this uri
        //getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertSuggestionData(Uri uri, ContentValues values) {
        int userId = values.getAsInteger(SuggestionEntry.COLUMN_USER_ID);
        int suggType = values.getAsInteger(SuggestionEntry.COLUMN_SUGGESTION_TYPE);
        String description = values.getAsString(SuggestionEntry.COLUMN_DESCRIPTION);

        if (!ServiceMgmtContract.isValidId(userId))
            throw new IllegalArgumentException("invalid id input in sugg. table");
        if (!SuggestionEntry.isValidSuggestionType(suggType))
            throw new IllegalArgumentException("invalid sugg. type input in sugg. table");
        if (TextUtils.isEmpty(description))
            throw new IllegalArgumentException("empty description input in sugg. table");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(SuggestionEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e("ServiceMgmtProvider", "Failed to insert row for " + uri);
            return null;
        }

        ///notifying all listeners that the content has changed at this uri
        //getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertCurrentData(Uri uri, ContentValues values) {

        if (!ServiceMgmtContract.isValidId(values.getAsInteger(CurrentIdEntry.COLUMN_USER_ID)))
            throw new IllegalArgumentException("invalid id input in currentid table");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(CurrentIdEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e("ServiceMgmtProvider", "Failed to insert row for " + uri);
            return null;
        }

        ///notifying all listeners that the content has changed at this uri
        //getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USER_ID:
                selection = UserEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case USER:
                rowsUpdated = database.delete(UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            ///////////////////////////////////////////////
            case PROVIDER_ID:
                selection = ProviderEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case PROVIDER:
                rowsUpdated = database.delete(ProviderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            ///////////////////////////////////////////////
            case APPOINTMENT_ID:
                selection = AppointmentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case APPOINTMENT:
                rowsUpdated = database.delete(AppointmentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            ///////////////////////////////////////////////
            case SERVICE_ID:
                selection = ServiceEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case SERVICE:
                rowsUpdated = database.delete(ServiceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            ///////////////////////////////////////////////
            case SUGGESTION_ID:
                selection = SuggestionEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case SUGGESTION:
                rowsUpdated = database.delete(SuggestionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            ///////////////////////////////////////////////
            case CURRENT:
                rowsUpdated = database.delete(CurrentIdEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        /*if(rowsUpdated!=0){
            ///notifying all listeners if affected rows >0 that the content has changed at this uri
            getContext().getContentResolver().notifyChange(uri,null);
        }
        */

        return rowsUpdated;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        if(values==null || values.size()==0)
            throw new IllegalArgumentException("empty ContentValues object using in update oper. with uri:"+uri);

        int match = sUriMatcher.match(uri);
        switch (match) {
            case USER_ID:
                selection = UserEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case USER:
                return updateUserData(uri, values, selection, selectionArgs);
            case PROVIDER_ID:
                selection = ProviderEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case PROVIDER:
                return updateProviderData(uri, values, selection, selectionArgs);
            case APPOINTMENT_ID:
                selection = AppointmentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case APPOINTMENT:
                return updateAppointmentData(uri, values, selection, selectionArgs);
            case SERVICE_ID:
                selection = ServiceEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case SERVICE:
                return updateServiceData(uri, values, selection, selectionArgs);
            case SUGGESTION_ID:
                selection = SuggestionEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case SUGGESTION:
                return updateSuggestionData(uri, values, selection, selectionArgs);
            case CURRENT:
                return updateCurrentIdData(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("no update operation associated with the uri:" + uri);
        }
    }

    private int updateUserData(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(UserEntry.COLUMN_USERNAME)) {
            String username = values.getAsString(UserEntry.COLUMN_USERNAME);
            if (TextUtils.isEmpty(username))
                throw new IllegalArgumentException("empty username update oper. in user table");
        }
        if (values.containsKey(UserEntry.COLUMN_PASSWORD)) {
            String password = values.getAsString(UserEntry.COLUMN_PASSWORD);
            if (TextUtils.isEmpty(password))
                throw new IllegalArgumentException("empty password update oper. in user table");
        }
        if (values.containsKey(UserEntry.COLUMN_FULL_NAME)) {
            String fullName = values.getAsString(UserEntry.COLUMN_FULL_NAME);
            if (TextUtils.isEmpty(fullName))
                throw new IllegalArgumentException("empty name update oper. in user table");
        }
        if (values.containsKey(UserEntry.COLUMN_CONTACT_NUMBER)) {
            String contact = values.getAsString(UserEntry.COLUMN_CONTACT_NUMBER);
            if (TextUtils.isEmpty(contact))
                throw new IllegalArgumentException("empty contact update oper. in user table");
        }
        if (values.containsKey(UserEntry.COLUMN_PIN_CODE)) {
            int pinCode = values.getAsInteger(UserEntry.COLUMN_PIN_CODE);
            if (!(pinCode > 0))
                throw new IllegalArgumentException("invalid pincode update oper. in user table");
        }
        if (values.containsKey(UserEntry.COLUMN_HOUSE_NUMBER)) {
            String houseNo = values.getAsString(UserEntry.COLUMN_HOUSE_NUMBER);
            if (TextUtils.isEmpty(houseNo))
                throw new IllegalArgumentException("empty house no update oper. in user table");
        }
        if (values.containsKey(UserEntry.COLUMN_LOCALITY)) {
            String locality = values.getAsString(UserEntry.COLUMN_LOCALITY);
            if (TextUtils.isEmpty(locality))
                throw new IllegalArgumentException("empty locality update oper. in user table");
        }
        if (values.containsKey(UserEntry.COLUMN_lANDMARK)) {
            String landmark = values.getAsString(UserEntry.COLUMN_lANDMARK);
            if (TextUtils.isEmpty(landmark))
                throw new IllegalArgumentException("empty landmark update oper. in user table");
        }
        if (values.containsKey(UserEntry.COLUMN_CITY)) {
            String city = values.getAsString(UserEntry.COLUMN_CITY);
            if (TextUtils.isEmpty(city))
                throw new IllegalArgumentException("empty city update oper. in user table");
        }
        if (values.containsKey(UserEntry.COLUMN_STATE)) {
            String state = values.getAsString(UserEntry.COLUMN_STATE);
            if (TextUtils.isEmpty(state))
                throw new IllegalArgumentException("empty state update oper. in user table");
        }
        if (values.containsKey(UserEntry.COLUMN_COUNTRY)) {
            String country = values.getAsString(UserEntry.COLUMN_COUNTRY);
            if (TextUtils.isEmpty(country))
                throw new IllegalArgumentException("empty country update oper. in user table");
        }
        if (values.containsKey(UserEntry.COLUMN_IS_PROVIDER)) {
            int isProvider = values.getAsInteger(UserEntry.COLUMN_IS_PROVIDER);
            if (!(isProvider >= -1))
                throw new IllegalArgumentException("invalid isProvider update oper. in user table");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(UserEntry.TABLE_NAME, values, selection, selectionArgs);

        return rowsUpdated;
    }

    private int updateProviderData(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ProviderEntry.COLUMN_TYPE)) {
            String type = values.getAsString(ProviderEntry.COLUMN_TYPE);
            if (TextUtils.isEmpty(type))
                throw new IllegalArgumentException("empty type update oper. in provider table");
        }
        if (values.containsKey(ProviderEntry.COLUMN_PIN_CODE)) {
            int pinCode = values.getAsInteger(ProviderEntry.COLUMN_PIN_CODE);
            if (!(pinCode > 0))
                throw new IllegalArgumentException("invalid pinCode update oper. in provider table");
        }
        if (values.containsKey(ProviderEntry.COLUMN_LOCALITY)) {
            String locality = values.getAsString(ProviderEntry.COLUMN_LOCALITY);
            if (TextUtils.isEmpty(locality))
                throw new IllegalArgumentException("empty locality update oper. in provider table");
        }
        if (values.containsKey(ProviderEntry.COLUMN_CITY)) {
            String city = values.getAsString(ProviderEntry.COLUMN_CITY);
            if (TextUtils.isEmpty(city))
                throw new IllegalArgumentException("empty city update oper. in provider table");
        }
        if (values.containsKey(ProviderEntry.COLUMN_STATE)) {
            String state = values.getAsString(ProviderEntry.COLUMN_STATE);
            if (TextUtils.isEmpty(state))
                throw new IllegalArgumentException("empty state update oper. in provider table");
        }
        if (values.containsKey(ProviderEntry.COLUMN_COUNTRY)) {
            String country = values.getAsString(ProviderEntry.COLUMN_COUNTRY);
            if (TextUtils.isEmpty(country))
                throw new IllegalArgumentException("empty country update oper. in provider table");
        }
        if (values.containsKey(ProviderEntry.COLUMN_COMPANY_NAME)) {
            String companyName = values.getAsString(ProviderEntry.COLUMN_COMPANY_NAME);
            if (TextUtils.isEmpty(companyName))
                throw new IllegalArgumentException("empty companyName update oper. in provider table");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(ProviderEntry.TABLE_NAME, values, selection, selectionArgs);

        return rowsUpdated;
    }

    private int updateAppointmentData(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(AppointmentEntry.COLUMN_USER_ID)) {
            int userId = values.getAsInteger(AppointmentEntry.COLUMN_USER_ID);
            if (!ServiceMgmtContract.isValidId(userId))
                throw new IllegalArgumentException("invalid userid update oper. in appoint. table");
        }
        if (values.containsKey(AppointmentEntry.COLUMN_PROVIDER_ID)) {
            int providerId = values.getAsInteger(AppointmentEntry.COLUMN_PROVIDER_ID);
            if (!ServiceMgmtContract.isValidId(providerId))
                throw new IllegalArgumentException("invalid providerid update oper. in appoint. table");
        }
        if (values.containsKey(AppointmentEntry.COLUMN_SERVICE_ID)) {
            int serviceId = values.getAsInteger(AppointmentEntry.COLUMN_SERVICE_ID);
            if (!ServiceMgmtContract.isValidId(serviceId))
                throw new IllegalArgumentException("invalid serviceid update oper. in appoint. table");
        }
        if (values.containsKey(AppointmentEntry.COLUMN_STATUS)) {
            int status = values.getAsInteger(AppointmentEntry.COLUMN_STATUS);
            if (!AppointmentEntry.isValidStatus(status))
                throw new IllegalArgumentException("invalid status update oper. in appoint. table");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(AppointmentEntry.TABLE_NAME, values, selection, selectionArgs);

        //notifying change on the uri
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }

    private int updateServiceData(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ServiceEntry.COLUMN_TYPE)) {
            String type = values.getAsString(ServiceEntry.COLUMN_TYPE);
            if (TextUtils.isEmpty(type))
                throw new IllegalArgumentException("empty type update oper. in service table");
        }
        if (values.containsKey(ServiceEntry.COLUMN_SHORT_DESCRIPTION)) {
            String shortDesc = values.getAsString(ServiceEntry.COLUMN_SHORT_DESCRIPTION);
            if (TextUtils.isEmpty(shortDesc))
                throw new IllegalArgumentException("empty short desc update oper. in service table");
        }
        if (values.containsKey(ServiceEntry.COLUMN_DESCRIPTION)) {
            String desc = values.getAsString(ServiceEntry.COLUMN_DESCRIPTION);
            if (TextUtils.isEmpty(desc))
                throw new IllegalArgumentException("empty desc. update oper. in service table");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(ServiceEntry.TABLE_NAME, values, selection, selectionArgs);

        return rowsUpdated;
    }

    private int updateSuggestionData(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(SuggestionEntry.COLUMN_USER_ID)) {
            int userId = values.getAsInteger(SuggestionEntry.COLUMN_USER_ID);
            if (!ServiceMgmtContract.isValidId(userId))
                throw new IllegalArgumentException("invalid uerid update oper. in sugg. table");
        }
        if (values.containsKey(SuggestionEntry.COLUMN_SUGGESTION_TYPE)) {
            int suggType = values.getAsInteger(SuggestionEntry.COLUMN_SUGGESTION_TYPE);
            if (!SuggestionEntry.isValidSuggestionType(suggType))
                throw new IllegalArgumentException("invalid sugg. type update oper. in sugg. table");
        }
        if (values.containsKey(SuggestionEntry.COLUMN_DESCRIPTION)) {
            String desc = values.getAsString(SuggestionEntry.COLUMN_DESCRIPTION);
            if (TextUtils.isEmpty(desc))
                throw new IllegalArgumentException("empty desc. update oper. in sugg. table");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(SuggestionEntry.TABLE_NAME, values, selection, selectionArgs);

        return rowsUpdated;
    }

    private int updateCurrentIdData(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(CurrentIdEntry.COLUMN_USER_ID)) {
            int userId = values.getAsInteger(CurrentIdEntry.COLUMN_USER_ID);
            if (!ServiceMgmtContract.isValidId(userId))
                throw new IllegalArgumentException("invalid uerid update oper. in currentid table");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(CurrentIdEntry.TABLE_NAME, values, selection, selectionArgs);

        return rowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

}