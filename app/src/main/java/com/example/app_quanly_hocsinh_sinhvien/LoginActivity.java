package com.example.app_quanly_hocsinh_sinhvien;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    private TextView signupRedirect;
    private EditText login_email, login_password;
    private Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        initListener();
    }

    private void initListener() {
        signupRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogIn();
            }
        });
    }

    private void onClickLogIn() {
        String str_email = login_email.getText().toString().trim();
        String str_password = login_password.getText().toString().trim();
        if (str_email.isEmpty()) {
            new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Thiếu email")
                    .setContentText("Vui lòng nhập email để đăng ký")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        login_email.requestFocus(); // Đưa con trỏ đến trường email
                    })
                    .show();
        } else if (str_password.isEmpty()) {
            new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Thiếu mật khẩu")
                    .setContentText("Vui lòng nhập mật khẩu")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        login_password.requestFocus(); // Đưa con trỏ đến trường mật khẩu
                    })
                    .show();
        }else{
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(str_email,str_password)
                    .addOnCompleteListener(this,task -> {
                        if(task.isSuccessful()){
                            // Đăng nhập thành công
                            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Đăng nhập thành công")
                                    .setContentText("Chào mừng trở lại!")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(sDialog -> {
                                        sDialog.dismissWithAnimation();
                                        goToMainActivity();

                                    })
                                    .show();
                        }else{
                            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Đăng nhập thất bại")
                                    .setContentText("Email hoặc mật khẩu không chính xác")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                    .show();
                        }
                    });
        }
    }

    private void goToMainActivity() {
        // Chuyển đến trang chính của ứng dụng
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finishAffinity(); // Đóng tất cả các activity trước đó
    }

    private void initUi(){
        signupRedirect = findViewById(R.id.signupRedirectText);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_button = findViewById(R.id.login_button);
    }
}