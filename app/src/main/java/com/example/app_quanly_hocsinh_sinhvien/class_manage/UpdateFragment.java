package com.example.app_quanly_hocsinh_sinhvien.class_manage;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UpdateFragment extends Fragment {

    private EditText edt_update_code_class, edt_update_name_class, edt_update_name_lecturer, edt_update_academic_year;
    private Button btn_update_class;
    private String id = "";
    private DatabaseReference reference;
    private TextView breadcrumb_home, breadcrumb_classroom;
    private Spinner spinner_update_name_faculty;

    // Adapter và danh sách cho Spinner
    private ArrayAdapter<String> facultyAdapter;
    private ArrayList<String> facultyList;
    private Map<String, String> facultyMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_class, container, false);

        initUi(view);
        facultyList = new ArrayList<>();
        loadFacultyList();
        Bundle bundle = getArguments();
        if(bundle != null){
            edt_update_code_class.setText(bundle.getString("ma_lop"));
            edt_update_name_class.setText(bundle.getString("ten_lop"));
            int position = facultyList.indexOf(bundle.getString("ten_khoa"));
            if (position >= 0) {
                spinner_update_name_faculty.setSelection(position);
            }
            edt_update_name_lecturer.setText(bundle.getString("ten_co_van"));
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
    private void onClickButtonUpdateClass(){
        String str_code_class = edt_update_code_class.getText().toString().trim();
        String str_name_class = edt_update_name_class.getText().toString().trim();
        String str_name_faculty = spinner_update_name_faculty.getSelectedItem().toString().trim();
        String id_faculty = facultyMap.get(str_name_faculty);
        String str_name_lecturer = edt_update_name_lecturer.getText().toString().trim();
        String str_academic_year = edt_update_academic_year.getText().toString().trim();
        if (str_code_class.isEmpty() || str_name_class.isEmpty() || Objects.requireNonNull(id_faculty).isEmpty() || str_name_lecturer.isEmpty() || str_academic_year.isEmpty()) {
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

        Classroom classroom = new Classroom(id, str_code_class, str_academic_year, str_name_lecturer, str_name_class, id_faculty);
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
                                // Sau khi cập nhật thành công,tạo một bundle mới để truyền dữ liệu
                                Bundle resultBundle = new Bundle();
                                resultBundle.putString("ma_lop", str_code_class);
                                resultBundle.putString("ten_lop", str_name_class);
                                resultBundle.putString("id_khoa", id_faculty);
                                resultBundle.putString("ten_co_van", str_name_lecturer);
                                resultBundle.putString("nam_hoc", str_academic_year);

                                // Thiết lập lại kết quả cho DetailFragment
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
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Lỗi!")
                            .setContentText("Không thể cập nhật lớp.")
                            .setConfirmText("OK")
                            .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                            .show();
                }
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
                    if(name_faculty != null){
                        facultyList.add(name_faculty);
                        facultyMap.put(name_faculty, id_faculty);
                    }
                }
                // Khởi tạo ArrayAdapter cho Spinner và tải dữ liệu khoa từ Firebase
                facultyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, facultyList);
                facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_update_name_faculty.setAdapter(facultyAdapter);
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
    private void initUi(View view){
        edt_update_code_class = view.findViewById(R.id.edt_update_code_class);
        edt_update_name_class = view.findViewById(R.id.edt_update_name_class);
//        edt_update_name_faculty = view.findViewById(R.id.edt_update_name_faculty);
        edt_update_name_lecturer = view.findViewById(R.id.edt_update_name_lecturer);
        edt_update_academic_year = view.findViewById(R.id.edt_update_academic_year);
        btn_update_class = view.findViewById(R.id.btn_update_class);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_classroom = view.findViewById(R.id.breadcrumb_classroom);
        spinner_update_name_faculty = view.findViewById(R.id.spinner_update_name_faculty);
    }
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();  // Không dùng addToBackStack(null)
    }

}