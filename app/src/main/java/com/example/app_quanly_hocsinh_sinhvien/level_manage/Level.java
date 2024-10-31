package com.example.app_quanly_hocsinh_sinhvien.level_manage;

import java.util.HashMap;
import java.util.Map;

public class Level {
    private String id;
    private String ten_trinh_do;

    public Level() {
    }

    public Level(String id, String ten_trinh_do) {
        this.id = id;
        this.ten_trinh_do = ten_trinh_do;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTen_trinh_do() {
        return ten_trinh_do;
    }

    public void setTen_trinh_do(String ten_trinh_do) {
        this.ten_trinh_do = ten_trinh_do;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("ten_trinh_do", ten_trinh_do);
        return result;
    }

    @Override
    public String toString() {
        return "Level{" +
                "id='" + id + '\'' +
                ", ten_trinh_do='" + ten_trinh_do + '\'' +
                '}';
    }
}
