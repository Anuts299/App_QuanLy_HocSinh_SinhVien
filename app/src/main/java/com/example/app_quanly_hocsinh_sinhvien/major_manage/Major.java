package com.example.app_quanly_hocsinh_sinhvien.major_manage;

import java.util.HashMap;
import java.util.Map;

public class Major {
    private String id;
    private String ten_chuyen_nganh;
    private String id_khoa;
    private String id_trinh_do;

    public Major() {
    }

    public Major(String id, String ten_chuyen_nganh, String id_khoa,  String id_trinh_do) {
        this.id = id;
        this.ten_chuyen_nganh = ten_chuyen_nganh;
        this.id_khoa = id_khoa;
        this.id_trinh_do = id_trinh_do;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_khoa() {
        return id_khoa;
    }

    public void setId_khoa(String id_khoa) {
        this.id_khoa = id_khoa;
    }

    public String getTen_chuyen_nganh() {
        return ten_chuyen_nganh;
    }

    public void setTen_chuyen_nganh(String ten_chuyen_nganh) {
        this.ten_chuyen_nganh = ten_chuyen_nganh;
    }

    public String getId_trinh_do() {
        return id_trinh_do;
    }

    public void setId_trinh_do(String id_trinh_do) {
        this.id_trinh_do = id_trinh_do;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("id_khoa", id_khoa);
        result.put("ten_chuyen_nganh", ten_chuyen_nganh);
        result.put("id_trinh_do", id_trinh_do);
        return result;
    }

    @Override
    public String toString() {
        return "Major{" +
                "id='" + id + '\'' +
                ", ten_chuyen_nganh='" + ten_chuyen_nganh + '\'' +
                ", id_khoa='" + id_khoa + '\'' +
                ", id_trinh_do='" + id_trinh_do + '\'' +
                '}';
    }
}
