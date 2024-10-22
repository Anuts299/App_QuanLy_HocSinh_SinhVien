package com.example.app_quanly_hocsinh_sinhvien;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.app_quanly_hocsinh_sinhvien.ui.ChangePasswordFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.ChartFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.GradestypeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.InfoFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.LevelFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.UserFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Arrays;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int MY_REQUEST_CODES =10;
    public static String role_admin = "Quản trị viên hệ thống",
            role_lecturer = "Giảng viên bộ môn",
            role_student = "Sinh viên",
            role_departmentManager = "Quản lý khoa";
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_CHART = 1;
    private static final int FRAGMENT_GRADESTYPE = 2;
    private static final int FRAGMENT_LEVEL = 3;
    private static final int FRAGMENT_USER = 4;
    private static final int FRAGMENT_INFO = 5;
    private static final int FRAGMENT_CHANGE_PASSWORD = 6;

    private int mCurrentFragment = FRAGMENT_HOME;

    final private UserFragment mUserFragment = new UserFragment();

    private DrawerLayout drawerLayout;
    private ImageView img_avatar;
    private TextView tv_name, tv_email;
    private NavigationView mNavigationView;

    final private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if(o.getResultCode() == RESULT_OK){
                Intent intent = o.getData();
                if(intent == null){
                    return;
                }
                Uri uri = intent.getData();
                mUserFragment.setUri(uri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                    mUserFragment.setBitmapImageView(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initUi();

        drawerLayout = findViewById(R.id.drawer_layout);

        mNavigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        replaceFragment(new HomeFragment());
        mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
        showUserInformation();

    }
    private void initUi(){
        mNavigationView = findViewById(R.id.nav_view);
        img_avatar = mNavigationView.getHeaderView(0).findViewById(R.id.img_avatar);
        tv_name = mNavigationView.getHeaderView(0).findViewById(R.id.tv_name);
        tv_email = mNavigationView.getHeaderView(0).findViewById(R.id.tv_email);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Xóa toàn bộ các fragment trong back stack để quay lại HomeFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            // Kiểm tra và thay thế HomeFragment nếu cần
            if (mCurrentFragment != FRAGMENT_HOME) {
                replaceFragment(new HomeFragment());
                mCurrentFragment = FRAGMENT_HOME;
            }
        } else if (id == R.id.nav_user) {
            if(mCurrentFragment != FRAGMENT_USER){
                replaceFragment(mUserFragment);
                mCurrentFragment = FRAGMENT_USER;
            }
        } else if (id == R.id.nav_gradestype) {
            if(mCurrentFragment != FRAGMENT_GRADESTYPE){
                replaceFragment(new GradestypeFragment());
                mCurrentFragment = FRAGMENT_GRADESTYPE;
            }
        } else if (id == R.id.nav_info) {
            if(mCurrentFragment != FRAGMENT_INFO){
                replaceFragment(new InfoFragment());
                mCurrentFragment = FRAGMENT_INFO;
            }
        }else if(id == R.id.nav_chart){
            if(mCurrentFragment != FRAGMENT_CHART){
                replaceFragment(new ChartFragment());
                mCurrentFragment = FRAGMENT_CHART;
            }
        }else if(id == R.id.nav_change_password){
            if(mCurrentFragment != FRAGMENT_CHANGE_PASSWORD){
                replaceFragment(new ChangePasswordFragment());
                mCurrentFragment = FRAGMENT_CHANGE_PASSWORD;
            }
        } else if (id == R.id.nav_logout) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Đăng xuất tài khoản")
                    .setContentText("Xác nhận đăng xuất?")
                    .setConfirmButton("Đồng ý", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();

                            // Đăng xuất tài khoản
                            FirebaseAuth.getInstance().signOut();

                            // Chuyển hướng đến màn hình đăng nhập
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Xóa tất cả các activity trước đó
                            startActivity(intent);
                            finish(); // Kết thúc activity hiện tại
                        }
                    })
                    .setCancelButton("Hủy bỏ", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation(); // Đóng dialog khi nhấn Hủy bỏ
                        }
                    })
                    .show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }
    public void showUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();
        if(name == null){
            tv_name.setVisibility(View.GONE);
        }else{
            tv_name.setVisibility(View.VISIBLE);
            tv_name.setText(name);
        }
        tv_email.setText(email);
        Glide.with(this).load(photoUrl).error(R.drawable.ic_account_def).into(img_avatar);
    }
    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_CODES){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]))
            {
                // Quyền đã bị từ chối vĩnh viễn (người dùng chọn "Don't ask again")
                new AlertDialog.Builder(this)
                        .setMessage("Bạn cần cấp quyền trong phần cài đặt")
                        .setPositiveButton("Đi đến cài đặt", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }else {
                Log.d("PermissionResult", "grantResults: " + Arrays.toString(grantResults));
                new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("Truy cập thất bại")
                        .setContentText("Vui lòng cho phép truy cập")
                        .setConfirmText("OK")
                        .setConfirmClickListener(sDialog -> {
                            sDialog.dismissWithAnimation();

                        })
                        .show();
            }
        }
    }

    public void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Chọn hình ảnh"));
    }
}