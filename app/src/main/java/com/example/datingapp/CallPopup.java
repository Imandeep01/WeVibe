package com.example.datingapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.datingapp.modal.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CallPopup extends Dialog{
    String callerName = "Caller";
    String callerId = "";
    ImageView acceptCall, rejectCall;
    DatabaseReference firebaseRef;
    public CallPopup(@NonNull Context context, String callerId) {
        super(context);
        this.callerId = callerId;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Set the content view to your popup layout
        setContentView(R.layout.call_popup);

        TextView caller = findViewById(R.id.incomingCallText);
        acceptCall = findViewById(R.id.acceptBtn);
        rejectCall = findViewById(R.id.rejectBtn);

        caller.setText(callerId);

        firebaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseRef.child(callerId).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callerName = Objects.requireNonNull(snapshot.getValue()).toString();
                caller.setText(callerName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getUid();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.TOP;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        // Set the window parameters
        getWindow().setAttributes(params);

        acceptCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent(getContext(), VideoCall.class);

                intent.putExtra("callerId", callerId);
                intent.putExtra("connId", callerId);

                firebaseRef.child(userId).child("call").child("incoming").setValue("null");

                getContext().startActivity(intent);
                dismiss();
            }
        });
        rejectCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseRef.child(userId).child("call").child("incoming").setValue("null");
                dismiss();
            }
        });
    }
}
