package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.format.DateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

import static ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence.AssistanceContract.*;

public class Assistance {
    private int id;
    private String dni;
    private String date;
    private int conferenceId;
    private String catcherName;
    private boolean sent;

    public Assistance() {

    }

    public Assistance(Cursor cursor) {
        int idIndex = cursor.getColumnIndexOrThrow("_id");
        int dniIndex = cursor.getColumnIndexOrThrow("dni");
        int conferenceIdIndex = cursor.getColumnIndexOrThrow("conference_id");
        int dateIndex = cursor.getColumnIndexOrThrow("date");
        int catcherIndex = cursor.getColumnIndexOrThrow("catcher_name");
        int sentIndex = cursor.getColumnIndexOrThrow("sent");

        this.id = cursor.getInt(idIndex);
        this.dni = cursor.getString(dniIndex);
        this.date = cursor.getString(dateIndex);
        this.conferenceId = cursor.getInt(conferenceIdIndex);
        this.catcherName = cursor.getString(catcherIndex);
        this.sent = (cursor.getInt(sentIndex) == 1);
    }

    public Assistance(JSONObject assistantJson, int conferenceId, String catcherName) {
        try {
            this.dni = assistantJson.getString("dni");
            this.date = DateFormat.format("dd/MM/yyyy HH:mm:ss", new Date()).toString();
            this.conferenceId = conferenceId;
            this.catcherName = catcherName;
            this.sent = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(int conferenceId) {
        this.conferenceId = conferenceId;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getCatcherName() {
        return catcherName;
    }

    public void setCatcherName(String catcherName) {
        this.catcherName = catcherName;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(AssistanceEntry.DNI, dni);
        values.put(AssistanceEntry.DATE, date.toString());
        values.put(AssistanceEntry.CONFERENCEID, conferenceId);
        values.put(AssistanceEntry.CATCHER_NAME, catcherName);
        values.put(AssistanceEntry.SENT, sent);
        return values;
    }
}

