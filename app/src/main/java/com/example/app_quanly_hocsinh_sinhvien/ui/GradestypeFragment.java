package com.example.app_quanly_hocsinh_sinhvien.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.FragmentActionListener;
import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.gradestype_manage.Gradestype;
import com.example.app_quanly_hocsinh_sinhvien.gradestype_manage.GradestypeAdapter;
import com.example.app_quanly_hocsinh_sinhvien.level_manage.Level;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class GradestypeFragment extends Fragment {

    FloatingActionButton fab_gradestype;
    private RecyclerView recGrade;
    private GradestypeAdapter mGradesTypeAdapter;
    private List<Gradestype> mListGradesType;

    private TextView breadcrumb_home;
    private FragmentActionListener mListenerHome;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActionListener) {
            mListenerHome = (FragmentActionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentActionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gradestype, container, false);
        mListGradesType = new ArrayList<>();
        mGradesTypeAdapter = new GradestypeAdapter(mListGradesType, new GradestypeAdapter.IClickListener() {
            @Override
            public void onClickUpdateItem(Gradestype gradestype) {
                openDialogUpdateItemGradesType(gradestype);
            }

            @Override
            public void onClickDetailItem(Gradestype gradestype) {
                onClickDeleteData(gradestype);
            }
        });
        initUi(view);
        initListener();
        getListGradesTypeFromRealtimeDatabase();
        return view;
    }
    private void onClickUploadGradeType() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_upload_level);

        // Thiết lập chiều rộng và chiều cao cho Dialog
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Tìm Spinner và thiết lập giá trị
        Spinner spinner_weight = dialog.findViewById(R.id.spinner_weight);
        spinner_weight.setVisibility(View.VISIBLE);
        Integer[] weights = {1, 2, 3}; // Các giá trị hệ số
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                dialog.getContext(),
                android.R.layout.simple_spinner_item,
                weights
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_weight.setAdapter(adapter);

        // Các thành phần khác trong Dialog
        TextView tv_grade = dialog.findViewById(R.id.tv_level);
        tv_grade.setText("Thêm Loại điểm");
        EditText editTextGrade = dialog.findViewById(R.id.edit_text_level);
        editTextGrade.setHint("Nhập loại điểm");

        Button buttonAdd = dialog.findViewById(R.id.btn_upload_level);
        buttonAdd.setText("THÊM LOẠI ĐIỂM");
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy loại điểm từ EditText
                String name_gradestype = editTextGrade.getText().toString().trim();

                // Lấy giá trị được chọn từ Spinner
                Integer selectedWeight = (Integer) spinner_weight.getSelectedItem();

                // Kiểm tra và xử lý
                if (name_gradestype.isEmpty()) {
                    editTextGrade.setError("Vui lòng nhập loại điểm!");
                    return;
                }
                Gradestype gradestype = new Gradestype(null, name_gradestype, selectedWeight);
                UploadGradetype(gradestype);
                // Đóng Dialog
                dialog.dismiss();
            }
        });

        // Hiển thị Dialog
        dialog.show();
    }
    private void UploadGradetype(Gradestype gradestype){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("GRADESTYPE");
        Query checkGrade = myRef.orderByChild("ten_loai_diem").equalTo(gradestype.getTen_loai_diem());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        checkGrade.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialog.dismiss();
                if (snapshot.exists()) {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Loại điểm đã tồn tại")
                            .setContentText("Tên loại điểm này đã có trong hệ thống.")
                            .setConfirmText("OK")
                            .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                            .show();
                }else{
                    String key = myRef.push().getKey();
                    gradestype.setId(key);
                    myRef.child(key).setValue(gradestype, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Thêm loại điểm thất bại")
                                        .setContentText("Xin hãy thử lại")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                        .show();
                            } else {
                                new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Thêm loại điểm thành công")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                        .show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Thêm loại điểm thất bại")
                        .setContentText("Xin hãy thử lại")
                        .setConfirmText("OK")
                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show();
            }
        });
    }
    private void getListGradesTypeFromRealtimeDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("GRADESTYPE");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Gradestype gradestype = snapshot.getValue(Gradestype.class);
                if(gradestype != null){
                    mListGradesType.add(gradestype);
                    mGradesTypeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Gradestype gradestype = snapshot.getValue(Gradestype.class);
                if(gradestype == null || mListGradesType == null || mListGradesType.isEmpty()){
                    return;
                }
                for(int i = 0; i < mListGradesType.size(); i++){
                    if(Objects.equals(gradestype.getId(), mListGradesType.get(i).getId())){
                        mListGradesType.set(i, gradestype);
                        break;
                    }
                }
                mGradesTypeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Gradestype gradestype = snapshot.getValue(Gradestype.class);
                if(gradestype == null || mListGradesType == null || mListGradesType.isEmpty()){
                    return;
                }
                for(int i = 0; i < mListGradesType.size(); i++){
                    if(Objects.equals(gradestype.getId(), mListGradesType.get(i).getId())){
                        mListGradesType.remove(i);
                        break;
                    }
                }
                mGradesTypeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void openDialogUpdateItemGradesType(Gradestype gradestype){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_upload_level);

        TextView titleTextView = dialog.findViewById(R.id.tv_level); // Adjust ID if needed
        if (titleTextView != null) {
            titleTextView.setText("Chỉnh sửa Loại điểm");
        }

        // Find the "Thêm trình độ" button and set new text
        Button uploadButton = dialog.findViewById(R.id.btn_upload_level); // Adjust ID if needed
        if (uploadButton != null) {
            uploadButton.setText("CHỈNH SỬA LOẠI ĐIỂM");
        }
        Button cancelButton = dialog.findViewById(R.id.btn_cancel_upload_level);
        EditText edit_text_grade = dialog.findViewById(R.id.edit_text_level);
        // Tìm Spinner và thiết lập giá trị
        Spinner spinner_weight = dialog.findViewById(R.id.spinner_weight);
        spinner_weight.setVisibility(View.VISIBLE);
        Integer[] weights = {1, 2, 3}; // Các giá trị hệ số
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                dialog.getContext(),
                android.R.layout.simple_spinner_item,
                weights
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_weight.setAdapter(adapter);
        // Thiết lập chiều rộng và chiều cao cho Dialog
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        edit_text_grade.setText(gradestype.getTen_loai_diem());
        spinner_weight.setSelection(gradestype.getHe_so()-1);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setCancelable(false);
                builder.setView(R.layout.progress_layout);
                AlertDialog sdialog = builder.create();
                sdialog.show();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("GRADESTYPE");

                String new_name_grade = edit_text_grade.getText().toString().trim();
                int so_credit = Integer.parseInt(spinner_weight.getSelectedItem().toString().trim());
                gradestype.setTen_loai_diem(new_name_grade);
                gradestype.setHe_so(so_credit);
                myRef.child(gradestype.getId()).updateChildren(gradestype.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        sdialog.dismiss();
                        new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Chỉnh sửa thành công")
                                .setContentText("Đã chỉnh sửa Loại điểm")
                                .setConfirmText("OK")
                                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                .show();
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    private void onClickDeleteData(Gradestype gradestype){
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Không thể xóa điểm")
                .setContentText("Loại điểm này không thể xóa")
                .setConfirmText("OK")
                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                .show();
//        // Hiển thị SweetAlertDialog xác nhận xóa
//        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
//                .setTitleText("Xác nhận xóa")
//                .setContentText("Bạn có chắc chắn muốn xóa loại điểm này không?")
//                .setConfirmText("Xóa")
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//                        builder.setCancelable(false);
//                        builder.setView(R.layout.progress_layout);
//                        AlertDialog dialog = builder.create();
//                        dialog.show();
//                        // Xác nhận xóa - thực hiện xóa trong Firebase
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("GRADESTYPE");
//                        reference.child(gradestype.getId()) // Thay "id_classroom" bằng ID lớp học bạn muốn xóa
//                                .removeValue()
//                                .addOnCompleteListener(task -> {
//                                    dialog.dismiss();
//                                    if (task.isSuccessful()) {
//                                        sDialog
//                                                .setTitleText("Đã xóa!")
//                                                .setContentText("Loại điểm đã được xóa.")
//                                                .setConfirmText("OK")
//                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                    @Override
//                                                    public void onClick(SweetAlertDialog sDialog) {
//                                                        sDialog.dismiss();
//                                                    }
//                                                })
//                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//
//
//                                    } else {
//                                        sDialog
//                                                .setTitleText("Lỗi!")
//                                                .setContentText("Không thể xóa loại điểm.")
//                                                .setConfirmText("OK")
//                                                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
//                                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
//                                    }
//                                });
//                    }
//                })
//                .setCancelButton("Hủy", SweetAlertDialog::dismiss)
//                .show();
    }
    private void initUi(View view){
        fab_gradestype = view.findViewById(R.id.fab_gradestype);
        recGrade = view.findViewById(R.id.recyclerview);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recGrade.setLayoutManager(linearLayoutManager);

        recGrade.setAdapter(mGradesTypeAdapter);
    }

    private void initListener(){
        fab_gradestype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUploadGradeType();
            }
        });
        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListenerHome != null) {
                    mListenerHome.onFragmentAction(R.id.nav_home);
                }
            }
        });
    }
}