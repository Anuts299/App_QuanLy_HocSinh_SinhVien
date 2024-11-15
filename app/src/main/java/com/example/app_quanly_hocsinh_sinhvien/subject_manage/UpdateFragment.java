package com.example.app_quanly_hocsinh_sinhvien.subject_manage;

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
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.SubjectFragment;
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

    private TextView breadcrumb_home, breadcrumb_subject, tv_ud_code_subject,
            tv_ud_credit;
    private EditText edt_ud_name_subject, edt_ud_lecturer_hour, edt_ud_lap_hour;
    private Spinner spinner_ud_name_major;
    private Button btn_ud_major;
    private String id = "";
    private DatabaseReference reference;

    // Adapter và danh sách cho Spinner
    private ArrayAdapter<String> majorAdapter;
    private ArrayList<String> majorList;
    private Map<String, String> majorMap = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_subject, container, false);
        initUi(view);
        majorList = new ArrayList<>();
        Bundle bundle = getArguments();
        if(bundle != null){
            tv_ud_code_subject.setText(bundle.getString("ma_mon"));
            edt_ud_name_subject.setText(bundle.getString("ten_mon"));
            tv_ud_credit.setText(String.valueOf(bundle.getInt("so_TC",0)));
            edt_ud_lecturer_hour.setText(String.valueOf(bundle.getFloat("so_LT", 0.0F)));
            edt_ud_lap_hour.setText(String.valueOf(bundle.getFloat("so_TH", 0.0F)));
            id = bundle.getString("id");
        }
        loadMajorList();
        reference = FirebaseDatabase.getInstance().getReference("SUBJECT").child(id);
        initListener();
        return view;
    }
    private void initUi(View view){
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_subject = view.findViewById(R.id.breadcrumb_subject);
        tv_ud_code_subject = view.findViewById(R.id.tv_ud_code_subject);
        tv_ud_credit = view.findViewById(R.id.tv_ud_credit);
        edt_ud_name_subject = view.findViewById(R.id.edt_ud_name_subject);
        edt_ud_lecturer_hour = view.findViewById(R.id.edt_ud_lecturer_hour);
        edt_ud_lap_hour = view.findViewById(R.id.edt_ud_lap_hour);
        spinner_ud_name_major = view.findViewById(R.id.spinner_ud_name_major);
        btn_ud_major = view.findViewById(R.id.btn_ud_major);
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
                    if(id_major != null || name_major != null){
                        majorList.add(name_major);
                        majorMap.put(name_major, id_major);
                    }
                }
                // Khởi tạo ArrayAdapter cho Spinner và tải dữ liệu chuyên ngành từ Firebase
                majorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, majorList);
                majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_ud_name_major.setAdapter(majorAdapter);
                // Chọn đúng giá trị của Spinner từ Bundle sau khi dữ liệu đã tải xong
                Bundle bundle = getArguments();
                if(bundle != null) {
                    int position_major = majorList.indexOf(bundle.getString("ten_chuyen_nganh"));
                    if(position_major >= 0){
                        spinner_ud_name_major.setSelection(position_major);
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

    private void initListener(){
        btn_ud_major.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButtonUpdateSubject();
            }
        });
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
    }

    private void onClickButtonUpdateSubject(){
        String str_name_subject = edt_ud_name_subject.getText().toString().trim();
        float so_gio_LT = Float.parseFloat(edt_ud_lecturer_hour.getText().toString().trim());
        float so_gio_TH = Float.parseFloat(edt_ud_lap_hour.getText().toString().trim());
        int so_credit = (int) Math.ceil((so_gio_LT / 15) + (so_gio_TH / 30));
        String str_name_major = spinner_ud_name_major.getSelectedItem().toString().trim();
        String id_major = majorMap.get(str_name_major);

        if(str_name_subject.isEmpty() || Objects.requireNonNull(id_major).isEmpty()|| so_gio_LT == 0.0 ||so_gio_TH == 0.0 || so_credit == 0){
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

        DatabaseReference subjectRef = FirebaseDatabase.getInstance().getReference("SUBJECT");
        Subject subject = new Subject(id, str_name_major, so_credit, so_gio_LT, so_gio_TH, id_major);

        reference.setValue(subject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if(task.isSuccessful()){
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Chỉnh sửa môn học thành công")
                            .setConfirmText("OK")
                            .setConfirmClickListener(sDialog -> {
                                sDialog.dismissWithAnimation();
                                Bundle resultBundle = new Bundle();
                                resultBundle.putString("ma_mon", id);
                                resultBundle.putString("ten_mon_hoc", str_name_subject);
                                resultBundle.putFloat("so_gio_LT", so_gio_LT);
                                resultBundle.putFloat("so_gio_TH", so_gio_TH);
                                resultBundle.putInt("so_dvht", so_credit);
                                resultBundle.putString("ten_chuyen_nganh", str_name_major);
                                resultBundle.putString("id", id);
                                DetailFragment detailFragment = new DetailFragment();
                                detailFragment.setArguments(resultBundle);

                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, detailFragment)
                                        .addToBackStack(null)
                                        .commit();
                            })
                            .show();
                }else {
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
        fragmentTransaction.commit();  // Không dùng addToBackStack(null)
    }
}