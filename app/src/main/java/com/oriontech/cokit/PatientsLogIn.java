package com.oriontech.cokit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class PatientsLogIn extends AppCompatActivity {
    TextInputEditText txtUsername, txtPassword;
    Button btnLogin, txtSignup, txtReset;
    Config config = new Config();
    ArrayList<String> patientDetails = new ArrayList<>();
    String username, password;

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_log_in);

        txtReset = findViewById(R.id.forgotPassword);
        txtUsername = findViewById(R.id.usernameLogin);
        txtPassword = findViewById(R.id.passwordLogin);
        txtSignup = findViewById(R.id.signUpText);
        btnLogin = findViewById(R.id.buttonLogin);

        txtSignup.setOnClickListener(v -> openIntent(SignUp.class));
        txtReset.setOnClickListener(v -> openIntent(ResetPassword.class));

        btnLogin.setOnClickListener(v -> {
            username = Objects.requireNonNull(txtUsername.getText()).toString();
            password = Objects.requireNonNull(txtPassword.getText()).toString();
            if (!username.equals("") && !password.equals("")) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    String[] field = new String[3];
                    field[0] = "username";
                    field[1] = "password";
                    field[2] = "table";
                    String[] data = new String[3];
                    data[0] = username;
                    data[1] = password;
                    data[2] = "patient";
                    PutData putData = new PutData(config.getDatabase() + "LoginRegister/login.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();
                            if (result.equals("Login Success")) {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
                                editor.putString("username", username);
                                editor.putString("fullname", "");
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();
                                getfullName();
                                Intent intent = new Intent(getApplicationContext(), PatientMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    //End Write and Read data with URL
                });
            } else {
                Toast.makeText(getApplicationContext(), "All field are required", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void getfullName() {
        Config config = new Config();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = new String[2];
            field[0] = "username";
            field[1] = "userType";
            String[] data = new String[2];
            data[0] = username;
            data[1] = "patient";
            PutData putData = new PutData(config.getDatabase() + "LoginRegister/getfullname.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    String[] Array = result.split(";");
                    int i = Array.length;
                    while (i > 0) {
                        patientDetails.add(Array[i - 1]);
                        i -= 1;
                    }
                    Collections.reverse(patientDetails);
                    Log.e("Error", String.valueOf(patientDetails));
                    SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
                    editor.putString("fullname", patientDetails.get(0));
                    editor.putString("email", patientDetails.get(1));
                    editor.putString("firstname", patientDetails.get(2));
                    editor.putString("middlename", patientDetails.get(3));
                    editor.putString("surname", patientDetails.get(4));
                    editor.putString("number", patientDetails.get(5));
                    if(patientDetails.size()==7){
                        editor.putString("code", patientDetails.get(6));
                    }
                    editor.apply();
                }
            }
        });
    }



    void openIntent(Class className) {
        Intent intent = new Intent(getApplicationContext(), className);
        startActivity(intent);
    }

}