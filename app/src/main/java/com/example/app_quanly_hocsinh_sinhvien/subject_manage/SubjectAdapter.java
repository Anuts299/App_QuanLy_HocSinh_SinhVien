package com.example.app_quanly_hocsinh_sinhvien.subject_manage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_quanly_hocsinh_sinhvien.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>{

    private List<Subject> mListSubject;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(Subject subject);
    }

    public SubjectAdapter(List<Subject> mListSubject, OnItemClickListener listener) {
        this.mListSubject = mListSubject;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = mListSubject.get(position);
        if (subject == null){
            return;
        }
        holder.tv_name_subject.setText(subject.getTen_mon_hoc());
        holder.tv_code_subject.setText(subject.getId()+" - ");
        holder.tv_credit_subject.setText("Số ĐVHT: "+subject.getSo_dvht());
        // Thiết lập sự kiện click
        holder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(subject);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListSubject != null){
            return mListSubject.size();
        }
        return 0;
    }
    public void searchSubjectList(ArrayList<Subject> searchList){
        mListSubject = searchList;
        notifyDataSetChanged();
    }
    public class SubjectViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name_subject, tv_code_subject, tv_credit_subject;


        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_subject = itemView.findViewById(R.id.tv_name_subject);
            tv_code_subject = itemView.findViewById(R.id.tv_code_subject);
            tv_credit_subject = itemView.findViewById(R.id.tv_credit_subject);
        }
    }
}
