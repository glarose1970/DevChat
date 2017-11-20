package com.commandcenter.devchat.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.commandcenter.devchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Activity_PrivateMessageBox extends AppCompatActivity {

    //==========FIREBASE==========//
    private FirebaseAuth mAuth;
    private FirebaseDatabase mData;
    private DatabaseReference mMessageRef;
    private DatabaseReference mUserData;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__private_message_box);

        //==========INITIALIZE FIREBASE==========//
        init();
    }

    //==========MENU==========//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (mAuth.getCurrentUser() != null) {

            switch (item.getItemId()) {
                case R.id.logout:
                    Toast.makeText(this, "User signed out!", Toast.LENGTH_SHORT).show();
                    signOut();
                    Intent intent = new Intent(Activity_PrivateMessageBox.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navFriends:

                    return true;
                case R.id.navUsers:
                    intent = new Intent(Activity_PrivateMessageBox.this, UsersList.class);
                    startActivity(intent);
                    return true;
                case R.id.settings:
                    intent = new Intent(Activity_PrivateMessageBox.this, Main_Settings_Profile.class);
                    startActivity(intent);
                    return true;
                case R.id.navChatbox:
                    intent = new Intent(Activity_PrivateMessageBox.this, Chatbox_Activity.class);
                    startActivity(intent);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        return super.onOptionsItemSelected(item);
    }
    //==========END MENU==========//

    //==========SETUP FIREBASE==========//
    private void init() {

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mUserData = mData.getReference().child("users");
            mMessageRef = mData.getReference().child("users").child(mUser.getUid()).child("private_messages");

        }else {
            mUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mUserData = mData.getReference().child("users");
            mMessageRef = mData.getReference().child("users").child(mUser.getUid()).child("private_messages");
        }
    }
    //==========END==========//
    private void signOut() {

        if (mUser != null) {
            mUserData.child(mAuth.getCurrentUser().getUid()).child("status").setValue("Offline");
            mAuth.signOut();
            Intent intent = new Intent(Activity_PrivateMessageBox.this, MainActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(Activity_PrivateMessageBox.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
