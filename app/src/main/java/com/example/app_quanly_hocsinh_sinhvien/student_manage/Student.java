package com.example.app_quanly_hocsinh_sinhvien.student_manage;

import java.util.HashMap;
import java.util.Map;

public class Student {
    private String id;
    private String ten_sinh_vien;
    private String ngay_sinh;  // Chuyển từ LocalDate sang String
    private String gioi_tinh;
    private String dia_chi;
    private String so_dien_thoai;
    private String email;
    private String ngay_nhap_hoc;  // Chuyển từ LocalDate sang String
    private String hinh_anh;
    private String id_lop;
    private String id_trinh_do;
    private String he_dao_tao;

    // Constructor không tham số
    public Student() {
    }

    // Constructor đầy đủ
    public Student(String id, String ten_sinh_vien, String ngay_sinh, String gioi_tinh, String dia_chi,
                   String so_dien_thoai, String email, String ngay_nhap_hoc, String hinh_anh,
                   String id_lop, String id_trinh_do, String he_dao_tao) {
        this.id = id;
        this.ten_sinh_vien = ten_sinh_vien;
        this.ngay_sinh = ngay_sinh;  // Lưu ngày sinh dưới dạng String
        this.gioi_tinh = gioi_tinh;
        this.dia_chi = dia_chi;
        this.so_dien_thoai = so_dien_thoai;
        this.email = email;
        this.ngay_nhap_hoc = ngay_nhap_hoc;  // Lưu ngày nhập học dưới dạng String
        this.hinh_anh = hinh_anh;
        this.id_lop = id_lop;
        this.id_trinh_do = id_trinh_do;
        this.he_dao_tao = he_dao_tao;
    }

    public String getHe_dao_tao() {
        return he_dao_tao;
    }

    public void setHe_dao_tao(String he_dao_tao) {
        this.he_dao_tao = he_dao_tao;
    }

    // Getters và Setters
    public String getDia_chi() {
        return dia_chi;
    }

    public void setDia_chi(String dia_chi) {
        this.dia_chi = dia_chi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHinh_anh() {
        return hinh_anh;
    }

    public void setHinh_anh(String hinh_anh) {
        this.hinh_anh = hinh_anh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_lop() {
        return id_lop;
    }

    public String getGioi_tinh() {
        return gioi_tinh;
    }

    public void setGioi_tinh(String gioi_tinh) {
        this.gioi_tinh = gioi_tinh;
    }

    public void setId_lop(String id_lop) {
        this.id_lop = id_lop;
    }

    public String getId_trinh_do() {
        return id_trinh_do;
    }

    public void setId_trinh_do(String id_trinh_do) {
        this.id_trinh_do = id_trinh_do;
    }

    // Sử dụng String cho ngày sinh và ngày nhập học
    public String getNgay_nhap_hoc() {
        return ngay_nhap_hoc;
    }

    public void setNgay_nhap_hoc(String ngay_nhap_hoc) {
        this.ngay_nhap_hoc = ngay_nhap_hoc;
    }

    public String getNgay_sinh() {
        return ngay_sinh;
    }

    public void setNgay_sinh(String ngay_sinh) {
        this.ngay_sinh = ngay_sinh;
    }

    public String getSo_dien_thoai() {
        return so_dien_thoai;
    }

    public void setSo_dien_thoai(String so_dien_thoai) {
        this.so_dien_thoai = so_dien_thoai;
    }

    public String getTen_sinh_vien() {
        return ten_sinh_vien;
    }

    public void setTen_sinh_vien(String ten_sinh_vien) {
        this.ten_sinh_vien = ten_sinh_vien;
    }

    // Chuyển đổi đối tượng thành Map
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("ten_sinh_vien", ten_sinh_vien);
        result.put("ngay_sinh", ngay_sinh);  // Lưu dưới dạng String
        result.put("gioi_tinh", gioi_tinh);
        result.put("dia_chi", dia_chi);
        result.put("so_dien_thoai", so_dien_thoai);
        result.put("email", email);
        result.put("ngay_nhap_hoc", ngay_nhap_hoc);  // Lưu dưới dạng String
        result.put("hinh_anh", hinh_anh);
        result.put("id_lop", id_lop);
        result.put("id_trinh_do", id_trinh_do);
        result.put("he_dao_tao", he_dao_tao);
        return result;
    }

    @Override
    public String toString() {
        return "Student{" +
                "dia_chi='" + dia_chi + '\'' +
                ", id='" + id + '\'' +
                ", ten_sinh_vien='" + ten_sinh_vien + '\'' +
                ", ngay_sinh='" + ngay_sinh + '\'' +
                ", gioi_tinh='" + gioi_tinh + '\'' +
                ", so_dien_thoai='" + so_dien_thoai + '\'' +
                ", email='" + email + '\'' +
                ", ngay_nhap_hoc='" + ngay_nhap_hoc + '\'' +
                ", hinh_anh='" + hinh_anh + '\'' +
                ", id_lop='" + id_lop + '\'' +
                ", id_trinh_do='" + id_trinh_do + '\'' +
                ", he_dao_tao='" + he_dao_tao + '\'' +
                '}';
    }
}
