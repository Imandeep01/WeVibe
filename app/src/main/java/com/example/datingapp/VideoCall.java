package com.example.datingapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import io.agora.rtc2.Constants;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.ChannelMediaOptions;
import android.os.Bundle;

import com.example.datingapp.databinding.ActivityVideoCallBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
public class VideoCall extends AppCompatActivity {
    @NonNull
    ActivityVideoCallBinding binding;
    String connId = "";
    FirebaseAuth auth;
    String userId, receiverUserId;
    boolean isPeerConnected = false;
    DatabaseReference firebaseRef;
    private static final int PERMISSION_REQ_ID = 22;
    private final String appId = "ac56768211874d8f973996c8b534bc18";
    private int uid = 0;
    private boolean isJoined = false;
    boolean isMute = false;
    boolean cameraOff = false;

    private RtcEngine agoraEngine;
    private SurfaceView localSurfaceView;
    private SurfaceView remoteSurfaceView;
    private static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //TextView OtherUser = binding.ToolbarUserName;

        auth = FirebaseAuth.getInstance();
        userId = auth.getUid();

        receiverUserId = getIntent().getStringExtra("callerId");
        connId = getIntent().getStringExtra("connId");

        firebaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        firebaseRef.child(receiverUserId).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String otherUserName = snapshot.getValue().toString();
                binding.ToolbarUserName.setText(otherUserName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        setupVideoSDKEngine();
        if(!Objects.equals(connId, "")){
            binding.JoinButton.performClick();
        }else{
            connId = userId;
//            firebaseRef.child(receiverUserId).child("call").child("incoming").setValue("");
            firebaseRef.child(receiverUserId).child("call").child("incoming").setValue(connId);
            binding.JoinButton.performClick();
        }
    }
    private boolean checkSelfPermission()
    {
        if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) !=  PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[1]) !=  PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }
        return true;
    }

    void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void setupVideoSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
            agoraEngine.enableVideo();
        } catch (Exception e) {
            showMessage(e.toString());
        }
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        remoteSurfaceView = new SurfaceView(getBaseContext());
        remoteSurfaceView.setZOrderMediaOverlay(true);
        container.addView(remoteSurfaceView);
        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
        remoteSurfaceView.setVisibility(View.VISIBLE);

        container = findViewById(R.id.local_video_view_container);
        localSurfaceView = new SurfaceView(getBaseContext());
        container.addView(localSurfaceView);
        agoraEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }

    private void setupLocalVideo() {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        localSurfaceView = new SurfaceView(getBaseContext());
        container.addView(localSurfaceView);
        agoraEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
        showMessage("Ringing");
    }

    public void joinChannel(View view) {

        if (checkSelfPermission()) {
            ChannelMediaOptions options = new ChannelMediaOptions();

            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
            setupLocalVideo();
            localSurfaceView.setVisibility(View.VISIBLE);
            agoraEngine.startPreview();
            agoraEngine.joinChannel(null, connId, uid, options);
        } else {
            Toast.makeText(getApplicationContext(), "Permissions were not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(() -> setupRemoteVideo(uid));
        }
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;
        }
        @Override
        public void onUserOffline(int uid, int reason) {
            agoraEngine.leaveChannel();
            showMessage("Call has ended");
            isJoined = false;
            finish();
        }
    };
    protected void onDestroy() {
        super.onDestroy();
        agoraEngine.stopPreview();
        agoraEngine.leaveChannel();

        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;
        }).start();
    }

    public void endCall(View v){
        if (!isJoined) {
            showMessage("Join a channel first");
        } else {
            agoraEngine.leaveChannel();
            showMessage("Call has ended");
            isJoined = false;
            finish();
        }
    }
    public void muteAudio(View v){
        isMute = !isMute;
        agoraEngine.muteLocalAudioStream(isMute);
    }
    public void turnOffCamera(View v) {
        cameraOff = !cameraOff;
        agoraEngine.muteLocalVideoStream(cameraOff);
    }
}