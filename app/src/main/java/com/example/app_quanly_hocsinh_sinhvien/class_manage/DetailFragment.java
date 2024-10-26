package com.example.app_quanly_hocsinh_sinhvien.class_manage;

import static android.content.Intent.getIntent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;


public class DetailFragment extends Fragment {

    TextView tv_de_name_class, tv_de_name_faculties, tv_de_name_lecturer, tv_de_academic_year;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_class, container, false);
        initUi(view);
        Bundle bundle = getArguments();
        if(bundle != null){
            tv_de_name_class.setText(bundle.getString("ma_lop"));
            tv_de_name_faculties.setText(bundle.getString("ten_khoa"));
            tv_de_name_lecturer.setText(bundle.getString("ten_co_van"));
            tv_de_academic_year.setText(bundle.getString("nam_hoc"));
        }
        return view;

    }
    private void initUi(View view){
        tv_de_name_class = view.findViewById(R.id.tv_de_name_class);
        tv_de_name_faculties = view.findViewById(R.id.tv_de_name_faculties);
        tv_de_name_lecturer = view.findViewById(R.id.tv_de_name_lecturer);
        tv_de_academic_year = view.findViewById(R.id.tv_de_academic_year);
    }
}