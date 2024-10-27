package com.example.app_quanly_hocsinh_sinhvien.class_manage;

import static android.content.Intent.getIntent;

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
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class DetailFragment extends Fragment {

    TextView tv_de_name_class, tv_de_name_faculties, tv_de_name_lecturer, tv_de_academic_year, tv_de_name_class2, breadcrumb_classroom, breadcrumb_home;
    FloatingActionButton deleteButtonClass, editButtonClass;
    String id = "";
    private ClassFragment classFragment = new ClassFragment();;
    private HomeFragment homeFragment = new HomeFragment();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_class, container, false);
        initUi(view);
        Bundle bundle = getArguments();
        if(bundle != null){
            tv_de_name_class.setText(bundle.getString("ma_lop"));
            tv_de_name_faculties.setText(bundle.getString("ten_khoa"));
            tv_de_name_lecturer.setText(bundle.getString("ten_co_van"));
            tv_de_academic_year.setText(bundle.getString("nam_hoc"));
            tv_de_name_class2.setText(bundle.getString("ten_lop"));
            id = bundle.getString("id");
        }
        initListener();
        return view;

    }
    private void initUi(View view){
        tv_de_name_class = view.findViewById(R.id.tv_de_name_class);
        tv_de_name_faculties = view.findViewById(R.id.tv_de_name_faculties);
        tv_de_name_lecturer = view.findViewById(R.id.tv_de_name_lecturer);
        tv_de_academic_year = view.findViewById(R.id.tv_de_academic_year);
        tv_de_name_class2 = view.findViewById(R.id.tv_de_name_class2);
        deleteButtonClass = view.findViewById(R.id.deleteButtonClass);
        editButtonClass = view.findViewById(R.id.editButtonClass);
        breadcrumb_classroom = view.findViewById(R.id.breadcrumb_classroom);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
    }
    private void initListener(){
        deleteButtonClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị SweetAlertDialog xác nhận xóa
                new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Xác nhận xóa")
                        .setContentText("Bạn có chắc chắn muốn xóa lớp học này không?")
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
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CLASSROOM");
                                reference.child(id) // Thay "id_classroom" bằng ID lớp học bạn muốn xóa
                                        .removeValue()
                                        .addOnCompleteListener(task -> {
                                            dialog.dismiss();
                                            if (task.isSuccessful()) {
                                                sDialog
                                                        .setTitleText("Đã xóa!")
                                                        .setContentText("Lớp học đã được xóa.")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.dismiss();

                                                                switchFragment(classFragment);
                                                            }
                                                        })
                                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);


                                            } else {
                                                sDialog
                                                        .setTitleText("Lỗi!")
                                                        .setContentText("Không thể xóa lớp học.")
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
        editButtonClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("EditButton", "Edit button clicked");

                Bundle bundle = new Bundle();
                String maLop = tv_de_name_class.getText().toString();
                String tenLop = tv_de_name_class2.getText().toString();
                String tenKhoa = tv_de_name_faculties.getText().toString();
                String tenCoVan = tv_de_name_lecturer.getText().toString();
                String namHoc = tv_de_academic_year.getText().toString();

                Log.d("EditButton", "maLop: " + maLop);
                Log.d("EditButton", "tenLop: " + tenLop);
                Log.d("EditButton", "tenKhoa: " + tenKhoa);
                Log.d("EditButton", "tenCoVan: " + tenCoVan);
                Log.d("EditButton", "namHoc: " + namHoc);
                Log.d("EditButton", "id: " + id);

                bundle.putString("ma_lop", maLop);
                bundle.putString("ten_lop", tenLop);
                bundle.putString("ten_khoa", tenKhoa);
                bundle.putString("ten_co_van", tenCoVan);
                bundle.putString("nam_hoc", namHoc);
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
                switchFragment(homeFragment);
            }
        });
        breadcrumb_classroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(classFragment);
            }
        });
    }
    public void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}