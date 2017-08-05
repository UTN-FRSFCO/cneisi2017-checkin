package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConferenceDbHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cneisi.db";

    private static ConferenceDbHelper sInstance;

    private ConferenceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized ConferenceDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ConferenceDbHelper(context.getApplicationContext());
        }

        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.ConferenceEntry.SQL_CREATE_CONFERENCES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.ConferenceEntry.SQL_DELETE_CONFERENCES);
        onCreate(db);
    }
}
