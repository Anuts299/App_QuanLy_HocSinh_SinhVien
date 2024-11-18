package com.example.app_quanly_hocsinh_sinhvien.input_score;

import java.util.HashMap;
import java.util.Map;

public class InputScore {
    private String id;
    private String id_khoa;
    private String id_lop;
    private String id_sinh_vien;
    private String id_mon_hoc;
    private String id_loai_diem;
    private float so_diem;

    public InputScore() {
    }

    public InputScore(String id, String id_khoa, String id_loai_diem, String id_lop, String id_mon_hoc, String id_sinh_vien, float so_diem) {
        this.id = id;
        this.id_khoa = id_khoa;
        this.id_loai_diem = id_loai_diem;
        this.id_lop = id_lop;
        this.id_mon_hoc = id_mon_hoc;
        this.id_sinh_vien = id_sinh_vien;
        this.so_diem = so_diem;
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

    public String getId_loai_diem() {
        return id_loai_diem;
    }

    public void setId_loai_diem(String id_loai_diem) {
        this.id_loai_diem = id_loai_diem;
    }

    public String getId_lop() {
        return id_lop;
    }

    public void setId_lop(String id_lop) {
        this.id_lop = id_lop;
    }

    public String getId_mon_hoc() {
        return id_mon_hoc;
    }

    public void setId_mon_hoc(String id_mon_hoc) {
        this.id_mon_hoc = id_mon_hoc;
    }

    public String getId_sinh_vien() {
        return id_sinh_vien;
    }

    public void setId_sinh_vien(String id_sinh_vien) {
        this.id_sinh_vien = id_sinh_vien;
    }

    public float getSo_diem() {
        return so_diem;
    }

    public void setSo_diem(float so_diem) {
        this.so_diem = so_diem;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("id_khoa",id_khoa);
        result.put("id_lop",id_lop);
        result.put("id_sinh_vien",id_sinh_vien);
        result.put("id_mon_hoc",id_mon_hoc);
        result.put("id_loai_diem",id_loai_diem);
        result.put("so_diem",so_diem);

        return result;
    }

    @Override
    public String toString() {
        return "InputScore{" +
                "id='" + id + '\'' +
                ", id_khoa='" + id_khoa + '\'' +
                ", id_lop='" + id_lop + '\'' +
                ", id_sinh_vien='" + id_sinh_vien + '\'' +
                ", id_mon_hoc='" + id_mon_hoc + '\'' +
                ", id_loai_diem='" + id_loai_diem + '\'' +
                ", so_diem=" + so_diem +
                '}';
    }
}
