package com.example.app_quanly_hocsinh_sinhvien.input_score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputScore {
    private String id;
    private String id_khoa;
    private String id_lop;
    private String id_sinh_vien;
    private String id_mon_hoc;
    private String id_loai_diem; // Giữ lại để linh hoạt hơn

    // Các danh sách điểm
    private List<Float> diemKTTX = new ArrayList<>(); // Điểm kiểm tra thường xuyên
    private List<Float> diemKTDK = new ArrayList<>(); // Điểm kiểm tra định kỳ
    private List<Float> diemThi = new ArrayList<>();  // Điểm thi
    private List<Float> diemThi2 = new ArrayList<>(); // Điểm thi lần 2

    // Constructor không tham số
    public InputScore() {
    }

    // Constructor có tham số
    public InputScore(String id, String id_khoa, String id_lop, String id_sinh_vien, String id_mon_hoc, String id_loai_diem) {
        this.id = id;
        this.id_khoa = id_khoa;
        this.id_lop = id_lop;
        this.id_sinh_vien = id_sinh_vien;
        this.id_mon_hoc = id_mon_hoc;
        this.id_loai_diem = id_loai_diem;
        this.diemKTTX = new ArrayList<>();
        this.diemKTDK = new ArrayList<>();
        this.diemThi = new ArrayList<>();
        this.diemThi2 = new ArrayList<>();
    }

    // Getter và Setter cho các thuộc tính cơ bản
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

    public String getId_lop() {
        return id_lop;
    }

    public void setId_lop(String id_lop) {
        this.id_lop = id_lop;
    }

    public String getId_sinh_vien() {
        return id_sinh_vien;
    }

    public void setId_sinh_vien(String id_sinh_vien) {
        this.id_sinh_vien = id_sinh_vien;
    }

    public String getId_mon_hoc() {
        return id_mon_hoc;
    }

    public void setId_mon_hoc(String id_mon_hoc) {
        this.id_mon_hoc = id_mon_hoc;
    }

    public String getId_loai_diem() {
        return id_loai_diem;
    }

    public void setId_loai_diem(String id_loai_diem) {
        this.id_loai_diem = id_loai_diem;
    }

    // Getter và Setter cho danh sách điểm
    public List<Float> getDiemKTTX() {
        return diemKTTX;  // Trả về danh sách gốc
    }


    public void setDiemKTTX(List<Float> diemKTTX) {
        this.diemKTTX = diemKTTX == null ? new ArrayList<>() : new ArrayList<>(diemKTTX);
    }

    public List<Float> getDiemKTDK() {
        return diemKTDK;
    }

    public void setDiemKTDK(List<Float> diemKTDK) {
        this.diemKTDK = diemKTDK == null ? new ArrayList<>() : new ArrayList<>(diemKTDK);
    }

    public List<Float> getDiemThi() {
        return diemThi;
    }

    public void setDiemThi(List<Float> diemThi) {
        this.diemThi = diemThi == null ? new ArrayList<>() : new ArrayList<>(diemThi);
    }

    public List<Float> getDiemThi2() {
        return diemThi2;
    }

    public void setDiemThi2(List<Float> diemThi2) {
        this.diemThi2 = diemThi2 == null ? new ArrayList<>() : new ArrayList<>(diemThi2);
    }

    // Chuyển đối tượng thành Map
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("id_khoa", id_khoa);
        result.put("id_lop", id_lop);
        result.put("id_sinh_vien", id_sinh_vien);
        result.put("id_mon_hoc", id_mon_hoc);
        result.put("id_loai_diem", id_loai_diem);
        result.put("diemKTTX", new ArrayList<>(diemKTTX));
        result.put("diemKTDK", new ArrayList<>(diemKTDK));
        result.put("diemThi", new ArrayList<>(diemThi));
        result.put("diemThi2", new ArrayList<>(diemThi2));

        return result;
    }

    // Tạo chuỗi đại diện cho đối tượng
    @Override
    public String toString() {
        return "InputScore{" +
                "id='" + id + '\'' +
                ", id_khoa='" + id_khoa + '\'' +
                ", id_lop='" + id_lop + '\'' +
                ", id_sinh_vien='" + id_sinh_vien + '\'' +
                ", id_mon_hoc='" + id_mon_hoc + '\'' +
                ", id_loai_diem='" + id_loai_diem + '\'' +
                ", diemKTTX=" + diemKTTX +
                ", diemKTDK=" + diemKTDK +
                ", diemThi=" + diemThi +
                ", diemThi2=" + diemThi2 +
                '}';
    }
}
