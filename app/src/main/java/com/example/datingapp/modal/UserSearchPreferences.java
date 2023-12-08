package com.example.datingapp.modal;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserSearchPreferences {
    private boolean isGenderRestricted, isAgeRestricted, isDistanceRestricted, isStatusRestricted;
    private String filterGender, filterStatus;
    private int minAge, maxAge, searchDistance;

    public UserSearchPreferences(boolean isGenderRestricted, boolean isAgeRestricted, boolean isDistanceRestricted, boolean isStatusRestricted,
                                 String filterGender, String filterStatus, int minAge, int maxAge, int searchDistance) {
        this.isGenderRestricted = isGenderRestricted;
        this.isAgeRestricted = isAgeRestricted;
        this.isDistanceRestricted = isDistanceRestricted;
        this.isStatusRestricted = isStatusRestricted;
        this.filterGender = filterGender;
        this.filterStatus = filterStatus;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.searchDistance = searchDistance;

        UpdateData();
    }

    public boolean isGenderRestricted() {
        return isGenderRestricted;
    }

    public void setGenderRestricted(boolean genderRestricted) {
        this.isGenderRestricted = genderRestricted;
    }
    public boolean isAgeRestricted() {
        return isAgeRestricted;
    }
    public void setAgeRestricted(boolean ageRestricted) {
        this.isAgeRestricted = ageRestricted;
    }

    public boolean isDistanceRestricted() {
        return isDistanceRestricted;
    }

    public void setDistanceRestricted(boolean distanceRestricted) {
        this.isDistanceRestricted = distanceRestricted;
    }

    public boolean isStatusRestricted() {
        return isStatusRestricted;
    }

    public void setStatusRestricted(boolean statusRestricted) {
        this.isStatusRestricted = statusRestricted;
    }

    public String getFilterGender() {
        return filterGender;
    }

    public void setFilterGender(String filterGender) {
        this.filterGender = filterGender;
    }

    public String getFilterStatus() {
        return filterStatus;
    }

    public void setFilterStatus(String filterStatus) {
        this.filterStatus = filterStatus;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getSearchDistance() {
        return searchDistance;
    }

    public void setSearchDistance(int searchDistance) {
        this.searchDistance = searchDistance;
    }

    private void UpdateData(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("preferences");

        databaseReference.setValue(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                }
            }
        });
    }
}
