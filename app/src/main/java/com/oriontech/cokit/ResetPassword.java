package com.oriontech.cokit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.vishnusivadas.advanced_httpurlconnection.PutData;
public class ResetPassword extends AppCompatActivity {
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("name", MODE_PRIVATE);
        setContentView(R.layout.activity_reset_password);
        Config config = new Config();
        EditText emailTxt = findViewById(R.id.txtSubmitEmail);
        Button submit = findViewById(R.id.btnSubmit);
        submit.setOnClickListener(v -> {
            String email = emailTxt.getText().toString();
            if (!email.equals("")) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        String[] field = new String[1];
                        field[0] = "email";
                        String[] data = new String[1];
                        data[0] = email;
                        PutData putData = new PutData(config.getDatabase() + "LoginRegister/submit_email.php", "POST", field, data);
                        if (putData.startPut()) {
                            if (putData.onComplete()) {
                                String result = putData.getResult();
                                if (result.equals("Check Your Email and Click on the link sent to your email")) {
                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), Welcome.class);
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
                    emailTxt.setError("Enter a valid email");
                }
            } else {
                emailTxt.setError("Email can not be empty");
            }
        });
    }
}