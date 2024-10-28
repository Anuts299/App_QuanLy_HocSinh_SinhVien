package com.example.app_quanly_hocsinh_sinhvien.ui;

import static android.content.ContentValues.TAG;
import static com.example.app_quanly_hocsinh_sinhvien.MainActivity.MY_REQUEST_CODES;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Magnifier;

import com.bumptech.glide.Glide;
import com.example.app_quanly_hocsinh_sinhvien.MainActivity;
import com.example.app_quanly_hocsinh_sinhvien.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UserFragment extends Fragment {
    private View mView;
    private ImageView img_avatar;
    private EditText edt_full_name,edt_email, edt_name_role;
    private Button btn_update, btn_update_email;
    private Uri mUri;
    private MainActivity mMainActivity;
    private CircularProgressIndicator progressIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_user, container, false);
        initUi();
        setUserInformation();
        mMainActivity = (MainActivity) getActivity();
        initListener();
        return mView;
    }



    private void initUi(){
        img_avatar = mView.findViewById(R.id.img_avatar);
        edt_full_name = mView.findViewById(R.id.edt_full_name);
        edt_email = mView.findViewById(R.id.edt_email);
        edt_name_role = mView.findViewById(R.id.edt_name_role);
        btn_update = mView.findViewById(R.id.btn_update);
        btn_update_email = mView.findViewById(R.id.btn_update_email);
    }
    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }else{
            edt_full_name.setText(user.getDisplayName());
            edt_email.setText(user.getEmail());
            Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_account_def).into(img_avatar);

            // Lấy vai trò từ Firestore
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String role = document.getString("role"); // Lấy vai trò
                                    if (role != null) {
                                        edt_name_role.setText(role); // Hiển thị vai trò
                                    } else {
                                        edt_name_role.setText(""); // Nếu không có vai trò, để trống
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void initListener(){
        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermission();
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateProfile();
            }
        });
        btn_update_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateEmail();
            }
        });
    }

    private void onClickRequestPermission() {
        mMainActivity = (MainActivity) getActivity();

        if(mMainActivity == null){
            return;
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            mMainActivity.openGallery();
            return;
        }
        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            mMainActivity.openGallery();
        }else{
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permissions, MY_REQUEST_CODES);
        }
    }

    public void setBitmapImageView(Bitmap bitmapImageView){
        img_avatar.setImageBitmap(bitmapImageView);
    }


    public void setUri(Uri mUri) {
        this.mUri = mUri;
    }

    private void onClickUpdateProfile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        String str_full_name = edt_full_name.getText().toString().trim();
        if(str_full_name.isEmpty()){
            new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Thiếu thông tin")
                    .setContentText("Xin vui lòng điền đủ thông tin")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                    })
                    .show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(str_full_name)
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Thành công")
                                    .setContentText("Cập nhật thông tin thành công")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(sDialog -> {
                                        sDialog.dismissWithAnimation();
                                    })
                                    .show();
                            mMainActivity.showUserInformation();
                        }
                    }
                });
    }

    private void onClickUpdateEmail() {
        String str_new_email = edt_email.getText().toString().trim();
        if(str_new_email.isEmpty()){
            new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Thiếu thông tin")
                    .setContentText("Xin vui lòng điền đủ thông tin")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                    })
                    .show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Gửi email xác minh tới email mới
        user.verifyBeforeUpdateEmail(str_new_email)
                .addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        // Nếu email xác minh đã được gửi thành công
                        new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Xác minh email")
                                .setContentText("Một email xác minh đã được gửi đến địa chỉ email mới. Vui lòng xác minh trước khi cập nhật.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(sDialog -> sDialog.dismissWithAnimation())
                                .show();
                    } else {
                        // Nếu có lỗi xảy ra khi gửi email xác minh
                        new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Thất bại")
                                .setContentText("Không thể gửi email xác minh: " + task.getException().getMessage())
                                .setConfirmText("OK")
                                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                .show();
                    }
                });
    }

}