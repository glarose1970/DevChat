package com.commandcenter.devchat.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.EditText;
import android.widget.Toast;

import com.commandcenter.devchat.Model.ChatboxMessage;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by jason on 11/19/2017.
 */

public class PhoneStateReceiver extends BroadcastReceiver {

    private DatabaseReference mNewMessageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsers;
    private FirebaseAuth mAuth;

    public String user;
    private String curDate = setDate();



    private String setDate() {
        mDatabase = FirebaseDatabase.getInstance();


        String thisDate = "";
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Florida"));
        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("America/Florida"));
        thisDate = formatter.format(cal.getTime());
        return thisDate;
    }



    @Override
    public void onReceive(Context context, Intent intent) {


        try {
            System.out.println("Receiver start");
            Toast.makeText(context," Receiver start ", Toast.LENGTH_SHORT).show();
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            mAuth = FirebaseAuth.getInstance();
            mNewMessageRef = mDatabase.getReference("messages").child(curDate);
            mUsers = mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid());

            mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.child("username").getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Toast.makeText(context,"Ringing State Number is -" + incomingNumber ,Toast.LENGTH_SHORT).show();
                ChatboxMessage message = new ChatboxMessage("Piggy Bot",user + "Someone is receiving a phone call and may not respond right away", "", curDate, "", "default_url");
                mNewMessageRef.push().setValue(message);
            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
                Toast.makeText(context,"Received State",Toast.LENGTH_SHORT).show();
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                Toast.makeText(context,"Idle State",Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


}
