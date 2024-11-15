package com.example.app_quanly_hocsinh_sinhvien.subject_manage;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.ClassFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.SubjectFragment;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class DetailFragment extends Fragment {

    private TextView breadcrumb_home, breadcrumb_subject,tv_de_credit, tv_de_code_subject, tv_de_name_subject, tv_de_lecturer_hour,
            tv_de_lap_hour, tv_de_name_major;
    private FloatingActionButton editButtonSubject, deleteButtonSubject;
    String id = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_subject, container, false);
        initUi(view);
        Bundle bundle = getArguments();
        if(bundle != null){
            tv_de_code_subject.setText(bundle.getString("id"));
            tv_de_name_subject.setText(bundle.getString("ten_mon_hoc"));
            tv_de_lecturer_hour.setText(bundle.getFloat("so_gio_LT")+"");
            tv_de_lap_hour.setText(bundle.getFloat("so_gio_TH")+"");
            tv_de_credit.setText(bundle.getInt("so_dvht")+"");
            tv_de_name_major.setText(bundle.getString("ten_chuyen_nganh"));
            id = bundle.getString("id");
        }
        initListener();
        return view;
    }
    private void initUi(View view){
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_subject = view.findViewById(R.id.breadcrumb_subject);
        tv_de_credit = view.findViewById(R.id.tv_de_credit);
        tv_de_code_subject = view.findViewById(R.id.tv_de_code_subject);
        tv_de_name_subject = view.findViewById(R.id.tv_de_name_subject);
        tv_de_lecturer_hour = view.findViewById(R.id.tv_de_lecturer_hour);
        tv_de_lap_hour = view.findViewById(R.id.tv_de_lap_hour);
        tv_de_name_major = view.findViewById(R.id.tv_de_name_major);
        editButtonSubject = view.findViewById(R.id.editButtonSubject);
        deleteButtonSubject = view.findViewById(R.id.deleteButtonSubject);
    }

    private void initListener(){
        deleteButtonSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị SweetAlertDialog xác nhận xóa
                new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Xác nhận xóa")
                        .setContentText("Bạn có chắc chắn muốn xóa môn học này không?")
                        .setConfirmText("Xóa")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                builder.setCancelable(false);
                                builder.setView(R.layout.progress_layout);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                // Xác nhận xóa - thực hiện xóa trong Firebase
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SUBJECT");
                                reference.child(id) // Thay "id_classroom" bằng ID lớp học bạn muốn xóa
                                        .removeValue()
                                        .addOnCompleteListener(task -> {
                                            dialog.dismiss();
                                            if (task.isSuccessful()) {
                                                sDialog
                                                        .setTitleText("Đã xóa!")
                                                        .setContentText("Môn học đã được xóa.")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.dismiss();

                                                                switchFragment(new ClassFragment());
                                                            }
                                                        })
                                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);


                                            } else {
                                                sDialog
                                                        .setTitleText("Lỗi!")
                                                        .setContentText("Không thể xóa môn học.")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                            }
                                        });
                            }
                        })
                        .setCancelButton("Hủy", SweetAlertDialog::dismiss)
                        .show();
            }
        });
        editButtonSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                String maMon = tv_de_code_subject.getText().toString().trim();
                String tenMon = tv_de_name_subject.getText().toString().trim();
                float soGLT = Float.parseFloat(tv_de_lecturer_hour.getText().toString().trim());
                float soGTH = Float.parseFloat(tv_de_lap_hour.getText().toString().trim());
                Integer soCre = Integer.parseInt(tv_de_credit.getText().toString().trim());
                String tenCN = tv_de_name_major.getText().toString().trim();

                bundle.putString("ma_mon", maMon);
                bundle.putString("ten_mon", tenMon);
                bundle.putString("ten_CN", tenCN);
                bundle.putFloat("so_LT", soGLT);
                bundle.putFloat("so_TH", soGTH);
                bundle.putInt("so_TC", soCre);
                bundle.putString("id", id);

                UpdateFragment updateFragment = new UpdateFragment();
                updateFragment.setArguments(bundle);

                try {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, updateFragment)
                            .addToBackStack(null)
                            .commit();
                    Log.d("EditButton", "Fragment replaced successfully");
                } catch (Exception e) {
                    Log.e("EditButton", "Error replacing fragment", e);
                }
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
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();  // Không dùng addToBackStack(null)
    }
}