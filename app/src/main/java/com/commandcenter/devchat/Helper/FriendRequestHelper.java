package com.commandcenter.devchat.Helper;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendRequestHelper extends Application {

    public enum FRIEND_REQUEST_STATE {
        FRIEND,
        REQUEST_SENT,
        REQUEST_RECIEVED,
        REQUEST_ACCEPTED,
        REQUEST_DENIED
    }

    Context context;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataRef;
    private DatabaseReference mUser_Requests;
    private DatabaseReference mFriend_User_Requests;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUsers;
    private String friendUserID;
    private String mCurUserID;
    private boolean isFriend;

    private Button btn_sendRequest;


    public FriendRequestHelper(Context context, String friendUserID, String curUserID) {

        this.context = context;
        this.friendUserID = friendUserID;
        this.mCurrentUser = mCurrentUser;
        mCurUserID = curUserID;

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth = FirebaseAuth.getInstance();
        mDataRef = FirebaseDatabase.getInstance();
        mUser_Requests = mDataRef.getReference().child("users").child(mCurUserID).child("requests");
        mFriend_User_Requests = mDataRef.getReference().child("users").child(friendUserID).child("requests");
        mUsers =  mDataRef.getReference("users");

    }

    public void sendRequest(final String senderID, final String receiverID) {

        mFriend_User_Requests.child(senderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("request_code")) {
                    String request_status = dataSnapshot.getValue().toString();
                    if (request_status.equalsIgnoreCase("sent")) {

                    }
                }else {
                    mUsers.child(receiverID).child("requests").child(senderID).child("request_code").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, "Friend Request Sent!", Toast.LENGTH_SHORT).show();

                                }
                            });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUser_Requests.child(receiverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("request_code")) {
                    String request_status = dataSnapshot.getValue().toString();
                    if (request_status.equalsIgnoreCase("pending")) {

                    }
                }else {
                    mUsers.child(senderID).child("requests").child(receiverID).child("request_status").setValue("pending");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getFriendUserID() {
        return friendUserID;
    }

    public void setFriendUserID(String friendUserID) {
        this.friendUserID = friendUserID;
    }

    public FirebaseUser getmCurrentUser() {
        return mCurrentUser;
    }

    public void setmCurrentUser(FirebaseUser mCurrentUser) {
        this.mCurrentUser = mCurrentUser;
    }
}
