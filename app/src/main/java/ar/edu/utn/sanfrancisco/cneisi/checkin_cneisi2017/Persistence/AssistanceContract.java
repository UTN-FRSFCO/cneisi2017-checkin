package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class AssistanceContract {
    public static final String AUTHORITY = "ar.edu.ar.sanfrancisco.cneisi.checkin_cneisi2017.provider";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri Assistance_URI = Uri.withAppendedPath(AssistanceContract.BASE_URI, "/assistance");

    public static final String URI_TYPE_ASSISTANCE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "vnd.ar.edu.ar.sanfrancisco.cneisi.checkin_cneisi2017.provider.assistantes";

    public static final String URI_TYPE_STUDENT_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE +
            "vnd.ar.edu.ar.sanfrancisco.cneisi.checkin_cneisi2017.provider.students";

    public static abstract class AssistanceEntry implements BaseColumns {
        public static final String TABLE_NAME ="assistences";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DNI = "dni";
        public static final String DOCKET = "docket";
        public static final String DATE = "date";
        public static final String CONFERENCEID = "conference_id";
        public static final String SENT = "sent";
    }
}
