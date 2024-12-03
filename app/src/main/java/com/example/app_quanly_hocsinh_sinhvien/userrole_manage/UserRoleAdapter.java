package com.example.app_quanly_hocsinh_sinhvien.userrole_manage;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.drawable.Drawable;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.student_manage.Student;
import com.example.app_quanly_hocsinh_sinhvien.student_manage.StudentAdapter;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class UserRoleAdapter extends  RecyclerView.Adapter<UserRoleAdapter.UserRoleViewHolder>{

    private List<UserModel> mListUser;
    private OnClickItemListener mListener;

    public interface OnClickItemListener{
        void onItemClick(UserModel userModel);
    }

    public UserRoleAdapter(List<UserModel> mListUser, OnClickItemListener mListener) {
        this.mListener = mListener;
        this.mListUser = mListUser;
    }

    @NonNull
    @Override
    public UserRoleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item_user, viewGroup, false);
        return new UserRoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRoleViewHolder userRoleViewHolder, int i) {
        UserModel user = mListUser.get(i);
        if (user == null) {
            return;
        }

        userRoleViewHolder.tv_name_user.setText(user.getName() != null ? user.getName() : "");
        userRoleViewHolder.tv_email_user.setText(user.getEmail() != null ? user.getEmail() : "Chưa có email");
        userRoleViewHolder.tv_role_user.setText(user.getRole() != null ? user.getRole() : "Không xác định");

        if ("Giảng viên".equals(user.getRole())) {
            userRoleViewHolder.recImageUser.setImageDrawable(
                    ContextCompat.getDrawable(userRoleViewHolder.recImageUser.getContext(), R.drawable.user_9073143)
            );
        } else if ("Quản trị viên".equals(user.getRole())) {
            userRoleViewHolder.recImageUser.setImageDrawable(
                    ContextCompat.getDrawable(userRoleViewHolder.recImageUser.getContext(), R.drawable.user)
            );
        } else {
            userRoleViewHolder.recImageUser.setImageDrawable(
                    ContextCompat.getDrawable(userRoleViewHolder.recImageUser.getContext(), R.drawable.user_9073143)
            );
        }
        // Thiết lập sự kiện click
        userRoleViewHolder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListUser != null ? mListUser.size() : 0;
    }

    public void searchUserList(ArrayList<UserModel> searchList){
        mListUser = searchList;
        notifyDataSetChanged();
    }

    public class UserRoleViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_name_user, tv_email_user, tv_role_user;
        private ImageView recImageUser;
        private CardView recCardUser;

        public UserRoleViewHolder(@NonNull View itemView) {
            super(itemView);
            recImageUser = itemView.findViewById(R.id.recImageUser);
            tv_name_user = itemView.findViewById(R.id.tv_user_name);
            tv_email_user = itemView.findViewById(R.id.tv_user_email);
            tv_role_user = itemView.findViewById(R.id.tv_user_role);
            recCardUser = itemView.findViewById(R.id.recCardUser);
        }
    }
}
