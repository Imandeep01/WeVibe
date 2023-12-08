package com.example.datingapp;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.datingapp.databinding.FragmentSearchingBinding;
import com.example.datingapp.modal.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Searching extends Fragment {
    DatabaseReference databaseReference;
    String userId;
    private FragmentSearchingBinding binding;
    private ValueEventListener searchValueEventListener;

    public Searching()
    {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentSearchingBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();

        databaseReference.child(userId).child("isSearching").setValue(true);
        searchValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isBothSearching = false;
                String otherUserId = "test";
               for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    String id = user.getId();
                    if(id == null)
                        continue;
                    else {
                        if (!user.getId().equals(userId) && user.getIsSearching()) {
                        isBothSearching = true;
                        otherUserId = user.getId();
                            Log.e("info", "Connected!");
                        if (searchValueEventListener != null) {
                            databaseReference.removeEventListener(searchValueEventListener);
                        }
                        break;
                    }
                    }

                }
                if (isBothSearching && (otherUserId != null)) {
                    Log.e("info", userId);
                    Log.e("info", otherUserId);
                    // Both users are searching, start chat
                    databaseReference.child(userId).child("isSearching").setValue(false);
                    databaseReference.child(otherUserId).child("isSearching").setValue(false);
                    openChatActivity(otherUserId);
                } else {
                    Toast.makeText(getActivity(), "Please wait for a partner to be available.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Home", "Failed to read user data.", error.toException());
            }
        };
        databaseReference.addValueEventListener(searchValueEventListener);
        return rootView;
    }
public void openChatActivity(String userId) {
        ChatPage chatPage = new ChatPage();
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        bundle.putBoolean("restricted", true);
        chatPage.setArguments(bundle);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, chatPage);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        }
    public void onDestroyView() {
        super.onDestroyView();
        if (searchValueEventListener != null) {
            databaseReference.child(userId).child("isSearching").setValue(false);
            databaseReference.removeEventListener(searchValueEventListener);
        }
        databaseReference.child(userId).child("isSearching").setValue(false);
        Log.e("info", "Exited searching");
    }
}