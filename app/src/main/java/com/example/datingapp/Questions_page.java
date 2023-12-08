package com.example.datingapp;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.datingapp.modal.UserPersonalInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Questions_page extends AppCompatActivity {
    ProgressBar progressBar;
    FirebaseUser user;
    String UserID;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    EditText location, race, education, profession, lifeSkill, impThings, spareTime, friendsSaying;
    RadioGroup radioGr_status, radioGr_children, radioGr_familyPlan, radioGr_smoke, radioGr_drink;
    RadioButton radioBtn_status, radioBtn_children, radioBtn_familyPlan, radioBtn_smoke, radioBtn_drink;

    Button nextButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_page);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        user = firebaseAuth.getInstance().getCurrentUser();
        UserID = user.getUid();

        location = (EditText)findViewById(R.id.ans_location);
        race = (EditText)findViewById(R.id.ans_race);
        education = (EditText)findViewById(R.id.ans_education);
        profession = (EditText)findViewById(R.id.ans_profession);
        lifeSkill = (EditText)findViewById(R.id.ans_lifeSkill);
        impThings = (EditText)findViewById(R.id.ans_impThings);
        spareTime = (EditText)findViewById(R.id.ans_spareTime);
        friendsSaying = (EditText)findViewById(R.id.ans_friendsSaying);

        radioGr_status = (RadioGroup)findViewById(R.id.radioGroup_status);
        radioGr_children = (RadioGroup)findViewById(R.id.radioGroup_children);
        radioGr_familyPlan = (RadioGroup)findViewById(R.id.radioGroup_familyPlan);
        radioGr_smoke = (RadioGroup)findViewById(R.id.radioGroup_smoke);
        radioGr_drink = (RadioGroup)findViewById(R.id.radioGroup_drink);
    }
    public void onClickNext(View v) {
        radioBtn_status = findViewById(radioGr_status.getCheckedRadioButtonId());
        radioBtn_children = findViewById(radioGr_children.getCheckedRadioButtonId());
        radioBtn_familyPlan = findViewById(radioGr_familyPlan.getCheckedRadioButtonId());
        radioBtn_smoke = findViewById(radioGr_smoke.getCheckedRadioButtonId());
        radioBtn_drink = findViewById(radioGr_drink.getCheckedRadioButtonId());

        String textLocation = location.getText().toString();
        String textRace = race.getText().toString();
        String textEducation = education.getText().toString();
        String textProfession = profession.getText().toString();
        String textLifeSkill = lifeSkill.getText().toString();
        String textImpThings = impThings.getText().toString();
        String textSpareTime = spareTime.getText().toString();
        String textFriendsSaying = friendsSaying.getText().toString();

        String textStatus = radioBtn_status.getText().toString();
        String textChildren = radioBtn_children.getText().toString();

        boolean boolFamilyPlan = radioBtn_familyPlan.getText().toString().equals("Yes");
        boolean boolSmoke = radioBtn_smoke.getText().toString().equals("Yes");
        boolean boolDrink = radioBtn_drink.getText().toString().equals("Yes");

        //validation goes here
        if(TextUtils.isEmpty(textLocation))
        {
            Toast.makeText(Questions_page.this, "Please enter your location", Toast.LENGTH_LONG).show();
            location.setError("FullName is required");
            location.requestFocus();
        }
        else if(TextUtils.isEmpty(textRace))
        {
            Toast.makeText(Questions_page.this, "Please enter your race/ethnicity", Toast.LENGTH_LONG).show();
            race.setError("Race/Ethnicity is required");
            race.requestFocus();
        }
        else if(TextUtils.isEmpty(textEducation)) {
            Toast.makeText(Questions_page.this, "Please enter your highest qualification", Toast.LENGTH_LONG).show();
            education.setError("Education is required");
            education.requestFocus();
        }
        else if(TextUtils.isEmpty(textProfession))
        {
            Toast.makeText(Questions_page.this, "Please enter your profession", Toast.LENGTH_LONG).show();
            profession.setError("Profession is required");
            profession.requestFocus();
        }
        else if(TextUtils.isEmpty(textLifeSkill))
        {
            Toast.makeText(Questions_page.this, "Please enter your best life skill", Toast.LENGTH_LONG).show();
            lifeSkill.setError("Life Skill is required");
            lifeSkill.requestFocus();
        }
        else if(TextUtils.isEmpty(textImpThings))
        {
            Toast.makeText(Questions_page.this, "Please enter things you cannot live without", Toast.LENGTH_LONG).show();
            location.setError("Things you cannot live without are required");
            location.requestFocus();
        }
        else if(TextUtils.isEmpty(textSpareTime))
        {
            Toast.makeText(Questions_page.this, "Please enter your leisure time activities", Toast.LENGTH_LONG).show();
            spareTime.setError("Spare time activities is required");
            spareTime.requestFocus();
        }
        else if(TextUtils.isEmpty(textFriendsSaying))
        {
            Toast.makeText(Questions_page.this, "Please enter what your friends say about you", Toast.LENGTH_LONG).show();
            friendsSaying.setError("Friends sayings are required");
            friendsSaying.requestFocus();
        }
        else
        {
            UserPersonalInfo userPersonalInfo = new UserPersonalInfo(textLocation, textStatus, textRace, textChildren, textEducation, textProfession,
                    boolFamilyPlan, boolSmoke, boolDrink, textLifeSkill, textImpThings, textSpareTime, textFriendsSaying);

            Intent intent = new Intent(this, Preferences.class);
            startActivity(intent);
        }
    }
}