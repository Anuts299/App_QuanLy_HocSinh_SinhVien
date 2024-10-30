package com.example.app_quanly_hocsinh_sinhvien.lecturer_manage;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.ClassFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.LevelFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.TeacherFragment;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class DetailFragment extends Fragment {

    TextView tv_de_name_lecturer, tv_de_name_faculty, tv_de_name_level, breadcrumb_lecturer, breadcrumb_home;
    FloatingActionButton deleteButtonLecturer, editButtonLecturer;
    String id = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_lecturer, container, false);

        initUi(view);
        initListener();
        Bundle bundle = getArguments();
        if(bundle != null){
            id = bundle.getString("id");
            tv_de_name_lecturer.setText(bundle.getString("ten_giang_vien"));
            tv_de_name_faculty.setText(bundle.getString("ten_khoa"));
        }
        return view;
    }
    private void initUi(View view){
        tv_de_name_lecturer = view.findViewById(R.id.tv_de_name_lecturer);
        tv_de_name_faculty = view.findViewById(R.id.tv_de_name_faculty);
        tv_de_name_level = view.findViewById(R.id.tv_de_name_level);
        breadcrumb_lecturer = view.findViewById(R.id.breadcrumb_lecturer);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        deleteButtonLecturer = view.findViewById(R.id.deleteButtonLecturer);
        editButtonLecturer = view.findViewById(R.id.editButtonLecturer);
    }
    private void initListener(){
        deleteButtonLecturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị SweetAlertDialog xác nhận xóa
                new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Xác nhận xóa")
                        .setContentText("Bạn có chắc chắn muốn xóa giảng viên này không?")
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
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("LECTURER");
                                reference.child(id) // Thay "id" bằng ID lớp học bạn muốn xóa
                                        .removeValue()
                                        .addOnCompleteListener(task -> {
                                            dialog.dismiss();
                                            if (task.isSuccessful()) {
                                                sDialog
                                                        .setTitleText("Đã xóa!")
                                                        .setContentText("Giảng viên đã được xóa.")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.dismiss();

                                                                switchFragment(new TeacherFragment());
                                                            }
                                                        })
                                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);


                                            } else {
                                                sDialog
                                                        .setTitleText("Lỗi!")
                                                        .setContentText("Không thể xóa giảng viên.")
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
        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new HomeFragment());
            }
        });
        breadcrumb_lecturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new LevelFragment());
            }
        });
    }
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();  // Không dùng addToBackStack(null)
    }
}