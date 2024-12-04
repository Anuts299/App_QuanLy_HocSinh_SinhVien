package com.example.app_quanly_hocsinh_sinhvien.student_manage;

import static androidx.core.content.ContextCompat.getSystemService;

import static com.example.app_quanly_hocsinh_sinhvien.MainActivity.userRole;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.app_quanly_hocsinh_sinhvien.FragmentActionListener;
import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.gradestype_manage.Gradestype;
import com.example.app_quanly_hocsinh_sinhvien.input_score.InputScore;
import com.example.app_quanly_hocsinh_sinhvien.transcript.TranscrpitAdapter;
import com.example.app_quanly_hocsinh_sinhvien.ui.ClassFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.StudentFragment;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class DetailFragment extends Fragment implements FragmentActionListener {

    TextView breadcrumb_home, breadcrumb_student, tv_de_name_student, tv_de_code_student, tv_de_code_class, tv_de_birthday, tv_de_gender_student, tv_de_program,
            tv_de_locate_student, tv_de_phonenumber_student, tv_de_email_student, tv_de_admissiondate_student, tv_de_name_level, tv_sum_credit, tv_sum_avg_10, tv_sum_avg_4, tv_classification;
    ImageView image_de_student;
    FloatingActionButton editButtonStudent, deleteButtonStudent;
    String id = "",ma_lop, hinh_anh;
    private RecyclerView recyView_transcript;
    private TranscrpitAdapter mtranscrpitAdapter;
    private List<InputScore> mTranscriptList;

    private Map<String, String> idToSubjectMap = new HashMap<>();
    private Map<String, Integer> idToCreditsMap = new HashMap<>();

    private int totalSoTC = 0;
    private float totalTBHP = 0;
    private int countTBHP = 0;


    @Override
    public void onFragmentAction(int actionId) {

    }

    @Override
    public void onTranscriptCalculated(int soTC, float tbhp, int position) {
        totalSoTC += soTC;
        tv_sum_credit.setText(""+totalSoTC);

        totalTBHP += tbhp;
        countTBHP++;
        float averageTBHP = totalTBHP / countTBHP;
        float averageScale4 = convertToScale4(averageTBHP);
        tv_sum_avg_10.setText(String.format("%.2f", averageTBHP));
        tv_sum_avg_4.setText(String.format("%.2f", averageScale4));
        tv_classification.setText(getClassification(totalSoTC, tbhp));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_student, container, false);
        mTranscriptList = new ArrayList<>();
        initUi(view);
        Bundle bundle = getArguments();
        if(bundle != null){
            tv_de_name_student.setText(bundle.getString("ten_sinh_vien"));
            tv_de_code_student.setText(bundle.getString("ma_sinh_vien"));
            tv_de_code_class.setText(bundle.getString("ma_lop"));
            ma_lop = bundle.getString("ma_lop");
            tv_de_birthday.setText(bundle.getString("ngay_sinh"));
            tv_de_gender_student.setText(bundle.getString("gioi_tinh"));
            tv_de_locate_student.setText(bundle.getString("dia_chi"));
            tv_de_phonenumber_student.setText(bundle.getString("SDT"));
            tv_de_email_student.setText(bundle.getString("email"));
            tv_de_admissiondate_student.setText(bundle.getString("ngay_nhap_hoc"));
            tv_de_name_level.setText(bundle.getString("trinh_do"));
            tv_de_program.setText(bundle.getString("he_dao_tao"));
            hinh_anh = bundle.getString("hinh_anh");
            Glide.with(getActivity()).load(bundle.getString("hinh_anh")).into(image_de_student);
            id = bundle.getString("id");
        }
        createIdToSubjectCodeMap();
        initListener();
        loadTableRecap();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadTranscript();
    }

    private void initUi(View view){
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_student = view.findViewById(R.id.breadcrumb_student);
        tv_de_name_student = view.findViewById(R.id.tv_de_name_student);
        tv_de_code_student = view.findViewById(R.id.tv_de_code_student);
        tv_de_code_class = view.findViewById(R.id.tv_de_code_class);
        tv_de_birthday = view.findViewById(R.id.tv_de_birthday);
        tv_de_gender_student = view.findViewById(R.id.tv_de_gender_student);
        tv_de_locate_student = view.findViewById(R.id.tv_de_locate_student);
        tv_de_phonenumber_student = view.findViewById(R.id.tv_de_phonenumber_student);
        tv_de_email_student = view.findViewById(R.id.tv_de_email_student);
        tv_de_admissiondate_student = view.findViewById(R.id.tv_de_admissiondate_student);
        tv_de_name_level = view.findViewById(R.id.tv_de_name_level);
        tv_sum_credit = view.findViewById(R.id.tv_sum_credit);
        tv_sum_avg_10 = view.findViewById(R.id.tv_sum_avg_10);
        tv_sum_avg_4 = view.findViewById(R.id.tv_sum_avg_4);
        tv_classification = view.findViewById(R.id.tv_classification);
        tv_de_program = view.findViewById(R.id.tv_de_program);
        editButtonStudent = view.findViewById(R.id.editButtonStudent);
        deleteButtonStudent = view.findViewById(R.id.deleteButtonStudent);
        image_de_student = view.findViewById(R.id.image_de_student);
        recyView_transcript = view.findViewById(R.id.recyView_transcript);

        mtranscrpitAdapter = new TranscrpitAdapter(idToCreditsMap, idToSubjectMap, mTranscriptList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyView_transcript.setLayoutManager(linearLayoutManager);
        recyView_transcript.setAdapter(mtranscrpitAdapter);
    }

    private void initListener(){
        deleteButtonStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userRole.equals("Quản trị viên")){
                    // Hiển thị SweetAlertDialog xác nhận xóa
                    new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Xác nhận xóa")
                            .setContentText("Bạn có chắc chắn muốn xóa sinh viên này không?")
                            .setConfirmText("Xóa")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                    builder.setCancelable(false);
                                    builder.setView(R.layout.progress_layout);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    // Xác nhận xóa - thực hiện xóa trong Firebase
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("STUDENT");
                                    reference.child(id)
                                            .removeValue()
                                            .addOnCompleteListener(task -> {
                                                dialog.dismiss();
                                                if (task.isSuccessful()) {
                                                    sDialog
                                                            .setTitleText("Đã xóa!")
                                                            .setContentText("Sinh viên đã được xóa.")
                                                            .setConfirmText("OK")
                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                @Override
                                                                public void onClick(SweetAlertDialog sDialog) {
                                                                    sDialog.dismiss();

                                                                    switchFragment(new StudentFragment());
                                                                }
                                                            })
                                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);


                                                } else {
                                                    sDialog
                                                            .setTitleText("Lỗi!")
                                                            .setContentText("Không thể xóa sinh viên.")
                                                            .setConfirmText("OK")
                                                            .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                                }
                                            });

                                }
                            })
                            .setCancelButton("Hủy", SweetAlertDialog::dismiss)
                            .show();
                }else{
                    showAccessDeniedAlert();
                }
            }
        });

            editButtonStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(userRole.equals("Quản trị viên")) {
                        Bundle bundle = new Bundle();
                        String tenSV = tv_de_name_student.getText().toString().trim();
                        String maSV = tv_de_code_student.getText().toString().trim();
                        String maLop = tv_de_code_class.getText().toString().trim();
                        String ngaySinh = tv_de_birthday.getText().toString().trim();
                        String gioiTinh = tv_de_gender_student.getText().toString().trim();
                        String diaChi = tv_de_locate_student.getText().toString().trim();
                        String SDT = tv_de_phonenumber_student.getText().toString().trim();
                        String email = tv_de_email_student.getText().toString().trim();
                        String ngayNhapHoc = tv_de_admissiondate_student.getText().toString().trim();
                        String trinhDo = tv_de_name_level.getText().toString().trim();
                        String heDaoTao = tv_de_program.getText().toString().trim();

                        bundle.putString("ten_sinh_vien", tenSV);
                        bundle.putString("ma_sinh_vien", maSV);
                        bundle.putString("ma_lop", maLop);
                        bundle.putString("ngay_sinh", ngaySinh);
                        bundle.putString("gioi_tinh", gioiTinh);
                        bundle.putString("dia_chi", diaChi);
                        bundle.putString("SDT", SDT);
                        bundle.putString("email", email);
                        bundle.putString("ngay_nhap_hoc", ngayNhapHoc);
                        bundle.putString("trinh_do", trinhDo);
                        bundle.putString("hinh_anh", hinh_anh);
                        bundle.putString("he_dao_tao", heDaoTao);
                        bundle.putString("id", id);

                        UpdateFragment updateFragment = new UpdateFragment();
                        updateFragment.setArguments(bundle);

                        try {
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, updateFragment)
                                    .addToBackStack(null)
                                    .commit();
                            Log.d("EditButton", "Fragment replaced successfully");
                        } catch (Exception e) {
                            Log.e("EditButton", "Error replacing fragment", e);
                        }
                    }else{
                        showAccessDeniedAlert();
                    }
                }
            });


        breadcrumb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new HomeFragment());
            }
        });

        breadcrumb_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new StudentFragment());
            }
        });
    }

    private void loadTranscript() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TRANSCRIPT");
        if (id != null && !id.isEmpty()) {
            reference.orderByChild("id_sinh_vien").equalTo(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mTranscriptList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            InputScore inputScore = data.getValue(InputScore.class);
                            if (inputScore != null) {
                                mTranscriptList.add(inputScore);
                            } else {
                                Log.d("DetailFragment", "Data is null for a child");
                            }
                        }
                        if (mtranscrpitAdapter != null) {
                            mtranscrpitAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("DetailFragment", "Adapter is null");
                        }
                    } else {
                        Log.d("DetailFragment", "Không có dữ liệu trong Firebase.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("DetailFragment", "Lỗi Firebase: " + error.getMessage());
                }
            });
        } else {
            Log.d("DetailFragment", "id của sinh viên không hợp lệ");
        }
    }

    private void createIdToSubjectCodeMap(){
        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("SUBJECT");
        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot subjectSnapshot : snapshot.getChildren()){
                    String id_subject = subjectSnapshot.getKey();
                    String name_subject = subjectSnapshot.child("ten_mon_hoc").getValue(String.class);
                    Integer so_TC = subjectSnapshot.child("so_dvht").getValue(Integer.class);
                    if(id_subject != null && name_subject != null && so_TC != null){
                        idToSubjectMap.put(id_subject, name_subject);
                        idToCreditsMap.put(id_subject, so_TC);
                    }
                }
                mtranscrpitAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentFragment", "Error loading Student", error.toException());
            }
        });
    }
    private void loadTableRecap(){
        mtranscrpitAdapter.setOnTranscriptCalculatedListener(this);
    }
    private float convertToScale4(float score10) {
        if (score10 >= 8.5) return 4.0f;
        if (score10 >= 8.0) return 3.7f;
        if (score10 >= 7.0) return 3.0f;
        if (score10 >= 6.5) return 2.7f;
        if (score10 >= 5.5) return 2.0f;
        if (score10 >= 5.0) return 1.7f;
        if (score10 >= 4.0) return 1.0f;
        return 0.0f;
    }
    private String getClassification(int totalSoTC, float tbhp) {
        // Ví dụ phân loại theo tổng tín chỉ và TBHP
        if (tbhp >= 9.0 && totalSoTC >= 80) return "Xuất sắc";
        else if (tbhp >= 8.0 && totalSoTC >= 70) return "Giỏi";
        else if (tbhp >= 7.0 && totalSoTC >= 60) return "Khá";
        else if (tbhp >= 5.0 && totalSoTC >= 50) return "Trung bình";
        return "Yếu";
    }
    private File createExcelFile() throws IOException {
        // Tạo workbook và sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Danh Sách Sinh Viên");

        // Tạo dòng và các ô
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("STT");
        headerRow.createCell(1).setCellValue("Họ Tên");
        headerRow.createCell(2).setCellValue("Lớp");

        // Thêm dữ liệu mẫu
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("1");
        dataRow.createCell(1).setCellValue("Nguyen Van A");
        dataRow.createCell(2).setCellValue("10A1");

        // Lưu file vào bộ nhớ
        File fileDirectory = requireContext().getExternalFilesDir(null);
        if (fileDirectory == null) {
            throw new IOException("Không thể truy cập thư mục bộ nhớ.");
        }
        File file = new File(fileDirectory, "DanhSachSinhVien.xlsx");
        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();

        return file;
    }

    private void downloadFile(File file) {
        DownloadManager downloadManager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            Uri fileUri = Uri.fromFile(file);
            DownloadManager.Request request = new DownloadManager.Request(fileUri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file.getName());
            downloadManager.enqueue(request);
        }
    }
    private void showAccessDeniedAlert(){
        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Bạn không đủ quyền để sử dụng chức năng này")
                .setConfirmText("OK")
                .setConfirmClickListener(Dialog -> {
                    Dialog.dismissWithAnimation();
                })
                .show();
    }
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
//        fragmentTransaction.addToBackStack(null); // Thêm dòng này để fragment vào back stack
        fragmentTransaction.commit();
    }



}