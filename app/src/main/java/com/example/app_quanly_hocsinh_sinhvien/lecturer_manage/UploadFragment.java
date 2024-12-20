package com.example.app_quanly_hocsinh_sinhvien.lecturer_manage;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.TeacherFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UploadFragment extends Fragment {
    Button btn_upload_lecturer;
    EditText edt_name_lecturer;
    private Spinner spinner_name_faculty, spinner_name_level;
    TextView breadcrumb_home, breadcrumb_lecturer;

    // Adapter và danh sách cho Spinner Faculty
    private ArrayAdapter<String> facultyAdapter;
    private ArrayList<String> facultyList;
    private Map<String, String> facultyMap = new HashMap<>();

    // Adapter và danh sách cho Spinner Level
    private ArrayAdapter<String> levelAdapter;
    private ArrayList<String> levelList;
    private Map<String, String> levelMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_lecturer, container, false);
        initUi(view);

        facultyList = new ArrayList<>();
        loadFacultyList();
        levelList = new ArrayList<>();
        loadLevelList();
        initListener();
        return view;
    }

    private void initUi(View view){
        btn_upload_lecturer = view.findViewById(R.id.btn_upload_lecturer);
        edt_name_lecturer = view.findViewById(R.id.edt_name_lecturer);
        spinner_name_faculty = view.findViewById(R.id.spinner_name_faculty);
        spinner_name_level = view.findViewById(R.id.spinner_name_level);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_lecturer = view.findViewById(R.id.breadcrumb_lecturer);
    }

    private void initListener(){
        btn_upload_lecturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name_lecturer = edt_name_lecturer.getText().toString().trim();
                String name_faculty = spinner_name_faculty.getSelectedItem().toString().trim();
                String id_faculty = facultyMap.get(name_faculty);
                String name_level = spinner_name_level.getSelectedItem().toString().trim();
                String id_level = levelMap.get(name_level);
                if(name_lecturer.isEmpty() || id_faculty.isEmpty() || id_level.isEmpty()){
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Thiếu thông tin")
                            .setContentText("Vui lòng điền đầy đủ thông tin.")
                            .setConfirmText("OK")
                            .show();
                    return; // Dừng lại nếu có trường nào đó rỗng
                }
                Lecturer lecturer = new Lecturer(null, name_lecturer, id_faculty, id_level);
                onClickUploadLecturer(lecturer);
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
                switchFragment(new TeacherFragment());
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
                        levelMap.put(name_level, id_level);
                    }
                }
                // Khởi tạo ArrayAdapter cho Spinner và tải dữ liệu level từ Firebase
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
    private void onClickUploadLecturer(Lecturer lecturer){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("LECTURER");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        String key = myRef.push().getKey();
        lecturer.setId(key);
        myRef.child(key).setValue(lecturer, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                dialog.dismiss();
                if (error != null) {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Thêm giảng viên thất bại")
                            .setContentText("Lỗi: " + error.getMessage())
                            .setConfirmText("OK")
                            .show();
                    switchFragment(new TeacherFragment());
                } else {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Thêm giảng viên thành công")
                            .setConfirmText("OK")
                            .show();
                    switchFragment(new TeacherFragment());
                }
            }

            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
                new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Thêm giản viên thất bại")
                        .setContentText("Lỗi: " + databaseError.getMessage())
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