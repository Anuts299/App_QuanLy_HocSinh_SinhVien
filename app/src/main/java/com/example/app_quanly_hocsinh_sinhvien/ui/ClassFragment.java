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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.class_manage.Classroom;
import com.example.app_quanly_hocsinh_sinhvien.class_manage.ClassroomAdapter;
import com.example.app_quanly_hocsinh_sinhvien.class_manage.DetailFragment;
import com.example.app_quanly_hocsinh_sinhvien.class_manage.UploadFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ClassFragment extends Fragment {

    FloatingActionButton fab_class;
    private RecyclerView recClass;
    private ClassroomAdapter mClassroomAdapter;
    private List<Classroom> mListClassroom;
    private SearchView searchView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class, container, false);

        initUi(view);
        initListener();
        getListClassroomsFromRealtimeDatabase();
        return view;
    }
    private void initUi(View view){
        fab_class = view.findViewById(R.id.fab_class);
        recClass = view.findViewById(R.id.recyclerview);
        searchView = view.findViewById(R.id.searchClass);
        searchView.clearFocus();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recClass.setLayoutManager(linearLayoutManager);


        mListClassroom = new ArrayList<>();
        mClassroomAdapter = new ClassroomAdapter(mListClassroom, classroom -> openDetailFragment(classroom));
        recClass.setAdapter(mClassroomAdapter);

        recClass.setAdapter(mClassroomAdapter);
        searchItemClassroom();
    }

    private void initListener(){
        fab_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadFragment uploadFragment = new UploadFragment();
                switchFragment(uploadFragment);
            }
        });
    }
    // Phương thức chuyển đổi giữa các fragment
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    //Tìm kiếm lớp học
    public void searchItemClassroom(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
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
        bundle.putString("ten_khoa", classroom.getTen_khoa());
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