package com.oriontech.cokit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.Objects;

public class SignUp extends AppCompatActivity {
    TextInputEditText txtSurname, txtFirstName, txtMI, txtEmail, txtUsername, txtPassword, intNumber, txtDoctor;
    Button btnSignup, btnLogin;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        prefs = getSharedPreferences("name", MODE_PRIVATE);
        txtDoctor = findViewById(R.id.refCode);
        txtSurname = findViewById(R.id.surname);
        txtFirstName = findViewById(R.id.firstname);
        txtMI = findViewById(R.id.middleinitial);
        txtEmail = findViewById(R.id.email);
        txtUsername = findViewById(R.id.usernameSignup);
        txtPassword = findViewById(R.id.passwordSignup);
        btnSignup = findViewById(R.id.btnSignup);
        btnLogin = findViewById(R.id.btnLogin);
        intNumber = findViewById(R.id.phoneno);
        btnLogin.setOnClickListener(v -> onBackPressed());

        btnSignup.setOnClickListener(v -> {
            Config config = new Config();
            if (validEmail(getString(txtEmail))) {
                if (isEmpty(txtSurname) && isEmpty(txtFirstName) && isEmpty(txtUsername) &&
                        isEmpty(txtEmail) && isEmpty(intNumber) && isEmpty(txtPassword)) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        String[] field = new String[9];
                        field[0] = "fullname";
                        field[1] = "email";
                        field[2] = "username";
                        field[3] = "password";
                        field[4] = "firstname";
                        field[5] = "middlename";
                        field[6] = "surname";
                        field[7] = "number";
                        field[8] = "doctor";
                        //Creating array for data
                        String[] data = new String[9];
                        data[0] = getString(txtFirstName) + " " + getString(txtMI) + " " + getString(txtSurname);
                        data[1] = getString(txtEmail);
                        data[2] = getString(txtUsername);
                        data[3] = getString(txtPassword);
                        data[4] = getString(txtFirstName);
                        data[5] = getString(txtMI);
                        data[6] = getString(txtSurname);
                        data[7] = getString(intNumber);
                        data[8] = getString(txtDoctor);
                        PutData putData = new PutData(config.getDatabase() + "LoginRegister/signup.php", "POST", field, data);
                        if (putData.startPut()) {
                            if (putData.onComplete()) {
                                String result = putData.getResult();
                                if (result.equals("Sign Up Success")) {
                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), PatientsLogIn.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Enter a valid email address", Toast.LENGTH_SHORT).show();
            }
        });
    }

    String getString(EditText text) {
        return Objects.requireNonNull(text.getText()).toString();
    }

    boolean isEmpty(EditText text) {
        if (TextUtils.isEmpty(getString(text))) {
            text.setError(text.getHint().toString() + " can not be empty");
            return false;
        }
        return true;
    }

    private boolean validEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}