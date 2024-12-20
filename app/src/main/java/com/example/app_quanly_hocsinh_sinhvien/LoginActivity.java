package com.example.app_quanly_hocsinh_sinhvien;

import static com.example.app_quanly_hocsinh_sinhvien.MainActivity.role_lecturer;
import static com.example.app_quanly_hocsinh_sinhvien.MainActivity.role_student;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    private TextView signupRedirect;
    private EditText login_email, login_password;
    private Button login_button;
    private TextView tv_forgot_password;

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
        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickForgotPassword();
            }
        });
    }



    private void onClickLogIn() {
        String str_email = login_email.getText().toString().trim();
        String str_password = login_password.getText().toString().trim();

        if (str_email.isEmpty()) {
            // Hiển thị thông báo thiếu email
            showAlertDialog("Thiếu email", "Vui lòng nhập email để đăng ký", login_email);
        } else if (str_password.isEmpty()) {
            // Hiển thị thông báo thiếu mật khẩu
            showAlertDialog("Thiếu mật khẩu", "Vui lòng nhập mật khẩu", login_password);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(str_email, str_password)
                    .addOnCompleteListener(this, task -> {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công, lấy thông tin vai trò từ Firestore
                            String userId = auth.getCurrentUser().getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            db.collection("users").document(userId).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            // Lấy vai trò người dùng
                                            String userRole = documentSnapshot.getString("role");
                                            if(userRole == null){
                                                Map<String, Object> user = new HashMap<>();
                                                user.put("email", str_email);
                                                user.put("role", role_student);

                                                //Cập nhật vào Firestore
                                                db.collection("users").document(userId).set(user, SetOptions.merge());

                                                // Lưu vai trò vào SharedPreferences
                                                getSharedPreferences("USER_PREFS", MODE_PRIVATE)
                                                        .edit()
                                                        .putString("userRole", role_student)
                                                        .apply();
                                            }else {
                                                // Lưu vai trò vào SharedPreferences
                                                getSharedPreferences("USER_PREFS", MODE_PRIVATE)
                                                        .edit()
                                                        .putString("userRole", userRole)
                                                        .apply();
                                            }
                                            // Hiển thị thông báo thành công và chuyển đến MainActivity
                                            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("Đăng nhập thành công")
                                                    .setContentText("Chào mừng trở lại!")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(sDialog -> {
                                                        sDialog.dismissWithAnimation();
                                                        goToMainActivity();
                                                    })
                                                    .show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Xử lý lỗi khi không lấy được vai trò
                                        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Lỗi")
                                                .setContentText("Không thể lấy vai trò người dùng")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                                .show();
                                    });
                        } else {
                            // Đăng nhập thất bại
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

    // Hàm hiển thị hộp thoại cảnh báo
    private void showAlertDialog(String title, String message, EditText focusField) {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .setConfirmText("OK")
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    focusField.requestFocus(); // Đưa con trỏ đến trường cần nhập
                })
                .show();
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
        tv_forgot_password = findViewById(R.id.tv_forgot_password);
    }

    private void onClickForgotPassword() {
        // Tạo hộp thoại nhập email
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quên mật khẩu");

        // Tạo một EditText để người dùng nhập email
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("Nhập email của bạn");
        builder.setView(input);

        // Nút xác nhận
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String emailAddress = input.getText().toString().trim();

            if (!emailAddress.isEmpty()) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Thành công")
                                        .setContentText("Email đặt lại mật khẩu đã được gửi!")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                        .show();
                            } else {
                                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Lỗi")
                                        .setContentText("Không thể gửi email đặt lại mật khẩu. Vui lòng thử lại.")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                        .show();
                            }
                        });
            } else {
                // Hiển thị thông báo nếu người dùng chưa nhập email
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Thiếu email")
                        .setContentText("Vui lòng nhập email để đặt lại mật khẩu!")
                        .setConfirmText("OK")
                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show();
            }
        });

        // Nút hủy
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        // Hiển thị hộp thoại
        builder.show();
    }

}