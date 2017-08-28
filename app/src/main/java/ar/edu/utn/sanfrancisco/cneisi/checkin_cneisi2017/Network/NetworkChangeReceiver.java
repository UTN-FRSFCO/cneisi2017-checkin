package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
            AsyncPostAssistances asyncPostAssistances = new AsyncPostAssistances(context);
            asyncPostAssistances.execute();
        }
    }

//    boolean checkInternet(Context context) {
//        ServiceManager serviceManager = new ServiceManager(context);
//        if (serviceManager.isNetworkAvailable()) {
//            return true;
//        } else {
//            return false;
//        }
//    }


    private boolean checkInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        return (info != null && info.isConnected() && info.isAvailable());
    }

    public class AsyncPostAssistances extends AsyncTask<String, String, Void> {
        protected Context context;

        public AsyncPostAssistances(Context context)
        {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
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

                cursor.close();
            } catch (Exception e)
            {
                Log.e("ERROR SYNC", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Log.i("SYNC INFO", "Asistencias sincronizadas");
        }
    }
}
