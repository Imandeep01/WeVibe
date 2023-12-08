package com.example.datingapp.modal;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserPersonalInfo {
    private String city, status, race, children, education, profession, lifeSkill, impThings, spareTime, friendsSaying;
    private boolean familyPlan, smoke, drink;

    public UserPersonalInfo() {
    }

    public UserPersonalInfo(String city, String status, String race, String children, String education, String profession,
                            boolean familyPlan, boolean smoke, boolean drink, String lifeSkill, String impThings, String spareTime, String friendsSaying) {
        this.city = city;
        this.status = status;
        this.race = race;
        this.children = children;
        this.education = education;
        this.profession = profession;
        this.familyPlan = familyPlan;
        this.smoke = smoke;
        this.drink = drink;
        this.lifeSkill = lifeSkill;
        this.impThings = impThings;
        this.spareTime = spareTime;
        this.friendsSaying = friendsSaying;

        this.updateData();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public boolean isFamilyPlan() {
        return familyPlan;
    }

    public void setFamilyPlan(boolean familyPlan) {
        this.familyPlan = familyPlan;
    }

    public boolean isSmoke() {
        return smoke;
    }

    public void setSmoke(boolean smoke) {
        this.smoke = smoke;
    }

    public boolean isDrink() {
        return drink;
    }

    public void setDrink(boolean drink) {
        this.drink = drink;
    }

    public String getLifeSkill() {
        return lifeSkill;
    }

    public void setLifeSkill(String lifeSkill) {
        this.lifeSkill = lifeSkill;
    }

    public String getSpareTime() {
        return spareTime;
    }

    public void setSpareTime(String spareTime) {
        this.spareTime = spareTime;
    }

    public String getImpThings() {
        return impThings;
    }

    public void setImpThings(String impThings) {
        this.impThings = impThings;
    }

    public String getFriendsSaying() {
        return friendsSaying;
    }

    public void setFriendsSaying(String friendsSaying) {
        this.friendsSaying = friendsSaying;
    }

    private void updateData() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        if (userId != null) {
            databaseReference.child(userId).child("personalInfo").setValue(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Data saved successfully
                            }
                        }
                    });
        }
    }
}
