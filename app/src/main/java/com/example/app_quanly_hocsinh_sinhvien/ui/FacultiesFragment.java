package com.example.app_quanly_hocsinh_sinhvien.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.example.app_quanly_hocsinh_sinhvien.class_manage.ClassroomAdapter;
import com.example.app_quanly_hocsinh_sinhvien.faculty_manage.DetailFragment;
import com.example.app_quanly_hocsinh_sinhvien.faculty_manage.Faculty;
import com.example.app_quanly_hocsinh_sinhvien.faculty_manage.FacultyAdapter;
import com.example.app_quanly_hocsinh_sinhvien.faculty_manage.UploadFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FacultiesFragment extends Fragment {
    FloatingActionButton fab_faculty;
    RecyclerView recyView_Faculty;
    FacultyAdapter mFacultyAdapter;
    private SearchView searchView;
    List<Faculty> mListFaculty;
    TextView breadcrumb_home;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_faculties, container, false);
        mListFaculty = new ArrayList<>();
        mFacultyAdapter = new FacultyAdapter(mListFaculty, faculty -> openDetailFragment(faculty));
        initUi(view);
        initListener();
        getListFacultyFromRealtimeDatabase();
        return view;
    }


    private void initUi(View view){
        fab_faculty = view.findViewById(R.id.fab_faculty);
        recyView_Faculty = view.findViewById(R.id.recyView_Faculty);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        searchView = view.findViewById(R.id.searchFaculty);
        searchView.clearFocus();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyView_Faculty.setLayoutManager(linearLayoutManager);

        recyView_Faculty.setAdapter(mFacultyAdapter);
        searchItemFaculty();
    }
    private void initListener(){
        fab_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadFragment uploadFragment = new UploadFragment();
                switchFragment(uploadFragment);
            }
        });
        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new HomeFragment());
            }
        });
    }
    private void getListFacultyFromRealtimeDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("FACULTY");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Faculty faculty = snapshot.getValue(Faculty.class);
                if(faculty != null){
                    mListFaculty.add(faculty);
                    mFacultyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Faculty faculty = snapshot.getValue(Faculty.class);
                if(faculty == null || mListFaculty == null || mListFaculty.isEmpty()){
                    return;
                }
                for(int i = 0; i < mListFaculty.size(); i++){
                    if(Objects.equals(faculty.getId(), mListFaculty.get(i).getId())){
                        mListFaculty.set(i, faculty);
                        break;
                    }
                }
                mFacultyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Faculty faculty = snapshot.getValue(Faculty.class);
                if(faculty == null || mListFaculty == null || mListFaculty.isEmpty()){
                    return;
                }
                for(int i = 0; i < mListFaculty.size(); i++){
                    if(Objects.equals(faculty.getId(), mListFaculty.get(i).getId())){
                        mListFaculty.remove(mListFaculty.get(i));
                        break;
                    }
                }
                mFacultyAdapter.notifyDataSetChanged();
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

    //Tìm kiếm khoa
    public void searchItemFaculty(){
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
        ArrayList<Faculty> searchList = new ArrayList<>();
        for(Faculty faculty : mListFaculty){
            if(faculty.getTen_khoa().toLowerCase().contains(text.toLowerCase())){
                searchList.add(faculty);
            }
        }
        mFacultyAdapter.searchFacultyList(searchList);
    }

    private void openDetailFragment(Faculty faculty){
        DetailFragment detailFragment = new DetailFragment();
        // Truyền dữ liệu vào Bundle
        Bundle bundle = new Bundle();
        bundle.putString("ten_khoa", faculty.getTen_khoa());
        bundle.putString("id", faculty.getId());
        bundle.putString("ma_dinh_dang", faculty.getMa_dinh_dang());
        detailFragment.setArguments(bundle);

        // Chuyển sang DetailFragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}