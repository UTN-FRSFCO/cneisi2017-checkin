package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models;

import android.content.ContentValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

import static ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence.AssistanceContract.*;

public class Assistance {
    private String id;
    private String dni;
    private Date date;
    private int conferenceId;
    private String catcherName;
    private boolean sent;

    public Assistance() {

    }

    public Assistance(JSONObject assistantJson, int conferenceId, String catcherName) {
        try {
            this.id = UUID.randomUUID().toString();
            this.dni = assistantJson.getString("dni");
            this.date = new Date();
            this.conferenceId = conferenceId;
            this.catcherName = catcherName;
            this.sent = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

