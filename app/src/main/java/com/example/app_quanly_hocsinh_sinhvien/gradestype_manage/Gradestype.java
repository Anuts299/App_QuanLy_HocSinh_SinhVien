package com.example.app_quanly_hocsinh_sinhvien.gradestype_manage;

import java.util.HashMap;
import java.util.Map;

public class Gradestype {
    private String id;
    private String ten_loai_diem;
    private int he_so;

    public Gradestype() {
    }

    public Gradestype( String id, String ten_loai_diem, int he_so) {
        this.he_so = he_so;
        this.id = id;
        this.ten_loai_diem = ten_loai_diem;
    }

    public int getHe_so() {
        return he_so;
    }

    public void setHe_so(int he_so) {
        this.he_so = he_so;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTen_loai_diem() {
        return ten_loai_diem;
    }

    public void setTen_loai_diem(String ten_loai_diem) {
        this.ten_loai_diem = ten_loai_diem;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("ten_loai_diem", ten_loai_diem);
        result.put("he_so", he_so);
        return result;
    }

    @Override
    public String toString() {
        return "Gradestype{" +
                "he_so=" + he_so +
                ", id='" + id + '\'' +
                ", ten_loai_diem='" + ten_loai_diem + '\'' +
                '}';
    }
}
