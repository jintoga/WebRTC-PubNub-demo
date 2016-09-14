package com.dat.android.experimentwebrtc;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.dat.android.experimentwebrtc.Activities.IncomingCallActivity;
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

    public static void subscribeStdBy(Pubnub pubnub, final Context context, final String username) {

        String stdbyChannel = username + Constants.STDBY_SUFFIX;
        try {
            pubnub.subscribe(stdbyChannel, new Callback() {
                @Override

                public void successCallback(String channel, Object message) {
                    Log.d("MA-success", "MESSAGE: " + message.toString());
                    if (!(message instanceof JSONObject)) return; // Ignore if not JSONObject
                    JSONObject jsonMsg = (JSONObject) message;
                    try {
                        if (!jsonMsg.has(username)) {
                            return;
                        }
                        String callingUser = jsonMsg.getString(username);
                        // Consider Accept/Reject call here
                        Intent intent = new Intent(context, IncomingCallActivity.class);
                        intent.putExtra(Constants.USER_NAME_KEY, username);
                        intent.putExtra(Constants.CALL_NUMBER_KEY, callingUser);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    public static Pubnub initPubNub(final Context context, final String username) {
        Pubnub pubnub = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        pubnub.setUUID(username);
        subscribeStdBy(pubnub, context, username);
        return pubnub;
    }
}
