package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.models;

import android.content.ContentValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.data.AssistanceContract;

import static android.R.attr.phoneNumber;
import static ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.data.AssistanceContract.*;

public class Assistance {
    private String id;
    private String name;
    private String dni;
    private String docket;
    private Date date;
    private int conferenceId;

    public Assistance() {

    }

    public Assistance(JSONObject assistantJson, int conferenceId) {
        try {
            this.id = UUID.randomUUID().toString();
            this.name = assistantJson.getString("nombre");
            this.dni = assistantJson.getString("dni");
            this.docket = assistantJson.getString("legajo");
            this.date = new Date();
            this.conferenceId = conferenceId;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDocket() {
        return docket;
    }

    public void setDocket(String docket) {
        this.docket = docket;
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

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(AssistanceEntry.ID, id);
        values.put(AssistanceEntry.NAME, name);
        values.put(AssistanceEntry.DNI, dni);
        values.put(AssistanceEntry.DOCKET, docket);
        values.put(AssistanceEntry.DATE, date.toString());
        values.put(AssistanceEntry.CONFERENCEID, conferenceId);
        return values;
    }
}

