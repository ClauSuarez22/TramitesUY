package uy.edu.ucu.android.tramitesuy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract.CategoryEntry;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract.LocationEntry;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract.ProceedingEntry;

/**
 * SQLiteOpenHelper that creates tables on a sqlite database
 * IMPORTANT! YOU MUST NOT CHANGE THIS FILE!
 */
public class ProceedingsOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "proceedings.db";

    public ProceedingsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoryEntry.COLUMN_CODE + " TEXT NOT NULL, " +
                CategoryEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                " UNIQUE (" + CategoryEntry.COLUMN_CODE + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_PROCEEDINGS_TABLE = "CREATE TABLE " + ProceedingEntry.TABLE_NAME + " (" +
                ProceedingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ProceedingEntry.COLUMN_TITLE + " TEXT, " +
                ProceedingEntry.COLUMN_DESCRIPTION + " TEXT, " +
                ProceedingEntry.COLUMN_MAIL + " TEXT, " +
                ProceedingEntry.COLUMN_URL + " TEXT, " +
                ProceedingEntry.COLUMN_REQUISITES + " TEXT, " +
                ProceedingEntry.COLUMN_STATUS + " TEXT, " +
                ProceedingEntry.COLUMN_PROCESS + " TEXT, " +
                ProceedingEntry.COLUMN_ONLINE_ACCESS + " TEXT, " +
                ProceedingEntry.COLUMN_DEPENDS_ON + " TEXT, " +
                ProceedingEntry.COLUMN_LOCATION_OTHER_DATA + " TEXT, " +
                ProceedingEntry.COLUMN_HOW_TO_APPLY + " TEXT, " +
                ProceedingEntry.COLUMN_TAKE_INTO_ACCOUNT_OTHER_DATA + " TEXT, " +
                ProceedingEntry.COLUMN_COST + " TEXT, " +

                // category fk
                ProceedingEntry.COLUMN_CAT_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + ProceedingEntry.COLUMN_CAT_KEY + ") REFERENCES " +
                CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + "));";


        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LocationEntry.COLUMN_IS_URUGUAY + " TEXT, " +
                LocationEntry.COLUMN_ADDRESS + " TEXT, " +
                LocationEntry.COLUMN_CITY + " TEXT, " +
                LocationEntry.COLUMN_COMMENTS + " TEXT, " +
                LocationEntry.COLUMN_PHONE + " TEXT, " +
                LocationEntry.COLUMN_TIME + " TEXT, " +
                LocationEntry.COLUMN_STATE + " TEXT, " +

                // proceeding fk
                LocationEntry.COLUMN_PROC_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + LocationEntry.COLUMN_PROC_KEY + ") REFERENCES " +
                ProceedingEntry.TABLE_NAME + " (" + ProceedingEntry._ID + "));";


        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_PROCEEDINGS_TABLE);
        db.execSQL(SQL_CREATE_LOCATION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ProceedingEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }

}
