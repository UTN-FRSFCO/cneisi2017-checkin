package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.IntegerRes;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence.AssistanceContract.AssistanceEntry;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models.Assistance;

public class AssistanceDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cneisi.db";

    private static AssistanceDbHelper sInstance;

    public AssistanceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized AssistanceDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AssistanceDbHelper(context.getApplicationContext());
        }

        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DatabaseContract.ConferenceEntry.SQL_CREATE_CONFERENCES_TABLE);
        sqLiteDatabase.execSQL("CREATE TABLE " + AssistanceEntry.TABLE_NAME + " ("
                + AssistanceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AssistanceEntry.DNI + " TEXT NOT NULL,"
                + AssistanceEntry.DATE + " TEXT NOT NULL,"
                + AssistanceEntry.CONFERENCEID + " INTEGER NOT NULL,"
                + AssistanceEntry.CATCHER_NAME + " TEXT NOT NULL,"
                + AssistanceEntry.SENT + " BOOLEAN NOT NULL,"
                + "UNIQUE (" + AssistanceEntry._ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.ConferenceEntry.SQL_DELETE_CONFERENCES);
        db.execSQL("DROP TABLE IF EXISTS " + AssistanceEntry.TABLE_NAME);
        onCreate(db);
    }

    public long saveAssistance(Assistance assistance) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                AssistanceEntry.TABLE_NAME,
                null,
                assistance.toContentValues());
    }

    public long updateAssistance(Assistance assistance) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.update(
                AssistanceEntry.TABLE_NAME,
                assistance.toContentValues(),
                AssistanceEntry._ID + " LIKE ?",
                new String[]{Integer.toString(assistance.getId())}
        );
    }

    public Cursor getAllAssistances() {
        return getReadableDatabase()
                .query(
                        AssistanceEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getUnsyncAssistances() {
        return getReadableDatabase()
                .query(
                        AssistanceEntry.TABLE_NAME,
                        null,
                        AssistanceEntry.SENT + "= ?",
                        new String[]{"0"},
                        null,
                        null,
                        null);
    }

    public Cursor getByConferenceIdCloud(int idCloud) {
        return getReadableDatabase()
                .query(
                        AssistanceEntry.TABLE_NAME,
                        null,
                        AssistanceEntry.CONFERENCEID + "= ?",
                        new String[]{Integer.toString(idCloud)},
                        null,
                        null,
                        null);
    }

    public Cursor getAssistanceById(String assistanceId) {
        Cursor c = getReadableDatabase().query(
                AssistanceEntry.TABLE_NAME,
                null,
                AssistanceEntry._ID + " LIKE ?",
                new String[]{assistanceId},
                null,
                null,
                null);
        return c;
    }
}
