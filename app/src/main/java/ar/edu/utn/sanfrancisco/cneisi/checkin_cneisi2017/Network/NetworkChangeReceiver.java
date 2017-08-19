package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models.Assistance;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence.AssistanceDbHelper;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Services.ApiService;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if(checkInternet(context))
        {
            AssistanceDbHelper assistanceDbHelper = AssistanceDbHelper.getInstance(context);
            ApiService apiService = new ApiService();


            try {
                Cursor cursor = assistanceDbHelper.getUnsyncAssistances();

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Assistance assistance = new Assistance(cursor);
                    cursor.moveToNext();

                    boolean sent = apiService.postAssistance(assistance);

                    if (sent) {
                        assistance.setSent(true);
                        assistanceDbHelper.updateAssistance(assistance);
                    }
                }
            } catch (Exception e)
            {
                Log.e("ERROR", e.getMessage());
            }
        }
    }

    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            return true;
        } else {
            return false;
        }
    }
}
