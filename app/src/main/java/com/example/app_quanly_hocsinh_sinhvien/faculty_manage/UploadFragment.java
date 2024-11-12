package com.example.app_quanly_hocsinh_sinhvien.faculty_manage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.FacultiesFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UploadFragment extends Fragment {
    private TextView breadcrumb_home, breadcrumb_faculty;
    private EditText edt_name_faculty, edt_format_code;
    private Button btn_upload_faculty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_faculty, container, false);
        initUi(view);
        iniListener();
        return view;
    }
    private void initUi(View view){
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_faculty = view.findViewById(R.id.breadcrumb_faculty);
        edt_name_faculty = view.findViewById(R.id.edt_name_faculty);
        edt_format_code = view.findViewById(R.id.edt_format_code);
        btn_upload_faculty = view.findViewById(R.id.btn_upload_faculty);
    }
    private void iniListener(){
        btn_upload_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name_faculty = edt_name_faculty.getText().toString().trim();
                String format_code = edt_format_code.getText().toString().trim();
                if(name_faculty.isEmpty() || format_code.isEmpty()){
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Thiếu thông tin")
                            .setContentText("Vui lòng điền đầy đủ thông tin.")
                            .setConfirmText("OK")
                            .show();
                    return; // Dừng lại nếu có trường nào đó rỗng
                }
                Faculty faculty = new Faculty(null, name_faculty, format_code);
                onClickUploadFaculty(faculty);
            }
        });
        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new HomeFragment());
            }
        });
        breadcrumb_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new FacultiesFragment());
            }
        });
    }
    private void onClickUploadFaculty(Faculty faculty){
        FacultiesFragment facultiesFragment = new FacultiesFragment();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("FACULTY");
        Query checkFaculty = myRef.orderByChild("ten_khoa").equalTo(faculty.getTen_khoa());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        checkFaculty.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialog.dismiss();
                if(snapshot.exists()){
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Khoa đã tồn tại")
                            .setContentText("Tên khoa này đã có trong hệ thống.")
                            .setConfirmText("OK")
                            .show();
                }else{
                    String key = myRef.push().getKey();
                    faculty.setId(key);
                    myRef.child(key).setValue(faculty, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if(error != null){
                                new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Thêm khoa thất bại")
                                        .setContentText("Lỗi: " + error.getMessage())
                                        .setConfirmText("OK")
                                        .show();
                                switchFragment(facultiesFragment);
                            }else {
                                new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Thêm khoa thành công")
                                        .setConfirmText("OK")
                                        .show();
                                switchFragment(facultiesFragment);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Thêm khoa thất bại")
                        .setContentText("Lỗi: " + error.getMessage())
                        .setConfirmText("OK")
                        .show();
            }
        });
    }
    // Phương thức chuyển đổi giữa các fragment
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();  // Không dùng addToBackStack(null)
    }

}