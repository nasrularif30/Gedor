package com.movtech.gedor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.movtech.gedor.model.DataHistory;
import com.movtech.gedor.model.HistoryAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.movtech.gedor.Menu.DateDataFormat;

public class History extends AppCompatActivity {
    String id, activity, waktu;
    TextView tvStartDate, tvEndDate;
    CardView cvStartDate, cvEndDate;
    String startDate, endDate;
    Button btnTampil;
    Calendar mCalendar;
    Handler handler = new Handler();
    List<DataHistory> dataHistoryList;
    Runnable refresh;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mCalendar = Calendar.getInstance();
        tvStartDate = findViewById(R.id.tvtglawal);
        tvEndDate = findViewById(R.id.tvtglakhir);
        btnTampil = findViewById(R.id.btn_tampilhistory);
        cvStartDate = findViewById(R.id.tglawal);
        cvEndDate = findViewById(R.id.tglakhir);
        recyclerView = findViewById(R.id.rv_data);
        String childDate = DateDataFormat.format(Calendar.getInstance().getTime());
        dataHistoryList = new ArrayList<>();
        cvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(History.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        tvStartDate.setText(DateDataFormat.format(mCalendar.getTime()));
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        cvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(History.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        tvEndDate.setText(DateDataFormat.format(mCalendar.getTime()));
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnTampil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDate = tvStartDate.getText().toString();
                endDate = tvEndDate.getText().toString();
                Log.i("strt", "onClick: "+startDate+"_"+endDate);
                if (startDate.equals("--Pilih Tanggal Awal--")||startDate.isEmpty()){
                    new SweetAlertDialog(History.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Silakan Lengkapi Data!")
                            .setContentText("Tanggal awal dan akhir tidak boleh kosong")
                            .setConfirmText("OK")
                            .show();
                }
                else {
                    databaseReference.child("history").child(startDate).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            recyclerView.setVisibility(View.VISIBLE);
                            if (snapshot.getChildrenCount()>0){
                                dataHistoryList.clear();
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    try {
                                        id = dataSnapshot.child("id").getValue(String.class);
                                        waktu = dataSnapshot.child("time").getValue(String.class);
                                        activity = dataSnapshot.child("activity").getValue(String.class);
                                        dataHistoryList.add(new DataHistory(Integer.parseInt(id), waktu, activity));
                                    }
                                    catch (Exception e){
                                        Log.d("catche", "onDataChange: "+e);
                                    }
                                }
//                            Collections.reverse(dataHistoryList);
                                historyAdapter = new HistoryAdapter(dataHistoryList);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(History.this);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(historyAdapter);
                            }
                            else {
                                new SweetAlertDialog(History.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Data Tidak Tersedia")
                                        .setContentText("Tidak ada aktifitas pada tanggal tersebut")
                                        .show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            recyclerView.setVisibility(View.INVISIBLE);
                            new SweetAlertDialog(History.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Terjadi Masalah Database!")
                                    .setContentText("Periksa koneksi internet")
                                    .show();
                        }
                    });
                }
            }
        });
        Log.i("cdt", "onCreate: "+childDate);
        databaseReference.child("history").child(childDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0){
                    recyclerView.setVisibility(View.VISIBLE);
                    dataHistoryList.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        try {
                            id = dataSnapshot.child("id").getValue(String.class);
                            waktu = dataSnapshot.child("time").getValue(String.class);
                            activity = dataSnapshot.child("activity").getValue(String.class);
                            dataHistoryList.add(new DataHistory(Integer.parseInt(id), waktu, activity));
                        }
                        catch (Exception e){
                            Log.d("catche", "onDataChange: "+e);
                        }
                    }
//                            Collections.reverse(dataHistoryList);
                    historyAdapter = new HistoryAdapter(dataHistoryList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(History.this);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(historyAdapter);
                }
                else {
                    recyclerView.setVisibility(View.INVISIBLE);
                    new SweetAlertDialog(History.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Data Tidak Tersedia")
                            .setContentText("Tidak ada aktifitas pada tanggal tersebut")
                            .show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                recyclerView.setVisibility(View.INVISIBLE);
                new SweetAlertDialog(History.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Terjadi Masalah Database!")
                        .setContentText("Periksa koneksi internet")
                        .show();
            }
        });
    }
}