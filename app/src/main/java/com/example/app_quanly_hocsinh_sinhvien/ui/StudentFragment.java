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
import androidx.appcompat.widget.SearchView;

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
import com.example.app_quanly_hocsinh_sinhvien.student_manage.DetailFragment;
import com.example.app_quanly_hocsinh_sinhvien.student_manage.Student;
import com.example.app_quanly_hocsinh_sinhvien.student_manage.StudentAdapter;
import com.example.app_quanly_hocsinh_sinhvien.student_manage.UploadFragment;
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


public class StudentFragment extends Fragment {

    FloatingActionButton fab_student;
    private RecyclerView recyView_Student;
    private StudentAdapter mStudentAdapter;
    private List<Student> mListStudent;
    private TextView breadcrumb_home, tv_display_results;
    private SearchView searchView;
    private Spinner spinner_filter_class;
    private DatabaseReference databaseReference;
    private Map<String, String> idToCodeClassMap = new HashMap<>();
    private Map<String, String> idToNameLevelMap = new HashMap<>();
    private List<String> mListClass;
    private List<Classroom> listClassroom;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student, container, false);
        mListStudent = new ArrayList<>();
        mStudentAdapter = new StudentAdapter(idToCodeClassMap, idToNameLevelMap, mListStudent, student -> openDetailFragment(student));
        databaseReference = FirebaseDatabase.getInstance().getReference("CLASSROOM");
        mListClass = new ArrayList<>();
        listClassroom = new ArrayList<>();

        iniUi(view);
        initListener();
        getListStudentFromRealtimeDatabase();
        createIdToClassCodeMap();
        createIdToLevelNameMap();
        loadClassList();
        searchItemStudent();
        return view;
    }
    private void iniUi(View view){
        fab_student = view.findViewById(R.id.fab_student);
        recyView_Student = view.findViewById(R.id.recyView_Student);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        searchView = view.findViewById(R.id.searchStudent);
        tv_display_results = view.findViewById(R.id.tv_display_results);
        spinner_filter_class = view.findViewById(R.id.spinner_filter_class);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyView_Student.setLayoutManager(linearLayoutManager);

        recyView_Student.setAdapter(mStudentAdapter);
    }
    private void initListener(){
        fab_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userRole.equals("Quản trị viên")) {
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
    private void createIdToClassCodeMap(){
        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("CLASSROOM");
        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot classSnapshot : snapshot.getChildren()){
                    String id_class = classSnapshot.getKey();
                    String code_class = classSnapshot.child("ma_lop").getValue(String.class);
                    if(id_class != null && code_class != null){
                        idToCodeClassMap.put(id_class, code_class);
                    }
                }
                mStudentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentFragment", "Error loading Student", error.toException());
            }
        });
    }

    private void createIdToLevelNameMap(){
        DatabaseReference levelRef = FirebaseDatabase.getInstance().getReference("LEVEL");
        levelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot levelSnapshot : snapshot.getChildren()){
                    String id_level = levelSnapshot.getKey();
                    String name_level = levelSnapshot.child("ten_trinh_do").getValue(String.class);
                    if(id_level != null && name_level != null){
                        idToNameLevelMap.put(id_level, name_level);
                    }
                }
                mStudentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentFragment", "Error loading level", error.toException());
            }
        });
    }
    private void getListStudentFromRealtimeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("STUDENT");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Student student = snapshot.getValue(Student.class);
                if (student != null) {
                    mListStudent.add(student);
                    updateRecyclerView();
                    updateDisplayedItemCount();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Student student = snapshot.getValue(Student.class);
                if (student == null || mListStudent == null || mListStudent.isEmpty()) {
                    return;
                }

                for (int i = 0; i < mListStudent.size(); i++) {
                    if (Objects.equals(student.getId(), mListStudent.get(i).getId())) {
                        mListStudent.set(i, student);
                        break;
                    }
                }
                updateRecyclerView();
                updateDisplayedItemCount();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Student student = snapshot.getValue(Student.class);
                if (student == null || mListStudent == null || mListStudent.isEmpty()) {
                    return;
                }

                for (int i = 0; i < mListStudent.size(); i++) {
                    if (Objects.equals(student.getId(), mListStudent.get(i).getId())) {
                        mListStudent.remove(i);
                        break;
                    }
                }

                // Kiểm tra trạng thái Fragment trước khi cập nhật giao diện
                if (isAdded() && getActivity() != null) {
                    updateRecyclerView();
                    updateDisplayedItemCount();
                } else {
                    Log.w("StudentFragment", "Fragment not attached to an activity. Skipping UI updates.");
                }
            }


            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // No action needed for this case
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentFragment", "Error loading students", error.toException());
            }
        });
    }

    private void updateRecyclerView() {
        if (isAdded() && getActivity() != null) { // Kiểm tra Fragment vẫn gắn với Activity
            requireActivity().runOnUiThread(() -> mStudentAdapter.notifyDataSetChanged());
        } else {
            Log.w("StudentFragment", "Cannot update RecyclerView: Fragment not attached to an activity.");
        }
    }


    // Phương thức chuyển đổi giữa các fragment
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void updateDisplayedItemCount(){
        int itemCount = mStudentAdapter.getItemCount();
        tv_display_results.setText("Kết quả: "+itemCount);
    }
    private void searchItemStudent(){
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

    private void searchList(String text) {
        ArrayList<Student> searchList = new ArrayList<>();
        for (Student student : mListStudent) {
            if (student.getId().toLowerCase().contains(text.toLowerCase()) ||
                    student.getTen_sinh_vien().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(student);
            }
        }
        mStudentAdapter.searchStudentList(searchList);
        updateDisplayedItemCount();
    }

    private void loadClassList(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListClass.add("Tất cả");
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Classroom classroom = dataSnapshot.getValue(Classroom.class);
                    if(classroom != null && classroom.getMa_lop() != null){
                        mListClass.add(classroom.getMa_lop());
                        listClassroom.add(classroom);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mListClass);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_filter_class.setAdapter(adapter);

        spinner_filter_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ma_lop = parent.getItemAtPosition(position).toString();
                filterStudentByClass(ma_lop);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void filterStudentByClass(String maLop){
        ArrayList<Student> filteredList = new ArrayList<>();

        Map<String, String> tenLopToIdLopMap = new HashMap<>();
        for(Classroom classroom : listClassroom){
            tenLopToIdLopMap.put(classroom.getMa_lop(), classroom.getId());
        }

        if(maLop.equals("Tất cả")){
            filteredList.addAll(mListStudent);
        }else{
            String idLop = tenLopToIdLopMap.get(maLop);
            if(idLop != null){
                for(Student student : mListStudent){
                    if(student.getId_lop().equals(idLop)){
                        filteredList.add(student);
                    }
                }
            }
        }

        mStudentAdapter.searchStudentList(filteredList);
        updateDisplayedItemCount();
    }

    private void openDetailFragment(Student student){
        DetailFragment detailFragment = new DetailFragment();

        // Truyền dữ liệu vào Bundle
        Bundle bundle = new Bundle();
        bundle.putString("ten_sinh_vien", student.getTen_sinh_vien());
        bundle.putString("ma_sinh_vien", student.getId());
        bundle.putString("ma_lop", idToCodeClassMap.get(student.getId_lop()));
        bundle.putString("ngay_sinh", student.getNgay_sinh());
        bundle.putString("gioi_tinh", student.getGioi_tinh());
        bundle.putString("dia_chi", student.getDia_chi());
        bundle.putString("SDT", student.getSo_dien_thoai());
        bundle.putString("email", student.getEmail());
        bundle.putString("ngay_nhap_hoc", student.getNgay_nhap_hoc());
        bundle.putString("trinh_do", idToNameLevelMap.get(student.getId_trinh_do()));
        bundle.putString("hinh_anh", student.getHinh_anh());
        bundle.putString("he_dao_tao", student.getHe_dao_tao());
        bundle.putString("id", student.getId());
        detailFragment.setArguments(bundle);

        // Chuyển sang DetailFragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();

    }
}