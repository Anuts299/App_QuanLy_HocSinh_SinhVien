package com.example.app_quanly_hocsinh_sinhvien.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.FragmentActionListener;
import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.student_manage.Student;
import com.example.app_quanly_hocsinh_sinhvien.userrole_manage.DetailFragment;
import com.example.app_quanly_hocsinh_sinhvien.userrole_manage.UploadFragment;
import com.example.app_quanly_hocsinh_sinhvien.userrole_manage.UserModel;
import com.example.app_quanly_hocsinh_sinhvien.userrole_manage.UserRoleAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class UserRoleFragment extends Fragment {
    private TextView breadcrumb_home, tv_display_results;
    private FloatingActionButton fab_user_role;
    private RecyclerView recy_userrole;

    private FirebaseFirestore db;
    private UserRoleAdapter adapter;
    private List<UserModel> userList = new ArrayList<>();

    private SearchView searchView;
    private Spinner spinner_filter_role;
    private DatabaseReference databaseReference;
    private List<UserModel> mListUser;


    private FragmentActionListener mListenerHome;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActionListener) {
            mListenerHome = (FragmentActionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentActionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_role, container, false);
        mListUser = new ArrayList<>();
        initUi(view);
        initListener();
        loadUserData();
        searchItemStudent();
        return view;
    }

    private void initUi(View view){
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        fab_user_role = view.findViewById(R.id.fab_user_role);
        recy_userrole = view.findViewById(R.id.recyclerview);
        searchView = view.findViewById(R.id.searchUser);
        tv_display_results = view.findViewById(R.id.tv_display_results);
        spinner_filter_role = view.findViewById(R.id.spinner_filter_role);
        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());


        // Thêm giá trị vào Spinner
        List<String> roleList = new ArrayList<>();
        roleList.add("Tất cả");
        roleList.add("Giảng viên");
        roleList.add("Quản trị viên");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                roleList
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_filter_role.setAdapter(spinnerAdapter);
        spinner_filter_role.setSelection(0);

        recy_userrole.setLayoutManager(linearLayoutManager);
        // Thiết lập RecyclerView và Adapter
        adapter = new UserRoleAdapter(userList, userModel -> openDetailFragment(userModel));
        recy_userrole.setAdapter(adapter);
    }

    private void initListener(){
        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListenerHome != null) {
                    mListenerHome.onFragmentAction(R.id.nav_home);
                }
            }
        });
        setupSpinnerListener();
        fab_user_role.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new UploadFragment());
            }
        });
    }
    private void loadUserData() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserModel user = document.toObject(UserModel.class);
                            userList.add(user);
                        }
                        // Gán dữ liệu vào mListUser
                        mListUser = new ArrayList<>(userList);
                        filterUsersByRole("Tất cả");
                    } else {
                        Log.e("UserRoleFragment", "Lấy dữ liệu thất bại.", task.getException());
                    }
                });
    }

    private void updateDisplayedItemCount(){
        int itemCount = adapter.getItemCount();
        tv_display_results.setText("Kết quả: "+itemCount);
    }
    private void searchItemStudent(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
    }
    private void searchList(String text) {
        ArrayList<UserModel> searchList = new ArrayList<>();
        for (UserModel userModel : mListUser) {
            if (userModel.getName().toLowerCase().contains(text.toLowerCase()) ||
                    userModel.getEmail().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(userModel);
            }
        }
        adapter.searchUserList(searchList);
        updateDisplayedItemCount();
    }

    private void setupSpinnerListener() {
        spinner_filter_role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRole = parent.getItemAtPosition(position).toString();

                // Lọc danh sách người dùng theo role
                filterUsersByRole(selectedRole);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì nếu không có gì được chọn
            }
        });
    }
    private void filterUsersByRole(String role) {
        ArrayList<UserModel> filteredList = new ArrayList<>();
        if (role.equals("Tất cả")){
            filteredList.addAll(mListUser);
        }
        for (UserModel user : userList) {
            if (user.getRole() != null && user.getRole().equalsIgnoreCase(role)) {
                filteredList.add(user);
            }
        }

        // Cập nhật RecyclerView với danh sách lọc
        adapter.searchUserList(filteredList);
        updateDisplayedItemCount();
    }
    private void openDetailFragment(UserModel userModel){
        DetailFragment detailFragment = new DetailFragment();

        Bundle bundle = new Bundle();
        bundle.putString("ten_tai_khoan", userModel.getName());
        bundle.putString("email", userModel.getEmail());
        bundle.putString("vai_tro", userModel.getRole());
        detailFragment.setArguments(bundle);

        // Chuyển sang DetailFragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();

    }
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}