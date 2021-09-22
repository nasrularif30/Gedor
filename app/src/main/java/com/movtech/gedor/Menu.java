package com.movtech.gedor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Menu extends AppCompatActivity {
    Button btnMasuk, btnDaftar, btnHistory;
    SharedPreferences sharedPreferences;
    boolean sudahLogin;
    int count;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    final public static SimpleDateFormat DateDataFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("id","ID"));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnDaftar = findViewById(R.id.btn_daftar);
        btnHistory = findViewById(R.id.btn_history);
        btnMasuk = findViewById(R.id.btn_masuk);

        sharedPreferences = getSharedPreferences("data_user", Context.MODE_PRIVATE);

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("kontrol").setValue("67");
                databaseReference.child("temp_id").setValue(0);
                Intent intent = new Intent(Menu.this, Daftar.class);
                startActivity(intent);

            }
        });

        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog progressDialog = new SweetAlertDialog(Menu.this, SweetAlertDialog.PROGRESS_TYPE);
                progressDialog.getProgressHelper().setBarColor(Color.BLUE);
                progressDialog.setTitleText("Scanning...");
                progressDialog.setCustomImage(R.drawable.ic_fingerprint_icon);
                progressDialog.setContentText("Silakan tempelkan jari anda ke sensor fingerprint!");
                progressDialog.setCancelable(true);

                databaseReference.child("temp_id").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String id = String.valueOf(snapshot.getValue(Integer.class));
                        progressDialog.show();

                        databaseReference.child("kontrol").setValue("55");
                            if(snapshot.getValue(Integer.class)!=0) {
                                progressDialog.cancel();
                                progressDialog.dismiss();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("id", id);
                                editor.putBoolean("sudahLogin", true);
                                editor.apply();
                                String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                                String childDate = DateDataFormat.format(Calendar.getInstance().getTime());
                                Map<String, Object> values = new HashMap<>();
                                values.put("id", id);
                                values.put("time",currentDateTimeString);
                                values.put("activity", "checkin");
                                databaseReference.child("active_user").child(id).child("checkin").setValue(currentDateTimeString);
                                databaseReference.child("history").child(childDate).push().setValue(values);
                                databaseReference.child("kontrol").setValue("66");
                                databaseReference.child("counter_active_user").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        count = snapshot.getValue(Integer.class);
                                        databaseReference.child("counter_active_user").setValue(count+1);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                databaseReference.child("temp_id").setValue(0);
                                Intent i = new Intent(Menu.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.cancel();
                        SweetAlertDialog alertDialog = new SweetAlertDialog(Menu.this, SweetAlertDialog.ERROR_TYPE);
                        alertDialog.setTitleText("Mohon maaf, ada kesalahan database");
                        alertDialog.dismissWithAnimation();
                    }
                });
            }
        });
    }
}