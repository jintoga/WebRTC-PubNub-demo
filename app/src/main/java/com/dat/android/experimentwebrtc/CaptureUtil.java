package com.dat.android.experimentwebrtc;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;

public class CaptureUtil {

    public static VideoSource getVideoSourceForFrontFacingCamera(
        PeerConnectionFactory peerConnectionFactory) {
        VideoCapturer videoCapturer =
            VideoCapturerAndroid.create(VideoCapturerAndroid.getNameOfFrontFacingDevice());
        return peerConnectionFactory.createVideoSource(videoCapturer, new MediaConstraints());
    }

    public static AudioTrack getAudioTrack(PeerConnectionFactory peerConnectionFactory) {
        AudioSource source = peerConnectionFactory.createAudioSource(new MediaConstraints());
        return peerConnectionFactory.createAudioTrack("67890", source);
    }
}
