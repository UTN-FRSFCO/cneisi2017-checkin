package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.IntegerRes;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models.Conference;

public class ConferenceDbHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cneisi.db";

    private static ConferenceDbHelper sInstance;

    public ConferenceDbHelper(Context context) {
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
        db.execSQL("CREATE TABLE " + AssistanceContract.AssistanceEntry.TABLE_NAME + " ("
                + AssistanceContract.AssistanceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AssistanceContract.AssistanceEntry.DNI + " TEXT NOT NULL,"
                + AssistanceContract.AssistanceEntry.DATE + " TEXT NOT NULL,"
                + AssistanceContract.AssistanceEntry.CONFERENCEID + " INTEGER NOT NULL,"
                + AssistanceContract.AssistanceEntry.CATCHER_NAME + " TEXT NOT NULL,"
                + AssistanceContract.AssistanceEntry.SENT + " BOOLEAN NOT NULL,"
                + "UNIQUE (" + AssistanceContract.AssistanceEntry._ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.ConferenceEntry.SQL_DELETE_CONFERENCES);
        db.execSQL("DROP TABLE IF EXISTS " + AssistanceContract.AssistanceEntry.TABLE_NAME);
        onCreate(db);
    }

    public long saveConference(Conference conference) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                DatabaseContract.ConferenceEntry.TABLE_NAME,
                null,
                conference.toContentValues());
    }

    public long updateConference(Conference conference) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.update(
                DatabaseContract.ConferenceEntry.TABLE_NAME,
                conference.toContentValues(),
                DatabaseContract.ConferenceEntry._ID + " LIKE ?",
                new String[]{Integer.toString(conference.getId())}
        );
    }

    public Cursor getAllConferences() {
        return getReadableDatabase()
                .query(
                        DatabaseContract.ConferenceEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getByAuditorium(String auditorium) {
        return getReadableDatabase()
                .query(
                        DatabaseContract.ConferenceEntry.TABLE_NAME,
                        null,
                        DatabaseContract.ConferenceEntry.AUDITORIUM + "= ?",
                        new String[]{auditorium},
                        null,
                        null,
                        null);
    }

    public Cursor getByIdCloud(int idCloud) {
        return getReadableDatabase()
                .query(
                        DatabaseContract.ConferenceEntry.TABLE_NAME,
                        null,
                        DatabaseContract.ConferenceEntry.ID_CLOUD + "= ?",
                        new String[]{Integer.toString(idCloud)},
                        null,
                        null,
                        null);
    }
}
