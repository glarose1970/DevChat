package com.commandcenter.devchat.Controller;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.commandcenter.devchat.Helper.FriendRequestHelper;
import com.commandcenter.devchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class User_Profile extends AppCompatActivity {

    private TextView tv_displayName, tv_Rank, tv_Status;
    private Button btn_sed_request;
    private de.hdodenhof.circleimageview.CircleImageView iv_profileImg;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mFriendsData;
    private DatabaseReference mFriendRequestData;
    private DatabaseReference mUsersData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile);

        final String userID = getIntent().getStringExtra("user_id");


        tv_displayName = (TextView) findViewById(R.id.user_profile_tv_displayName);
        tv_Rank        = (TextView) findViewById(R.id.user_profile_tv_Rank);
        tv_Status      = (TextView) findViewById(R.id.user_profile_tv_Status);
        iv_profileImg  = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.user_profile_iv_profileImg);
        btn_sed_request = (Button) findViewById(R.id.user_profile_btnSendRequest);

        if (mAuth != null) {
           // mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance();
            mUsersData = mDatabase.getReference().child("users");
            mFriendsData = mDatabase.getReference("users").child(mUser.getUid()).child("friends");
            mFriendRequestData = mDatabase.getReference("users").child(mUser.getUid()).child("requests");
            mUser = mAuth.getCurrentUser();
        }else {
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance();
            mUsersData = mDatabase.getReference("users");
            mFriendsData = mDatabase.getReference().child("users").child(mUser.getUid()).child("friends");
            mFriendRequestData = mDatabase.getReference().child("users").child(mUser.getUid()).child("requests");
        }

        mUsersData.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String displayName = dataSnapshot.child("username").getValue().toString();
                String rank = dataSnapshot.child("rank").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String profileUrl = dataSnapshot.child("main_img_url").getValue().toString();

                tv_displayName.setText(displayName);
                tv_Rank.setText(rank);
                tv_Status.setText(status);
                Picasso.with(User_Profile.this).load(profileUrl).placeholder(R.drawable.ic_person).into(iv_profileImg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //====== check request code ========
        mUsersData.child(userID).child("requests").child(mUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(("request_code"))) {
                            String requestCode = dataSnapshot.child("request_code").getValue().toString();
                            if (requestCode.equalsIgnoreCase("pending")) {
                                btn_sed_request.setEnabled(false);
                                btn_sed_request.setBackgroundColor(Color.RED);
                                btn_sed_request.setTextColor(Color.BLACK);
                                btn_sed_request.setText("Request Sent");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        //======== Check Friend Status ==========//
        mUsersData.child(userID).child("friends").child(mUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("friend_status")) {
                            String friendCode = dataSnapshot.child("friend_status").getValue().toString();
                            if (friendCode.equalsIgnoreCase("friends")) {
                                btn_sed_request.setEnabled(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        //*****************Button Click**********************
        btn_sed_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsersData.child(userID).child("requests").child(mUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(("request_code"))) {
                                    String requestCode = dataSnapshot.child("request_code").getValue().toString();
                                    if (requestCode.equalsIgnoreCase("pending")) {
                                        btn_sed_request.setEnabled(false);
                                        btn_sed_request.setBackgroundColor(Color.RED);
                                        btn_sed_request.setTextColor(Color.BLACK);
                                        btn_sed_request.setText("Request Sent");
                                    }
                                }else {
                                    FriendRequestHelper requestHelper = new FriendRequestHelper(User_Profile.this,mUser.getUid().toString(), userID);
                                    requestHelper.sendRequest(mUser.getUid().toString(), userID);
                                    btn_sed_request.setEnabled(false);
                                    btn_sed_request.setBackgroundColor(Color.RED);
                                    btn_sed_request.setTextColor(Color.BLACK);
                                    btn_sed_request.setText("Request Sent");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

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
                    Intent intent = new Intent(User_Profile.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navFriends:

                    return true;
                case R.id.navUsers:
                    intent = new Intent(User_Profile.this, UsersList.class);
                    startActivity(intent);
                    return true;
                case R.id.settings:
                    intent = new Intent(User_Profile.this, Main_Settings_Profile.class);
                    startActivity(intent);
                    return true;
                case R.id.navChatbox:
                    intent = new Intent(User_Profile.this, Chatbox_Activity.class);
                    startActivity(intent);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {

        if (mUser != null) {
            mUsersData.child(mAuth.getCurrentUser().getUid()).child("status").setValue("Offline");
            mAuth.signOut();
            Intent intent = new Intent(User_Profile.this, MainActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(User_Profile.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
