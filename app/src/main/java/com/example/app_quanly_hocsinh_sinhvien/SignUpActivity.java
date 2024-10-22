package com.example.app_quanly_hocsinh_sinhvien;

import static com.example.app_quanly_hocsinh_sinhvien.MainActivity.role_student;

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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(str_email).matches()) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Email không hợp lệ")
                    .setContentText("Vui lòng nhập email hợp lệ")
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
        }else if(str_password.length() < 8){
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Mật khẩu không hợp lệ")
                    .setConfirmText("Mật khẩu phải lớn hơn 8 kí tự")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        signup_email.requestFocus(); // Đưa con trỏ đến trường email
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
                            // Xử lý khi đăng ký thành công
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            Map<String, Object> user = new HashMap<>();
                            user.put("email", str_email);
                            user.put("role", role_student);

                            db.collection("users").document(userId)
                                    .set(user)
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("Đăng kí thành công")
                                                    .setContentText("Tài khoản sinh viên đã được tạo.")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(sDialog -> {
                                                        sDialog.dismissWithAnimation();
                                                        goToLoginActivity();
                                                    })
                                                    .show();
                                        } else {
                                            Log.e("FirestoreError", "Không thể lưu thông tin tài khoản: ", task1.getException());
                                            new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Lỗi Firestore")
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
                                new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Email đã tồn tại")
                                        .setContentText("Email này đã được đăng ký. Vui lòng sử dụng email khác.")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(sDialog -> {
                                            sDialog.dismissWithAnimation();
                                            signup_email.requestFocus();
                                        })
                                        .show();
                            } else {
                                // In ra logcat chi tiết lỗi
                                Log.e("SignUpError", "Đăng ký thất bại: ", task.getException());
                                // Kiểm tra lỗi cụ thể khác (ví dụ: mật khẩu yếu, định dạng email sai, kết nối mạng)
                                String errorMessage;
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    errorMessage = "Mật khẩu quá yếu. Vui lòng chọn mật khẩu mạnh hơn.";
                                    signup_password.requestFocus();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    errorMessage = "Định dạng email không hợp lệ.";
                                    signup_email.requestFocus();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    errorMessage = "Tài khoản người dùng không hợp lệ.";
                                } catch (Exception e) {
                                    errorMessage = "Đăng kí thất bại. Vui lòng thử lại sau.";
                                }

                                new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Đăng kí thất bại")
                                        .setContentText(errorMessage)
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                        .show();
                            }
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