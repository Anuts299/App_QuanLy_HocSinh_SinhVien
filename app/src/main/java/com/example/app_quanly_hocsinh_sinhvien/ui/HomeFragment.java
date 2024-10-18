package com.example.app_quanly_hocsinh_sinhvien.ui;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app_quanly_hocsinh_sinhvien.R;


public class HomeFragment extends Fragment {
    private CardView class_card, student_card, subject_card, faculties_card, input_score_card, teacher_card;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initUi(view);
        initListener();
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

    private void initListener() {
        class_card.setOnClickListener(v -> {
            // Chuyển sang ClassFragment
            ClassFragment classFragment = new ClassFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, classFragment); // ID của container chứa Fragment
            fragmentTransaction.addToBackStack(null); // Thêm vào back stack để có thể quay lại
            fragmentTransaction.commit();
        });
        student_card.setOnClickListener(v -> {
            //Chuyển sang StudentFragment
            StudentFragment studentFragment = new StudentFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,studentFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        subject_card.setOnClickListener(v -> {
            //Chuyển sang SubjectFragment
            SubjectFragment subjectFragment = new SubjectFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, subjectFragment );
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        faculties_card.setOnClickListener(v -> {
            //Chuyển sang FacultiesFragment
            FacultiesFragment facultiesFragment = new FacultiesFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, facultiesFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        input_score_card.setOnClickListener(v -> {
            //Chuyển sang FacultiesFragment
            GradesFragment gradesFragment = new GradesFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, gradesFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        teacher_card.setOnClickListener(v -> {
            //Chuyển sang FacultiesFragment
            TeacherFragment teacherFragment = new TeacherFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, teacherFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

    }
}