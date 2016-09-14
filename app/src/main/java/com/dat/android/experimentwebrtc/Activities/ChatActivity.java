package com.dat.android.experimentwebrtc.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.dat.android.experimentwebrtc.ChatFragment;
import com.dat.android.experimentwebrtc.Constants;
import com.dat.android.experimentwebrtc.R;

public class ChatActivity extends AppCompatActivity {

    private String username, callnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
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
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, ChatFragment.newInstance(username, callnumber),
            "FRAG");
        fragmentTransaction.commit();
    }
}
