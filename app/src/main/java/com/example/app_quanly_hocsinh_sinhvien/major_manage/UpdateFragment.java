package com.example.app_quanly_hocsinh_sinhvien.major_manage;

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
import com.example.app_quanly_hocsinh_sinhvien.major_manage.DetailFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.MajorFragment;
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

    private TextView breadcrumb_home, breadcrumb_major;
    private EditText edt_ud_name_major;
    private Spinner spinner_ud_name_faculty, spinner_ud_name_level;
    private Button btn_ud_major;
    private DatabaseReference reference;
    private String id ="";

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
        View view = inflater.inflate(R.layout.fragment_update_major, container, false);
        iniUi(view);
        facultyList = new ArrayList<>();
        levelList = new ArrayList<>();
        loadFacultyList();
        loadLevelList();

        Bundle bundle = getArguments();
        if(bundle != null){
            edt_ud_name_major.setText(bundle.getString("ten_CN"));
            id = bundle.getString("id");

        }
        Log.d("UploadFragment", id+"12345");
        reference = FirebaseDatabase.getInstance().getReference("MAJOR");
        initListener();

        return view;
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
                spinner_ud_name_faculty.setAdapter(facultyAdapter);
                facultyAdapter.notifyDataSetChanged();
                // Chọn đúng giá trị của Spinner từ Bundle sau khi dữ liệu đã tải xong
                Bundle bundle = getArguments();
                if(bundle != null) {
                    int position_faculty = facultyList.indexOf(bundle.getString("ten_khoa"));
                    if(position_faculty >= 0){
                        spinner_ud_name_faculty.setSelection(position_faculty);
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
                    if(id_level != null && name_level != null){
                        levelList.add(name_level);
                        levelMap.put(name_level, id_level);
                    }
                }
                // Khởi tạo ArrayAdapter cho Spinner và tải dữ liệu TRÌNH ĐỘ từ Firebase
                levelAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, levelList);
                levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_ud_name_level.setAdapter(levelAdapter);
                levelAdapter.notifyDataSetChanged();

                // Chọn đúng giá trị của Spinner từ Bundle sau khi dữ liệu đã tải xong
                Bundle bundle = getArguments();
                if(bundle != null) {
                    int position_level = levelList.indexOf(bundle.getString("ten_trinh_do"));
                    if(position_level >= 0){
                        spinner_ud_name_level.setSelection(position_level);
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
    private void iniUi(View view){
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_major = view.findViewById(R.id.breadcrumb_major);
        edt_ud_name_major = view.findViewById(R.id.edt_ud_name_major);
        spinner_ud_name_faculty = view.findViewById(R.id.spinner_ud_name_faculty);
        spinner_ud_name_level = view.findViewById(R.id.spinner_ud_name_level);
        btn_ud_major = view.findViewById(R.id.btn_ud_major);

    }

    private void initListener(){
        btn_ud_major.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButtonUpdateMajor();
            }
        });
        breadcrumb_major.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    private void onClickButtonUpdateMajor(){
        String str_name_major = edt_ud_name_major.getText().toString().trim();
        String str_name_faculty = spinner_ud_name_faculty.getSelectedItem().toString().trim();
        String id_faculty = facultyMap.get(str_name_faculty);
        String str_name_level = spinner_ud_name_level.getSelectedItem().toString().trim();
        String id_level = levelMap.get(str_name_level);

        if(str_name_major.isEmpty() || Objects.requireNonNull(id_faculty).isEmpty() || Objects.requireNonNull(id_level).isEmpty()){
            showSweetAlertDialog(SweetAlertDialog.WARNING_TYPE, "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("MAJOR");

        // Kiểm tra xem chuyên ngành đã tồn tại trong cơ sở dữ liệu không, nếu có thì kiểm tra thêm về khoa và trình độ
        Query checkMajor = myRef.orderByChild("ten_chuyen_nganh").equalTo(str_name_major);

        checkMajor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isMajorExist = false;
                for (DataSnapshot majorSnapshot : snapshot.getChildren()) {
                    String existingFacultyId = majorSnapshot.child("id_khoa").getValue(String.class);
                    String existingLevelId = majorSnapshot.child("id_trinh_do").getValue(String.class);

                    // Kiểm tra xem chuyên ngành này có tồn tại với khoa và trình độ khác không
                    if (existingFacultyId.equals(id_faculty) && existingLevelId.equals(id_level)) {
                        isMajorExist = true;
                        break;
                    }
                }

                if (isMajorExist) {
                    dialog.dismiss();
                    showSweetAlertDialog(SweetAlertDialog.WARNING_TYPE, "Chuyên ngành đã tồn tại", "Chuyên ngành này đã có trong trình độ và khoa.");
                } else {
                    dialog.dismiss();
                    updateMajorToDatabase(new Major(), reference, id, str_name_major, id_faculty, id_level, dialog);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                showDatabaseError(error);
            }
        });
    }

    private void showSweetAlertDialog(int type, String title, String content) {
        new SweetAlertDialog(requireActivity(), type)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("OK")
                .show();
    }
    private void updateMajorToDatabase(Major major, DatabaseReference reference, String id, String str_name_major, String id_faculty, String id_level, AlertDialog dialog){
        major = new Major(id, str_name_major, id_faculty, id_level);
        reference.child(id).setValue(major).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Chỉnh sửa chuyên ngành thành công")
                            .setConfirmText("OK")
                            .setConfirmClickListener(sDialog -> {
                                sDialog.dismissWithAnimation();

                                // Tạo Bundle chứa dữ liệu
                                Bundle resultBundle = new Bundle();
                                resultBundle.putString("ten_chuyen_nganh", str_name_major);
                                resultBundle.putString("id_khoa", id_faculty);
                                resultBundle.putString("id_trinh_do", id_level);

                                // Tạo Fragment mới
                                DetailFragment detailFragment = new DetailFragment();
                                detailFragment.setArguments(resultBundle);

                                // Thay thế fragment và thêm vào back stack
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, detailFragment)
                                        .addToBackStack(null)  // Thêm vào back stack để có thể quay lại fragment trước
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
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}