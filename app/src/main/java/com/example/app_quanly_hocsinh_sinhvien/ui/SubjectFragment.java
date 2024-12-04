package com.example.app_quanly_hocsinh_sinhvien.ui;

import static com.example.app_quanly_hocsinh_sinhvien.MainActivity.userRole;

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
import com.example.app_quanly_hocsinh_sinhvien.faculty_manage.Faculty;
import com.example.app_quanly_hocsinh_sinhvien.lecturer_manage.Lecturer;
import com.example.app_quanly_hocsinh_sinhvien.major_manage.Major;
import com.example.app_quanly_hocsinh_sinhvien.major_manage.MajorAdapter;
import com.example.app_quanly_hocsinh_sinhvien.subject_manage.DetailFragment;
import com.example.app_quanly_hocsinh_sinhvien.subject_manage.Subject;
import com.example.app_quanly_hocsinh_sinhvien.subject_manage.SubjectAdapter;
import com.example.app_quanly_hocsinh_sinhvien.subject_manage.UploadFragment;
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


public class SubjectFragment extends Fragment {

    FloatingActionButton fab_subject;
    private TextView tv_display_results, breadcrumb_home;
    private RecyclerView recSubject;
    private SubjectAdapter mSubjectAdapter;
    private List<Subject> mListSubject;

    private SearchView searchSubject;
    private Spinner spinner_filter_major;
    private DatabaseReference databaseReference;
    private List<String> mListMajor;
    private List<Major> listMajor;

    private Map<String, String> idToMajorNameMap = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subject, container, false);

        mListSubject = new ArrayList<>();
        mSubjectAdapter = new SubjectAdapter(mListSubject,this::openDetailFragment);
        databaseReference = FirebaseDatabase.getInstance().getReference("MAJOR");
        mListMajor = new ArrayList<>();
        listMajor = new ArrayList<>();
        initUi(view);
        initListener();
        getListSubjectFromRealtimeDatabase();
        searchItemSubject();
        createIdMajorToNameMap();
        loadMajorList();
        return view;
    }
    private void initUi(View view){
        fab_subject = view.findViewById(R.id.fab_subject);
        recSubject = view.findViewById(R.id.recyclerview);
        recSubject.setLayoutManager(new LinearLayoutManager(getActivity()));
        recSubject.setAdapter(mSubjectAdapter);
        searchSubject = view.findViewById(R.id.searchSubject);
        spinner_filter_major = view.findViewById(R.id.spinner_filter_major);
        tv_display_results = view.findViewById(R.id.tv_display_results);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
    }

    private void initListener(){
        fab_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userRole.equals("Quản trị viên")) {
                    switchFragment(new UploadFragment());
                } else{
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
    private void getListSubjectFromRealtimeDatabase(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("SUBJECT");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Subject subject = snapshot.getValue(Subject.class);
                if(subject != null){
                    mListSubject.add(subject);
                    mSubjectAdapter.notifyDataSetChanged();
                    updateDisplayedItemCount();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Subject subject = snapshot.getValue(Subject.class);
                for(int i =0; i<mListSubject.size(); i++){
                    if (Objects.equals(subject.getId(), mListSubject.get(i).getId())) {
                        mListSubject.set(i, subject);
                        mSubjectAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                mSubjectAdapter.notifyDataSetChanged();
                updateDisplayedItemCount();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Subject subject = snapshot.getValue(Subject.class);
                for(int i =0; i<mListSubject.size(); i++){
                    if (Objects.equals(subject.getId(), mListSubject.get(i).getId())) {
                        mListSubject.remove(i);
                        mSubjectAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                mSubjectAdapter.notifyDataSetChanged();
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
    private void searchItemSubject(){
        searchSubject.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchSubject.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return false;
            }
        });
    }
    private void searchList(String text) {
        ArrayList<Subject> searchList = new ArrayList<>();
        for (Subject subject : mListSubject) {
            if (subject.getTen_mon_hoc().toLowerCase().contains(text.toLowerCase()) ||
                    subject.getId().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(subject);
            }
        }
        mSubjectAdapter.searchSubjectList(searchList);
        updateDisplayedItemCount();
    }
    private void loadMajorList(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListMajor.add("Tất cả");
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Major major = dataSnapshot.getValue(Major.class);
                    if(major != null && major.getTen_chuyen_nganh() != null){
                        mListMajor.add(major.getTen_chuyen_nganh());
                        listMajor.add(major);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mListMajor);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_filter_major.setAdapter(adapter);

        spinner_filter_major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ten_chuyen_nganh = parent.getItemAtPosition(position).toString();
                filterSubjectByMajor(ten_chuyen_nganh);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void filterSubjectByMajor(String tenChuyenNganh) {
        ArrayList<Subject> filteredList = new ArrayList<>();

        Map<String, String> tenCNToIdCNMap = new HashMap<>();
        for (Major major : listMajor) {
            tenCNToIdCNMap.put(major.getTen_chuyen_nganh(), major.getId());
        }

        if (tenChuyenNganh.equals("Tất cả")) {
            filteredList.addAll(mListSubject);
        } else {
            String idCN = tenCNToIdCNMap.get(tenChuyenNganh);

            if (idCN != null) {
                for (Subject subject : mListSubject) {
                    if (subject.getId_chuyen_nganh().equals(idCN)) {
                        filteredList.add(subject);
                    }
                }
            }
        }
        mSubjectAdapter.searchSubjectList(filteredList);
        updateDisplayedItemCount();
    }
    private void updateDisplayedItemCount() {
        int itemCount = mSubjectAdapter.getItemCount();
        tv_display_results.setText("Kết quả: " + itemCount);
    }

    private void createIdMajorToNameMap() {
        DatabaseReference majorRef = FirebaseDatabase.getInstance().getReference("MAJOR");
        majorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot majorSnapshot : snapshot.getChildren()) {
                    String id_major = majorSnapshot.getKey();
                    String name_major = majorSnapshot.child("ten_chuyen_nganh").getValue(String.class);
                    if (id_major != null && name_major != null) {
                        idToMajorNameMap.put(id_major, name_major);
                    }
                }
                mSubjectAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ClassFragment", "Error loading Lecturer", error.toException());
            }
        });
    }
    private void openDetailFragment(Subject subject) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", subject.getId());
        bundle.putString("ten_mon_hoc", subject.getTen_mon_hoc());
        bundle.putInt("so_dvht", subject.getSo_dvht());
        bundle.putFloat("so_gio_LT", subject.getSo_gio_LT());
        bundle.putFloat("so_gio_TH", subject.getSo_gio_TH());
        bundle.putString("ten_chuyen_nganh", idToMajorNameMap.get(subject.getId_chuyen_nganh()));
        detailFragment.setArguments(bundle);

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