package com.example.app_quanly_hocsinh_sinhvien;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUpActivity extends AppCompatActivity {
    private TextView  loginRedirect;
    private EditText signup_email, signup_password, signup_confirm_password;
    private Button signup_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUI();
        initListener();
    }

    private void initListener() {
        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onClickSignUp();
            }
        });
    }

    private void onClickSignUp() {
        String str_email = signup_email.getText().toString().trim();
        String str_password = signup_password.getText().toString().trim();
        String str_confirm_password = signup_confirm_password.getText().toString().trim();

        if (str_email.isEmpty()) {
            new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Thiếu email")
                    .setContentText("Vui lòng nhập email để đăng ký")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        signup_email.requestFocus(); // Đưa con trỏ đến trường email
                    })
                    .show();
        } else if (str_password.isEmpty()) {
            new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Thiếu mật khẩu")
                    .setContentText("Vui lòng nhập mật khẩu")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        signup_password.requestFocus(); // Đưa con trỏ đến trường mật khẩu
                    })
                    .show();
        } else if (str_confirm_password.isEmpty()) {
            new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Thiếu xác nhận mật khẩu")
                    .setContentText("Vui lòng nhập xác nhận mật khẩu")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        signup_confirm_password.requestFocus(); // Đưa con trỏ đến trường xác nhận mật khẩu
                    })
                    .show();
        } else if (!str_password.equals(str_confirm_password)) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Xác nhận mật khẩu thất bại")
                    .setContentText("Hãy xác nhận lại mật khẩu!!!")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        signup_confirm_password.requestFocus(); // Đưa con trỏ đến trường xác nhận mật khẩu
                    })
                    .show();
        } else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(str_email, str_password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Đăng kí thành công")
                                    .setContentText("Studify - Quản lý học sinh sinh viên")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(sDialog -> {
                                        sDialog.dismissWithAnimation();
                                        goToLoginActivity(); // Tách việc chuyển activity ra thành hàm riêng
                                    })
                                    .show();
                        }else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            // Lỗi email đã tồn tại
                            new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Email đã tồn tại")
                                    .setContentText("Email này đã được đăng ký. Vui lòng sử dụng email khác.")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(sDialog -> {
                                        sDialog.dismissWithAnimation();
                                        signup_email.requestFocus(); // Đưa con trỏ về trường email
                                    })
                                    .show();
                        } else {
                            new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Đăng kí thất bại")
                                    .setContentText("Xin vui lòng thử lại sau")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(sDialog -> {
                                        sDialog.dismissWithAnimation();
                                    })
                                    .show();
                        }
                    });
        }
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finishAffinity(); // Đóng tất cả các activity trước đó
    }


    private void initUI(){
        loginRedirect = findViewById(R.id.loginRedirectText);
        signup_email = findViewById(R.id.signup_email);
        signup_password = findViewById(R.id.signup_password);
        signup_confirm_password = findViewById(R.id.signup_confirm_password);
        signup_button = findViewById(R.id.signup_button);
    }
}