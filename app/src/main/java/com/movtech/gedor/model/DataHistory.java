package com.movtech.gedor.model;

public class DataHistory {
    int id;
    String activity;
    String waktu;

    public DataHistory(int id, String activity, String waktu) {
        this.id = id;
        this.activity = activity;
        this.waktu = waktu;
    }

    public int getId() {
        return id;
    }

    public String getActivity() {
        return activity;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }
}
