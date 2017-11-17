package com.commandcenter.devchat.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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
    private DatabaseReference mUsersData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile);

        tv_displayName = (TextView) findViewById(R.id.user_profile_tv_displayName);
        tv_Rank        = (TextView) findViewById(R.id.user_profile_tv_Rank);
        tv_Status      = (TextView) findViewById(R.id.user_profile_tv_Status);
        iv_profileImg  = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.user_profile_iv_profileImg);
        btn_sed_request = (Button) findViewById(R.id.user_profile_btnSendRequest);


        String userID = getIntent().getStringExtra("user_id");
        if (mAuth != null) {
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance();
            mUsersData = mDatabase.getReference().child("users").child(userID);
        }else {
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance();
            mUsersData = mDatabase.getReference("users").child(userID);
        }

        mUsersData.addListenerForSingleValueEvent(new ValueEventListener() {
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

    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}
