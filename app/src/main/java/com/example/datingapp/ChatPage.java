package com.example.datingapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.telecom.InCallService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datingapp.Adapter.MessagesAdapter;
import com.example.datingapp.modal.Chat;
import com.example.datingapp.modal.Notifier;
import com.example.datingapp.modal.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class ChatPage extends Fragment {
    private ValueEventListener listener;

    EditText chatInputEditText;
    LinearLayout chatMessagesContainer;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    TextView userName, timer;
    ImageButton btn_send, btn_videoCall;
    MessagesAdapter messagesAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView;
    String[] permissions = new String[]{Manifest.permission.INTERNET, android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_NETWORK_STATE };
    String otherUserName = "", userId;
    private CountDownTimer countDownTimer;
    int requestCode = 1;
    private ValueEventListener usersResponseListener;
    boolean areFriends;

    public ChatPage() {
        // Required empty public constructor
    }

    private boolean isPermissionGranted(){
        for(String permission : permissions){
            if(ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    void askPermissions(){
        ActivityCompat.requestPermissions(getActivity(), permissions, requestCode);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        Bundle bundle = getArguments();
        String receiverId = bundle.getString("userId");
        boolean restricted = bundle.getBoolean("restricted");

        Notifier notifier = (Notifier)getActivity().getApplicationContext();
        listener = notifier.getValueEventListener();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(FirebaseAuth.getInstance().getUid()).child("call").child("incoming")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue() != null && !snapshot.getValue().equals("null")) {
                            Log.i("info", "Waiting for incoming ");
                            CallPopup popup = new CallPopup(getContext(), (String) snapshot.getValue());
                            popup.show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        userName = rootView.findViewById(R.id.Toolbar_userName);
        chatInputEditText = rootView.findViewById(R.id.message_inputBox);
        timer = rootView.findViewById(R.id.Toolbar_timer);

        btn_send = rootView.findViewById(R.id.send_button);
        btn_videoCall = rootView.findViewById(R.id.videoCallButton);

        recyclerView = rootView.findViewById(R.id.recycleV_messages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        RelativeLayout layout_messageinput = rootView.findViewById(R.id.layout_messageinput);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("friends").child(userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> friendIds = new ArrayList<>();
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    String friendId = friendSnapshot.getKey();
                    friendIds.add(friendId);
                }
                // Check if the second user is in the list of friends
                if (friendIds.contains(receiverId)) {
                    areFriends = true;
                    ImageButton videoCallBtn = rootView.findViewById(R.id.videoCallButton);
                    videoCallBtn.setVisibility(View.VISIBLE);
                    Log.e("info", "Are friends");
                    // The two users are friends
                } else {
                    areFriends = false;
                    if(!restricted){
                        layout_messageinput.setVisibility(View.GONE);
                    }
                    Log.e("info", "Are not friends");
                    // The two users are not friends
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
        if(restricted){
            timer.setVisibility(View.VISIBLE);
            countDownTimer = new CountDownTimer(240000, 1000) {
                @Override
                public void onTick(long millisLeft) {
                    long secondsLeft = millisLeft / 1000;
                    timer.setText(String.format("Timer: %d", secondsLeft));
                }
                @Override
                public void onFinish() {
                    layout_messageinput.setVisibility(View.GONE);
                    DatabaseReference databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users");

                    databaseReferenceUsers.child(userId).child("buttonPressed").setValue("");

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Time Over");
                    builder.setMessage("Do you wish to continue with this user?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final AlertDialog[] dialog3 = new AlertDialog[1];
                            final AlertDialog[] dialog1 = new AlertDialog[1];
                            final AlertDialog[] dialog2 = new AlertDialog[1];

                            AlertDialog.Builder builderMatch = new AlertDialog.Builder(getContext());
                            builderMatch.setTitle("Its a Match");
                            builderMatch.setMessage("The other user wants to match");
                            dialog3[0] = builderMatch.create();

                            AlertDialog.Builder builderNoMatch = new AlertDialog.Builder(getContext());
                            builderNoMatch.setTitle("Its not a Match");
                            builderNoMatch.setMessage("The other user does not want a match");
                            dialog1[0] = builderNoMatch.create();

                            AlertDialog.Builder builderWait = new AlertDialog.Builder(getContext());
                            builderWait.setTitle("Wait");
                            builderWait.setMessage("Waiting for other users response");
                            dialog2[0] = builderWait.create();

                            databaseReferenceUsers.child(userId).child("buttonPressed").setValue("Yes");
                            usersResponseListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.getValue() != null && snapshot.getValue().toString().equals("Yes")){
                                        dialog2[0].hide();
                                        dialog3[0].show();
                                        layout_messageinput.setVisibility(View.VISIBLE);
                                        timer.setVisibility(View.GONE);
                                        //databaseReferenceUsers.child(userId).child("Matches").push().setValue(receiverId);
                                        databaseReferenceUsers.child(receiverId).child("buttonPressed").removeEventListener(usersResponseListener);

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = database.getReference("friends").child(userId);
                                        myRef.child(receiverId).setValue(true);
                                    } else if (snapshot.getValue() != null && snapshot.getValue().toString().equals("No")) {
                                        dialog1[0].show();
                                        dialog2[0].hide();
                                        databaseReferenceUsers.child(receiverId).child("buttonPressed").removeEventListener(usersResponseListener);
                                    }else{
                                        dialog2[0].show();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };
                            databaseReferenceUsers.child(receiverId).child("buttonPressed").addValueEventListener(usersResponseListener);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReferenceUsers.child(userId).child("buttonPressed").setValue("No");
                            // Close the dialog
                            AlertDialog.Builder builderMatch = new AlertDialog.Builder(getContext());
                            builderMatch.setTitle("Its not a Match");
                            builderMatch.setMessage("You do not want to match");
                            AlertDialog dialogNoMatch = builderMatch.create();
                            dialog.dismiss();
                            dialogNoMatch.show();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }.start();
        }else{
            timer.setVisibility(View.GONE);
        }

        btn_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String message = chatInputEditText.getText().toString();
                if(!message.equals("")){
                    sendMessage(firebaseUser.getUid(), receiverId, message);
                }
                chatInputEditText.setText("");
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(receiverId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                 otherUserName = user.getName();
                userName.setText(otherUserName);

                readMessages(firebaseUser.getUid(), user.getId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btn_videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast. makeText(getActivity(), "Video Call Button Pressed!", Toast. LENGTH_SHORT);

                Intent intent = new Intent(getActivity(), VideoCall.class);
                intent.putExtra("callerId", receiverId);
                //intent.putExtra("otherUserName", otherUserName);
                intent.putExtra("connId", "");
//
                if(isPermissionGranted()){
                    startActivity(intent);
                }else{
                    askPermissions();
                }
            }
        });
        return rootView;
    }
//    public void ItsAMatch(String userId){
//        ChatPage chatPage = new ChatPage();
//        Bundle bundle = new Bundle();
//        bundle.putString("userId", userId);
//        bundle.putBoolean("restricted", false);
//        chatPage.setArguments(bundle);
//
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout, chatPage);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
//    }
//
//    public void ItsNotAMatch(){
//        Home home = new Home();
//
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout, home);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
//    }
    private void sendMessage(String sender, String receiver, String message){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats");

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        databaseReference.push().setValue(hashMap);
    }
    private void readMessages(String myId, String userId){
        mchat = new ArrayList<Chat>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(myId) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(myId)){
                        mchat.add(chat);
                    }
                    messagesAdapter = new MessagesAdapter(getActivity(), mchat);
                    recyclerView.setAdapter(messagesAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}
