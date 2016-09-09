package com.dat.android.experimentwebrtc;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.dat.android.experimentwebrtc.Activities.ChatActivity;
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

    public static Pubnub initPubNub(final Context context, final String username,
        final String callnumber) {
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
                        if (!jsonMsg.has(username)) return;
                        String user = jsonMsg.getString(username);
                        // Consider Accept/Reject call here
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra(Constants.USER_NAME_KEY, username);
                        intent.putExtra(Constants.CALL_NUMBER_KEY, callnumber);
                        context.startActivity(intent);
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
