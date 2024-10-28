package com.example.app_quanly_hocsinh_sinhvien.ui;

import static com.example.app_quanly_hocsinh_sinhvien.MainActivity.role_student;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ChangePasswordFragment extends Fragment {
    private View mView;
    private EditText edt_password_present, edt_new_password, edt_confirm_new_password;
    private Button btn_change_password;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_change_password, container, false);

        initUi(mView);
        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChangePassword();
            }
        });
        return mView;
    }

    // Phương thức ánh xạ UI
    private void initUi(View view) {
        edt_password_present = mView.findViewById(R.id.edt_password_present);
        edt_new_password = mView.findViewById(R.id.edt_new_password);
        edt_confirm_new_password = mView.findViewById(R.id.edt_confirm_new_password);
        btn_change_password = mView.findViewById(R.id.btn_change_password);
    }


    private void onClickChangePassword() {
        String str_password = edt_password_present.getText().toString().trim();
        String str_new_password = edt_new_password.getText().toString().trim();
        String str_confirm_new_password = edt_confirm_new_password.getText().toString().trim();

        if (str_password.isEmpty()) {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Thiếu email")
                    .setContentText("Vui lòng nhập email hiện tại")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        edt_password_present.requestFocus(); // Đưa con trỏ đến trường email
                    })
                    .show();
        } else if (str_new_password.isEmpty()) {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Thiếu mật khẩu mới")
                    .setContentText("Vui lòng nhập mật khẩu mới")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        edt_new_password.requestFocus(); // Đưa con trỏ đến trường mật khẩu
                    })
                    .show();
        }else if(str_new_password.length() < 8){
            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Mật khẩu mới không hợp lệ")
                    .setConfirmText("Mật khẩu phải lớn hơn 8 kí tự")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        edt_new_password.requestFocus(); // Đưa con trỏ đến trường email
                    })
                    .show();
        } else if (str_confirm_new_password.isEmpty()) {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Thiếu xác nhận mật khẩu")
                    .setContentText("Vui lòng nhập xác nhận mật khẩu")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        edt_confirm_new_password.requestFocus(); // Đưa con trỏ đến trường xác nhận mật khẩu
                    })
                    .show();
        } else if (!str_new_password.equals(str_confirm_new_password)) {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Xác nhận mật khẩu mới thất bại")
                    .setContentText("Hãy xác nhận lại mật khẩu!!!")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        edt_confirm_new_password.requestFocus(); // Đưa con trỏ đến trường xác nhận mật khẩu
                    })
                    .show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), str_password);

            // Xác thực lại người dùng
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            user.updatePassword(str_new_password)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText("Đổi mật khẩu thành công")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                                        .show();
                                            }
                                        }
                                    });
                        } else {
                            // Mật khẩu không đúng
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Lỗi")
                                    .setContentText("Mật khẩu hiện tại không đúng. Vui lòng thử lại.")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                    .show();
                        }
                    });
        }
    }
}