package com.dat.android.experimentwebrtc;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.dat.android.experimentwebrtc.Activities.BasePubNubFragment;
import com.dat.android.experimentwebrtc.Activities.MainActivity;
import me.kevingleason.pnwebrtc.PnRTCClient;
import org.webrtc.AudioTrack;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

/**
 * Created by Nguyen on 9/14/2016.
 */
public class ChatFragment extends BasePubNubFragment
    implements MyPnRTCListener.IConnectionListener {

    @Bind(R.id.glSurfaceView)
    protected GLSurfaceView glSurfaceView;

    private PeerConnectionFactory peerConnectionFactory;
    private VideoSource videoSource;
    private VideoRenderer.Callbacks localRenderer;
    private VideoRenderer.Callbacks remoteRenderer;
    private MediaStream mediaStream;

    private String username, callnumber;
    View view;

    public static ChatFragment newInstance(String user, String callnumber) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(Constants.USER_NAME_KEY, user);
        args.putString(Constants.CALL_NUMBER_KEY, callnumber);
        chatFragment.setArguments(args);
        return chatFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras == null || !extras.containsKey("USERNAME")) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            Toast.makeText(getActivity(),
                "Need to pass username to VideoChatActivity in intent extras (Constants.USER_NAME).",
                Toast.LENGTH_SHORT).show();
            return;
        }
        username = extras.getString(Constants.USER_NAME_KEY, "");
        callnumber = extras.getString(Constants.CALL_NUMBER_KEY, "");
        if (!Validate.hasPermissions(getContext())) {
            Validate.requestPermissions(getActivity());
        } else {
            setupWebRTC();
        }
    }

    public void setupWebRTC() {
        PeerConnectionFactory.initializeAndroidGlobals(getActivity(), true, true, true);
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

    public void setupPeerConnection() {
        pnRTCClient = new PnRTCClient(Constants.PUB_KEY, Constants.SUB_KEY, username);

        pnRTCClient.attachRTCListener(
            new MyPnRTCListener(getActivity(), localRenderer, remoteRenderer, this));
        pnRTCClient.attachLocalMediaStream(mediaStream);

        pnRTCClient.setMaxConnections(1);

        pnRTCClient.listenOn(username);
        pnRTCClient.connect(callnumber);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.glSurfaceView != null) {
            this.glSurfaceView.onPause();
        }
        if (this.videoSource != null) {
            this.videoSource.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.glSurfaceView != null) {
            this.glSurfaceView.onResume();
        }
        if (this.videoSource != null) {
            this.videoSource.restart();
        }
    }

    @Override
    public void onDestroy() {
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

    }

    @Override
    public void connected() {
    }
}
