package com.example.app_quanly_hocsinh_sinhvien.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ClassFragment extends Fragment {

    FloatingActionButton fab_class;
    private RecyclerView recClass;
    private ClassroomAdapter mClassroomAdapter;
    private List<Classroom> mListClassroom;
    private SearchView searchView;
    private TextView breadcrumb_home;
    private DatabaseReference databaseReference;
    private Spinner spinner_filter_faculty;
    private List<String> mListFaculty;
    private List<Faculty> listFaculty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class, container, false);
        mListClassroom = new ArrayList<>();
        mClassroomAdapter = new ClassroomAdapter(mListClassroom, classroom -> openDetailFragment(classroom));
        initUi(view);
        initListener();

        databaseReference = FirebaseDatabase.getInstance().getReference("FACULTY");
        mListFaculty = new ArrayList<>();
        listFaculty = new ArrayList<>();

        getListClassroomsFromRealtimeDatabase();
        loadFacultyList();
        return view;
    }

    private void loadFacultyList(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListFaculty.add("Tất cả");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Faculty faculty = dataSnapshot.getValue(Faculty.class);
                    if (faculty != null && faculty.getTen_khoa() != null) {
                        mListFaculty.add(faculty.getTen_khoa());
                        listFaculty.add(faculty);
                    }
                }
                setupSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ClassFragment", "Error loading faculty list", error.toException());
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
        ArrayList<Classroom> filteredList = new ArrayList<>();

        Map<String, String> tenKhoaToIdKhoaMap = new HashMap<>();
        for (Faculty faculty : listFaculty) {
            tenKhoaToIdKhoaMap.put(faculty.getTen_khoa(), faculty.getId());
        }

        if (tenKhoa.equals("Tất cả")) {
            filteredList.addAll(mListClassroom);
        } else {
            String idKhoa = tenKhoaToIdKhoaMap.get(tenKhoa);
            Log.d("ClassFragment", "Available faculties: " + tenKhoaToIdKhoaMap.keySet());
            if (idKhoa != null) {
                Log.d("ClassFragment", idKhoa);
            } else {
                Log.d("ClassFragment", "idKhoa is null for faculty: " + tenKhoa);
            }
            if (idKhoa != null) {
                for (Classroom classroom : mListClassroom) {
                    if (classroom.getId_khoa().equals(idKhoa)) {
                        filteredList.add(classroom);
                    }
                }
            }
        }

        mClassroomAdapter.searchClassroomList(filteredList);
    }


    private void initUi(View view){
        fab_class = view.findViewById(R.id.fab_class);
        recClass = view.findViewById(R.id.recyclerview);
        searchView = view.findViewById(R.id.searchClass);
        spinner_filter_faculty = view.findViewById(R.id.spinner_filter_faculty);
        searchView.clearFocus();
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recClass.setLayoutManager(linearLayoutManager);

        recClass.setAdapter(mClassroomAdapter);
        searchItemClassroom();

    }

    private void initListener(){
        fab_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    // Phương thức chuyển đổi giữa các fragment
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();  // Không dùng addToBackStack(null)
    }

    //Tìm kiếm lớp học
    public void searchItemClassroom(){
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
    //Tìm danh sách
    public void searchList(String text){
        ArrayList<Classroom> searchList = new ArrayList<>();
        for(Classroom classroom : mListClassroom){
            if(classroom.getMa_lop().toLowerCase().contains(text.toLowerCase())){
                searchList.add(classroom);
            }
        }
        mClassroomAdapter.searchClassroomList(searchList);
    }
    //Lấy danh sách lớp học từ database
    private void getListClassroomsFromRealtimeDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("CLASSROOM");
        //Cách 1
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    Classroom classroom = dataSnapshot.getValue(Classroom.class);
//                    mListClassroom.add(classroom);
//                }
//                mClassroomAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
//                        .setTitleText("Thất bại")
//                        .setContentText("Lấy danh sách lớp thất bại")
//                        .setConfirmText("OK")
//                        .setConfirmClickListener(sDialog -> {
//                            sDialog.dismissWithAnimation();
//                        })
//                        .show();
//            }
//        });
        //Cách 2:
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Classroom classroom = snapshot.getValue(Classroom.class);
                if(classroom != null){
                    mListClassroom.add(classroom);
                    mClassroomAdapter.notifyDataSetChanged();
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

    private void openDetailFragment(Classroom classroom) {
        DetailFragment detailFragment = new DetailFragment();

        // Truyền dữ liệu vào Bundle
        Bundle bundle = new Bundle();
        bundle.putString("ma_lop", classroom.getMa_lop());
        bundle.putString("ten_lop",classroom.getTen_lop());
        bundle.putString("id_khoa", classroom.getId_khoa());
        bundle.putString("ten_co_van", classroom.getTen_co_van());
        bundle.putString("nam_hoc", classroom.getNam_hoc());
        bundle.putString("id",classroom.getId());
        detailFragment.setArguments(bundle);

        // Chuyển sang DetailFragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

}