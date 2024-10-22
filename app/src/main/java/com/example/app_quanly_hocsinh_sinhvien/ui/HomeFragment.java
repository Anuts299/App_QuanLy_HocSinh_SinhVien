package com.example.app_quanly_hocsinh_sinhvien.ui;

import static com.example.app_quanly_hocsinh_sinhvien.MainActivity.role_lecturer;
import static com.example.app_quanly_hocsinh_sinhvien.MainActivity.role_student;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class HomeFragment extends Fragment {
    private CardView class_card, student_card, subject_card, faculties_card, input_score_card, teacher_card;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userRole;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Khởi tạo Firebase Firestore và FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        initUi(view);
        getUserRole();//Lấy vai trò của người dùng
        return view;
    }
    // Phương thức ánh xạ UI
    private void initUi(View view) {
        class_card = view.findViewById(R.id.class_card);
        student_card = view.findViewById(R.id.student_card);
        subject_card = view.findViewById(R.id.subject_card);
        faculties_card = view.findViewById(R.id.faculties_card);
        input_score_card = view.findViewById(R.id.input_score_card);
        teacher_card = view.findViewById(R.id.teacher_card);
    }
    //Lấy vai tro của người dùng từ FireBase
    private void getUserRole(){
        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userRole = documentSnapshot.getString("role"); // Lưu vai trò người dùng
                        initListener();  // Sau khi có vai trò, thiết lập các sự kiện
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi nếu không thể lấy dữ liệu
                    Toast.makeText(getContext(), "Lỗi lấy vai trò người dùng", Toast.LENGTH_SHORT).show();
                });
    }
    private void initListener() {
        class_card.setOnClickListener(v -> {
            // Chuyển sang ClassFragment
            ClassFragment classFragment = new ClassFragment();
            switchFragment(classFragment);
        });
        student_card.setOnClickListener(v -> {
            //Chuyển sang StudentFragment
            StudentFragment studentFragment = new StudentFragment();
            switchFragment(studentFragment);
        });
        subject_card.setOnClickListener(v -> {
            //Chuyển sang SubjectFragment
            SubjectFragment subjectFragment = new SubjectFragment();
            switchFragment(subjectFragment);
        });
        faculties_card.setOnClickListener(v -> {
            //Kiểm tra vai trò cua nguoi dung
            if(role_student.equals(userRole)){
                new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Không thể truy cập")
                        .setContentText("Bạn không được cấp quyền truy cập.")
                        .setConfirmText("OK")
                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show();
            }else{
                //Chuyển sang FacultiesFragment
                FacultiesFragment facultiesFragment = new FacultiesFragment();
                switchFragment(facultiesFragment);
            }
        });
        input_score_card.setOnClickListener(v -> {
            //Chuyển sang FacultiesFragment
            GradesFragment gradesFragment = new GradesFragment();
            switchFragment(gradesFragment);
        });
        teacher_card.setOnClickListener(v -> {
            //Chuyển sang FacultiesFragment
            TeacherFragment teacherFragment = new TeacherFragment();
            switchFragment(teacherFragment);
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
}