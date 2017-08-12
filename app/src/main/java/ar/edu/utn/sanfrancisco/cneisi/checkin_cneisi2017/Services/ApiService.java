package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Services;

import android.os.StrictMode;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models.Assistance;

public class ApiService {

    private String API_URL = "http://127.0.0.1:8000/:8000/api";

    public boolean postAssistance(Assistance assistance) {
        try {
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                //your codes here

            }

            String urlPath = API_URL + "/assistance";
            URL url = new URL(urlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("conference_id", "1");
            urlConnection.setRequestProperty("date", "11/11/2017");
            urlConnection.setRequestProperty("dni", "98989898");
            urlConnection.setRequestProperty("catcher_name", "Joseee");
            urlConnection.connect();

            try {
                if(urlConnection.getResponseCode() == 200) {
                    return true;
                }
            } finally {
                urlConnection.disconnect();
            }

            return false;
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return false;
        }
    }
}
