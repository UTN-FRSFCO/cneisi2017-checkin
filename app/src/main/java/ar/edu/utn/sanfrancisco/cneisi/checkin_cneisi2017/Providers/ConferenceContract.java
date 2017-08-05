package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Providers;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ConferenceContract {
    public static final String AUTHORITY = "ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.provider";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONFERENCES_URI = Uri.withAppendedPath(ConferenceContract.BASE_URI, "/conferences");

    /*
        MIME Types
        Para listas se necesita  'vnd.android.cursor.dir/vnd.com.example.andres.provider.students
        Para items se necesita 'vnd.android.cursor.item/vnd.com.example.andres.provider.students'
        La primera parte viene esta definida en constantes de ContentResolver
     */
    public static final String URI_TYPE_CONFERENCE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "/vnd.ar.edu.utn.sanfrancisco.cneisi.provider.conferences";

    public static final String URI_TYPE_CONFERENCE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE +
            "/vnd.ar.edu.utn.sanfrancisco.cneisi.provider.conferences";

    /*
        Tabla definida en provider. Aca podria ser una distinta a la de la base de datos,
        pero consideramos la misma.
     */
    public static final class ConferencesColumns implements BaseColumns {

        private ConferencesColumns(){}

        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String DATE = "date";
        public static final String AUDITORIUM_ID = "auditorium_id";
        public static final String ID_CLOUD = "id_cloud";

        public static final String DEFAULT_SORT_ORDER = ID_CLOUD + " ASC";

    }
}
