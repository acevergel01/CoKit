package com.oriontech.covidcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btndoctor = (Button) findViewById(R.id.btnDoctor);
        Button btnpatient = (Button) findViewById(R.id.btnPatient);
        btndoctor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                   openDoctorLogin();
            }
        });
        btnpatient.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openPatientLogin();
            }
        });
    }
    public void openDoctorLogin(){
        Intent intent = new Intent(this,DoctorLogin.class);
        startActivity(intent);
    }
    public void openPatientLogin(){
        Intent intent = new Intent(this,PatientLogin.class);
        startActivity(intent);
    }

}