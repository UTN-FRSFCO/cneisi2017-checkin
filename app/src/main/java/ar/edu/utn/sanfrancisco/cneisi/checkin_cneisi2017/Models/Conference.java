package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence.DatabaseContract;

public class Conference {
    private int id;
    private String title;
    private String description;
    private String date;
    private int duration;
    private String auditorium;
    private int idCloud;

    public Conference() {

    }

    public Conference(Cursor cursor) {
        int idIndex = cursor.getColumnIndexOrThrow("_id");
        int titleIndex = cursor.getColumnIndexOrThrow("title");
        int descriptionIndex = cursor.getColumnIndexOrThrow("description");
        int dateIndex = cursor.getColumnIndexOrThrow("date");
        int durationIndex = cursor.getColumnIndexOrThrow("duration");
        int autitoriumIndex = cursor.getColumnIndexOrThrow("auditorium");
        int idCloudIndex = cursor.getColumnIndexOrThrow("id_cloud");


        this.id = cursor.getInt(idIndex);
        this.title = cursor.getString(titleIndex);
        this.description = cursor.getString(descriptionIndex);
        this.date = cursor.getString(dateIndex);
        this.duration = cursor.getInt(durationIndex);
        this.auditorium = cursor.getString(autitoriumIndex);
        this.idCloud = cursor.getInt(idCloudIndex);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAuditorium() {
        return auditorium;
    }

    public void setAuditorium(String auditorium) {
        this.auditorium = auditorium;
    }

    public int getExternalId() {
        return idCloud;
    }

    public void setExternalId(int externalId) {
        this.idCloud = externalId;
    }

    public static ArrayList<Conference> fromJson(JSONArray json) {
        JSONObject conferenceJson;
        ArrayList<Conference> conferences = new ArrayList<Conference>(json.length());

        for (int i = 0; i < json.length(); i++) {
            try {
                conferenceJson = json.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Conference conference = Conference.fromJson(conferenceJson);
            if (conference != null) {
                conferences.add(conference);
            }
        }

        return conferences;
    }

    public static Conference fromJson(JSONObject jsonObject) {
        Conference conference = new Conference();

        try {
            conference.title = jsonObject.getString("title");
            conference.description = jsonObject.getString("description");
            conference.date = jsonObject.getString("date");
            conference.duration = jsonObject.getInt("duration");
            conference.auditorium = jsonObject.getString("auditorium");
            conference.idCloud = jsonObject.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return conference;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ConferenceEntry.TITLE, title);
        values.put(DatabaseContract.ConferenceEntry.DESCRIPTION, description);
        values.put(DatabaseContract.ConferenceEntry.DATE, date);
        values.put(DatabaseContract.ConferenceEntry.DURATION, duration);
        values.put(DatabaseContract.ConferenceEntry.AUDITORIUM, auditorium);
        values.put(DatabaseContract.ConferenceEntry.ID_CLOUD, idCloud);
        return values;
    }

    public String getHour() {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.getDate());

            String formattedDate = new SimpleDateFormat("HH:mm").format(date);

            return formattedDate;
        } catch (Exception e) {
            return this.getDate();
        }
    }
}
