package com.movtech.gedor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.movtech.gedor.Menu.DateDataFormat;

public class Daftar extends AppCompatActivity {
    Button btnDaftar;
    EditText etUsername, etNama, etPassword, etId;
    String id, username, nama, password;
    int getId, count;
    SharedPreferences sharedPreferences;
    boolean sudahLogin;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        sharedPreferences = getSharedPreferences("data_user", Context.MODE_PRIVATE);

        btnDaftar = findViewById(R.id.btn_daftar);
        etId = findViewById(R.id.et_idfinger);
        etNama = findViewById(R.id.et_nama);
        etPassword = findViewById(R.id.et_password);
        etUsername = findViewById(R.id.et_username);


        databaseReference.child("temp_id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(Integer.class)!=0){
                    getId = snapshot.getValue(Integer.class);
                    etId.setText(String.valueOf(getId));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = etId.getText().toString();
                nama = etNama.getText().toString();
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                Log.i("ututu", "onClick: "+id+nama+username+password);
                if (id.isEmpty()||nama.isEmpty()||username.isEmpty()||password.isEmpty()){
                    new SweetAlertDialog(Daftar.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("DATA HARUS LENGKAP")
                            .setContentText("Silakan isi data dengan benar")
                            .show();
                }
                else {
                    Map<String, Object> values = new HashMap<>();
                    values.put("id", Integer.parseInt(id));
                    values.put("nama", nama);
                    values.put("username", username);
                    values.put("password", password);
                    databaseReference.child("user").child(id).setValue(values);
                    new SweetAlertDialog(Daftar.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Apakah anda ingin masuk?")
                            .setContentText("klik tidak untuk kembali ke menu!")
                            .setCancelText("Tidak")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent intent = new Intent(Daftar.this, Menu.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setConfirmText("Ya")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

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
                                    Intent i = new Intent(Daftar.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            })
                            .show();
                }
            }
        });
        etId.setFocusableInTouchMode(false);
        etId.setClickable(false);
    }
}