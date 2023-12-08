package com.example.datingapp.modal;

import java.util.Calendar;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class User {
    private String id,name,phone,email,password,birthday,gender, imageURL;
    private boolean isSearching;
    public User(String id, String email, String name, String phone, String birthday, String gender) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.birthday = birthday;
        this.gender = gender;
        this.imageURL = "default";
        this.isSearching = false;
    }
    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phoneNumber) {
        this.phone = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getGender() {return gender;}
    public void setGender(String gender) {this.gender = gender;}
    public int getAge(){
        return 0;
    }
    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public boolean getIsSearching() {
        return isSearching;
    }
}