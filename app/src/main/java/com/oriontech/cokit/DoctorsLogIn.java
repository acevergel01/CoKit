package com.oriontech.cokit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class DoctorsLogIn extends AppCompatActivity {
    TextInputEditText txtUsername, txtPassword;
    Button btnLogin, txtReset;
    Config config = new Config();
    ArrayList<String> doctorDetails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_log_in);

        txtReset = findViewById(R.id.forgotPassword);
        txtUsername = findViewById(R.id.usernameLogin);
        txtPassword = findViewById(R.id.passwordLogin);
        btnLogin = findViewById(R.id.buttonLogin);
        txtReset.setOnClickListener(v -> openIntent(ResetPassword.class));
        btnLogin.setOnClickListener(v -> {
            String username, password;
            username = Objects.requireNonNull(txtUsername.getText()).toString();
            password = Objects.requireNonNull(txtPassword.getText()).toString();
            if (!username.equals("") && !password.equals("")) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    String[] field = new String[3];
                    field[0] = "username";
                    field[1] = "password";
                    field[2] = "table";
                    //Creating array for data
                    String[] data = new String[3];
                    data[0] = username;
                    data[1] = password;
                    data[2] = "doctor";
                    PutData putData = new PutData(config.getDatabase() + "LoginRegister/login.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();
                            if (result.equals("Login Success")) {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
                                editor.putString("username", username);
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();
                                getUserDetails(username);
                                Intent intent = new Intent(getApplicationContext(), DoctorsMonitoring.class);
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

    void getUserDetails(String user) {
        Config config = new Config();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = {"username","userType"};
            String[] data = {user,"doctor"};
            PutData putData = new PutData(config.getDatabase() + "LoginRegister/getfullname.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String details = putData.getResult();
                    String[] Array = details.split(";");
                    int i = Array.length;
                    while (i > 0) {
                        doctorDetails.add(Array[i - 1]);
                        i -= 1;
                    }
                    Collections.reverse(doctorDetails);
                    SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
                    editor.putString("fullname", doctorDetails.get(0));
                    editor.putString("email", doctorDetails.get(1));
                    editor.putString("firstname", doctorDetails.get(2));
                    editor.putString("middlename", doctorDetails.get(3));
                    editor.putString("surname", doctorDetails.get(4));
                    editor.putString("number", doctorDetails.get(5));
                    editor.putString("code", doctorDetails.get(6));
                    editor.apply();
                }
            }
        });
    }

    void openIntent(Class className) {
        Intent intent = new Intent(getApplicationContext(), className);
        startActivity(intent);
    }

    String getUserType(SharedPreferences prefs) {
        return prefs.getString("userType", "doctor");
    }

}