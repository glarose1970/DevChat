package com.commandcenter.devchat.Helper;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FriendRequestHelper {

    public enum FRIEND_REQUEST_STATE{
        FRIEND,
        REQUEST_SENT,
        REQUEST_RECIEVED,
        REQUEST_ACCEPTED,
        REQUEST_DENIED
    }

    Context context;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataRef;
    private DatabaseReference mUser_Friend_Request;
    private String friendUserID;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUsers;

    public FriendRequestHelper(Context context, String friendUserID, FirebaseUser mCurrentUser) {

        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        mDataRef = FirebaseDatabase.getInstance();
        this.friendUserID = friendUserID;
        this.mCurrentUser = mCurrentUser;
        mDataRef.getReference("friend_request");
        mDataRef.getReference("users").child(mCurrentUser.getUid());

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
