package com.example.app_quanly_hocsinh_sinhvien.class_manage;

import static android.content.Intent.getIntent;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class DetailFragment extends Fragment {

    TextView tv_de_name_class, tv_de_name_faculties, tv_de_name_lecturer, tv_de_academic_year;
    FloatingActionButton deleteButtonClass;
    String id = "";


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
            id = bundle.getString("id");
        }
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
                                                                // Trở về fragment trước đó
                                                                requireActivity().getSupportFragmentManager().popBackStack();
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

        return view;

    }
    private void initUi(View view){
        tv_de_name_class = view.findViewById(R.id.tv_de_name_class);
        tv_de_name_faculties = view.findViewById(R.id.tv_de_name_faculties);
        tv_de_name_lecturer = view.findViewById(R.id.tv_de_name_lecturer);
        tv_de_academic_year = view.findViewById(R.id.tv_de_academic_year);
        deleteButtonClass = view.findViewById(R.id.deleteButtonClass);
    }
}