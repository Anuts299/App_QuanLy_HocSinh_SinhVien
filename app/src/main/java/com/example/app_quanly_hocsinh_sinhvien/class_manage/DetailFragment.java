package com.example.app_quanly_hocsinh_sinhvien.class_manage;

import static android.content.Intent.getIntent;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.ClassFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class DetailFragment extends Fragment {

    TextView tv_de_name_class, tv_de_name_faculties, tv_de_name_lecturer, tv_de_academic_year, tv_de_name_class2, breadcrumb_classroom, breadcrumb_home;
    FloatingActionButton deleteButtonClass, editButtonClass;
    String id = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_class, container, false);
        initUi(view);
        Bundle bundle = getArguments();
        if(bundle != null){
            tv_de_name_class.setText(bundle.getString("ma_lop"));
            String id_khoa = bundle.getString("id_khoa");
            // Gọi phương thức để lấy tên khoa từ id_khoa
            getFacultyNameById(id_khoa, new FacultyCallback() {
                @Override
                public void onCallback(String ten_khoa) {
                    if (ten_khoa != null) {
                        tv_de_name_faculties.setText(ten_khoa);
                    } else {
                        tv_de_name_faculties.setText("Không tìm thấy tên khoa");
                    }
                }
            });
            tv_de_name_lecturer.setText(bundle.getString("ten_co_van"));
            tv_de_academic_year.setText(bundle.getString("nam_hoc"));
            tv_de_name_class2.setText(bundle.getString("ten_lop"));
            id = bundle.getString("id");
        }
        initListener();
        return view;

    }
    private void initUi(View view){
        tv_de_name_class = view.findViewById(R.id.tv_de_name_class);
        tv_de_name_faculties = view.findViewById(R.id.tv_de_name_faculties);
        tv_de_name_lecturer = view.findViewById(R.id.tv_de_name_lecturer);
        tv_de_academic_year = view.findViewById(R.id.tv_de_academic_year);
        tv_de_name_class2 = view.findViewById(R.id.tv_de_name_class2);
        deleteButtonClass = view.findViewById(R.id.deleteButtonClass);
        editButtonClass = view.findViewById(R.id.editButtonClass);
        breadcrumb_classroom = view.findViewById(R.id.breadcrumb_classroom);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
    }
    private void initListener(){
        deleteButtonClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị SweetAlertDialog xác nhận xóa
                new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Xác nhận xóa")
                        .setContentText("Bạn có chắc chắn muốn xóa lớp học này không?")
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
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CLASSROOM");
                                reference.child(id) // Thay "id_classroom" bằng ID lớp học bạn muốn xóa
                                        .removeValue()
                                        .addOnCompleteListener(task -> {
                                            dialog.dismiss();
                                            if (task.isSuccessful()) {
                                                sDialog
                                                        .setTitleText("Đã xóa!")
                                                        .setContentText("Lớp học đã được xóa.")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.dismiss();

                                                                switchFragment(new ClassFragment());
                                                            }
                                                        })
                                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);


                                            } else {
                                                sDialog
                                                        .setTitleText("Lỗi!")
                                                        .setContentText("Không thể xóa lớp học.")
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
        editButtonClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                String maLop = tv_de_name_class.getText().toString().trim();
                String tenLop = tv_de_name_class2.getText().toString().trim();
                String tenKhoa = tv_de_name_faculties.getText().toString().trim();
                String tenCoVan = tv_de_name_lecturer.getText().toString().trim();
                String namHoc = tv_de_academic_year.getText().toString().trim();

                bundle.putString("ma_lop", maLop);
                bundle.putString("ten_lop", tenLop);
                bundle.putString("ten_khoa", tenKhoa);
                bundle.putString("ten_co_van", tenCoVan);
                bundle.putString("nam_hoc", namHoc);
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
        breadcrumb_classroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new ClassFragment());
            }
        });
    }
    public void getFacultyNameById(String id_khoa, final FacultyCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FACULTY");
        databaseReference.child(id_khoa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String ten_khoa = dataSnapshot.child("ten_khoa").getValue(String.class);
                    callback.onCallback(ten_khoa);
                } else {
                    callback.onCallback(null); // Khoa không tồn tại
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onCallback(null); // Xử lý lỗi
            }
        });
    }

    // Callback interface
    public interface FacultyCallback {
        void onCallback(String ten_khoa);
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();  // Không dùng addToBackStack(null)
    }

}