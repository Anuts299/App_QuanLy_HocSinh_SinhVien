package com.example.app_quanly_hocsinh_sinhvien.student_manage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.StudentFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UpdateFragment extends Fragment {

    private EditText edt_code_student, edt_ud_name_student, edt_ud_locate_student, edt_ud_phonenumber_student, edt_ud_email_student;
    private Spinner spinner_ud_gender, spinner_ud_name_class, spinner_ud_name_level;
    private Button btn_upload_student;
    private DatabaseReference reference;
    private ImageView image_ud_student;
    private String id = "";
    private TextView breadcrumb_home, breadcrumb_student, tv_ud_admissiondate_student, tv_ud_birthdate_student;
    private Uri uri;
    private String imageURL;

    private ArrayAdapter<String> classAdapter;
    private ArrayList<String> classList;
    private Map<String, String> classMap = new HashMap<>();

    // Adapter và danh sách cho Spinner
    private ArrayAdapter<String> levelAdapter;
    private ArrayList<String> levelList;
    private Map<String, String> levelMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_student, container, false);
        initUi(view);
        classList = new ArrayList<>();
        levelList = new ArrayList<>();
        loadClassList();
        loadLevelList();
        Bundle bundle = getArguments();
        if(bundle != null){
            edt_code_student.setText(bundle.getString("ma_sinh_vien"));
            edt_ud_name_student.setText(bundle.getString("ten_sinh_vien"));
            edt_ud_locate_student.setText(bundle.getString("dia_chi"));
            edt_ud_phonenumber_student.setText(bundle.getString("SDT"));
            edt_ud_email_student.setText(bundle.getString("email"));
            tv_ud_admissiondate_student.setText(bundle.getString("ngay_nhap_hoc"));
            tv_ud_birthdate_student.setText(bundle.getString("ngay_sinh"));
            imageURL = bundle.getString("hinh_anh");
            Glide.with(getActivity()).load(bundle.getString("hinh_anh")).into(image_ud_student);
            String gender = bundle.getString("gioi_tinh");
            if (gender != null) {
                int genderPosition = ((ArrayAdapter<String>) spinner_ud_gender.getAdapter()).getPosition(gender);
                spinner_ud_gender.setSelection(genderPosition);
            }
            String className = bundle.getString("ma_lop");
            // Sử dụng `post` để trì hoãn việc thiết lập `classPosition` cho đến khi adapter đã được gán vào spinner
            view.post(() -> {
                if (className != null && spinner_ud_name_class.getAdapter() != null) {
                    int classPosition = ((ArrayAdapter<String>) spinner_ud_name_class.getAdapter()).getPosition(className);
                    spinner_ud_name_class.setSelection(classPosition);
                } else {
                    Log.e("UpdateFragment", "Adapter is not set or className is null");
                }
            });

            String level = bundle.getString("trinh_do");
            // Sử dụng `post` để trì hoãn việc thiết lập `classPosition` cho đến khi adapter đã được gán vào spinner
            view.post(() -> {
                if (level != null && spinner_ud_name_level.getAdapter() != null) {
                    int levelPosition = ((ArrayAdapter<String>) spinner_ud_name_level.getAdapter()).getPosition(level);
                    spinner_ud_name_level.setSelection(levelPosition);
                } else {
                    Log.e("UpdateFragment", "Adapter is not set or level is null");
                }
            });
            id = bundle.getString("id");
            reference = FirebaseDatabase.getInstance().getReference("STUDENT");
            btn_upload_student.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadImageStudent();
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
            ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult o) {
                            if(o.getResultCode() == Activity.RESULT_OK){
                                Intent data = o.getData();
                                uri = data.getData();
                                image_ud_student.setImageURI(uri);
                            } else{
                                new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Không có ảnh nào được chọn")
                                        .setContentText("Vui lòng thử lại và chọn một ảnh.")
                                        .setConfirmText("OK")
                                        .show();
                            }
                        }
                    }
            );
            image_ud_student.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoPicker = new Intent(Intent.ACTION_PICK);
                    photoPicker.setType("image/*");
                    activityResultLauncher.launch(photoPicker);
                }
            });

        }
        return view;
    }

    private void initUi(View view){
        edt_code_student = view.findViewById(R.id.edt_code_student);
        edt_ud_name_student = view.findViewById(R.id.edt_ud_name_student);
        edt_ud_locate_student = view.findViewById(R.id.edt_ud_locate_student);
        edt_ud_phonenumber_student = view.findViewById(R.id.edt_ud_phonenumber_student);
        edt_ud_email_student = view.findViewById(R.id.edt_ud_email_student);
        spinner_ud_gender = view.findViewById(R.id.spinner_ud_gender);
        spinner_ud_name_class = view.findViewById(R.id.spinner_ud_name_class);
        spinner_ud_name_level = view.findViewById(R.id.spinner_ud_name_level);
        btn_upload_student = view.findViewById(R.id.btn_upload_student);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_student = view.findViewById(R.id.breadcrumb_student);
        tv_ud_admissiondate_student = view.findViewById(R.id.tv_ud_admissiondate_student);
        tv_ud_birthdate_student = view.findViewById(R.id.tv_ud_birthdate_student);
        image_ud_student = view.findViewById(R.id.image_ud_student);
        // Tạo một mảng các giá trị cho Spinner
        String[] genders = {"Nam", "Nữ", "Khác"};

        // Tạo một ArrayAdapter
        ArrayAdapter<String> adapterGender = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_item, genders);

        // Thiết lập layout cho các item trong Spinner
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_ud_gender.setAdapter(adapterGender);
    }
    private void loadClassList(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("CLASSROOM");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classList.clear();
                for(DataSnapshot classSnapshot : snapshot.getChildren()){
                    String id_class = classSnapshot.getKey();
                    String name_class = classSnapshot.child("ma_lop").getValue(String.class);
                    if(id_class != null && name_class != null){
                        classList.add(name_class);
                        Log.d("UploadFragment", "Class loaded: " + name_class);
                        classMap.put(name_class, id_class);
                    }
                }
                // Khởi tạo ArrayAdapter cho Spinner và tải dữ liệu giảng viên từ Firebase
                classAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, classList);
                classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_ud_name_class.setAdapter(classAdapter);
                classAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Tải danh sách thất bại")
                        .setConfirmText("OK")
                        .show();
            }
        });
    }

    private void loadLevelList(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("LEVEL");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                levelList.clear();
                for(DataSnapshot levelSnapshot : snapshot.getChildren()){
                    String id_level = levelSnapshot.getKey();
                    String name_level = levelSnapshot.child("ten_trinh_do").getValue(String.class);
                    if(id_level != null && name_level != null){
                        levelList.add(name_level);
                        Log.d("UploadFragment", "Level loaded: " + name_level);
                        levelMap.put(name_level, id_level);
                    }
                }
                // Khởi tạo ArrayAdapter cho Spinner và tải dữ liệu TRÌNH ĐỘ từ Firebase
                levelAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, levelList);
                levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_ud_name_level.setAdapter(levelAdapter);
                levelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Tải danh sách thất bại")
                        .setConfirmText("OK")
                        .show();
            }
        });
    }
    private void onClickButtonUpdateStudent(){
        String str_name_student = edt_ud_name_student.getText().toString().trim();
        String birthdateString = tv_ud_birthdate_student.getText().toString().trim();
        String admissiondateString = tv_ud_admissiondate_student.getText().toString().trim();
        String str_locate_student = edt_ud_locate_student.getText().toString().trim();
        String str_phone_student = edt_ud_phonenumber_student.getText().toString().trim();
        String str_email_student = edt_ud_email_student.getText().toString().trim();
        String str_gender_student = spinner_ud_gender.getSelectedItem().toString().trim();

        // Kiểm tra số điện thoại
        if (!str_phone_student.matches("\\d{10}")) {
            showAlert("Lỗi dữ liệu nhập", "Hãy nhập đúng số điện thoại (10 chữ số)");
            return;
        }

        // Kiểm tra định dạng email
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!str_email_student.matches(emailPattern)) {
            showAlert("Lỗi dữ liệu nhập", "Hãy nhập đúng email (abc@gmail.com)");
            return;
        }

        // Kiểm tra định dạng ngày sinh và ngày nhập học (giữ dưới dạng String)
        if (birthdateString.isEmpty() || admissiondateString.isEmpty()) {
            showAlert("Lỗi định dạng ngày tháng", "Ngày tháng không hợp lệ. Vui lòng kiểm tra lại định dạng (vd: dd/mm/yyyy).");
            return;
        }

        String str_name_class = spinner_ud_name_class.getSelectedItem().toString().trim();
        String id_name_class = classMap.get(str_name_class);
        String str_name_level = spinner_ud_name_level.getSelectedItem().toString().trim();
        String id_name_level = levelMap.get(str_name_level);

        if (admissiondateString.isEmpty() || id_name_class == null || id_name_class.isEmpty()) {
            showAlert("Thiếu thông tin", "Mã lớp hoặc ngày nhập học không được bỏ trống.");
            return;
        }

        if (str_name_student.isEmpty() || str_locate_student.isEmpty() ||
                str_phone_student.isEmpty() || str_email_student.isEmpty() || str_gender_student.isEmpty() ||
                birthdateString.isEmpty() || admissiondateString.isEmpty() ||
                id_name_class == null || id_name_level == null || id_name_level.isEmpty()) {

            showAlert("Thiếu thông tin", "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        Student student = new Student(id, str_name_student, birthdateString, str_gender_student,
                    str_locate_student, str_phone_student, str_email_student, admissiondateString, imageURL, id_name_class, id_name_level);

        reference.child(id).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Chỉnh sửa sinh viên thành công")
                            .setConfirmText("OK")
                            .setConfirmClickListener(sDialog -> {
                                sDialog.dismissWithAnimation();
                                Bundle resultBundle = new Bundle();
                                resultBundle.putString("ten_sinh_vien", str_name_student);
                                resultBundle.putString("ma_lop", str_name_class);
                                resultBundle.putString("ngay_sinh", birthdateString);
                                resultBundle.putString("gioi_tinh", str_gender_student);
                                resultBundle.putString("dia_chi", str_locate_student);
                                resultBundle.putString("SDT", str_phone_student);
                                resultBundle.putString("email", str_email_student);
                                resultBundle.putString("ngay_nhap_hoc", admissiondateString);
                                resultBundle.putString("trinh_do", str_name_level);
                                resultBundle.putString("hinh_anh", imageURL);
                                resultBundle.putString("ma_sinh_vien", id);
                                DetailFragment detailFragment = new DetailFragment();
                                detailFragment.setArguments(resultBundle);

                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, detailFragment)
                                        .addToBackStack(null)
                                        .commit();
                            })
                            .show();
                } else {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Lỗi!")
                            .setContentText("Không thể cập nhật lớp.")
                            .setConfirmText("OK")
                            .show();
                }
            }
        });
    }
    // Hàm trợ giúp để hiển thị cảnh báo
    private void showAlert(String title, String content) {
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("OK")
                .show();
    }

    // Hàm trợ giúp để hiển thị cảnh báo
    private void showAlertSuccess(String title, String content) {
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("OK")
                .show();
    }
    private void uploadImageStudent() {
        // Kiểm tra nếu uri là null
        if (uri == null) {

            onClickButtonUpdateStudent();
            return; // Thoát khỏi phương thức
        }

        // Nếu uri không null thì tiếp tục thực hiện upload ảnh
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Studify Images")
                .child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                onClickButtonUpdateStudent();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Student ID", "Đã có lỗi xảy ra: " + e.getMessage());
                dialog.dismiss();
            }
        });
    }
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null); // Thêm dòng này để fragment vào back stack
        fragmentTransaction.commit();
    }
}