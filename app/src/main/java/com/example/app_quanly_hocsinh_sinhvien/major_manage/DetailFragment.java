package com.example.app_quanly_hocsinh_sinhvien.major_manage;

import android.content.Context;
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

import com.example.app_quanly_hocsinh_sinhvien.FragmentActionListener;
import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.ClassFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.MajorFragment;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class DetailFragment extends Fragment {

    private TextView breadcrumb_home, breadcrumb_major, tv_de_name_major, tv_de_name_faculties, tv_de_name_level;
    private FloatingActionButton deleteButtonMajor, editButtonMajor;
    String id = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_major, container, false);
        initUi(view);
        Bundle bundle = getArguments();
        if(bundle != null){
            tv_de_name_major.setText(bundle.getString("ten_chuyen_nganh"));
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

            String id_trinh_do = bundle.getString("id_trinh_do");
            // Gọi phương thức để lấy tên trình độ từ id_trinh_do
            getLevelNameById(id_trinh_do, new LevelCallback() {
                @Override
                public void onCallback(String ten_trinh_do) {
                    if (ten_trinh_do != null) {
                        tv_de_name_level.setText(ten_trinh_do);
                    } else {
                        tv_de_name_level.setText("Không tìm thấy tên trình độ");
                    }
                }
            });
            id = bundle.getString("id");
        }
        initListener();
        return view;
    }

    private void initUi(View view){
        tv_de_name_major = view.findViewById(R.id.tv_de_name_major);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_major = view.findViewById(R.id.breadcrumb_major);
        tv_de_name_faculties = view.findViewById(R.id.tv_de_name_faculties);
        tv_de_name_level = view.findViewById(R.id.tv_de_name_level);
        deleteButtonMajor = view.findViewById(R.id.deleteButtonMajor);
        editButtonMajor = view.findViewById(R.id.editButtonMajor);
    }

    private void initListener(){
        breadcrumb_major.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new MajorFragment());
            }
        });
        deleteButtonMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị SweetAlertDialog xác nhận xóa
                new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Xác nhận xóa")
                        .setContentText("Bạn có chắc chắn muốn xóa chuyên ngành này không?")
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
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MAJOR");
                                reference.child(id)
                                        .removeValue()
                                        .addOnCompleteListener(task -> {
                                            dialog.dismiss();
                                            if (task.isSuccessful()) {
                                                sDialog
                                                        .setTitleText("Đã xóa!")
                                                        .setContentText("Chuyên ngành đã được xóa.")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.dismiss();
                                                                switchFragment(new MajorFragment());
                                                            }
                                                        })
                                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);


                                            } else {
                                                sDialog
                                                        .setTitleText("Lỗi!")
                                                        .setContentText("Không thể xóa chuyên ngành.")
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
        editButtonMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                String tenCN = tv_de_name_major.getText().toString().trim();
                String tenKhoa = tv_de_name_faculties.getText().toString().trim();
                String tenTD = tv_de_name_level.getText().toString().trim();

                bundle.putString("ten_CN", tenCN);
                bundle.putString("ten_khoa", tenKhoa);
                bundle.putString("ten_trinh_do", tenTD);
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
    }
    private void getFacultyNameById(String id_khoa, FacultyCallback callback) {
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
    private interface FacultyCallback {
        void onCallback(String ten_khoa);
    }

    private void getLevelNameById(String id_trinh_do, LevelCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("LEVEL");
        databaseReference.child(id_trinh_do).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String ten_trinh_do = dataSnapshot.child("ten_trinh_do").getValue(String.class);
                    callback.onCallback(ten_trinh_do);
                } else {
                    callback.onCallback(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onCallback(null); // Xử lý lỗi
            }
        });
    }

    // Callback interface
    private interface LevelCallback {
        void onCallback(String ten_trinh_do);
    }
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}