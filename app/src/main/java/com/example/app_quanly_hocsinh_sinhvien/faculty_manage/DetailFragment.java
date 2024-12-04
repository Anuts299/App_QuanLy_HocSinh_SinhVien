package com.example.app_quanly_hocsinh_sinhvien.faculty_manage;

import static com.example.app_quanly_hocsinh_sinhvien.MainActivity.userRole;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.lecturer_manage.Lecturer;
import com.example.app_quanly_hocsinh_sinhvien.lecturer_manage.LecturerAdapter;
import com.example.app_quanly_hocsinh_sinhvien.ui.FacultiesFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class DetailFragment extends Fragment {

    private TextView tv_de_name_faculty, breadcrumb_home, breadcrumb_faculty, tv_de_format_code, tv_result_lecturer;
    String id = "";
    FloatingActionButton deleteButtonFaculty, editButtonFaculty;
    private RecyclerView recyView_LecturerSm;

    private LecturerAdapter mLecturerAdapter;
    private List<Lecturer> mLecturerList;

    private Map<String, String> idToLevelNameMap = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_faculty, container, false);
        mLecturerList = new ArrayList<>();
        initUi(view);
        Bundle bundle = getArguments();
        if(bundle != null){
            tv_de_name_faculty.setText(bundle.getString("ten_khoa"));
            tv_de_format_code.setText(bundle.getString("ma_dinh_dang"));
            id = bundle.getString("id");
            loadLecturers();
        }
        createIdToLevelNameMap();
        initListener();
        return view;
    }
    private void initUi(View view){
        tv_de_name_faculty = view.findViewById(R.id.tv_de_name_faculty);
        tv_de_format_code = view.findViewById(R.id.tv_de_format_code);
        deleteButtonFaculty = view.findViewById(R.id.deleteButtonFaculty);
        editButtonFaculty = view.findViewById(R.id.editButtonFaculty);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_faculty = view.findViewById(R.id.breadcrumb_faculty);
        tv_result_lecturer = view.findViewById(R.id.tv_result_lecturer);
        recyView_LecturerSm = view.findViewById(R.id.recyView_LecturerSm);

        mLecturerAdapter = new LecturerAdapter(mLecturerList, null, idToLevelNameMap, null);
        mLecturerAdapter.setSimpleMode(true);
        recyView_LecturerSm.setLayoutManager(new LinearLayoutManager(getContext()));
        recyView_LecturerSm.setAdapter(mLecturerAdapter);
    }

    private void initListener(){
        deleteButtonFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userRole.equals("Quản trị viên")){
                    // Hiển thị SweetAlertDialog xác nhận xóa
                    new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Xác nhận xóa")
                            .setContentText("Bạn có chắc chắn muốn xóa khoa này không?")
                            .setConfirmText("Xóa")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                    builder.setCancelable(false);
                                    builder.setView(R.layout.progress_layout);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    // Xác nhận xóa - thực hiện xóa trong Firebase
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("FACULTY");
                                    reference.child(id)
                                            .removeValue()
                                            .addOnCompleteListener(task -> {
                                                dialog.dismiss();
                                                if (task.isSuccessful()) {
                                                    sDialog
                                                            .setTitleText("Đã xóa!")
                                                            .setContentText("Khoa đã được xóa.")
                                                            .setConfirmText("OK")
                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                @Override
                                                                public void onClick(SweetAlertDialog sDialog) {
                                                                    sDialog.dismiss();
                                                                    switchFragment(new FacultiesFragment());
                                                                }
                                                            })
                                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                }else {
                                                    sDialog
                                                            .setTitleText("Lỗi!")
                                                            .setContentText("Không thể xóa khoa.")
                                                            .setConfirmText("OK")
                                                            .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                                }
                                            });
                                }
                            })
                            .setCancelButton("Hủy", SweetAlertDialog::dismiss)
                            .show();
                }else{
                    showAccessDeniedAlert();
                }
            }
        });

            editButtonFaculty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(userRole.equals("Quản trị viên")){
                    Bundle bundle = new Bundle();
                    String tenkhoa = tv_de_name_faculty.getText().toString().trim();
                    String madinhdang = tv_de_format_code.getText().toString().trim();
                    bundle.putString("ten_khoa",tenkhoa);
                    bundle.putString("ma_dinh_dang", madinhdang);
                    bundle.putString("id", id);

                    UpdateFragment updateFragment = new UpdateFragment();
                    updateFragment.setArguments(bundle);

                    try {
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, updateFragment)
                                .addToBackStack(null)
                                .commit();
                        Log.d("EditButton", "Fragment replaced successfully");
                    } catch (Exception e) {
                        Log.e("EditButton", "Error replacing fragment", e);
                    }
                    }else{
                        showAccessDeniedAlert();
                    }
                }
            });


        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new HomeFragment());
            }
        });
        breadcrumb_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new FacultiesFragment());
            }
        });
    }
    private void loadLecturers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("LECTURER");
        reference.orderByChild("id_khoa").equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mLecturerList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Lecturer lecturer = data.getValue(Lecturer.class);
                    mLecturerList.add(lecturer);
                }
                mLecturerAdapter.notifyDataSetChanged();
                tv_result_lecturer.setText("Số lượng giảng viên: " + mLecturerList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }
    private void createIdToLevelNameMap() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("LEVEL");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot levelSnapshot : snapshot.getChildren()) {
                    String id_level = levelSnapshot.getKey();
                    String name_level = levelSnapshot.child("ten_trinh_do").getValue(String.class);
                    if (id_level != null && name_level != null) {
                        idToLevelNameMap.put(id_level, name_level);
                    }
                }
                mLecturerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TeacherFragment", "Error loading levels", error.toException());
            }
        });
    }
    private void showAccessDeniedAlert(){
        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Bạn không đủ quyền để sử dụng chức năng này")
                .setConfirmText("OK")
                .setConfirmClickListener(Dialog -> {
                    Dialog.dismissWithAnimation();
                })
                .show();
    }
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();  // Không dùng addToBackStack(null)
    }

}