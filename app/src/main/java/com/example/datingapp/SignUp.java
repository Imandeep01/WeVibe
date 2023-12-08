package com.example.datingapp;

import android.app.DatePickerDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.datingapp.modal.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignUp extends AppCompatActivity {
    Button btn_signUp;
    DatabaseReference databaseReference;
    TextInputEditText user_name, user_email, user_password, user_phone, user_dob, user_confirmPswrd;
    ProgressBar progressBar;
    RadioGroup radioGroup;
    RadioButton radioButtonSelectedGender;
    int age;

    public static String currentId = "";

    private static final String TAG = "SignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btn_signUp = (Button) findViewById(R.id.signUp1_btn);

        progressBar = findViewById(R.id.progressBar);

        user_name = (TextInputEditText) findViewById(R.id.signUp_name);
        user_email = (TextInputEditText) findViewById(R.id.signUp_email);
        user_phone = (TextInputEditText) findViewById(R.id.signUp_phone);
        user_dob = (TextInputEditText) findViewById(R.id.signUp_dob);
        user_password = (TextInputEditText) findViewById(R.id.signUp_password);
        user_confirmPswrd = (TextInputEditText) findViewById(R.id.signUpConfirm_password);

        radioGroup = findViewById(R.id.radioGroup_gender);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        EditText birthdayEditText = findViewById(R.id.signUp_dob);
        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Calendar minDate = Calendar.getInstance();
                minDate.add(Calendar.YEAR, -19);

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day)
                            {
                                // Set the text of the EditText to the selected date
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(Calendar.YEAR, year);
                                selectedDate.set(Calendar.MONTH, month);
                                selectedDate.set(Calendar.DAY_OF_MONTH, day);

                                Calendar minDate = Calendar.getInstance();
                                minDate.add(Calendar.YEAR, -19);

                                Calendar currentDate = Calendar.getInstance();

                                if (selectedDate.after(currentDate))
                                {
                                    Toast.makeText(SignUp.this, "Selected date cannot be in the future.", Toast.LENGTH_LONG).show();
                                    birthdayEditText.setText("");
                                } else if (selectedDate.compareTo(minDate)  >0)
                                {
                                    Toast.makeText(SignUp.this, "You must be at least 19 years old to use this app", Toast.LENGTH_LONG).show();
                                    birthdayEditText.setText("");
                                } else
                                {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                    birthdayEditText.setText(dateFormat.format(selectedDate.getTime()));
                                }
                                int age = currentDate.get(Calendar.YEAR) - year;

                                if (currentDate.get(Calendar.MONTH) < month ||(currentDate.get(Calendar.MONTH) == month && currentDate.get(Calendar.DAY_OF_MONTH) < day)) {
                                age--;
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                    birthdayEditText.setText(dateFormat.format(selectedDate.getTime()));
                                }
                            }


                        },
                minDate.get(Calendar.YEAR),
                        minDate.get(Calendar.MONTH),
                        minDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        btn_signUp.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderInt = radioGroup.getCheckedRadioButtonId();
                radioButtonSelectedGender = findViewById(selectedGenderInt);

                String textFullName = user_name.getText().toString();
                String textEmail = user_email.getText().toString();
                String textPassword = user_password.getText().toString();
                String textConfirmPswrd = user_confirmPswrd.getText().toString();
                String textPhone = user_phone.getText().toString();
                String textDOB = user_dob.getText().toString();
                String textGender;

                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(SignUp.this, "Please enter your fullname", Toast.LENGTH_LONG).show();
                    user_name.setError("FullName is required");
                    user_name.requestFocus();

                } else if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(SignUp.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    user_email.setError("Email is required");
                    user_email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(SignUp.this, "Please re enter your email", Toast.LENGTH_LONG).show();
                    user_email.setError("Valid email is required");
                    user_email.requestFocus();
                } else if (TextUtils.isEmpty(textPhone)) {
                    Toast.makeText(SignUp.this, "Please enter your phone number", Toast.LENGTH_LONG).show();
                    user_phone.setError("Phone number is required");
                    user_phone.requestFocus();
                } else if (textPhone.length() != 10) {
                    Toast.makeText(SignUp.this, "Please enter a valid phone number", Toast.LENGTH_LONG).show();
                    user_phone.setError("Phone number should be 10 digits");
                    user_dob.requestFocus();
                } else if (TextUtils.isEmpty(textDOB)) {
                    Toast.makeText(SignUp.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                    user_dob.setError("Date of Birth is required");
                    user_dob.requestFocus();
                } else if (radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(SignUp.this, "Please select your gender", Toast.LENGTH_LONG).show();
                    radioButtonSelectedGender.setError("Gender is required");
                    radioButtonSelectedGender.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(SignUp.this, "Please enter your password", Toast.LENGTH_LONG).show();
                } else if (textPassword.length() < 6) {
                    Toast.makeText(SignUp.this, "Password should be atleast 6 digits", Toast.LENGTH_LONG).show();
                    user_password.setError("Password too weak");
                    user_password.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPswrd)) {
                    Toast.makeText(SignUp.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                    user_confirmPswrd.setError("Password confirmation is required");
                    user_confirmPswrd.requestFocus();
                } else if (!textPassword.equals(textConfirmPswrd)) {
                    Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    user_confirmPswrd.setError("Password confirmation is required");
                    user_confirmPswrd.requestFocus();

                    //clear the passwords
                    user_password.clearComposingText();
                    user_confirmPswrd.clearComposingText();
                } else {
                    textGender = radioButtonSelectedGender.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textPassword, textPhone, textDOB, textGender);
                }
            }
        }));
    }
    private void registerUser(String textFullName, String textEmail, String textPassword, String textPhone, String textDOB, String textGender) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        firebaseAuth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseAuth.signInWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            String userId = currentUser.getUid();
                            Uri imageURL = currentUser.getPhotoUrl();

                            User newUser = new User(userId, textEmail, textFullName, textPhone, textDOB, textGender);

                            // Save the user data to the database using the userId as the key
                            databaseReference.child(userId).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUp.this, "New User Added", Toast.LENGTH_LONG).show();

                                        // Proceed to the next activity
                                        Intent intent = new Intent(SignUp.this, Questions_page.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignUp.this, "Registration Failed", Toast.LENGTH_LONG).show();
                                    }

                                    // Hide the progress bar
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        user_password.setError("Your password is too weak");
                        user_password.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        user_email.setError("Invalid Email");
                        user_password.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        user_email.setError("This Email is already registered");
                        user_password.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    // Hide the progress bar
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}