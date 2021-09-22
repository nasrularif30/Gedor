package com.movtech.gedor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    EditText etid, etUsername, etPassword;
    String id, username, nama, password;
    int getId;


    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btn_login);
        etPassword = findViewById(R.id.et_password);
        etUsername = findViewById(R.id.et_username);
        etid = findViewById(R.id.et_idfinger);
        id = etid.getText().toString();
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = etid.getText().toString();
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                Log.i("uauau", "onClick: "+databaseReference.child("user").getKey());
                if (id.isEmpty()||username.isEmpty()||password.isEmpty()){
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("DATA HARUS LENGKAP")
                            .setContentText("Silakan isi data dengan benar")
                            .show();
                }
                else {
                    databaseReference.child("user").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(id)){
                                String dtUsername = snapshot.child(id).child("username").getValue(String.class);
                                String dtpassword = snapshot.child(id).child("password").getValue(String.class);
                                if (username.equals(dtUsername) && password.equals(dtpassword)){
                                    Intent intent = new Intent(LoginActivity.this, History.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("ERROR")
                                            .setContentText("Silakan periksa data anda kembali!")
                                            .show();
                                }
                            }

                            else {
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("ID BELUM TERDAFTAR")
                                        .setContentText("Silakan daftar terlebih dahulu!")
                                        .show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });

    }
}