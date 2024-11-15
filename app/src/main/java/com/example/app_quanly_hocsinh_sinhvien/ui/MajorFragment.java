package com.example.app_quanly_hocsinh_sinhvien.ui;

import android.content.Context;
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

import com.example.app_quanly_hocsinh_sinhvien.FragmentActionListener;
import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.class_manage.Classroom;
import com.example.app_quanly_hocsinh_sinhvien.major_manage.DetailFragment;
import com.example.app_quanly_hocsinh_sinhvien.faculty_manage.Faculty;
import com.example.app_quanly_hocsinh_sinhvien.major_manage.Major;
import com.example.app_quanly_hocsinh_sinhvien.major_manage.MajorAdapter;
import com.example.app_quanly_hocsinh_sinhvien.major_manage.UploadFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
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


public class MajorFragment extends Fragment {

    FloatingActionButton fab_major;
    private RecyclerView recMajor;
    private MajorAdapter mMajorAdapter;
    private List<Major> mListMajor;
    private TextView breadcrumb_home, tv_display_results;
    private SearchView searchView;
    private Spinner spinner_filter_faculty;
    private List<String> mListFaculty;
    private List<Faculty> listFaculty;
    private DatabaseReference databaseReference;

    private Map<String, String> idToLevelNameMap = new HashMap<>();
    private FragmentActionListener mListenerHome;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Kiểm tra xem context có phải là MainActivity và thực thi FragmentActionListener không
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
        View view = inflater.inflate(R.layout.fragment_major, container, false);
        mListMajor = new ArrayList<>();
        mMajorAdapter = new MajorAdapter(mListMajor, idToLevelNameMap, major -> openDetailFragment(major));
        initUi(view);
        initListener();

        databaseReference = FirebaseDatabase.getInstance().getReference("FACULTY");
        mListFaculty = new ArrayList<>();
        listFaculty = new ArrayList<>();

        getListLevelFromRealtimeDatabase();
        loadFacultyList();
        createIdToLevelNameMap();
        return view;
    }

    private void initUi(View view){

        fab_major = view.findViewById(R.id.fab_major);
        recMajor = view.findViewById(R.id.recyclerview);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        tv_display_results = view.findViewById(R.id.tv_display_results);
        searchView = view.findViewById(R.id.searchMajor);
        spinner_filter_faculty = view.findViewById(R.id.spinner_filter_faculty);
        searchView.clearFocus();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recMajor.setLayoutManager(linearLayoutManager);

        recMajor.setAdapter(mMajorAdapter);
        searchItemMajor();
    }

    private void initListener(){
        fab_major.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new UploadFragment());
            }
        });
        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListenerHome != null) {
                    mListenerHome.onFragmentAction(R.id.nav_home);
                }
            }
        });
    }
    private void updateDisplayedItemCount() {
        int itemCount = mMajorAdapter.getItemCount();
        tv_display_results.setText("Kết quả: " + itemCount);
    }
    //Lấy danh sách trình chuyên ngành từ database
    private void getListLevelFromRealtimeDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("MAJOR");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Major major = snapshot.getValue(Major.class);
                if(major != null){
                    mListMajor.add(major);
                    updateDisplayedItemCount();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Major major = snapshot.getValue(Major.class);
                if(major == null || mListMajor == null || mListMajor.isEmpty()){
                    return;
                }
                for(int i = 0; i < mListMajor.size(); i++){
                    if(Objects.equals(major.getId(), mListMajor.get(i).getId())){
                        mListMajor.set(i, major);
                        break;
                    }
                }
                updateDisplayedItemCount();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Major major = snapshot.getValue(Major.class);
                if(major == null || mListMajor == null || mListMajor.isEmpty()){
                    return;
                }
                for(int i = 0; i < mListMajor.size(); i++){
                    if(Objects.equals(major.getId(), mListMajor.get(i).getId())){
                        mListMajor.remove(mListMajor.get(i));
                        break;
                    }
                }
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


    private void createIdToLevelNameMap() {
        DatabaseReference levelRef = FirebaseDatabase.getInstance().getReference("LEVEL");
        levelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot levelSnapshot : snapshot.getChildren()) {
                    String id_level = levelSnapshot.getKey();
                    String name_level = levelSnapshot.child("ten_trinh_do").getValue(String.class);
                    if (id_level != null && name_level != null) {
                        idToLevelNameMap.put(id_level, name_level);
                    }
                }
                mMajorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MajorFragment", "Error loading Lecturer", error.toException());
            }
        });
    }
    private void searchItemMajor(){
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
    private void searchList(String text){
        ArrayList<Major> searchList = new ArrayList<>();
        for(Major major : mListMajor){
            if(major.getTen_chuyen_nganh().toLowerCase().contains(text.toLowerCase())){
                searchList.add(major);
            }
        }
        mMajorAdapter.searchMajorList(searchList);
        updateDisplayedItemCount();
    }
    private void setupSpinner(){
        Context context = getContext();
        if (context != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, mListFaculty);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_filter_faculty.setAdapter(adapter);

            spinner_filter_faculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String ten_khoa = parent.getItemAtPosition(position).toString();
                    filterMajorByFaculty(ten_khoa);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }


    private void filterMajorByFaculty(String tenKhoa) {
        ArrayList<Major> filteredList = new ArrayList<>();

        Map<String, String> tenKhoaToIdKhoaMap = new HashMap<>();
        for (Faculty faculty : listFaculty) {
            tenKhoaToIdKhoaMap.put(faculty.getTen_khoa(), faculty.getId());
        }

        if (tenKhoa.equals("Tất cả")) {
            filteredList.addAll(mListMajor);
        } else {
            String idKhoa = tenKhoaToIdKhoaMap.get(tenKhoa);
            if (idKhoa != null) {
                for (Major major : mListMajor) {
                    if (major.getId_khoa().equals(idKhoa)) {
                        filteredList.add(major);
                    }
                }
            }
        }

        mMajorAdapter.searchMajorList(filteredList);
        updateDisplayedItemCount();
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
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openDetailFragment(Major major) {
        DetailFragment detailFragment = new DetailFragment();

        // Truyền dữ liệu vào Bundle
        Bundle bundle = new Bundle();
        bundle.putString("ten_chuyen_nganh", major.getTen_chuyen_nganh());
        bundle.putString("id_khoa", major.getId_khoa());
        bundle.putString("id_trinh_do", major.getId_trinh_do());
        bundle.putString("id", major.getId());
        detailFragment.setArguments(bundle);

        // Chuyển sang DetailFragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}