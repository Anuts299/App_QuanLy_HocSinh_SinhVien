package com.example.app_quanly_hocsinh_sinhvien.faculty_manage;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Faculty {
    String id;
    String ten_khoa;

    public Faculty() {
    }

    public Faculty(String id, String ten_khoa) {
        this.id = id;
        this.ten_khoa = ten_khoa;
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

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("ten_khoa", ten_khoa);
        return result;
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id='" + id + '\'' +
                ", ten_khoa='" + ten_khoa + '\'' +
                '}';
    }
}
