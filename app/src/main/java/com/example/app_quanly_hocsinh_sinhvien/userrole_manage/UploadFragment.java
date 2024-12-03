package com.example.app_quanly_hocsinh_sinhvien.userrole_manage;

import static com.example.app_quanly_hocsinh_sinhvien.MainActivity.role_student;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.SignUpActivity;
import com.example.app_quanly_hocsinh_sinhvien.ui.UserRoleFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UploadFragment extends Fragment {

    private TextView breadcrumb_user;
    private EditText edt_upload_name_user, edt_upload_email_user, edt_upload_password_user;
    private Spinner spinner_name_role;
    private Button btn_upload_user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_user, container, false);
        initUi(view);
        initListener();
        return view;
    }

    private void initUi(View view){
        breadcrumb_user = view.findViewById(R.id.breadcrumb_user);
        edt_upload_name_user = view.findViewById(R.id.edt_upload_name_user);
        edt_upload_email_user = view.findViewById(R.id.edt_upload_email_user);
        edt_upload_password_user = view.findViewById(R.id.edt_upload_password_user);
        spinner_name_role = view.findViewById(R.id.spinner_name_role);
        btn_upload_user = view.findViewById(R.id.btn_upload_user);

        // Thêm giá trị vào Spinner
        List<String> roleList = new ArrayList<>();
        roleList.add("Giảng viên");
        roleList.add("Quản trị viên");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                roleList
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_name_role.setAdapter(spinnerAdapter);
    }

    private void initListener(){
        breadcrumb_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new UserRoleFragment());
            }
        });
        btn_upload_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUploadUser();
            }
        });
    }

    private void onClickUploadUser(){
        String name_user = edt_upload_name_user.getText().toString().trim();
        String email_user = edt_upload_email_user.getText().toString().trim();
        String pass_user = edt_upload_password_user.getText().toString();
        String name_role = spinner_name_role.getSelectedItem().toString().trim();
        if(name_role.isEmpty() || email_user.isEmpty() || name_user.isEmpty() || pass_user.isEmpty()){
            new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Thiếu thông tin")
                    .setContentText("Vui lòng điền đầy đủ thông tin.")
                    .setConfirmText("OK")
                    .show();
            return;
        }
        else if(!isValidEmail(email_user)){
            new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Sai định dạng")
                    .setContentText("Email phải có định dạng abc@gmail.com")
                    .setConfirmText("OK")
                    .show();
            return;
        }else if(pass_user.length() < 8){
            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Mật khẩu không hợp lệ")
                    .setConfirmText("Mật khẩu phải lớn hơn 8 kí tự")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        edt_upload_password_user.requestFocus(); // Đưa con trỏ đến trường email
                    })
                    .show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email_user, pass_user)
                .addOnCompleteListener(requireActivity(), task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        // Xử lý khi đăng ký thành công
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        Map<String, Object> user = new HashMap<>();
                        user.put("email", email_user);
                        user.put("role", name_role);
                        user.put("name", name_user);

                        db.collection("users").document(userId)
                                .set(user)
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Đăng kí thành công")
                                                .setContentText("Tài khoản đã được tạo.")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(sDialog -> {
                                                    sDialog.dismissWithAnimation();
                                                    // Đăng xuất sau khi tạo tài khoản thành công
                                                    FirebaseAuth.getInstance().signOut();
                                                })
                                                .show();
                                    } else {
                                        Log.e("FirestoreError", "Không thể lưu thông tin tài khoản: ", task1.getException());
                                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Đăng kí không thành công")
                                                .setContentText("Không thể lưu thông tin tài khoản. Vui lòng kiểm tra lại.")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                                .show();
                                    }
                                });

                    } else {
                        // Xử lý các ngoại lệ khác nhau
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            // Email đã tồn tại
                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Email đã tồn tại")
                                    .setContentText("Email này đã được đăng ký. Vui lòng sử dụng email khác.")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(sDialog -> {
                                        sDialog.dismissWithAnimation();
                                        edt_upload_email_user.requestFocus();
                                    })
                                    .show();
                        } else {
                            String errorMessage;
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                errorMessage = "Tài khoản người dùng không hợp lệ.";
                            } catch (Exception e) {
                                errorMessage = "Đăng kí thất bại. Vui lòng thử lại sau.";
                            }

                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Đăng kí thất bại")
                                    .setContentText(errorMessage)
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                    .show();
                        }
                    }
                });
    }
    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailPattern);
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}