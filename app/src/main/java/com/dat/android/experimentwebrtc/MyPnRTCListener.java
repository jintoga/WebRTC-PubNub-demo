package com.dat.android.experimentwebrtc;

import android.app.Activity;
import android.widget.Toast;
import me.kevingleason.pnwebrtc.PnPeer;
import me.kevingleason.pnwebrtc.PnRTCListener;
import org.webrtc.MediaStream;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

/**
 * Created by Nguyen on 9/8/2016.
 */
public class MyPnRTCListener extends PnRTCListener {

    private Activity parentActivity;
    private VideoRenderer.Callbacks localRenderer;
    private VideoRenderer.Callbacks remoteRenderer;
    IConnectionListener listener;

    public interface IConnectionListener {
        void updateConnectionStatus(String status);

        void connected();
    }

    public MyPnRTCListener(Activity activity, VideoRenderer.Callbacks local,
        VideoRenderer.Callbacks remote, IConnectionListener listener) {
        parentActivity = activity;
        localRenderer = local;
        remoteRenderer = remote;
        this.listener = listener;
    }

    @Override
    public void onPeerStatusChanged(final PnPeer peer) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listener.updateConnectionStatus(peer.getStatus());
            }
        });
    }

    @Override
    public void onConnected(final String userId) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(parentActivity, "Connected to " + userId, Toast.LENGTH_SHORT).show();
                listener.connected();
            }
        });
    }

    @Override
    public void onAddRemoteStream(final MediaStream remoteStream, final PnPeer peer) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (remoteStream.videoTracks.size() > 0) {
                        remoteStream.videoTracks.get(0)
                            .addRenderer(new VideoRenderer(remoteRenderer));
                        VideoRendererGui.update(remoteRenderer, 0, 0, 100, 100,
                            VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
                        VideoRendererGui.update(localRenderer, 72, 72, 25, 25,
                            VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onLocalStream(final MediaStream localStream) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (localStream.videoTracks.size() == 0) return;
                localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRenderer));
            }
        });
    }
}
