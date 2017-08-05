package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence.ConferenceDbHelper;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence.DatabaseContract;

public class ConferenceProvider extends ContentProvider {
    public static final int CONFERENCE_LIST = 1;
    public static final int CONFERENCE_ID = 2;

    private static final UriMatcher sUriMatcher;
    static{
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            /*
                URI para todos las conferencias.
                Se setea que cuando se pregunta a UriMatcher por la URI:
                content://ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.provider/conferences
                se devuelva un entero con el valor de 1.
             */
        sUriMatcher.addURI(ConferenceContract.AUTHORITY, "conferences", CONFERENCE_LIST);
            /*
                URI para una conferencia.
                Se setea que cuando se pregunta a UriMatcher por la URI:
                content://ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.provider/conferences/#
                se devuelva un entero con el valor de 2.
             */
        sUriMatcher.addURI(ConferenceContract.AUTHORITY, "conferences/#", CONFERENCE_ID);
    }

    private ConferenceDbHelper mDbHelper;

    public ConferenceProvider() { }

    @Override
    public boolean onCreate() {
        mDbHelper = ConferenceDbHelper.getInstance(getContext());
        return true;
    }

    /*
        Llamado para borrar una o mas filas de una tabla
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rows = 0;
        switch (sUriMatcher.match(uri)) {
            case CONFERENCE_LIST:
                // Se borran todas las filas
                rows = db.delete(DatabaseContract.ConferenceEntry.TABLE_NAME, null, null);
                break;
            case CONFERENCE_ID:
                // Se borra la fila del ID seleccionado
                rows = db.delete(DatabaseContract.ConferenceEntry.TABLE_NAME, selection, selectionArgs);
        }
        // Se retorna el numero de filas eliminadas
        return rows;
    }

    /*
        Se determina el MIME Type del dato o conjunto de datos al que apunta la URI
     */
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case CONFERENCE_LIST:
                return ConferenceContract.URI_TYPE_CONFERENCE_DIR;
            case CONFERENCE_ID:
                return ConferenceContract.URI_TYPE_CONFERENCE_ITEM;
            default:
                return null;
        }
    }

    /*
        Inserta nuevas conferencias
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.insert(DatabaseContract.ConferenceEntry.TABLE_NAME, null, values);

        // Le avisa a los observadores
        getContext().getContentResolver().notifyChange(uri, null);

        return null;
    }

    /*
        Retorna el o los datos que se le pida de acuerdo a la URI
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)){

            // Se pide la lista completa de conferencias
            case CONFERENCE_LIST:
                // Si no hay un orden especificado,
                // lo ordenamos de manera ascendente de acuerdo a lo que diga el contrato
                if (sortOrder == null || TextUtils.isEmpty(sortOrder))
                    sortOrder = ConferenceContract.ConferencesColumns.DEFAULT_SORT_ORDER;
                break;

            // Se pide una conferencia en particular
            case CONFERENCE_ID:
                // Se adjunta la ID de la conferencia selecciondo en el filtro de la seleccion
                if (selection == null)
                    selection = "";
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;

            // La URI que se recibe no esta definida
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + uri);
        }
        Cursor cursor = db.query(DatabaseContract.ConferenceEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        // Se retorna un cursor sobre el cual se debe iterar para obtener los datos
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // No se implemento un update
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
