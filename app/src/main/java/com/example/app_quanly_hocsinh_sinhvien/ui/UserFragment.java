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

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UserFragment extends Fragment {
    private View mView;
    private ImageView img_avatar;
    private EditText edt_full_name,edt_email, edt_name_role;
    private Button btn_update;
    private Uri mUri;
    private MainActivity mMainActivity;

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
    }
    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }else{
            edt_full_name.setText(user.getDisplayName());
            edt_email.setText(user.getEmail());
            Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_account_def).into(img_avatar);
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
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(str_full_name)
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
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
}