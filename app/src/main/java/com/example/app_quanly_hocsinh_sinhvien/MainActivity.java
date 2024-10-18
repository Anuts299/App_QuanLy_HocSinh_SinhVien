package com.example.app_quanly_hocsinh_sinhvien;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.app_quanly_hocsinh_sinhvien.ui.GradestypeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.InfoFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.LevelFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.UserFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ImageView img_avatar;
    private TextView tv_name, tv_email;
    private NavigationView mNavigationView;

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

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            mNavigationView.setCheckedItem(R.id.nav_home);
        }
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
        if (item.getItemId() == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (item.getItemId() == R.id.nav_user) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserFragment()).commit();
        } else if (item.getItemId() == R.id.nav_gradestype) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GradestypeFragment()).commit();
        } else if (item.getItemId() == R.id.nav_info) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InfoFragment()).commit();
        } else if (item.getItemId() == R.id.nav_logout) {
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
    private void showUserInformation(){
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
}