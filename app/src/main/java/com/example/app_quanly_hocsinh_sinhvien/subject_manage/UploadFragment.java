package com.example.app_quanly_hocsinh_sinhvien.subject_manage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.app_quanly_hocsinh_sinhvien.ui.SubjectFragment;
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

    private TextView breadcrumb_home, breadcrumb_subject, tv_credit;
    private EditText edt_name_subject, edt_lecture_hours, edt_lap_hours, edt_code_subject;
    private Button btn_upload_subject;
    private Spinner spinner_name_major;

    // Adapter và danh sách cho Spinner
    private ArrayAdapter<String> majorAdapter;
    private ArrayList<String> majorList;
    private Map<String, String> majorMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_subject, container, false);
        initUi(view);
        majorList = new ArrayList<>();
        loadMajorList();
        initListener();
        return view;
    }

    private void initUi(View view){
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_subject = view.findViewById(R.id.breadcrumb_subject);
        tv_credit = view.findViewById(R.id.tv_credit);
        edt_name_subject = view.findViewById(R.id.edt_name_subject);
        edt_lecture_hours = view.findViewById(R.id.edt_lecture_hours);
        edt_lap_hours = view.findViewById(R.id.edt_lap_hours);
        edt_code_subject = view.findViewById(R.id.edt_code_subject);
        btn_upload_subject = view.findViewById(R.id.btn_upload_subject);
        spinner_name_major = view.findViewById(R.id.spinner_name_major);
    }

    private void initListener(){
        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new HomeFragment());
            }
        });
        breadcrumb_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new SubjectFragment());
            }
        });
        btn_upload_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUploadButtonSubject();
            }
        });
    }
    private void onClickUploadButtonSubject(){
        String str_code_subject = edt_code_subject.getText().toString().trim();
        String str_name_subject = edt_name_subject.getText().toString().trim();
        float lecturer_hours = Float.parseFloat(edt_lecture_hours.getText().toString().trim());
        float lap_hours = Float.parseFloat(edt_lap_hours.getText().toString().trim());
        int credits = (int) Math.ceil((lecturer_hours / 15) + (lap_hours / 30));
        String str_name_major = spinner_name_major.getSelectedItem().toString().trim();
        String id_major = majorMap.get(str_name_major);


        tv_credit.setText(String.valueOf(credits));


        if (str_code_subject.isEmpty() || str_name_subject.isEmpty() || lecturer_hours == 0 || lap_hours == 0 || credits == 0) {
            showAlert("Thiếu thông tin", "Vui lòng điền đầy đủ thông tin.");
            return;
        }


        Subject subject = new Subject(str_code_subject, str_name_subject, credits, lecturer_hours, lap_hours, id_major);
        saveDataSubject(subject);
    }
    private void loadMajorList(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("MAJOR");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                majorList.clear();
                for(DataSnapshot majorSnapshot : snapshot.getChildren()){
                    String id_major = majorSnapshot.getKey();
                    String name_major = majorSnapshot.child("ten_chuyen_nganh").getValue(String.class);
                    if(id_major != null && name_major != null){
                        majorList.add(name_major);
                        majorMap.put(name_major, id_major);
                    }
                }
                // Khởi tạo ArrayAdapter cho Spinner và tải dữ liệu khoa từ Firebase
                majorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, majorList);
                majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_name_major.setAdapter(majorAdapter);
                majorAdapter.notifyDataSetChanged();
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
    // Hàm trợ giúp để hiển thị cảnh báo
    private void showAlert(String title, String content) {
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("OK")
                .show();
    }
    private void saveDataSubject(Subject subject){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("SUBJECT");
        Query checkSubject = myRef.orderByChild("id").equalTo(subject.getId());

        checkSubject.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Mã chuyên ngành đã tồn tại")
                            .setContentText("Hãy nhập mã chuyên ngành khác")
                            .setConfirmText("OK")
                            .show();
                }else{
                    String pathObject = subject.getId();
                    myRef.child(pathObject).setValue(subject, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Thêm chuyên ngành thành công")
                                    .setContentText("Chuyên ngành đã được thêm vào danh sách")
                                    .setConfirmText("OK")
                                    .show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}