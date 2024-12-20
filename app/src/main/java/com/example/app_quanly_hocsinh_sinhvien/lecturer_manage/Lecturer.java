package com.example.app_quanly_hocsinh_sinhvien.lecturer_manage;

import java.util.HashMap;
import java.util.Map;

public class Lecturer {
    private String id;
    private String ten_giang_vien;
    private String id_khoa;
    private String id_trinh_do;

    public Lecturer() {
    }

    public Lecturer(String id, String ten_giang_vien, String id_khoa, String id_trinh_do) {
        this.id = id;
        this.ten_giang_vien = ten_giang_vien;
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

    public String getTen_giang_vien() {
        return ten_giang_vien;
    }

    public void setTen_giang_vien(String ten_giang_vien) {
        this.ten_giang_vien = ten_giang_vien;
    }

    public String getId_trinh_do() {
        return id_trinh_do;
    }

    public void setId_trinh_do(String id_trinh_do) {
        this.id_trinh_do = id_trinh_do;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("ten_giang_vien", ten_giang_vien);
        result.put("id_khoa", id_khoa);
        result.put("id_trinh_do", id_trinh_do);
        return result;
    }

    @Override
    public String toString() {
        return "Lecturer{" +
                "id='" + id + '\'' +
                ", ten_giang_vien='" + ten_giang_vien + '\'' +
                ", id_khoa='" + id_khoa + '\'' +
                ", id_trinh_do='" + id_trinh_do + '\'' +
                '}';
    }
}
