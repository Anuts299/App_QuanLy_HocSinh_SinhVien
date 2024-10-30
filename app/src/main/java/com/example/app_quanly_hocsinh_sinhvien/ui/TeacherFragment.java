package com.example.app_quanly_hocsinh_sinhvien.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.app_quanly_hocsinh_sinhvien.class_manage.Classroom;
import com.example.app_quanly_hocsinh_sinhvien.lecturer_manage.DetailFragment;
import com.example.app_quanly_hocsinh_sinhvien.lecturer_manage.Lecturer;
import com.example.app_quanly_hocsinh_sinhvien.lecturer_manage.LecturerAdapter;
import com.example.app_quanly_hocsinh_sinhvien.lecturer_manage.UploadFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TeacherFragment extends Fragment {


    FloatingActionButton fab_lecturer;
    TextView breadcrumb_home;
    //Thành phần cho thêm giảng viên
    private RecyclerView recLecturer;
    private LecturerAdapter mLecturerAdapter;
    private List<Lecturer> mListLecturer;

    //HasMap chuyển id_khoa thành ten_khoa
    private Map<String, String> idToFacultyNameMap = new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher, container, false);
        //Phần hiển thị giảng viên
        mListLecturer = new ArrayList<>();
        mLecturerAdapter = new LecturerAdapter(mListLecturer, idToFacultyNameMap, lecturer -> openDetailFragment(lecturer));

        initUi(view);
        initListener();

        //Phần hiển thị giảng viên
        getListLecturerFromRealtimeDatabase();
        createIdToFacultyNameMap();
        return view;
    }

    private void initUi(View view){
        fab_lecturer = view.findViewById(R.id.fab_lecturer);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        //Phần hiển thị các giảng viên
        recLecturer = view.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recLecturer.setLayoutManager(linearLayoutManager);
        recLecturer.setAdapter(mLecturerAdapter);

    }
    private void initListener(){
        fab_lecturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TeacherFragment", "Đã nhấn fab Lecturer");
                switchFragment(new UploadFragment());
            }
        });
        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new HomeFragment());
            }
        });
    }
    private void createIdToFacultyNameMap() {
        DatabaseReference facultyRef = FirebaseDatabase.getInstance().getReference("FACULTY");
        facultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot facultySnapshot : snapshot.getChildren()) {
                    String idKhoa = facultySnapshot.getKey();
                    String tenKhoa = facultySnapshot.child("ten_khoa").getValue(String.class);
                    if (idKhoa != null && tenKhoa != null) {
                        idToFacultyNameMap.put(idKhoa, tenKhoa);
                    }
                }
                mLecturerAdapter.notifyDataSetChanged();
                Log.d("TeacherFragment", "Loaded faculties: " + idToFacultyNameMap.keySet());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TeacherFragment", "Error loading faculties", error.toException());
            }
        });
    }
    private void getListLecturerFromRealtimeDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("LECTURER");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Lecturer lecturer = snapshot.getValue(Lecturer.class);
                if(lecturer != null){
                    mListLecturer.add(lecturer);
                    mLecturerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Phương thức chuyển đổi giữa các fragment
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();  // Không dùng addToBackStack(null)
    }

    private void openDetailFragment(Lecturer lecturer) {
        DetailFragment detailFragment = new DetailFragment();

        // Truyền dữ liệu vào Bundle
        Bundle bundle = new Bundle();
        bundle.putString("id", lecturer.getId());
        bundle.putString("ten_giang_vien",lecturer.getTen_giang_vien());
        bundle.putString("ten_khoa",idToFacultyNameMap.get(lecturer.getId_khoa()));
        detailFragment.setArguments(bundle);

        // Chuyển sang DetailFragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}