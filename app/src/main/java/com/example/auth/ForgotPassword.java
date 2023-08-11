package com.example.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPassword extends AppCompatActivity {
    Button phone,email,verifyemail,verifyphone;
    EditText email2;
    String emaill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);

        EditText Email = findViewById(R.id.emai);
        verifyemail=findViewById(R.id.button2);
        verifyemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emaill=Email.getText().toString().trim();
                Intent intent = new Intent(ForgotPassword.this, PasswordChange.class);
                intent.putExtra("email", emaill);
                startActivity(intent);

            }
        });






    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}


