package com.example.app_quanly_hocsinh_sinhvien.ui;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.app_quanly_hocsinh_sinhvien.MainActivity;
import com.example.app_quanly_hocsinh_sinhvien.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserFragment extends Fragment {
    private View mView;
    public ImageView img_avatar;
    private EditText edt_full_name, edt_email, edt_name_role;
    private Button btn_update, btn_update_email;
    public Uri mUri;
    private MainActivity mMainActivity;
    private CircularProgressIndicator progressIndicator;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_user, container, false);
        initUi();
        setUserInformation();
        mMainActivity = (MainActivity) getActivity();
        registerGalleryLauncher(); // Đăng ký launcher
        initListener();
        return mView;
    }

    private void initUi() {
        img_avatar = mView.findViewById(R.id.img_avatar);
        edt_full_name = mView.findViewById(R.id.edt_full_name);
        edt_email = mView.findViewById(R.id.edt_email);
        edt_name_role = mView.findViewById(R.id.edt_name_role);
        btn_update = mView.findViewById(R.id.btn_update);
        btn_update_email = mView.findViewById(R.id.btn_update_email);
    }

    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        edt_email.setText(user.getEmail());

        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_account_def).into(img_avatar);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        String name_user = documentSnapshot.getString("name");
                        edt_name_role.setText(role != null ? role : "Unknown");
                        edt_full_name.setText(name_user != null ? name_user : "Unknown");
                    } else {
                        edt_name_role.setText("Unknown");
                        edt_full_name.setText("Unknown");
                    }
                })
                .addOnFailureListener(e -> {
                    edt_name_role.setText("Error loading role");
                    Log.e("setUserInformation", "Error fetching user role", e);
                });
    }

    private void registerGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        if (selectedImage != null) {
                            mUri = selectedImage;
                            Glide.with(this).load(selectedImage).into(img_avatar);
                            Log.d(TAG, "Image selected: " + selectedImage.toString());
                        }
                    }
                }
        );
    }

    private void initListener() {
        img_avatar.setOnClickListener(v -> onClickRequestPermission());
        btn_update.setOnClickListener(v -> onClickUpdateProfile());
        btn_update_email.setOnClickListener(v -> onClickUpdateEmail());
    }

    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (requireActivity().checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                requestPermissionWithExplanation(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android 6.0+
            if (requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                requestPermissionWithExplanation(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            openGallery(); // API < 23 không cần xin quyền runtime
        }
    }

    private void requestPermissionWithExplanation(String permission) {
        if (shouldShowRequestPermissionRationale(permission)) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Yêu cầu quyền")
                    .setMessage("Ứng dụng cần quyền để truy cập thư viện ảnh. Vui lòng cấp quyền.")
                    .setPositiveButton("Đồng ý", (dialog, which) -> requestPermissions(new String[]{permission}, 1000))
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            requestPermissions(new String[]{permission}, 1000);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void onClickUpdateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String str_full_name = edt_full_name.getText().toString().trim();
        if (str_full_name.isEmpty() || mUri == null) {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Thiếu thông tin")
                    .setContentText("Xin vui lòng điền đủ thông tin và chọn ảnh.")
                    .setConfirmText("OK")
                    .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
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
                .addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Thành công")
                                .setContentText("Cập nhật thông tin thành công")
                                .setConfirmText("OK")
                                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                .show();
                        mMainActivity.showUserInformation();
                    }
                });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = user.getUid();

        db.collection("users").document(userId)
                .update("name", str_full_name)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreUpdate", "Tên người dùng đã được cập nhật trong Firestore.");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Không thể cập nhật tên trong Firestore: ", e);
                });

    }

    private void onClickUpdateEmail() {
        String str_new_email = edt_email.getText().toString().trim();
        if (str_new_email.isEmpty()) {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Thiếu thông tin")
                    .setContentText("Xin vui lòng điền đủ thông tin")
                    .setConfirmText("OK")
                    .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                    .show();
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.verifyBeforeUpdateEmail(str_new_email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Xác minh email")
                                    .setContentText("Email xác minh đã được gửi. Vui lòng xác minh trước khi cập nhật.")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                    .show();
                        }
                    });
        }
    }
}
