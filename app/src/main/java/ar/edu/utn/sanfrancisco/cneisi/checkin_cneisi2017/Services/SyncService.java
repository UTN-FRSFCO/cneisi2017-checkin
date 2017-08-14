package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Network.SyncAdapter;

public class SyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /**
     * Nuevos sync requests va a ser mandados directamente al SyncAdapter usando este canal.
     * @param intent Intent que invoca.
     * @return Binder handle for {@link SyncAdapter}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
