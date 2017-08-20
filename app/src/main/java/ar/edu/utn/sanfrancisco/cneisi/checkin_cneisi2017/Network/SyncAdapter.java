package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Network;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence.DatabaseContract;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Providers.ConferenceContract;

/**
 * Clase SyncAdapter que implementa los métodos relevantes para la sincronzación.
 * El método {@link #onPerformSync(Account, Bundle, String, ContentProviderClient, SyncResult)}
 * es el más relevante, ya que este es el que se llama cuando se va a realizar la sincronización.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private ContentResolver mContentResolver;
    private String mToken;
    private AccountManager mAccountManager;

    public SyncAdapter (Context context, boolean autoInitialize){
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        mAccountManager = AccountManager.get(context);
    }

    /**
     * Llamado cuando se sincroniza. Acá se debe hacer el llamado al servidor y la
     * actualizacion de datos locales.
     * @param account
     * @param extras
     * @param authority
     * @param provider
     * @param syncResult
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        try {
            //TODO HACER PETICIO A API
            // Manejo de errores
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes data coming from the cloud, POST or GET, and upgrade the corresponding values
     * calling corresponding methods.
     */
    protected void updateData(ArrayList<String> responses)
            throws JSONException, RemoteException, OperationApplicationException {
        if (responses == null) return;
        if (responses.size()>0){
            String response = responses.get(0);
            JSONArray jsonArray = new JSONArray(response);
            Uri uri = ConferenceContract.CONFERENCES_URI;
            Cursor cursor = mContentResolver.query(ConferenceContract.CONFERENCES_URI,
                    null, null, null, null);
            ArrayList<Integer> conferences = new ArrayList<>();
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                int idsCloud = cursor.getInt(cursor.getColumnIndexOrThrow(
                        ConferenceContract.ConferencesColumns.ID_CLOUD));
                conferences.add(idsCloud);
            }
            for (int i=0; i< jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString(DatabaseContract.ConferenceEntry.TITLE);
                String description = jsonObject.getString(DatabaseContract.ConferenceEntry.DESCRIPTION);
                String date = jsonObject.getString(DatabaseContract.ConferenceEntry.DATE);
                int idCloud = jsonObject.getInt(DatabaseContract.ConferenceEntry.ID);
                if (!conferences.contains(idCloud)){
                    ContentValues values = new ContentValues();
                    values.put(ConferenceContract.ConferencesColumns.TITLE, title);
                    values.put(ConferenceContract.ConferencesColumns.DESCRIPTION, description);
                    values.put(ConferenceContract.ConferencesColumns.DATE, date);
                    values.put(ConferenceContract.ConferencesColumns.ID_CLOUD, idCloud);

                    mContentResolver.insert(uri, values);
                }
            }
            cursor.close();
        }
    }
}
