package com.example.app_quanly_hocsinh_sinhvien.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.FragmentActionListener;
import com.example.app_quanly_hocsinh_sinhvien.R;


public class InfoFragment extends Fragment {
    private TextView breadcrumb_home;
    private FragmentActionListener mListenerHome;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListenerHome != null) {
                    mListenerHome.onFragmentAction(R.id.nav_home);
                }
            }
        });
        return view;
    }
}