package com.example.datingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datingapp.modal.UserSearchPreferences;


public class Preferences extends AppCompatActivity
{
    private int minAge = 19, maxAge = 20;
    private int searchDistance = 1;
    private Button btn_minAgeInc, btn_minAgeDec, btn_maxAgeInc, btn_maxAgeDec, btn_Login;
    private EditText seekEditText;
    public TextView TextView_minAge, TextView_maxAge;
    private SeekBar seekbar;
    private SwitchCompat Switch_AnyGender, Switch_AnyAge, Switch_AnyDistance, Switch_AnyStatus;
    private RadioGroup radioGroup_gender, radioGroup_status;
    private RadioButton radioBtn_selectedGender, radioBtn_selectedStatus;
    private String PreferredGender, PreferredStatus;

    private boolean isGenderFiltered, isAgeRestricted, isDistanceRestricted, isStatusRestricted;

    LinearLayout GenderRadioLayout, AgeLimitsLayout, SearchDistanceLayout, StatusLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        btn_minAgeInc = findViewById(R.id.Button_minAgeInc);
        btn_minAgeDec = findViewById(R.id.Button_minAgeDec);
        btn_maxAgeInc = findViewById(R.id.Button_maxAgeInc);
        btn_maxAgeDec = findViewById(R.id.Button_maxAgeDec);
        btn_Login = findViewById(R.id.login_button);

        TextView_minAge = findViewById(R.id.EditText_minAge);
        TextView_maxAge = findViewById(R.id.EditText_maxAge);

        Switch_AnyGender = findViewById(R.id.switch_anyGender);
        Switch_AnyAge = findViewById(R.id.switch_anyAge);
        Switch_AnyDistance = findViewById(R.id.switch_anyDistance);
        Switch_AnyStatus = findViewById(R.id.switch_anyStatus);

        radioGroup_gender = findViewById(R.id.radioGroup_lookingFor);
        radioGroup_status = findViewById(R.id.radioGroup_status);

        seekbar = findViewById(R.id.seekbar_distance);
        seekEditText = findViewById(R.id.EditText_distance);

        GenderRadioLayout = findViewById(R.id.layout_genderRadioGrp);
        AgeLimitsLayout = findViewById(R.id.layout_ageLimits);
        SearchDistanceLayout = findViewById(R.id.layout_searchDistance);
        StatusLayout = findViewById(R.id.layout_statusFilter);

        updateMinAge();
        updateMaxAge();

        Switch_AnyGender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //Toast.makeText(Preferences.this, "Switched On", Toast.LENGTH_LONG).show();
                    GenderRadioLayout.setVisibility(View.GONE);
                }else{
                   // Toast.makeText(Preferences.this, "Switched Off", Toast.LENGTH_LONG).show();
                    GenderRadioLayout.setVisibility(View.VISIBLE);
                }
                isGenderFiltered = !isChecked;
            }
        });

        Switch_AnyAge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //Toast.makeText(Preferences.this, "Switched On", Toast.LENGTH_LONG).show();
                    AgeLimitsLayout.setVisibility(View.GONE);
                }else{
                  //  Toast.makeText(Preferences.this, "Switched Off", Toast.LENGTH_LONG).show();
                    AgeLimitsLayout.setVisibility(View.VISIBLE);
                }
                isAgeRestricted = !isChecked;
            }
        });
        Switch_AnyDistance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                   // Toast.makeText(Preferences.this, "Switched On", Toast.LENGTH_LONG).show();
                    SearchDistanceLayout.setVisibility(View.GONE);
                }else{
                   // Toast.makeText(Preferences.this, "Switched Off", Toast.LENGTH_LONG).show();
                    SearchDistanceLayout.setVisibility(View.VISIBLE);
                }
                isDistanceRestricted = !isChecked;
            }
        });
        Switch_AnyStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                   // Toast.makeText(Preferences.this, "Switched On", Toast.LENGTH_LONG).show();
                    StatusLayout.setVisibility(View.GONE);
                }else{
                    //Toast.makeText(Preferences.this, "Switched Off", Toast.LENGTH_LONG).show();
                    StatusLayout.setVisibility(View.VISIBLE);
                }
                isStatusRestricted = !isChecked;
            }
        });

        seekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progressValue;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressValue = progress;
                        seekEditText.setText(String.valueOf(progress));
                        searchDistance = progressValue;
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        seekEditText.setText(String.valueOf( progressValue));
                        searchDistance = progressValue;
                    }
                }
        );

        seekEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    int value = Integer.parseInt(s.toString());
                    if (value > seekbar.getMax()) {
                        value = seekbar.getMax();
                    }
                    seekbar.setProgress(value);
                    searchDistance = value;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btn_minAgeInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(minAge < (maxAge-1)){
                    minAge++;
                    updateMinAge();
                }
            }
        });

        btn_minAgeDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minAge > 19) {
                    minAge--;
                    updateMinAge();
                }
            }
        });
        btn_maxAgeInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maxAge++;
                updateMaxAge();
            }
        });

        btn_maxAgeDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (maxAge > (minAge + 1)) {
                    maxAge--;
                    updateMaxAge();
                }
            }
        });
        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Preferences.this, "Login Pressed", Toast.LENGTH_LONG).show();

                radioBtn_selectedGender = findViewById(radioGroup_gender.getCheckedRadioButtonId());
                radioBtn_selectedStatus = findViewById(radioGroup_status.getCheckedRadioButtonId());

                PreferredGender = radioBtn_selectedGender.getText().toString();
                PreferredStatus = radioBtn_selectedStatus.getText().toString();

                UserSearchPreferences searchPreferences = new UserSearchPreferences(isGenderFiltered, isAgeRestricted, isDistanceRestricted, isStatusRestricted,
                        PreferredGender, PreferredStatus, minAge, maxAge, searchDistance);

                Intent intent = new Intent(Preferences.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
        private void updateMinAge() {
            TextView_minAge.setText(String.valueOf(minAge));
        }

        private void updateMaxAge() {
            TextView_maxAge.setText(String.valueOf(maxAge));
        }

}
