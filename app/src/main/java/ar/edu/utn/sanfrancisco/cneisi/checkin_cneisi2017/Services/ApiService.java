package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Services;

import android.os.StrictMode;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models.Assistance;

public class ApiService {

    private String API_URL = "http://82f46fec.ngrok.io/api";

    public boolean postAssistance(Assistance assistance) {
        try {
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            String urlPath = API_URL + "/assistance?api_token=123456";
            URL url = new URL(urlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept", "application/json");

            urlConnection.setDoOutput(true);

            String params = "conference_id="  + assistance.getConferenceId() +
                    "&date=" + DateFormat.format("dd/MM/yyyy", assistance.getDate()).toString() +
                    "&dni=" + assistance.getDni() +
                    "&catcher_name=" + assistance.getCatcherName();

            urlConnection.setDoOutput(true);
            OutputStream os = urlConnection.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();

            urlConnection.connect();

            Log.i(urlConnection.getResponseMessage(), "Failed");

            try {
                if(urlConnection.getResponseCode() == 200) {
                    return true;
                }
            } finally {
                urlConnection.disconnect();
            }

            return false;
        } catch (Exception e) {
            Log.e("ERROR AL ENVIAR", e.getMessage(), e);
            return false;
        }
    }
}
