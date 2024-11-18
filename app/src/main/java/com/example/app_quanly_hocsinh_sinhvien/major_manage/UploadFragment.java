package com.example.app_quanly_hocsinh_sinhvien.major_manage;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.FragmentActionListener;
import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.ClassFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.MajorFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UploadFragment extends Fragment {

    private TextView breadcrumb_home, breadcrumb_major;
    private EditText edt_upload_name_major;
    private Spinner spinner_name_faculty, spinner_name_level;
    private Button btn_upload_major;

    //Adapter và danh sách cho Spinner
    private ArrayAdapter<String> facultyAdapter;
    private ArrayList<String> facultyList;
    private Map<String, String> facultyMap = new HashMap<>();

    private ArrayAdapter<String> levelAdapter;
    private ArrayList<String> levelList;
    private Map<String, String> levelMap = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_major, container, false);
        initUi(view);
        facultyList = new ArrayList<>();
        levelList = new ArrayList<>();
        loadFacultyList();
        loadLevelList();
        initLister();
        return view;
    }

    private void initUi(View view){
        breadcrumb_major = view.findViewById(R.id.breadcrumb_major);
        edt_upload_name_major = view.findViewById(R.id.edt_upload_name_major);
        spinner_name_faculty = view.findViewById(R.id.spinner_name_faculty);
        spinner_name_level = view.findViewById(R.id.spinner_name_level);
        btn_upload_major = view.findViewById(R.id.btn_upload_major);
    }

    private void initLister(){

        breadcrumb_major.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new MajorFragment());
            }
        });
        btn_upload_major.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name_major = edt_upload_name_major.getText().toString().trim();
                String name_faculty = spinner_name_faculty.getSelectedItem().toString().trim();
                String id_faculty = facultyMap.get(name_faculty);
                String name_level = spinner_name_level.getSelectedItem().toString().trim();
                String id_level = levelMap.get(name_level);
                if (name_major.isEmpty() || id_level.isEmpty() || id_faculty == null || id_faculty.isEmpty() || id_level == null) {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Thiếu thông tin")
                            .setContentText("Vui lòng điền đầy đủ thông tin.")
                            .setConfirmText("OK")
                            .show();
                    return; // Dừng lại nếu có trường nào đó rỗng
                }
                Major major = new Major(null, name_major, id_faculty, id_level);
                onClickUploadMajor(major);
            }
        });
    }
    private void loadFacultyList(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("FACULTY");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                facultyList.clear();
                for(DataSnapshot facultySnapshot : snapshot.getChildren()){
                    String id_faculty = facultySnapshot.getKey();
                    String name_faculty = facultySnapshot.child("ten_khoa").getValue(String.class);
                    if(id_faculty != null && name_faculty != null){
                        facultyList.add(name_faculty);
                        facultyMap.put(name_faculty, id_faculty);
                    }
                }
                // Khởi tạo ArrayAdapter cho Spinner và tải dữ liệu khoa từ Firebase
                facultyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, facultyList);
                facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_name_faculty.setAdapter(facultyAdapter);
                facultyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Tải danh sách thất bại")
                        .setConfirmText("OK")
                        .show();
            }
        });
    }
    private void loadLevelList(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("LEVEL");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                levelList.clear();
                for(DataSnapshot levelSnapshot : snapshot.getChildren()){
                    String id_level = levelSnapshot.getKey();
                    String name_level = levelSnapshot.child("ten_trinh_do").getValue(String.class);
                    if(id_level != null && name_level != null){
                        levelList.add(name_level);
                        Log.d("UploadFragment", "Level loaded: " + name_level);
                        levelMap.put(name_level, id_level);
                    }
                }
                // Khởi tạo ArrayAdapter cho Spinner và tải dữ liệu TRÌNH ĐỘ từ Firebase
                levelAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, levelList);
                levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_name_level.setAdapter(levelAdapter);
                levelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Tải danh sách thất bại")
                        .setConfirmText("OK")
                        .show();
            }
        });
    }
    private void onClickUploadMajor(Major major) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("MAJOR");
        Query checkMajor = myRef.orderByChild("ten_chuyen_nganh").equalTo(major.getTen_chuyen_nganh());
        Query checkLevel = myRef.orderByChild("id_trinh_do").equalTo(major.getId_trinh_do());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        checkMajor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Tiếp tục kiểm tra checkLevel nếu checkMajor tồn tại
                    checkLevel.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot levelSnapshot) {
                            dialog.dismiss();
                            if (levelSnapshot.exists()) {
                                // Thông báo lỗi nếu chuyên ngành và trình độ đều tồn tại
                                new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Chuyên ngành đã tồn tại")
                                        .setContentText("Chuyên ngành này đã có trong trình độ.")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                        .show();
                            } else {
                                // Thêm mới nếu trong trình độ chưa có
                                addMajorToDatabase(myRef, major, dialog);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dialog.dismiss();
                            showDatabaseError(error);
                        }
                    });
                } else {
                    // Thêm mới nếu chuyên ngành chưa tồn tại
                    addMajorToDatabase(myRef, major, dialog);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                showDatabaseError(error);
            }
        });
    }

    private void addMajorToDatabase(DatabaseReference myRef, Major major, AlertDialog dialog) {
        String key = myRef.push().getKey();
        major.setId(key);
        myRef.child(key).setValue(major, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                dialog.dismiss();
                if (error != null) {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Thêm chuyên ngành thất bại")
                            .setContentText("Lỗi: " + error.getMessage())
                            .setConfirmText("OK")
                            .show();
                } else {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Thêm chuyên ngành thành công")
                            .setConfirmText("OK")
                            .show();
                }
                switchFragment(new MajorFragment());
            }
        });
    }

    private void showDatabaseError(DatabaseError error) {
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Lỗi cơ sở dữ liệu")
                .setContentText("Lỗi: " + error.getMessage())
                .setConfirmText("OK")
                .show();
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}