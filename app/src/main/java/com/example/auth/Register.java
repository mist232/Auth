package com.example.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Register extends AppCompatActivity implements View.OnClickListener{
    EditText name,email,password,confirm_password,phone;
    private boolean passwordShowing = false;
    private boolean passwordShowing2 = false;


    Button mRegisterbtn;
    TextView mLoginPageBack;
    FirebaseAuth mAuth;
    DatabaseReference mdatabase;
    String Name,Email,Password,Confirm_password,Phone,r;
    ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText)findViewById(R.id.fullname);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password_regis);
        mRegisterbtn = (Button)findViewById(R.id.sign_up_button);
        mLoginPageBack = (TextView)findViewById(R.id.sign_in);
        confirm_password=(EditText)findViewById(R.id.password_confirm);
        ImageView passwordIcon = findViewById(R.id.show);
        passwordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordShowing) {
                    passwordShowing = false;
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.hide_icon_148530);
                } else {
                    passwordShowing = true;
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.show_eye_icon_183818);
                }
                password.setSelection(password.length());
            }
        });
        ImageView passwordIcon2 = findViewById(R.id.show2);
        passwordIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordShowing2) {
                    passwordShowing2 = false;
                    confirm_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIcon2.setImageResource(R.drawable.hide_icon_148530);
                } else {
                    passwordShowing2 = true;
                    confirm_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordIcon2.setImageResource(R.drawable.show_eye_icon_183818);
                }
                confirm_password.setSelection(password.length());
            }
        });
        // set click event using setOnCountryChangeListener for countryName, countryCode, countryCodeName, countryFlagImage and more

        // for authentication using FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();
        mRegisterbtn.setOnClickListener(this);
        mLoginPageBack.setOnClickListener(this);
        mDialog = new ProgressDialog(this);
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    }


    @Override
    public void onClick(View v) {
        if (v==mRegisterbtn){
            UserRegister();
        }else if (v== mLoginPageBack){
            startActivity(new Intent(Register.this, MainActivity.class));
        }
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }
    private void UserRegister() {
        Name = name.getText().toString().trim();
        Email = email.getText().toString().trim();
        Password = password.getText().toString().trim();
        Confirm_password = confirm_password.getText().toString().trim();

        if (TextUtils.isEmpty(Name)) {
            Toast.makeText(Register.this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Email) ) {
            Toast.makeText(Register.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
            return;

        }
        else if (!isValidEmail(Email))
        {
            Toast.makeText(Register.this, "Enter Email", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(Password)) {
            Toast.makeText(Register.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        } else if (Password.length() < 8) {
            Toast.makeText(Register.this, "Password must be greater then 8 digits!", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isValidPassword(Password)) {
            Toast.makeText(Register.this, " Password must contain minimum 8 characters at least 1 Alphabet, 1 Number and 1 Special Character!", Toast.LENGTH_SHORT).show();
        } else if (!Password.equals(Confirm_password)) {
            Toast.makeText(Register.this, " Passwords don't match!", Toast.LENGTH_SHORT).show();

        } else {
            mDialog.setMessage("Creating User please wait...");
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
            mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    sendEmailVerification();
                    mDialog.dismiss();
                    OnAuth(Objects.requireNonNull(task.getResult().getUser()));
                    mAuth.signOut();
                } else {
                    Toast.makeText(Register.this, "error on creating user", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }




    //Email verification code using FirebaseUser object and using isSucccessful()function.
    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null){
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    User newUser = new User(getDisplayName(), getUserEmail(), new Date().getTime());
                    databaseReference.child("Users").child(user.getUid()).setValue(newUser)
                            .addOnCompleteListener(tasks -> {
                                if (tasks.isSuccessful()) {

                                    // Data added successfully
                                    // You can update UI or show a success message
                                } else {
                                    // Data addition failed
                                    // Handle the error or show an error message
                                }
                            });
                    Toast.makeText(Register.this,"Check your Email for verification",Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                }
            });
        }
    }

    private void OnAuth(FirebaseUser user) {
        createAnewUser(user.getUid());
    }

    private void createAnewUser(String uid) {
        User user = BuildNewuser();
        mdatabase.child(uid).setValue(user);
    }


    private User BuildNewuser(){
        return new User(
                getDisplayName(),
                getUserEmail(),
                new Date().getTime()

        );
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    public String getDisplayName() {
        return Name;
    }

    public String getUserEmail() {
        return Email;
    }

}