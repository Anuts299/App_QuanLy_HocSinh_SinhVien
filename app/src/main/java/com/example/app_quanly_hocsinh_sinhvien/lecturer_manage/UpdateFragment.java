package com.example.app_quanly_hocsinh_sinhvien.lecturer_manage;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.TeacherFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UpdateFragment extends Fragment {

    private EditText edt_update_name_lecturer;
    private Button btn_update_lecturer;
    private String id="";
    private DatabaseReference reference;
    private TextView breadcrumb_home, breadcrumb_lecturer;
    private Spinner spinner_update_name_faculty, spinner_update_name_level;

    // Adapter và danh sách cho Spinner khoa
    private ArrayAdapter<String> facultyAdapter;
    private ArrayList<String> facultyList;
    private Map<String, String> facultyMap = new HashMap<>();

    //Adapter và danh sách cho Spinner lecturer
    private ArrayAdapter<String> levelAdapter;
    private ArrayList<String> levelList;
    private Map<String, String> levelMap = new HashMap<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_lecturer, container, false);

        initUi(view);
        initListener();
        facultyList = new ArrayList<>();
        levelList = new ArrayList<>();
        loadFacultyList();
        loadLevelList();
        Bundle bundle = getArguments();
        if(bundle != null){
            edt_update_name_lecturer.setText(bundle.getString("ten_giang_vien"));
            id = bundle.getString("id");
        }
        reference = FirebaseDatabase.getInstance().getReference("LECTURER");

        return view;
    }

    private void initUi(View view){
        edt_update_name_lecturer = view.findViewById(R.id.edt_update_name_lecturer);
        btn_update_lecturer = view.findViewById(R.id.btn_update_lecturer);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_lecturer = view.findViewById(R.id.breadcrumb_lecturer);
        spinner_update_name_faculty = view.findViewById(R.id.spinner_update_name_faculty);
        spinner_update_name_level = view.findViewById(R.id.spinner_update_name_level);
    }
    private void initListener(){
        btn_update_lecturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButtonUpdateLecturer();
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
                    if(id_faculty!= null || name_faculty != null){
                        facultyList.add(name_faculty);
                        facultyMap.put(name_faculty, id_faculty);
                    }
                }
                // Khởi tạo ArrayAdapter cho Spinner và tải dữ liệu khoa từ Firebase
                facultyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, facultyList);
                facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_update_name_faculty.setAdapter(facultyAdapter);

                // Chọn đúng giá trị của Spinner từ Bundle sau khi dữ liệu đã tải xong
                Bundle bundle = getArguments();
                if(bundle != null) {
                    int position_faculty = facultyList.indexOf(bundle.getString("ten_khoa"));
                    if(position_faculty >= 0){
                        spinner_update_name_faculty.setSelection(position_faculty);
                    }
                }
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
                    if(id_level != null || name_level != null){
                        levelList.add(name_level);
                        levelMap.put(name_level, id_level);
                    }
                }
                // Khởi tạo ArrayAdapter cho Spinner và tải dữ liệu LEVEL từ Firebase
                levelAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, levelList);
                levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_update_name_level.setAdapter(levelAdapter);

                // Chọn đúng giá trị của Spinner từ Bundle sau khi dữ liệu đã tải xong
                Bundle bundle = getArguments();
                if(bundle != null) {
                    int position_level = levelList.indexOf(bundle.getString("ten_trinh_do"));
                    if(position_level >= 0){
                        spinner_update_name_level.setSelection(position_level);
                    }
                }

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
    private void onClickButtonUpdateLecturer(){
        String str_name_lecturer = edt_update_name_lecturer.getText().toString().trim();
        String str_name_faculty = spinner_update_name_faculty.getSelectedItem().toString();
        String str_name_level = spinner_update_name_level.getSelectedItem().toString();
        String id_faculty = facultyMap.get(str_name_faculty);
        String id_level = levelMap.get(str_name_level);

        if(str_name_lecturer.isEmpty() || id_faculty == null || id_faculty.isEmpty() || id_level == null || id_level.isEmpty()){
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Thiếu thông tin")
                    .setContentText("Vui lòng điền đầy đủ thông tin.")
                    .setConfirmText("OK")
                    .show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        Lecturer lecturer = new Lecturer(id, str_name_lecturer, id_faculty, id_level);
        reference.child(id).setValue(lecturer).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if(task.isSuccessful()){
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Chỉnh sửa giảng viên thành công")
                            .setConfirmText("OK")
                            .setConfirmClickListener(sDialog -> {
                                sDialog.dismissWithAnimation();
                                // Tạo bundle mới để truyền dữ liệu
                                Bundle resultBundle = new Bundle();
                                resultBundle.putString("ten_giang_vien", str_name_lecturer);
                                resultBundle.putString("ten_khoa", str_name_faculty);
                                resultBundle.putString("ten_trinh_do", str_name_level);

                                DetailFragment detailFragment = new DetailFragment();
                                detailFragment.setArguments(resultBundle);


                                // Quay lại DetailFragment
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, detailFragment)
                                        .addToBackStack(null)
                                        .commit();
                            })
                            .show();
                } else {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Lỗi!")
                            .setContentText("Không thể cập nhật lớp.")
                            .setConfirmText("OK")
                            .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                            .show();
                }
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