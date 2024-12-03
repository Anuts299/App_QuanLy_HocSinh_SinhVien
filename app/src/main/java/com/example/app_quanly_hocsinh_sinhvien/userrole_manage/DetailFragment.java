package com.example.app_quanly_hocsinh_sinhvien.userrole_manage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.UserRoleFragment;
import com.github.clans.fab.FloatingActionButton;


public class DetailFragment extends Fragment {

    private TextView breadcrumb_user, tv_de_name_user, tv_de_email_user, tv_de_name_role;
    private FloatingActionButton disableButtonUser, deleteButtonUser, editButtonUser;
    private String email = "", role = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_userrole, container, false);
        initUi(view);
        Bundle bundle = getArguments();
        if(bundle != null){
            tv_de_name_user.setText(bundle.getString("ten_tai_khoan"));
            tv_de_email_user.setText(bundle.getString("email"));
            tv_de_name_role.setText(bundle.getString("vai_tro"));
            email = bundle.getString("email");
            role = bundle.getString("vai_tro");
        }
        initListener();
        return view;
    }
    private void initUi(View view){
        breadcrumb_user = view.findViewById(R.id.breadcrumb_user);
        tv_de_name_user = view.findViewById(R.id.tv_de_name_user);
        tv_de_email_user = view.findViewById(R.id.tv_de_email_user);
        tv_de_name_role = view.findViewById(R.id.tv_de_name_role);
        disableButtonUser = view.findViewById(R.id.disableButtonUser);
        deleteButtonUser = view.findViewById(R.id.deleteButtonUser);
        editButtonUser = view.findViewById(R.id.editButtonUser);
    }

    private void initListener(){
        breadcrumb_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new UserRoleFragment());
            }
        });
        deleteButtonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        disableButtonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!role.equals("Quản trị viên")){

                }else{

                }
            }
        });
        editButtonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}