package com.example.app_quanly_hocsinh_sinhvien.faculty_manage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.FacultiesFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UpdateFragment extends Fragment {

    private EditText edt_update_name_faculty, edt_update_format_code;
    private Button btn_update_faculty;
    private String id = "";
    private DatabaseReference reference;
    private TextView breadcrumb_home, breadcrumb_faculty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_faculty, container, false);
        initUi(view);
        Bundle bundle = getArguments();
        if(bundle != null){
            edt_update_name_faculty.setText(bundle.getString("ten_khoa"));
            edt_update_format_code.setText(bundle.getString("ma_dinh_dang"));
            id = bundle.getString("id");
        }
        reference = FirebaseDatabase.getInstance().getReference("FACULTY").child(id);
        btn_update_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButtonUpdateFaculty();
            }
        });
        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new HomeFragment());
            }
        });
        breadcrumb_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new FacultiesFragment());
            }
        });
        return view;
    }
    private void onClickButtonUpdateFaculty(){
        String str_name_faculty = edt_update_name_faculty.getText().toString().trim();
        String str_format_code = edt_update_format_code.getText().toString().trim();
        if(str_name_faculty.isEmpty() || str_format_code.isEmpty()){
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

        Faculty faculty = new Faculty(id, str_name_faculty, str_format_code);
        reference.setValue(faculty).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if(task.isSuccessful()){
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Chỉnh sửa khoa thành công")
                            .setConfirmText("OK")
                            .setConfirmClickListener(sDialog -> {
                                sDialog.dismissWithAnimation();

                                Bundle resultBundle = new Bundle();
                                resultBundle.putString("ten_khoa",str_name_faculty);
                                resultBundle.putString("ma_dinh_dang",str_format_code);

                                DetailFragment detailFragment = new DetailFragment();
                                detailFragment.setArguments(resultBundle);

                                // Quay lại DetailFragment
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, detailFragment)
                                        .addToBackStack(null)
                                        .commit();
                            })
                            .show();
                }else {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Lỗi!")
                            .setContentText("Không thể cập nhật khoa.")
                            .setConfirmText("OK")
                            .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                            .show();
                }
            }
        });
    }
    private void initUi(View view){
        edt_update_name_faculty = view.findViewById(R.id.edt_update_name_faculty);
        edt_update_format_code = view.findViewById(R.id.edt_update_format_code);
        btn_update_faculty = view.findViewById(R.id.btn_update_faculty);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_faculty = view.findViewById(R.id.breadcrumb_faculty);
    }
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();  // Không dùng addToBackStack(null)
    }

}