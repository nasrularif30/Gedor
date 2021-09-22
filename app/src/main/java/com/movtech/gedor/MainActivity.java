package com.movtech.gedor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLEngineResult;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.movtech.gedor.Menu.DateDataFormat;

public class MainActivity extends AppCompatActivity {
    CardView cvLampu, cvKipas, cvPintu, cvCheckout;
    ImageView ivLampu, ivKipas, ivPintu, ivCheckout, ivHistory;
    TextView tvLampu, tvKipas, tvPintu, tvCheckout;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("data_user", Context.MODE_PRIVATE);

        cvCheckout = findViewById(R.id.cv_checkout);
        cvKipas = findViewById(R.id.cv_kipas);
        cvLampu = findViewById(R.id.cv_lampu);
        cvPintu = findViewById(R.id.cv_pintu);

        ivCheckout = findViewById(R.id.iv_checkout);
        ivKipas = findViewById(R.id.iv_kipas);
        ivLampu = findViewById(R.id.iv_lampu);
        ivPintu = findViewById(R.id.iv_pintu);
        ivHistory = findViewById(R.id.iv_history);

        tvKipas = findViewById(R.id.tv_state_kipas);
        tvLampu = findViewById(R.id.tv_state_lampu);
        tvPintu = findViewById(R.id.tv_state_pintu);

        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, History.class);
                startActivity(intent);
            }
        });
        String id = sharedPreferences.getString("id", null);
        databaseReference.child("control").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("fan").getValue(String.class).equals("ON")){
                    tvKipas.setText("ON");
                }
                if (snapshot.child("fan").getValue(String.class).equals("OFF")){
                    tvKipas.setText("OFF");
                }
                if (snapshot.child("lamp").getValue(String.class).equals("ON")){
                    tvLampu.setText("ON");
                }
                if (snapshot.child("lamp").getValue(String.class).equals("OFF")){
                    tvLampu.setText("OFF");
                }
                if (snapshot.child("door").getValue(String.class).equals("ON")){
                    tvPintu.setText("OPEN");
                }
                if (snapshot.child("door").getValue(String.class).equals("OFF")){
                    tvPintu.setText("CLOSE");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("ERROR")
                        .setContentText("Terjadi Masalah Database!")
                        .show();
            }
        });
        ivKipas.setOnClickListener(new View.OnClickListener() {
            private boolean state;
            @Override
            public void onClick(View v) {
                if (state){
                    state = false;
                    databaseReference.child("kontrol").setValue("11");
                }
                else{
                    state = true;
                    databaseReference.child("kontrol").setValue("22");
                }
                    return;
            }
        });
        ivLampu.setOnClickListener(new View.OnClickListener() {
            private boolean state;
            @Override
            public void onClick(View v) {
                if (state){
                    state = false;
                    databaseReference.child("kontrol").setValue("77");
                }
                else{
                    state = true;
                    databaseReference.child("kontrol").setValue("88");

                }
                return;
            }
        });
        ivPintu.setOnClickListener(new View.OnClickListener() {
            private boolean state;
            @Override
            public void onClick(View v) {
                if (state){
                    state = false;
                    databaseReference.child("kontrol").setValue("33");
                }
                else{
                    state = true;
                    databaseReference.child("kontrol").setValue("44");

                }
                return;
            }
        });

        ivCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE).
                        setTitleText("Anda yakin?").setContentText("Untuk melakukan checkout").setCancelText("Tidak").setConfirmText("Ya").
                        showCancelButton(true).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener(){
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("id", null);
                        editor.putBoolean("sudahLogin", false);
                        editor.apply();
                        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                        String childDate = DateDataFormat.format(Calendar.getInstance().getTime());
                        Map<String, Object> values = new HashMap<>();
                        values.put("id", id);
                        values.put("time",currentDateTimeString);
                        values.put("activity", "checkout");
                        databaseReference.child("active_user").child(id).removeValue();

                        databaseReference.child("counter_active_user").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int count = snapshot.getValue(Integer.class);
                                if (count == 1){
                                    databaseReference.child("kontrol").setValue("99");

                                }
                                databaseReference.child("counter_active_user").setValue(count-1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        databaseReference.child("history").child(childDate).push().setValue(values);
                        Intent i = new Intent(MainActivity.this, Menu.class);
                        startActivity(i);
                        finish();
                        sweetAlertDialog.cancel();
                    }
                }).show();
            }
        });
    }
}