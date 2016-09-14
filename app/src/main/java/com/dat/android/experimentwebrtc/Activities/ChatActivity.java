package com.dat.android.experimentwebrtc.Activities;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.dat.android.experimentwebrtc.CaptureUtil;
import com.dat.android.experimentwebrtc.Constants;
import com.dat.android.experimentwebrtc.MyPnRTCListener;
import com.dat.android.experimentwebrtc.R;
import com.dat.android.experimentwebrtc.Validate;
import java.util.ArrayList;
import me.kevingleason.pnwebrtc.PnRTCClient;
import org.webrtc.AudioTrack;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class ChatActivity extends BasePubNubActivity {

    @Bind(R.id.glSurfaceView)
    protected GLSurfaceView glSurfaceView;

    private PeerConnectionFactory peerConnectionFactory;
    private VideoSource videoSource;
    private VideoRenderer.Callbacks localRenderer;
    private VideoRenderer.Callbacks remoteRenderer;
    private MediaStream mediaStream;

    private String username, callnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey("USERNAME")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this,
                "Need to pass username to VideoChatActivity in intent extras (Constants.USER_NAME).",
                Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        username = extras.getString(Constants.USER_NAME_KEY, "");
        callnumber = extras.getString(Constants.CALL_NUMBER_KEY, "");
        if (!Validate.hasPermissions(this)) {
            Validate.requestPermissions(this);
        } else {
            setupWebRTC();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        if (requestCode == Validate.PERMISSION_REQUEST) {
            setupWebRTC();
        }
    }

    public void setupWebRTC() {
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
        peerConnectionFactory = new PeerConnectionFactory();

        videoSource = CaptureUtil.getVideoSourceForFrontFacingCamera(peerConnectionFactory);
        VideoTrack localVideoTrack = peerConnectionFactory.createVideoTrack("123", videoSource);

        AudioTrack localAudioTrack = CaptureUtil.getAudioTrack(peerConnectionFactory);

        VideoRendererGui.setView(glSurfaceView, null);

        try {
            remoteRenderer = VideoRendererGui.create(0, 0, 100, 100,
                RendererCommon.ScalingType.SCALE_ASPECT_FILL, false);
            localRenderer = VideoRendererGui.create(0, 0, 100, 100,
                RendererCommon.ScalingType.SCALE_ASPECT_FILL, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaStream = peerConnectionFactory.createLocalMediaStream("qwerty");

        mediaStream.addTrack(localVideoTrack);
        mediaStream.addTrack(localAudioTrack);

        setupPeerConnection();
    }

    private void setupPeerConnection() {
        iceServers = new ArrayList<>();
        pnRTCClient = new PnRTCClient(Constants.PUB_KEY, Constants.SUB_KEY, username);

        pnRTCClient.attachRTCListener(
            new MyPnRTCListener(ChatActivity.this, localRenderer, remoteRenderer));
        pnRTCClient.attachLocalMediaStream(mediaStream);

        pnRTCClient.listenOn(username);
        pnRTCClient.setMaxConnections(1);
        pnRTCClient.connect(callnumber);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.glSurfaceView.onPause();
        this.videoSource.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.glSurfaceView.onResume();
        this.videoSource.restart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.videoSource != null) {
            this.videoSource.stop();
        }
        if (this.pnRTCClient != null) {
            this.pnRTCClient.onDestroy();
        }
    }
}
