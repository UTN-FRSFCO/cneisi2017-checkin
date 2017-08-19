package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence;

import android.provider.BaseColumns;

public final class DatabaseContract {

    public DatabaseContract() {

    }

    public static abstract class ConferenceEntry implements BaseColumns {
        public static final String TABLE_NAME = "conferences";

        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String DATE = "date";
        public static final String DURATION = "duration";
        public static final String AUDITORIUM = "auditorium";
        public static final String ID_CLOUD = "id_cloud";

        public static final String TEXT_TYPE = " TEXT";
        public static final String INTEGER_TYPE = " INTEGER";
        public static final String COMMA_SEP = ",";

        public static final String SQL_CREATE_CONFERENCES_TABLE =
                "CREATE TABLE " + ConferenceEntry.TABLE_NAME + " (" +
                        ConferenceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        ConferenceEntry.TITLE + TEXT_TYPE + COMMA_SEP +
                        ConferenceEntry.DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                        ConferenceEntry.DATE + TEXT_TYPE + COMMA_SEP +
                        ConferenceEntry.DURATION + INTEGER_TYPE + COMMA_SEP +
                        ConferenceEntry.AUDITORIUM + TEXT_TYPE + COMMA_SEP +
                        ConferenceEntry.ID_CLOUD + INTEGER_TYPE + COMMA_SEP +
                        "UNIQUE (" + ConferenceEntry._ID + "))";

        public static final String SQL_DELETE_CONFERENCES =
                "DROP TABLE IF EXISTS " + ConferenceEntry.TABLE_NAME;
    }
}
