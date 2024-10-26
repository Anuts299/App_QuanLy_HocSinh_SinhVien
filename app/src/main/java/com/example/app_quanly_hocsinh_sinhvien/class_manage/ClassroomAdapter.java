package com.example.app_quanly_hocsinh_sinhvien.class_manage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_quanly_hocsinh_sinhvien.R;

import java.util.ArrayList;
import java.util.List;

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.ClassroomViewHolder> {

    private List<Classroom> mListClassroom;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Classroom classroom);
    }

    // Constructor mới
    public ClassroomAdapter(List<Classroom> mListClassroom, OnItemClickListener listener) {
        this.mListClassroom = mListClassroom;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_clasroom,parent,false);
        return new ClassroomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomViewHolder holder, int position) {
        Classroom classroom = mListClassroom.get(position);
        if(classroom == null){
            return;
        }
        holder.tv_code_class.setText(classroom.getMa_lop());
        holder.tv_lecturer_class.setText(classroom.getTen_co_van());

        // Thiết lập sự kiện click
        holder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(classroom);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(mListClassroom != null){
            return mListClassroom.size();
        }
        return 0;
    }

    public void searchClassroomList(ArrayList<Classroom> searchList){
        mListClassroom = searchList;
        notifyDataSetChanged();
    }

    public class ClassroomViewHolder extends RecyclerView.ViewHolder{

        TextView tv_code_class,tv_lecturer_class;

        public ClassroomViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_code_class = itemView.findViewById(R.id.tv_code_class);
            tv_lecturer_class = itemView.findViewById(R.id.tv_lecturer_class);
        }
    }

}


