package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence.AssistanceContract.AssistanceEntry;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models.Assistance;

public class AssistanceDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cneisi.db";

    public AssistanceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + AssistanceEntry.TABLE_NAME + " ("
                + AssistanceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AssistanceEntry.ID + " TEXT NOT NULL,"
                + AssistanceEntry.DNI + " TEXT NOT NULL,"
                + AssistanceEntry.DATE + " TEXT NOT NULL,"
                + AssistanceEntry.CONFERENCEID + " INTEGER NOT NULL,"
                + AssistanceEntry.CATCHER_NAME + " TEXT NOT NULL,"
                + AssistanceEntry.SENT + " BOOLEAN NOT NULL,"
                + "UNIQUE (" + AssistanceEntry.ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
                "id=" + assistance.getId(),
                null
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

    public Cursor getAssistanceById(String assistanceId) {
        Cursor c = getReadableDatabase().query(
                AssistanceEntry.TABLE_NAME,
                null,
                AssistanceEntry.ID + " LIKE ?",
                new String[]{assistanceId},
                null,
                null,
                null);
        return c;
    }
}
