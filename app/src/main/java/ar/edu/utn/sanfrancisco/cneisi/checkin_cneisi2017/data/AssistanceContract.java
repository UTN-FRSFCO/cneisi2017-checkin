package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.data;

import android.provider.BaseColumns;

public class AssistanceContract {
    public static abstract class AssistanceEntry implements BaseColumns {
        public static final String TABLE_NAME ="assistences";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DNI = "dni";
        public static final String DOCKET = "docket";
        public static final String DATE = "date";
        public static final String CONFERENCEID = "conference_id";
    }
}
