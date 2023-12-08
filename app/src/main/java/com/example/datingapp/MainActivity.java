package com.example.datingapp;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.internal.RegisterListenerMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public Button signUp_btn, login_btn;
    public TextView forgot_password;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    TextInputEditText editTextUsername, editTextPassword;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the default Firebase app
        FirebaseApp.initializeApp(this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        editTextUsername = findViewById(R.id.logIn_email);
        editTextPassword = findViewById(R.id.logIn_password);
        progressBar = findViewById(R.id.progressBar);
        login_btn = findViewById(R.id.login_btn);
        forgot_password = findViewById(R.id.forgot_password);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ResetPassword.class));
            }
        });

        if (firebaseUser != null) {
            Intent intent = new Intent(MainActivity.this, NavigationPage.class);
            startActivity(intent);
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String email, password;

                email = editTextUsername.getText().toString();
                password = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    editTextUsername.setError("Email is required");
                    editTextUsername.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    editTextPassword.setError("Password is required");
                    editTextPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(email, password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(MainActivity.this, NavigationPage.class);
                    startActivity(intent);
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        editTextUsername.setError("Invalid User");
                        editTextUsername.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextPassword.setError("Invalid credentials");
                        editTextPassword.requestFocus();
                    } catch (Exception e) {
                        Log.e("MainActivity", e.getMessage());
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void onClickLogIn(View v) {
        Intent intent = new Intent(this, NavigationPage.class);
        startActivity(intent);
    }

    public void onClickSignUp(View v) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }
}
