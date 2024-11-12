package com.example.app_quanly_hocsinh_sinhvien.faculty_manage;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Faculty {
    String id;
    String ten_khoa;
    String ma_dinh_dang;

    public Faculty() {
    }

    public Faculty(String id, String ten_khoa, String ma_dinh_dang) {
        this.id = id;
        this.ten_khoa = ten_khoa;
        this.ma_dinh_dang = ma_dinh_dang;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTen_khoa() {
        return ten_khoa;
    }

    public void setTen_khoa(String ten_khoa) {
        this.ten_khoa = ten_khoa;
    }

    public String getMa_dinh_dang() {
        return ma_dinh_dang;
    }

    public void setMa_dinh_dang(String ma_dinh_dang) {
        this.ma_dinh_dang = ma_dinh_dang;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("ten_khoa", ten_khoa);
        result.put("ma_dinh_dang", ma_dinh_dang);
        return result;
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id='" + id + '\'' +
                ", ten_khoa='" + ten_khoa + '\'' +
                ", ma_dinh_dang='" + ma_dinh_dang + '\'' +
                '}';
    }
}
