package com.example.app_quanly_hocsinh_sinhvien.gradestype_manage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_quanly_hocsinh_sinhvien.R;

import java.util.List;

public class GradestypeAdapter extends RecyclerView.Adapter<GradestypeAdapter.GradesTypeViewModel>{

    private List<Gradestype> mListGradesType;
    private IClickListener mIClickListener;


    public interface IClickListener{
        void onClickUpdateItem(Gradestype gradestype);
        void onClickDetailItem(Gradestype gradestype);
    }

    public GradestypeAdapter(List<Gradestype> mListGradesType, IClickListener mIClickListener) {
        this.mListGradesType = mListGradesType;
        this.mIClickListener = mIClickListener;
    }

    @NonNull
    @Override
    public GradesTypeViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_gradestype,parent,false);
        return new GradesTypeViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradesTypeViewModel holder, int position) {
        Gradestype gradestype = mListGradesType.get(position);
        if(gradestype == null){
            return;
        }
        holder.tv_name_gradestype.setText(gradestype.getTen_loai_diem());
        holder.tv_weight.setText("Hệ số: "+gradestype.getHe_so());

        holder.btn_update_grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIClickListener.onClickUpdateItem(gradestype);
            }
        });

        holder.btn_delete_grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIClickListener.onClickDetailItem(gradestype);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListGradesType != null){
            return mListGradesType.size();
        }
        return 0;
    }

    public class GradesTypeViewModel extends RecyclerView.ViewHolder{

        private TextView tv_name_gradestype, tv_weight;
        private Button btn_update_grade, btn_delete_grade;

        public GradesTypeViewModel(@NonNull View itemView) {
            super(itemView);
            tv_name_gradestype = itemView.findViewById(R.id.tv_name_gradestype);
            tv_weight = itemView.findViewById(R.id.tv_weight);
            btn_update_grade = itemView.findViewById(R.id.btn_update_grade);
            btn_delete_grade = itemView.findViewById(R.id.btn_delete_grade);
        }
    }
}
