package com.example.app_quanly_hocsinh_sinhvien.faculty_manage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.class_manage.Classroom;
import com.example.app_quanly_hocsinh_sinhvien.class_manage.ClassroomAdapter;

import java.util.ArrayList;
import java.util.List;

public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder>{

    private List<Faculty> mListFaculty;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Faculty faculty);
    }

    public FacultyAdapter(List<Faculty> mListFaculty, OnItemClickListener listener) {
        this.mListFaculty = mListFaculty;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public FacultyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_faculty, parent,false);
        return new FacultyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultyViewHolder holder, int position) {
        Faculty faculty = mListFaculty.get(position);
        if(faculty == null){
            return;
        }
        holder.tv_name_faculty.setText(faculty.getTen_khoa());

        // Thiết lập sự kiện click
        holder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(faculty);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListFaculty != null){
            return mListFaculty.size();
        }
        return 0;
    }

    public void searchFacultyList(ArrayList<Faculty> searchList){
        mListFaculty = searchList;
        notifyDataSetChanged();
    }

    public class FacultyViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_name_faculty;

        public FacultyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_faculty = itemView.findViewById(R.id.tv_name_faculty);
        }
    }

}
