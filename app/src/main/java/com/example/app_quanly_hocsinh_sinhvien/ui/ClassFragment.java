package com.example.app_quanly_hocsinh_sinhvien.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.class_manage.Classroom;
import com.example.app_quanly_hocsinh_sinhvien.class_manage.ClassroomAdapter;
import com.example.app_quanly_hocsinh_sinhvien.class_manage.DetailFragment;
import com.example.app_quanly_hocsinh_sinhvien.class_manage.UploadFragment;
import com.example.app_quanly_hocsinh_sinhvien.faculty_manage.Faculty;
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

public class ClassFragment extends Fragment {

    private TextView breadcrumb_home;
    private FloatingActionButton fab_class;
    private RecyclerView recClass;
    private ClassroomAdapter mClassroomAdapter;
    private List<Classroom> mListClassroom;
    private Spinner spinner_filter_faculty;
    private List<String> mListFaculty;
    private List<Faculty> listFaculty;
    private Map<String, String> idToLecturerNameMap = new HashMap<>();
    private AlertDialog loadingDialog; // Dialog cho tiến trình
    private int pendingLoads = 3; // Số lần tải dữ liệu cần hoàn thành

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class, container, false);

        initUi(view);
        setupLoadingDialog(); // Khởi tạo dialog
        showLoadingDialog(); // Hiển thị dialog khi bắt đầu

        mListClassroom = new ArrayList<>();
        mClassroomAdapter = new ClassroomAdapter(mListClassroom, idToLecturerNameMap, classroom -> openDetailFragment(classroom));
        recClass.setAdapter(mClassroomAdapter);

        loadInitialData(); // Bắt đầu tải dữ liệu

        return view;
    }

    private void initUi(View view) {
        fab_class = view.findViewById(R.id.fab_class);
        recClass = view.findViewById(R.id.recyclerview);
        spinner_filter_faculty = view.findViewById(R.id.spinner_filter_faculty);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);

        recClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Xử lý sự kiện click cho FloatingActionButton
        fab_class.setOnClickListener(v -> switchToUploadFragment());
        breadcrumb_home.setOnClickListener(v -> switchToHomeFragment());
    }

    private void setupLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false); // Không cho phép hủy
        builder.setView(R.layout.progress_layout); // Sử dụng layout tùy chỉnh
        loadingDialog = builder.create();
    }

    private void showLoadingDialog() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void loadInitialData() {
        getListClassroomsFromRealtimeDatabase();
        loadFacultyList();
        createIdToLecturerNameMap();
    }

    private void getListClassroomsFromRealtimeDatabase() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("CLASSROOM");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Classroom classroom = snapshot.getValue(Classroom.class);
                if (classroom != null) {
                    mListClassroom.add(classroom);
                    mClassroomAdapter.notifyDataSetChanged();
                }
                checkAndDismissLoadingDialog();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                checkAndDismissLoadingDialog();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                checkAndDismissLoadingDialog();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                checkAndDismissLoadingDialog();
            }
        });
    }

    private void loadFacultyList() {
        DatabaseReference facultyRef = FirebaseDatabase.getInstance().getReference("FACULTY");

        facultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListFaculty = new ArrayList<>();
                listFaculty = new ArrayList<>();
                mListFaculty.add("Tất cả");

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Faculty faculty = dataSnapshot.getValue(Faculty.class);
                    if (faculty != null) {
                        mListFaculty.add(faculty.getTen_khoa());
                        listFaculty.add(faculty);
                    }
                }
                setupSpinner();
                checkAndDismissLoadingDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                checkAndDismissLoadingDialog();
            }
        });
    }

    private void createIdToLecturerNameMap() {
        DatabaseReference lecturerRef = FirebaseDatabase.getInstance().getReference("LECTURER");

        lecturerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot lecturerSnapshot : snapshot.getChildren()) {
                    String id_lecturer = lecturerSnapshot.getKey();
                    String name_lecturer = lecturerSnapshot.child("ten_giang_vien").getValue(String.class);
                    if (id_lecturer != null && name_lecturer != null) {
                        idToLecturerNameMap.put(id_lecturer, name_lecturer);
                    }
                }
                mClassroomAdapter.notifyDataSetChanged();
                checkAndDismissLoadingDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                checkAndDismissLoadingDialog();
            }
        });
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mListFaculty);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_filter_faculty.setAdapter(adapter);
    }

    private void checkAndDismissLoadingDialog() {
        pendingLoads--; // Giảm số lần tải
        if (pendingLoads <= 0) {
            dismissLoadingDialog(); // Đóng dialog khi hoàn thành tất cả tải
        }
    }

    private void openDetailFragment(Classroom classroom) {
        DetailFragment detailFragment = new DetailFragment();

        Bundle bundle = new Bundle();
        bundle.putString("ma_lop", classroom.getMa_lop());
        bundle.putString("ten_lop", classroom.getTen_lop());
        bundle.putString("id_khoa", classroom.getId_khoa());
        bundle.putString("ten_co_van", idToLecturerNameMap.get(classroom.getId_giang_vien()));
        bundle.putString("nam_hoc", classroom.getNam_hoc());
        bundle.putString("id", classroom.getId());
        detailFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void switchToUploadFragment() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new UploadFragment())
                .addToBackStack(null)
                .commit();
    }
    private void switchToHomeFragment() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .addToBackStack(null)
                .commit();
    }
}
