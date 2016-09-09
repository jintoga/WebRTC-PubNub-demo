package com.dat.android.experimentwebrtc.Activities;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.dat.android.experimentwebrtc.CaptureUtil;
import com.dat.android.experimentwebrtc.Constants;
import com.dat.android.experimentwebrtc.MyPnRTCListener;
import com.dat.android.experimentwebrtc.MyPnRTCListener.IConnectionListener;
import com.dat.android.experimentwebrtc.PubNubUtil;
import com.dat.android.experimentwebrtc.R;
import com.dat.android.experimentwebrtc.Validate;
import com.pubnub.api.Callback;
import java.util.ArrayList;
import me.kevingleason.pnwebrtc.PnRTCClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioTrack;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class MainActivity extends BasePubNubActivity implements IConnectionListener {

    @Bind(R.id.glSurfaceView)
    protected GLSurfaceView glSurfaceView;
    @Bind(R.id.username)
    protected EditText username;
    @Bind(R.id.callnumber)
    protected EditText callnumber;
    @Bind(R.id.connectionStatus)
    protected TextView connectionStatus;

    private PeerConnectionFactory peerConnectionFactory;
    private VideoSource videoSource;
    private VideoRenderer.Callbacks localRenderer;
    private VideoRenderer.Callbacks remoteRenderer;
    private MediaStream mediaStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true, null);
        peerConnectionFactory = new PeerConnectionFactory();

        videoSource = CaptureUtil.getVideoSourceForFrontFacingCamera(peerConnectionFactory);
        VideoTrack localVideoTrack = peerConnectionFactory.createVideoTrack("123", videoSource);

        AudioTrack localAudioTrack = CaptureUtil.getAudioTrack(peerConnectionFactory);

        VideoRendererGui.setView(glSurfaceView, null);

        try {
            remoteRenderer = VideoRendererGui.create(0, 0, 100, 100,
                VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
            localRenderer = VideoRendererGui.create(0, 0, 100, 100,
                VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);
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
        mPubNub =
            PubNubUtil.initPubNub(username.getText().toString(), callnumber.getText().toString(),
                pubNubSuccessCallback);
        pnRTCClient =
            new PnRTCClient(Constants.PUB_KEY, Constants.SUB_KEY, username.getText().toString());
    }

    @OnClick(R.id.call)
    protected void makeCall() {
        final String callNumStdBy = username.getText().toString() + "-stdby";
        JSONObject jsonCall = new JSONObject();
        connectionStatus.setVisibility(View.VISIBLE);
        /*Toast.makeText(MainActivity.this, "Calling " + callnumber.getText().toString(),
            Toast.LENGTH_SHORT).show();*/
        try {
            jsonCall.put(callnumber.getText().toString(), username.getText().toString());
            mPubNub.publish(callNumStdBy, jsonCall, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("successCallback", "SUCCESS: " + message.toString());
                    pubNubSuccessCallback.didInitiateCall();
                }

                @Override
                public void connectCallback(String channel, Object message) {
                    Log.d("connectCallback",
                        "connectCallback: channel " + channel + " " + message.toString());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private PubNubUtil.SuccessCallback pubNubSuccessCallback = new PubNubUtil.SuccessCallback() {
        @Override
        public void didInitiateCall() {
            pnRTCClient.attachRTCListener(
                new MyPnRTCListener(MainActivity.this, localRenderer, remoteRenderer,
                    MainActivity.this));
            pnRTCClient.attachLocalMediaStream(mediaStream);

            pnRTCClient.listenOn(username.getText().toString());
            pnRTCClient.setMaxConnections(1);
            pnRTCClient.connect(callnumber.getText().toString());
        }
    };

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

    @Override
    public void updateConnectionStatus(String status) {
        connectionStatus.setText(status);
    }

    @Override
    public void connected() {
        connectionStatus.setVisibility(View.INVISIBLE);
    }
}
