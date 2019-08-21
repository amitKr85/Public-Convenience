package com.example.amit.projectapp2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.amit.projectapp2.data.ServiceMgmtContract.*;

public class ServiceMgmtDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "service_mgmt.db";
    public static final int DATABASE_VERSION = 1;

    public ServiceMgmtDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " ("
                + UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UserEntry.COLUMN_USERNAME + " TEXT,"
                + UserEntry.COLUMN_PASSWORD + " TEXT,"
                + UserEntry.COLUMN_FULL_NAME + " TEXT,"
                + UserEntry.COLUMN_CONTACT_NUMBER + " TEXT,"
                + UserEntry.COLUMN_PIN_CODE + " INTEGER,"
                + UserEntry.COLUMN_HOUSE_NUMBER + " TEXT,"
                + UserEntry.COLUMN_LOCALITY + " TEXT,"
                + UserEntry.COLUMN_lANDMARK + " TEXT,"
                + UserEntry.COLUMN_CITY + " TEXT,"
                + UserEntry.COLUMN_STATE + " TEXT,"
                + UserEntry.COLUMN_COUNTRY + " TEXT,"
                + UserEntry.COLUMN_IS_PROVIDER + " INTEGER);";

        String SQL_CREATE_PROVIDER_TABLE = "CREATE TABLE " + ProviderEntry.TABLE_NAME + " ("
                + ProviderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ProviderEntry.COLUMN_TYPE + " TEXT,"
                + ProviderEntry.COLUMN_PIN_CODE + " INTEGER,"
                + ProviderEntry.COLUMN_LOCALITY + " TEXT,"
                + ProviderEntry.COLUMN_CITY + " TEXT,"
                + ProviderEntry.COLUMN_STATE + " TEXT,"
                + ProviderEntry.COLUMN_COUNTRY + " TEXT,"
                + ProviderEntry.COLUMN_COMPANY_NAME + " TEXT);";

        String SQL_CREATE_APPOINTMENT_TABLE = "CREATE TABLE " + AppointmentEntry.TABLE_NAME + " ("
                + AppointmentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AppointmentEntry.COLUMN_USER_ID + " INTEGER,"
                + AppointmentEntry.COLUMN_PROVIDER_ID + " INTEGER,"
                + AppointmentEntry.COLUMN_SERVICE_ID + " INTEGER,"
                + AppointmentEntry.COLUMN_STATUS + " INTEGER);";

        String SQL_CREATE_SERVICE_TABLE = "CREATE TABLE " + ServiceEntry.TABLE_NAME + " ("
                + ServiceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ServiceEntry.COLUMN_TYPE + " TEXT,"
                + ServiceEntry.COLUMN_SHORT_DESCRIPTION + " TEXT,"
                + ServiceEntry.COLUMN_DESCRIPTION + " TEXT);";

        String SQL_CREATE_SUGGESTION_TABLE = "CREATE TABLE " + SuggestionEntry.TABLE_NAME + " ("
                + SuggestionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SuggestionEntry.COLUMN_USER_ID + " INTEGER,"
                + SuggestionEntry.COLUMN_SUGGESTION_TYPE + " INTEGER,"
                + SuggestionEntry.COLUMN_DESCRIPTION + " TEXT);";

        String SQL_CREATE_CURRENT_ID_TABLE = "CREATE TABLE " + CurrentIdEntry.TABLE_NAME + " ("
                + CurrentIdEntry.COLUMN_USER_ID + " INTEGER PRIMARY KEY);";

        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_PROVIDER_TABLE);
        db.execSQL(SQL_CREATE_APPOINTMENT_TABLE);
        db.execSQL(SQL_CREATE_SERVICE_TABLE);
        db.execSQL(SQL_CREATE_SUGGESTION_TABLE);
        db.execSQL(SQL_CREATE_CURRENT_ID_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
