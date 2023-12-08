package com.example.datingapp;

import com.example.datingapp.profilef;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datingapp.modal.User;
import com.example.datingapp.modal.UserPersonalInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;

public class edit_profile extends Fragment {

    String name, userId, phone, birthdate, email, city;

    private static final int REQUEST_CODE_SELECT_IMAGE = 100;

    private CompoundButton.OnCheckedChangeListener personalInfoCheckedChangeListener;
    private boolean isPersonalInfoFiltered;

    LinearLayout PersonalInfo;
    DatabaseReference databaseReference;
    ImageView imageView;
    TextView userAge;

    public edit_profile() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && requestCode == REQUEST_CODE_SELECT_IMAGE && data != null) {
            Uri selectedImageUri = data.getData();

            // Do something with the selected image
            // For example, upload it to Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("users/" + userId + "/profile.jpg");
            imageRef.putFile(selectedImageUri);

            Picasso.get().load(selectedImageUri).into(imageView);

            //Update the user's profile with the image URL
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build();

                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ImageView galleryIcon = view.findViewById(R.id.gallery_icon);
        imageView = view.findViewById(R.id.profile_image);
        ImageView SaveEdits = view.findViewById(R.id.btn_doneEdit);
        SwitchCompat switchPersonalInfo = view.findViewById(R.id.switch_personalInfo);
        PersonalInfo = view.findViewById(R.id.personalInfo);
        EditText userName = view.findViewById(R.id.ProfileUserName);
        EditText userPhone = view.findViewById(R.id.user_phone);
        EditText userCity = view.findViewById(R.id.user_city);
        RadioGroup radioGroupGender = view.findViewById(R.id.radioGroup_gender);
        EditText user_birthday = view.findViewById(R.id.Userbirthday);
        RadioGroup radioGroup_maritalStatus = view.findViewById(R.id.radioGroup_maritalStatus);
        EditText race = view.findViewById(R.id.race);
        EditText qualification = view.findViewById(R.id.quailification);
        EditText profession = view.findViewById(R.id.profession);
        RadioGroup radioGroupFamilyPlan = view.findViewById(R.id.radioGroup_familyPlan);
        RadioGroup radioGroupSmoke = view.findViewById(R.id.radioGroup_Smoke);
        RadioGroup radioGroupDrink = view.findViewById(R.id.radioGroup_drink);
        EditText lifeSkill = view.findViewById(R.id.lifeSkill);
        EditText vitalThing = view.findViewById(R.id.vitalThing);
        EditText hobby = view.findViewById(R.id.hobby);
        EditText friendsSaying = view.findViewById(R.id.friendsSayings);
        RadioGroup radioGroupNumberChildren = view.findViewById(R.id.radioGroup_numberchildren);

        userId = FirebaseAuth.getInstance().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userId).child("personalInfo").child("city").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                city = snapshot.getValue(String.class);;
                userCity.setText(city);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    name = user.getName();
                    phone = user.getPhone();
                    birthdate = user.getBirthday();
                    email = user.getEmail();
                    userName.setText(name);
                    userPhone.setText(phone);
                    user_birthday.setText(birthdate);


                    profilef profile = new profilef();
                    int age = profile.calculateAge(birthdate);

                    // Set the age value to the ageTextView
                    TextView ageTextView = view.findViewById(R.id.UserAge);
                    ageTextView.setText(String.valueOf(age));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Read the user's personal information from the database
        DatabaseReference personalInfoRef = databaseReference.child(userId).child("personalInfo");
        personalInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserPersonalInfo personalInfo = snapshot.getValue(UserPersonalInfo.class);
                    if (personalInfo != null) {
                        String raceValue = personalInfo.getRace();
                        String qualificationValue = personalInfo.getEducation();
                        String professionValue = personalInfo.getProfession();
                        String lifeSkillValue = personalInfo.getLifeSkill();
                        String vitalThingValue = personalInfo.getImpThings();
                        String hobbyValue = personalInfo.getSpareTime();
                        String friendsSayingValue = personalInfo.getFriendsSaying();


                        // Set the personal information in the respective views
                        race.setText(raceValue);
                        qualification.setText(qualificationValue);
                        profession.setText(professionValue);
                        lifeSkill.setText(lifeSkillValue);
                        vitalThing.setText(vitalThingValue);
                        hobby.setText(hobbyValue);
                        friendsSaying.setText(friendsSayingValue);

//                        // Set the switch based on the family plan value
//                        switchPersonalInfo.setChecked(personalInfo.isFamilyPlan());
//                        isPersonalInfoFiltered = personalInfo.isFamilyPlan();

                        boolean familyPlanValue = personalInfo.isFamilyPlan();
                        boolean smokeValue = personalInfo.isSmoke();
                        boolean drinkValue = personalInfo.isDrink();

                        // Set the checked state of the radio buttons based on the retrieved values
                        radioGroupFamilyPlan.setOnCheckedChangeListener(null); // Disable the listener temporarily
                        radioGroupSmoke.setOnCheckedChangeListener(null); // Disable the listener temporarily
                        radioGroupDrink.setOnCheckedChangeListener(null); // Disable the listener temporarily

                        if (familyPlanValue) {
                            radioGroupFamilyPlan.check(R.id.radio_planYes);
                        } else {
                            radioGroupFamilyPlan.check(R.id.radio_planNo);
                        }

                        if (smokeValue) {
                            radioGroupSmoke.check(R.id.radio_smokeYes);
                        } else {
                            radioGroupSmoke.check(R.id.radio_smokeNo);
                        }

                        if (drinkValue) {
                            radioGroupDrink.check(R.id.radio_drinkYes);
                        } else {
                            radioGroupDrink.check(R.id.radio_drinkNo);
                        }

                        radioGroupFamilyPlan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                boolean selectedFamilyPlan;
                                if (checkedId == R.id.radio_planYes) {
                                    selectedFamilyPlan = true;
                                } else {
                                    selectedFamilyPlan = false;
                                }
                                personalInfo.setFamilyPlan(selectedFamilyPlan);
                                databaseReference.child(userId).child("personalInfo").setValue(personalInfo);
                            }
                        });

                        radioGroupSmoke.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                boolean selectedSmoke;
                                if (checkedId == R.id.radio_smokeYes) {
                                    selectedSmoke = true;
                                } else {
                                    selectedSmoke = false;
                                }
                                personalInfo.setSmoke(selectedSmoke);
                                databaseReference.child(userId).child("personalInfo").setValue(personalInfo);
                            }
                        });

                        radioGroupDrink.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                boolean selectedDrink;
                                if (checkedId == R.id.radio_drinkYes) {
                                    selectedDrink = true;
                                } else {
                                    selectedDrink = false;
                                }
                                personalInfo.setDrink(selectedDrink);
                                databaseReference.child(userId).child("personalInfo").setValue(personalInfo);
                            }
                        });


                        // Retrieve the marital status value from the personalInfo object
                        String maritalStatusValue = personalInfo.getStatus();
                        // Set the checked state of the radio buttons based on the marital status value
                        radioGroup_maritalStatus.setOnCheckedChangeListener(null); // Disable the listener temporarily
                        if (!TextUtils.isEmpty(maritalStatusValue)) {
                            if (maritalStatusValue.equals("Married")) {
                                radioGroup_maritalStatus.check(R.id.radio_married);
                            } else if (maritalStatusValue.equals("Single")) {
                                radioGroup_maritalStatus.check(R.id.radio_single);
                            } else if (maritalStatusValue.equals("Divorced")) {
                                radioGroup_maritalStatus.check(R.id.radio_divorced);
                            }
                        }
                        radioGroup_maritalStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                String selectedMaritalStatus;
                                if (checkedId == R.id.radio_married) {
                                    selectedMaritalStatus = "Married";
                                } else if (checkedId == R.id.radio_single) {
                                    selectedMaritalStatus = "Single";
                                } else if (checkedId == R.id.radio_divorced) {
                                    selectedMaritalStatus = "Divorced";
                                } else {
                                    selectedMaritalStatus = ""; // Handle other cases if necessary
                                }

                                // Update the marital status in the personalInfo object
                                personalInfo.setStatus(selectedMaritalStatus);

                                // Save the updated personalInfo object in the database
                                databaseReference.child(userId).child("personalInfo").setValue(personalInfo);
                            }
                        });

                        // Retrieve the number of children value from the personalInfo object
                        String numberOfChildrenValue = personalInfo.getChildren();
                        // Set the checked state of the radio buttons based on the number of children value
                        radioGroupNumberChildren.setOnCheckedChangeListener(null); // Disable the listener temporarily
                        if (!TextUtils.isEmpty(numberOfChildrenValue)) {
                            if (numberOfChildrenValue.equals("None")) {
                                radioGroupNumberChildren.check(R.id.radio_none);
                            } else if (numberOfChildrenValue.equals("1-2")) {
                                radioGroupNumberChildren.check(R.id.radio_onetwo);
                            } else if (numberOfChildrenValue.equals("3-4")) {
                                radioGroupNumberChildren.check(R.id.radio_threefour);
                            } else if (numberOfChildrenValue.equals("Many")) {
                                radioGroupNumberChildren.check(R.id.radio_many);
                            }
                        }
                        radioGroupNumberChildren.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                String selectedNumberOfChildren;
                                if (checkedId == R.id.radio_none) {
                                    selectedNumberOfChildren = "None";
                                } else if (checkedId == R.id.radio_onetwo) {
                                    selectedNumberOfChildren = "1-2";
                                } else if (checkedId == R.id.radio_threefour) {
                                    selectedNumberOfChildren = "3-4";
                                } else if (checkedId == R.id.radio_many) {
                                    selectedNumberOfChildren = "Many";
                                } else {
                                    selectedNumberOfChildren = ""; // Handle other cases if necessary
                                }

                                // Update the number of children in the personalInfo object
                                personalInfo.setChildren(selectedNumberOfChildren);

                                // Save the updated personalInfo object in the database
                                databaseReference.child(userId).child("personalInfo").setValue(personalInfo);
                            }
                        });

                        // ...
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });

        personalInfoCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PersonalInfo.setVisibility(View.VISIBLE);
                    isPersonalInfoFiltered = true;
                } else {
                    PersonalInfo.setVisibility(View.GONE);
                    isPersonalInfoFiltered = false;
                }
            }
        };

        switchPersonalInfo.setOnCheckedChangeListener(personalInfoCheckedChangeListener);

        SaveEdits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = userName.getText().toString();
                phone = userPhone.getText().toString();
                birthdate = user_birthday.getText().toString();
                city = userCity.getText().toString();

                databaseReference.child(userId).child("name").setValue(name);
                databaseReference.child(userId).child("phone").setValue(phone);
                databaseReference.child(userId).child("birthday").setValue(birthdate);
                databaseReference.child(userId).child("personalInfo").child("city").setValue(city);

                boolean selectedFamilyPlan = radioGroupFamilyPlan.getCheckedRadioButtonId() == R.id.radio_planYes;

                // Get the selected smoke value from the radio group
                boolean selectedSmoke = radioGroupSmoke.getCheckedRadioButtonId() == R.id.radio_smokeYes;

                // Get the selected drink value from the radio group
                boolean selectedDrink = radioGroupDrink.getCheckedRadioButtonId() == R.id.radio_drinkYes;

                // Update the personalInfo object with the new values
                databaseReference.child(userId).child("personalInfo").child("familyPlan").setValue(selectedFamilyPlan);
                databaseReference.child(userId).child("personalInfo").child("smoke").setValue(selectedSmoke);
                databaseReference.child(userId).child("personalInfo").child("drink").setValue(selectedDrink);

                // Get the selected marital status from the radio group
                String selectedMaritalStatus;
                if (radioGroup_maritalStatus.getCheckedRadioButtonId() == R.id.radio_married) {
                    selectedMaritalStatus = "Married";
                } else if (radioGroup_maritalStatus.getCheckedRadioButtonId() == R.id.radio_single) {
                    selectedMaritalStatus = "Single";
                } else if (radioGroup_maritalStatus.getCheckedRadioButtonId() == R.id.radio_divorced) {
                    selectedMaritalStatus = "Divorced";
                } else {
                    selectedMaritalStatus = "";
                }

                // Update the marital status in the database
                databaseReference.child(userId).child("personalInfo").child("status").setValue(selectedMaritalStatus);

                // Get the selected number of children from the radio group
                String selectedNumberOfChildren;
                if (radioGroupNumberChildren.getCheckedRadioButtonId() == R.id.radio_none) {
                    selectedNumberOfChildren = "None";
                } else if (radioGroupNumberChildren.getCheckedRadioButtonId() == R.id.radio_onetwo) {
                    selectedNumberOfChildren = "1-2";
                } else if (radioGroupNumberChildren.getCheckedRadioButtonId() == R.id.radio_threefour) {
                    selectedNumberOfChildren = "3-4";
                } else if (radioGroupNumberChildren.getCheckedRadioButtonId() == R.id.radio_many) {
                    selectedNumberOfChildren = "Many";
                } else {
                    selectedNumberOfChildren = "";
                }

                // Update the number of children in the database
                databaseReference.child(userId).child("personalInfo").child("numberOfChildren").setValue(selectedNumberOfChildren);

                // Get the values from the personal information fields
                String raceValue = race.getText().toString();
                String qualificationValue = qualification.getText().toString();
                String professionValue = profession.getText().toString();
                String lifeSkillValue = lifeSkill.getText().toString();
                String vitalThingValue = vitalThing.getText().toString();
                String hobbyValue = hobby.getText().toString();
                String friendsSayingsValue = friendsSaying.getText().toString();

                // Create a new UserPersonalInfo object with the field values
                UserPersonalInfo personalInfo = new UserPersonalInfo(city, selectedMaritalStatus, raceValue, selectedNumberOfChildren, qualificationValue, professionValue, selectedFamilyPlan, selectedSmoke, selectedDrink, lifeSkillValue, vitalThingValue, hobbyValue, friendsSayingsValue);

                // Save the personal information in the database
                databaseReference.child(userId).child("personalInfo").setValue(personalInfo);

                profilef profilePage = new profilef();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, profilePage);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        EditText editTextBirthdate = view.findViewById(R.id.Userbirthday);
        editTextBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date as default
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Check if user is at least 19 years old
                int ageLimit = 19;
                Calendar birthday = Calendar.getInstance();
                birthday.set(year - ageLimit, month, day);
                if (birthday.after(calendar)) {
                    // User is less than 19 years old, show error message
                    Toast.makeText(getContext(), "You must be at least 19 years old to use this app", Toast.LENGTH_SHORT).show();
                    // Disable the EditText to prevent selecting a date
                    editTextBirthdate.setEnabled(false);
                } else {
                    // Create a DatePickerDialog to show the calendar
                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    // Update the text of the EditText with the selected date
                                    String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);
                                    editTextBirthdate.setText(selectedDate);

                                    // Calculate the age using the method from the profilef class
                                    int age = profilef.calculateAge(selectedDate);

                                    // Set the age in the userAge TextView
                                    userAge.setText(String.valueOf(age));
                                }
                            }, year, month, day);


                    // Show the DatePickerDialog
                    datePickerDialog.show();
                }
            }
        });

        // Set OnClickListener for the gallery icon
        galleryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get the photo URL for the user's profile picture
        if (user.getPhotoUrl() != null) {
            Uri photoUrl = user.getPhotoUrl();

            // Load the photo into an ImageView using a library like Glide or Picasso
            ImageView imageView = view.findViewById(R.id.profile_image);
            Picasso.get().load(photoUrl).into(imageView);
        }

        // Retrieve the gender value from the database and set the checked state of the radio buttons
        databaseReference.child(userId).child("gender").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String genderValue = snapshot.getValue(String.class);
                if (genderValue != null) {
                    if (genderValue.equals("Male")) {
                        radioGroupGender.check(R.id.radio_male);
                    } else if (genderValue.equals("Female")) {
                        radioGroupGender.check(R.id.radio_female);
                    } else if (genderValue.equals("Non-Binary")) {
                        radioGroupGender.check(R.id.radio_nonBinary);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });

// Add the following code after retrieving the gender value from the database
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String selectedGender;
                if (checkedId == R.id.radio_male) {
                    selectedGender = "Male";
                } else if (checkedId == R.id.radio_female) {
                    selectedGender = "Female";
                } else if (checkedId == R.id.radio_nonBinary) {
                    selectedGender = "Non-Binary";
                } else {
                    selectedGender = ""; // Handle other cases if necessary
                }

                // Update the gender value in the database
                databaseReference.child(userId).child("gender").setValue(selectedGender);
            }
        });


        return view;
    }
}
