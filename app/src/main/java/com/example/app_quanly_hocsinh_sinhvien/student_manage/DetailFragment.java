package com.example.app_quanly_hocsinh_sinhvien.student_manage;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.ClassFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.StudentFragment;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class DetailFragment extends Fragment {

    TextView breadcrumb_home, breadcrumb_student, tv_de_name_student, tv_de_code_student, tv_de_code_class, tv_de_birthday, tv_de_gender_student,
            tv_de_locate_student, tv_de_phonenumber_student, tv_de_email_student, tv_de_admissiondate_student, tv_de_name_level;
    ImageView image_de_student;
    FloatingActionButton editButtonStudent, deleteButtonStudent;
    String id = "", hinh_anh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_student, container, false);
        initUi(view);
        Bundle bundle = getArguments();
        if(bundle != null){
            tv_de_name_student.setText(bundle.getString("ten_sinh_vien"));
            tv_de_code_student.setText(bundle.getString("ma_sinh_vien"));
            tv_de_code_class.setText(bundle.getString("ma_lop"));
            tv_de_birthday.setText(bundle.getString("ngay_sinh"));
            tv_de_gender_student.setText(bundle.getString("gioi_tinh"));
            tv_de_locate_student.setText(bundle.getString("dia_chi"));
            tv_de_phonenumber_student.setText(bundle.getString("SDT"));
            tv_de_email_student.setText(bundle.getString("email"));
            tv_de_admissiondate_student.setText(bundle.getString("ngay_nhap_hoc"));
            tv_de_name_level.setText(bundle.getString("trinh_do"));
            hinh_anh = bundle.getString("hinh_anh");
            Glide.with(getActivity()).load(bundle.getString("hinh_anh")).into(image_de_student);
            id = bundle.getString("id");
        }
        initListener();
        return view;
    }

    private void initUi(View view){
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_student = view.findViewById(R.id.breadcrumb_student);
        tv_de_name_student = view.findViewById(R.id.tv_de_name_student);
        tv_de_code_student = view.findViewById(R.id.tv_de_code_student);
        tv_de_code_class = view.findViewById(R.id.tv_de_code_class);
        tv_de_birthday = view.findViewById(R.id.tv_de_birthday);
        tv_de_gender_student = view.findViewById(R.id.tv_de_gender_student);
        tv_de_locate_student = view.findViewById(R.id.tv_de_locate_student);
        tv_de_phonenumber_student = view.findViewById(R.id.tv_de_phonenumber_student);
        tv_de_email_student = view.findViewById(R.id.tv_de_email_student);
        tv_de_admissiondate_student = view.findViewById(R.id.tv_de_admissiondate_student);
        tv_de_name_level = view.findViewById(R.id.tv_de_name_level);
        editButtonStudent = view.findViewById(R.id.editButtonStudent);
        deleteButtonStudent = view.findViewById(R.id.deleteButtonStudent);
        image_de_student = view.findViewById(R.id.image_de_student);
    }

    private void initListener(){
        deleteButtonStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị SweetAlertDialog xác nhận xóa
                new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Xác nhận xóa")
                        .setContentText("Bạn có chắc chắn muốn xóa sinh viên này không?")
                        .setConfirmText("Xóa")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                builder.setCancelable(false);
                                builder.setView(R.layout.progress_layout);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                // Xác nhận xóa - thực hiện xóa trong Firebase
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("STUDENT");
                                reference.child(id)
                                        .removeValue()
                                        .addOnCompleteListener(task -> {
                                            dialog.dismiss();
                                            if (task.isSuccessful()) {
                                                sDialog
                                                        .setTitleText("Đã xóa!")
                                                        .setContentText("Sinh viên đã được xóa.")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.dismiss();

                                                                switchFragment(new StudentFragment());
                                                            }
                                                        })
                                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);


                                            } else {
                                                sDialog
                                                        .setTitleText("Lỗi!")
                                                        .setContentText("Không thể xóa sinh viên.")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                            }
                                        });
                            }
                        })
                        .setCancelButton("Hủy", SweetAlertDialog::dismiss)
                        .show();
            }
        });
        editButtonStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                String tenSV = tv_de_name_student.getText().toString().trim();
                String maSV = tv_de_code_student.getText().toString().trim();
                String maLop = tv_de_code_class.getText().toString().trim();
                String ngaySinh = tv_de_birthday.getText().toString().trim();
                String gioiTinh = tv_de_gender_student.getText().toString().trim();
                String diaChi = tv_de_locate_student.getText().toString().trim();
                String SDT = tv_de_phonenumber_student.getText().toString().trim();
                String email = tv_de_email_student.getText().toString().trim();
                String ngayNhapHoc = tv_de_admissiondate_student.getText().toString().trim();
                String trinhDo = tv_de_name_level.getText().toString().trim();

                bundle.putString("ten_sinh_vien", tenSV);
                bundle.putString("ma_sinh_vien", maSV);
                bundle.putString("ma_lop", maLop);
                bundle.putString("ngay_sinh", ngaySinh);
                bundle.putString("gioi_tinh", gioiTinh);
                bundle.putString("dia_chi", diaChi);
                bundle.putString("SDT", SDT);
                bundle.putString("email", email);
                bundle.putString("ngay_nhap_hoc", ngayNhapHoc);
                bundle.putString("trinh_do", trinhDo);
                bundle.putString("hinh_anh", hinh_anh);
                bundle.putString("id", id);

                UpdateFragment updateFragment = new UpdateFragment();
                updateFragment.setArguments(bundle);

                try {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, updateFragment)
                            .addToBackStack(null)
                            .commit();
                    Log.d("EditButton", "Fragment replaced successfully");
                } catch (Exception e) {
                    Log.e("EditButton", "Error replacing fragment", e);
                }
            }
        });
        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new HomeFragment());
            }
        });

        breadcrumb_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new StudentFragment());
            }
        });
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
//        fragmentTransaction.addToBackStack(null); // Thêm dòng này để fragment vào back stack
        fragmentTransaction.commit();
    }


}