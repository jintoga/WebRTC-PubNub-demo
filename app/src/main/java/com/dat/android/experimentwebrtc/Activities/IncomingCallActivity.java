package com.dat.android.experimentwebrtc.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.dat.android.experimentwebrtc.Constants;
import com.dat.android.experimentwebrtc.R;

public class IncomingCallActivity extends AppCompatActivity {

    @Bind(R.id.caller_id)
    protected TextView callerId;

    private String username, callnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
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

        callerId.setText(callnumber);
    }

    @OnClick(R.id.acceptCall)
    protected void acceptCall() {
        Intent intent = new Intent(IncomingCallActivity.this, ChatActivity.class);
        intent.putExtra(Constants.USER_NAME_KEY, username);
        intent.putExtra(Constants.CALL_NUMBER_KEY, callnumber);
        startActivity(intent);
    }
}
