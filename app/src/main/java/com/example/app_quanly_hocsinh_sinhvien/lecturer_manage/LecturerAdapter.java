package com.example.app_quanly_hocsinh_sinhvien.lecturer_manage;

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
import java.util.Map;

public class LecturerAdapter extends RecyclerView.Adapter<LecturerAdapter.LecturerViewHolder>{

    //phần hiện thị các giảng viên
    private List<Lecturer> mListLecturer;

    private Map<String, String> idToFacultyNameMap;

    private Map<String, String> idToLevelNameMap;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Lecturer lecturer);
    }

    //- phần hiển thị các giảng viên
    public LecturerAdapter(List<Lecturer> mListLecturer, Map<String, String> idToFacultyNameMap, Map<String, String> idToLevelNameMap, OnItemClickListener listener) {
        this.mListLecturer = mListLecturer;
        this.idToFacultyNameMap = idToFacultyNameMap;
        this.idToLevelNameMap = idToLevelNameMap;
        this.mListener = listener;
    }

    //Phần hiển thị các giảng viên
    @NonNull
    @Override
    public LecturerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_lecturer, parent, false);
        return new LecturerViewHolder(view);
    }

    //- Phần hiển thị các giảng viên
    @Override
    public void onBindViewHolder(@NonNull LecturerViewHolder holder, int position) {
        Lecturer lecturer = mListLecturer.get(position);
        if(lecturer == null){
            return;
        }
        holder.tv_name_lecturer.setText(lecturer.getTen_giang_vien());

        // Get faculty name using the id_khoa
        String tenKhoa = idToFacultyNameMap.get(lecturer.getId_khoa());
        holder.tv_name_faculty.setText(tenKhoa != null ? tenKhoa : "Khoa không xác định");

        // Thiết lập sự kiện click
        holder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(lecturer);
            }
        });
    }

    //Phần hiển thị các giảng viên
    @Override
    public int getItemCount() {
        if(mListLecturer != null){
            return mListLecturer.size();
        }
        return 0;
    }

    public void searchLecturerList(ArrayList<Lecturer> searchList){
        mListLecturer = searchList;
        notifyDataSetChanged();
    }

    //Phần hiển thị các giảng viên
    public class LecturerViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name_lecturer, tv_name_faculty;

        public LecturerViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_lecturer = itemView.findViewById(R.id.tv_name_lecturer);
            tv_name_faculty = itemView.findViewById(R.id.tv_name_faculty);
        }
    }
}
