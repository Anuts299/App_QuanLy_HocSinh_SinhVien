package com.example.app_quanly_hocsinh_sinhvien.subject_manage;

import java.util.HashMap;
import java.util.Map;

public class Subject {
    private String id;
    private String ten_mon_hoc;
    private int so_dvht;
    private float so_gio_LT;
    private float so_gio_TH;
    private String id_chuyen_nganh;

    public Subject() {
    }

    public Subject(String id, String ten_mon_hoc, int so_dvht, float so_gio_LT, float so_gio_TH, String id_chuyen_nganh) {
        this.id = id;
        this.so_dvht = so_dvht;
        this.so_gio_LT = so_gio_LT;
        this.so_gio_TH = so_gio_TH;
        this.ten_mon_hoc = ten_mon_hoc;
        this.id_chuyen_nganh = id_chuyen_nganh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_chuyen_nganh() {
        return id_chuyen_nganh;
    }

    public void setId_chuyen_nganh(String id_chuyen_nganh) {
        this.id_chuyen_nganh = id_chuyen_nganh;
    }

    public int getSo_dvht() {
        return so_dvht;
    }

    public void setSo_dvht(int so_dvht) {
        this.so_dvht = so_dvht;
    }

    public float getSo_gio_LT() {
        return so_gio_LT;
    }

    public void setSo_gio_LT(float so_gio_LT) {
        this.so_gio_LT = so_gio_LT;
    }

    public float getSo_gio_TH() {
        return so_gio_TH;
    }

    public void setSo_gio_TH(float so_gio_TH) {
        this.so_gio_TH = so_gio_TH;
    }

    public String getTen_mon_hoc() {
        return ten_mon_hoc;
    }

    public void setTen_mon_hoc(String ten_mon_hoc) {
        this.ten_mon_hoc = ten_mon_hoc;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("ten_mon_hoc", ten_mon_hoc);
        result.put("so_dvht", so_dvht);
        result.put("so_gio_LT", so_gio_LT);
        result.put("so_gio_TH", so_gio_TH);
        result.put("id_chuyen_nganh", id_chuyen_nganh);
        return result;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id='" + id + '\'' +
                ", ten_mon_hoc='" + ten_mon_hoc + '\'' +
                ", so_dvht=" + so_dvht +
                ", so_gio_LT=" + so_gio_LT +
                ", so_gio_TH=" + so_gio_TH +
                ", id_chuyen_nganh='" + id_chuyen_nganh + '\'' +
                '}';
    }
}
