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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.ClassFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UpdateFragment extends Fragment {

    private EditText edt_update_code_class, edt_update_name_class, edt_update_academic_year;
    private Button btn_update_class;
    private String id = "";
    private DatabaseReference reference;
    private TextView breadcrumb_home, breadcrumb_classroom;
    private Spinner spinner_update_name_faculty, spinner_update_name_lecturer;

    // Adapter và danh sách cho Spinner
    private ArrayAdapter<String> facultyAdapter;
    private ArrayList<String> facultyList;
    private Map<String, String> facultyMap = new HashMap<>();

    // Adapter và danh sách cho Spinner Giảng viên
    private ArrayAdapter<String> lecturerAdapter;
    private ArrayList<String> lecturerList;
    private Map<String, String> lecturerMap = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_class, container, false);

        initUi(view);
        facultyList = new ArrayList<>();
        lecturerList = new ArrayList<>();
        loadFacultyList();
        loadLecturerList();
        Bundle bundle = getArguments();
        if(bundle != null){
            edt_update_code_class.setText(bundle.getString("ma_lop"));
            edt_update_name_class.setText(bundle.getString("ten_lop"));
            edt_update_academic_year.setText(bundle.getString("nam_hoc"));
            id = bundle.getString("id");
        }
        reference = FirebaseDatabase.getInstance().getReference("CLASSROOM").child(id);
        btn_update_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButtonUpdateClass();
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
        return view;
    }
    private void onClickButtonUpdateClass() {
        String str_code_class = edt_update_code_class.getText().toString().trim();
        String str_name_class = edt_update_name_class.getText().toString().trim();
        String str_name_faculty = spinner_update_name_faculty.getSelectedItem().toString().trim();
        String id_faculty = facultyMap.get(str_name_faculty);
        String str_name_lecturer = spinner_update_name_lecturer.getSelectedItem().toString().trim();
        String id_lecturer = lecturerMap.get(str_name_lecturer);
        String str_academic_year = edt_update_academic_year.getText().toString().trim();

        if (str_code_class.isEmpty() || str_name_class.isEmpty() || Objects.requireNonNull(id_faculty).isEmpty() || Objects.requireNonNull(id_lecturer).isEmpty() || str_academic_year.isEmpty()) {
            new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Thiếu thông tin")
                    .setContentText("Vui lòng điền đầy đủ thông tin.")
                    .setConfirmText("OK")
                    .show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("CLASSROOM");
        Query checkClassQuery = classRef.orderByChild("ma_lop").equalTo(str_code_class);

        checkClassQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.dismiss();
                if (dataSnapshot.exists()) {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Lớp đã tồn tại")
                            .setContentText("Mã lớp này đã có trong hệ thống.")
                            .setConfirmText("OK")
                            .show();
                } else {
                    // Nếu mã lớp chưa tồn tại, tiến hành cập nhật lớp
                    Classroom classroom = new Classroom(id, str_code_class, str_academic_year, id_lecturer, str_name_class, id_faculty);

                    reference.setValue(classroom).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Chỉnh sửa lớp thành công")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(sDialog -> {
                                            sDialog.dismissWithAnimation();
                                            Bundle resultBundle = new Bundle();
                                            resultBundle.putString("ma_lop", str_code_class);
                                            resultBundle.putString("ten_lop", str_name_class);
                                            resultBundle.putString("id_khoa", id_faculty);
                                            resultBundle.putString("ten_co_van", str_name_lecturer);
                                            resultBundle.putString("nam_hoc", str_academic_year);

                                            DetailFragment detailFragment = new DetailFragment();
                                            detailFragment.setArguments(resultBundle);

                                            getParentFragmentManager().beginTransaction()
                                                    .replace(R.id.fragment_container, detailFragment)
                                                    .addToBackStack(null)
                                                    .commit();
                                        })
                                        .show();
                            } else {
                                new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Lỗi!")
                                        .setContentText("Không thể cập nhật lớp.")
                                        .setConfirmText("OK")
                                        .show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Lỗi!")
                        .setContentText("Lỗi khi truy vấn dữ liệu.")
                        .setConfirmText("OK")
                        .show();
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
                    if(id_faculty != null || name_faculty != null){
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
    private void loadLecturerList(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("LECTURER");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lecturerList.clear();
                for(DataSnapshot lecturerSnapshot : snapshot.getChildren()){
                    String id_lecturer = lecturerSnapshot.getKey();
                    String name_lecturer = lecturerSnapshot.child("ten_giang_vien").getValue(String.class);
                    if(id_lecturer != null || name_lecturer != null){
                        lecturerList.add(name_lecturer);
                        lecturerMap.put(name_lecturer, id_lecturer);
                    }
                }
                // Khởi tạo ArrayAdapter cho Spinner và tải dữ liệu khoa từ Firebase
                lecturerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, lecturerList);
                lecturerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_update_name_lecturer.setAdapter(lecturerAdapter);
                // Chọn đúng giá trị của Spinner từ Bundle sau khi dữ liệu đã tải xong
                Bundle bundle = getArguments();
                if(bundle != null) {
                    int position_lecturer = lecturerList.indexOf(bundle.getString("ten_co_van"));
                    if(position_lecturer >= 0){
                        spinner_update_name_lecturer.setSelection(position_lecturer);
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
    private void initUi(View view){
        edt_update_code_class = view.findViewById(R.id.edt_update_code_class);
        edt_update_name_class = view.findViewById(R.id.edt_update_name_class);
        edt_update_academic_year = view.findViewById(R.id.edt_update_academic_year);
        btn_update_class = view.findViewById(R.id.btn_update_class);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_classroom = view.findViewById(R.id.breadcrumb_classroom);
        spinner_update_name_faculty = view.findViewById(R.id.spinner_update_name_faculty);
        spinner_update_name_lecturer = view.findViewById(R.id.spinner_update_name_lecturer);
    }
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();  // Không dùng addToBackStack(null)
    }

}