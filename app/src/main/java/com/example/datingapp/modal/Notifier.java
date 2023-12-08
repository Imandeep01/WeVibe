package com.example.datingapp.modal;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.datingapp.MainActivity;
import com.example.datingapp.Questions_page;
import com.example.datingapp.SignUp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Notifier extends Application {
    DatabaseReference firebaseRef;
    FirebaseAuth auth;
    String userID;
    private ValueEventListener valueEventListener;
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getAppContext();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("imp", "Data Changed");
                launchMainActivity();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
    }

    public ValueEventListener getValueEventListener() {
        return valueEventListener;
    }

    public void setValueEventListener(ValueEventListener valueEventListener) {
        this.valueEventListener = valueEventListener;
    }

    public void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static Context getAppContext() {
        return mContext;
    }

    private void launchMainActivity() {
        Intent intent = new Intent(getAppContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("KEY", "VALUE");
        intent.putExtras(bundle);
        getAppContext().startActivity(intent);
    }
}

