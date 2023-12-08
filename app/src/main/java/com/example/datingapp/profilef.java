package com.example.datingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.datingapp.modal.User;
import com.example.datingapp.modal.UserPersonalInfo;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class profilef extends Fragment {
    DatabaseReference databaseReference;
    TextView userName, userAge, userCity, userEmail, userPhone;
    ShapeableImageView profilePic;
    ImageView logout_btn;

    public profilef() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profilef, container, false);

        ImageView editIcon = view.findViewById(R.id.edit_icon);
        logout_btn = view.findViewById(R.id.logout_btn);
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_profile editFragment = new edit_profile();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, editFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user != null){
                    userName = view.findViewById(R.id.ProfileUserName);
                    userAge = view.findViewById(R.id.UserAge);
                    userCity = view.findViewById(R.id.UserCity);
                    userEmail = view.findViewById(R.id.UserEmail);
                    userPhone = view.findViewById(R.id.UserPhone);

                    profilePic = view.findViewById(R.id.profilePic);

                    String name = user.getName();
                    String Email = user.getEmail();
                    String Phone = user.getPhone();

                    userName.setText(name);
                    userEmail.setText(Email);
                    userPhone.setText(Phone);
                    String birthday = user.getBirthday();


                    int age = calculateAge(birthday);
                    userAge.setText(String.valueOf(age));

//                    if(user.getImageURL().equals("default")){
//                        profilePic.setImageResource(R.drawable.pink4);
//                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        databaseReference.child(userId).child("personalInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserPersonalInfo userPersonalInfo = snapshot.getValue(UserPersonalInfo.class);
                if(userPersonalInfo != null){
                    userCity = view.findViewById(R.id.UserCity);

                    String city = userPersonalInfo.getCity();

                    Log.i("info", city);
                    userCity.setText(city);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user.getPhotoUrl() != null){

            Uri photoUrl = user.getPhotoUrl();
            ImageView imageView = view.findViewById(R.id.profilePic);
            Picasso.get().load(photoUrl).into(imageView);
        }
        return view;
    }

    public static int calculateAge(String birthday) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Update the date format
        try {
            Date birthDate = sdf.parse(birthday);
            Calendar dob = Calendar.getInstance();
            dob.setTime(birthDate);
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH) ||
                    (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH))) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}