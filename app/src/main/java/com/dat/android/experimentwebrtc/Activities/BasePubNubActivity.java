package com.dat.android.experimentwebrtc.Activities;

import android.support.v7.app.AppCompatActivity;
import com.pubnub.api.Pubnub;
import java.util.ArrayList;
import me.kevingleason.pnwebrtc.PnRTCClient;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;

/**
 * Created by Nguyen on 9/8/2016.
 */
public class BasePubNubActivity extends AppCompatActivity
    implements PeerConnection.Observer {

    protected ArrayList<PeerConnection.IceServer> iceServers;
    protected Pubnub mPubNub;
    protected PnRTCClient pnRTCClient;

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {

    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {

    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {

    }

    @Override
    public void onAddStream(MediaStream mediaStream) {

    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {

    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {

    }

    @Override
    public void onRenegotiationNeeded() {

    }
}
