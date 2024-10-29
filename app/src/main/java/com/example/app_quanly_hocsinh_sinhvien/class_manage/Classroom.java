package com.example.app_quanly_hocsinh_sinhvien.class_manage;

import java.util.HashMap;
import java.util.Map;

public class Classroom {
    private String id; // Đổi int thành String
    private String ma_lop;
    private String ten_lop;
    private String nam_hoc;
    private String ten_co_van;
    private String id_khoa;

    public Classroom() {
    }

    public Classroom(String id, String ma_lop, String nam_hoc, String ten_co_van, String ten_lop, String id_khoa) {
        this.id = id;
        this.ma_lop = ma_lop;
        this.nam_hoc = nam_hoc;
        this.ten_co_van = ten_co_van;
        this.ten_lop = ten_lop;
        this.id_khoa = id_khoa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMa_lop() {
        return ma_lop;
    }

    public void setMa_lop(String ma_lop) {
        this.ma_lop = ma_lop;
    }

    public String getTen_lop() {
        return ten_lop;
    }

    public void setTen_lop(String ten_lop) {
        this.ten_lop = ten_lop;
    }

    public String getNam_hoc() {
        return nam_hoc;
    }

    public void setNam_hoc(String nam_hoc) {
        this.nam_hoc = nam_hoc;
    }

    public String getTen_co_van() {
        return ten_co_van;
    }

    public void setTen_co_van(String ten_co_van) {
        this.ten_co_van = ten_co_van;
    }


    public String getId_khoa() {
        return id_khoa;
    }

    public void setId_khoa(String id_khoa) {
        this.id_khoa = id_khoa;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("ma_lop", ma_lop);
        result.put("ten_lop", ten_lop);
        result.put("nam_hoc", nam_hoc);
        result.put("ten_co_van", ten_co_van);
        result.put("id_khoa", id_khoa);
        return result;
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "id='" + id + '\'' +
                ", ma_lop='" + ma_lop + '\'' +
                ", ten_lop='" + ten_lop + '\'' +
                ", nam_hoc='" + nam_hoc + '\'' +
                ", ten_co_van='" + ten_co_van + '\'' +
                ", id_khoa='" + id_khoa + '\'' +
                '}';
    }
}

