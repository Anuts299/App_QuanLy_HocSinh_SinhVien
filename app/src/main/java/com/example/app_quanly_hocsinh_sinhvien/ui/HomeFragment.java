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
    private CardView class_card;

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
    }
}