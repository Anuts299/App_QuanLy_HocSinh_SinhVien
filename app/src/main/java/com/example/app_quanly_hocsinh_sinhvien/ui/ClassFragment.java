package com.example.app_quanly_hocsinh_sinhvien.ui;

import static com.example.app_quanly_hocsinh_sinhvien.MainActivity.userRole;

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
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ClassFragment extends Fragment {

    FloatingActionButton fab_class;
    private RecyclerView recClass;
    private ClassroomAdapter mClassroomAdapter;
    private List<Classroom> mListClassroom;
    private SearchView searchView;
    private TextView breadcrumb_home, tv_display_results;
    private DatabaseReference databaseReference;
    private Spinner spinner_filter_faculty;
    private List<String> mListFaculty;
    private List<Faculty> listFaculty;

    //HasMap chuyển id_giangvien thành ten_giang_vien
    private Map<String, String> idToLecturerNameMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class, container, false);
        mListClassroom = new ArrayList<>();
        mClassroomAdapter = new ClassroomAdapter(mListClassroom, idToLecturerNameMap, classroom -> openDetailFragment(classroom));
        initUi(view);
        initListener();

        databaseReference = FirebaseDatabase.getInstance().getReference("FACULTY");
        mListFaculty = new ArrayList<>();
        listFaculty = new ArrayList<>();

        getListClassroomsFromRealtimeDatabase();
        loadFacultyList();
        createIdToLecturerNameMap();
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

    private void createIdToLecturerNameMap() {
        DatabaseReference facultyRef = FirebaseDatabase.getInstance().getReference("LECTURER");
        facultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ClassFragment", "Error loading Lecturer", error.toException());
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
                for (Classroom classroom : mListClassroom) {
                    if (classroom.getId_khoa().equals(idKhoa)) {
                        filteredList.add(classroom);
                    }
                }
            }
        }

        mClassroomAdapter.searchClassroomList(filteredList);
        updateDisplayedItemCount();
    }


    private void initUi(View view){
        fab_class = view.findViewById(R.id.fab_class);
        recClass = view.findViewById(R.id.recyclerview);
        searchView = view.findViewById(R.id.searchClass);
        spinner_filter_faculty = view.findViewById(R.id.spinner_filter_faculty);
        searchView.clearFocus();
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        tv_display_results = view.findViewById(R.id.tv_display_results);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recClass.setLayoutManager(linearLayoutManager);

        recClass.setAdapter(mClassroomAdapter);
        searchItemClassroom();
    }

    private void initListener(){
        fab_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userRole.equals("Quản trị viên")){
                    switchFragment(new UploadFragment());
                }else{
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Bạn không đủ quyền để sử dụng chức năng này")
                            .setConfirmText("OK")
                            .setConfirmClickListener(Dialog -> {
                                Dialog.dismissWithAnimation();
                            })
                            .show();
                }

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
    // Phương thức cập nhật số lượng lớp học đang hiển thị
    private void updateDisplayedItemCount() {
        int itemCount = mClassroomAdapter.getItemCount();
        tv_display_results.setText("Kết quả: " + itemCount);
    }
    //Tìm kiếm lớp học
    private void searchItemClassroom(){
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
    private void searchList(String text){
        ArrayList<Classroom> searchList = new ArrayList<>();
        for(Classroom classroom : mListClassroom){
            if(classroom.getMa_lop().toLowerCase().contains(text.toLowerCase())){
                searchList.add(classroom);
            }
        }
        mClassroomAdapter.searchClassroomList(searchList);
        updateDisplayedItemCount();
    }
    //Lấy danh sách lớp học từ database
    private void getListClassroomsFromRealtimeDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("CLASSROOM");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Classroom classroom = snapshot.getValue(Classroom.class);
                if(classroom != null){
                    mListClassroom.add(classroom);
                    mClassroomAdapter.notifyDataSetChanged();
                    updateDisplayedItemCount();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Classroom classroom = snapshot.getValue(Classroom.class);
                if(classroom == null || mListClassroom == null || mListClassroom.isEmpty()){
                    return;
                }
                for(int i = 0; i < mListClassroom.size(); i++){
                    if(Objects.equals(classroom.getId(), mListClassroom.get(i).getId())){
                        mListClassroom.set(i, classroom);
                        break;
                    }
                }
                mClassroomAdapter.notifyDataSetChanged();
                updateDisplayedItemCount();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Classroom classroom = snapshot.getValue(Classroom.class);
                if(classroom == null || mListClassroom == null || mListClassroom.isEmpty()){
                    return;
                }
                for(int i = 0; i < mListClassroom.size(); i++){
                    if(Objects.equals(classroom.getId(), mListClassroom.get(i).getId())){
                        mListClassroom.remove(mListClassroom.get(i));
                        break;
                    }
                }
                mClassroomAdapter.notifyDataSetChanged();
                updateDisplayedItemCount();
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
        bundle.putString("ten_co_van", idToLecturerNameMap.get(classroom.getId_giang_vien()));
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