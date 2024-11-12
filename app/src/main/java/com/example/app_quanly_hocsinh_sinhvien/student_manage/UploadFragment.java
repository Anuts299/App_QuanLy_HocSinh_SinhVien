package com.example.app_quanly_hocsinh_sinhvien.student_manage;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.R;
import com.example.app_quanly_hocsinh_sinhvien.ui.FacultiesFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.HomeFragment;
import com.example.app_quanly_hocsinh_sinhvien.ui.StudentFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UploadFragment extends Fragment {

    private TextView breadcrumb_home, breadcrumb_student, tv_birthdate_student, tv_admissiondate_student;
    private EditText edt_code_student, edt_name_student, edt_locate_student, edt_phonenumber_student, edt_email_student;
    private Spinner spinner_gender, spinner_name_class, spinner_name_level;
    private Button btn_upload_student;
    private ImageView image_upload_student;
    private String imageURL;
    private Uri uri;
    // Định dạng ngày theo dd/MM/yyyy
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Adapter và danh sách cho Spinner
    private ArrayAdapter<String> classAdapter;
    private ArrayList<String> classList;
    private Map<String, String> classMap = new HashMap<>();

    // Adapter và danh sách cho Spinner
    private ArrayAdapter<String> levelAdapter;
    private ArrayList<String> levelList;
    private Map<String, String> levelMap = new HashMap<>();

    private DatabaseReference classesReference;
    private DatabaseReference facultyReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_student, container, false);

        initUi(view);
        initListener();
        classList = new ArrayList<>();
        levelList = new ArrayList<>();
        loadClassList();
        loadLevelList();
        classesReference = FirebaseDatabase.getInstance().getReference("CLASSROOM");
        facultyReference = FirebaseDatabase.getInstance().getReference("FACULTY");
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode() == Activity.RESULT_OK){
                            Intent data = o.getData();
                            uri = data.getData();
                            image_upload_student.setImageURI(uri);
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
        image_upload_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        btn_upload_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageStudent();
            }
        });



        return view;
    }

    private void initUi(View view){
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        breadcrumb_student = view.findViewById(R.id.breadcrumb_student);
        edt_code_student = view.findViewById(R.id.edt_code_student);
        edt_name_student = view.findViewById(R.id.edt_name_student);
        tv_birthdate_student = view.findViewById(R.id.tv_birthdate_student);
        edt_locate_student = view.findViewById(R.id.edt_locate_student);
        edt_phonenumber_student = view.findViewById(R.id.edt_phonenumber_student);
        edt_email_student = view.findViewById(R.id.edt_email_student);
        tv_admissiondate_student = view.findViewById(R.id.tv_admissiondate_student);
        image_upload_student = view.findViewById(R.id.image_upload_student);
        spinner_gender = view.findViewById(R.id.spinner_gender);
        // Tạo một mảng các giá trị cho Spinner
        String[] genders = {"Nam", "Nữ", "Khác"};

        // Tạo một ArrayAdapter
        ArrayAdapter<String> adapterGender = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_item, genders);

        // Thiết lập layout cho các item trong Spinner
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gán adapter cho Spinner
        spinner_gender.setAdapter(adapterGender);

        spinner_name_class = view.findViewById(R.id.spinner_name_class);
        spinner_name_level = view.findViewById(R.id.spinner_name_level);
        btn_upload_student = view.findViewById(R.id.btn_upload_student);
        image_upload_student = view.findViewById(R.id.image_upload_student);
    }

    private void initListener(){
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
        // Thiết lập sự kiện click cho EditText để mở DatePicker
        tv_birthdate_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(tv_birthdate_student);
            }
        });

        tv_admissiondate_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(tv_admissiondate_student);
            }
        });
    }

    private void showDatePickerDialog(final TextView textView) {
        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Định dạng ngày tháng năm và hiển thị lên EditText
                        String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        textView.setText(selectedDate);
                    }
                }, year, month, day);

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }

    private void uploadImageStudent() {
        // Kiểm tra nếu uri là null
        if (uri == null) {
            // Nếu uri là null, không lưu ảnh, chỉ gọi phương thức onClickUploadStudent
            onClickUploadStudent();
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
                onClickUploadStudent();
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
                spinner_name_class.setAdapter(classAdapter);
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
                spinner_name_level.setAdapter(levelAdapter);
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


    private void onClickUploadStudent() {
        String str_name_student = edt_name_student.getText().toString().trim();
        String birthdateString = tv_birthdate_student.getText().toString().trim();
        String admissiondateString = tv_admissiondate_student.getText().toString().trim();
        String str_locate_student = edt_locate_student.getText().toString().trim();
        String str_phone_student = edt_phonenumber_student.getText().toString().trim();
        String str_email_student = edt_email_student.getText().toString().trim();
        String str_gender_student = spinner_gender.getSelectedItem().toString().trim();

        // Sử dụng AtomicReference cho str_code_student
        AtomicReference<String> str_code_student = new AtomicReference<>("");

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

        String str_name_class = spinner_name_class.getSelectedItem().toString().trim();
        String id_name_class = classMap.get(str_name_class);
        String str_name_level = spinner_name_level.getSelectedItem().toString().trim();
        String id_name_level = levelMap.get(str_name_level);

        if (admissiondateString.isEmpty() || id_name_class == null || id_name_class.isEmpty()) {
            showAlert("Thiếu thông tin", "Mã lớp hoặc ngày nhập học không được bỏ trống.");
            return;
        }

        // Tạo mã sinh viên
        generateStudentID(admissiondateString, id_name_class, new FormatCodeCallback() {
            @Override
            public void onCallback(String studentID) {
                if (studentID != null) {
                    str_code_student.set(studentID); // Cập nhật giá trị qua AtomicReference
                    edt_code_student.setText(studentID);

                    // Kiểm tra tất cả các trường bắt buộc
                    if (str_code_student.get().isEmpty() || str_name_student.isEmpty() || str_locate_student.isEmpty() ||
                            str_phone_student.isEmpty() || str_email_student.isEmpty() || str_gender_student.isEmpty() ||
                            birthdateString.isEmpty() || admissiondateString.isEmpty() ||
                            id_name_class == null || id_name_level == null || id_name_level.isEmpty()) {

                        showAlert("Thiếu thông tin", "Vui lòng điền đầy đủ thông tin.");
                    } else {
                        Student student = new Student(str_code_student.get(), str_name_student, birthdateString, str_gender_student,
                                str_locate_student, str_phone_student, str_email_student, admissiondateString, imageURL, id_name_class, id_name_level);
                        saveDataStudent(student);
                    }
                } else {
                    Log.d("Student ID", "Không thể tạo mã sinh viên");
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

    // Hàm trợ giúp để phân tích ngày
    private LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void saveDataStudent(Student student){
        StudentFragment studentFragment = new StudentFragment();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("STUDENT");
        Query checkStudent = myRef.orderByChild("id").equalTo(student.getId());


        checkStudent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Mã sinh viên đã tồn tại")
                            .setContentText("Hãy chọn lại lớp hay năm nhập học một lần nữa")
                            .setConfirmText("OK")
                            .show();
                }else{
                    String pathObject = student.getId();
                    myRef.child(pathObject).setValue(student, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            showAlertSuccess("Thêm sinh viên thành công","Sinh viên đã được thêm vào danh sách");
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface FormatCodeCallback {
        void onCallback(String formatCode);
    }

    // Hàm tạo mã sinh viên đã chỉnh sửa
    private void generateStudentID(String birthdateString, String classCode, FormatCodeCallback callback) {
        // Lấy năm sinh
        String[] dateParts = birthdateString.split("/");
        String year = dateParts[2].substring(2); // Lấy 2 chữ số cuối cùng của năm sinh

        // Lấy mã khoa từ mã lớp (sử dụng callback để nhận giá trị bất đồng bộ)
        getDepartmentFormatCodeFromClass(classCode, new FormatCodeCallback() {
            @Override
            public void onCallback(String formatCode) {
                // Tạo 4 số ngẫu nhiên
                String randomDigits = String.format("%04d", new Random().nextInt(10000)); // Tạo 4 chữ số ngẫu nhiên

                // Tạo mã sinh viên
                String studentID = year + formatCode + randomDigits;

                // Trả về mã sinh viên qua callback
                callback.onCallback(studentID);
            }
        });
    }

    // Hàm lấy mã định dạng khoa từ mã lớp
    private void getDepartmentFormatCodeFromClass(String classId, FormatCodeCallback callback) {
        classesReference.child(classId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot classSnapshot) {
                if (classSnapshot.exists()) {
                    // Lấy id khoa từ lớp
                    String departmentId = classSnapshot.child("id_khoa").getValue(String.class);

                    // Lấy mã định dạng khoa từ ID khoa
                    getDepartmentFormatCodeFromId(departmentId, callback);
                } else {
                    Log.e("Class ID", "Lớp không tồn tại");
                    callback.onCallback(null); // Trả về null nếu không tìm thấy lớp
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database Error", databaseError.getMessage());
                callback.onCallback(null); // Trả về null trong trường hợp lỗi
            }
        });
    }

    // Hàm lấy mã định dạng khoa từ ID khoa
    private void getDepartmentFormatCodeFromId(String departmentId, FormatCodeCallback callback) {
        facultyReference.child(departmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot departmentSnapshot) {
                if (departmentSnapshot.exists()) {
                    String formatCode = departmentSnapshot.child("ma_dinh_dang").getValue(String.class);
                    callback.onCallback(formatCode); // Trả về mã định dạng qua callback
                } else {
                    Log.e("Department ID", "Khoa không tồn tại");
                    callback.onCallback(null); // Trả về null nếu không tìm thấy khoa
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database Error", databaseError.getMessage());
                callback.onCallback(null); // Trả về null trong trường hợp lỗi
            }
        });
    }


    // Phương thức chuyển đổi giữa các fragment
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}