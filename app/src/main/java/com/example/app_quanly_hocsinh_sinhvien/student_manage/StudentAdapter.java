package com.example.app_quanly_hocsinh_sinhvien.student_manage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_quanly_hocsinh_sinhvien.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder>{

    private List<Student> mListStudent;
    private Map<String, String> idToClassMap;
    private Map<String, String> idToLevelMap;

    private OnClickItemListener mListener;

    public interface OnClickItemListener{
        void onItemClick(Student student);
    }

    public StudentAdapter(Map<String, String> idToClassMap, Map<String, String> idToLevelMap, List<Student> mListStudent, OnClickItemListener listener) {
        this.idToClassMap = idToClassMap;
        this.idToLevelMap = idToLevelMap;
        this.mListStudent = mListStudent;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = mListStudent.get(position);
        if(student == null){
            return;
        }
        holder.tv_name_student.setText(student.getTen_sinh_vien());
        holder.tv_code_student.setText(student.getId());
        // Tải hình ảnh từ URL hoặc đường dẫn
        String imagePath = student.getHinh_anh();
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imagePath)
                    .into(holder.recImageStudent);
        } else {

            holder.recImageStudent.setImageResource(R.drawable.students_2995459);
        }

        String maLop = idToClassMap.get(student.getId_lop());
        holder.tv_code_class_student.setText(maLop != null ? maLop : "Mã lớp không xác định");

        String tenTrinhdo = idToLevelMap.get(student.getId_trinh_do());
        holder.tv_level_student.setText(tenTrinhdo != null ? tenTrinhdo : "Trình độ không xác định");

        // Thiết lập sự kiện click
        holder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListStudent != null){
            return mListStudent.size();
        }
        return 0;
    }

    public void searchStudentList(ArrayList<Student> searchList){
        mListStudent = searchList;
        notifyDataSetChanged();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder{

        ImageView recImageStudent;
        TextView tv_name_student, tv_code_student, tv_code_class_student, tv_level_student;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            recImageStudent = itemView.findViewById(R.id.recImageStudent);
            tv_name_student = itemView.findViewById(R.id.tv_name_student);
            tv_code_student = itemView.findViewById(R.id.tv_code_student);
            tv_code_class_student = itemView.findViewById(R.id.tv_code_class_student);
            tv_level_student = itemView.findViewById(R.id.tv_level_student);
        }
    }

}
