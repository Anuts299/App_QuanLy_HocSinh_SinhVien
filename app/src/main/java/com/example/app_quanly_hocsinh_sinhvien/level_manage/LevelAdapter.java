package com.example.app_quanly_hocsinh_sinhvien.level_manage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_quanly_hocsinh_sinhvien.R;

import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewModel>{

    private List<Level> mListLevel;
    private IClickListener mIClickListener;

    public interface IClickListener{
        void onClickUpdateItem(Level level);
        void onClickDeleteItem(Level level);
    }

    public LevelAdapter(List<Level> mListLevel, IClickListener mIClickListener) {
        this.mListLevel = mListLevel;
        this.mIClickListener = mIClickListener;
    }

    @NonNull
    @Override
    public LevelViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_level,parent,false);
        return new LevelViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewModel holder, int position) {
        Level level = mListLevel.get(position);
        if(level == null){
            return;
        }
        holder.tv_name_level.setText(level.getTen_trinh_do());

        holder.btn_update_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIClickListener.onClickUpdateItem(level);
            }
        });

        holder.btn_delete_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIClickListener.onClickDeleteItem(level);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListLevel != null){
            return mListLevel.size();
        }
        return 0;
    }

    public class LevelViewModel extends RecyclerView.ViewHolder{

        private TextView tv_name_level;
        private Button btn_update_level, btn_delete_level;

        public LevelViewModel(@NonNull View itemView) {
            super(itemView);
            tv_name_level = itemView.findViewById(R.id.tv_name_level);
            btn_update_level = itemView.findViewById(R.id.btn_update_level);
            btn_delete_level = itemView.findViewById(R.id.btn_delete_level);
        }
    }
}
