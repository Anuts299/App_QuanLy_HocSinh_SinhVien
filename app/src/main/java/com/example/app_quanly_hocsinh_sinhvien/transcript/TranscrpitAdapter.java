package com.example.app_quanly_hocsinh_sinhvien.transcript;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_quanly_hocsinh_sinhvien.FragmentActionListener;
import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.gradestype_manage.Gradestype;
import com.example.app_quanly_hocsinh_sinhvien.input_score.InputScore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TranscrpitAdapter extends RecyclerView.Adapter<TranscrpitAdapter.TranscrpitViewHolder>{


    private List<InputScore> mListInputScore;

    private Map<String, String> idToSubjectMap;
    private Map<String, Integer> idToCreditsMap;

    private FragmentActionListener listener;

    public void setOnTranscriptCalculatedListener(FragmentActionListener listener){
        this.listener = listener;
    }


    public TranscrpitAdapter(Map<String, Integer> idToCreditsMap, Map<String, String> idToSubjectMap, List<InputScore> mListInputScore) {
        this.idToCreditsMap = idToCreditsMap;
        this.idToSubjectMap = idToSubjectMap;
        this.mListInputScore = mListInputScore;
    }


    @NonNull
    @Override
    public TranscrpitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_transcript,parent,false);
        return new TranscrpitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TranscrpitViewHolder holder, int position) {
        InputScore inputScore = mListInputScore.get(position);
        if(inputScore == null){
            return;
        }
        holder.tv_code_subject.setText(inputScore.getId_mon_hoc());
        String tenmonhoc = idToSubjectMap.get(inputScore.getId_mon_hoc());
        if(tenmonhoc != null){
            holder.tv_name_subject.setText(tenmonhoc);
        }else{
            holder.tv_name_subject.setText("Tên môn không xác định");
        }
        Integer so_TC = idToCreditsMap.get(inputScore.getId_mon_hoc());
        if(so_TC != null){
            holder.tv_credit.setText(String.valueOf(so_TC));
        }else{
            holder.tv_credit.setText("Không xác định");
        }
        int sumKTTX = 0, sumKTDK = 0;
        int countKTTX = 0, countKTDK = 0;
        float tbm = 0.0F;
        for (int i = 0; i < inputScore.getDiemKTTX().size(); i++) {
            if (inputScore.getDiemKTTX().get(i) != null) {
                if (i == 0) {
                    holder.tv_grades_kttx1.setText(String.valueOf(inputScore.getDiemKTTX().get(i)));
                    sumKTTX += inputScore.getDiemKTTX().get(i);
                    countKTTX++;  // Tăng số lượng điểm KTTX hợp lệ
                } else if (i == 1) {
                    holder.tv_grades_kttx2.setText(String.valueOf(inputScore.getDiemKTTX().get(i)));
                    sumKTTX += inputScore.getDiemKTTX().get(i);
                    countKTTX++;
                } else if (i == 2) {
                    holder.tv_grades_kttx3.setText(String.valueOf(inputScore.getDiemKTTX().get(i)));
                    sumKTTX += inputScore.getDiemKTTX().get(i);
                    countKTTX++;
                }
            } else {
                holder.tv_grades_kttx1.setText("");
                holder.tv_grades_kttx2.setText("");
                holder.tv_grades_kttx3.setText("");
            }
        }

        for (int i = 0; i < inputScore.getDiemKTDK().size(); i++) {
            if (inputScore.getDiemKTDK().get(i) != null) {
                if (i == 0) {
                    holder.tv_grades_ktdk1.setText(String.valueOf(inputScore.getDiemKTDK().get(i)));
                    sumKTDK += inputScore.getDiemKTDK().get(i);
                    countKTDK++;  // Tăng số lượng điểm KTDK hợp lệ
                } else if (i == 1) {
                    holder.tv_grades_ktdk2.setText(String.valueOf(inputScore.getDiemKTDK().get(i)));
                    sumKTDK += inputScore.getDiemKTDK().get(i);
                    countKTDK++;
                } else if (i == 2) {
                    holder.tv_grades_ktdk3.setText(String.valueOf(inputScore.getDiemKTDK().get(i)));
                    sumKTDK += inputScore.getDiemKTDK().get(i);
                    countKTDK++;
                }
            } else {
                holder.tv_grades_ktdk1.setText("");
                holder.tv_grades_ktdk2.setText("");
                holder.tv_grades_ktdk3.setText("");
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        if (countKTTX == 3 && countKTDK == 3) {
            tbm = (float) (sumKTTX + (sumKTDK * 2)) / (3 + 3 * 2);  // Tổng điểm của 6 cột (3 KTTX + 3 KTDK)
            holder.tv_average_subject.setText(decimalFormat.format(tbm));
        } else {
            holder.tv_average_subject.setText("");
        }
        if (inputScore.getDiemThi().size() > 0 && inputScore.getDiemThi().get(inputScore.getDiemThi().size() - 1) != null) {
            holder.tv_final_exam1.setText(String.valueOf(inputScore.getDiemThi().get(inputScore.getDiemThi().size() - 1)));
        } else {
            holder.tv_final_exam1.setText("");
        }

        if (inputScore.getDiemThi2().size() > 0 && inputScore.getDiemThi2().get(inputScore.getDiemThi2().size() - 1) != null) {
            holder.tv_final_exam2.setText(String.valueOf(inputScore.getDiemThi2().get(inputScore.getDiemThi2().size() - 1)));
        } else {
            holder.tv_final_exam2.setText("");
        }
        String str_thi1 = holder.tv_final_exam1.getText().toString().trim();
        String str_thi2 = holder.tv_final_exam2.getText().toString().trim();
        float tbhp = 0.0F;

        if (tbm != 0.0F) {
            try {
                if (!str_thi2.isEmpty()) {
                    float thi2 = Float.parseFloat(str_thi2); // Chuyển đổi str_thi2 thành float
                    tbhp = (sumKTTX + (sumKTDK * 2) + (thi2 * 3)) / (3 + 3 * 2 + 3);
                } else if (!str_thi1.isEmpty()) {
                    float thi1 = Float.parseFloat(str_thi1); // Chuyển đổi str_thi1 thành float
                    tbhp = (sumKTTX + (sumKTDK * 2) + (thi1 * 3)) / (3 + 3 * 2 + 3);
                }

                if(tbhp >= 9.20){
                    holder.tv_average_grade.setText("A"+"("+decimalFormat.format(tbhp)+")");
                }else if(tbhp >= 8.5){
                    holder.tv_average_grade.setText("B+"+"("+decimalFormat.format(tbhp)+")");
                } else if (tbhp >= 7.5){
                    holder.tv_average_grade.setText("B"+"("+decimalFormat.format(tbhp)+")");
                } else if (tbhp >= 6.8){
                    holder.tv_average_grade.setText("B-"+"("+decimalFormat.format(tbhp)+")");
                }else if (tbhp >= 6.3){
                    holder.tv_average_grade.setText("C+"+"("+decimalFormat.format(tbhp)+")");
                } else if (tbhp >= 5.5){
                    holder.tv_average_grade.setText("C"+"("+decimalFormat.format(tbhp)+")");
                }else if (tbhp >= 4.8){
                    holder.tv_average_grade.setText("C-"+"("+decimalFormat.format(tbhp)+")");
                }else if (tbhp >= 4){
                    holder.tv_average_grade.setText("D+"+"("+decimalFormat.format(tbhp)+")");
                }else if (tbhp >= 3.5){
                    holder.tv_average_grade.setText("D"+"("+decimalFormat.format(tbhp)+")");
                }else if (tbhp >= 3){
                    holder.tv_average_grade.setText("D-"+"("+decimalFormat.format(tbhp)+")");
                } else {
                    holder.tv_average_grade.setText("F"+"("+decimalFormat.format(tbhp)+")");
                }


            } catch (NumberFormatException e) {
                // Xử lý ngoại lệ khi đầu vào không hợp lệ
                e.printStackTrace();
            }
        }
        if (listener != null) {
            listener.onTranscriptCalculated(tbm, tbhp, position);
        }
    }

    @Override
    public int getItemCount() {
        if(mListInputScore != null){
            return mListInputScore.size();
        }
        return 0;
    }

    public class TranscrpitViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name_subject, tv_code_subject, tv_credit, tv_grades_kttx1, tv_grades_kttx2, tv_grades_kttx3, tv_grades_ktdk1, tv_grades_ktdk2, tv_grades_ktdk3
                , tv_average_subject, tv_final_exam1, tv_final_exam2, tv_average_grade;

        public TranscrpitViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_subject = itemView.findViewById(R.id.tv_name_subject);
            tv_code_subject = itemView.findViewById(R.id.tv_code_subject);
            tv_credit = itemView.findViewById(R.id.tv_credit);
            tv_grades_kttx1 = itemView.findViewById(R.id.tv_grades_kttx1);
            tv_grades_kttx2 = itemView.findViewById(R.id.tv_grades_kttx2);
            tv_grades_kttx3 = itemView.findViewById(R.id.tv_grades_kttx3);
            tv_grades_ktdk1 = itemView.findViewById(R.id.tv_grades_ktdk1);
            tv_grades_ktdk2 = itemView.findViewById(R.id.tv_grades_ktdk2);
            tv_grades_ktdk3 = itemView.findViewById(R.id.tv_grades_ktdk3);
            tv_average_subject = itemView.findViewById(R.id.tv_average_subject);
            tv_final_exam1 = itemView.findViewById(R.id.tv_final_exam1);
            tv_final_exam2 = itemView.findViewById(R.id.tv_final_exam2);
            tv_average_grade = itemView.findViewById(R.id.tv_average_grade);
        }
    }
}
