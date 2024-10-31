package com.example.app_quanly_hocsinh_sinhvien.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.level_manage.Level;
import com.example.app_quanly_hocsinh_sinhvien.level_manage.LevelAdapter;
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

public class LevelFragment extends Fragment {

    FloatingActionButton fab_level;
    private RecyclerView recLevel;
    private LevelAdapter mLevelAdapter;
    private List<Level> mListLevel;
    private TextView breadcrumb_home;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_level, container, false);
        mListLevel = new ArrayList<>();
        mLevelAdapter = new LevelAdapter(mListLevel, new LevelAdapter.IClickListener() {
            @Override
            public void onClickUpdateItem(Level level) {
                openDialogUpdateItemLevel(level);
            }

            @Override
            public void onClickDeleteItem(Level level) {
                onClickDeleteData(level);
            }
        });
        initUi(view);
        initListener();


        getListLevelFromRealtimeDatabase();
        return view;
    }

    private void initUi(View view){
        fab_level = view.findViewById(R.id.fab_level);
        recLevel = view.findViewById(R.id.recyclerview);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recLevel.setLayoutManager(linearLayoutManager);

        recLevel.setAdapter(mLevelAdapter);

    }

    private void initListener(){
        fab_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUploadLevel();
            }
        });
        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new HomeFragment());
            }
        });
    }

    private void onClickUploadLevel() {
        // Tạo Dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_upload_level);

        // Thiết lập chiều rộng và chiều cao cho Dialog
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Lấy EditText từ layout
        EditText editTextLevel = dialog.findViewById(R.id.edit_text_level);

        // Thêm sự kiện cho nút Thêm
        Button buttonAdd = dialog.findViewById(R.id.btn_upload_level);
        buttonAdd.setOnClickListener(v -> {
                String name_level = editTextLevel.getText().toString().trim();
            if (!name_level.isEmpty()) {
                Level level1 = new Level(null, name_level);
                UploadLevel(level1);
                dialog.dismiss(); // Đóng dialog
            } else {
                new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Thiếu thông tin")
                        .setContentText("Vui lòng nhập đủ thông tin")
                        .setConfirmText("OK")
                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show();
            }
        });

        // Thêm sự kiện cho nút Hủy
        Button buttonCancel = dialog.findViewById(R.id.btn_cancel_upload_level);
        buttonCancel.setOnClickListener(v -> dialog.dismiss()); // Đóng dialog

        dialog.show(); // Hiển thị dialog
    }
    private void UploadLevel(Level level){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("LEVEL");
        Query checkLevel = myRef.orderByChild("ten_trinh_do").equalTo(level.getTen_trinh_do());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        checkLevel.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialog.dismiss();
                if (snapshot.exists()) {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Trình độ đã tồn tại")
                            .setContentText("Tên trình độ này đã có trong hệ thống.")
                            .setConfirmText("OK")
                            .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                            .show();
                }else {
                    // Sử dụng push() để Firebase tự tạo ID
                    String key = myRef.push().getKey();
                    level.setId(key);
                    myRef.child(key).setValue(level, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Thêm trình độ thất bại")
                                        .setContentText("Lỗi: " + error.getMessage())
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                        .show();
                            } else {
                                new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Thêm trình độ thành công")
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
                        .setTitleText("Thêm trình độ thất bại")
                        .setContentText("Lỗi: " + error.getMessage())
                        .setConfirmText("OK")
                        .show();
            }
        });
    }
    private void getListLevelFromRealtimeDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("LEVEL");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Level level = snapshot.getValue(Level.class);
                if(level != null){
                    mListLevel.add(level);
                    mLevelAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Level level = snapshot.getValue(Level.class);
                if(level == null ||mListLevel == null || mListLevel.isEmpty()){
                    return;
                }

                for(int i = 0; i < mListLevel.size(); i++){
                    if(Objects.equals(level.getId(), mListLevel.get(i).getId())){
                        mListLevel.set(i, level);
                        break;
                    }
                }
                mLevelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Level level = snapshot.getValue(Level.class);
                if(level == null ||mListLevel == null || mListLevel.isEmpty()){
                    return;
                }
                for(int i = 0; i < mListLevel.size(); i++){
                    if(Objects.equals(level.getId(), mListLevel.get(i).getId())){
                        mListLevel.remove(mListLevel.get(i));
                        break;
                    }
                }
                mLevelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void openDialogUpdateItemLevel(Level level){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_upload_level);

        TextView titleTextView = dialog.findViewById(R.id.tv_level); // Adjust ID if needed
        if (titleTextView != null) {
            titleTextView.setText("Chỉnh sửa trình độ");
        }

        // Find the "Thêm trình độ" button and set new text
        Button uploadButton = dialog.findViewById(R.id.btn_upload_level); // Adjust ID if needed
        if (uploadButton != null) {
            uploadButton.setText("CHỈNH SỬA");
        }
        Button cancelButton = dialog.findViewById(R.id.btn_cancel_upload_level);
        EditText edit_text_level = dialog.findViewById(R.id.edit_text_level);

        // Thiết lập chiều rộng và chiều cao cho Dialog
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        edit_text_level.setText(level.getTen_trinh_do());
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
                DatabaseReference myRef = database.getReference("LEVEL");

                String new_name_level = edit_text_level.getText().toString().trim();
                level.setTen_trinh_do(new_name_level);
                myRef.child(level.getId()).updateChildren(level.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        sdialog.dismiss();
                        new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Chỉnh sửa thành công")
                                .setContentText("Đã chỉnh sửa tên trình độ")
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

    private void onClickDeleteData(Level level){
        // Hiển thị SweetAlertDialog xác nhận xóa
        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Xác nhận xóa")
                .setContentText("Bạn có chắc chắn muốn xóa trình độ học này không?")
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
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("LEVEL");
                        reference.child(level.getId()) // Thay "id_classroom" bằng ID lớp học bạn muốn xóa
                                .removeValue()
                                .addOnCompleteListener(task -> {
                                    dialog.dismiss();
                                    if (task.isSuccessful()) {
                                        sDialog
                                                .setTitleText("Đã xóa!")
                                                .setContentText("Trình độ đã được xóa.")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismiss();
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
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();  // Không dùng addToBackStack(null)
    }



}
