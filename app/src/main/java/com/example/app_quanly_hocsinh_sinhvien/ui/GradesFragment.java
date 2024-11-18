package com.example.app_quanly_hocsinh_sinhvien.ui;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.input_score.InputScore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class GradesFragment extends Fragment {

    private TextView breadcrumb_home, breadcrumb_inputscore;
    private Spinner spinner_name_faculty, spinner_name_class, spinner_name_student, spinner_name_subject, spinner_name_gradestype;
    private EditText edt_inputscore;
    private Button btn_upload_grades;

    // Adapter và danh sách cho Spinner
    private ArrayAdapter<String> facultyAdapter;
    private ArrayList<String> facultyList;
    private Map<String, String> facultyMap = new HashMap<>();

    private ArrayAdapter<String> classAdapter;
    private ArrayList<String> classList;
    private Map<String, String> classMap = new HashMap<>();

    private ArrayAdapter<String> studentAdapter;
    private ArrayList<String> studentList;
    private Map<String, String> studentMap = new HashMap<>();

    private ArrayAdapter<String> subjectAdapter;
    private ArrayList<String> subjectList;
    private Map<String, String> subjectMap = new HashMap<>();

    private ArrayAdapter<String> gradestypeAdapter;
    private ArrayList<String> gradestypeList;
    private Map<String, String> gradestypeMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grades, container, false);

        facultyList = new ArrayList<>();
        facultyList.add("Chọn khoa...");

        classList = new ArrayList<>();
        classList.add("Chọn lớp...");

        studentList = new ArrayList<>();
        studentList.add("Chọn sinh viên...");
        subjectList = new ArrayList<>();
        gradestypeList = new ArrayList<>();
        initUi(view);
        loadFacultyList();
        loadClassList(null);
        loadStudentList(null);
        loadSubjectList();
        loadGradesTypeList();
        initListener();
        return view;
    }
    private void loadFacultyList() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("FACULTY");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                facultyList.clear(); // Xóa placeholder
                facultyList.add("Chọn khoa..."); // Thêm lại mục mặc định
                for (DataSnapshot facultySnapshot : snapshot.getChildren()) {
                    String id_faculty = facultySnapshot.getKey();
                    String name_faculty = facultySnapshot.child("ten_khoa").getValue(String.class);
                    if (id_faculty != null && name_faculty != null) {
                        facultyList.add(name_faculty);
                        facultyMap.put(name_faculty, id_faculty);
                    }
                }
                // Adapter cho Spinner khoa
                facultyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, facultyList);
                facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_name_faculty.setAdapter(facultyAdapter);
                facultyAdapter.notifyDataSetChanged(); // Cập nhật Adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showDialogError("Tải danh sách thất bại", "OK", null);
            }
        });
    }


    private void loadClassList(String selectedFacultyId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("CLASSROOM");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classList.clear();
                classList.add("Chọn lớp...");
                for (DataSnapshot classSnapshot : snapshot.getChildren()) {
                    String id_class = classSnapshot.getKey();
                    String name_class = classSnapshot.child("ma_lop").getValue(String.class);
                    String id_faculty = classSnapshot.child("id_khoa").getValue(String.class); // Lấy id_khoa của lớp

                    // Kiểm tra nếu lớp thuộc khoa được chọn
                    if (id_class != null && name_class != null && id_faculty != null && id_faculty.equals(selectedFacultyId)) {
                        classList.add(name_class);
                        classMap.put(name_class, id_class);
                    }
                }
                // Adapter cho Spinner lớp
                classAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, classList);
                classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_name_class.setAdapter(classAdapter);
                classAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showDialogError("Tải danh sách thất bại", "OK", null);
            }
        });
    }


    private void loadStudentList(String selectedStudentId){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("STUDENT");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList.clear();
                studentList.add("Chọn sinh viên...");
                for(DataSnapshot studentSnapshot : snapshot.getChildren()){
                    String id_student = studentSnapshot.getKey();
                    String name_student = studentSnapshot.child("ten_sinh_vien").getValue(String.class);
                    String id_class = studentSnapshot.child("id_lop").getValue(String.class);
                    if (id_student != null && name_student != null && id_class != null && id_class.equals(selectedStudentId)){
                        studentList.add(name_student);
                        studentMap.put(name_student, id_student);
                    }
                }
                // Adapter cho Spinner sinh viên
                studentAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, studentList);
                studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_name_student.setAdapter(studentAdapter);
                studentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showDialogError("Tải danh sách thất bại", "OK", null);
            }
        });
    }

    private void loadSubjectList(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("SUBJECT");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subjectList.clear();
                subjectList.add("Chọn môn học...");
                for(DataSnapshot subjectSnapshot : snapshot.getChildren()){
                    String id_subject = subjectSnapshot.getKey();
                    String name_subject = subjectSnapshot.child("ten_mon_hoc").getValue(String.class);
                    if (id_subject != null && name_subject != null){
                        subjectList.add(name_subject);
                        subjectMap.put(name_subject, id_subject);
                    }
                }
                // Adapter cho Spinner môn học
                subjectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjectList);
                subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_name_subject.setAdapter(subjectAdapter);
                subjectAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showDialogError("Tải danh sách thất bại", "OK", null);
            }
        });
    }

    private void loadGradesTypeList(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("GRADESTYPE");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gradestypeList.clear();
                for(DataSnapshot gradestypeSnapshot : snapshot.getChildren()){
                    String id_gradestype = gradestypeSnapshot.getKey();
                    String name_gradestype = gradestypeSnapshot.child("ten_loai_diem").getValue(String.class);
                    if (id_gradestype != null && name_gradestype != null){
                        gradestypeList.add(name_gradestype);
                        gradestypeMap.put(name_gradestype, id_gradestype);
                    }
                }
                // Adapter cho Spinner loại điểm
                gradestypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, gradestypeList);
                gradestypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_name_gradestype.setAdapter(gradestypeAdapter);
                gradestypeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showDialogError("Tải danh sách thất bại", "OK", null);
            }
        });
    }

    private void showDialogError(String Title, String Confirm, String Content){
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(Title)
                .setContentText(Content)
                .setConfirmText(Confirm)
                .show();
    }

    private void showDialogSuccess(String Title, String Confirm, String Content){
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(Title)
                .setContentText(Content)
                .setConfirmText(Confirm)
                .show();
    }

    private void initListener(){
        spinner_name_faculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFaculty = facultyList.get(position);
                if (!selectedFaculty.equals("Chọn khoa...")) {
                    String selectedFacultyId = facultyMap.get(selectedFaculty);
                    if (selectedFacultyId != null) {
                        loadClassList(selectedFacultyId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì nếu không có mục nào được chọn
            }
        });

        spinner_name_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedClass = classList.get(position);
                if(!selectedClass.equals("Chọn lớp...")){
                    String selectedClassId = classMap.get(selectedClass);
                    if (selectedClassId != null) {
                        loadStudentList(selectedClassId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_upload_grades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name_faculty = spinner_name_faculty.getSelectedItem().toString().trim();
                String id_faculty = facultyMap.get(name_faculty);
                String name_class = spinner_name_class.getSelectedItem().toString().trim();
                String id_class = classMap.get(name_class);
                String name_student = spinner_name_student.getSelectedItem().toString().trim();
                String id_student = studentMap.get(name_student);
                String name_subject = spinner_name_subject.getSelectedItem().toString().trim();
                String id_subject = subjectMap.get(name_subject);
                String name_gradestype = spinner_name_gradestype.getSelectedItem().toString().trim();
                String id_gradestype = gradestypeMap.get(name_gradestype);
                String inputScore = edt_inputscore.getText().toString().trim();

                if (id_faculty == null || id_faculty.isEmpty() || id_class == null || id_class.isEmpty() || id_student == null || id_student.isEmpty() ||
                        id_subject == null || id_subject.isEmpty() || id_gradestype == null || id_gradestype.isEmpty() || inputScore.isEmpty()) {
                    showDialogError("Thiếu thông tin", "OK","Vui lòng điển đủ thông tin");
                    return;
                }

                // Kiểm tra điểm có hợp lệ không
                float grade;
                try {
                    grade = Float.parseFloat(inputScore);
                    if (grade < 0 || grade > 10) {
                        showDialogError("Điểm phải nằm trong khoảng từ 0 đến 10", "OK", null);
                        return;
                    }
                } catch (NumberFormatException e) {
                    showDialogError("Điểm không hợp lệ", "OK", null);
                    return;
                }

                InputScore inputScore1 = new InputScore(null, id_faculty, id_gradestype, id_class, id_subject, id_student, grade);
                onClickUploadGrade(inputScore1);
            }
        });
        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new HomeFragment());
            }
        });
    }


    private void onClickUploadGrade(InputScore inputScore){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("GRADE");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        String key = myRef.push().getKey();
        inputScore.setId(key);
        myRef.child(key).setValue(inputScore, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                dialog.dismiss();
                if(error != null){
                    showDialogError("Lưu điểm thất bại", "OK", "Xin vui lòng thử lại sau");
                }else{
                    showDialogSuccess("Thêm điểm thành công thành công", "OK", null);
                }
            }
        });
    }
    private void initUi(View view){
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_inputscore = view.findViewById(R.id.breadcrumb_inputscore);
        spinner_name_faculty = view.findViewById(R.id.spinner_name_faculty);
        spinner_name_class = view.findViewById(R.id.spinner_name_class);
        spinner_name_student = view.findViewById(R.id.spinner_name_student);
        spinner_name_subject = view.findViewById(R.id.spinner_name_subject);
        spinner_name_gradestype = view.findViewById(R.id.spinner_name_gradestype);
        edt_inputscore = view.findViewById(R.id.edt_inputscore);
        btn_upload_grades = view.findViewById(R.id.btn_upload_grades);

    }
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();  // Không dùng addToBackStack(null)
    }
}