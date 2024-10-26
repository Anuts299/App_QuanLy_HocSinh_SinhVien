package com.example.app_quanly_hocsinh_sinhvien.class_manage;

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

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.ClassFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UploadFragment extends Fragment {
    Button btn_upload_class;
    EditText edt_name_class, edt_name_faculty, edt_name_lecturer, edt_academic_year, edt_code_class;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_class, container, false);
        initUi(view);

        btn_upload_class.setOnClickListener(v -> {
            String class_code = edt_code_class.getText().toString().trim();
            String name_class = edt_name_class.getText().toString().trim();
            String name_faculty = edt_name_faculty.getText().toString().trim();
            String name_lecturer = edt_name_lecturer.getText().toString().trim();
            String academic_year = edt_academic_year.getText().toString().trim();
            if (class_code.isEmpty() || name_class.isEmpty() || name_faculty.isEmpty() || name_lecturer.isEmpty() || academic_year.isEmpty()) {
                new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Thiếu thông tin")
                        .setContentText("Vui lòng điền đầy đủ thông tin.")
                        .setConfirmText("OK")
                        .show();
                return; // Dừng lại nếu có trường nào đó rỗng
            }
            Classroom classroom = new Classroom(null,class_code, academic_year, name_lecturer, name_faculty, name_class);
            onClickUploadClass(classroom);
        });

        return view;
    }
    private void initUi(View view){
        btn_upload_class = view.findViewById(R.id.btn_upload_class);
        edt_name_class = view.findViewById(R.id.edt_name_class);
        edt_name_faculty = view.findViewById(R.id.edt_name_faculty);
        edt_name_lecturer = view.findViewById(R.id.edt_name_lecturer);
        edt_academic_year = view.findViewById(R.id.edt_academic_year);
        edt_code_class = view.findViewById(R.id.edt_code_class);
    }

    private void onClickUploadClass(Classroom classroom) {
        ClassFragment classFragment = new ClassFragment();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("CLASSROOM");
        Query checkClass = myRef.orderByChild("ma_lop").equalTo(classroom.getMa_lop());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        checkClass.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.dismiss(); // Đảm bảo dialog luôn được đóng
                if (dataSnapshot.exists()) {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Lớp đã tồn tại")
                            .setContentText("Mã lớp này đã có trong hệ thống.")
                            .setConfirmText("OK")
                            .show();
                } else {
                    // Sử dụng push() để Firebase tự tạo ID
                    String key = myRef.push().getKey();
                    classroom.setId(key);
                    myRef.child(key).setValue(classroom, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Thêm lớp thất bại")
                                        .setContentText("Lỗi: " + error.getMessage())
                                        .setConfirmText("OK")
                                        .show();
                                switchFragment(classFragment);
                            } else {
                                new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Thêm lớp thành công")
                                        .setConfirmText("OK")
                                        .show();
                                switchFragment(classFragment);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
                new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Thêm lớp thất bại")
                        .setContentText("Lỗi: " + databaseError.getMessage())
                        .setConfirmText("OK")
                        .show();
            }
        });
    }
    // Phương thức chuyển đổi giữa các fragment
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
