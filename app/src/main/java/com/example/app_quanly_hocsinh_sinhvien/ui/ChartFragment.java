package com.example.app_quanly_hocsinh_sinhvien.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app_quanly_hocsinh_sinhvien.FragmentActionListener;
import com.example.app_quanly_hocsinh_sinhvien.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChartFragment extends Fragment {
    private Spinner spinner_year;
    private TextView breadcrumb_home;
    private BarChart chart_sd_fa;
    private LineChart chart_sd_aca;

    private FragmentActionListener mListenerHome;

    private List<String> xValues = new ArrayList<>();
    private boolean isDataReady = false;

    private List<String> yearList = new ArrayList<>();
    private ArrayAdapter<String> yearAdapter;


    // Tạo danh sách lưu số lượng sinh viên cho mỗi khoa
    private Map<String, Integer> khoaSinhVienMap = new HashMap<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActionListener) {
            mListenerHome = (FragmentActionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentActionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        spinner_year = view.findViewById(R.id.spinner_year);
        breadcrumb_home = view.findViewById(R.id.breadcrumb_home);
        chart_sd_fa = view.findViewById(R.id.chart_sd_fa);
        chart_sd_aca = view.findViewById(R.id.chart_sd_aca);
        chart_sd_fa.getAxisRight().setDrawLabels(false);
        chart_sd_aca.getAxisRight().setDrawLabels(false);

        // Lấy dữ liệu từ Firebase và tính số lượng sinh viên của mỗi khoa
        getDataFromFirebase();
        populateYears();
        setupSpinner();
        // Đợi Firebase trả về số liệu và cập nhật biểu đồ
        ArrayList<BarEntry> entries = new ArrayList<>();
        int index = 0;
        for (String khoa : xValues) {
            int count = khoaSinhVienMap.getOrDefault(khoa, 0);
            entries.add(new BarEntry(index++, count));
        }

        // Tùy chỉnh trục Y (Left)
        YAxis yAxis = chart_sd_fa.getAxisLeft();
        yAxis.setAxisMinimum(0f);  // Min value
        yAxis.setAxisMaximum(10f);  // Max value (tùy chỉnh cho phù hợp)
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);

        // Tạo Dataset và gán vào biểu đồ
        BarDataSet dataSet = new BarDataSet(entries, "Faculty");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);  // Chọn màu cho các cột

        // Tạo BarData và gán vào biểu đồ
        BarData barData = new BarData(dataSet);
        chart_sd_fa.setData(barData);

        // Tắt mô tả (description) trên biểu đồ
        chart_sd_fa.getDescription().setEnabled(false);

        // Tùy chỉnh trục X
        chart_sd_fa.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));  // Cập nhật giá trị cho trục X
        chart_sd_fa.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart_sd_fa.getXAxis().setGranularity(1f);
        chart_sd_fa.getXAxis().setGranularityEnabled(true);

        // Làm mới biểu đồ
        chart_sd_fa.invalidate();
        initListener();
        return view;
    }

    private void getDataFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference sinhVienRef = database.getReference("STUDENT");
        DatabaseReference lopRef = database.getReference("CLASSROOM");
        DatabaseReference khoaRef = database.getReference("FACULTY");

        sinhVienRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalStudents = (int) dataSnapshot.getChildrenCount();
                final int[] processedStudents = {0};

                if (totalStudents == 0) {
                    Log.d("ChartFragment", "No students found in Firebase.");
                    return;
                }

                for (DataSnapshot sinhVienSnapshot : dataSnapshot.getChildren()) {
                    String idLop = sinhVienSnapshot.child("id_lop").getValue(String.class);

                    if (idLop == null) {
                        Log.w("ChartFragment", "id_lop is null for student: " + sinhVienSnapshot.getKey());
                        checkAllProcessed(processedStudents, totalStudents);
                        continue;
                    }

                    lopRef.child(idLop).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot lopSnapshot) {
                            String idKhoa = lopSnapshot.child("id_khoa").getValue(String.class);

                            if (idKhoa == null) {
                                Log.w("ChartFragment", "id_khoa is null for class: " + lopSnapshot.getKey());
                                checkAllProcessed(processedStudents, totalStudents);
                                return;
                            }

                            khoaRef.child(idKhoa).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot khoaSnapshot) {
                                    String khoaName = khoaSnapshot.child("ten_khoa").getValue(String.class);
                                    if (khoaName != null) {
                                        // Cắt bớt tên khoa nếu dài quá
                                        if (khoaName.length() > 15) {  // Giới hạn chiều dài tên khoa là 20 ký tự
                                            khoaName = khoaName.substring(0, 15) + "...";
                                        }

                                        khoaSinhVienMap.put(khoaName, khoaSinhVienMap.getOrDefault(khoaName, 0) + 1);

                                        if (!xValues.contains(khoaName)) {
                                            xValues.add(khoaName);
                                        }
                                    }

                                    checkAllProcessed(processedStudents, totalStudents);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("ChartFragment", "Error reading faculty data: " + databaseError.getMessage());
                                    checkAllProcessed(processedStudents, totalStudents);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("ChartFragment", "Error reading classroom data: " + databaseError.getMessage());
                            checkAllProcessed(processedStudents, totalStudents);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChartFragment", "Error reading student data: " + databaseError.getMessage());
            }
        });
    }

    // Hàm kiểm tra nếu tất cả sinh viên đã được xử lý
    private void checkAllProcessed(int[] processedStudents, int totalStudents) {
        processedStudents[0]++;
        if (processedStudents[0] == totalStudents) {
            Log.d("ChartFragment", "All students processed.");
            isDataReady = true;
            updateChart();
        }
    }




    private void updateChart() {
        if (khoaSinhVienMap.isEmpty()) {
            return;
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        int index = 0;

        for (String khoa : xValues) {
            int count = khoaSinhVienMap.getOrDefault(khoa, 0);
            entries.add(new BarEntry(index++, count));
        }


        BarDataSet dataSet = new BarDataSet(entries, "Khoa");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Chuyển thành số nguyên
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        chart_sd_fa.setData(barData);
        chart_sd_fa.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
        chart_sd_fa.invalidate();
    }
    // Hàm cập nhật biểu đồ đường
    private void updateLineChart(Map<Integer, Integer> monthStudentCount) {
        List<Entry> entries = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            int count = monthStudentCount.getOrDefault(month, 0);  // Nếu không có dữ liệu thì mặc định là 0
            entries.add(new Entry(month, count));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "Sinh viên nhập học");
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(14f);
        lineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Chuyển thành số nguyên
            }
        });
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.LTGRAY);

        LineData lineData = new LineData(lineDataSet);
        chart_sd_aca.setData(lineData);
        chart_sd_aca.invalidate();  // Cập nhật biểu đồ
    }
    // Lấy danh sách các năm từ Firebase
    private void populateYears() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference studentRef = database.getReference("STUDENT");

        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> yearsSet = new HashSet<>();
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String ngayNhapHoc = studentSnapshot.child("ngay_nhap_hoc").getValue(String.class);
                    if (ngayNhapHoc != null) {
                        String year = ngayNhapHoc.split("/")[2]; // Lấy năm từ ngày nhập học
                        yearsSet.add(year);
                    }
                }

                // Chuyển đổi Set thành List và cập nhật Spinner
                yearList.clear();
                yearList.addAll(yearsSet);
                Collections.sort(yearList);  // Sắp xếp theo thứ tự tăng dần
                yearAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChartFragment", "Error getting student data: " + databaseError.getMessage());
            }
        });
    }
    // Cài đặt Spinner với dữ liệu năm
    private void setupSpinner() {
        yearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(yearAdapter);

        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedYear = parentView.getItemAtPosition(position).toString();
                updateChartForYear(selectedYear);  // Cập nhật biểu đồ khi người dùng chọn năm
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    // Hàm cập nhật biểu đồ khi chọn năm
    private void updateChartForYear(String selectedYear) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference studentRef = database.getReference("STUDENT");

        // Map lưu trữ số lượng sinh viên nhập học theo tháng
        Map<Integer, Integer> monthStudentCount = new HashMap<>();

        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Khởi tạo lại map cho mỗi lần chọn năm
                monthStudentCount.clear();

                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String ngayNhapHoc = studentSnapshot.child("ngay_nhap_hoc").getValue(String.class);
                    if (ngayNhapHoc != null && ngayNhapHoc.split("/")[2].equals(selectedYear)) {
                        int month = Integer.parseInt(ngayNhapHoc.split("/")[1]);  // Lấy tháng từ ngày nhập học
                        monthStudentCount.put(month, monthStudentCount.getOrDefault(month, 0) + 1);
                    }
                }

                // Cập nhật biểu đồ đường (line chart)
                updateLineChart(monthStudentCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChartFragment", "Error getting student data: " + databaseError.getMessage());
            }
        });
    }




    private void initListener() {
        breadcrumb_home.setOnClickListener(v -> {
            if (mListenerHome != null) {
                mListenerHome.onFragmentAction(R.id.nav_home);
            }
        });
    }
}


