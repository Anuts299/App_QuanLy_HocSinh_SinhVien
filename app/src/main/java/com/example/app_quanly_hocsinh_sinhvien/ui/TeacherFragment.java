package com.example.app_quanly_hocsinh_sinhvien.ui;

import static com.example.app_quanly_hocsinh_sinhvien.MainActivity.userRole;

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
import androidx.appcompat.widget.SearchView; // Thêm import này

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.class_manage.Classroom;
import com.example.app_quanly_hocsinh_sinhvien.faculty_manage.Faculty;
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
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TeacherFragment extends Fragment {

    FloatingActionButton fab_lecturer;
    TextView breadcrumb_home, tv_display_results;

    private RecyclerView recLecturer;
    private LecturerAdapter mLecturerAdapter;
    private List<Lecturer> mListLecturer;

    private androidx.appcompat.widget.SearchView searchView;
    private Map<String, String> idToFacultyNameMap = new HashMap<>();
    private Map<String, String> idToLevelNameMap = new HashMap<>();

    private Spinner spinner_filter_faculty;
    private DatabaseReference databaseReference;
    private List<String> mListFaculty;
    private List<Faculty> listFaculty;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher, container, false);
        mListLecturer = new ArrayList<>();
        mLecturerAdapter = new LecturerAdapter(mListLecturer, idToFacultyNameMap, idToLevelNameMap, this::openDetailFragment);

        databaseReference = FirebaseDatabase.getInstance().getReference("FACULTY");
        mListFaculty = new ArrayList<>();
        listFaculty = new ArrayList<>();
        initUi(view);
        initListener();
        getListLecturerFromRealtimeDatabase();
        createIdToFacultyNameMap();
        createIdToLevelNameMap();
        searchItemLecturer();
        loadFacultyList();

        return view;
    }

    private void initUi(View view) {
        fab_lecturer = view.findViewById(R.id.fab_lecturer);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        tv_display_results  = view.findViewById(R.id.tv_display_results);
        searchView = view.findViewById(R.id.searchLecturer);
        recLecturer = view.findViewById(R.id.recyclerview);
        spinner_filter_faculty = view.findViewById(R.id.spinner_filter_faculty);
        recLecturer.setLayoutManager(new LinearLayoutManager(getActivity()));
        recLecturer.setAdapter(mLecturerAdapter);
    }

    private void initListener() {
        fab_lecturer.setOnClickListener(v -> {
            if(userRole.equals("Quản trị viên")) {
                switchFragment(new UploadFragment());
            } else {
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Bạn không đủ quyền để sử dụng chức năng này")
                        .setConfirmText("OK")
                        .setConfirmClickListener(Dialog -> {
                            Dialog.dismissWithAnimation();
                        })
                        .show();
            }
        });
        breadcrumb_home.setOnClickListener(v -> switchFragment(new HomeFragment()));
    }
    private void loadFacultyList(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListFaculty.add("Tất cả");
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Faculty faculty = dataSnapshot.getValue(Faculty.class);
                    if(faculty != null && faculty.getTen_khoa() != null){
                        mListFaculty.add(faculty.getTen_khoa());
                        listFaculty.add(faculty);
                    }
                }
                setupSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setupSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mListFaculty);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_filter_faculty.setAdapter(adapter);

        spinner_filter_faculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ten_khoa = parent.getItemAtPosition(position).toString();
                filterClassroomByFaculty(ten_khoa);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void filterClassroomByFaculty(String tenKhoa) {
        ArrayList<Lecturer> filteredList = new ArrayList<>();

        Map<String, String> tenKhoaToIdKhoaMap = new HashMap<>();
        for (Faculty faculty : listFaculty) {
            tenKhoaToIdKhoaMap.put(faculty.getTen_khoa(), faculty.getId());
        }

        if (tenKhoa.equals("Tất cả")) {
            filteredList.addAll(mListLecturer);
        } else {
            String idKhoa = tenKhoaToIdKhoaMap.get(tenKhoa);

            if (idKhoa != null) {
                Log.d("LecturerFragment", idKhoa);
            } else {
                Log.d("LecturerFragment", "idKhoa is null for faculty: " + tenKhoa);
            }
            if (idKhoa != null) {
                for (Lecturer lecturer : mListLecturer) {
                    if (lecturer.getId_khoa().equals(idKhoa)) {
                        filteredList.add(lecturer);
                    }
                }
            }
        }
        mLecturerAdapter.searchLecturerList(filteredList);
        updateDisplayedItemCount();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TeacherFragment", "Error loading faculties", error.toException());
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

    public void searchItemLecturer() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchLecturer(newText);
                return true;
            }
        });
    }

    private void searchLecturer(String text) {
        ArrayList<Lecturer> searchList = new ArrayList<>();
        for (Lecturer lecturer : mListLecturer) {
            if (lecturer.getTen_giang_vien().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(lecturer);
            }
        }
        mLecturerAdapter.searchLecturerList(searchList);
        updateDisplayedItemCount();
    }

    private void getListLecturerFromRealtimeDatabase() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("LECTURER");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Lecturer lecturer = snapshot.getValue(Lecturer.class);
                if (lecturer != null) {
                    mListLecturer.add(lecturer);
                    mLecturerAdapter.notifyDataSetChanged();
                    updateDisplayedItemCount();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Lecturer lecturer = snapshot.getValue(Lecturer.class);
                if (lecturer == null) return;

                for (int i = 0; i < mListLecturer.size(); i++) {
                    if (Objects.equals(lecturer.getId(), mListLecturer.get(i).getId())) {
                        mListLecturer.set(i, lecturer);
                        mLecturerAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                updateDisplayedItemCount();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Lecturer lecturer = snapshot.getValue(Lecturer.class);
                if (lecturer == null) return;

                for (int i = 0; i < mListLecturer.size(); i++) {
                    if (Objects.equals(lecturer.getId(), mListLecturer.get(i).getId())) {
                        mListLecturer.remove(i);
                        mLecturerAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                updateDisplayedItemCount();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    // Phương thức cập nhật số lượng lớp học đang hiển thị
    private void updateDisplayedItemCount() {
        int itemCount = mLecturerAdapter.getItemCount();
        tv_display_results.setText("Kết quả: " + itemCount);
    }
    private void openDetailFragment(Lecturer lecturer) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", lecturer.getId());
        bundle.putString("ten_giang_vien", lecturer.getTen_giang_vien());
        bundle.putString("ten_khoa", idToFacultyNameMap.get(lecturer.getId_khoa()));
        bundle.putString("ten_trinh_do", idToLevelNameMap.get(lecturer.getId_trinh_do()));
        detailFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}
