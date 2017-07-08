package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.models;

import java.util.List;

public class Auditorium {
    private int id;
    private String name;

    private List<Conference> conferences;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Conference> getConferences() {
        return conferences;
    }

    public void setConferences(List<Conference> conferences) {
        this.conferences = conferences;
    }
}
