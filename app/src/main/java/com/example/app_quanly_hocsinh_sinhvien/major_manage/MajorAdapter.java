package com.example.app_quanly_hocsinh_sinhvien.major_manage;

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

public class MajorAdapter extends RecyclerView.Adapter<MajorAdapter.MajorViewHolder>{

    private List<Major> mListMajor;
    private OnItemClickListener mListener;

    private Map<String, String> idToLevelMap;


    public interface OnItemClickListener{
        void onItemClick(Major major);
    }

    public MajorAdapter(List<Major> mListMajor, Map<String, String> idToLevelMap, OnItemClickListener listener) {
        this.mListMajor = mListMajor;
        this.idToLevelMap = idToLevelMap;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MajorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_major,parent,false);
        return new MajorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MajorViewHolder holder, int position) {
        Major major = mListMajor.get(position);
        if(major == null){
            return;
        }
        holder.tv_name_major.setText(major.getTen_chuyen_nganh());

        String tenTrinhdo = idToLevelMap.get(major.getId_trinh_do());
        holder.tv_name_level_major.setText(tenTrinhdo != null ? tenTrinhdo : "Trình độ không xác định");

        // Thiết lập sự kiện click
        holder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(major);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListMajor != null){
            return mListMajor.size();
        }
        return 0;
    }

    public void searchMajorList(ArrayList<Major> searchList){
        mListMajor = searchList;
        notifyDataSetChanged();
    }

    public class MajorViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name_major, tv_name_level_major;

        public MajorViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_major = itemView.findViewById(R.id.tv_name_major);
            tv_name_level_major = itemView.findViewById(R.id.tv_name_level_major);
        }
    }
}
