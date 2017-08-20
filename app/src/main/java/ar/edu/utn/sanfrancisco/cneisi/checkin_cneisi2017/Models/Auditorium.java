package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models;

import java.util.List;

public class Auditorium {
    private int id;
    private String name;
    private String code;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
