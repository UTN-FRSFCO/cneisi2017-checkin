package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Services;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models.Assistance;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models.Conference;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence.AssistanceDbHelper;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence.ConferenceDbHelper;

public class ApiService {

    private String API_URL = "http://dc32fb58.ngrok.io/api";

    public boolean postAssistance(Assistance assistance) {
        try {
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
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

            String params = "conference_id=" + assistance.getConferenceId() +
                    "&date=" + assistance.getDate() +
                    "&dni=" + assistance.getDni() +
                    "&catcher_name=" + assistance.getCatcherName();

            urlConnection.setDoOutput(true);
            OutputStream os = urlConnection.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();

            urlConnection.connect();

            try {
                if (urlConnection.getResponseCode() == 200) {
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

    public void getConferences(Context context) {
        AsyncGetConferences asyncGetConferences = new AsyncGetConferences(context);
        asyncGetConferences.execute();
    }

    public class AsyncGetConferences extends AsyncTask<String, String, String> {
        protected Context context;

        public AsyncGetConferences(Context context)
        {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String urlPath = API_URL + "/conferences?api_token=123456";
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    String jsonString = stringBuilder.toString();

                    return jsonString;
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String jsonString) {
            super.onPostExecute(jsonString);

            try {
                final JSONArray jsonArray = new JSONArray(jsonString);

                final ArrayList<Conference> conferences = Conference.fromJson(jsonArray);

                saveConferences(conferences);
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage());
            }
        }

        public void saveConferences(ArrayList<Conference> conferences) {
            ConferenceDbHelper conferenceDbHelper = ConferenceDbHelper.getInstance(this.context);

            if (conferenceDbHelper.getAllConferences().getCount() == 0) {
                for (Conference conference: conferences) {
                    conferenceDbHelper.saveConference(conference);
                }
            }
        }
    }
}
