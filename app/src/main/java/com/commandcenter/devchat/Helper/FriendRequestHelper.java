package com.commandcenter.devchat.Helper;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.commandcenter.devchat.Model.Request;
import com.commandcenter.devchat.Utils.Request_Code;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FriendRequestHelper extends Application {

    //==========FIELDS==========//
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataRef;
    private DatabaseReference mUser_Requests;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUsers;
    private String friendUserID;
    private String mCurUserID;
    private boolean isFriend;
    //==========END FIELDS==========//

    //==========RECEIVER FIELDS==========//
    String receiver_userName;
    String receiver_id;
    String receiver_img_url;
    //==========END RECEIVER FIELDS==========//

    //==========SENDER FIELDS==========//
    String sender_userName;
    String sender_id;
    String sender_img_url;
    //==========END SENDER FIELDS==========//

    //==========CONSTRUCTOR==========//
    public FriendRequestHelper(Context context, String friendUserID, String curUserID) {

        this.context = context;
        this.friendUserID = friendUserID;
        this.mCurrentUser = mCurrentUser;
        mCurUserID = curUserID;

        if (mAuth != null) {
            mAuth = FirebaseAuth.getInstance();
            mDataRef = FirebaseDatabase.getInstance();
            mUsers =  mDataRef.getReference("users");
        }else {
            mDataRef = FirebaseDatabase.getInstance();
            mUsers =  mDataRef.getReference("users");
        }
    }
    //==========END CONSTRUCTOR==========//

    @Override
    public void onCreate() {
        super.onCreate();

    }
    //==========SEND FRIEND REQUEST==========//
    public void sendRequest(final String senderID, final String receiverID) {

        //==========Get Sender Info==========//
        mUsers.child(senderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sender_userName = dataSnapshot.child("username").getValue().toString();
                sender_id = senderID;
                sender_img_url = dataSnapshot.child("main_img_url").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //==========Get Receiver Info==========//
        mUsers.child(receiverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                receiver_userName = dataSnapshot.child("username").getValue().toString();
                receiver_id = receiverID;
                receiver_img_url = dataSnapshot.child("main_img_url").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //==========Set the friend node==========//
            mUsers.child(receiverID).child("requests").child(senderID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("requestCode")) {
                        String request_status = dataSnapshot.getValue().toString();
                        if (request_status.equalsIgnoreCase(Request_Code.PENDING.toString())) {

                        }
                    }else {

                        Request receiver_request = new Request(senderID, sender_userName, sender_img_url, Request_Code.PENDING.toString(), setRequestDate());
                        mUsers.child(receiverID).child("requests").child(senderID).setValue(receiver_request)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "Request Received!", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


       //==========set the current user node==========//
            mUsers.child(senderID).child("requests").child(receiverID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("requestCode")) {
                        String request_status = dataSnapshot.getValue().toString();
                        if (request_status.equalsIgnoreCase(Request_Code.SENT.toString())) {

                        }
                    }else {
                        Request sender_request = new Request(receiverID, receiver_userName, receiver_img_url, Request_Code.SENT.toString(), setRequestDate());
                        mUsers.child(senderID).child("requests").child(receiverID).setValue(sender_request)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "Request Sent!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }
    //==========END SEND FRIEND REQUEST==========//

    //==========ACCEPT FRIEND REQUEST==========//
    public void acceptRequest(final String curUserID, final String friendID) {

        //==========UPDATE CURRENT USER REQUEST==========//
        mUsers.child(curUserID).child("requests").child(friendID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("request_code")) {
                    mUsers.child(curUserID).child("requests").child(friendID).child("request_code").setValue(Request_Code.ACCEPTED);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //==========END UPDATE REQUEST==========//

        //==========UPDATE FRIEND REQUEST==========//
        mUsers.child(friendID).child("requests").child(curUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("request_code")) {
                    mUsers.child(friendID).child("requests").child(curUserID).child("request_code").setValue(Request_Code.ACCEPTED);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //==========END UPDATE FRIEND REQUEST==========//

    }
    //==========END ACCEPT FRIEND REQUEST==========//

    private String setRequestDate() {

        String  nowDate;
        DateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date request_date = new Date();
        nowDate = dFormat.format(request_date);

        return nowDate;
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
