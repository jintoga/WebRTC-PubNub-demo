package com.dat.android.experimentwebrtc;

import android.util.Log;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nguyen on 9/9/2016.
 */
public class PubNubUtil {

    public interface SuccessCallback {
        void didInitiateCall();
    }

    public static Pubnub initPubNub(String username, final String callnumber,
        final SuccessCallback callback) {
        String stdbyChannel = username + Constants.STDBY_SUFFIX;
        Pubnub pubnub = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        pubnub.setUUID(username);
        try {
            pubnub.subscribe(stdbyChannel, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("MA-success", "MESSAGE: " + message.toString());
                    if (!(message instanceof JSONObject)) return; // Ignore if not JSONObject
                    JSONObject jsonMsg = (JSONObject) message;
                    try {
                        if (!jsonMsg.has(callnumber)) return;
                        String user = jsonMsg.getString(callnumber);
                        // Consider Accept/Reject call here
                        callback.didInitiateCall();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }
        return pubnub;
    }
}
