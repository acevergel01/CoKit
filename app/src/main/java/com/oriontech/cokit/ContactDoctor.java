package com.oriontech.cokit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.Objects;

public class ContactDoctor extends AppCompatActivity {
    View linearlayout;
    String doctorString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                doctorString = null;
            } else {
                doctorString = extras.getString("code");
            }
        }
        setContentView(R.layout.activity_contact_doctor);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        linearlayout = findViewById(R.id.data);
        linearlayout.bringToFront();
        Log.e("Error",doctorString);
        if (!doctorString.equals("general")){
            getPatientList(doctorString);
        }
        else{
            linearlayout.setVisibility(View.GONE);
        }
    }

    void getPatientList(String doctor) {
        Config config = new Config();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = new String[2];
            field[0] = "doctor";
            field[1] = "table";
            String[] data = new String[2];
            data[0] = doctor;
            data[1] = "doctor";
            PutData putData = new PutData(config.getDatabase() + "LoginRegister/getPatient.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    Log.e("Error",putData.getResult());
                    String result = putData.getResult();
                    String[] Array = result.split(";");
                    ((TextView) findViewById(R.id.name)).setText(Array[0]);
                    ((TextView) findViewById(R.id.number)).setText(Array[2]);
                    ((TextView) findViewById(R.id.email)).setText(Array[3]);

                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.anim_slide_from_left,R.anim.anim_slide_to_right);
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_from_left,R.anim.anim_slide_to_right);
    }

}