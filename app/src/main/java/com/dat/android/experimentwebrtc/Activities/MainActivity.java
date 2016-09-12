package com.dat.android.experimentwebrtc.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.dat.android.experimentwebrtc.Constants;
import com.dat.android.experimentwebrtc.MyPnRTCListener;
import com.dat.android.experimentwebrtc.PubNubUtil;
import com.dat.android.experimentwebrtc.R;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements MyPnRTCListener.IConnectionListener {

    @Bind(R.id.username)
    protected EditText username;
    @Bind(R.id.callnumber)
    protected EditText callnumber;
    @Bind(R.id.connectionStatus)
    protected TextView connectionStatus;

    private Pubnub mPubNub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login)
    protected void login() {
        mPubNub = PubNubUtil.initPubNub(this, username.getText().toString());
    }

    @OnClick(R.id.call)
    protected void makeCall() {
        final String callNumStdBy = callnumber.getText().toString() + "-stdby";
        JSONObject jsonCall = new JSONObject();
        connectionStatus.setVisibility(View.VISIBLE);
        try {
            jsonCall.put(callnumber.getText().toString(), username.getText().toString());
            mPubNub.publish(callNumStdBy, jsonCall, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("successCallback", "SUCCESS: " + message.toString());
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    intent.putExtra(Constants.USER_NAME_KEY, username.getText().toString());
                    intent.putExtra(Constants.CALL_NUMBER_KEY, callnumber.getText().toString());
                    startActivity(intent);
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

    @Override
    public void updateConnectionStatus(String status) {
        connectionStatus.setText(status);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (this.mPubNub == null) {
            mPubNub = PubNubUtil.initPubNub(this, username.getText().toString());
        } else {
            PubNubUtil.subscribeStdBy(mPubNub, this, username.getText().toString());
        }
    }

    @Override
    public void connected() {
        connectionStatus.setVisibility(View.INVISIBLE);
    }
}
